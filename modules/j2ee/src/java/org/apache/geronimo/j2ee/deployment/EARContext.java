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
package org.apache.geronimo.j2ee.deployment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.geronimo.deployment.DeploymentContext;
import org.apache.geronimo.deployment.DeploymentException;
import org.apache.geronimo.kernel.Kernel;

/**
 * @version $Revision: 1.3 $ $Date: 2004/05/26 21:12:08 $
 */
public class EARContext extends DeploymentContext {
    private final Map ejbRefs = new HashMap();
    private final Map ejbLocalRefs = new HashMap();
    private final String j2eeDomainName;
    private final String j2eeServerName;
    private final String j2eeApplicationName;
    private final ObjectName domainObjectName;
    private final ObjectName serverObjectName;
    private final ObjectName applicationObjectName;

    public EARContext(JarOutputStream jos, URI id, URI parentID, Kernel kernel, String j2eeDomainName, String j2eeServerName, String j2eeApplicationName) throws MalformedObjectNameException, DeploymentException {
        super(jos, id, parentID, kernel);
        this.j2eeDomainName = j2eeDomainName;
        this.j2eeServerName = j2eeServerName;

        if (j2eeApplicationName == null) {
            j2eeApplicationName = "null";
        }
        this.j2eeApplicationName = j2eeApplicationName;

        Properties domainNameProps = new Properties();
        domainNameProps.put("j2eeType", "J2EEDomain");
        domainNameProps.put("name", j2eeDomainName);
        try {
            domainObjectName = new ObjectName(j2eeDomainName, domainNameProps);
        } catch (MalformedObjectNameException e) {
            throw new DeploymentException("Unable to construct J2EEDomain ObjectName", e);
        }

        Properties serverNameProps = new Properties();
        serverNameProps.put("j2eeType", "J2EEServer");
        serverNameProps.put("name", j2eeServerName);
        try {
            serverObjectName = new ObjectName(j2eeDomainName, serverNameProps);
        } catch (MalformedObjectNameException e) {
            throw new DeploymentException("Unable to construct J2EEServer ObjectName", e);
        }

        if (!j2eeApplicationName.equals("null")) {
            Properties applicationNameProps = new Properties();
            applicationNameProps.put("j2eeType", "J2EEApplication");
            applicationNameProps.put("name", j2eeApplicationName);
            applicationNameProps.put("J2EEServer", j2eeServerName);
            try {
                applicationObjectName = new ObjectName(j2eeDomainName, applicationNameProps);
            } catch (MalformedObjectNameException e) {
                throw new DeploymentException("Unable to construct J2EEApplication ObjectName", e);
            }
        } else {
            applicationObjectName = null;
        }

    }

    public String getJ2EEDomainName() {
        return j2eeDomainName;
    }

    public String getJ2EEServerName() {
        return j2eeServerName;
    }

    public String getJ2EEApplicationName() {
        return j2eeApplicationName;
    }

    public ObjectName getDomainObjectName() {
        return domainObjectName;
    }

    public ObjectName getServerObjectName() {
        return serverObjectName;
    }

    public ObjectName getApplicationObjectName() {
        return applicationObjectName;
    }

    public void addEJBRef(URI modulePath, String name, Object reference) throws DeploymentException {
        Map references = (Map) ejbRefs.get(name);
        if (references == null || references.isEmpty()) {
            references = new HashMap();
            ejbRefs.put(name, references);
        }
        addRef(modulePath, name, reference, references);
    }

    public void addEJBLocalRef(URI modulePath, String name, Object reference) throws DeploymentException {
        Map references = (Map) ejbLocalRefs.get(name);
        if (references == null || references.isEmpty()) {
            references = new HashMap();
            ejbLocalRefs.put(name, references);
        }
        addRef(modulePath, name, reference, references);
    }

    private void addRef(URI modulePath, String name, Object reference, Map references) throws DeploymentException {
        try {
            URI ejbURI = new URI(null, null, modulePath.getPath(), name);
            references.put(ejbURI, reference);
        } catch (URISyntaxException e) {
            throw new DeploymentException(e);
        }
    }

    public Object getEJBRef(URI module, String ejbLink) throws DeploymentException {
        String name = ejbLink.substring(ejbLink.lastIndexOf('#') + 1);
        return getRef(module, ejbLink, (Map) ejbRefs.get(name));
    }

    public Object getEJBLocalRef(URI module, String ejbLink) throws DeploymentException {
        String name = ejbLink.substring(ejbLink.lastIndexOf('#') + 1);
        return getRef(module, ejbLink, (Map) ejbLocalRefs.get(name));
    }

    private Object getRef(URI module, String ejbLink, Map references) throws AmbiguousEJBRefException, UnknownEJBRefException {
        if (references == null || references.isEmpty()) {
            throw new UnknownEJBRefException(ejbLink);
        }
        if (ejbLink.indexOf('#') < 0) {
            // non absolute reference
            if (references.size() != 1) {
                // check for an ejb in the current module
                Object ejbRef = references.get(module.resolve("#" + ejbLink));
                if (ejbRef == null) {
                    throw new AmbiguousEJBRefException(ejbLink);
                }
                return ejbRef;
            }
            Object ejbRef = references.values().iterator().next();
            if (ejbRef == null) {
                throw new UnknownEJBRefException(ejbLink);
            }
            return ejbRef;
        } else {
            // absolute reference  ../relative/path/Module#EJBName
            URI ejbURI = module.resolve(ejbLink).normalize();
            Object ejbRef = references.get(ejbURI);
            if (ejbRef == null) {
                throw new UnknownEJBRefException(ejbLink);
            }
            return ejbRef;
        }
    }
}
