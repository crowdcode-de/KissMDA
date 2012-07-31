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
package de.crowdcode.kissmda.core.uml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;

/**
 * Data Type Helper class for UML.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class DataTypeUtils {

	private static Map<String, Code> primitiveTypeCodes = null;

	public Map<String, Code> getPrimitiveTypeCodes() {
		if (primitiveTypeCodes == null) {
			primitiveTypeCodes = new HashMap<String, Code>();
			primitiveTypeCodes.put("integer", PrimitiveType.INT);
			primitiveTypeCodes.put("short", PrimitiveType.SHORT);
			primitiveTypeCodes.put("double", PrimitiveType.DOUBLE);
			primitiveTypeCodes.put("long", PrimitiveType.LONG);
			primitiveTypeCodes.put("boolean", PrimitiveType.BOOLEAN);
			primitiveTypeCodes.put("byte", PrimitiveType.BYTE);
			primitiveTypeCodes.put("character", PrimitiveType.CHAR);
			primitiveTypeCodes.put("void", PrimitiveType.VOID);
		}

		return primitiveTypeCodes;
	}
}
