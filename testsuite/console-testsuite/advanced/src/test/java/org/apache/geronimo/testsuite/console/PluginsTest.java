/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.geronimo.testsuite.console;

import org.testng.annotations.Test;
import org.apache.geronimo.testsupport.console.ConsoleTestSupport;

@Test
public class PluginsTest extends ConsoleTestSupport {
    
    @Test
    public void testListPlugins() throws Exception {
        try {
            login();

            String link = "http://geronimo-server:8080/plugin/maven-repo/";
            String actualLink = "http://localhost:8080/plugin/maven-repo/";
            	
            selenium.click("link=Plugins");
            selenium.waitForPageToLoad("30000");            
            assertTrue(selenium.isTextPresent(link));                     
            
            selenium.click("link=Add Repository");
            selenium.waitForPageToLoad("30000");
            assertTrue(selenium.isTextPresent(link));
                        
            selenium.type("newRepository", actualLink);
            selenium.click("//input[@value='Add Repository']");
            selenium.waitForPageToLoad("30000");
            
            selenium.select("repository", "label=" + actualLink);
            selenium.type("username", "system");
            selenium.type("password", "manager");            
            selenium.click("//input[@value='Show Plugins in selected repository']");
            selenium.waitForPageToLoad("30000");
            
            assertTrue(selenium.isTextPresent("Geronimo Assemblies :: Boilerplate Minimal"));
            selenium.click("link=Geronimo Assemblies :: Boilerplate Minimal");
            selenium.waitForPageToLoad("30000");
            
            assertTrue(selenium.isTextPresent("Geronimo-Versions:"));
        } finally {
            logout();
        }
    }

}
