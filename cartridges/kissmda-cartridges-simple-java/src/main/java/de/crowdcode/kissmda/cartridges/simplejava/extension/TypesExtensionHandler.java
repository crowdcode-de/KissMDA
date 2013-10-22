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
package de.crowdcode.kissmda.cartridges.simplejava.extension;

import java.util.Map;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import de.crowdcode.kissmda.core.jdt.event.JavaTypeCodesCreatedEvent;
import de.crowdcode.kissmda.core.jdt.event.PrimitiveTypeCodesCreatedEvent;

/**
 * Extension for the Java types.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.4.0
 */
public class TypesExtensionHandler {

	private static final Logger logger = Logger
			.getLogger(TypesExtensionHandler.class.getName());

	private Boolean isAlreadyRunOncePrimitive = false;

	private Boolean isAlreadyRunOnceJava = false;

	@Subscribe
	public void onPrimitiveTypeCodesCreated(PrimitiveTypeCodesCreatedEvent event) {
		if (!isAlreadyRunOncePrimitive) {
			logger.info("Primitive types can be extended here... "
					+ event.getPrimitiveTypeCodes());
		}
		isAlreadyRunOncePrimitive = true;
	}

	@Subscribe
	public void onJavaTypeCodesCreated(JavaTypeCodesCreatedEvent event) {
		if (!isAlreadyRunOnceJava) {
			logger.info("Java types can be extended here... before... "
					+ event.getJavaTypeCodes());
			Map<String, String> javaTypeCodes = event.getJavaTypeCodes();
			javaTypeCodes.put("test", "de.test.Test");
			logger.info("Java types can be extended here... after... "
					+ event.getJavaTypeCodes());
		}
		isAlreadyRunOnceJava = true;
	}
}
