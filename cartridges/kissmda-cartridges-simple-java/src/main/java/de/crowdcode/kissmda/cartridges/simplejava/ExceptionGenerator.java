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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.Type;

import de.crowdcode.kissmda.core.TransformerException;
import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.jdt.MethodHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Generate Exception from UML class.
 * 
 * <p>
 * Most important helper classes from kissmda-core which are used in this
 * Transformer: PackageHelper, MethodHelper, JavaHelper, FileWriter and
 * DataTypeUtils
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
	private MethodHelper methodHelper;

	@Inject
	private JdtHelper jdtHelper;

	@Inject
	private PackageHelper packageHelper;

	private String sourceDirectoryPackageName;

	private boolean isCheckedException = true;

	public void setCheckedException(boolean isCheckedException) {
		this.isCheckedException = isCheckedException;
	}

	public void setMethodHelper(MethodHelper methodHelper) {
		this.methodHelper = methodHelper;
	}

	public void setJdtHelper(JdtHelper javaHelper) {
		this.jdtHelper = javaHelper;
	}

	public void setPackageHelper(PackageHelper packageHelper) {
		this.packageHelper = packageHelper;
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

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateMethods(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
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

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateMethods(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu.toString();
	}

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

	@SuppressWarnings("unchecked")
	private void generateClassTemplateParams(Classifier clazz, AST ast,
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

	public void generatePackage(Classifier clazz, AST ast, CompilationUnit cu) {
		PackageDeclaration p1 = ast.newPackageDeclaration();
		String fullPackageName = getFullPackageName(clazz);
		p1.setName(ast.newName(fullPackageName));
		cu.setPackage(p1);
	}

	@SuppressWarnings("unchecked")
	private void generateMethods(Classifier clazz, AST ast, TypeDeclaration td) {
		// Get all methods for this clazz
		// Only for this class without inheritance
		EList<Operation> operations = clazz.getOperations();
		for (Operation operation : operations) {
			MethodDeclaration md = ast.newMethodDeclaration();
			md.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			md.setName(ast.newSimpleName(operation.getName()));
			// Parameters, exclude the return parameter
			EList<Parameter> parameters = operation.getOwnedParameters();
			for (Parameter parameter : parameters) {
				if (parameter.getDirection().getValue() != ParameterDirectionKind.RETURN) {
					Type type = parameter.getType();
					String umlTypeName = type.getName();
					String umlQualifiedTypeName = type.getQualifiedName();
					String umlPropertyName = StringUtils.uncapitalize(parameter
							.getName());
					logger.info("Parameter: " + parameter.getName() + " - "
							+ "Type: " + umlTypeName);
					jdtHelper.createParameterTypes(ast, td, md, umlTypeName,
							umlQualifiedTypeName, umlPropertyName,
							sourceDirectoryPackageName);
				}
			}
			// Return type?
			Type type = operation.getType();
			String umlTypeName = type.getName();
			String umlQualifiedTypeName = type.getQualifiedName();
			logger.info("UmlQualifiedTypeName: " + umlQualifiedTypeName + " - "
					+ "umlTypeName: " + umlTypeName);
			jdtHelper.createReturnType(ast, td, md, umlTypeName,
					umlQualifiedTypeName, sourceDirectoryPackageName);
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
