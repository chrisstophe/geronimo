/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Geronimo" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Geronimo", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */
package org.apache.geronimo.deployment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.URI;
import javax.management.ObjectName;

/**
 *
 *
 * @version $Revision: 1.5 $ $Date: 2003/08/20 22:41:23 $
 */
public class MBeanMetadata {
    private String code;
    private ObjectName name;
    private ObjectName loaderName;
    private ObjectName parentName;
    private URI baseURI;
    private final List constructorTypes = new ArrayList();
    private final List constructorArgs = new ArrayList();
    private final Map attributeValues = new HashMap();
    private final Set relationships = new HashSet();
    private final Set dependencies = new HashSet();

    public MBeanMetadata() {
    }

    public MBeanMetadata(ObjectName name) {
        this.name = name;
    }

    public MBeanMetadata(ObjectName name, String code) {
        this.name = name;
        this.code = code;
    }

    public MBeanMetadata(ObjectName name, String code, ObjectName loaderName) {
        this.name = name;
        this.code = code;
        this.loaderName = loaderName;
    }

    public MBeanMetadata(ObjectName name, String code, ObjectName loaderName, ObjectName parentName) {
        this.name = name;
        this.code = code;
        this.loaderName = loaderName;
        this.parentName = parentName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ObjectName getName() {
        return name;
    }

    public void setName(ObjectName name) {
        this.name = name;
    }

    public ObjectName getLoaderName() {
        return loaderName;
    }

    public void setLoaderName(ObjectName loaderName) {
        this.loaderName = loaderName;
    }

    public ObjectName getParentName() {
        return parentName;
    }

    public void setParentName(ObjectName parentName) {
        this.parentName = parentName;
    }

    public URI getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }

    public Map getAttributeValues() {
        return attributeValues;
    }

    public List getConstructorArgs() {
        return constructorArgs;
    }

    public List getConstructorTypes() {
        return constructorTypes;
    }

    public Set getDependencies() {
        return dependencies;
    }

    public Set getRelationships() {
        return relationships;
    }
}
