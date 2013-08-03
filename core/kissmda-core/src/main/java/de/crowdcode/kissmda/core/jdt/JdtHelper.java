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
package de.crowdcode.kissmda.core.jdt;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Helper class for JDT.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class JdtHelper {

	private static final String JAVA_UTIL_COLLECTION = "java.util.Collection";

	@Inject
	private PackageHelper packageHelper;

	@Inject
	private DataTypeUtils dataTypeUtils;

	public void setDataTypeUtils(DataTypeUtils dataTypeUtils) {
		this.dataTypeUtils = dataTypeUtils;
	}

	public void setPackageHelper(PackageHelper packageHelper) {
		this.packageHelper = packageHelper;
	}

	public Name createFullQualifiedTypeAsName(AST ast,
			String fullQualifiedUmlTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper
				.removeUmlPrefixes(fullQualifiedUmlTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		Name name = ast.newName(typeName);

		return name;
	}

	@SuppressWarnings("unchecked")
	public void createReturnType(AST ast, TypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.removeUmlPrefixes(umlQualifiedTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		// Check whether primitive or array type or simple type?
		Type chosenType = null;
		if (dataTypeUtils.isPrimitiveType(typeName)) {
			chosenType = getAstPrimitiveType(ast, umlTypeName);
		} else if (dataTypeUtils.isArrayType(typeName)) {
			chosenType = getAstArrayType(ast, typeName);
		} else if (dataTypeUtils.isParameterizedType(typeName)) {
			chosenType = getAstParameterizedType(ast, typeName);
		} else {
			chosenType = getAstSimpleType(ast, typeName);
		}

		md.setReturnType2(chosenType);
		td.bodyDeclarations().add(md);
	}

	@SuppressWarnings("unchecked")
	public void createReturnTypeAsCollection(AST ast, TypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.removeUmlPrefixes(umlQualifiedTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		// Create Collection
		SimpleType tp = getAstSimpleType(ast, typeName);
		SimpleType collectionType = ast.newSimpleType(ast
				.newName(JAVA_UTIL_COLLECTION));
		ParameterizedType pt = ast.newParameterizedType(collectionType);
		pt.typeArguments().add(tp);
		md.setReturnType2(pt);
		td.bodyDeclarations().add(md);
	}

	public SimpleType getAstSimpleType(AST ast, String typeName) {
		String javaType = dataTypeUtils.getJavaTypes().get(
				typeName.toLowerCase());
		SimpleType tp = null;
		if (javaType != null) {
			tp = ast.newSimpleType(ast.newName(javaType));
		} else {
			tp = ast.newSimpleType(ast.newName(typeName));
		}
		return tp;
	}

	public PrimitiveType getAstPrimitiveType(AST ast, String typeName) {
		Code typeCode = dataTypeUtils.getPrimitiveTypeCodes().get(
				typeName.toLowerCase());
		PrimitiveType primitiveType = ast.newPrimitiveType(typeCode);
		return primitiveType;
	}

	public ArrayType getAstArrayType(AST ast, String typeName) {
		Type componentType = null;
		// Remove [] for componentType
		typeName = StringUtils.remove(typeName, "[]");
		if (dataTypeUtils.isPrimitiveType(typeName)) {
			componentType = getAstPrimitiveType(ast, typeName);
		} else {
			componentType = getAstSimpleType(ast, typeName);
		}

		ArrayType arrayType = ast.newArrayType(componentType);
		return arrayType;
	}

	@SuppressWarnings("unchecked")
	public ParameterizedType getAstParameterizedType(AST ast, String typeName) {
		// Get the component type and parameters <Type, Type, ...>
		String componentTypeName = StringUtils.substringBefore(typeName, "<");
		Type componentType = getAstSimpleType(ast, componentTypeName);
		ParameterizedType parameterizedType = ast
				.newParameterizedType(componentType);

		String paramTypeNames = StringUtils.substringAfter(typeName, "<");
		// Result: String, Integer, Boolean>
		String[] parametersAsString = StringUtils.split(paramTypeNames, ",");
		for (int index = 0; index < parametersAsString.length; index++) {
			String paramTypeName = parametersAsString[index];
			paramTypeName = StringUtils.remove(paramTypeName, ",");
			paramTypeName = StringUtils.remove(paramTypeName, ">");
			paramTypeName = StringUtils.trim(paramTypeName);
			Type paramType = getAstSimpleType(ast, paramTypeName);
			// Add the type arguments
			parameterizedType.typeArguments().add(paramType);
		}

		return parameterizedType;
	}

	@SuppressWarnings("unchecked")
	public void createParameterTypes(AST ast, TypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String umlPropertyName,
			String sourceDirectoryPackageName) {
		String typeName = packageHelper.removeUmlPrefixes(umlQualifiedTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		// Check whether primitive or array type or simple type?
		Type chosenType = null;
		if (dataTypeUtils.isPrimitiveType(typeName)) {
			chosenType = getAstPrimitiveType(ast, umlTypeName);
		} else if (dataTypeUtils.isArrayType(typeName)) {
			chosenType = getAstArrayType(ast, typeName);
		} else if (dataTypeUtils.isParameterizedType(typeName)) {
			chosenType = getAstParameterizedType(ast, typeName);
		} else {
			chosenType = getAstSimpleType(ast, typeName);
		}

		SingleVariableDeclaration variableDeclaration = ast
				.newSingleVariableDeclaration();
		variableDeclaration.setType(chosenType);
		variableDeclaration.setName(ast.newSimpleName(umlPropertyName));
		md.parameters().add(variableDeclaration);
	}

	public String getClassName(final String fullClassName) {
		int lastIndexPoint = fullClassName.lastIndexOf(".");
		String resultClassName = fullClassName.substring(lastIndexPoint + 1,
				fullClassName.length());
		return resultClassName;
	}
}
