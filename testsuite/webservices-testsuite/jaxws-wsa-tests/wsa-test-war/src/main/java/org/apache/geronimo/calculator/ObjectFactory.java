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
package org.apache.geronimo.calculator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.apache.geronimo.calculator package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Add_QNAME = new QName("http://geronimo.apache.org/calculator", "add");
    private final static QName _Multiply_QNAME = new QName("http://geronimo.apache.org/calculator", "multiply");
    private final static QName _MultiplyResponse_QNAME = new QName("http://geronimo.apache.org/calculator", "multiplyResponse");
    private final static QName _GetEPR_QNAME = new QName("http://geronimo.apache.org/calculator", "getEPR");
    private final static QName _GetEPRResponse_QNAME = new QName("http://geronimo.apache.org/calculator", "getEPRResponse");
    private final static QName _AddResponse_QNAME = new QName("http://geronimo.apache.org/calculator", "addResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.geronimo.calculator
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddResponse }
     * 
     */
    public AddResponse createAddResponse() {
        return new AddResponse();
    }

    /**
     * Create an instance of {@link GetEPRResponse }
     * 
     */
    public GetEPRResponse createGetEPRResponse() {
        return new GetEPRResponse();
    }

    /**
     * Create an instance of {@link MultiplyResponse }
     * 
     */
    public MultiplyResponse createMultiplyResponse() {
        return new MultiplyResponse();
    }

    /**
     * Create an instance of {@link Multiply }
     * 
     */
    public Multiply createMultiply() {
        return new Multiply();
    }

    /**
     * Create an instance of {@link Add }
     * 
     */
    public Add createAdd() {
        return new Add();
    }

    /**
     * Create an instance of {@link GetEPR }
     * 
     */
    public GetEPR createGetEPR() {
        return new GetEPR();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Add }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "add")
    public JAXBElement<Add> createAdd(Add value) {
        return new JAXBElement<Add>(_Add_QNAME, Add.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Multiply }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "multiply")
    public JAXBElement<Multiply> createMultiply(Multiply value) {
        return new JAXBElement<Multiply>(_Multiply_QNAME, Multiply.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiplyResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "multiplyResponse")
    public JAXBElement<MultiplyResponse> createMultiplyResponse(MultiplyResponse value) {
        return new JAXBElement<MultiplyResponse>(_MultiplyResponse_QNAME, MultiplyResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEPR }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "getEPR")
    public JAXBElement<GetEPR> createGetEPR(GetEPR value) {
        return new JAXBElement<GetEPR>(_GetEPR_QNAME, GetEPR.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEPRResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "getEPRResponse")
    public JAXBElement<GetEPRResponse> createGetEPRResponse(GetEPRResponse value) {
        return new JAXBElement<GetEPRResponse>(_GetEPRResponse_QNAME, GetEPRResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://geronimo.apache.org/calculator", name = "addResponse")
    public JAXBElement<AddResponse> createAddResponse(AddResponse value) {
        return new JAXBElement<AddResponse>(_AddResponse_QNAME, AddResponse.class, null, value);
    }

}
