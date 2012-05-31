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
package de.crowdcode.kissmda.cartridges.simplejava;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Element;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.ReaderWriter;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;

/**
 * Simple Java Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class SimpleJavaTransformer implements Transformer {
	private static final Logger logger = Logger
			.getLogger(SimpleJavaTransformer.class.getName());

	@Override
	public void transform(Context context) throws TransformerException {
		try {
			ReaderWriter app = new ReaderWriter();
			String uriString = this.getClass()
					.getResource(context.getSourceModel()).toURI().toString();
			URI uri = URI.createURI(uriString);
			app.registerSchema();
			app.registerResourceFactories();
			app.registerPathmaps(uri);

			org.eclipse.uml2.uml.Package outPackage = app.load(uri);

			logger.log(Level.INFO, outPackage.getName());
			logger.log(Level.INFO, outPackage.getPackagedElements().toString());
			logger.log(Level.INFO, outPackage.getProfileApplications()
					.toString());
			logger.log(Level.INFO, outPackage.getAllAppliedProfiles()
					.toString());

			EList<Element> list = outPackage.allOwnedElements();
			int index = 1;
			for (Element element : list) {
				logger.log(Level.INFO, index + " - " + element.toString());
				index++;
			}
		} catch (URISyntaxException e) {
			throw new TransformerException(e);
		}
	}
}
