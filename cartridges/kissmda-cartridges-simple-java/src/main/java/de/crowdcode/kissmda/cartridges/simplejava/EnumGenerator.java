/*
x * Licensed to the Apache Software Foundation (ASF) under one
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

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;

import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Generate enumeration from UML enumeration.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: PackageHelper.
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

	@Inject
	private InterfaceGenerator interfaceGenerator;

	@Inject
	private JdtHelper jdtHelper;

	private String sourceDirectoryPackageName;

	private final ArrayList<String> constructorParameterNames = new ArrayList<String>();

	/**
	 * Generate the Class Interface. This is the main generation part for this
	 * SimpleJavaTransformer.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return CompilationUnit the complete class with its content as a String
	 */
	public CompilationUnit generateEnum(Classifier clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		EnumDeclaration ed = generateEnum(clazz, ast, cu);
		generateAttributes(clazz, ast, ed);
		generateConstructor(clazz, ast, ed);
		generateConstants(clazz, ast, ed);
		generateGetterMethod(clazz, ast, ed);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu;
	}

	/**
	 * Generate the attributes.
	 * 
	 * @param clazz
	 *            UML2 class
	 * @param ast
	 *            JDT AST
	 * @param ed
	 *            EnumerationDeclaration
	 */
	@SuppressWarnings("unchecked")
	public void generateAttributes(Classifier clazz, AST ast, EnumDeclaration ed) {
		EList<Property> properties = clazz.getAttributes();
		for (Property property : properties) {
			ed.bodyDeclarations().add(generateAttribute(clazz, ast, property));
		}
	}

	/**
	 * Generate the attribute.
	 * 
	 * @param clazz
	 *            UM2 class
	 * @param ast
	 *            JDT AST
	 * @param property
	 *            UML2 property
	 * @return FieldDeclaration
	 */
	public FieldDeclaration generateAttribute(Classifier clazz, AST ast,
			Property property) {
		Type type = property.getType();
		logger.log(Level.FINE, "Class: " + clazz.getName() + " - "
				+ "Property: " + property.getName() + " - "
				+ "Property Upper: " + property.getUpper() + " - "
				+ "Property Lower: " + property.getLower());
		String umlTypeName = type.getName();
		String umlQualifiedTypeName = type.getQualifiedName();

		// Check whether primitive or array type or simple type?
		org.eclipse.jdt.core.dom.Type chosenType = jdtHelper.getChosenType(ast,
				umlTypeName, umlQualifiedTypeName, sourceDirectoryPackageName);

		VariableDeclarationFragment fragment = ast
				.newVariableDeclarationFragment();
		SimpleName variableName = ast.newSimpleName(property.getName());
		fragment.setName(variableName);

		FieldDeclaration fieldDeclaration = ast.newFieldDeclaration(fragment);
		fieldDeclaration.setType(chosenType);

		return fieldDeclaration;
	}

	/**
	 * Generate getter method.
	 * 
	 * @param clazz
	 *            UML2 classifier
	 * @param ast
	 *            JDT AST tree
	 * @param ed
	 *            EnumDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	public void generateGetterMethod(Classifier clazz, AST ast,
			EnumDeclaration ed) {
		EList<Property> properties = clazz.getAttributes();
		for (Property property : properties) {
			Type type = property.getType();
			logger.log(Level.FINE, "Class: " + clazz.getName() + " - "
					+ "Property: " + property.getName() + " - "
					+ "Property Upper: " + property.getUpper() + " - "
					+ "Property Lower: " + property.getLower());
			String umlTypeName = type.getName();
			String umlQualifiedTypeName = type.getQualifiedName();
			MethodDeclaration methodDeclaration = interfaceGenerator
					.generateGetterMethod(ast, ed, property, umlTypeName,
							umlQualifiedTypeName, sourceDirectoryPackageName);

			// Public
			methodDeclaration.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));

			// Content of getter method
			Block block = ast.newBlock();
			ReturnStatement returnStatement = ast.newReturnStatement();
			SimpleName simpleName = ast.newSimpleName(property.getName());
			returnStatement.setExpression(simpleName);
			block.statements().add(returnStatement);
			methodDeclaration.setBody(block);
		}
	}

	/**
	 * Generate getter method. Use this method if you need to call the method
	 * generateGetterMethod from outside this class.
	 * 
	 * @param clazz
	 *            UML2 classifier
	 * @param ast
	 *            JDT AST tree
	 * @param ed
	 *            EnumDeclaration JDT
	 * @param sourceDirectoryPackageName
	 *            root package name of the UML model
	 */
	public void generateGetterMethod(Classifier clazz, AST ast,
			EnumDeclaration ed, String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;
		generateGetterMethod(clazz, ast, ed);
	}

	/**
	 * Generate constructor.
	 * 
	 * @param clazz
	 *            UML2 class
	 * @param ast
	 *            JDT AST
	 * @param ed
	 *            EnumDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	public void generateConstructor(Classifier clazz, AST ast,
			EnumDeclaration ed) {
		// Constructor
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setConstructor(true);
		md.setName(ast.newSimpleName(clazz.getName()));
		md.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PRIVATE_KEYWORD));
		ed.bodyDeclarations().add(md);

		// We need to build contructor parameters for each properties
		generateContructorParameters(clazz, ast, md);

		// Content of constructor
		Block block = ast.newBlock();

		EList<Property> properties = clazz.getAttributes();
		for (Property property : properties) {
			logger.log(Level.FINE, "Class: " + clazz.getName() + " - "
					+ "Property: " + property.getName() + " - "
					+ "Property Upper: " + property.getUpper() + " - "
					+ "Property Lower: " + property.getLower());

			// Left expression
			SimpleName simpleName = ast.newSimpleName(property.getName());
			ThisExpression thisExpression = ast.newThisExpression();
			FieldAccess fieldAccess = ast.newFieldAccess();
			fieldAccess.setName(simpleName);
			fieldAccess.setExpression(thisExpression);

			// Right expression
			SimpleName parameter = ast.newSimpleName(property.getName());

			Assignment assignment = ast.newAssignment();
			assignment
					.setOperator(org.eclipse.jdt.core.dom.Assignment.Operator.ASSIGN);
			assignment.setLeftHandSide(fieldAccess);
			assignment.setRightHandSide(parameter);

			// Expression
			ExpressionStatement expressionStatement = ast
					.newExpressionStatement(assignment);

			block.statements().add(expressionStatement);
		}

		// Set Body to MethodDeclaration
		md.setBody(block);
	}

	@SuppressWarnings("unchecked")
	void generateContructorParameters(Classifier clazz, AST ast,
			MethodDeclaration md) {
		// Empty the list first
		constructorParameterNames.clear();

		EList<Property> constructorProperties = clazz.getAttributes();
		for (Property constructorProperty : constructorProperties) {
			Type constructorType = constructorProperty.getType();
			// Save the variable declaration for later use
			constructorParameterNames.add(constructorProperty.getName());

			logger.log(
					Level.FINE,
					"Class: " + clazz.getName() + " - "
							+ "Constructor property: "
							+ constructorProperty.getName() + " - "
							+ "Constructor property Upper: "
							+ constructorProperty.getUpper() + " - "
							+ "Constructor property Lower: "
							+ constructorProperty.getLower());

			String contructorUmlTypeName = constructorType.getName();
			String constructorUmlQualifiedTypeName = constructorType
					.getQualifiedName();

			// Check whether primitive or array type or simple type?
			org.eclipse.jdt.core.dom.Type constructorChosenType = jdtHelper
					.getChosenType(ast, contructorUmlTypeName,
							constructorUmlQualifiedTypeName,
							sourceDirectoryPackageName);

			SingleVariableDeclaration variableDeclaration = ast
					.newSingleVariableDeclaration();
			variableDeclaration.setType(constructorChosenType);
			variableDeclaration.setName(ast.newSimpleName(constructorProperty
					.getName()));

			md.parameters().add(variableDeclaration);
		}
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
		PackageDeclaration pd = ast.newPackageDeclaration();
		String fullPackageName = getFullPackageName(clazz);
		pd.setName(ast.newName(fullPackageName));

		Date now = new Date();
		String commentDate = "Generation date: " + now.toString() + ".";

		interfaceGenerator.generatePackageJavadoc(ast, pd,
				PackageComment.CONTENT_1.getValue(),
				PackageComment.CONTENT_2.getValue(), " ",
				PackageComment.CONTENT_3.getValue(), " ", commentDate);

		cu.setPackage(pd);
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
	public void generateConstants(Classifier clazz, AST ast, EnumDeclaration ed) {
		// Get all properties for this enumeration
		Enumeration enumeration = (Enumeration) clazz;
		EList<EnumerationLiteral> enumerationLiterals = enumeration
				.getOwnedLiterals();
		for (EnumerationLiteral enumLiteral : enumerationLiterals) {
			EnumConstantDeclaration ec = ast.newEnumConstantDeclaration();
			ec.setName(ast.newSimpleName(enumLiteral.getName().toUpperCase()));

			// We need to sort the arguments so that it match the
			// constructor!
			if (!constructorParameterNames.isEmpty()) {
				for (String constructorParameterName : constructorParameterNames) {
					logger.log(Level.FINE, "constructorParameterName: "
							+ constructorParameterNames.toString());

					Slot slot = findSlotByName(constructorParameterName,
							enumLiteral);
					if (slot != null) {
						// We found a slot with the same type
						Property property = (Property) slot
								.getDefiningFeature();
						Type type = property.getType();
						chooseLiteralTypeAndAddToEnumConstantArguments(ast, ec,
								slot, type);
					} else {
						// We didn't find the slot
						// TODO doing something...
					}
				}
			} else {
				// Constructor parameter types is empty
				// So we are adding the literal to the EnumConstant arguments
				// just as it is
				EList<Slot> slots = enumLiteral.getSlots();
				for (Slot slot : slots) {
					Property property = (Property) slot.getDefiningFeature();
					Type type = property.getType();
					chooseLiteralTypeAndAddToEnumConstantArguments(ast, ec,
							slot, type);
				}
			}

			ed.enumConstants().add(ec);
		}
	}

	@SuppressWarnings("unchecked")
	void chooseLiteralTypeAndAddToEnumConstantArguments(AST ast,
			EnumConstantDeclaration ec, Slot slot, Type type) {
		EList<ValueSpecification> valueSpecifications = slot.getValues();
		for (ValueSpecification valueSpecification : valueSpecifications) {
			if (type.getName().equalsIgnoreCase("Integer")) {
				NumberLiteral numberLiteral = ast.newNumberLiteral();
				numberLiteral.setToken(String.valueOf(valueSpecification
						.integerValue()));
				ec.arguments().add(numberLiteral);
			} else if (type.getName().equalsIgnoreCase("Long")) {
				NumberLiteral numberLiteral = ast.newNumberLiteral();
				numberLiteral.setToken(String.valueOf(
						valueSpecification.integerValue()).concat("L"));
				ec.arguments().add(numberLiteral);
			} else if (type.getName().equalsIgnoreCase("Boolean")) {
				BooleanLiteral booleanLiteral = ast
						.newBooleanLiteral(valueSpecification.booleanValue());
				ec.arguments().add(booleanLiteral);
			} else if (type.getName().equalsIgnoreCase("String")) {
				StringLiteral stringLiteral = ast.newStringLiteral();
				stringLiteral.setLiteralValue(valueSpecification.stringValue());
				ec.arguments().add(stringLiteral);
			}
		}
	}

	Slot findSlotByName(String name, EnumerationLiteral enumLiteral) {
		EList<Slot> slots = enumLiteral.getSlots();
		for (Slot slot : slots) {
			Property property = (Property) slot.getDefiningFeature();
			String slotPropertyName = property.getName();

			if (slotPropertyName.equals(name)) {
				// We found it
				return slot;
			}
		}

		// We finished but cannot find it
		return null;
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
