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
package de.crowdcode.kissmda.core;

import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;

/**
 * Unit test for simple Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class ReaderWriterTest {

	@Test
	public void testTestUml() throws URISyntaxException {
		ReaderWriter app = new ReaderWriter();
		String uriString = this.getClass()
				.getResource("/model/emf/test-uml.uml").toURI().toString();
		app.out(uriString);
		URI uri = URI.createURI(uriString);
		app.registerSchema();
		app.registerResourceFactories();
		app.registerPathmaps(uri);

		org.eclipse.uml2.uml.Package outPackage = app.load(uri);
		app.out(outPackage.getName());
		app.out(outPackage.getPackagedElements().toString());

		assertTrue(true);
	}
}
