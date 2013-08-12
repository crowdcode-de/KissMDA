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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * Java Code Formatter. Part of this code is taken from Maven Java Formatter
 * Plugin: https://code.google.com/p/maven-java-formatter-plugin.
 * 
 * @author Lofi Dewanto
 * @version 1.1.0
 * @since 1.1.0
 */
public class JavaCodeFormatter {

	private static final String JAVA_VERSION = "1.5";

	private static final Logger logger = Logger
			.getLogger(JavaCodeFormatter.class.getName());

	private static final String LINE_ENDING_AUTO = "AUTO";
	private static final String LINE_ENDING_KEEP = "KEEP";
	private static final String LINE_ENDING_LF = "LF";
	private static final String LINE_ENDING_CRLF = "CRLF";
	private static final String LINE_ENDING_CR = "CR";

	private static final String LINE_ENDING_LF_CHAR = "\n";
	private static final String LINE_ENDING_CRLF_CHARS = "\r\n";
	private static final String LINE_ENDING_CR_CHAR = "\r";

	private final String lineEnding = LINE_ENDING_AUTO;

	private CodeFormatter codeFormatter;

	private void createCodeFormatter() {
		Map<String, String> options = getFormattingOptions();
		codeFormatter = ToolFactory.createCodeFormatter(options);
	}

	private Map<String, String> getFormattingOptions() {
		Map<String, String> options = new HashMap<String, String>();
		options.put(JavaCore.COMPILER_SOURCE, JAVA_VERSION);
		options.put(JavaCore.COMPILER_COMPLIANCE, JAVA_VERSION);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JAVA_VERSION);
		return options;
	}

	/**
	 * Format the given code.
	 * 
	 * @param compilationUnit
	 *            Compilation Unit as unformatted code
	 * @return formatted code
	 */
	public String format(String compilationUnit) {
		createCodeFormatter();
		String lineSeparator = getLineEnding(compilationUnit);

		TextEdit te = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT
				+ CodeFormatter.F_INCLUDE_COMMENTS, compilationUnit, 0,
				compilationUnit.length(), 0, lineSeparator);
		IDocument doc = new Document(compilationUnit);
		try {
			te.apply(doc);
		} catch (MalformedTreeException e) {
			logger.log(Level.SEVERE, e.getStackTrace().toString());
		} catch (BadLocationException e) {
			logger.log(Level.SEVERE, e.getStackTrace().toString());
		}
		String formattedCode = doc.get();
		return formattedCode;
	}

	/**
	 * Returns the lineEnding parameter as characters when the value is known
	 * (LF, CRLF, CR) or can be determined from the file text (KEEP). Otherwise
	 * null is returned.
	 * 
	 * @return String
	 */
	private String getLineEnding(String fileDataString) {
		String lineEnd = null;
		if (LINE_ENDING_KEEP.equals(lineEnding)) {
			lineEnd = determineLineEnding(fileDataString);
		} else if (LINE_ENDING_LF.equals(lineEnding)) {
			lineEnd = LINE_ENDING_LF_CHAR;
		} else if (LINE_ENDING_CRLF.equals(lineEnding)) {
			lineEnd = LINE_ENDING_CRLF_CHARS;
		} else if (LINE_ENDING_CR.equals(lineEnding)) {
			lineEnd = LINE_ENDING_CR_CHAR;
		}
		return lineEnd;
	}

	/**
	 * Returns the most occurring line-ending characters in the file text or
	 * null if no line-ending occurs the most.
	 * 
	 * @return String
	 */
	private String determineLineEnding(String fileDataString) {
		int lfCount = 0;
		int crCount = 0;
		int crlfCount = 0;

		for (int i = 0; i < fileDataString.length(); i++) {
			char c = fileDataString.charAt(i);
			if (c == '\r') {
				if ((i + 1) < fileDataString.length()
						&& fileDataString.charAt(i + 1) == '\n') {
					crlfCount++;
					i++;
				} else {
					crCount++;
				}
			} else if (c == '\n') {
				lfCount++;
			}
		}
		if (lfCount > crCount && lfCount > crlfCount) {
			return LINE_ENDING_LF_CHAR;
		} else if (crlfCount > lfCount && crlfCount > crCount) {
			return LINE_ENDING_CRLF_CHARS;
		} else if (crCount > lfCount && crCount > crlfCount) {
			return LINE_ENDING_CR_CHAR;
		}
		return null;
	}
}
