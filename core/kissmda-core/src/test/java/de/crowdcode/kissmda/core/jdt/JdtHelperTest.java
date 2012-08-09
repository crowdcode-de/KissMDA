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
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.SimpleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for Java Helper.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class JdtHelperTest {

	private JdtHelper jdtHelper;

	@Mock
	private DataTypeUtils dataTypeUtils;

	private final AST ast = AST.newAST(AST.JLS3);;

	@Before
	public void setUp() throws Exception {
		jdtHelper = new JdtHelper();
		jdtHelper.setDataTypeUtils(dataTypeUtils);
	}

	@Test
	public void testGetClassName() {
		String name = jdtHelper
				.getClassName("de.crowdcode.kissmda.testapp.Person");
		assertEquals("Person", name);
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeNotNull() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "integer";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("Integer", tp.getName().toString());
	}

	@Test
	public void testGetAstSimpleTypeJavaTypeNull() {
		Map<String, String> javaTypes = createJavaTypes();
		when(dataTypeUtils.getJavaTypes()).thenReturn(javaTypes);

		String typeName = "org.codecrowd.Test";
		SimpleType tp = jdtHelper.getAstSimpleType(ast, typeName);

		assertEquals("org.codecrowd.Test", tp.getName().toString());
	}

	@Test
	public void testGetAstPrimitiveType() {
		Map<String, Code> primitiveTypes = createPrimitiveTypeCodes();
		when(dataTypeUtils.getPrimitiveTypeCodes()).thenReturn(primitiveTypes);

		String typeName = "Integer";
		PrimitiveType tp = jdtHelper.getAstPrimitiveType(ast, typeName);

		assertEquals("int", tp.toString());
	}

	private Map<String, String> createJavaTypes() {
		Map<String, String> javaTypes = new HashMap<String, String>();
		javaTypes.put("integer", "Integer");
		javaTypes.put("short", "Short");
		return javaTypes;
	}

	private Map<String, Code> createPrimitiveTypeCodes() {
		Map<String, Code> primitiveTypeCodes = new HashMap<String, Code>();
		primitiveTypeCodes.put("integer", PrimitiveType.INT);
		primitiveTypeCodes.put("short", PrimitiveType.SHORT);
		return primitiveTypeCodes;
	}
}
