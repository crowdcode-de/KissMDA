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
package de.crowdcode.kissmda.core.jdt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

/**
 * Data Type Helper class for Java language with JDT.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class DataTypeUtils {

	private static Map<String, Code> primitiveTypeCodes = null;

	private static Map<String, String> javaTypes = null;

	/**
	 * Get the primitive type codes.
	 * 
	 * @return Map of key and code
	 */
	public Map<String, Code> getPrimitiveTypeCodes() {
		if (primitiveTypeCodes == null) {
			primitiveTypeCodes = new HashMap<String, Code>();
			primitiveTypeCodes.put("integer", PrimitiveType.INT);
			primitiveTypeCodes.put("int", PrimitiveType.INT);
			primitiveTypeCodes.put("short", PrimitiveType.SHORT);
			primitiveTypeCodes.put("double", PrimitiveType.DOUBLE);
			primitiveTypeCodes.put("long", PrimitiveType.LONG);
			primitiveTypeCodes.put("boolean", PrimitiveType.BOOLEAN);
			primitiveTypeCodes.put("byte", PrimitiveType.BYTE);
			primitiveTypeCodes.put("character", PrimitiveType.CHAR);
			primitiveTypeCodes.put("char", PrimitiveType.CHAR);
			primitiveTypeCodes.put("float", PrimitiveType.FLOAT);
			primitiveTypeCodes.put("void", PrimitiveType.VOID);
		}

		return primitiveTypeCodes;
	}

	/**
	 * Get the Java types.
	 * 
	 * @return Map of type key and Java type
	 */
	public Map<String, String> getJavaTypes() {
		if (javaTypes == null) {
			javaTypes = new HashMap<String, String>();
			javaTypes.put("object", "Object");
			javaTypes.put("integer", "Integer");
			javaTypes.put("short", "Short");
			javaTypes.put("double", "Double");
			javaTypes.put("long", "Long");
			javaTypes.put("float", "Float");
			javaTypes.put("boolean", "Boolean");
			javaTypes.put("string", "String");
			javaTypes.put("byte", "Byte");
			javaTypes.put("character", "Character");
			javaTypes.put("date", "java.util.Date");
			javaTypes.put("collection", "java.util.Collection");
			javaTypes.put("list", "java.util.List");
			javaTypes.put("set", "java.util.Set");
			javaTypes.put("queue", "java.util.Queue");
			javaTypes.put("sortedset", "java.util.SortedSet");
			javaTypes.put("map", "java.util.Map");
			javaTypes.put("sortedmap", "java.util.SortedMap");
			javaTypes.put("sortedset", "java.util.SortedSet");
			javaTypes
					.put("blockingqueue", "java.util.concurrent.BlockingQueue");
			javaTypes.put("blob", "java.sql.Blob");
			javaTypes.put("clob", "java.sql.Clob");
			javaTypes.put("timestamp", "java.sql.Timestamp");
			javaTypes.put("file", "java.io.File");
			javaTypes.put("guid", "java.util.UUID");
			javaTypes.put("treenode", "javax.swing.tree.TreeNode");
			javaTypes.put("uri", "java.net.URI");
			javaTypes.put("url", "java.net.URL");
		}

		return javaTypes;
	}

	/**
	 * Check whether the type name is a primitive type.
	 * 
	 * @param typeName
	 *            to be checked
	 * @return true or false
	 */
	public boolean isPrimitiveType(String typeName) {
		Map<String, Code> primitiveTypeCodes = getPrimitiveTypeCodes();
		Code primitiveTypeCode = primitiveTypeCodes.get(typeName);
		if (primitiveTypeCode != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checked whether the type name is an array type.
	 * 
	 * @param typeName
	 *            to be checked
	 * @return true or false
	 */
	public boolean isArrayType(String typeName) {
		if (typeName.contains("[]")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checked whether the type name is a parameterized type.
	 * 
	 * @param typeName
	 *            to be checked
	 * @return true or false
	 */
	public boolean isParameterizedType(String typeName) {
		if (typeName.contains("<") && typeName.contains(">")) {
			return true;
		} else {
			return false;
		}
	}
}
