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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

	/**
	 * Create a file.
	 * 
	 * @param context
	 *            Context of the Transformer
	 * @param directory
	 *            target directory
	 * @param fileName
	 *            the target filename
	 * @param fileContent
	 *            the target content
	 * @throws IOException
	 */
	public void createFile(final Context context, final String directory, final String fileName,
			final String fileContent) throws IOException {

		String directoryToBeCreated = context.getTargetModel() + File.separator + directory;
		new File(directoryToBeCreated).mkdirs();

		// Create the class file
		Writer writer = null;
		try {
			File file = new File(directoryToBeCreated + File.separator + fileName);
			if (context.getTargetEncoding() != null) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
						context.getTargetEncoding()));
			} else {
				writer = new BufferedWriter(new java.io.FileWriter(file));
			}
			writer.write(fileContent);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
