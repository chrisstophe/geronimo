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

package org.apache.geronimo.common.propertyeditor;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * A property editor for {@link javax.management.ObjectName}.
 *
 * @version $Revision: 1.2 $ $Date: 2004/02/25 09:57:03 $
 */
public class ObjectNameEditor
    extends TextPropertyEditorSupport
{
    /**
     * Returns a ObjectName for the input object converted to a string.
     *
     * @return a ObjectName object
     *
     * @throws PropertyEditorException   An MalformedObjectNameException occured.
     */
    public Object getValue()
    {
        try {
            return new ObjectName(getAsText());
        }
        catch (MalformedObjectNameException e) {
            throw new PropertyEditorException(e);
        }
    }
}
