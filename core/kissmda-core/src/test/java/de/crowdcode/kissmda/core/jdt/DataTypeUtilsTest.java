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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.eventbus.EventBus;

/**
 * Unit test for Data Type Utils.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class DataTypeUtilsTest {

	@InjectMocks
	private DataTypeUtils dataTypeUtils;

	@Mock
	private EventBus eventBus;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsPrimitiveType1() {
		boolean isPrimitiveType = dataTypeUtils.isPrimitiveType("integer");
		assertTrue(isPrimitiveType);
	}

	@Test
	public void testIsPrimitiveType2() {
		boolean isPrimitiveType = dataTypeUtils.isPrimitiveType("int");
		assertTrue(isPrimitiveType);
	}

	@Test
	public void testIsJavaType1() {
		boolean isJavaType = dataTypeUtils.isJavaType("Integer");
		assertTrue(isJavaType);
	}

	@Test
	public void testIsJavaType2() {
		boolean isJavaType = dataTypeUtils.isJavaType("integer");
		assertFalse(isJavaType);
	}

	@Test
	public void testIsArrayType1() {
		boolean isArrayType = dataTypeUtils.isArrayType("datatype.byte[]");
		assertTrue(isArrayType);
	}

	@Test
	public void testIsArrayType2() {
		boolean isArrayType = dataTypeUtils.isArrayType("datatype.char[]");
		assertTrue(isArrayType);
	}

	@Test
	public void testIsParameterizedType1() {
		boolean isParameterizedType = dataTypeUtils
				.isParameterizedType("datatype.Collection<String>");
		assertTrue(isParameterizedType);
	}

	@Test
	public void testIsParameterizedType2() {
		boolean isParameterizedType = dataTypeUtils
				.isParameterizedType("List<String>");
		assertTrue(isParameterizedType);
	}

	@Test
	public void testIsNotParameterizedType() {
		boolean isParameterizedType = dataTypeUtils
				.isParameterizedType("datatype.Collection<String");
		assertFalse(isParameterizedType);
	}
}
