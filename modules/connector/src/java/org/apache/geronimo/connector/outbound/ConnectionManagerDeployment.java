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

package org.apache.geronimo.connector.outbound;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionFactory;

import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTracker;
import org.apache.geronimo.gbean.GAttributeInfo;
import org.apache.geronimo.gbean.GBean;
import org.apache.geronimo.gbean.GBeanContext;
import org.apache.geronimo.gbean.GBeanInfo;
import org.apache.geronimo.gbean.GBeanInfoFactory;
import org.apache.geronimo.gbean.GConstructorInfo;
import org.apache.geronimo.gbean.GReferenceInfo;
import org.apache.geronimo.gbean.GOperationInfo;
import org.apache.geronimo.kernel.KernelMBean;
import org.apache.geronimo.security.bridge.RealmBridge;

/**
 * ConnectionManagerDeployment is an mbean that sets up a ProxyConnectionManager
 * and connection manager stack according to the policies described in the attributes.
 * It's used by deserialized copies of the proxy to get a reference to the actual stack.
 *
 * @version $Revision: 1.4 $ $Date: 2004/02/25 09:57:10 $
 * */
public class ConnectionManagerDeployment implements ConnectionManagerFactory, GBean {

    public static final GBeanInfo GBEAN_INFO;

    private final static String MBEAN_SERVER_DELEGATE =
            "JMImplementation:type=MBeanServerDelegate";

    /**
     * The original Serializable ProxyConnectionManager that provides the
     * ConnectionManager implementation.
     */
    private ProxyConnectionManager cm;

    //connection manager configuration choices
    private boolean useConnectionRequestInfo;
    private boolean useSubject;
    private boolean useTransactionCaching;
    private boolean useLocalTransactions;
    private boolean useTransactions;
    private int maxSize;
    private int blockingTimeout;
    /**
     * Identifying string used by unshareable resource detection
     */
    private String name;
    //dependencies
    private RealmBridge realmBridge;
    private ConnectionTracker connectionTracker;
    private KernelMBean kernel;

    //default constructor for use as endpoint
    public ConnectionManagerDeployment() {
    }

    public ConnectionManagerDeployment(boolean useConnectionRequestInfo,
            boolean useSubject,
            boolean useTransactionCaching,
            boolean useLocalTransactions,
            boolean useTransactions,
            int maxSize,
            int blockingTimeout,
            String name,
            RealmBridge realmBridge,
            ConnectionTracker connectionTracker,
            KernelMBean kernel) {
        this.useConnectionRequestInfo = useConnectionRequestInfo;
        this.useLocalTransactions = useLocalTransactions;
        this.useSubject = useSubject;
        this.useTransactionCaching = useTransactionCaching;
        this.useTransactions = useTransactions;
        this.maxSize = maxSize;
        this.blockingTimeout = blockingTimeout;
        this.realmBridge = realmBridge;
        this.name = name;
        this.connectionTracker = connectionTracker;
        this.kernel = kernel;
    }

    public void setGBeanContext(GBeanContext context) {
    }

    public void doStart() {
        MBeanServer mbeanServer = null;
        if (kernel != null) {
            mbeanServer = kernel.getMBeanServer();
        } else {
            throw new IllegalStateException("Neither kernel nor context is set, but you're trying to start");
        }

        String agentID;
        try {
            ObjectName name = ObjectName.getInstance(MBEAN_SERVER_DELEGATE);
            agentID = (String) mbeanServer.getAttribute(name, "MBeanServerId");
        } catch (Exception e) {
            throw new RuntimeException("Problem getting agentID from MBeanServerDelegate", e);
        }

        ObjectName connectionManagerName = null;
        try {
            connectionManagerName = ObjectName.getInstance("geronimo.management:j2eeType=ConnectionManager,name=" + name);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("could not construct an object name for ConnectionManagerDeployment mbean", e);
        }
        setUpConnectionManager(agentID, connectionManagerName);

    }

    /**
     * Order of constructed interceptors:
     *
     * ConnectionTrackingInterceptor (connectionTracker != null)
     * ConnectionHandleInterceptor
     * TransactionCachingInterceptor (useTransactions & useTransactionCaching)
     * TransactionEnlistingInterceptor (useTransactions)
     * SubjectInterceptor (realmBridge != null)
     * SinglePoolConnectionInterceptor or MultiPoolConnectionInterceptor
     * LocalXAResourceInsertionInterceptor or XAResourceInsertionInterceptor (useTransactions (&localTransactions))
     * MCFConnectionInterceptor
     */
    private void setUpConnectionManager(String agentID, ObjectName connectionManagerName) {
        //check for consistency between attributes
        if (realmBridge == null) {
            assert useSubject == false: "To use Subject in pooling, you need a SecurityDomain";
        }

        //Set up the interceptor stack
        ConnectionInterceptor stack = new MCFConnectionInterceptor(this);
        if (useTransactions) {
            if (useLocalTransactions) {
                stack = new LocalXAResourceInsertionInterceptor(stack);
            } else {
                stack = new XAResourceInsertionInterceptor(stack);
            }
        }
        if (useSubject || useConnectionRequestInfo) {
            stack = new MultiPoolConnectionInterceptor(
                    stack,
                    maxSize,
                    blockingTimeout,
                    useSubject,
                    useConnectionRequestInfo);
        } else {
            stack = new SinglePoolConnectionInterceptor(
                    stack,
                    null,
                    null,
                    maxSize,
                    blockingTimeout);
        }
        if (realmBridge != null) {
            stack = new SubjectInterceptor(stack, realmBridge);
        }
        if (useTransactions) {
            stack = new TransactionEnlistingInterceptor(stack);
            if (useTransactionCaching) {
                stack = new TransactionCachingInterceptor(stack, connectionTracker);
            }
        }
        stack = new ConnectionHandleInterceptor(stack);
        if (connectionTracker != null) {
            stack = new ConnectionTrackingInterceptor(
                    stack,
                    name,
                    connectionTracker,
                    realmBridge);
        }

        cm = new ProxyConnectionManager(agentID, connectionManagerName, stack);
    }

    public void doStop() {
        cm = null;
        realmBridge = null;
        connectionTracker = null;
    }

    public void doFail() {
    }

    public ConnectionInterceptor getStack() {
        return cm.getStack();
    }


    public Object createConnectionFactory(ManagedConnectionFactory mcf) throws ResourceException {
        return mcf.createConnectionFactory(cm);
    }

    public int getBlockingTimeout() {
        return blockingTimeout;
    }

    public void setBlockingTimeout(int blockingTimeout) {
        this.blockingTimeout = blockingTimeout;
    }

    public ConnectionTracker getConnectionTracker() {
        return connectionTracker;
    }

    public void setConnectionTracker(ConnectionTracker connectionTracker) {
        this.connectionTracker = connectionTracker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public RealmBridge getRealmBridge() {
        return realmBridge;
    }

    public void setRealmBridge(RealmBridge realmBridge) {
        this.realmBridge = realmBridge;
    }

    public boolean isUseConnectionRequestInfo() {
        return useConnectionRequestInfo;
    }

    public void setUseConnectionRequestInfo(boolean useConnectionRequestInfo) {
        this.useConnectionRequestInfo = useConnectionRequestInfo;
    }

    //TODO determine this from [geronimo-]ra.xml transaction attribute.
    public boolean isUseTransactions() {
        return useTransactions;
    }

    public void setUseTransactions(boolean useTransactions) {
        this.useTransactions = useTransactions;
    }

    public boolean isUseLocalTransactions() {
        return useLocalTransactions;
    }

    public void setUseLocalTransactions(boolean useLocalTransactions) {
        this.useLocalTransactions = useLocalTransactions;
    }

    //Even if realmBridge is present, if reauthentication is supported, you might not want to use
    //the subject as pooling crieteria.
    public boolean isUseSubject() {
        return useSubject;
    }

    public void setUseSubject(boolean useSubject) {
        this.useSubject = useSubject;
    }

    public boolean isUseTransactionCaching() {
        return useTransactionCaching;
    }

    public void setUseTransactionCaching(boolean useTransactionCaching) {
        this.useTransactionCaching = useTransactionCaching;
    }

    public KernelMBean getKernel() {
        return kernel;
    }

    public void setKernel(KernelMBean kernel) {
        this.kernel = kernel;
    }

    static {
        GBeanInfoFactory infoFactory = new GBeanInfoFactory(ConnectionManagerDeployment.class.getName());

        infoFactory.addAttribute(new GAttributeInfo("Name", true));
        infoFactory.addAttribute(new GAttributeInfo("BlockingTimeout", true));
        infoFactory.addAttribute(new GAttributeInfo("MaxSize", true));
        infoFactory.addAttribute(new GAttributeInfo("UseTransactions", true));
        infoFactory.addAttribute(new GAttributeInfo("UseLocalTransactions", true));
        infoFactory.addAttribute(new GAttributeInfo("UseTransactionCaching", true));
        infoFactory.addAttribute(new GAttributeInfo("UseConnectionRequestInfo", true));
        infoFactory.addAttribute(new GAttributeInfo("UseSubject", true));

        infoFactory.addOperation(new GOperationInfo("getStack"));
        infoFactory.addOperation(new GOperationInfo("createConnectionFactory", new String[]{ManagedConnectionFactory.class.getName()}));

        infoFactory.addReference(new GReferenceInfo("ConnectionTracker", ConnectionTracker.class.getName()));
        infoFactory.addReference(new GReferenceInfo("RealmBridge", RealmBridge.class.getName()));
        infoFactory.addReference(new GReferenceInfo("Kernel", KernelMBean.class.getName()));

        infoFactory.setConstructor(new GConstructorInfo(
                new String[]{"UseConnectionRequestInfo", "UseSubject", "UseTransactionCaching", "UseLocalTransactions", "UseTransactions",
                             "MaxSize", "BlockingTimeout", "Name", "RealmBridge", "ConnectionTracker", "Kernel"},
                new Class[]{Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE,
                            Integer.TYPE, Integer.TYPE, String.class, RealmBridge.class, ConnectionTracker.class, KernelMBean.class}));
        GBEAN_INFO = infoFactory.getBeanInfo();
    }

    public static GBeanInfo getGBeanInfo() {
        return GBEAN_INFO;
    }

}
