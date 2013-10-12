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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.Type;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;

/**
 * Test Interface Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(JukitoRunner.class)
public class InterfaceGeneratorTest {

	@Inject
	private InterfaceGenerator interfaceGenerator;

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
		when(clazz.getGeneralizations()).thenReturn(
				new UniqueEList<Generalization>());
		when(clazz.getOwnedComments()).thenReturn(new UniqueEList<Comment>());
	}

	@Test
	public void testGeneratePackage() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		interfaceGenerator.generatePackage(clazz, ast, cu);

		assertEquals("package de.crowdcode.kissmda.testapp.components;\n",
				cu.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClass() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		TypeDeclaration typeDeclaration = interfaceGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(), td.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClassWithInheritance() {
		EList<Generalization> generalizations = new UniqueEList<Generalization>();
		Generalization generalization = mock(Generalization.class);
		Class clazzGeneralization = mock(Class.class);
		generalizations.add(generalization);
		when(generalization.getGeneral()).thenReturn(clazzGeneralization);
		when(clazzGeneralization.getQualifiedName()).thenReturn(
				"de::test::SuperCompany");
		when(clazz.getGeneralizations()).thenReturn(generalizations);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		Name name = ast.newName("de.test.SuperCompany");
		SimpleType simpleType = ast.newSimpleType(name);
		td.superInterfaceTypes().add(simpleType);

		TypeDeclaration typeDeclaration = interfaceGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(), td.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClassWithTemplate() {
		TemplateSignature templateSignature = mock(TemplateSignature.class);
		EList<TemplateParameter> templateParameters = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		when(clazz.getOwnedTemplateSignature()).thenReturn(templateSignature);
		when(templateSignature.getParameters()).thenReturn(templateParameters);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		TypeDeclaration typeDeclaration = interfaceGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(), td.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateMethods() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		EList<Operation> operations = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Operation operation = mock(Operation.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Type operationType = mock(Type.class, Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Operation> iteratorOperation = mock(Iterator.class);
		EList<Type> raisedExceptions = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Type> iteratorException = mock(Iterator.class);
		Type exceptionType = mock(Type.class, Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getOperations()).thenReturn(operations);
		when(operations.iterator()).thenReturn(iteratorOperation);
		when(iteratorOperation.hasNext()).thenReturn(true, false);
		when(iteratorOperation.next()).thenReturn(operation);
		when(operation.getName()).thenReturn("calculateMe");
		when(operation.getType()).thenReturn(operationType);
		when(operation.getRaisedExceptions()).thenReturn(raisedExceptions);
		when(raisedExceptions.iterator()).thenReturn(iteratorException);
		when(iteratorException.hasNext()).thenReturn(true, false);
		when(iteratorException.next()).thenReturn(exceptionType);

		when(exceptionType.getQualifiedName()).thenReturn(
				"de::test::CalculatorException");
		when(operationType.getQualifiedName()).thenReturn(
				"de::test::Calculator");

		interfaceGenerator.generateMethods(clazz, ast, td);

		assertEquals(
				"public interface Company {\n  de.test.Calculator calculateMe() throws de.test.CalculatorException ;\n}\n",
				td.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateMethodJavadoc() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName("calculateAge"));

		Operation operation = mock(Operation.class,
				Answers.RETURNS_DEEP_STUBS.get());
		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Comment> commentIterator = mock(Iterator.class);
		Comment comment = mock(Comment.class, Answers.RETURNS_DEEP_STUBS.get());

		when(operation.getOwnedComments()).thenReturn(comments);
		when(comments.iterator()).thenReturn(commentIterator);
		when(commentIterator.hasNext()).thenReturn(true, false);
		when(commentIterator.next()).thenReturn(comment);
		when(comment.getBody()).thenReturn(
				"Comment...\nTest\n@author: Lofi Dewanto");

		interfaceGenerator.generateMethodJavadoc(ast, operation, md);

		assertEquals(
				"/** \n * Comment...\n * Test\n * @author: Lofi Dewanto\n */\nvoid calculateAge();\n",
				md.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClassJavadoc() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Class clazz = mock(Class.class, Answers.RETURNS_DEEP_STUBS.get());
		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Comment> commentIterator = mock(Iterator.class);
		Comment comment = mock(Comment.class, Answers.RETURNS_DEEP_STUBS.get());

		when(clazz.getOwnedComments()).thenReturn(comments);
		when(comments.iterator()).thenReturn(commentIterator);
		when(commentIterator.hasNext()).thenReturn(true, false);
		when(commentIterator.next()).thenReturn(comment);
		when(comment.getBody()).thenReturn(
				"Comment...\nTest\n@author: Lofi Dewanto");

		interfaceGenerator.generateClassJavadoc(clazz, ast, td);

		assertEquals(
				"/** \n * Comment...\n * Test\n * @author: Lofi Dewanto\n */\npublic interface Company {\n}\n",
				td.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterSetterJavadoc() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName("calculateAge"));

		Property property = mock(Property.class,
				Answers.RETURNS_DEEP_STUBS.get());
		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Comment> commentIterator = mock(Iterator.class);
		Comment comment = mock(Comment.class, Answers.RETURNS_DEEP_STUBS.get());

		when(property.getOwnedComments()).thenReturn(comments);
		when(comments.iterator()).thenReturn(commentIterator);
		when(commentIterator.hasNext()).thenReturn(true, false);
		when(commentIterator.next()).thenReturn(comment);
		when(comment.getBody()).thenReturn(
				"Comment...\nTest\n@author: Lofi Dewanto");

		interfaceGenerator.generateGetterSetterJavadoc(ast, property, md);

		assertEquals(
				"/** \n * Comment...\n * Test\n * @author: Lofi Dewanto\n */\nvoid calculateAge();\n",
				md.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateMethodTemplateParams() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName("calculateAge"));

		Operation operation = mock(Operation.class,
				Answers.RETURNS_DEEP_STUBS.get());
		EList<TemplateParameter> templateParams = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<TemplateParameter> templateParamIterator = mock(Iterator.class);
		TemplateSignature templateSignature = mock(TemplateSignature.class,
				Answers.RETURNS_DEEP_STUBS.get());
		TemplateParameter templateParameter = mock(TemplateParameter.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Classifier classifier = mock(Classifier.class);

		when(operation.getOwnedTemplateSignature()).thenReturn(
				templateSignature);
		when(templateSignature.getParameters()).thenReturn(templateParams);
		when(templateParams.iterator()).thenReturn(templateParamIterator);
		when(templateParamIterator.hasNext()).thenReturn(true, false);
		when(templateParamIterator.next()).thenReturn(templateParameter);
		when(templateParameter.getOwnedParameteredElement()).thenReturn(
				classifier);
		when(classifier.getLabel()).thenReturn("T");

		interfaceGenerator.generateMethodTemplateParams(ast, operation, md);

		assertEquals("<T>void calculateAge();\n", md.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAssociationEndUpperCardinalityMultiplesIsOrdered() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		MethodDeclaration mdGetter = ast.newMethodDeclaration();
		mdGetter.setName(ast.newSimpleName("getCompanies"));

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		when(property.isOrdered()).thenReturn(true);
		when(property.isUnique()).thenReturn(false);

		interfaceGenerator.generateAssociationEndUpperCardinalityMultiples(ast,
				td, property, mdGetter, umlTypeName, umlQualifiedTypeName);

		assertEquals("java.util.List<test.de.Company>", mdGetter
				.getReturnType2().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAssociationEndUpperCardinalityMultiplesIsUnique() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		MethodDeclaration mdGetter = ast.newMethodDeclaration();
		mdGetter.setName(ast.newSimpleName("getCompanies"));

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		when(property.isOrdered()).thenReturn(false);
		when(property.isUnique()).thenReturn(true);

		interfaceGenerator.generateAssociationEndUpperCardinalityMultiples(ast,
				td, property, mdGetter, umlTypeName, umlQualifiedTypeName);

		assertEquals("java.util.Set<test.de.Company>", mdGetter
				.getReturnType2().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAssociationEndUpperCardinalityMultiplesDefault() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		MethodDeclaration mdGetter = ast.newMethodDeclaration();
		mdGetter.setName(ast.newSimpleName("getCompanies"));

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		when(property.isOrdered()).thenReturn(false);
		when(property.isUnique()).thenReturn(false);

		interfaceGenerator.generateAssociationEndUpperCardinalityMultiples(ast,
				td, property, mdGetter, umlTypeName, umlQualifiedTypeName);

		assertEquals("java.util.Collection<test.de.Company>", mdGetter
				.getReturnType2().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAssociationEndUpperCardinalityMultiplesIsUniqueAndIsOrdered() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		MethodDeclaration mdGetter = ast.newMethodDeclaration();
		mdGetter.setName(ast.newSimpleName("getCompanies"));

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		when(property.isOrdered()).thenReturn(true);
		when(property.isUnique()).thenReturn(true);

		interfaceGenerator.generateAssociationEndUpperCardinalityMultiples(ast,
				td, property, mdGetter, umlTypeName, umlQualifiedTypeName);

		assertEquals("java.util.SortedSet<test.de.Company>", mdGetter
				.getReturnType2().toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodUpperZero() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(property.getName()).thenReturn("name");
		when(property.getUpper()).thenReturn(0);
		when(property.getOwnedComments()).thenReturn(comments);

		interfaceGenerator.generateGetterMethod(ast, td, property, umlTypeName,
				umlQualifiedTypeName);

		MethodDeclaration[] methods = td.getMethods();
		for (MethodDeclaration method : methods) {
			assertEquals("getName", method.getName().toString());
			assertEquals("test.de.Company", method.getReturnType2().toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodUpperAsterisk() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		String umlQualifiedTypeName = "test.de.Company";
		String umlTypeName = "Company";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(property.getName()).thenReturn("name");
		when(property.getUpper()).thenReturn(-1);
		when(property.getOwnedComments()).thenReturn(comments);

		interfaceGenerator.generateGetterMethod(ast, td, property, umlTypeName,
				umlQualifiedTypeName);

		MethodDeclaration[] methods = td.getMethods();
		for (MethodDeclaration method : methods) {
			assertEquals("getName", method.getName().toString());
			assertEquals("java.util.Collection<test.de.Company>", method
					.getReturnType2().toString());

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodTypeBoolean() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		String umlQualifiedTypeName = "Boolean";
		String umlTypeName = "Boolean";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(property.getName()).thenReturn("name");
		when(property.getUpper()).thenReturn(0);
		when(property.getOwnedComments()).thenReturn(comments);

		interfaceGenerator.generateGetterMethod(ast, td, property, umlTypeName,
				umlQualifiedTypeName);

		MethodDeclaration[] methods = td.getMethods();
		for (MethodDeclaration method : methods) {
			assertEquals("isName", method.getName().toString());
			assertEquals("Boolean", method.getReturnType2().toString());

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateGetterMethodTypeLowerCaseBoolean() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		Property property = mock(Property.class);

		String umlQualifiedTypeName = "boolean";
		String umlTypeName = "boolean";

		EList<Comment> comments = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		when(property.getName()).thenReturn("name");
		when(property.getUpper()).thenReturn(0);
		when(property.getOwnedComments()).thenReturn(comments);

		interfaceGenerator.generateGetterMethod(ast, td, property, umlTypeName,
				umlQualifiedTypeName);

		MethodDeclaration[] methods = td.getMethods();
		for (MethodDeclaration method : methods) {
			assertEquals("isName", method.getName().toString());
			assertEquals("boolean", method.getReturnType2().toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateMethodParams() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName("calculateAge"));

		Operation operation = mock(Operation.class,
				Answers.RETURNS_DEEP_STUBS.get());
		EList<TemplateParameter> templateParams = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<TemplateParameter> templateParamIterator = mock(Iterator.class);
		TemplateSignature templateSignature = mock(TemplateSignature.class,
				Answers.RETURNS_DEEP_STUBS.get());
		TemplateParameter templateParameter = mock(TemplateParameter.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Classifier classifier = mock(Classifier.class);

		when(operation.getOwnedTemplateSignature()).thenReturn(
				templateSignature);
		when(templateSignature.getParameters()).thenReturn(templateParams);
		when(templateParams.iterator()).thenReturn(templateParamIterator);
		when(templateParamIterator.hasNext()).thenReturn(true, false);
		when(templateParamIterator.next()).thenReturn(templateParameter);
		when(templateParameter.getOwnedParameteredElement()).thenReturn(
				classifier);

		interfaceGenerator.generateMethodParams(ast, td, operation, md);

		assertEquals("void calculateAge();\n", md.toString());
	}
}
