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
package org.apache.geronimo.kernel.lifecycle;

import org.apache.geronimo.gbean.AbstractName;

import java.util.EventListener;

/**
 * @version $Rev$ $Date$
 */
public interface LifecycleListener extends EventListener {
    public void loaded(AbstractName abstractName);
    public void starting(AbstractName abstractName);
    public void running(AbstractName abstractName);
    public void stopping(AbstractName abstractName);
    public void stopped(AbstractName abstractName);
    public void failed(AbstractName abstractName);
    public void unloaded(AbstractName abstractName);
}
