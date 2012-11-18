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

import de.crowdcode.kissmda.core.file.FileWriterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.crowdcode.kissmda.core.file.JavaFileWriterTest;
import de.crowdcode.kissmda.core.jdt.MethodHelperTest;
import de.crowdcode.kissmda.core.uml.PackageHelperTest;
import de.crowdcode.kissmda.core.uml.ReaderWriterTest;

/**
 * Test suite.
 * 
 * @author Lofi Dewanto
 * @version 1.0.0
 * @since 1.0.0
 */
@RunWith(Suite.class)
@SuiteClasses({ FileWriterTest.class, JavaFileWriterTest.class, MethodHelperTest.class,
		PackageHelperTest.class, ReaderWriterTest.class })
public class AllTests {
}
