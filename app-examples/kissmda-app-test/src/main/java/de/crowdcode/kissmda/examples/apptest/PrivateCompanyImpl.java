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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

import de.crowdcode.kissmda.examples.apptest.components.CompanyImpl;
import de.crowdcode.kissmda.testapp.PrivateCompany;
import de.crowdcode.kissmda.testapp.components.Company;
import de.crowdcode.kissmda.testapp.components.CompanyAttribute;

public class PrivateCompanyImpl extends CompanyImpl implements PrivateCompany {

	private String owner;

    private LocalTime createdTime;

    private LocalDateTime createdComplete;

	private Collection<Company> companies;

	private SortedSet<CompanyAttribute<String, Integer>> companyAttributes;

    @Override
	public Integer calculateRevenue() {
		return 1000;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public Collection<Company> getCompanies() {
		return companies;
	}

	@Override
	public void addCompany(Company company) {
		this.companies.add(company);
	}

	@Override
	public SortedSet<CompanyAttribute<String, Integer>> getCompanyAttributes() {
		return companyAttributes;
	}

	@Override
	public void addCompanyAttribute(
			CompanyAttribute<String, Integer> companyAttribute) {
		this.companyAttributes.add(companyAttribute);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SortedSet<CompanyAttribute<String, Integer>> calculateCompanies(
			Collection<CompanyAttribute> companyAttributes,
			Set<CompanyAttribute<String, Integer>> companyCompleteAttributes) {
		return this.companyAttributes;
	}

	@Override
	public LocalTime getCreatedTime() {
		return createdTime;
	}

	@Override
	public void setCreatedTime(LocalTime createdTime) {
		this.createdTime = createdTime;
	}

    @Override
    public LocalDateTime getCreatedCompleted() {
        return createdComplete;
    }

    @Override
    public void setCreatedCompleted(LocalDateTime createdCompleted) {
        this.createdComplete = createdCompleted;
    }
}
