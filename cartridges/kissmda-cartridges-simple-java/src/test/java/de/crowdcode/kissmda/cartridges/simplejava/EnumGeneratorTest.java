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
package de.crowdcode.kissmda.cartridges.simplejava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;

/**
 * Test Enum Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(JukitoRunner.class)
public class EnumGeneratorTest {

	@Inject
	private EnumGenerator enumGenerator;

	private Class clazz;

	@Before
	public void setUp() throws Exception {
		setUpMocks();
	}

	public void setUpMocks() throws Exception {
		String fullQualifiedName = "de::crowdcode::kissmda::testapp::components::Company";
		clazz = mock(Class.class);
		when(clazz.getQualifiedName()).thenReturn(fullQualifiedName);
		when(clazz.getName()).thenReturn("Company");
		when(clazz.getAssociations())
				.thenReturn(new UniqueEList<Association>());
		when(clazz.getImplementedInterfaces()).thenReturn(
				new UniqueEList<Interface>());
	}

	@Test
	public void testGeneratePackage() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		enumGenerator.generatePackage(clazz, ast, cu);

		String onlyPackage = cu.toString();
		String expectedResult = "package de.crowdcode.kissmda.testapp.components;\n";

		boolean isInside = onlyPackage.contains(expectedResult);

		assertTrue(isInside);
	}

	@Test
	public void testGenerateEnum() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		enumGenerator.generateEnum(clazz, ast, cu);

		assertEquals("public enum Company {}\n", cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsIntegerWithNoParamNames() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots = mock(EList.class);
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot = mock(Slot.class);

		Property property = mock(Property.class);
		Type type = mock(Type.class);

		EList<ValueSpecification> valueSpecifications = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter = mock(Iterator.class);
		ValueSpecification valueSpecification = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(false);
		when(slotIter.next()).thenReturn(slot);
		when(slot.getDefiningFeature()).thenReturn(property);

		when(property.getType()).thenReturn(type);
		when(type.getName()).thenReturn("Integer");

		when(slot.getValues()).thenReturn(valueSpecifications);
		when(valueSpecifications.iterator()).thenReturn(valueSpecificationIter);
		when(valueSpecificationIter.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter.next()).thenReturn(valueSpecification);
		when(valueSpecification.integerValue()).thenReturn(0);

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(0)}\n", ed.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsStringWithNoParamNames() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots = mock(EList.class);
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot = mock(Slot.class);

		Property property = mock(Property.class);
		Type type = mock(Type.class);

		EList<ValueSpecification> valueSpecifications = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter = mock(Iterator.class);
		ValueSpecification valueSpecification = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(false);
		when(slotIter.next()).thenReturn(slot);
		when(slot.getDefiningFeature()).thenReturn(property);

		when(property.getType()).thenReturn(type);
		when(type.getName()).thenReturn("String");

		when(slot.getValues()).thenReturn(valueSpecifications);
		when(valueSpecifications.iterator()).thenReturn(valueSpecificationIter);
		when(valueSpecificationIter.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter.next()).thenReturn(valueSpecification);
		when(valueSpecification.stringValue()).thenReturn("Home");

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(\"Home\")}\n", ed.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsLongWithNoParamNames() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots = mock(EList.class);
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot = mock(Slot.class);

		Property property = mock(Property.class);
		Type type = mock(Type.class);

		EList<ValueSpecification> valueSpecifications = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter = mock(Iterator.class);
		ValueSpecification valueSpecification = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(false);
		when(slotIter.next()).thenReturn(slot);
		when(slot.getDefiningFeature()).thenReturn(property);

		when(property.getType()).thenReturn(type);
		when(type.getName()).thenReturn("Long");

		when(slot.getValues()).thenReturn(valueSpecifications);
		when(valueSpecifications.iterator()).thenReturn(valueSpecificationIter);
		when(valueSpecificationIter.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter.next()).thenReturn(valueSpecification);
		when(valueSpecification.integerValue()).thenReturn(1);

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(1L)}\n", ed.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsBooleanWithNoParamNames() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots = mock(EList.class);
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot = mock(Slot.class);

		Property property = mock(Property.class);
		Type type = mock(Type.class);

		EList<ValueSpecification> valueSpecifications = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter = mock(Iterator.class);
		ValueSpecification valueSpecification = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(false);
		when(slotIter.next()).thenReturn(slot);
		when(slot.getDefiningFeature()).thenReturn(property);

		when(property.getType()).thenReturn(type);
		when(type.getName()).thenReturn("boolean");

		when(slot.getValues()).thenReturn(valueSpecifications);
		when(valueSpecifications.iterator()).thenReturn(valueSpecificationIter);
		when(valueSpecificationIter.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter.next()).thenReturn(valueSpecification);
		when(valueSpecification.booleanValue()).thenReturn(true);

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(true)}\n", ed.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethod() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> properties = mock(EList.class);
		Iterator<Property> propertyIter = mock(Iterator.class);
		Property property = mock(Property.class);
		Type type = mock(Type.class);
		String name = "type";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(properties);
		when(properties.iterator()).thenReturn(propertyIter);
		when(propertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(propertyIter.next()).thenReturn(property);
		when(property.getType()).thenReturn(type);
		when(property.getName()).thenReturn(name);
		when(property.getUpper()).thenReturn(1);
		when(property.getLower()).thenReturn(1);
		when(property.getOwnedComments()).thenReturn(comments);
		when(type.getName()).thenReturn("String");
		when(type.getQualifiedName()).thenReturn("String");

		enumGenerator.generateGetterMethod(clazz, ast, ed);

		assertEquals(
				"public enum Company {; public String getType(){\n  return type;\n}\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodWithSourceDirectoryPackageNameWrong() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> properties = mock(EList.class);
		Iterator<Property> propertyIter = mock(Iterator.class);
		Property property = mock(Property.class);
		Type type = mock(Type.class);
		String name = "type";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(properties);
		when(properties.iterator()).thenReturn(propertyIter);
		when(propertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(propertyIter.next()).thenReturn(property);
		when(property.getType()).thenReturn(type);
		when(property.getName()).thenReturn(name);
		when(property.getUpper()).thenReturn(1);
		when(property.getLower()).thenReturn(1);
		when(property.getOwnedComments()).thenReturn(comments);
		when(type.getName()).thenReturn("Data::String");
		when(type.getQualifiedName()).thenReturn("Data::String");

		enumGenerator.generateGetterMethod(clazz, ast, ed);

		assertEquals(
				"public enum Company {; public Data.String getType(){\n  return type;\n}\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodWithSourceDirectoryPackageNameCorrect() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> properties = mock(EList.class);
		Iterator<Property> propertyIter = mock(Iterator.class);
		Property property = mock(Property.class);
		Type type = mock(Type.class);
		String name = "type";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(properties);
		when(properties.iterator()).thenReturn(propertyIter);
		when(propertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(propertyIter.next()).thenReturn(property);
		when(property.getType()).thenReturn(type);
		when(property.getName()).thenReturn(name);
		when(property.getUpper()).thenReturn(1);
		when(property.getLower()).thenReturn(1);
		when(property.getOwnedComments()).thenReturn(comments);
		when(type.getName()).thenReturn("Data::String");
		when(type.getQualifiedName()).thenReturn("Data::String");

		enumGenerator.generateGetterMethod(clazz, ast, ed, "Data");

		assertEquals(
				"public enum Company {; public String getType(){\n  return type;\n}\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAttribute() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> properties = mock(EList.class);
		Iterator<Property> propertyIter = mock(Iterator.class);
		Property property = mock(Property.class);
		Type type = mock(Type.class);
		String name = "type";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(properties);
		when(properties.iterator()).thenReturn(propertyIter);
		when(propertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(propertyIter.next()).thenReturn(property);
		when(property.getType()).thenReturn(type);
		when(property.getName()).thenReturn(name);
		when(property.getUpper()).thenReturn(1);
		when(property.getLower()).thenReturn(1);
		when(property.getOwnedComments()).thenReturn(comments);
		when(type.getName()).thenReturn("String");
		when(type.getQualifiedName()).thenReturn("String");

		enumGenerator.generateAttributes(clazz, ast, ed);

		assertEquals("public enum Company {; String type;\n}\n", cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstructorWithOneParameter() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> firstProperties = mock(EList.class);
		Iterator<Property> firstPropertyIter = mock(Iterator.class);

		EList<Property> secondProperties = mock(EList.class);
		Iterator<Property> secondPropertyIter = mock(Iterator.class);

		Property property = mock(Property.class);
		Type type = mock(Type.class);
		String name = "type";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(firstProperties).thenReturn(
				secondProperties);
		when(firstProperties.iterator()).thenReturn(firstPropertyIter);
		when(firstPropertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(firstPropertyIter.next()).thenReturn(property);

		when(secondProperties.iterator()).thenReturn(secondPropertyIter);
		when(secondPropertyIter.hasNext()).thenReturn(true).thenReturn(false);
		when(secondPropertyIter.next()).thenReturn(property);

		when(property.getType()).thenReturn(type);
		when(property.getName()).thenReturn(name);
		when(property.getUpper()).thenReturn(1);
		when(property.getLower()).thenReturn(1);
		when(property.getOwnedComments()).thenReturn(comments);
		when(type.getName()).thenReturn("String");
		when(type.getQualifiedName()).thenReturn("String");

		enumGenerator.generateConstructor(clazz, ast, ed);

		assertEquals(
				"public enum Company {; private Company(String type){\n  this.type=type;\n}\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstructorWithTwoParameters() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		EList<Property> firstProperties = mock(EList.class);
		Iterator<Property> firstPropertyIter = mock(Iterator.class);

		EList<Property> secondProperties = mock(EList.class);
		Iterator<Property> secondPropertyIter = mock(Iterator.class);

		Property firstProperty = mock(Property.class);
		Type firstType = mock(Type.class);
		String firstName = "type";

		Property secondProperty = mock(Property.class);
		Type secondType = mock(Type.class);
		String secondName = "count";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(firstProperties).thenReturn(
				secondProperties);
		when(firstProperties.iterator()).thenReturn(firstPropertyIter);
		when(firstPropertyIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(firstPropertyIter.next()).thenReturn(firstProperty).thenReturn(
				secondProperty);

		when(secondProperties.iterator()).thenReturn(secondPropertyIter);
		when(secondPropertyIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(secondPropertyIter.next()).thenReturn(firstProperty).thenReturn(
				secondProperty);

		when(firstProperty.getType()).thenReturn(firstType);
		when(firstProperty.getName()).thenReturn(firstName);
		when(firstProperty.getUpper()).thenReturn(1);
		when(firstProperty.getLower()).thenReturn(1);
		when(firstProperty.getOwnedComments()).thenReturn(comments);
		when(firstType.getName()).thenReturn("String");
		when(firstType.getQualifiedName()).thenReturn("String");

		when(secondProperty.getType()).thenReturn(secondType);
		when(secondProperty.getName()).thenReturn(secondName);
		when(secondProperty.getUpper()).thenReturn(1);
		when(secondProperty.getLower()).thenReturn(1);
		when(secondProperty.getOwnedComments()).thenReturn(comments);
		when(secondType.getName()).thenReturn("Integer");
		when(secondType.getQualifiedName()).thenReturn("Integer");

		enumGenerator.generateConstructor(clazz, ast, ed);

		assertEquals(
				"public enum Company {; private Company(String type,Integer count){\n  this.type=type;\n  this.count=count;\n}\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateContructorParameters() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(true);
		md.setName(ast.newSimpleName(clazz.getName()));
		md.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));

		EList<Property> properties = mock(EList.class);
		Iterator<Property> propertyIter = mock(Iterator.class);
		Property propertyName = mock(Property.class);
		String name = "name";
		Type typeName = mock(Type.class);
		Property propertyCount = mock(Property.class);
		String count = "count";
		Type typeCount = mock(Type.class);

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getAttributes()).thenReturn(properties);
		when(properties.iterator()).thenReturn(propertyIter);
		when(propertyIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(propertyIter.next()).thenReturn(propertyName).thenReturn(
				propertyCount);

		when(propertyName.getType()).thenReturn(typeName);
		when(propertyName.getName()).thenReturn(name);
		when(propertyName.getUpper()).thenReturn(1);
		when(propertyName.getLower()).thenReturn(1);
		when(propertyName.getOwnedComments()).thenReturn(comments);
		when(typeName.getName()).thenReturn("String");
		when(typeName.getQualifiedName()).thenReturn("String");

		when(propertyCount.getType()).thenReturn(typeCount);
		when(propertyCount.getName()).thenReturn(count);
		when(propertyCount.getUpper()).thenReturn(1);
		when(propertyCount.getLower()).thenReturn(1);
		when(propertyCount.getOwnedComments()).thenReturn(comments);
		when(typeCount.getName()).thenReturn("Integer");
		when(typeCount.getQualifiedName()).thenReturn("Integer");

		enumGenerator.generateContructorParameters(clazz, ast, md);

		ed.bodyDeclarations().add(md);

		assertEquals(
				"public enum Company {; private Company(String name,Integer count);\n}\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindSlotByNameFoundIt() {
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);
		EList<Slot> slots = mock(EList.class, Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot1 = mock(Slot.class);
		Slot slot2 = mock(Slot.class);
		Property property1 = mock(Property.class);
		Property property2 = mock(Property.class);

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(slotIter.next()).thenReturn(slot1).thenReturn(slot2);
		when(slot1.getDefiningFeature()).thenReturn(property1);
		when(slot2.getDefiningFeature()).thenReturn(property2);
		when(property1.getName()).thenReturn("test1");
		when(property2.getName()).thenReturn("test2");

		Slot slotResult = enumGenerator.findSlotByName("test1", enumLiteral);

		assertEquals(slot1, slotResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindSlotByNameNotFound() {
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);
		EList<Slot> slots = mock(EList.class, Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot1 = mock(Slot.class);
		Slot slot2 = mock(Slot.class);
		Property property1 = mock(Property.class);
		Property property2 = mock(Property.class);

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(slotIter.next()).thenReturn(slot1).thenReturn(slot2);
		when(slot1.getDefiningFeature()).thenReturn(property1);
		when(slot2.getDefiningFeature()).thenReturn(property2);
		when(property1.getName()).thenReturn("test1");
		when(property2.getName()).thenReturn("test2");

		Slot slotResult = enumGenerator.findSlotByName("test3", enumLiteral);

		assertEquals(null, slotResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsWithConstructorParameterNamesAndWithFoundSlot() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots = mock(EList.class);
		Iterator<Slot> slotIter = mock(Iterator.class);
		Slot slot1 = mock(Slot.class);
		Slot slot2 = mock(Slot.class);

		Property property1 = mock(Property.class);
		Property property2 = mock(Property.class);
		Type type1 = mock(Type.class);
		Type type2 = mock(Type.class);

		EList<ValueSpecification> valueSpecifications1 = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter1 = mock(Iterator.class);
		EList<ValueSpecification> valueSpecifications2 = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter2 = mock(Iterator.class);
		ValueSpecification valueSpecification1 = mock(ValueSpecification.class);
		ValueSpecification valueSpecification2 = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots);
		when(slots.iterator()).thenReturn(slotIter);
		when(slotIter.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(slotIter.next()).thenReturn(slot1).thenReturn(slot2);
		when(slot1.getDefiningFeature()).thenReturn(property1);
		when(slot2.getDefiningFeature()).thenReturn(property2);

		when(property1.getType()).thenReturn(type1);
		when(property2.getType()).thenReturn(type2);
		when(property1.getName()).thenReturn("type");
		when(property2.getName()).thenReturn("name");
		when(type1.getName()).thenReturn("boolean");
		when(type2.getName()).thenReturn("String");

		when(slot1.getValues()).thenReturn(valueSpecifications1);
		when(slot2.getValues()).thenReturn(valueSpecifications2);
		when(valueSpecifications1.iterator()).thenReturn(
				valueSpecificationIter1);
		when(valueSpecifications2.iterator()).thenReturn(
				valueSpecificationIter2);
		when(valueSpecificationIter1.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter2.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter1.next()).thenReturn(valueSpecification1);
		when(valueSpecificationIter2.next()).thenReturn(valueSpecification2);
		when(valueSpecification1.booleanValue()).thenReturn(true);
		when(valueSpecification2.stringValue()).thenReturn("Lofi");

		ArrayList<String> constructorParameterNames = new ArrayList<String>();
		constructorParameterNames.add("type");
		constructorParameterNames.add("name");
		enumGenerator.setConstructorParameterNames(constructorParameterNames);

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(true,\"Lofi\")}\n",
				ed.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstantsWithConstructorParameterNamesAndWithSlotNotFound() {
		// TODO
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		EnumDeclaration ed = enumGenerator.generateEnum(clazz, ast, cu);

		Enumeration enumeration = mock(Enumeration.class);
		EList<EnumerationLiteral> enumLiterals = mock(EList.class);
		Iterator<EnumerationLiteral> enumIter = mock(Iterator.class);
		EnumerationLiteral enumLiteral = mock(EnumerationLiteral.class);

		EList<Slot> slots1 = mock(EList.class);
		EList<Slot> slots2 = mock(EList.class);
		Iterator<Slot> slotIter1 = mock(Iterator.class);
		Iterator<Slot> slotIter2 = mock(Iterator.class);
		Slot slot1 = mock(Slot.class);
		Slot slot2 = mock(Slot.class);

		Property property1 = mock(Property.class);
		Property property2 = mock(Property.class);
		Type type1 = mock(Type.class);
		Type type2 = mock(Type.class);

		EList<ValueSpecification> valueSpecifications1 = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter1 = mock(Iterator.class);
		EList<ValueSpecification> valueSpecifications2 = mock(EList.class);
		Iterator<ValueSpecification> valueSpecificationIter2 = mock(Iterator.class);
		ValueSpecification valueSpecification1 = mock(ValueSpecification.class);
		ValueSpecification valueSpecification2 = mock(ValueSpecification.class);

		when(enumeration.getOwnedLiterals()).thenReturn(enumLiterals);
		when(enumLiterals.iterator()).thenReturn(enumIter);
		when(enumIter.hasNext()).thenReturn(true).thenReturn(false);
		when(enumIter.next()).thenReturn(enumLiteral);
		when(enumLiteral.getName()).thenReturn("Home");

		when(enumLiteral.getSlots()).thenReturn(slots1).thenReturn(slots2);
		when(slots1.iterator()).thenReturn(slotIter1);
		when(slots2.iterator()).thenReturn(slotIter2);
		when(slotIter1.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(slotIter2.hasNext()).thenReturn(true).thenReturn(true)
				.thenReturn(false);
		when(slotIter1.next()).thenReturn(slot1).thenReturn(slot2);
		when(slotIter2.next()).thenReturn(slot1).thenReturn(slot2);
		when(slot1.getDefiningFeature()).thenReturn(property1);
		when(slot2.getDefiningFeature()).thenReturn(property2);

		when(property1.getType()).thenReturn(type1);
		when(property2.getType()).thenReturn(type2);
		when(property1.getName()).thenReturn("type");
		when(property2.getName()).thenReturn("name");
		when(type1.getName()).thenReturn("boolean");
		when(type2.getName()).thenReturn("String");

		when(slot1.getValues()).thenReturn(valueSpecifications1);
		when(slot2.getValues()).thenReturn(valueSpecifications2);
		when(valueSpecifications1.iterator()).thenReturn(
				valueSpecificationIter1);
		when(valueSpecifications2.iterator()).thenReturn(
				valueSpecificationIter2);
		when(valueSpecificationIter1.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter2.hasNext()).thenReturn(true).thenReturn(
				false);
		when(valueSpecificationIter1.next()).thenReturn(valueSpecification1);
		when(valueSpecificationIter2.next()).thenReturn(valueSpecification2);
		when(valueSpecification1.booleanValue()).thenReturn(true);
		when(valueSpecification2.stringValue()).thenReturn("Lofi");

		// Cannot find the parameter name of the constructor!
		ArrayList<String> constructorParameterNames = new ArrayList<String>();
		constructorParameterNames.add("typeX");
		constructorParameterNames.add("nameY");
		enumGenerator.setConstructorParameterNames(constructorParameterNames);

		enumGenerator.generateConstants(enumeration, ast, ed);

		assertEquals("public enum Company {HOME(true,\"Lofi\")}\n",
				ed.toString());
	}
}
