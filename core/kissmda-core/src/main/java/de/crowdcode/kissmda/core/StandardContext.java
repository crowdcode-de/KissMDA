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
package de.crowdcode.kissmda.core;

/**
 * Standard context implementation.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public class StandardContext implements Context {
	private String sourceModel;
	private String targetModel;
	private String targetEncoding;

	/**
	 * {@link Context #setSourceModel(String)}
	 */
	@Override
	public String getSourceModel() {
		return sourceModel;
	}

	/**
	 * {@link Context #getTargetModel()}
	 */
	@Override
	public String getTargetModel() {
		return targetModel;
	}

	/**
	 * {@link Context #getTargetEncoding()}
	 */
	@Override
	public String getTargetEncoding() {
		return targetEncoding;
	}

	/**
	 * {@link Context #setSourceModel(String)}
	 */
	@Override
	public void setSourceModel(String sourceModel) {
		this.sourceModel = sourceModel;
	}

	/**
	 * {@link Context #setTargetModel(String)}
	 */
	@Override
	public void setTargetModel(String targetModel) {
		this.targetModel = targetModel;
	}

	/**
	 * {@link Context #setTargetEncoding(String)}
	 */
	@Override
	public void setTargetEncoding(String targetEncoding) {
		this.targetEncoding = targetEncoding;
	}
}
