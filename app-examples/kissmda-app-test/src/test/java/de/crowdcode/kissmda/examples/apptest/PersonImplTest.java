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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.crowdcode.kissmda.examples.apptest.components.CompanyImpl;
import de.crowdcode.kissmda.testapp.Address;
import de.crowdcode.kissmda.testapp.AddressType;
import de.crowdcode.kissmda.testapp.CompanyAttribute;
import de.crowdcode.kissmda.testapp.Person;
import de.crowdcode.kissmda.testapp.components.Company;

/**
 * Unit test for PersonImpl class.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class PersonImplTest {

	private Person person;

	@Before
	public void setUp() {
		person = new PersonImpl();
		person.setName("Lofi");
	}

	@Test
	public void testApp() {
		Company company = new CompanyImpl();
		company.setName("CrowdCode");

		CompanyAttribute<String, Integer> companyAttribute = new CompanyAttributeImpl();
		companyAttribute.setName("Lofi");
		companyAttribute.add("Test Element", 23);
		company.setCompanyAttribute(companyAttribute);

		Address address1 = new AddressImpl();
		address1.setStreet("Jakarta");
		address1.setAddressType(AddressType.HOME);
		address1.setPerson(person);
		Address address2 = new AddressImpl();
		address2.setStreet("Cologne");
		address2.setStreet("Solingen");
		address2.setAddressType(AddressType.OFFICE);
		address2.setPerson(person);

		person.setCompany(company);
		person.addAddresses(address1);
		person.addAddresses(address2);

		byte[] content = null;
		Date[] dateArray = person.run(content);

		assertNull(dateArray);
		assertEquals(0, person.calculateAge().intValue());
		assertEquals(false, person.isInRetirement());
		assertEquals(2, person.getAddresses().size());
		assertEquals("Lofi - Test Element - 23", person.getCompany()
				.getCompanyAttribute().getName());
	}

	@Test
	public void testOperationWithParameters() {
		Address addressLast = new AddressImpl();
		addressLast.setStreet("Muenster");
		addressLast.setPerson(person);

		person.changeLastAddress(addressLast, true);
	}
}
