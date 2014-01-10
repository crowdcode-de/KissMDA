/*
x * Licensed to the Apache Software Foundation (ASF) under one
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
package de.crowdcode.kissmda.cartridges.simplejava.event;

import org.eclipse.jdt.core.dom.CompilationUnit;

import de.crowdcode.kissmda.core.jdt.event.GenericEvent;

/**
 * Event published before the class file generated. You can use this event to do
 * everything like formatting code before the code created as a class file.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 2.0.0
 */
public class BeforeClassFileGeneratedEvent extends GenericEvent {

	private final CompilationUnit compilationUnit;

	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public BeforeClassFileGeneratedEvent(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
}
