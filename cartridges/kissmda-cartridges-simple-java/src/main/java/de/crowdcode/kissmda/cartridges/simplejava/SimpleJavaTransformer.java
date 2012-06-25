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

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Stereotype;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.ReaderWriter;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;

/**
 * Simple Java Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class SimpleJavaTransformer implements Transformer {

	private static final Logger logger = Logger
			.getLogger(SimpleJavaTransformer.class.getName());

	private static final String STEREOTYPE_INTERFACE = "Interface";

	private static final String STEREOTYPE_SOURCEDIRECTORY = "SourceDirectory";

	private String sourceDirectoryPackageName;

	@Override
	public void transform(Context context) throws TransformerException {
		try {
			// Get the root package
			org.eclipse.uml2.uml.Package outPackage = getRootPackage(context);
			sourceDirectoryPackageName = "";

			// Check the stereotype of the root package
			EList<Stereotype> rootStereotypes = outPackage
					.getAppliedStereotypes();
			for (Stereotype stereotype : rootStereotypes) {
				if (stereotype.getName().equals(STEREOTYPE_SOURCEDIRECTORY)) {
					// From this SourceDirectory we can work...
					org.eclipse.uml2.uml.Package packagez = outPackage;
					sourceDirectoryPackageName = packagez.getName();
					logger.info("SourceDirectory package name: "
							+ sourceDirectoryPackageName);
				}
			}

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
						generateInterface(clazz);
					}
				}
			}
		} catch (URISyntaxException e) {
			throw new TransformerException(e);
		}
	}

	private org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		ReaderWriter app = new ReaderWriter();
		String uriString = this.getClass()
				.getResource(context.getSourceModel()).toURI().toString();
		URI uri = URI.createURI(uriString);
		app.registerSchema();
		app.registerResourceFactories();
		app.registerPathmaps(uri);

		org.eclipse.uml2.uml.Package outPackage = app.load(uri);

		return outPackage;
	}

	@SuppressWarnings("unchecked")
	public void generateInterface(Class clazz) {
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		PackageDeclaration p1 = ast.newPackageDeclaration();
		String fullPackageName = getFullPackageName(clazz);

		p1.setName(ast.newName(fullPackageName));
		cu.setPackage(p1);

		ImportDeclaration id = ast.newImportDeclaration();
		id.setName(ast.newName(new String[] { "java", "util", "Set" }));
		cu.imports().add(id);

		TypeDeclaration td = ast.newTypeDeclaration();
		td.setName(ast.newSimpleName("Foo"));
		TypeParameter tp = ast.newTypeParameter();
		tp.setName(ast.newSimpleName("X"));
		td.typeParameters().add(tp);
		cu.types().add(td);

		MethodDeclaration md = ast.newMethodDeclaration();
		td.bodyDeclarations().add(md);

		Block block = ast.newBlock();
		md.setBody(block);

		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("x"));

		ExpressionStatement e = ast.newExpressionStatement(mi);
		block.statements().add(e);

		logger.log(Level.INFO, cu.toString());
	}

	private String getFullPackageName(Class clazz) {
		// Get package until the beginning of SourceDirectory
		logger.info("Qualified name: " + clazz.getQualifiedName());
		// Remove the sourceDirectoryPackageName
		String toBeDeleted = sourceDirectoryPackageName + "::";
		String fullPackageName = clazz.getQualifiedName().replaceFirst(
				toBeDeleted, "");
		// Remove class name
		toBeDeleted = "::" + clazz.getName();
		fullPackageName = fullPackageName.replaceFirst(toBeDeleted, "");
		// Change :: to .
		fullPackageName = fullPackageName.replaceAll("::", ".");
		logger.info("Real package name: " + fullPackageName);
		return fullPackageName;
	}
}
