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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;

import de.crowdcode.kissmda.core.TransformerException;
import de.crowdcode.kissmda.core.jdt.JdtHelper;

/**
 * Generate Exception from UML class.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: InterfaceGenerator, JdtHelper.
 * </p>
 * 
 * @author Lofi Dewanto
 * @version 1.1.0
 * @since 1.1.0
 */
public class ExceptionGenerator {

	private static final Logger logger = Logger
			.getLogger(ExceptionGenerator.class.getName());

	@Inject
	private InterfaceGenerator interfaceGenerator;

	@Inject
	private JdtHelper jdtHelper;

	private String sourceDirectoryPackageName;

	private boolean isCheckedException = true;

	public void setInterfaceGenerator(InterfaceGenerator interfaceGenerator) {
		this.interfaceGenerator = interfaceGenerator;
	}

	public void setCheckedException(boolean isCheckedException) {
		this.isCheckedException = isCheckedException;
	}

	public void setJdtHelper(JdtHelper javaHelper) {
		this.jdtHelper = javaHelper;
	}

	/**
	 * Generate the Class Exception.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return String the complete class with its content as a String
	 * @throws TransformerException
	 */
	public String generateCheckedException(Classifier clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;
		this.isCheckedException = true;
		CompilationUnit cu = generateException(clazz);
		return cu.toString();
	}

	/**
	 * Generate the Class RuntimeException.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return String the complete class with its content as a String
	 */
	public String generateUncheckedException(Classifier clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;
		this.isCheckedException = false;
		CompilationUnit cu = generateException(clazz);
		return cu.toString();
	}

	/**
	 * Generate general Exception. This will be called by Unchecked and Checked
	 * Exception generation.
	 * 
	 * @param clazz
	 *            UML class
	 * @return JDT compilation unit
	 */
	private CompilationUnit generateException(Classifier clazz) {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateSerialVersionUID(clazz, ast, td);
		generateMethods(clazz, ast, td);
		generateConstructors(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu;
	}

	/**
	 * Generate the serial version UID for the Exception class.
	 * 
	 * @param clazz
	 *            UML class
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            JDT type declaration (Class)
	 */
	@SuppressWarnings("unchecked")
	public void generateSerialVersionUID(Classifier clazz, AST ast,
			TypeDeclaration td) {
		VariableDeclarationFragment fragment = ast
				.newVariableDeclarationFragment();
		SimpleName variableName = ast.newSimpleName("serialVersionUID");
		fragment.setName(variableName);
		NumberLiteral initializer = ast.newNumberLiteral();
		initializer.setToken("1L");
		fragment.setInitializer(initializer);

		FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(fragment);
		fieldDeclaration.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		fieldDeclaration.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		fieldDeclaration.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
		fieldDeclaration.setType(ast.newPrimitiveType(PrimitiveType.LONG));

		td.bodyDeclarations().add(fieldDeclaration);
	}

	/**
	 * Generate the Exception Class.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param cu
	 *            the generated Java compilation unit
	 * @return TypeDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	public TypeDeclaration generateClass(Classifier clazz, AST ast,
			CompilationUnit cu) {
		String className = getClassName(clazz);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(false);
		td.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		td.setName(ast.newSimpleName(className));

		// Add inheritance
		generateClassInheritance(clazz, ast, td);
		// Add template params
		generateClassTemplateParams(clazz, ast, td);

		cu.types().add(td);

		return td;
	}

	/**
	 * {@link InterfaceGenerator #generateClassTemplateParams(Classifier, AST, TypeDeclaration)}
	 */
	private void generateClassTemplateParams(Classifier clazz, AST ast,
			TypeDeclaration td) {
		interfaceGenerator.generateClassTemplateParams(clazz, ast, td);
	}

	/**
	 * Generate the inheritance for the Exception Class "extends". Important:
	 * Java only supports single inheritance!
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration JDT
	 */
	private void generateClassInheritance(Classifier clazz, AST ast,
			TypeDeclaration td) {
		EList<Generalization> generalizations = clazz.getGeneralizations();
		if (generalizations != null) {
			if (!generalizations.isEmpty()) {
				if (generalizations.size() == 1) {
					// Java only supports one Generatlization
					for (Generalization generalization : generalizations) {
						Classifier interfaceClassifier = generalization
								.getGeneral();
						String fullQualifiedInterfaceName = interfaceClassifier
								.getQualifiedName();
						Name name = jdtHelper.createFullQualifiedTypeAsName(
								ast, fullQualifiedInterfaceName,
								sourceDirectoryPackageName);
						SimpleType simpleType = ast.newSimpleType(name);
						td.setSuperclassType(simpleType);
					}
				} else {
					throw new TransformerException(
							"Java only supports single inheritance! Wrong modeling in class: "
									+ clazz.getQualifiedName());
				}
			} else {
				// Empty, we extend from java.lang.Exception or
				// java.lang.RuntimeException
				String exceptionToBeInherited = "Exception";
				if (!isCheckedException) {
					exceptionToBeInherited = "RuntimeException";
				}
				SimpleType simpleType = ast.newSimpleType(ast
						.newSimpleName(exceptionToBeInherited));
				td.setSuperclassType(simpleType);
			}
		}
	}

	/**
	 * {@link InterfaceGenerator #generatePackage(Classifier, AST, CompilationUnit)}
	 */
	private void generatePackage(Classifier clazz, AST ast, CompilationUnit cu) {
		interfaceGenerator.generatePackage(clazz, ast, cu);
	}

	/**
	 * {@link InterfaceGenerator #generateMethods(Classifier, AST, TypeDeclaration)}
	 */
	private void generateMethods(Classifier clazz, AST ast, TypeDeclaration td) {
		interfaceGenerator.generateMethods(clazz, ast, td);
	}

	/**
	 * Generate the constructors for Exception.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration JDT
	 */
	private void generateConstructors(Classifier clazz, AST ast,
			TypeDeclaration td) {
		// TODO Auto-generated method stub

	}

	private String getClassName(Classifier clazz) {
		String className = clazz.getName();
		return className;
	}
}
