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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import de.crowdcode.kissmda.core.Context;

/**
 * File writer for UML class.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class FileWriter {

	public void createFile(final Context context, final String packageName,
			final String className, final String classContent)
			throws IOException {
		// Create the package directories from context information
		String packageNameToBeCreated = packageName.replace(".", "/");
		String directoryToBeCreated = context.getTargetModel() + "/"
				+ packageNameToBeCreated;
		new File(directoryToBeCreated).mkdirs();

		// Create the class file
		Writer writer = null;
		try {
			File file = new File(directoryToBeCreated + "/" + className
					+ ".java");
			writer = new BufferedWriter(new java.io.FileWriter(file));
			writer.write(classContent);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
