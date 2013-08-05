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

import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Test Interface Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionGeneratorTest {

	private ExceptionGenerator exceptionGenerator;
	private PackageHelper packageHelper;
	private JdtHelper jdtHelper;

	private Class clazz;

	@Before
	public void setUp() throws Exception {
		packageHelper = new PackageHelper();
		exceptionGenerator = new ExceptionGenerator();
		jdtHelper = new JdtHelper();
		jdtHelper.setPackageHelper(packageHelper);
		exceptionGenerator.setPackageHelper(packageHelper);
		exceptionGenerator.setJdtHelper(jdtHelper);

		setUpMocks();
	}

	public void setUpMocks() throws Exception {
		String fullQualifiedName = "de::crowdcode::kissmda::testapp::components::CompanyException";
		clazz = mock(Class.class);
		when(clazz.getQualifiedName()).thenReturn(fullQualifiedName);
		when(clazz.getName()).thenReturn("CompanyException");
		when(clazz.getAssociations())
				.thenReturn(new UniqueEList<Association>());
		when(clazz.getImplementedInterfaces()).thenReturn(
				new UniqueEList<Interface>());
		when(clazz.getGeneralizations()).thenReturn(
				new UniqueEList<Generalization>());
	}

	@Test
	public void testGeneratePackage() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		exceptionGenerator.generatePackage(clazz, ast, cu);

		assertEquals("package de.crowdcode.kissmda.testapp.components;\n",
				cu.toString());
	}

	@Test
	public void testGenerateClassCheckedExceptionWithNoInheritance() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends Exception {\n}\n");
	}

	@Test
	public void testGenerateClassCheckedExceptionWithInheritance() {
		EList<Generalization> generalizations = new UniqueEList<Generalization>();
		Generalization generalization = mock(Generalization.class);
		Class clazzGeneralization = mock(Class.class);
		generalizations.add(generalization);
		when(generalization.getGeneral()).thenReturn(clazzGeneralization);
		when(clazzGeneralization.getQualifiedName()).thenReturn(
				"de::test::SuperCompanyException");
		when(clazz.getGeneralizations()).thenReturn(generalizations);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends de.test.SuperCompanyException {\n}\n");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClassCheckedExceptionWithTemplate() {
		TemplateSignature templateSignature = mock(TemplateSignature.class);
		EList<TemplateParameter> templateParameters = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		when(clazz.getOwnedTemplateSignature()).thenReturn(templateSignature);
		when(templateSignature.getParameters()).thenReturn(templateParameters);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends Exception {\n}\n");
	}

	@Test
	public void testGenerateClassUncheckedExceptionWithNoInheritance() {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		exceptionGenerator.setCheckedException(false);
		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends RuntimeException {\n}\n");
	}

	@Test
	public void testGenerateClassUncheckedExceptionWithInheritance() {
		EList<Generalization> generalizations = new UniqueEList<Generalization>();
		Generalization generalization = mock(Generalization.class);
		Class clazzGeneralization = mock(Class.class);
		generalizations.add(generalization);
		when(generalization.getGeneral()).thenReturn(clazzGeneralization);
		when(clazzGeneralization.getQualifiedName()).thenReturn(
				"de::test::SuperCompanyException");
		when(clazz.getGeneralizations()).thenReturn(generalizations);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		exceptionGenerator.setCheckedException(false);
		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends de.test.SuperCompanyException {\n}\n");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateClassUncheckedExceptionWithTemplate() {
		TemplateSignature templateSignature = mock(TemplateSignature.class);
		EList<TemplateParameter> templateParameters = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		when(clazz.getOwnedTemplateSignature()).thenReturn(templateSignature);
		when(templateSignature.getParameters()).thenReturn(templateParameters);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		exceptionGenerator.setCheckedException(false);
		TypeDeclaration typeDeclaration = exceptionGenerator.generateClass(
				clazz, ast, cu);

		assertEquals(typeDeclaration.toString(),
				"public class CompanyException extends RuntimeException {\n}\n");
	}
}
