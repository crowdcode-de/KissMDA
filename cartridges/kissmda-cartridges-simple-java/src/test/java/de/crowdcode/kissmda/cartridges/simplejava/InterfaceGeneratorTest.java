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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Test Interface Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class InterfaceGeneratorTest {

	private InterfaceGenerator interfaceGenerator;
	private PackageHelper packageHelper;

	private Class clazz;

	@Before
	public void setUp() throws Exception {
		packageHelper = new PackageHelper();
		interfaceGenerator = new InterfaceGenerator();
		interfaceGenerator.setPackageHelper(packageHelper);

		setUpMocks();
	}

	public void setUpMocks() throws Exception {
		String fullQualifiedName = "Data::de::crowdcode::kissmda::testapp::components::Company";
		clazz = mock(Class.class);
		when(clazz.getQualifiedName()).thenReturn(fullQualifiedName);
		when(clazz.getName()).thenReturn("Company");
		when(clazz.getAssociations())
				.thenReturn(new UniqueEList<Association>());
		when(clazz.getImplementedInterfaces()).thenReturn(
				new UniqueEList<Interface>());
		when(clazz.getGeneralizations()).thenReturn(
				new UniqueEList<Generalization>());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGeneratePackage() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));

		interfaceGenerator.generatePackage(clazz, ast, cu);

		assertEquals("public interface Company {\n}\n", td.toString());
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
		when(clazzGeneralization.getName()).thenReturn("SuperCompany");
		when(clazz.getGeneralizations()).thenReturn(generalizations);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);

		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		SimpleName simpleName = ast.newSimpleName("SuperCompany");
		SimpleType simpleType = ast.newSimpleType(simpleName);
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
}
