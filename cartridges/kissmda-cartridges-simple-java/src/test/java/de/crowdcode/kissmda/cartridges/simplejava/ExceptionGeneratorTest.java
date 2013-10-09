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

import java.util.logging.Logger;

import javax.inject.Inject;

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
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;

import de.crowdcode.kissmda.core.TransformerException;

/**
 * Test Interface Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(JukitoRunner.class)
public class ExceptionGeneratorTest {

	private static final Logger logger = Logger
			.getLogger(ExceptionGeneratorTest.class.getName());

	@Inject
	private ExceptionGenerator exceptionGenerator;

	private Class clazz;

	@Before
	public void setUp() throws Exception {
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
	public void testGenerateClassUncheckedExceptionWithMultipleInheritance() {
		EList<Generalization> generalizations = new UniqueEList<Generalization>();
		Generalization generalization1 = mock(Generalization.class);
		Generalization generalization2 = mock(Generalization.class);
		Class clazzGeneralization1 = mock(Class.class);
		Class clazzGeneralization2 = mock(Class.class);
		generalizations.add(generalization1);
		generalizations.add(generalization2);
		when(generalization1.getGeneral()).thenReturn(clazzGeneralization1);
		when(clazzGeneralization1.getQualifiedName()).thenReturn(
				"de::test::SuperCompanyException");
		when(generalization2.getGeneral()).thenReturn(clazzGeneralization2);
		when(clazzGeneralization2.getQualifiedName()).thenReturn(
				"de::test::SuperDuperCompanyException");
		when(clazz.getGeneralizations()).thenReturn(generalizations);

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		exceptionGenerator.setCheckedException(false);
		try {
			exceptionGenerator.generateClass(clazz, ast, cu);
			assertTrue(false);
		} catch (TransformerException e) {
			logger.info("Error: " + e.getMessage());
			assertTrue(true);
		}
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

	@Test
	public void testGenerateSerialVersionUID() {
		AST ast = AST.newAST(AST.JLS3);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setName(ast.newSimpleName("Company"));

		exceptionGenerator.generateSerialVersionUID(clazz, ast, td);

		assertEquals(
				"class Company {\n  private static final long serialVersionUID=1L;\n}\n",
				td.toString());
	}

	@Test
	public void testGenerateConstructors() {
		AST ast = AST.newAST(AST.JLS3);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setName(ast.newSimpleName("CompanyException"));

		when(clazz.getName()).thenReturn("CompanyException");

		exceptionGenerator.generateConstructors(clazz, ast, td);

		assertEquals(
				"class CompanyException {\n  public CompanyException(){\n  }\n"
						+ "  public CompanyException(  Throwable cause){\n"
						+ "    super(cause);\n  }\n  public CompanyException(  String message){\n"
						+ "    super(message);\n  }\n"
						+ "  public CompanyException(  String message,  Throwable cause){\n"
						+ "    super(message,cause);\n  }\n" + "}\n",
				td.toString());
	}

	@Test
	public void testGenerateConstructorWithParams() {
		AST ast = AST.newAST(AST.JLS3);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setName(ast.newSimpleName("CompanyException"));

		when(clazz.getName()).thenReturn("CompanyException");

		exceptionGenerator.generateConstructorWithParams(clazz, ast, td,
				new String[] { "String", "Throwable" }, new String[] {
						"message", "cause" });

		assertEquals(
				"class CompanyException {\n"
						+ "  public CompanyException(  String message,  Throwable cause){\n"
						+ "    super(message,cause);\n  }\n" + "}\n",
				td.toString());
	}
}
