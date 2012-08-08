package de.crowdcode.kissmda.examples.apptest.components;

import java.util.Date;

import de.crowdcode.kissmda.testapp.components.Company;

public class CompanyImpl implements Company {

	private String name;
	private Double value;
	private Date created;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Double getValue() {
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
}
