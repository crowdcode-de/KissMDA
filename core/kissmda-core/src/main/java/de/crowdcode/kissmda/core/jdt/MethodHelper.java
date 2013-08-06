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

/**
 * Method Helper class for Java language.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class MethodHelper {

	/**
	 * Get the getter name.
	 * 
	 * @param name
	 *            input name
	 * @return getter name
	 */
	public String getGetterName(String name) {
		String getter = "get" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return getter;
	}

	/**
	 * Get the setter name.
	 * 
	 * @param name
	 *            input name
	 * @return setter name
	 */
	public String getSetterName(String name) {
		String setter = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return setter;
	}

	/**
	 * Get the adder name.
	 * 
	 * @param name
	 *            input name
	 * @return adder name
	 */
	public String getAdderName(String name) {
		String setter = "add" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		return setter;
	}

	/**
	 * Get the singular name of the plural name.
	 * 
	 * @param pluralName
	 *            input plural name
	 * @return singular name
	 */
	public String getSingularName(String pluralName) {
		// Remove the last two chars
		String singular = pluralName.substring(0, pluralName.length() - 2);
		return singular;
	}
}
