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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.Context;

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
	public void testExecuteOk() throws MojoExecutionException {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("de.crowdcode.kissmda.maven.plugin.withguice");
		MavenProject mavenProject = new MavenProject();
		mavenProject.setFile(new File("target/tmp/test"));

		kissMdaMojo.setTransformerScanPackageNames(packageNames);
		kissMdaMojo.setModelFile("src/main/resources/model/emf/test-uml.uml");
		kissMdaMojo.setProject(mavenProject);
		kissMdaMojo.execute();
		assertTrue(true);
	}

	@Test
	public void testExecuteNoGuiceModule() {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("de.crowdcode.kissmda.maven.plugin.noguice");
		MavenProject mavenProject = new MavenProject();
		mavenProject.setFile(new File("target/tmp/test"));

		kissMdaMojo.setTransformerScanPackageNames(packageNames);
		kissMdaMojo.setModelFile("src/main/resources/model/emf/test-uml.uml");
		kissMdaMojo.setProject(mavenProject);
		try {
			kissMdaMojo.execute();
		} catch (MojoExecutionException e) {
			assertEquals(e.getMessage(), KissMdaMojo.ERROR_GUICE_NOT_FOUND);
		}
	}

	@Test
	public void testExecuteWithGuiceModuleButInTheDifferentPackage() {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("de.crowdcode.kissmda.maven.plugin.differentpackage");
		MavenProject mavenProject = new MavenProject();
		mavenProject.setFile(new File("target/tmp/test"));

		kissMdaMojo.setTransformerScanPackageNames(packageNames);
		kissMdaMojo.setModelFile("src/main/resources/model/emf/test-uml.uml");
		kissMdaMojo.setProject(mavenProject);
		try {
			kissMdaMojo.execute();
		} catch (MojoExecutionException e) {
			assertEquals(e.getMessage(),
					KissMdaMojo.ERROR_GUICE_SAME_PACKAGE_NOT_FOUND);
		}
	}

	@Test
	public void testContextObject() throws MojoExecutionException {
		List<String> packageNames = new ArrayList<String>();
		packageNames.add("de.crowdcode.kissmda.maven.plugin.withguice");
		MavenProject mavenProject = new MavenProject();
		mavenProject.setFile(new File("target/tmp/test"));

		kissMdaMojo.setTransformerScanPackageNames(packageNames);
		kissMdaMojo.setModelFile("src/main/resources/model/emf/test-uml.uml");
		kissMdaMojo
				.setGeneratedSourcesTargetDirectory("target/generated-sources/kissmda");
		kissMdaMojo.setProject(mavenProject);
		kissMdaMojo.execute();

		Context context = kissMdaMojo.getContext();

		assertEquals(context.getSourceModel(),
				"target\\tmp/src/main/resources/model/emf/test-uml.uml");
		assertEquals(context.getTargetModel(),
				"target\\tmp/target/generated-sources/kissmda");
	}
}
