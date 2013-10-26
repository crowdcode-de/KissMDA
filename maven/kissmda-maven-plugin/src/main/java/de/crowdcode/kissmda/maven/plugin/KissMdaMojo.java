/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package de.crowdcode.kissmda.maven.plugin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.crowdcode.kissmda.core.CoreModule;
import de.crowdcode.kissmda.core.StandardContext;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;

/**
 * KissMDA Mojo.
 * 
 * @goal generate
 * @phase generate-sources
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class KissMdaMojo extends AbstractMojo {

	private final Log logger = getLog();

	private static final String TRANSFORMER_SUFFIX = "Transformer";

	private static final String MODULE_SUFFIX = "Module";

	public static final String ERROR_GUICE_SAME_PACKAGE_NOT_FOUND = "Error Guice module for the transformer in the same package not found!";

	/**
	 * The enclosing project.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * Package name to scan as collections.
	 * 
	 * @parameter
	 */
	private List<String> transformerScanPackageNames;

	/**
	 * Transformer name to scan each with order, so the transformers will be
	 * executed in the order configured.
	 * 
	 * @parameter
	 */
	private List<String> transformerNameWithOrders;

	/**
	 * Model file.
	 * 
	 * @parameter
	 * @required
	 */
	private String modelFile;

	/**
	 * Target directory for generated sources.
	 * 
	 * @parameter default-value="target/generated-sources/java"
	 * @required
	 */
	private String generatedSourcesTargetDirectory;

	/**
	 * Logging level.
	 * 
	 * @parameter default-value="INFO"
	 */
	private String loggingLevel;

	private final StandardContext context;

	private final LoggingLevelMapper loggingLevelMapper;

	public KissMdaMojo() {
		super();
		loggingLevelMapper = new LoggingLevelMapper();
		context = new StandardContext();
	}

	public StandardContext getContext() {
		return context;
	}

	public void setGeneratedSourcesTargetDirectory(
			String generatedSourcesTargetDirectory) {
		this.generatedSourcesTargetDirectory = generatedSourcesTargetDirectory;
	}

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public void setTransformerScanPackageNames(
			List<String> transformerScanPackageNames) {
		this.transformerScanPackageNames = transformerScanPackageNames;
	}

	public void setTransformerNameWithOrders(
			List<String> transformerNameWithOrders) {
		this.transformerNameWithOrders = transformerNameWithOrders;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setLoggingLevel(String loggingLevel) {
		this.loggingLevel = loggingLevel;
	}

	/**
	 * Execute.
	 * 
	 * @throws MojoExecutionException
	 */
	@Override
	public void execute() throws MojoExecutionException {
		// We need to execute the transformer, check what transformer should we
		// start...
		// Search for Interface Transformer in the classpath, create the class
		// and execute. Do until we have all the Transformers...
		logger.info("Start KissMdaMojo...");
		setLoggingLevel();

		try {
			// Create parent Guice module injector from kissmda core
			Injector parentInjector = Guice.createInjector(new CoreModule());
			// Go through other module injectors and create child module
			// injectors
			String fullNameModelFile = project.getBasedir() + "/" + modelFile;
			String fullNameTargetDirectory = project.getBasedir() + "/"
					+ generatedSourcesTargetDirectory;
			context.setSourceModel(fullNameModelFile);
			context.setTargetModel(fullNameTargetDirectory);

			if (transformerNameWithOrders != null
					&& transformerNameWithOrders.size() != 0) {
				// transformerNameWithOrders wins if both are configured
				useTransformerNamesWithOrder(parentInjector);
			} else {
				useTransformerScanPackageNames(parentInjector);
			}

			logger.info("Stop KissMdaMojo without error...");
		} catch (TransformerException e) {
			throw new MojoExecutionException("Error transform the model: "
					+ e.getLocalizedMessage(), e);
		} catch (InstantiationException e) {
			throw new MojoExecutionException("Error transform the model: "
					+ e.getLocalizedMessage(), e);
		} catch (IllegalAccessException e) {
			throw new MojoExecutionException("Error transform the model: "
					+ e.getLocalizedMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("Error transform the model: "
					+ e.getLocalizedMessage(), e);
		}
	}

	private void useTransformerScanPackageNames(Injector parentInjector)
			throws MojoExecutionException, InstantiationException,
			IllegalAccessException {
		for (String packageName : transformerScanPackageNames) {
			Reflections reflections = new Reflections(packageName);
			Set<Class<? extends Transformer>> transformers = reflections
					.getSubTypesOf(Transformer.class);
			Set<Class<? extends AbstractModule>> guiceModules = reflections
					.getSubTypesOf(AbstractModule.class);

			for (Class<? extends Transformer> transformerClazz : transformers) {
				logger.info("Start the transformation with following Transformer: "
						+ transformerClazz.getName());
				// We need the counterpart Guice module for this transformer
				// In the same package
				Class<? extends AbstractModule> guiceModuleClazz = getGuiceModule(
						guiceModules, transformerClazz);
				// Create the transformer class with Guice module as child
				// injector and execute
				Injector injector = parentInjector
						.createChildInjector(guiceModuleClazz.newInstance());
				Transformer transformer = injector
						.getInstance(transformerClazz);
				transformer.transform(context);

				logger.info("Stop the transformation with following Transformer:"
						+ transformerClazz.getName());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void useTransformerNamesWithOrder(Injector parentInjector)
			throws MojoExecutionException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		// Read the list and parse:
		// 1:de.crowdcode.kissmda.cartridges.extensions.ExtensionExamplesTransformer
		// Put it in a map and sort the content after the order
		Map<String, String> sortedtTransformerNameWithOrders = new TreeMap<String, String>();

		for (String content : transformerNameWithOrders) {
			String order = StringUtils.substringBefore(content, ":");
			String transformerClazz = StringUtils.substringAfter(content, ":");
			sortedtTransformerNameWithOrders.put(order, transformerClazz);
		}

		for (Map.Entry<String, String> entry : sortedtTransformerNameWithOrders
				.entrySet()) {
			// We need the counterpart Guice module for this transformer
			// In the same package but with Module as suffix
			String transformerClazzName = entry.getValue();
			String guiceModuleClazzName = getGuiceModuleName(transformerClazzName);
			Class<Transformer> transformerClazz = (Class<Transformer>) Class
					.forName(transformerClazzName);
			Class<Module> guiceModuleClazz = (Class<Module>) Class
					.forName(guiceModuleClazzName);

			logger.info("Start the transformation with following Transformer: "
					+ transformerClazzName);
			// Create the transformer class with Guice module as child
			// injector and execute
			Injector injector = parentInjector
					.createChildInjector(guiceModuleClazz.newInstance());
			Transformer transformer = injector.getInstance(transformerClazz);
			transformer.transform(context);

			logger.info("Stop the transformation with following Transformer:"
					+ transformerClazzName);
		}
	}

	String getGuiceModuleName(String transformerClazzName) {
		String guiceModuleClazzName = StringUtils.replace(transformerClazzName,
				"Transformer", "Module");
		return guiceModuleClazzName;
	}

	private void setLoggingLevel() {
		Logger log = LogManager.getLogManager().getLogger("");

		if (loggingLevel == null || loggingLevel.equals("")) {
			log.setLevel(Level.INFO);
		} else {
			log.setLevel(loggingLevelMapper.getLevel(loggingLevel));
		}

		for (Handler handler : log.getHandlers()) {
			if (loggingLevel == null || loggingLevel.equals("")) {
				handler.setLevel(Level.INFO);
			} else {
				handler.setLevel(loggingLevelMapper.getLevel(loggingLevel));
			}
		}
	}

	private Class<? extends AbstractModule> getGuiceModule(
			final Set<Class<? extends AbstractModule>> guiceModules,
			final Class<? extends Transformer> transformerClazz)
			throws MojoExecutionException {
		Class<? extends AbstractModule> currentGuiceModuleClazz = null;
		for (Class<? extends AbstractModule> guiceModuleClazz : guiceModules) {
			// Check the package
			String transformerPackageName = transformerClazz.getPackage()
					.getName();
			String guiceModulePackageName = guiceModuleClazz.getPackage()
					.getName();
			if (guiceModulePackageName.equalsIgnoreCase(transformerPackageName)) {
				String guiceModuleNameWithoutModule = StringUtils.replace(
						guiceModuleClazz.getName(), MODULE_SUFFIX, "");
				String transformerNameWithoutTransformer = StringUtils.replace(
						transformerClazz.getName(), TRANSFORMER_SUFFIX, "");
				if (guiceModuleNameWithoutModule
						.equals(transformerNameWithoutTransformer)) {
					currentGuiceModuleClazz = guiceModuleClazz;
					logger.info("Start the transformation with following Guice Module: "
							+ currentGuiceModuleClazz.getName());
					break;
				}
			}
		}

		if (currentGuiceModuleClazz == null) {
			// No module found at all, error
			throw new MojoExecutionException(ERROR_GUICE_SAME_PACKAGE_NOT_FOUND);
		}

		return currentGuiceModuleClazz;
	}
}
