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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.crowdcode.kissmda.core.Context;

/**
 * Unit test for FileWriter class.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class JavaFileWriterTest {

	@InjectMocks
	private JavaFileWriter fileJavaWriter;

	@Mock
	private FileWriter fileWriter;

	@Mock
	private Context context;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateFile() throws IOException {
		String classContent = "Some arbitrary content";

		when(context.getTargetModel()).thenReturn(
				"target/generated-test-sources/java");

		fileJavaWriter.createJavaFile(context, "de.crowdcode.kissmda.test",
				"Company", classContent);

		verify(fileWriter).createFile(
				context,
				"de" + File.separator + "crowdcode" + File.separator
						+ "kissmda" + File.separator + "test", "Company.java",
				classContent);
	}
}
