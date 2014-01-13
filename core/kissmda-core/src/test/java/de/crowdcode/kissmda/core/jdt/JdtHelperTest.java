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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Unit test for Java Helper.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class JdtHelperTest {

	@InjectMocks
	private JdtHelper jdtHelper;

	@Spy
	private PackageHelper packageHelper;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataTypeUtils dataTypeUtils;

	private final AST ast = AST.newAST(AST.JLS3);;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetClassName() {
		String name = jdtHelper
				.getClassName("de.crowdcode.kissmda.testapp.Person");
		assertEquals("Person", name);
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeNotNull() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "Integer";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("Integer", tp.getName().toString());
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeNull() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "org.codecrowd.Test";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("org.codecrowd.Test", tp.getName().toString());
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeCollection() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "Collection";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("java.util.Collection", tp.getName().toString());
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeList() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "List";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("java.util.List", tp.getName().toString());
	}

	@Test
	public void testGetAstPrimitiveType() {
		Map<String, Code> primitiveTypes = createPrimitiveTypeCodes();
		when(dataTypeUtils.getPrimitiveTypeCodes()).thenReturn(primitiveTypes);

		String typeName = "Integer";
		PrimitiveType tp = jdtHelper.getAstPrimitiveType(ast, typeName);

		assertEquals("int", tp.toString());
	}

	@Test
	public void testGetAstPrimitiveTypeBoolean() {
		Map<String, Code> primitiveTypes = createPrimitiveTypeCodes();
		when(dataTypeUtils.getPrimitiveTypeCodes()).thenReturn(primitiveTypes);

		String typeName = "boolean";
		PrimitiveType tp = jdtHelper.getAstPrimitiveType(ast, typeName);

		assertEquals("boolean", tp.toString());
	}

	@Test
	public void testGetAstArrayTypePrimitive() {
		Map<String, Code> primitiveTypes = createPrimitiveTypeCodes();
		when(dataTypeUtils.getPrimitiveTypeCodes()).thenReturn(primitiveTypes);
		when(dataTypeUtils.isPrimitiveType(Mockito.anyString())).thenReturn(
				true);

		String typeName = "byte[]";
		ArrayType tp = jdtHelper.getAstArrayType(ast, typeName);

		assertEquals("byte[]", tp.toString());
	}

	@Test
	public void testGetAstParameterizedType() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "Collection<String>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("java.util.Collection<String>", tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypeWithFullyQualifiedName() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "Collection<de.test.Company>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("java.util.Collection<de.test.Company>", tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypesWithEmptySpace() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "de.test.Attribute<String, Integer, Boolean>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("de.test.Attribute<String,Integer,Boolean>", tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypesWithoutEmptySpace() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "de.test.Attribute<String,Integer,Boolean>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("de.test.Attribute<String,Integer,Boolean>", tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypesComplex1() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);
		when(dataTypeUtils.isParameterizedType(Mockito.anyString()))
				.thenReturn(false).thenReturn(true).thenReturn(false);

		String typeName = "Map<String, List<String>>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("java.util.Map<String,java.util.List<String>>",
				tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypesComplex2() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);
		when(dataTypeUtils.isParameterizedType(Mockito.anyString()))
				.thenReturn(true).thenReturn(false);

		String typeName = "List<List<String>>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("java.util.List<java.util.List<String>>", tp.toString());
	}

	@Test
	public void testGetAstParameterizedTypesComplex3() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);
		when(dataTypeUtils.isArrayType(Mockito.anyString())).thenReturn(true)
				.thenReturn(false).thenReturn(false);
		when(dataTypeUtils.isParameterizedType(Mockito.anyString()))
				.thenReturn(true).thenReturn(false).thenReturn(false);

		String typeName = "Map<String[], List<String>>";
		ParameterizedType tp = jdtHelper.getAstParameterizedType(ast, typeName);

		assertEquals("java.util.Map<String[],java.util.List<String>>",
				tp.toString());
	}

	@Test
	public void testCreateFullQualifiedTypeAsName() {
		String sourceDirectoryPackageName = "Data";
		String umlQualifiedTypeName = "Data::de::test::SuperCompany";
		Name name = jdtHelper.createFullQualifiedTypeAsName(ast,
				umlQualifiedTypeName, sourceDirectoryPackageName);

		assertEquals(name.getFullyQualifiedName(), "de.test.SuperCompany");
	}

	@Test
	public void testCreateReturnType() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "Collection";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::Collection";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnType(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName);

		assertEquals("java.util.Collection", md.getReturnType2().toString());
	}

	@Test
	public void testCreateReturnTypeAsCollectionResultCollection() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "String";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::String";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnTypeAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName,
				JdtHelper.JAVA_UTIL_COLLECTION);

		assertEquals("java.util.Collection<String>", md.getReturnType2()
				.toString());
	}

	@Test
	public void testCreateReturnTypeAsCollectionResultSet() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "String";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::String";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnTypeAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName,
				JdtHelper.JAVA_UTIL_SET);

		assertEquals("java.util.Set<String>", md.getReturnType2().toString());
	}

	@Test
	public void testCreateReturnTypeAsCollectionResultList() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "String";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::String";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnTypeAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName,
				JdtHelper.JAVA_UTIL_LIST);

		assertEquals("java.util.List<String>", md.getReturnType2().toString());
	}

	@Test
	public void testCreateReturnTypeAsCollectionResultSortedSet() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "String";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::String";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnTypeAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName,
				JdtHelper.JAVA_UTIL_SORTEDSET);

		assertEquals("java.util.SortedSet<String>", md.getReturnType2()
				.toString());
	}

	@Test
	public void testCreateReturnTypeGenericsAsCollectionResultSortedSet() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);
		when(
				dataTypeUtils
						.isParameterizedType("CompanyAttribute<String, Integer>"))
				.thenReturn(true);
		when(dataTypeUtils.isParameterizedType("String")).thenReturn(false);
		when(dataTypeUtils.isArrayType("String")).thenReturn(false);
		when(dataTypeUtils.isPrimitiveType("String")).thenReturn(false);
		when(dataTypeUtils.isParameterizedType("Integer")).thenReturn(false);
		when(dataTypeUtils.isArrayType("Integer")).thenReturn(false);
		when(dataTypeUtils.isPrimitiveType("Integer")).thenReturn(false);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "CompanyAttribute<String, Integer>";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::CompanyAttribute<String, Integer>";
		String sourceDirectoryPackageName = "Data";
		jdtHelper.createReturnTypeAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName,
				JdtHelper.JAVA_UTIL_SORTEDSET);

		assertEquals("java.util.SortedSet<CompanyAttribute<String,Integer>>",
				md.getReturnType2().toString());
	}

	@Test
	public void testCreateParameterTypes() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "Person";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::Person";
		String sourceDirectoryPackageName = "Data";
		String umlPropertyName = "person";
		jdtHelper.createParameterTypes(ast, td, md, umlTypeName,
				umlQualifiedTypeName, umlPropertyName,
				sourceDirectoryPackageName);

		SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration) md
				.parameters().get(0);
		assertEquals("Person person", variableDeclaration.toString());
	}

	@Test
	public void testCreateParameterTypesAsCollection() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		TypeDeclaration td = ast.newTypeDeclaration();
		MethodDeclaration md = ast.newMethodDeclaration();
		String umlTypeName = "Person";
		String umlQualifiedTypeName = "Validation Profile::OCL Library::Person";
		String sourceDirectoryPackageName = "Data";
		String umlPropertyName = "persons";
		jdtHelper.createParameterTypesAsCollection(ast, td, md, umlTypeName,
				umlQualifiedTypeName, umlPropertyName,
				sourceDirectoryPackageName, JdtHelper.JAVA_UTIL_COLLECTION);

		SingleVariableDeclaration variableDeclaration = (SingleVariableDeclaration) md
				.parameters().get(0);
		assertEquals("java.util.Collection<Person> persons",
				variableDeclaration.toString());
	}

	private Map<String, String> createJavaTypes() {
		Map<String, String> javaTypes = new HashMap<String, String>();
		javaTypes.put("Integer", "Integer");
		javaTypes.put("Short", "Short");
		javaTypes.put("Collection", "java.util.Collection");
		javaTypes.put("List", "java.util.List");
		javaTypes.put("Map", "java.util.Map");
		return javaTypes;
	}

	private Map<String, Code> createPrimitiveTypeCodes() {
		Map<String, Code> primitiveTypeCodes = new HashMap<String, Code>();
		primitiveTypeCodes.put("integer", PrimitiveType.INT);
		primitiveTypeCodes.put("short", PrimitiveType.SHORT);
		primitiveTypeCodes.put("byte", PrimitiveType.BYTE);
		primitiveTypeCodes.put("boolean", PrimitiveType.BOOLEAN);
		return primitiveTypeCodes;
	}
}
