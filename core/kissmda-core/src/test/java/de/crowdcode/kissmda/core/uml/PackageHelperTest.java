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
	public void testGetFullPackageName() {
		Class clazz = mock(Class.class);
		when(clazz.getQualifiedName()).thenReturn(
				"Data::de::crowdcode::test::Company");
		String sourceDirectoryPackageName = "Data";

		String result = packageHelper.getFullPackageName(clazz,
				sourceDirectoryPackageName);

		verify(clazz, atLeastOnce()).getQualifiedName();
		assertEquals("de.crowdcode.test.Company", result);
	}
}
