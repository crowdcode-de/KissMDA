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
package de.crowdcode.kissmda.core.jdt;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Method Helper.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class MethodHelperTest {

	private MethodHelper methodHelper;

	@Before
	public void setUp() throws Exception {
		methodHelper = new MethodHelper();
	}

	@Test
	public void testGetGetterName() {
		String result = methodHelper.getGetterName("testfield");
		assertEquals("getTestfield", result);
	}

	@Test
	public void testGetSetterName() {
		String result = methodHelper.getSetterName("testfield");
		assertEquals("setTestfield", result);
	}

	@Test
	public void testAdderName() {
		String result = methodHelper.getAdderName("testfield");
		assertEquals("addTestfield", result);

		result = methodHelper.getAdderName("testfields");
		assertEquals("addTestfields", result);
	}

	@Test
	public void testGetSingularName() {
		String result1 = methodHelper.getSingularName("addresses");
		assertEquals("address", result1);

		String result2 = methodHelper.getSingularName("persons");
		assertEquals("perso", result2);

		String result3 = methodHelper.getSingularName("companies");
		assertEquals("compani", result3);
	}
}
