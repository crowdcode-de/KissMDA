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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.uml.UmlHelper;

/**
 * Test Interface Generator.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class InterfaceGeneratorTest {

	private InterfaceGenerator interfaceGenerator;

	private UmlHelper umlHelper;

	@Before
	public void setUp() throws Exception {
		interfaceGenerator = new InterfaceGenerator();
		umlHelper = new UmlHelper();
		interfaceGenerator.setUmlHelper(umlHelper);
	}

	@Test
	public void testGenerateInterface() {
		assertTrue(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateAssociations() {
		Class clazz = mock(Class.class);

		when(clazz.getQualifiedName()).thenReturn(
				"Data::de::crowdcode::test::Company");

		EList<Association> associations = new UniqueEList<Association>();
		when(clazz.getAssociations()).thenReturn(associations);

		EList<Interface> interfaces = new UniqueEList<Interface>();
		when(clazz.getImplementedInterfaces()).thenReturn(interfaces);

		AST ast = AST.newAST(AST.JLS3);
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		td.modifiers().add(
				ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		td.setName(ast.newSimpleName("Company"));

		interfaceGenerator.generateAssociations(clazz, ast, td);

		assertTrue(true);
	}
}
