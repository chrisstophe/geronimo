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

package org.apache.geronimo.network.protocol.totem.replication;

import org.apache.geronimo.network.protocol.DownPacket;
import org.apache.geronimo.network.protocol.Protocol;
import org.apache.geronimo.network.protocol.ProtocolException;
import org.apache.geronimo.network.protocol.UpPacket;


/**
 * @version $Revision: 1.4 $ $Date: 2004/08/01 13:03:51 $
 */
public class TotemReplicationProtocol implements Protocol {

    public void setup() throws ProtocolException {
    }

    public void drain() throws ProtocolException {
    }

    public void teardown() throws ProtocolException {
    }

    public void sendUp(UpPacket packet) throws ProtocolException {
    }

    public void sendDown(DownPacket packet) throws ProtocolException {
    }

    public Protocol getUpProtocol() {
        return null;
    }

    public void setUpProtocol(Protocol up) {
    }

    public Protocol getDownProtocol() {
        return null;
    }

    public void setDownProtocol(Protocol down) {
    }

    public void flush() throws ProtocolException {
    }

    public void clearLinks() {
    }

    public Protocol cloneProtocol() throws CloneNotSupportedException {
        return (Protocol) super.clone();
    }
}
