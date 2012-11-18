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
package de.crowdcode.kissmda.core.file;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.eclipse.uml2.uml.Class;
import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.core.Context;

/**
 * Unit test for FileWriter class.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class FileWriterTest {

	private FileWriter fileWriter;

	@Before
	public void setUp() throws Exception {
		fileWriter = new FileWriter();
	}

	@Test
	public void testCreateFile() throws IOException {
		String classContent = "package de.crowdcode.kissmda.testapp.components;\n "
				+ "public interface Company {\n" + "}";
		String packageName = "de.kissmda.test";
		Class clazz = mock(Class.class);
		Context context = mock(Context.class);

		when(clazz.getName()).thenReturn("Company");
		when(context.getTargetModel()).thenReturn(
				"target/generated-test-sources/java");

		fileWriter.createFile(context, packageName, clazz.getName(),
				classContent);

		assertTrue(true);
		verify(clazz).getName();
		verify(context).getTargetModel();
	}
}
