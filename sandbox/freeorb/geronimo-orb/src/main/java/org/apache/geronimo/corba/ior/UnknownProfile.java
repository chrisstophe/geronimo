/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.geronimo.corba.ior;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TaggedComponent;


public class UnknownProfile extends Profile {

    private final int tag;
    private final byte[] data;

    public UnknownProfile(int tag, byte[] data) {
        this.tag = tag;
        this.data = data;
    }

    public int tag() {
        return tag;
    }

    public int getComponentCount() {
        return 0;
    }

    public int getTag(int idx) {
        return 0;
    }

    public TaggedComponent getTaggedComponent(int idx) {
        return null;
    }

    public Component getComponent(int idx) {
        return null;
    }

    protected void write_encapsulated_content(OutputStream out) {
        out.write_octet_array(data, 0, data.length);
    }

    protected void write_content(OutputStream eo) {
        throw new INTERNAL();
    }

    protected byte[] get_encapsulation_bytes() {
        return data;
    }

}
