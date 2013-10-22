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

import javax.inject.Inject;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

import com.google.common.eventbus.EventBus;

import de.crowdcode.kissmda.core.jdt.event.JavaTypeCodesCreatedEvent;
import de.crowdcode.kissmda.core.jdt.event.PrimitiveTypeCodesCreatedEvent;

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

	@Inject
	private EventBus eventBus;

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

		// Publish an event to the bus
		eventBus.post(new PrimitiveTypeCodesCreatedEvent(primitiveTypeCodes));

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
			javaTypes.put("Object", "Object");
			javaTypes.put("Integer", "Integer");
			javaTypes.put("Short", "Short");
			javaTypes.put("Double", "Double");
			javaTypes.put("Long", "Long");
			javaTypes.put("Float", "Float");
			javaTypes.put("Boolean", "Boolean");
			javaTypes.put("String", "String");
			javaTypes.put("Byte", "Byte");
			javaTypes.put("Character", "Character");
			javaTypes.put("Date", "java.util.Date");
			javaTypes.put("date", "java.util.Date");
			javaTypes.put("Calendar", "java.util.Calendar");
			javaTypes.put("Collection", "java.util.Collection");
			javaTypes.put("List", "java.util.List");
			javaTypes.put("Set", "java.util.Set");
			javaTypes.put("Queue", "java.util.Queue");
			javaTypes.put("SortedSet", "java.util.SortedSet");
			javaTypes.put("Map", "java.util.Map");
			javaTypes.put("SortedMap", "java.util.SortedMap");
			javaTypes
					.put("BlockingQueue", "java.util.concurrent.BlockingQueue");
			javaTypes.put("Blob", "java.sql.Blob");
			javaTypes.put("Clob", "java.sql.Clob");
			javaTypes.put("Timestamp", "java.sql.Timestamp");
			javaTypes.put("File", "java.io.File");
			javaTypes.put("Guid", "java.util.UUID");
			javaTypes.put("TreeNode", "javax.swing.tree.TreeNode");
			javaTypes.put("URI", "java.net.URI");
			javaTypes.put("URL", "java.net.URL");
		}

		// Publish an event to the bus
		eventBus.post(new JavaTypeCodesCreatedEvent(javaTypes));

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
	 * Check whether the type name is a Java type.
	 * 
	 * @param typeName
	 *            to be checked
	 * @return true or false
	 */
	public boolean isJavaType(String typeName) {
		Map<String, String> javaTypes = getJavaTypes();
		String javaTypeCodes = javaTypes.get(typeName);
		if (javaTypeCodes != null) {
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
