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
import java.util.Locale;
import java.util.logging.Level;
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

	private static final Logger logger = Logger.getLogger(PackageHelper.class
			.getName());

	@Inject
	private ReaderWriter readerWriter;

	/**
	 * Get root package.
	 * 
	 * @param context
	 *            context of the transformer
	 * @return UML2 package
	 * @throws URISyntaxException
	 */
	public org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		logger.log(Level.INFO,
				"Get from following sourceModel: " + context.getSourceModel());
		String uriString = FILE_PROTOCOL + context.getSourceModel();
		logger.log(Level.INFO, "Get from following URI: " + uriString);
		URI uri = URI.createURI(uriString);
		readerWriter.registerSchema();
		readerWriter.registerResourceFactories();
		readerWriter.registerPathmaps();

		org.eclipse.uml2.uml.Package outPackage = readerWriter.load(uri);

		return outPackage;
	}

	/**
	 * Get full package name.
	 * 
	 * @param clazz
	 *            UML2 classifier
	 * @param sourceDirectoryPackageName
	 *            from the stereotype
	 * @return full package name
	 */
	public String getFullPackageName(Classifier clazz,
			String sourceDirectoryPackageName) {
		// Get package until the beginning of SourceDirectory
		logger.log(Level.FINE, "Qualified name: " + clazz.getQualifiedName());
		// Remove the sourceDirectoryPackageName
		String toBeDeleted = sourceDirectoryPackageName + "::";
		String fullPackageName = clazz.getQualifiedName().replaceFirst(
				toBeDeleted, "");
		// Remove class name
		toBeDeleted = "::" + clazz.getName();
		fullPackageName = fullPackageName.replaceFirst(toBeDeleted, "");
		// Change :: to .
		fullPackageName = fullPackageName.replaceAll("::", ".");
		logger.log(Level.FINE, "Real package name: " + fullPackageName);
		return fullPackageName;
	}

	/**
	 * Get full package name.
	 * 
	 * @param umlPackageNameWithClass
	 *            UML2 package name with classifier as String
	 * @param sourceDirectoryPackageName
	 *            from the stereotype
	 * @return full package name
	 */
	public String getFullPackageName(String umlPackageNameWithClass,
			String sourceDirectoryPackageName) {
		// Get package until the beginning of SourceDirectory
		logger.log(Level.FINE, "Qualified name: " + umlPackageNameWithClass);
		// Remove the sourceDirectoryPackageName
		String toBeDeleted = sourceDirectoryPackageName + "::";
		String fullPackageName = umlPackageNameWithClass.replaceFirst(
				toBeDeleted, "");
		// Change :: to .
		fullPackageName = fullPackageName.replaceAll("::", ".");
		logger.log(Level.FINE, "Real package name: " + fullPackageName);
		return fullPackageName;
	}

	/**
	 * Remove the UML type prefixes.
	 * 
	 * @param fullQualifiedName
	 *            fully qualified name
	 * @return clean type name
	 */
	public String removeUmlPrefixes(final String fullQualifiedName) {
		// At the moment MagicDraw use "Boolean" for primitive type instead of
		// "boolean". We need to change this into boolean since the primitive
		// types are always in lower case
		boolean isNeededToMakeLowerCase = false;
		if (StringUtils.contains(fullQualifiedName,
				UmlTypePrefix.UML_PRIMITIVE_TYPES.getValue())
				|| (StringUtils.contains(fullQualifiedName,
						UmlTypePrefix.JAVA_PRIMITIVE_TYPES.getValue()))) {
			isNeededToMakeLowerCase = true;
		}

		// MagicDraw specific prefixes...
		String result = fullQualifiedName.replace(
				UmlTypePrefix.MAGIC_DRAW_PROFILE_DATATYPES.getValue(), "");
		result = result.replace(UmlTypePrefix.UML_PRIMITIVE_TYPES.getValue(),
				"");
		result = result.replace(UmlTypePrefix.JAVA_PRIMITIVE_TYPES.getValue(),
				"");
		result = result.replace(
				UmlTypePrefix.VALIDATION_PROFILE_OCL_LIBRARY.getValue(), "");

		// Name dataype and dataype-bindings
		if (StringUtils
				.contains(result, UmlTypePrefix.DATA_DATATYPE.getValue())) {
			result = StringUtils.substringAfter(result,
					UmlTypePrefix.DATA_DATATYPE.getValue());
		}
		if (StringUtils.contains(result,
				UmlTypePrefix.DATA_DATATYPE_BINDINGS.getValue())) {
			result = StringUtils.substringAfter(result,
					UmlTypePrefix.DATA_DATATYPE_BINDINGS.getValue());
		}

		// We need to make lower case of the type
		if (isNeededToMakeLowerCase) {
			result = StringUtils.lowerCase(result, Locale.ENGLISH);
		}

		return result;
	}
}
