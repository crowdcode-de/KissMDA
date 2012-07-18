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
import java.util.Set;
import java.util.logging.Logger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;

import de.crowdcode.kissmda.core.Context;
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

	private static final Logger logger = Logger.getLogger(KissMdaMojo.class
			.getName());

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
	 * @required
	 */
	private List<String> transformerScanPackageNames;

	/**
	 * Model file.
	 * 
	 * @parameter
	 * @required
	 */
	private String modelFile;

	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}

	public void setTransformerScanPackageNames(
			List<String> transformerScanPackageNames) {
		this.transformerScanPackageNames = transformerScanPackageNames;
	}

	public void setProject(MavenProject project) {
		this.project = project;
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
		try {
			Context context = new StandardContext();
			String fullNameModelFile = project.getBasedir() + "/" + modelFile;
			context.setSourceModel(fullNameModelFile);
			for (String packageName : transformerScanPackageNames) {
				Reflections reflections = new Reflections(packageName);
				Set<Class<? extends Transformer>> transformers = reflections
						.getSubTypesOf(Transformer.class);
				for (Class<? extends Transformer> transformerClazz : transformers) {
					logger.info("Start the transformation with following Transformer: "
							+ transformerClazz.getName());
					// Create the transformer class and execute
					Transformer transformer = transformerClazz.newInstance();
					transformer.transform(context);
					logger.info("Stop the transformation with following Transformer:"
							+ transformerClazz.getName());
				}
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
		}
	}
}
