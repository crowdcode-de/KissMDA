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
 * Interface for Context.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 */
public interface Context {

	/**
	 * Get the source model.
	 * 
	 * @return source as String
	 */
	String getSourceModel();

	/**
	 * Get the target model.
	 * 
	 * @return target as String
	 */
	String getTargetModel();

	/**
	 * Get the target encoding.
	 * 
	 * @return targetEncoding as String
	 */
	String getTargetEncoding();

	/**
	 * Get property file and its location.
	 *
	 * @return propertyFile as String
	 */
	String getPropertyFile();

	/**
	 * Set source model.
	 * 
	 * @param sourceModel
	 */
	void setSourceModel(String sourceModel);

	/**
	 * Set target model.
	 * 
	 * @param targetModel
	 */
	void setTargetModel(String targetModel);

	/**
	 * Set target encoding.
	 * 
	 * @param targetEncoding
	 */
	void setTargetEncoding(String targetEncoding);

	/**
	 * Set property file location.
	 *
	 * @param propertyFile
	 */
	void setPropertyFile(String propertyFile);
}
