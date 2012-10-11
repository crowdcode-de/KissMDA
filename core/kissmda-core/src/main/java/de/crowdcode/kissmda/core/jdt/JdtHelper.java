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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
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

	private static final String VOID = "void";

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

	@SuppressWarnings("unchecked")
	public void createReturnType(AST ast, TypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String sourceDirectoryPackageName) {
		String typeName = packageHelper.removeUmlPrefixes(umlQualifiedTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		// Only void is primitive, everything else are simple type
		if (typeName.equalsIgnoreCase(VOID)) {
			PrimitiveType primitiveType = getAstPrimitiveType(ast, umlTypeName);
			md.setReturnType2(primitiveType);
			td.bodyDeclarations().add(md);
		} else {
			SimpleType tp = getAstSimpleType(ast, typeName);
			md.setReturnType2(tp);
			td.bodyDeclarations().add(md);
		}
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
		return ast.newPrimitiveType(typeCode);
	}

	@SuppressWarnings("unchecked")
	public void createParameterTypes(AST ast, TypeDeclaration td,
			MethodDeclaration md, String umlTypeName,
			String umlQualifiedTypeName, String umlPropertyName,
			String sourceDirectoryPackageName) {
		String typeName = packageHelper.removeUmlPrefixes(umlQualifiedTypeName);
		typeName = packageHelper.getFullPackageName(typeName,
				sourceDirectoryPackageName);
		// Only void is primitive, everything else are simple type
		if (typeName.equalsIgnoreCase(VOID)) {
			PrimitiveType primitiveType = getAstPrimitiveType(ast, umlTypeName);
			SingleVariableDeclaration variableDeclaration = ast
					.newSingleVariableDeclaration();
			variableDeclaration.setType(ast.newPrimitiveType(primitiveType
					.getPrimitiveTypeCode()));
			variableDeclaration.setName(ast.newSimpleName(umlPropertyName));
			md.parameters().add(variableDeclaration);
		} else {
			SimpleType tp = getAstSimpleType(ast, typeName);
			SingleVariableDeclaration variableDeclaration = ast
					.newSingleVariableDeclaration();
			variableDeclaration.setType(tp);
			variableDeclaration.setName(ast.newSimpleName(umlPropertyName));
			md.parameters().add(variableDeclaration);
		}
	}

	public String getClassName(final String fullClassName) {
		int lastIndexPoint = fullClassName.lastIndexOf(".");
		String resultClassName = fullClassName.substring(lastIndexPoint + 1,
				fullClassName.length());
		return resultClassName;
	}
}
