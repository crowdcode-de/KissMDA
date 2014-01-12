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
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Helper class for JDT.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class JdtHelper {

	public static final String JAVA_UTIL_COLLECTION = "java.util.Collection";

	public static final String JAVA_UTIL_LIST = "java.util.List";

	public static final String JAVA_UTIL_SET = "java.util.Set";

	public static final String JAVA_UTIL_SORTEDSET = "java.util.SortedSet";

	@Inject
	private PackageHelper packageHelper;

	@Inject
	private DataTypeUtils dataTypeUtils;

	/**
	 * Create a fully qualified type as JDT Name.
	 * 
	 * @param ast
	 *            AST tree
	 * @param fullQualifiedUmlTypeName
	 *            UML qualified type name
	 * @param sourceDirectoryPackageName
	 *            source directory of UML
	 * @return JDT Name
	 */
	public Name createFullQualifiedTypeAsName(AST ast,
			String fullQualifiedUmlTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.getFullPackageName(
				fullQualifiedUmlTypeName, sourceDirectoryPackageName);
		Name name = ast.newName(typeName);

		return name;
	}

	/**
	 * Create a fully qualified type as String.
	 * 
	 * @param ast
	 *            AST tree
	 * @param fullQualifiedUmlTypeName
	 *            UML qualified type name
	 * @param sourceDirectoryPackageName
	 *            source directory of UML
	 * @return the type name fully qualified
	 */
	public String createFullQualifiedTypeAsString(AST ast,
			String fullQualifiedUmlTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.getFullPackageName(
				fullQualifiedUmlTypeName, sourceDirectoryPackageName);

		return typeName;
	}

	/**
	 * Create return type for JDT MethodDeclaration.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            JDT abstract type declaration
	 * @param md
	 *            JDT method declaration
	 * @param umlTypeName
	 *            UML type name
	 * @param umlQualifiedTypeName
	 *            UML qualified type name
	 * @param sourceDirectoryPackageName
	 *            source directory of UML
	 */
	@SuppressWarnings("unchecked")
	public void createReturnType(AST ast, AbstractTypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String sourceDirectoryPackageName) {
		// Choose type
		Type chosenType = getChosenType(ast, umlTypeName, umlQualifiedTypeName,
				sourceDirectoryPackageName);

		md.setReturnType2(chosenType);
		td.bodyDeclarations().add(md);
	}

	/**
	 * Calculate the type we can choose.
	 * 
	 * @param ast
	 *            AST JDT
	 * @param umlTypeName
	 *            UML2 Type Name
	 * @param umlQualifiedTypeName
	 *            UML2 Qualified Type Name
	 * @param sourceDirectoryPackageName
	 *            source directory
	 * @return Type
	 */
	public Type getChosenType(AST ast, String umlTypeName,
			String umlQualifiedTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.getFullPackageName(
				umlQualifiedTypeName, sourceDirectoryPackageName);

		// Check whether primitive or array type or parameterized type
		// or simple type?
		Type chosenType = null;
		if (dataTypeUtils.isPrimitiveType(typeName)) {
			chosenType = getAstPrimitiveType(ast, typeName);
		} else if (dataTypeUtils.isArrayType(typeName)) {
			chosenType = getAstArrayType(ast, typeName);
		} else if (dataTypeUtils.isParameterizedType(typeName)) {
			chosenType = getAstParameterizedType(ast, typeName);
		} else {
			chosenType = getAstSimpleType(ast, typeName);
		}
		return chosenType;
	}

	/**
	 * Create return type for JDT MethodDeclaration as Java Collection.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            JDT abstract type declaration
	 * @param md
	 *            JDT method declaration
	 * @param umlTypeName
	 *            UML type name
	 * @param umlQualifiedTypeName
	 *            UML qualified type name
	 * @param sourceDirectoryPackageName
	 *            source directory of UML
	 * @param collectionTypeConstant
	 *            type of the collection: Collection, Set or List
	 */
	@SuppressWarnings("unchecked")
	public void createReturnTypeAsCollection(AST ast,
			AbstractTypeDeclaration td, MethodDeclaration md,
			String umlTypeName, String umlQualifiedTypeName,
			String sourceDirectoryPackageName, String collectionTypeConstant) {
		Type type = getChosenType(ast, umlTypeName, umlQualifiedTypeName,
				sourceDirectoryPackageName);
		// Create Collection
		SimpleType collectionType = ast.newSimpleType(ast
				.newName(collectionTypeConstant));
		ParameterizedType pt = ast.newParameterizedType(collectionType);
		pt.typeArguments().add(type);
		md.setReturnType2(pt);
		td.bodyDeclarations().add(md);
	}

	/**
	 * Get JDT SimpleType for the given type name.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param typeName
	 *            input type name
	 * @return JDT SimpleType
	 */
	public SimpleType getAstSimpleType(AST ast, String typeName) {
		String javaType = dataTypeUtils.getJavaTypes().get(typeName);
		SimpleType tp = null;
		if (javaType != null) {
			tp = ast.newSimpleType(ast.newName(javaType));
		} else {
			tp = ast.newSimpleType(ast.newName(typeName));
		}
		return tp;
	}

	/**
	 * Get JDT PrimitiveType for the given type name.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param typeName
	 *            input type name
	 * @return JDT PrimitiveType
	 */
	public PrimitiveType getAstPrimitiveType(AST ast, String typeName) {
		Code typeCode = dataTypeUtils.getPrimitiveTypeCodes().get(
				typeName.toLowerCase());
		PrimitiveType primitiveType = ast.newPrimitiveType(typeCode);
		return primitiveType;
	}

	/**
	 * Get JDT ArrayType for the given type name.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param typeName
	 *            input type name
	 * @return JDT ArrayType
	 */
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

	/**
	 * Get JDT ParameterizedType for the given type name.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param typeName
	 *            input type name
	 * @return JDT ParameterizedType
	 */
	@SuppressWarnings("unchecked")
	public ParameterizedType getAstParameterizedType(AST ast, String typeName) {
		// Get the component type and parameters <Type, Type, ...>
		String componentTypeName = StringUtils.substringBefore(typeName, "<");
		Type componentType = getAstSimpleType(ast, componentTypeName);
		ParameterizedType parameterizedType = ast
				.newParameterizedType(componentType);

		String paramTypeNames = StringUtils.substringAfter(typeName, "<");
		paramTypeNames = StringUtils.removeEnd(paramTypeNames, ">");
		// Result: String, Integer, List<Boolean>, de.test.Company
		String[] parametersAsString = StringUtils.split(paramTypeNames, ",");
		for (int index = 0; index < parametersAsString.length; index++) {
			String paramTypeName = parametersAsString[index];

			paramTypeName = StringUtils.remove(paramTypeName, ",");
			paramTypeName = StringUtils.trim(paramTypeName);

			Type paramType = getChosenType(ast, paramTypeName, paramTypeName,
					"");

			// Add the type arguments
			parameterizedType.typeArguments().add(paramType);
		}

		return parameterizedType;
	}

	/**
	 * Create parameter types for MethodDeclaration.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            JDT AbstractTypeDeclaration
	 * @param md
	 *            JDT MethodDeclaration
	 * @param umlTypeName
	 *            UML type name
	 * @param umlQualifiedTypeName
	 *            UML fully qualified type name
	 * @param umlPropertyName
	 *            UML property name
	 * @param sourceDirectoryPackageName
	 *            UML source directory start
	 */
	@SuppressWarnings("unchecked")
	public void createParameterTypes(AST ast, AbstractTypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String umlPropertyName,
			String sourceDirectoryPackageName) {
		Type chosenType = getChosenType(ast, umlTypeName, umlQualifiedTypeName,
				sourceDirectoryPackageName);

		SingleVariableDeclaration variableDeclaration = ast
				.newSingleVariableDeclaration();
		variableDeclaration.setType(chosenType);
		variableDeclaration.setName(ast.newSimpleName(umlPropertyName));
		md.parameters().add(variableDeclaration);
	}

	/**
	 * Get the class name as String.
	 * 
	 * @param fullClassName
	 *            given full qualified class name
	 * @return just the class name
	 */
	public String getClassName(final String fullClassName) {
		int lastIndexPoint = fullClassName.lastIndexOf(".");
		String resultClassName = fullClassName.substring(lastIndexPoint + 1,
				fullClassName.length());
		return resultClassName;
	}
}
