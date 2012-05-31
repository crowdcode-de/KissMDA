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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.StandardContext;
import de.crowdcode.kissmda.core.TransformerException;

/**
 * Unit test for simple Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class SimpleJavaTransformerTest {

	private SimpleJavaTransformer simpleJavaTransformer;
	private Context context;

	@Before
	public void setUp() {
		simpleJavaTransformer = new SimpleJavaTransformer();
		context = new StandardContext();
	}

	@Test
	public void testTransform() {
		try {
			context.setSourceModel("/model/emf/test-uml.uml");
			simpleJavaTransformer.transform(context);
		} catch (TransformerException e) {
			assertFalse(true);
		}
		assertTrue(true);
	}
}
