package de.crowdcode.kissmda.examples.apptest.components;

import java.util.Date;

import de.crowdcode.kissmda.testapp.CompanyAttribute;
import de.crowdcode.kissmda.testapp.components.Company;

public class CompanyImpl implements Company {

	private String name;
	private double value;
	private Date created;
	private CompanyAttribute<String, Integer> companyAttribute;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public CompanyAttribute<String, Integer> getCompanyAttribute() {
		return companyAttribute;
	}

	@Override
	public void setCompanyAttribute(
			CompanyAttribute<String, Integer> companyAttribute) {
		this.companyAttribute = companyAttribute;
	}
}
