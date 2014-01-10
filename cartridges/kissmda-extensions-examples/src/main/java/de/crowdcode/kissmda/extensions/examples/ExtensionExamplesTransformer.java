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
package de.crowdcode.kissmda.extensions.examples;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.google.common.eventbus.EventBus;

import de.crowdcode.kissmda.core.Context;
import de.crowdcode.kissmda.core.Transformer;
import de.crowdcode.kissmda.core.TransformerException;

/**
 * ExtensionExamplesTransformer Transformer.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExtensionExamplesTransformer implements Transformer {

	private static final Logger logger = Logger
			.getLogger(ExtensionExamplesTransformer.class.getName());

	@Inject
	private EventBus eventBus;

	@Inject
	private TypesExtensionHandler typesExtensionHandler;

	/**
	 * Register all the handlers, listeners and extensions.
	 */
	private void registerHandlers() {
		logger.log(Level.SEVERE, "Register event handlers to event bus: "
				+ eventBus.toString());
		eventBus.register(typesExtensionHandler);
	}

	/**
	 * Start the transformation and generation. We just register the handlers to
	 * the specific events.
	 * 
	 * @param context
	 *            context object from Maven plugin
	 * @return void nothing
	 * @exception throw
	 *                TransformerException if something wrong happens
	 */
	@Override
	public void transform(Context context) throws TransformerException {
		logger.log(Level.SEVERE, "Start Extension Examples Transformer");
		// Register event handlers
		logger.log(Level.SEVERE,
				"Do nothing! Just running the event handlers extensions");
		registerHandlers();
		logger.log(Level.SEVERE, "Stop Extension Examples Transformer");
	}
}