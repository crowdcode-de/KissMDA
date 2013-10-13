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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
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

		assertEquals("package de.crowdcode.kissmda.testapp.components;\n",
				cu.toString());
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
	public void testGenerateConstantsInteger() {
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
	public void testGenerateConstantsString() {
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
	public void testGenerateConstantsLong() {
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
	public void testGenerateConstantsBoolean() {
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

		enumGenerator.generateAttribute(clazz, ast, ed);

		assertEquals("public enum Company {; String type;\n}\n", cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateConstructor() {
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

		enumGenerator.generateConstructor(clazz, ast, ed);

		assertEquals(
				"public enum Company {; private Company(String type){\n  this.type=type;\n}\n}\n",
				cu.toString());
	}
}
