package de.crowdcode.kissmda.examples.apptest;

import java.util.ArrayList;
import java.util.Collection;

import de.crowdcode.kissmda.testapp.CompanyAttribute;

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
