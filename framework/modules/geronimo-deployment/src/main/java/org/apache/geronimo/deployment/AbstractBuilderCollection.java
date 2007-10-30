/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.geronimo.deployment;

import java.util.Collection;
import java.util.Collections;

import javax.xml.namespace.QName;

import org.apache.geronimo.deployment.xmlbeans.XmlBeansUtil;
import org.apache.geronimo.gbean.ReferenceCollection;
import org.apache.geronimo.gbean.ReferenceCollectionEvent;
import org.apache.geronimo.gbean.ReferenceCollectionListener;
import org.apache.xmlbeans.QNameSet;

/**
 * @version $Rev$ $Date$
 */
public abstract class AbstractBuilderCollection<T> {
    protected final Collection<T> builders;
    protected final QName basePlanElementName;
    protected QNameSet specQNames = QNameSet.EMPTY;
    protected QNameSet planQNames = QNameSet.EMPTY;

    protected AbstractBuilderCollection(Collection<T> builders, final QName basePlanElementName) {
        this.builders = builders == null ? Collections.EMPTY_SET : builders;
        this.basePlanElementName = basePlanElementName;
        if (builders instanceof ReferenceCollection) {
            ((ReferenceCollection) builders).addReferenceCollectionListener(new ReferenceCollectionListener() {

                public void memberAdded(ReferenceCollectionEvent event) {
                    addBuilder((T) event.getMember());
                }

                public void memberRemoved(ReferenceCollectionEvent event) {
                    T builder = (T) event.getMember();
                    QNameSet builderSpecQNames = ((AbstractNamespaceBuilder) builder).getSpecQNameSet();
                    specQNames = specQNames.intersect(builderSpecQNames.inverse());
                    QNameSet builderPlanQNames = ((AbstractNamespaceBuilder) builder).getPlanQNameSet();
                    planQNames = planQNames.intersect(builderPlanQNames.inverse());
                    XmlBeansUtil.unregisterSubstitutionGroupElements(basePlanElementName, builderPlanQNames);
                }
            });
        }
        for (T builder : this.builders) {
            addBuilder(builder);
        }
    }


    protected void addBuilder(T builder) {
        QNameSet builderSpecQNames = ((AbstractNamespaceBuilder) builder).getSpecQNameSet();
        QNameSet builderPlanQNames = ((AbstractNamespaceBuilder) builder).getPlanQNameSet();
        if (builderSpecQNames == null) {
            throw new IllegalStateException("Builder " + builder + " is missing spec qnames");
        }
        if (builderPlanQNames == null) {
            throw new IllegalStateException("Builder " + builder + " is missing plan qnames");
        }
        if (!specQNames.isDisjoint(builderSpecQNames) && !planQNames.isDisjoint(builderPlanQNames)) {
            throw new IllegalArgumentException("Duplicate builderSpecQNames in builder set: " + builderSpecQNames + " and duplicate builderPlanQNames in builder set: " + builderPlanQNames);
        }
        try {
            specQNames = specQNames.union(builderSpecQNames);
            planQNames = planQNames.union(builderPlanQNames);
        } catch (NullPointerException e) {
            throw (IllegalArgumentException) new IllegalArgumentException("could not merge qnamesets for builder " + builder).initCause(e);

        }
        //really?
        XmlBeansUtil.registerSubstitutionGroupElements(basePlanElementName, builderPlanQNames);
    }

    public QNameSet getSpecQNameSet() {
        return specQNames;
    }

    public QNameSet getPlanQNameSet() {
        return null;
    }
}
