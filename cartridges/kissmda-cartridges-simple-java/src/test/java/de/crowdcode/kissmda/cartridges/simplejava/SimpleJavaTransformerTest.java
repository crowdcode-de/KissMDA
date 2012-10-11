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

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.StandardContext;
import de.crowdcode.kissmda.core.TransformerException;
import de.crowdcode.kissmda.core.file.FileWriter;
import de.crowdcode.kissmda.core.jdt.DataTypeUtils;
import de.crowdcode.kissmda.core.jdt.JdtHelper;
import de.crowdcode.kissmda.core.jdt.MethodHelper;
import de.crowdcode.kissmda.core.uml.PackageHelper;
import de.crowdcode.kissmda.core.uml.ReaderWriter;

/**
 * Unit test for simple Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class SimpleJavaTransformerTest {

	private static final Logger logger = Logger
			.getLogger(SimpleJavaTransformerTest.class.getName());
	private SimpleJavaTransformer simpleJavaTransformer;
	private Context context;
	private PackageHelper packageHelper;
	private MethodHelper methodHelper;
	private ReaderWriter readerWriter;
	private FileWriter fileWriter;
	private DataTypeUtils dataTypeUtils;
	private JdtHelper jdtHelper;
	private InterfaceGenerator interfaceGenerator;

	@Before
	public void setUp() {
		readerWriter = new ReaderWriter();
		packageHelper = new PackageHelper();
		methodHelper = new MethodHelper();
		fileWriter = new FileWriter();
		dataTypeUtils = new DataTypeUtils();
		jdtHelper = new JdtHelper();
		jdtHelper.setDataTypeUtils(dataTypeUtils);
		jdtHelper.setPackageHelper(packageHelper);
		packageHelper.setReaderWriter(readerWriter);
		interfaceGenerator = new InterfaceGenerator();
		interfaceGenerator.setJdtHelper(jdtHelper);
		interfaceGenerator.setMethodHelper(methodHelper);
		interfaceGenerator.setPackageHelper(packageHelper);
		simpleJavaTransformer = new SimpleJavaTransformer();
		simpleJavaTransformer.setPackageHelper(packageHelper);
		simpleJavaTransformer.setFileWriter(fileWriter);
		simpleJavaTransformer.setInterfaceGenerator(interfaceGenerator);
		context = new StandardContext();
	}

	@Test
	public void testTransform() {
		try {
			String thisPath = this.getClass().getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			logger.info("Path: " + thisPath);
			context.setSourceModel(thisPath + "model/emf/test-uml.uml");
			context.setTargetModel("target/generated-sources/java-transformer");
			simpleJavaTransformer.transform(context);
		} catch (TransformerException e) {
			assertFalse(true);
		}
		assertTrue(true);
	}
}
