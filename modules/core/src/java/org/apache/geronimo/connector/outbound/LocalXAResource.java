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

package org.apache.geronimo.connector.outbound;

import javax.resource.ResourceException;
import javax.resource.spi.LocalTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * LocalXAResource adapts a local transaction to be controlled by a
 * JTA transaction manager.  Of course, it cannot provide xa
 * semantics.
 *
 *
 * @version $Revision: 1.4 $ $Date: 2003/12/09 04:16:25 $
 */
public class LocalXAResource implements XAResource {

    //accessible in package for testing
    final LocalTransaction localTransaction;
    private Xid xid;
    private int transactionTimeout;

    public LocalXAResource(LocalTransaction localTransaction) {
        this.localTransaction = localTransaction;
    }

    // Implementation of javax.transaction.xa.XAResource

    public void commit(Xid xid, boolean flag) throws XAException {
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException();
        }
        try {
            localTransaction.commit();
        } catch (ResourceException e) {
            XAException xae = new XAException();
            //xae.setLinkedException(e);
            throw xae;
        } finally {
            this.xid = null;
        }

    }

    public void forget(Xid xid) throws XAException {
        this.xid = null;
    }

    public int getTransactionTimeout() throws XAException {
        return transactionTimeout;
    }

    public boolean isSameRM(XAResource xares) throws XAException {
        return this == xares;
    }

    public Xid[] recover(int n) throws XAException {
        return null;
    }

    public void rollback(Xid xid) throws XAException {
        if (this.xid == null || !this.xid.equals(xid)) {
            throw new XAException();
        }
        try {
            localTransaction.rollback();
        } catch (ResourceException e) {
            XAException xae = new XAException();
            //xae.setLinkedException(e);
            throw xae;
        }
        finally {
            this.xid = null;
        }
    }

    public boolean setTransactionTimeout(int txTimeout) throws XAException {
        this.transactionTimeout = txTimeout;
        return true;
    }

    public void start(Xid xid, int flag) throws XAException {
        if (flag == XAResource.TMNOFLAGS) {
            // first time in this transaction
            if (this.xid != null) {
                throw new XAException("already enlisted");
            }
            this.xid = xid;
            try {
                localTransaction.begin();
            } catch (ResourceException e) {
                throw (XAException) new XAException("could not start local tx").initCause(e);
            }
        } else if (flag == XAResource.TMRESUME) {
            if (xid != this.xid) {
                throw new XAException("attempting to resume in different transaction");
            }
        } else {
            throw new XAException("unknown state");
        }
    }

    public void end(Xid xid, int flag) throws XAException {
        if (xid != this.xid) {
            throw new XAException();
        }
        //we could keep track of if the flag is TMSUCCESS...
    }

    public int prepare(Xid xid) throws XAException {
        //log warning that semantics are incorrect...
        return XAResource.XA_OK;
    }
}
