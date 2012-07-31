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
package de.crowdcode.kissmda.core.uml;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

/**
 * Reader and Writer class for UML2.
 * 
 * Information to this code:
 * http://wiki.eclipse.org/MDT/UML2/Introduction_to_UML2_Profiles
 * http://www.vogella.com/articles/EclipseEMFPersistence/article.html
 * http://www.java-forum.org/xml-co/90632-xmi-ueber-emf-uml2-einlesen.html
 * http://www.java-forum.org/xml-co/82142-xmi-parsen-ueber-emf.html# post511745
 * http://euml.wikispaces.com
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class ReaderWriter {

	private static final Logger logger = Logger.getLogger(ReaderWriter.class
			.getName());

	protected static final ResourceSet resourceSet = new ResourceSetImpl();

	protected void out(String output) {
		logger.log(Level.INFO, output);
	}

	protected void err(String error) {
		logger.log(Level.SEVERE, error);
	}

	public void registerSchema() {
		resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI,
				UMLPackage.eINSTANCE);
	}

	public void registerResourceFactories() {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
	}

	public void registerPathmaps(URI uri) {
		Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();

		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri
				.appendSegment("libraries").appendSegment(""));

		uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri
				.appendSegment("metamodels").appendSegment(""));

		uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri
				.appendSegment("profiles").appendSegment(""));
	}

	public void save(org.eclipse.uml2.uml.Package packageInput, URI uri) {
		Resource resource = resourceSet.createResource(uri);
		EList<EObject> contents = resource.getContents();

		contents.add(packageInput);

		for (Iterator<?> allContents = UMLUtil.getAllContents(packageInput,
				true, false); allContents.hasNext();) {

			EObject eObject = (EObject) allContents.next();

			if (eObject instanceof Element) {
				contents.addAll(((Element) eObject).getStereotypeApplications());
			}
		}

		try {
			resource.save(null);
			out("Done.");
		} catch (IOException ioe) {
			err(ioe.getMessage());
		}
	}

	public org.eclipse.uml2.uml.Package load(URI uri) {
		org.eclipse.uml2.uml.Package packageResult = null;
		try {
			Resource resource = resourceSet.getResource(uri, true);

			EList<EObject> list = resource.getContents();
			for (EObject eobject : list) {
				out(eobject.getClass().getName());
			}

			EObject eobject = resource.getContents().get(0);
			out(eobject.eAllContents().toString());

			packageResult = (org.eclipse.uml2.uml.Package) EcoreUtil
					.getObjectByType(resource.getContents(),
							UMLPackage.Literals.PACKAGE);
		} catch (WrappedException we) {
			err(we.getMessage());
		}

		return packageResult;
	}
}
