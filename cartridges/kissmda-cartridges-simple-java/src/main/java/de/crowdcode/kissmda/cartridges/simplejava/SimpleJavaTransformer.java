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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;
import de.crowdcode.kissmda.core.file.FileWriter;
import de.crowdcode.kissmda.core.uml.DataTypeUtils;
import de.crowdcode.kissmda.core.uml.JavaHelper;
import de.crowdcode.kissmda.core.uml.MethodHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;

/**
 * Simple Java Transformer. This generates Interfaces for all the classes from
 * the given UML model.
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
public class SimpleJavaTransformer implements Transformer {

	private static final Logger logger = Logger
			.getLogger(SimpleJavaTransformer.class.getName());

	private static final String STEREOTYPE_INTERFACE = "Interface";

	private static final String STEREOTYPE_SOURCEDIRECTORY = "SourceDirectory";

	private String sourceDirectoryPackageName;

	@Inject
	private PackageHelper packageHelper;

	@Inject
	private FileWriter fileWriter;

	@SuppressWarnings("unused")
	@Inject
	private DataTypeUtils dataTypeUtils;

	@Inject
	private MethodHelper methodHelper;

	@Inject
	private JavaHelper javaHelper;

	private Context context;

	public void setDataTypeUtils(DataTypeUtils dataTypeUtils) {
		this.dataTypeUtils = dataTypeUtils;
	}

	public void setFileWriter(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

	public void setPackageHelper(PackageHelper packageHelper) {
		this.packageHelper = packageHelper;
	}

	public void setMethodHelper(MethodHelper methodHelper) {
		this.methodHelper = methodHelper;
	}

	public void setJavaHelper(JavaHelper javaHelper) {
		this.javaHelper = javaHelper;
	}

	/**
	 * Start the transformation and generation.
	 * 
	 * @param context
	 *            context object from Maven plugin
	 * @return void nothing
	 * @exception throw
	 *                TransformerException if something wrong happens
	 */
	@Override
	public void transform(Context context) throws TransformerException {
		this.context = context;
		try {
			// Get the root package
			org.eclipse.uml2.uml.Package outPackage = getRootPackage(context);
			sourceDirectoryPackageName = "";

			// Check the stereotype of the root package
			checkStereotypeRootPackage(outPackage);

			// Get all elements with defined stereotypes
			EList<Element> list = outPackage.allOwnedElements();
			for (Element element : list) {
				EList<Stereotype> stereotypes = element.getAppliedStereotypes();
				for (Stereotype stereotype : stereotypes) {
					if (stereotype.getName().equals(STEREOTYPE_SOURCEDIRECTORY)) {
						// From this SourceDirectory we can work...
						org.eclipse.uml2.uml.Package packagez = (org.eclipse.uml2.uml.Package) element;
						sourceDirectoryPackageName = packagez.getName();
						logger.info("SourceDirectory package name: "
								+ sourceDirectoryPackageName);
					}
					if (stereotype.getName().equals(STEREOTYPE_INTERFACE)) {
						// Stereotype Interface
						Class clazz = (Class) element;
						logger.info("Class: " + clazz.getName() + " - "
								+ "Stereotype: " + stereotype.getName());
						// Generate the interface for this class
						String compilationUnit = generateInterface(clazz);
						generateClassFile(clazz, compilationUnit);
					}
				}
			}
		} catch (URISyntaxException e) {
			throw new TransformerException(e);
		} catch (IOException e) {
			throw new TransformerException(e);
		}
	}

	/**
	 * Generate the Class Interface. This is the main generation part for this
	 * SimpleJavaTransformer.
	 * 
	 * @param Class
	 *            clazz the UML class
	 * @return String the complete class with its content as a String
	 */
	public String generateInterface(Class clazz) {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		generatePackage(clazz, ast, cu);
		TypeDeclaration td = generateClass(clazz, ast, cu);
		generateMethods(clazz, ast, td);
		generateRelationships(clazz, ast, td);
		generateGettersSetters(clazz, ast, td);

		logger.log(Level.INFO, "Compilation unit: \n\n" + cu.toString());
		return cu.toString();
	}

	private void checkStereotypeRootPackage(
			org.eclipse.uml2.uml.Package outPackage) {
		EList<Stereotype> rootStereotypes = outPackage.getAppliedStereotypes();
		for (Stereotype stereotype : rootStereotypes) {
			if (stereotype.getName().equals(STEREOTYPE_SOURCEDIRECTORY)) {
				// From this SourceDirectory we can work...
				org.eclipse.uml2.uml.Package packagez = outPackage;
				sourceDirectoryPackageName = packagez.getName();
				logger.info("SourceDirectory package name: "
						+ sourceDirectoryPackageName);
			}
		}
	}

	private org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		org.eclipse.uml2.uml.Package outPackage = packageHelper
				.getRootPackage(context);
		return outPackage;
	}

	@SuppressWarnings("unchecked")
	private void generateGettersSetters(Class clazz, AST ast, TypeDeclaration td) {
		// Create getter and setter
		EList<Property> properties = clazz.getAllAttributes();
		for (Property property : properties) {
			MethodDeclaration md = ast.newMethodDeclaration();
			md.modifiers().add(
					ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			String getterName = methodHelper.getGetterName(property.getName());
			md.setName(ast.newSimpleName(getterName));
			// Return type?
			Type type = property.getType();
			String typeName = type.getQualifiedName();
			logger.info("Type: " + typeName);
			javaHelper.getType(ast, td, md, type, typeName,
					sourceDirectoryPackageName);

			// TODO Create setter method for each property
		}
	}

	private void generateRelationships(Class clazz, AST ast, TypeDeclaration td) {
		// TODO Get all the relationships of this class

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
			String typeName = type.getQualifiedName();
			logger.info("Type: " + typeName);
			javaHelper.getType(ast, td, md, type, typeName,
					sourceDirectoryPackageName);
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

	/**
	 * Create the output file on the directory.
	 * 
	 * @param clazz
	 *            UML2 class of Eclipse
	 * @param compilationUnit
	 *            compilation unit from JDT
	 * @throws IOException
	 *             input or output error on file system
	 */
	private void generateClassFile(Class clazz, String compilationUnit)
			throws IOException {
		String fullPackageName = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);
		fileWriter.createFile(context, fullPackageName, clazz.getName(),
				compilationUnit);
	}
}