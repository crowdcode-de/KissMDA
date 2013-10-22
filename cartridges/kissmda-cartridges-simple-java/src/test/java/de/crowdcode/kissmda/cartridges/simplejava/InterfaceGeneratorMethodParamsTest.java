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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.crowdcode.kissmda.core.jdt.DataTypeUtils;
import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.uml.UmlHelper;

/**
 * Test Interface Generator for method parameters.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class InterfaceGeneratorMethodParamsTest {

	@InjectMocks
	private final InterfaceGenerator interfaceGenerator = new InterfaceGenerator();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private UmlHelper umlHelper;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataTypeUtils dataTypeUtils;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private JdtHelper jdtHelper;

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateMethodParamsWithParameter() {
		AST ast = AST.newAST(AST.JLS3);
		ast.newCompilationUnit();
		TypeDeclaration td = ast.newTypeDeclaration();
		td.setInterface(true);
		Modifier modifier = ast
				.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		td.modifiers().add(modifier);
		td.setName(ast.newSimpleName("Company"));
		MethodDeclaration md = ast.newMethodDeclaration();
		md.setName(ast.newSimpleName("calculateAge"));

		Operation operation = mock(Operation.class,
				Answers.RETURNS_DEEP_STUBS.get());
		EList<Parameter> params = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Parameter> paramIterator = mock(Iterator.class);
		Parameter parameter = mock(Parameter.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Type type = mock(Type.class, Answers.RETURNS_DEEP_STUBS.get());

		Map<String, String> types = new HashMap<String, String>();
		types.put("umlTypeName", "Collection<de::test::Company>");
		types.put("umlQualifiedTypeName",
				"Data::datatype::Collection<de::test::Company>");

		when(operation.getOwnedParameters()).thenReturn(params);
		when(params.iterator()).thenReturn(paramIterator);
		when(paramIterator.hasNext()).thenReturn(true, false);
		when(paramIterator.next()).thenReturn(parameter);
		when(parameter.getDirection()).thenReturn(
				ParameterDirectionKind.get(ParameterDirectionKind.IN));
		when(parameter.getType()).thenReturn(type);
		when(parameter.getName()).thenReturn("companies");
		when(type.getName()).thenReturn("Collection<Company>");
		when(type.getQualifiedName()).thenReturn(
				"Data::datatype::Collection<Company>");
		when(dataTypeUtils.isParameterizedType(Mockito.anyString()))
				.thenReturn(true);
		when(
				umlHelper
						.checkParameterizedTypeForTemplateParameterSubstitution(type))
				.thenReturn(types);
		doNothing().when(jdtHelper).createParameterTypes(ast, td, md,
				"Collection<de::test::Company>",
				"Data::datatype::Collection<de::test::Company>", "companies",
				null);

		interfaceGenerator.generateMethodParams(ast, td, operation, md);

		verify(jdtHelper, times(1)).createParameterTypes(ast, td, md,
				"Collection<de::test::Company>",
				"Data::datatype::Collection<de::test::Company>", "companies",
				null);
	}
}
