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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.uml2.uml.Class;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Package Helper.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class PackageHelperTest {

	private PackageHelper packageHelper;

	@Before
	public void setUp() throws Exception {
		packageHelper = new PackageHelper();
	}

	@Test
	public void testGetFullPackageNameWithClass() {
		Class clazz = mock(Class.class);
		when(clazz.getQualifiedName()).thenReturn(
				"Data::de::crowdcode::test::Company");
		String sourceDirectoryPackageName = "Data";

		String result = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);

		verify(clazz, atLeastOnce()).getQualifiedName();
		assertEquals("de.crowdcode.test.Company", result);
	}

	@Test
	public void testGetFullPackageNameWithString() {
		String sourceDirectoryPackageName = "Data";
		String umlPackageNameWithClass = "Data::de.crowdcode.test.Company";

		String result = packageHelper.getFullPackageName(
				umlPackageNameWithClass, sourceDirectoryPackageName);

		assertEquals("de.crowdcode.test.Company", result);
	}

	@Test
	public void testGetFullPackageNameWithStringNonData() {
		String sourceDirectoryPackageName = "MyModel";
		String umlPackageNameWithClass = "MyModel::de.crowdcode.test.Company";

		String result = packageHelper.getFullPackageName(
				umlPackageNameWithClass, sourceDirectoryPackageName);

		assertEquals("de.crowdcode.test.Company", result);
	}

	@Test
	public void testGetFullPackageNameWithStringAlreadyOk() {
		String sourceDirectoryPackageName = "Data";
		String umlPackageNameWithClass = "de.crowdcode.test.Company";

		String result = packageHelper.getFullPackageName(
				umlPackageNameWithClass, sourceDirectoryPackageName);

		assertEquals("de.crowdcode.test.Company", result);
	}

	@Test
	public void testGetFullPackageNameWithStringAlreadyOkWithoutSourceDirectoryPackageName() {
		String sourceDirectoryPackageName = "";
		String umlPackageNameWithClass = "de.crowdcode.test.Company";

		String result = packageHelper.getFullPackageName(
				umlPackageNameWithClass, sourceDirectoryPackageName);

		assertEquals("de.crowdcode.test.Company", result);
	}

	@Test
	public void testGetFullPackageNameWithGenerics() {
		String sourceDirectoryPackageName = "Data";
		String umlPackageNameWithClass = "T";

		String result = packageHelper.getFullPackageName(
				umlPackageNameWithClass, sourceDirectoryPackageName);

		assertEquals("T", result);
	}

	@Test
	public void testRemoveUmlPrefixes1() {
		String fullQualifiedName = "UMLPrimitiveTypes::Boolean";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Boolean", result);
	}

	@Test
	public void testRemoveUmlPrefixes2() {
		String fullQualifiedName = "Data::de.crowdcode.test.Company";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Data::de.crowdcode.test.Company", result);
	}

	@Test
	public void testRemoveUmlPrefixes9() {
		String fullQualifiedName = "MyModel::de.crowdcode.test.Company";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("MyModel::de.crowdcode.test.Company", result);
	}

	@Test
	public void testRemoveUmlPrefixes3() {
		String fullQualifiedName = "MagicDraw Profile::datatypes::void";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("void", result);
	}

	@Test
	public void testRemoveUmlPrefixes4() {
		String fullQualifiedName = "JavaPrimitiveTypes::double";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("double", result);
	}

	@Test
	public void testRemoveUmlPrefixes5() {
		String fullQualifiedName = "Validation Profile::OCL Library::Collection";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Collection", result);
	}

	@Test
	public void testRemoveUmlPrefixes6() {
		String fullQualifiedName = "Data::datatype::byte[]";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("byte[]", result);
	}

	@Test
	public void testRemoveUmlPrefixes7() {
		String fullQualifiedName = "Data::datatype-bindings::Collection<String>";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Collection<String>", result);
	}

	@Test
	public void testRemoveUmlPrefixes8() {
		String fullQualifiedName = "T";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("T", result);
	}

	@Test
	public void testRemoveUmlPrefixes10() {
		String fullQualifiedName = "MyModel::datatype-bindings::Collection<String>";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Collection<String>", result);
	}

	@Test
	public void testRemoveUmlPrefixes11() {
		String fullQualifiedName = "JavaPrimitiveTypes::Boolean";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Boolean", result);
	}

	@Test
	public void testRemoveUmlPrefixes12() {
		String fullQualifiedName = "UMLPrimitiveTypes::Integer";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Integer", result);
	}

	@Test
	public void testRemoveUmlPrefixes13() {
		String fullQualifiedName = "MagicDraw Profile::datatypes::Boolean";
		String result = packageHelper.removeUmlPrefixes(fullQualifiedName);
		assertEquals("Boolean", result);
	}
}
