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
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;

import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Generate enumeration from UML enumeration.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: PackageHelper, MethodHelper, JavaHelper, FileWriter and
 * DataTypeUtils
 * </p>
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class EnumGenerator {

	private static final Logger logger = Logger.getLogger(EnumGenerator.class
			.getName());

	@Inject
	private PackageHelper packageHelper;

	private String sourceDirectoryPackageName;

	public void setPackageHelper(PackageHelper packageHelper) {
		this.packageHelper = packageHelper;
	}

	/**
	 * Generate the Class Interface. This is the main generation part for this
	 * SimpleJavaTransformer.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return String the complete class with its content as a String
	 */
	public String generateEnum(Classifier clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		EnumDeclaration ed = generateEnum(clazz, ast, cu);
		generateConstants(clazz, ast, ed);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu.toString();
	}

	/**
	 * Generate the Enum from UML.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param cu
	 *            the generated Java compilation unit
	 * @return EnumDeclaration
	 */
	@SuppressWarnings("unchecked")
	public EnumDeclaration generateEnum(Classifier clazz, AST ast,
			CompilationUnit cu) {
		String className = getClassName(clazz);
		EnumDeclaration ed = ast.newEnumDeclaration();
		ed.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		ed.setName(ast.newSimpleName(className));
		cu.types().add(ed);
		return ed;
	}

	/**
	 * Generate the Java package from UML package.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param cu
	 *            the generated Java compilation unit
	 */
	public void generatePackage(Classifier clazz, AST ast, CompilationUnit cu) {
		PackageDeclaration p1 = ast.newPackageDeclaration();
		String fullPackageName = getFullPackageName(clazz);
		p1.setName(ast.newName(fullPackageName));
		cu.setPackage(p1);
	}

	/**
	 * Generate Enumeration constants.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param ed
	 *            Enumeration declaration for Java JDT
	 */
	@SuppressWarnings("unchecked")
	private void generateConstants(Classifier clazz, AST ast, EnumDeclaration ed) {
		// Get all properties for this enumeration
		Enumeration enumeration = (Enumeration) clazz;
		EList<Element> elements = enumeration.allOwnedElements();
		for (Element element : elements) {
			EnumerationLiteral enumLiteral = (EnumerationLiteral) element;
			EnumConstantDeclaration ec = ast.newEnumConstantDeclaration();
			ec.setName(ast.newSimpleName(enumLiteral.getName().toUpperCase()));
			ed.enumConstants().add(ec);
		}
	}

	private String getClassName(Classifier clazz) {
		String className = clazz.getName();
		return className;
	}

	private String getFullPackageName(Classifier clazz) {
		String fullPackageName = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);
		return fullPackageName;
	}
}
