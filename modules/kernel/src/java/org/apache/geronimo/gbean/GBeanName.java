/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.geronimo.gbean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;


/**
 * Class that represents the name for a GBean.
 * A name is comprised of a domain combined with one or more properties.
 * The domain is a fixed base name, properties qualify that as necessary.
 * Two names are equal if they have the same name and the same properties.
 * The String representation of a name can be written as domain:key1=value1,key2=value2,...
 * Values are case sensitive, spaces are significant and there is no escaping mechanism;
 * this is intended to be fast rather than allow lax syntax.
 *
 * @version $Rev$ $Date$
 */
public final class GBeanName implements Serializable {
    private static final long serialVersionUID = 8571821054715922993L;

    /**
     * Original name preserved; used for toString and Serialized form
     */
    private final String name;
    private final transient String domain;
    private final transient HashMap props;
    private final transient int hashCode;

    /**
     * Construct a GBeanName by combining a domain with explicit properties.
     * The string representation of this name is generated by combining the properties in iteration order.
     *
     * @param domain the domain
     * @param props  the properties used to qualify this name; a Map<String,String>
     */
    public GBeanName(String domain, Map props) {
        if (domain == null) {
            throw new IllegalArgumentException("domain is null");
        } else if (props == null) {
            throw new IllegalArgumentException("props is null");
        } else if (props.isEmpty()) {
            throw new IllegalArgumentException("props is empty");
        }
        this.domain = domain;
        this.props = new HashMap(props);
        this.name = buildName(domain, props);
        this.hashCode = domain.hashCode() + 37 * props.hashCode();
    }

    private static String buildName(String domain, Map props) {
        StringBuffer buf = new StringBuffer(128);
        buf.append(domain).append(':');
        Iterator i = props.entrySet().iterator();
        Map.Entry entry = (Map.Entry) i.next();
        buf.append(entry.getKey()).append('=').append(entry.getValue());
        while (i.hasNext()) {
            entry = (Map.Entry) i.next();
            buf.append(',').append(entry.getKey()).append('=').append(entry.getValue());
        }
        return buf.toString();
    }

    /**
     * Construct a GBeanName by parsing a string.
     *
     * @param name the name to parse
     */
    public GBeanName(String name) {
        int idx = name.indexOf(':');
        if (idx == -1) {
            throw new IllegalArgumentException("Missing ':' for domain: " + name);
        }
        this.name = name;
        this.domain = name.substring(0, idx);
        this.props = parseName(name.substring(idx + 1));
        this.hashCode = domain.hashCode() + 37 * props.hashCode();
    }

    private static HashMap parseName(String name) {
        if (name.endsWith(",")) {
            throw new IllegalArgumentException("Missing last property pair");
        }
        HashMap props = new HashMap();
        String[] pairs = name.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            int idx = pair.indexOf('=');
            if (idx == -1) {
                throw new IllegalArgumentException("Invalid property pair: " + pair);
            }
            String key = pair.substring(0, idx);
            String value = pair.substring(idx + 1);
            if (props.put(key, value) != null) {
                throw new IllegalArgumentException("Duplicate property: " + key);
            }
        }
        return props;
    }

    /**
     * Determine if this name matches the supplied pattern.
     * This performs a fast but simplistic pattern match which is true if:
     * <ul>
     * <li>The domains are equal</li>
     * <li>If this instance has all the supplied properties with equal values</li>
     * <ul>
     * A null domain and a null or empty properties object are considered wildcards
     * and always match; in other words GBeanName.match(null, new Properties()) will
     * always evaluate to true.
     *
     * @param domain  the domain to match
     * @param pattern the set properties to match; a Map<String,String>
     * @return true if this instance matches the pattern
     */
    public boolean matches(String domain, Map pattern) {
        if (domain != null) {
            if (!this.domain.equals(domain)) {
                return false;
            }
        }
        if (pattern != null && !pattern.isEmpty()) {
            for (Iterator i = pattern.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                String key = (String) entry.getKey();
                String ourValue = (String) props.get(key);
                if (ourValue == null || !ourValue.equals(entry.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test for equality.
     * This instance will be equal if the supplied object is a GBeanName with
     * equal domain and properties.
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof GBeanName == false) return false;
        final GBeanName other = (GBeanName) obj;
        return this.domain.equals(other.domain) && this.props.equals(other.props);
    }

    public int hashCode() {
        return hashCode;
    }

    /**
     * Return a human readable version of this GBeanName. If the instance was created
     * by parsing a String, this will be the supplied value; it it was created by
     * supplying properties, the name will contain properties in an unspecified order.
     *
     * @return a readable name
     */
    public String toString() {
        return name;
    }

    /**
     * Return a String representation of ths GBeanName.
     * The format will be <domain> ':' <key> '=' <value> ( ',' <key> '=' <value> )*
     * Keys are appended in the order determined by the supplied Comparator.
     *
     * @param keySorter the Comparator to use to order the keys.
     * @return a String representation of this GBean
     */
    public String toString(Comparator keySorter) {
        String[] keyList = (String[]) props.keySet().toArray(new String[props.keySet().size()]);
        Arrays.sort(keyList, keySorter);

        StringBuffer buf = new StringBuffer(128);
        buf.append(domain).append(':');
        String key = keyList[0];
        buf.append(key).append('=').append(props.get(key));
        for (int i = 1; i < keyList.length; i++) {
            key = keyList[i];
            buf.append(',').append(key).append('=').append(props.get(key));
        }
        return buf.toString();
    }

    private Object readResolve() {
        return new GBeanName(name);
    }

    // utility methods to support conversion from ObjectName to GBeanName

    /**
     * @deprecated
     */
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName(domain, new Hashtable(props));
    }

    /**
     * @deprecated
     */
    public GBeanName(ObjectName name) {
        this.name = name.toString();
        this.domain = name.getDomain();
        this.props = new HashMap(name.getKeyPropertyList());
        this.hashCode = domain.hashCode() + 37 * props.hashCode();
    }
}