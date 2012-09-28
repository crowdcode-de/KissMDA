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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.jdt.MethodHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;
import de.crowdcode.kissmda.core.uml.UmlHelper;

/**
 * Generate Interface from UML class.
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
public class InterfaceGenerator {

	private static final Logger logger = Logger
			.getLogger(InterfaceGenerator.class.getName());

	@Inject
	private MethodHelper methodHelper;

	@Inject
	private JdtHelper jdtHelper;

	@Inject
	private PackageHelper packageHelper;

	@Inject
	private UmlHelper umlHelper;

	private String sourceDirectoryPackageName;

	public void setMethodHelper(MethodHelper methodHelper) {
		this.methodHelper = methodHelper;
	}

	public void setJdtHelper(JdtHelper javaHelper) {
		this.jdtHelper = javaHelper;
	}

	public void setUmlHelper(UmlHelper umlHelper) {
		this.umlHelper = umlHelper;
	}

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
	public String generateInterface(Class clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateMethods(clazz, ast, td);
		generateAssociations(clazz, ast, td);
		generateGettersSetters(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu.toString();
	}

	@SuppressWarnings("unchecked")
	private void generateGettersSetters(Class clazz, AST ast, TypeDeclaration td) {
		// Create getter and setter
		EList<Property> properties = clazz.getAllAttributes();
		for (Property property : properties) {
			MethodDeclaration mdGetter = ast.newMethodDeclaration();
			mdGetter.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			String getterName = methodHelper.getGetterName(property.getName());
			mdGetter.setName(ast.newSimpleName(getterName));
			// Return type?
			Type type = property.getType();
			String umlTypeName = type.getName();
			String umlQualifiedTypeName = type.getQualifiedName();
			jdtHelper.createReturnType(ast, td, mdGetter, umlTypeName,
					umlQualifiedTypeName, sourceDirectoryPackageName);

			// Create setter method for each property
			MethodDeclaration mdSetter = ast.newMethodDeclaration();
			mdSetter.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			String setterName = methodHelper.getSetterName(property.getName());
			mdSetter.setName(ast.newSimpleName(setterName));
			// Return type void
			PrimitiveType primitiveType = jdtHelper.getAstPrimitiveType(ast,
					"void");
			mdSetter.setReturnType2(primitiveType);
			td.bodyDeclarations().add(mdSetter);
			// Params
			String umlPropertyName = property.getName();
			jdtHelper.createParameterTypes(ast, td, mdSetter, umlTypeName,
					umlQualifiedTypeName, umlPropertyName,
					sourceDirectoryPackageName);
		}
	}

	public void generateAssociations(Class clazz, AST ast, TypeDeclaration td) {
		EList<Association> associations = umlHelper.getAllAssociations(clazz);
		for (Association association : associations) {
			logger.info("Association for Class: " + clazz.getName() + " - "
					+ association.toString());
			EList<Property> memberEnds = association.getMemberEnds();
			for (Property memberEnd : memberEnds) {
				logger.info("Member end: " + memberEnd.getName());
				// TODO Generate getter for each member ends
				// Return type: one - the class
				// Return type: many - collection of the class
				String getterMemberEndName = methodHelper
						.getGetterName(memberEnd.getName());
				logger.info("Member end Getter: " + getterMemberEndName);
				// TODO Generate adder for member end which has *-relationship
				// TODO Generate setter for member end which has 1-relationship
			}
		}
	}

	@SuppressWarnings("unchecked")
	private TypeDeclaration generateClass(Class clazz, AST ast,
			CompilationUnit cu) {
		String className = getClassName(clazz);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		td.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		td.setName(ast.newSimpleName(className));
		cu.types().add(td);
		return td;
	}

	private void generatePackage(Class clazz, AST ast, CompilationUnit cu) {
		PackageDeclaration p1 = ast.newPackageDeclaration();
		String fullPackageName = getFullPackageName(clazz);
		p1.setName(ast.newName(fullPackageName));
		cu.setPackage(p1);
	}

	@SuppressWarnings("unchecked")
	private void generateMethods(Class clazz, AST ast, TypeDeclaration td) {
		// Get all methods for this clazz
		EList<Operation> operations = clazz.getAllOperations();
		for (Operation operation : operations) {
			MethodDeclaration md = ast.newMethodDeclaration();
			md.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			md.setName(ast.newSimpleName(operation.getName()));
			// Return type?
			Type type = operation.getType();
			String umlTypeName = type.getName();
			String umlQualifiedTypeName = type.getQualifiedName();
			logger.info("Type: " + umlQualifiedTypeName);
			jdtHelper.createReturnType(ast, td, md, umlTypeName,
					umlQualifiedTypeName, sourceDirectoryPackageName);
		}
	}

	private String getClassName(Class clazz) {
		String className = clazz.getName();
		logger.info("Classname: " + className);
		return className;
	}

	private String getFullPackageName(Class clazz) {
		String fullPackageName = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);
		return fullPackageName;
	}
}
