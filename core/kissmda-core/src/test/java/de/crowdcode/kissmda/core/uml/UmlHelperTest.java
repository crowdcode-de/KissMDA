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
package de.crowdcode.kissmda.core.uml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameterSubstitution;
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

/**
 * Unit test for UML2 Helper.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class UmlHelperTest {

	@InjectMocks
	private UmlHelper umlHelper;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataTypeUtils dataTypeUtils;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private Type type;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetTemplateParameterSubstitutionNotPrimitiveType() {
		Classifier clazzifier = prepareMocks();

		when(dataTypeUtils.isPrimitiveType(Mockito.anyString())).thenReturn(
				false);
		when(clazzifier.getName()).thenReturn("Data::datatype::Company");
		when(clazzifier.getQualifiedName()).thenReturn(
				"Data::datatype::de::test::Company");

		List<String> results = umlHelper.getTemplateParameterSubstitution(type);

		assertEquals("Data::datatype::de::test::Company", results.get(0));
	}

	@Test
	public void testGetTemplateParameterSubstitutionNotPrimitiveTypeButJavaType() {
		Classifier clazzifier = prepareMocks();

		when(dataTypeUtils.isPrimitiveType(Mockito.anyString())).thenReturn(
				false);
		when(dataTypeUtils.isJavaType(Mockito.anyString())).thenReturn(true);

		when(clazzifier.getName()).thenReturn("Data::datatype::String");
		when(clazzifier.getQualifiedName()).thenReturn(
				"Data::datatype::de::test::String");

		List<String> results = umlHelper.getTemplateParameterSubstitution(type);

		assertEquals("Data::datatype::String", results.get(0));
	}

	@Test
	public void testGetTemplateParameterSubstitutionPrimitiveType() {
		Classifier clazzifier = prepareMocks();

		when(dataTypeUtils.isPrimitiveType(Mockito.anyString())).thenReturn(
				true);
		when(clazzifier.getName()).thenReturn("Data::datatype::Integer");
		when(clazzifier.getQualifiedName()).thenReturn(
				"Data::datatype::de::test::Integer");

		List<String> results = umlHelper.getTemplateParameterSubstitution(type);

		assertEquals("Data::datatype::Integer", results.get(0));
	}

	@Test
	public void testCheckParameterizedTypeForTemplateParameterSubstitution() {
		Classifier clazzifier = prepareMocks();

		when(type.getName()).thenReturn("Data::datatype::Collection<Integer>");
		when(type.getQualifiedName()).thenReturn(
				"Data::datatype::Collection<Integer>");

		when(dataTypeUtils.isPrimitiveType(Mockito.anyString())).thenReturn(
				true);
		when(clazzifier.getName()).thenReturn("Integer");
		when(clazzifier.getQualifiedName()).thenReturn(
				"Data::datatype::de::test::Integer");

		Map<String, String> results = umlHelper
				.checkParameterizedTypeForTemplateParameterSubstitution(type);

		String umlTypeName = results.get("umlTypeName");
		String umlQualifiedTypeName = results.get("umlQualifiedTypeName");
		assertEquals("Data::datatype::Collection<Integer>", umlTypeName);
		assertEquals("Data::datatype::Collection<Integer>",
				umlQualifiedTypeName);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCheckParameterizedTypeForTemplateParameterSubstitutionWithNoSubstitution() {
		prepareMocks();

		Iterator<Element> iteratorElement = mock(Iterator.class);
		when(iteratorElement.hasNext()).thenReturn(false);

		when(type.getName()).thenReturn("Data::datatype::Collection<Person>");
		when(type.getQualifiedName()).thenReturn(
				"Data::datatype::Collection<de::test::Person>");

		Map<String, String> results = umlHelper
				.checkParameterizedTypeForTemplateParameterSubstitution(type);

		String umlTypeName = results.get("umlTypeName");
		String umlQualifiedTypeName = results.get("umlQualifiedTypeName");
		assertEquals("Data::datatype::Collection<Person>", umlTypeName);
		assertEquals("Data::datatype::Collection<de::test::Person>",
				umlQualifiedTypeName);
	}

	@SuppressWarnings("unchecked")
	private Classifier prepareMocks() {
		// Mocks
		EList<Element> elements = mock(EList.class,
				Answers.RETURNS_DEEP_STUBS.get());

		TemplateBinding templateBinding = mock(TemplateBinding.class,
				Answers.RETURNS_DEEP_STUBS.get());
		Iterator<Element> iteratorElement = mock(Iterator.class);

		EList<TemplateParameterSubstitution> templateParameterSubstitutions = mock(
				EList.class, Answers.RETURNS_DEEP_STUBS.get());
		Iterator<TemplateParameterSubstitution> iteratorTemplateParameterSubstitution = mock(Iterator.class);
		TemplateParameterSubstitution templateParameterSubstitution = mock(
				TemplateParameterSubstitution.class,
				Answers.RETURNS_DEEP_STUBS.get());

		Classifier clazzifier = mock(Classifier.class);

		// Prepare...
		when(type.allOwnedElements()).thenReturn(elements);
		when(elements.iterator()).thenReturn(iteratorElement);
		when(iteratorElement.hasNext()).thenReturn(true, false);
		when(iteratorElement.next()).thenReturn(templateBinding);

		when(templateBinding.getParameterSubstitutions()).thenReturn(
				templateParameterSubstitutions);
		when(templateParameterSubstitutions.iterator()).thenReturn(
				iteratorTemplateParameterSubstitution);
		when(iteratorTemplateParameterSubstitution.hasNext()).thenReturn(true,
				false);
		when(iteratorTemplateParameterSubstitution.next()).thenReturn(
				templateParameterSubstitution);
		when(templateParameterSubstitution.getActual()).thenReturn(clazzifier);
		return clazzifier;
	}
}
