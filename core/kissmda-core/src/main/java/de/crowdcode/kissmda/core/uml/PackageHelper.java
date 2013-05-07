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
package de.crowdcode.kissmda.core.uml;

import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Classifier;

import de.crowdcode.kissmda.core.Context;

/**
 * Package Helper class for UML.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class PackageHelper {

	private static final String FILE_PROTOCOL = "file:/";

	private static final String JAVA_PRIMITIVE_TYPES = "JavaPrimitiveTypes::";

	private static final String UML_PRIMITIVE_TYPES = "UMLPrimitiveTypes::";

	private static final String MAGIC_DRAW_PROFILE_DATATYPES = "MagicDraw Profile::datatypes::";

	private static final String VALIDATION_PROFILE_OCL_LIBRARY = "Validation Profile::OCL Library::";

	private static final String DATA_DATATYPE = "datatype::";

	private static final String DATA_DATATYPE_BINDINGS = "datatype-bindings::";

	private static final Logger logger = Logger.getLogger(PackageHelper.class
			.getName());

	@Inject
	private ReaderWriter readerWriter;

	public void setReaderWriter(ReaderWriter readerWriter) {
		this.readerWriter = readerWriter;
	}

	public org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		logger.info("Get from following sourceModel: "
				+ context.getSourceModel());
		String uriString = FILE_PROTOCOL + context.getSourceModel();
		logger.info("Get from following URI: " + uriString);
		URI uri = URI.createURI(uriString);
		readerWriter.registerSchema();
		readerWriter.registerResourceFactories();
		readerWriter.registerPathmaps();

		org.eclipse.uml2.uml.Package outPackage = readerWriter.load(uri);

		return outPackage;
	}

	public String getFullPackageName(Classifier clazz,
			String sourceDirectoryPackageName) {
		// Get package until the beginning of SourceDirectory
		logger.info("Qualified name: " + clazz.getQualifiedName());
		// Remove the sourceDirectoryPackageName
		String toBeDeleted = sourceDirectoryPackageName + "::";
		String fullPackageName = clazz.getQualifiedName().replaceFirst(
				toBeDeleted, "");
		// Remove class name
		toBeDeleted = "::" + clazz.getName();
		fullPackageName = fullPackageName.replaceFirst(toBeDeleted, "");
		// Change :: to .
		fullPackageName = fullPackageName.replaceAll("::", ".");
		logger.info("Real package name: " + fullPackageName);
		return fullPackageName;
	}

	public String getFullPackageName(String umlPackageNameWithClass,
			String sourceDirectoryPackageName) {
		// Get package until the beginning of SourceDirectory
		logger.info("Qualified name: " + umlPackageNameWithClass);
		// Remove the sourceDirectoryPackageName
		String toBeDeleted = sourceDirectoryPackageName + "::";
		String fullPackageName = umlPackageNameWithClass.replaceFirst(
				toBeDeleted, "");
		// Change :: to .
		fullPackageName = fullPackageName.replaceAll("::", ".");
		logger.info("Real package name: " + fullPackageName);
		return fullPackageName;
	}

	public String removeUmlPrefixes(final String fullQualifiedName) {
		// MagicDraw specific stuffs...
		String result = fullQualifiedName.replace(MAGIC_DRAW_PROFILE_DATATYPES,
				"");
		result = result.replace(UML_PRIMITIVE_TYPES, "");
		result = result.replace(JAVA_PRIMITIVE_TYPES, "");
		result = result.replace(VALIDATION_PROFILE_OCL_LIBRARY, "");

		// Name dataype and dataype-bindings
		if (StringUtils.contains(result, DATA_DATATYPE)) {
			result = StringUtils.substringAfter(result, DATA_DATATYPE);
		}
		if (StringUtils.contains(result, DATA_DATATYPE_BINDINGS)) {
			result = StringUtils.substringAfter(result, DATA_DATATYPE_BINDINGS);
		}

		return result;
	}
}
