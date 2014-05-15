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
import java.util.SortedSet;
import java.util.TreeSet;

import de.crowdcode.kissmda.testapp.Address;
import de.crowdcode.kissmda.testapp.AddressComplexType;
import de.crowdcode.kissmda.testapp.AddressSimpleType;
import de.crowdcode.kissmda.testapp.AddressType;
import de.crowdcode.kissmda.testapp.Person;

/**
 * Address implementation.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class AddressImpl implements Address {

	private Address oldAddress;
	private String street;
	private Person person;
	private final SortedSet<Person> sortedPersons = new TreeSet<Person>();
	private AddressType addressType;
	private boolean old;
	private AddressSimpleType addressSimpleType;
	private AddressComplexType addressComplexType;

	@Override
	public Address getOldAddress() {
		return oldAddress;
	}

	@Override
	public String getStreet() {
		return street;
	}

	@Override
	public void setStreet(String street) {
		if (this.street == null) {
			this.street = street;
		} else {
			oldAddress = new AddressImpl();
			Person person = new PersonImpl();
			person.setName("Old Lofi");
			oldAddress.setPerson(person);
			oldAddress.setStreet(this.street);
			this.street = street;
		}
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "AddressImpl [oldAddress=" + oldAddress + ", street=" + street
				+ ", person=" + person.getName() + "]";
	}

	@Override
	public AddressType getAddressType() {
		return addressType;
	}

	@Override
	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getOldAddresses() {
		return null;
	}

	@Override
	public Collection<String> getNewAddresses() {
		return null;
	}

	@Override
	public SortedSet<Person> getNewPersons() {
		return sortedPersons;
	}

	@Override
	public void addNewPerson(Person newPerson) {
		sortedPersons.add(newPerson);
	}

	@Override
	public boolean isOld() {
		return old;
	}

	@Override
	public void setOld(boolean old) {
		this.old = old;
	}

	@Override
	public AddressSimpleType getAddressSimpleType() {
		return addressSimpleType;
	}

	@Override
	public void setAddressSimpleType(AddressSimpleType addressSimpleType) {
		this.addressSimpleType = addressSimpleType;
	}

	@Override
	public AddressComplexType getAddressComplexType() {
		return addressComplexType;
	}

	@Override
	public void setAddressComplexType(AddressComplexType addressComplexType) {
		this.addressComplexType = addressComplexType;
	}
}
