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

import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.ReaderWriter;

/**
 * Package Helper class for UML.
 * 
 * @author Lofi Dewanto
 * @since 1.0.0
 * @version 1.0.0
 */
public class PackageHelper {

	private static final Logger logger = Logger.getLogger(PackageHelper.class
			.getName());

	public org.eclipse.uml2.uml.Package getRootPackage(Context context)
			throws URISyntaxException {
		ReaderWriter app = new ReaderWriter();
		logger.info("Get from following sourceModel: "
				+ context.getSourceModel());
		String uriString = "file:/" + context.getSourceModel();
		logger.info("Get from following URI: " + uriString);
		URI uri = URI.createURI(uriString);
		app.registerSchema();
		app.registerResourceFactories();
		app.registerPathmaps(uri);

		org.eclipse.uml2.uml.Package outPackage = app.load(uri);

		return outPackage;
	}
}
