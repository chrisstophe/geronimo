/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.geronimo.common.net.protocol.resource;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.geronimo.common.Classes;

import org.apache.geronimo.common.net.protocol.DelegatingURLConnection;

/**
 * Provides access to system resources as a URLConnection.
 *
 * @version <tt>$Revision: 1.2 $</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResourceURLConnection
    extends DelegatingURLConnection
{
    public ResourceURLConnection(final URL url)
        throws MalformedURLException, IOException
    {
        super(url);
    }
    
    protected URL makeDelegateUrl(final URL url)
        throws MalformedURLException, IOException
    {
        String name = url.getPath();
        URL target = null;
        
        // If there is a ref, load from the ref class
        String ref = url.getRef();
        if (ref != null) {
            try {
                Class type = Classes.loadClass(ref);
                target = type.getResource(name);
            }
            catch (ClassNotFoundException ignore) {}
        }
        
        if (target == null) {
            // Then try TCL and then SCL
            ClassLoader cl = Classes.getContextClassLoader();
            target = cl.getResource(name);
            
            if (target == null) {
                cl = ClassLoader.getSystemClassLoader();
                target = cl.getResource(name);
            }
        }
        
        if (target == null) {
            throw new FileNotFoundException("Could not locate resource: " + name);
        }
        
        return target;
    }
}
