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
package de.crowdcode.kissmda.cartridges.extensions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;
import de.crowdcode.kissmda.core.file.FileWriter;
import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * ExtensionExamplesTransformer Transformer. This generates xxx for all the xxx from
 * the given UML model.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: PackageHelper, MethodHelper, JavaHelper, FileWriter and
 * DataTypeUtils
 * </p>
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExtensionExamplesTransformer implements Transformer {

	private static final Logger logger = Logger
			.getLogger(ExtensionExamplesTransformer.class.getName());

	private static final String STEREOTYPE_ENTITY = "Entity";

	private static final String STEREOTYPE_SOURCEDIRECTORY = "SourceDirectory";

	private String sourceDirectoryPackageName;

	@Inject
	private PackageHelper packageHelper;

	@Inject
	private FileWriter fileWriter;

	private Context context;

	public void setFileWriter(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

	public void setPackageHelper(PackageHelper packageHelper) {
		this.packageHelper = packageHelper;
	}

	/**
	 * Start the transformation and generation.
	 * 
	 * @param context
	 *            context object from Maven plugin
	 * @return void nothing
	 * @exception throw
	 *                TransformerException if something wrong happens
	 */
	@Override
	public void transform(Context context) throws TransformerException {
		this.context = context;
		try {
			// Get the root package
			org.eclipse.uml2.uml.Package outPackage = getRootPackage(context);
			sourceDirectoryPackageName = "";

			// Check the stereotype of the root package
			checkStereotypeRootPackage(outPackage);

			// Get all elements with defined stereotypes
			EList<Element> list = outPackage.allOwnedElements();
			for (Element element : list) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (Stereotype stereotype : stereotypes) {
					if (stereotype.getName().equals(STEREOTYPE_SOURCEDIRECTORY)) {
						// From this SourceDirectory we can work...
						org.eclipse.uml2.uml.Package packagez = (org.eclipse.uml2.uml.Package) element;
						sourceDirectoryPackageName = packagez.getName();
						logger.info("SourceDirectory package name: "
								+ sourceDirectoryPackageName);
					}
					if (stereotype.getName().equals(STEREOTYPE_ENTITY)) {
						// Stereotype Interface
						Class clazz = (Class) element;
						logger.info("Class: " + clazz.getName() + " - "
								+ "Stereotype: " + stereotype.getName());
						// Generate xxx for this class
						// ...
						
						generateClassFile(clazz, "TODO");
					}
				}
			}
		} catch (URISyntaxException e) {
			throw new TransformerException(e);
		} catch (IOException e) {
			throw new TransformerException(e);
		}
	}

	private void checkStereotypeRootPackage(
			org.eclipse.uml2.uml.Package outPackage) {
		EList<Stereotype> rootStereotypes = outPackage.getAppliedStereotypes();
		for (Stereotype stereotype : rootStereotypes) {
			if (stereotype.getName().equals(STEREOTYPE_SOURCEDIRECTORY)) {
				// From this SourceDirectory we can work...
				org.eclipse.uml2.uml.Package packagez = outPackage;
				sourceDirectoryPackageName = packagez.getName();
				logger.info("SourceDirectory package name: "
						+ sourceDirectoryPackageName);
			}
		}
	}

	private org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		org.eclipse.uml2.uml.Package outPackage = packageHelper
				.getRootPackage(context);
		return outPackage;
	}

	/**
	 * Create the output file on the directory.
	 * 
	 * @param clazz
	 *            UML2 class of Eclipse
	 * @param compilationUnit
	 *            compilation unit from JDT
	 * @throws IOException
	 *             input or output error on file system
	 */
	private void generateClassFile(Class clazz, String compilationUnit)
			throws IOException {
		String fullPackageName = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);
		fileWriter.createFile(context, fullPackageName, clazz.getName(),
				compilationUnit);
	}
}