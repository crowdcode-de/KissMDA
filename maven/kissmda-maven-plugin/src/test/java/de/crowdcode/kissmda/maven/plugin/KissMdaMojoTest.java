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
package de.crowdcode.kissmda.maven.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for KissMDA mojo.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class KissMdaMojoTest {

	private KissMdaMojo kissMdaMojo;

	@Before
	public void setUp() throws Exception {
		kissMdaMojo = new KissMdaMojo();
	}

	@Test
	public void testExecute() throws MojoExecutionException {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("de.crowdcode.kissmda.maven.plugin");
		kissMdaMojo.setTransformerScanPackageNames(packageNames);
		kissMdaMojo.setModelFile("src/main/resources/model/emf/test-uml.uml");
		kissMdaMojo.execute();
	}
}
