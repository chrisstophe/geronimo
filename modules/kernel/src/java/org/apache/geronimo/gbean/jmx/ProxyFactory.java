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

package org.apache.geronimo.gbean.jmx;

import java.lang.reflect.InvocationTargetException;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 *
 *
 * @version $Revision: 1.3 $ $Date: 2004/02/25 09:57:48 $
 */
public class ProxyFactory {
    private final Class type;
    private final Enhancer enhancer;

    public ProxyFactory(Class type) {
        enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(false);
        this.type = enhancer.createClass();
    }

    public Class getType() {
        return type;
    }

    public Object create(MethodInterceptor methodInterceptor) throws InvocationTargetException {
        return create(methodInterceptor, new Class[0], new Object[0]);
    }

    public synchronized Object create(MethodInterceptor methodInterceptor, Class[] types, Object[] arguments) throws InvocationTargetException {
        enhancer.setCallbacks(new Callback[]{methodInterceptor});
        // @todo trap CodeGenerationException indicating missing no-arg ctr
        return enhancer.create(types, arguments);
    }
}
