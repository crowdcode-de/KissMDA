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
package de.crowdcode.kissmda.examples.apptest.components;

import java.util.ArrayList;
import java.util.Collection;

import de.crowdcode.kissmda.testapp.components.CompanyAttribute;

public class CompanyAttributeImpl implements CompanyAttribute<String, Integer> {

	private String name;
	private final Collection<String> elementStringCollection = new ArrayList<String>();
	private final Collection<Integer> elementIntegerCollection = new ArrayList<Integer>();

	@Override
	public void add(String element1, Integer element2) {
		elementStringCollection.add(element1);
		elementIntegerCollection.add(element2);
	}

	@Override
	public java.lang.String getName() {
		return name + " - " + elementStringCollection.iterator().next() + " - "
				+ elementIntegerCollection.iterator().next();
	}

	@Override
	public void setName(java.lang.String name) {
		this.name = name;
	}
}
