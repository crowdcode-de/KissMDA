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
package de.crowdcode.kissmda.examples.apptest;

import java.util.Collection;
import java.util.logging.Logger;

import de.crowdcode.kissmda.testapp.Address;
import de.crowdcode.kissmda.testapp.Person;
import de.crowdcode.kissmda.testapp.components.Company;

/**
 * Person implementation.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class PersonImpl implements Person {

	private static final Logger logger = Logger.getLogger(PersonImpl.class
			.getName());
	private String name;
	private Collection<Address> addresses;
	private Company company;

	@Override
	public Integer calculateAge() {
		return 0;
	}

	@Override
	public Boolean isInRetirement() {
		return false;
	}

	@Override
	public void run() {
		logger.info("We are running the PersonImpl which implements the generated Person interface with: "
				+ name + " - Company: " + company.getName());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Company getCompany() {
		return company;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public void addAddresses(Address address) {
		addresses.add(address);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getAddresses() {
		return addresses;
	}
}
