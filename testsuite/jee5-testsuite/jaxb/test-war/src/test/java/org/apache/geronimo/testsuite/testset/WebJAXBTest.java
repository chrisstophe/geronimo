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
package org.apache.geronimo.testsuite.testset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.geronimo.testsupport.TestSupport;
import org.testng.annotations.Test;

public class WebJAXBTest extends TestSupport {

    private String baseURL = "http://localhost:8080/";

    @Test
    public void testServlet() throws Exception {
        checkReply("/JAXBServlet");
    }


    @Test
    public void testJSP() throws Exception {
        checkReply("/JAXBJSP.jsp");
    }

    private void checkReply(String address)
        throws Exception {
        String warName = System.getProperty("webAppName");
        assertNotNull(warName);
        URL url = new URL(baseURL + warName + address);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            BufferedReader reader = 
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
            assertTrue("Implementation", 
                       find(reader, "com.sun.xml.bind.v2.runtime.JAXBContextImpl"));
        } finally {
            connection.disconnect();
        }
    }

    private boolean find(BufferedReader reader, String text) 
        throws IOException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.indexOf(text) != -1) {
                return true;
            }
        }
        return false;
    }

}
