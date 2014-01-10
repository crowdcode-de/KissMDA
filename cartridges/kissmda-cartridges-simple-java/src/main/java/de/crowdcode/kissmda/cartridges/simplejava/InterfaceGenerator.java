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

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.Type;

import de.crowdcode.kissmda.core.jdt.DataTypeUtils;
import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.jdt.MethodHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;
import de.crowdcode.kissmda.core.uml.UmlHelper;

/**
 * Generate Interface from UML class.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: PackageHelper, MethodHelper, JdtHelper.
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

	@Inject
	private DataTypeUtils dataTypeUtils;

	private String sourceDirectoryPackageName;

	/**
	 * Generate the Class Interface. This is the main generation part for this
	 * SimpleJavaTransformer.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return CompilationUnit the complete class with its content as a String
	 */
	public CompilationUnit generateInterface(Classifier clazz,
			String sourceDirectoryPackageName) {
		this.sourceDirectoryPackageName = sourceDirectoryPackageName;
		logger.log(Level.FINE, "Start generateInterface: " + clazz.getName()
				+ " -----------------------------");

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateMethods(clazz, ast, td);
		generateGettersSetters(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		logger.log(Level.FINE, "End generateInterface: " + clazz.getName()
				+ " -----------------------------");
		return cu;
	}

	/**
	 * Generate comment for the compilation unit.
	 * 
	 * @param ast
	 *            the JDT Java AST
	 * @param packageDeclaration
	 *            the package declaration where we want to insert the javadoc
	 * @param comment
	 *            comments
	 */
	@SuppressWarnings("unchecked")
	public void generatePackageJavadoc(AST ast,
			PackageDeclaration packageDeclaration, String... comment) {
		Javadoc javadoc = ast.newJavadoc();

		for (String actualComment : comment) {
			TagElement tagElement = ast.newTagElement();
			tagElement.setTagName(actualComment);
			javadoc.tags().add(tagElement);
		}

		packageDeclaration.setJavadoc(javadoc);
	}

	/**
	 * Generate the Getters and Setters methods.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration Java JDT
	 */
	public void generateGettersSetters(Classifier clazz, AST ast,
			TypeDeclaration td) {
		// Create getter and setter for all attributes
		// Without inheritance
		EList<Property> properties = clazz.getAttributes();
		for (Property property : properties) {
			// Create getter for each property
			// Return type?
			Type type = property.getType();
			logger.log(Level.FINE, "Class: " + clazz.getName() + " - "
					+ "Property: " + property.getName() + " - "
					+ "Property Upper: " + property.getUpper() + " - "
					+ "Property Lower: " + property.getLower());
			String umlTypeName = type.getName();
			String umlQualifiedTypeName = type.getQualifiedName();

			// Only for parameterized type
			if (dataTypeUtils.isParameterizedType(umlTypeName)) {
				Map<String, String> types = umlHelper
						.checkParameterizedTypeForTemplateParameterSubstitution(type);
				umlTypeName = types.get("umlTypeName");
				umlQualifiedTypeName = types.get("umlQualifiedTypeName");
			}

			// Check the property name, no content means we have to get the
			// "type" and use it as the name
			if (property.getName().equals("")) {
				Type targetType = property.getType();
				String newPropertyName = "";
				if (property.getUpper() >= 0) {
					// Upper Cardinality 0..1
					newPropertyName = targetType.getName();
				} else {
					// Upper Cardinality 0..*
					newPropertyName = methodHelper.getPluralName(targetType
							.getName());
				}
				property.setName(StringUtils.uncapitalize(newPropertyName));
			}

			// Create getter for each property
			generateGetterMethod(ast, td, property, umlTypeName,
					umlQualifiedTypeName);

			if (!property.isReadOnly()) {
				// Create setter method for each property
				generateSetterMethod(ast, td, property, umlTypeName,
						umlQualifiedTypeName);
			}
		}
	}

	/**
	 * Generate the getter method.
	 * 
	 * @param ast
	 *            AST JDT
	 * @param td
	 *            Abstract Type Declaration JDT
	 * @param property
	 *            UML2 property
	 * @param umlTypeName
	 *            UML2 type name
	 * @param umlQualifiedTypeName
	 *            UML2 qualified type name
	 */
	public MethodDeclaration generateGetterMethod(AST ast,
			AbstractTypeDeclaration td, Property property, String umlTypeName,
			String umlQualifiedTypeName) {
		MethodDeclaration mdGetter = ast.newMethodDeclaration();

		String getterName = methodHelper.getGetterName(property.getName());
		// Check for boolean or Boolean, we need to make
		// isXxx instead of getXxx
		if (umlTypeName.equalsIgnoreCase("boolean")) {
			getterName = methodHelper.getIsName(property.getName());
		}
		mdGetter.setName(ast.newSimpleName(getterName));

		if (property.getUpper() >= 0) {
			// Upper Cardinality 0..1
			jdtHelper.createReturnType(ast, td, mdGetter, umlTypeName,
					umlQualifiedTypeName, sourceDirectoryPackageName);
		} else {
			// Upper Cardinality 0..*
			generateAssociationEndUpperCardinalityMultiples(ast, td, property,
					mdGetter, umlTypeName, umlQualifiedTypeName);
		}

		// Getter Javadoc
		generateGetterSetterJavadoc(ast, property, mdGetter);

		return mdGetter;
	}

	/**
	 * Generate the setter method.
	 * 
	 * @param ast
	 *            AST JDT
	 * @param td
	 *            Abstract Type Declaration JDT
	 * @param property
	 *            UML2 property
	 * @param umlTypeName
	 *            UML2 type name
	 * @param umlQualifiedTypeName
	 *            UML2 qualified type name
	 */
	@SuppressWarnings("unchecked")
	public void generateSetterMethod(AST ast, AbstractTypeDeclaration td,
			Property property, String umlTypeName, String umlQualifiedTypeName) {
		MethodDeclaration mdSetter = ast.newMethodDeclaration();
		// Return type void
		PrimitiveType primitiveType = jdtHelper
				.getAstPrimitiveType(ast, "void");
		mdSetter.setReturnType2(primitiveType);
		td.bodyDeclarations().add(mdSetter);
		String umlPropertyName = property.getName();

		if (property.getUpper() >= 0) {
			// Upper Cardinality 0..1 params
			String setterName = methodHelper.getSetterName(property.getName());
			mdSetter.setName(ast.newSimpleName(setterName));
			jdtHelper.createParameterTypes(ast, td, mdSetter, umlTypeName,
					umlQualifiedTypeName, umlPropertyName,
					sourceDirectoryPackageName);
		} else {
			// Upper Cardinality 0..* params
			// We need to use addXxx instead of setXxx
			String singularAdderName = methodHelper.getSingularName(property
					.getName());
			String adderName = methodHelper.getAdderName(singularAdderName);
			umlPropertyName = methodHelper.getSingularName(umlPropertyName);
			mdSetter.setName(ast.newSimpleName(adderName));
			jdtHelper.createParameterTypes(ast, td, mdSetter, umlTypeName,
					umlQualifiedTypeName, umlPropertyName,
					sourceDirectoryPackageName);
		}
		// Setter Javadoc
		generateGetterSetterJavadoc(ast, property, mdSetter);
	}

	/**
	 * Generate the association end for * association.
	 * 
	 * @param ast
	 *            AST from JDT
	 * @param td
	 *            TypeDeclaration JDT
	 * @param property
	 *            UML2 property (aka AssociationEnd)
	 * @param mdGetter
	 *            method declaration JDT
	 * @param umlTypeName
	 *            UML2 type name as String
	 * @param umlQualifiedTypeName
	 *            UML2 qualified type name as String
	 */
	public void generateAssociationEndUpperCardinalityMultiples(AST ast,
			AbstractTypeDeclaration td, Property property,
			MethodDeclaration mdGetter, String umlTypeName,
			String umlQualifiedTypeName) {
		// Check for isOrdered and isUnique
		if (property.isOrdered() && !property.isUnique()) {
			// We need to add List<Type> as returnType
			jdtHelper.createReturnTypeAsCollection(ast, td, mdGetter,
					umlTypeName, umlQualifiedTypeName,
					sourceDirectoryPackageName, JdtHelper.JAVA_UTIL_LIST);
		} else if (property.isUnique() && !property.isOrdered()) {
			// We need to add Set<Type> as returnType
			jdtHelper.createReturnTypeAsCollection(ast, td, mdGetter,
					umlTypeName, umlQualifiedTypeName,
					sourceDirectoryPackageName, JdtHelper.JAVA_UTIL_SET);
		} else if (property.isUnique() && property.isOrdered()) {
			// We need to add SortedSet<Type> as returnType
			jdtHelper.createReturnTypeAsCollection(ast, td, mdGetter,
					umlTypeName, umlQualifiedTypeName,
					sourceDirectoryPackageName, JdtHelper.JAVA_UTIL_SORTEDSET);
		} else {
			// We need to add Collection<Type> as returnType
			jdtHelper.createReturnTypeAsCollection(ast, td, mdGetter,
					umlTypeName, umlQualifiedTypeName,
					sourceDirectoryPackageName, JdtHelper.JAVA_UTIL_COLLECTION);
		}
	}

	/**
	 * Generate Javadoc for Getter and Setter method.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param property
	 *            UML Property
	 * @param methodDeclaration
	 *            MethodDeclaration for Getter and Setter
	 */
	public void generateGetterSetterJavadoc(AST ast, Property property,
			MethodDeclaration methodDeclaration) {
		EList<Comment> comments = property.getOwnedComments();
		for (Comment comment : comments) {
			Javadoc javadoc = ast.newJavadoc();
			generateJavadoc(ast, comment, javadoc);
			methodDeclaration.setJavadoc(javadoc);
		}
	}

	/**
	 * Generate the Interface.
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
		td.setInterface(true);
		td.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		td.setName(ast.newSimpleName(className));

		// Add inheritance
		generateClassInheritance(clazz, ast, td);
		// Add template params
		generateClassTemplateParams(clazz, ast, td);
		// Add Javadoc
		generateClassJavadoc(clazz, ast, td);

		cu.types().add(td);

		return td;
	}

	/**
	 * Generate Javadoc for Interface.
	 * 
	 * @param clazz
	 *            Classifier
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            AbstractTypeDeclaration
	 */
	public void generateClassJavadoc(Classifier clazz, AST ast,
			AbstractTypeDeclaration td) {
		EList<Comment> comments = clazz.getOwnedComments();
		for (Comment comment : comments) {
			Javadoc javadoc = ast.newJavadoc();
			generateJavadoc(ast, comment, javadoc);
			td.setJavadoc(javadoc);
		}
	}

	/**
	 * Generate the Generics for this Interface.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	public void generateClassTemplateParams(Classifier clazz, AST ast,
			TypeDeclaration td) {
		TemplateSignature templateSignature = clazz.getOwnedTemplateSignature();
		if (templateSignature != null) {
			EList<TemplateParameter> templateParameters = templateSignature
					.getParameters();
			for (TemplateParameter templateParameter : templateParameters) {
				Classifier classifier = (Classifier) templateParameter
						.getOwnedParameteredElement();
				String typeName = classifier.getLabel();
				TypeParameter typeParameter = ast.newTypeParameter();
				typeParameter.setName(ast.newSimpleName(typeName));
				td.typeParameters().add(typeParameter);
			}
		}
	}

	/**
	 * Generate the inheritance for the Interface "extends".
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	private void generateClassInheritance(Classifier clazz, AST ast,
			TypeDeclaration td) {
		EList<Generalization> generalizations = clazz.getGeneralizations();
		if (generalizations != null) {
			for (Generalization generalization : generalizations) {
				Classifier interfaceClassifier = generalization.getGeneral();
				String fullQualifiedInterfaceName = interfaceClassifier
						.getQualifiedName();
				Name name = jdtHelper.createFullQualifiedTypeAsName(ast,
						fullQualifiedInterfaceName, sourceDirectoryPackageName);
				SimpleType simpleType = ast.newSimpleType(name);
				td.superInterfaceTypes().add(simpleType);
			}
		}
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

		generatePackageJavadoc(ast, pd, PackageComment.CONTENT_1.getValue(),
				PackageComment.CONTENT_2.getValue(), " ",
				PackageComment.CONTENT_3.getValue(), " ", commentDate);

		cu.setPackage(pd);
	}

	/**
	 * Generaate the Java methods from UML.
	 * 
	 * @param clazz
	 *            the UML class
	 * @param ast
	 *            the JDT Java AST
	 * @param td
	 *            TypeDeclaration JDT
	 */
	public void generateMethods(Classifier clazz, AST ast, TypeDeclaration td) {
		// Get all methods for this clazz
		// Only for this class without inheritance
		EList<Operation> operations = clazz.getOperations();
		for (Operation operation : operations) {
			logger.log(Level.FINE, "Operation: " + operation.getName());
			MethodDeclaration md = ast.newMethodDeclaration();
			md.setName(ast.newSimpleName(operation.getName()));

			// Parameters, exclude the return parameter
			generateMethodParams(ast, td, operation, md);
			// Return type
			generateMethodReturnType(ast, td, operation, md);
			// Throws Exception
			generateMethodThrowException(ast, operation, md);
			// Generate Javadoc
			generateMethodJavadoc(ast, operation, md);
			// Generate Method template params
			generateMethodTemplateParams(ast, operation, md);
		}
	}

	/**
	 * Generate the template parameter for the given method - Generic Method.
	 * 
	 * @param ast
	 *            AST tree JDT
	 * @param operation
	 *            UML2 Operation
	 * @param md
	 *            MethodDeclaration JDT
	 */
	@SuppressWarnings("unchecked")
	public void generateMethodTemplateParams(AST ast, Operation operation,
			MethodDeclaration md) {
		TemplateSignature templateSignature = operation
				.getOwnedTemplateSignature();
		if (templateSignature != null) {
			EList<TemplateParameter> templateParameters = templateSignature
					.getParameters();
			for (TemplateParameter templateParameter : templateParameters) {
				Classifier classifier = (Classifier) templateParameter
						.getOwnedParameteredElement();
				String typeName = classifier.getLabel();
				TypeParameter typeParameter = ast.newTypeParameter();
				typeParameter.setName(ast.newSimpleName(typeName));
				md.typeParameters().add(typeParameter);
			}
		}
	}

	/**
	 * Generate Javadoc for UML Operation.
	 * 
	 * @param ast
	 *            AST tree JDT
	 * @param operation
	 *            UML Operation - Method
	 * @param md
	 *            MethodDeclaration
	 */
	public void generateMethodJavadoc(AST ast, Operation operation,
			MethodDeclaration md) {
		EList<Comment> comments = operation.getOwnedComments();
		for (Comment comment : comments) {
			Javadoc javadoc = ast.newJavadoc();
			generateJavadoc(ast, comment, javadoc);
			md.setJavadoc(javadoc);
		}
	}

	@SuppressWarnings("unchecked")
	private void generateJavadoc(AST ast, Comment comment, Javadoc javadoc) {
		String[] commentContents = parseComent(comment.getBody());
		for (String commentContent : commentContents) {
			TagElement tagElement = ast.newTagElement();
			tagElement.setTagName(commentContent);
			javadoc.tags().add(tagElement);
		}
	}

	private String[] parseComent(String body) {
		String lines[] = body.split("\\r?\\n");
		return lines;
	}

	@SuppressWarnings("unchecked")
	private void generateMethodThrowException(AST ast, Operation operation,
			MethodDeclaration md) {
		EList<Type> raisedExceptions = operation.getRaisedExceptions();
		for (Type raisedExceptionType : raisedExceptions) {
			String umlExceptionQualifiedTypeName = raisedExceptionType
					.getQualifiedName();
			String name = jdtHelper.createFullQualifiedTypeAsString(ast,
					umlExceptionQualifiedTypeName, sourceDirectoryPackageName);
			Name typeName = ast.newName(name);
			md.thrownExceptions().add(typeName);
		}
	}

	private void generateMethodReturnType(AST ast, TypeDeclaration td,
			Operation operation, MethodDeclaration md) {
		Type type = operation.getType();
		String umlTypeName = type.getName();
		String umlQualifiedTypeName = type.getQualifiedName();
		logger.log(Level.FINE, "UmlQualifiedTypeName: " + umlQualifiedTypeName
				+ " - " + "umlTypeName: " + umlTypeName);

		// Only for parameterized type
		if (dataTypeUtils.isParameterizedType(umlTypeName)) {
			Map<String, String> types = umlHelper
					.checkParameterizedTypeForTemplateParameterSubstitution(type);
			umlTypeName = types.get("umlTypeName");
			umlQualifiedTypeName = types.get("umlQualifiedTypeName");
		}

		jdtHelper.createReturnType(ast, td, md, umlTypeName,
				umlQualifiedTypeName, sourceDirectoryPackageName);
	}

	/**
	 * Generate method parameters.
	 * 
	 * @param ast
	 *            JDT AST tree
	 * @param td
	 *            JDT type declaration
	 * @param operation
	 *            UML2 operation
	 * @param md
	 *            JDT method declaration
	 */
	public void generateMethodParams(AST ast, TypeDeclaration td,
			Operation operation, MethodDeclaration md) {
		EList<Parameter> parameters = operation.getOwnedParameters();
		for (Parameter parameter : parameters) {
			if (parameter.getDirection().getValue() != ParameterDirectionKind.RETURN) {
				Type type = parameter.getType();
				String umlTypeName = type.getName();
				String umlQualifiedTypeName = type.getQualifiedName();

				// Only for parameterized type
				if (dataTypeUtils.isParameterizedType(umlTypeName)) {
					Map<String, String> types = umlHelper
							.checkParameterizedTypeForTemplateParameterSubstitution(type);
					umlTypeName = types.get("umlTypeName");
					umlQualifiedTypeName = types.get("umlQualifiedTypeName");
				}

				String umlPropertyName = StringUtils.uncapitalize(parameter
						.getName());
				logger.log(Level.FINE, "Parameter: " + parameter.getName()
						+ " - " + "Type: " + umlTypeName);
				jdtHelper.createParameterTypes(ast, td, md, umlTypeName,
						umlQualifiedTypeName, umlPropertyName,
						sourceDirectoryPackageName);
			}
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
