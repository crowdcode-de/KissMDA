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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
	private final Collection<Address> addresses = new ArrayList<Address>();
	private final Set<Address> uniqueAddresses = new HashSet<Address>();
	private Company company;
	private final Collection<Company> hiddenCompanies = new ArrayList<Company>();
	private Collection<Company> oldCompanies;

	@Override
	public Integer calculateAge() {
		return 0;
	}

	@Override
	public Boolean isInRetirement() {
		return false;
	}

	@Override
	public java.util.Date[] run(byte[] content) {
		logger.info("We are running the PersonImpl which implements the generated Person interface with: "
				+ name + " - Company: " + company.getName());
		logger.info("With following addresses: " + addresses.toString());
		return null;
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
	public void addAddress(Address address) {
		addresses.add(address);
	}

	@Override
	public Collection<Address> getAddresses() {
		return addresses;
	}

	@Override
	public void changeLastAddress(Address address, Boolean isLastOne) {
		addresses.add(address);
		logger.info("This is the last one: " + isLastOne);
	}

	@Override
	public Set<Address> getNewAddresses() {
		return uniqueAddresses;
	}

	@Override
	public void addNewAddress(Address newAddress) {
		uniqueAddresses.add(newAddress);
	}

	@Override
	public String getNickname() {
		// Calculated from name
		return name.substring(0, 2);
	}

	@Override
	public Collection<Company> getHiddenCompanies() {
		return hiddenCompanies;
	}

	@Override
	public Collection<Company> getOldCompanies() {
		return oldCompanies;
	}

	@Override
	public Collection<Company> calculateOldCompanies() {
		return oldCompanies;
	}
}
