/**
 *
 * Copyright 2006 The Apache Software Foundation
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

package org.apache.geronimo.deployment.service;

import org.apache.geronimo.common.DeploymentException;
import org.apache.geronimo.deployment.xbeans.ArtifactType;
import org.apache.geronimo.deployment.xbeans.ClassFilterType;
import org.apache.geronimo.deployment.xbeans.DependenciesType;
import org.apache.geronimo.deployment.xbeans.EnvironmentDocument;
import org.apache.geronimo.deployment.xbeans.EnvironmentType;
import org.apache.geronimo.deployment.xbeans.ImportType;
import org.apache.geronimo.deployment.xbeans.PropertiesType;
import org.apache.geronimo.deployment.xbeans.PropertyType;
import org.apache.geronimo.kernel.repository.Artifact;
import org.apache.geronimo.kernel.repository.Environment;
import org.apache.geronimo.kernel.repository.Dependency;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @version $Rev:$ $Date:$
 */
public class EnvironmentBuilder implements XmlAttributeBuilder {
    private final static String NAMESPACE = EnvironmentDocument.type.getDocumentElementName().getNamespaceURI();


    public static Environment buildEnvironment(EnvironmentType environmentType) {
        Environment environment = new Environment();
        if (environmentType != null) {
            if (environmentType.isSetConfigId()) {
                environment.setConfigId(toArtifact(environmentType.getConfigId()));
            }

            Map propertiesMap = new HashMap();
            if (environmentType.isSetProperties()) {
                PropertyType[] propertiesArray = environmentType.getProperties().getPropertyArray();
                for (int i = 0; i < propertiesArray.length; i++) {
                    PropertyType property = propertiesArray[i];
                    String key = property.getName().trim();
                    String value = property.getValue().trim();
                    propertiesMap.put(key, value);
                }
            }
            environment.setProperties(propertiesMap);

            if (environmentType.isSetDependencies()) {
                ArtifactType[] dependencyArray = environmentType.getDependencies().getDependencyArray();
                LinkedHashSet dependencies = toDependencies(dependencyArray);
                environment.setDependencies(dependencies);
            }
            environment.setInverseClassLoading(environmentType.isSetInverseClassloading());
            environment.setSuppressDefaultEnvironment(environmentType.isSetSuppressDefaultEnvironment());
            environment.setHiddenClasses(toFilters(environmentType.getHiddenClasses()));
            environment.setNonOverrideableClasses(toFilters(environmentType.getNonOverridableClasses()));
        }

        return environment;
    }

    public static void mergeEnvironments(Environment environment, Environment additionalEnvironment) {
        if (additionalEnvironment != null) {
            //TODO merge configIds??
            if (environment.getConfigId() == null){
                environment.setConfigId(additionalEnvironment.getConfigId());
            }
            environment.addDependencies(additionalEnvironment.getDependencies());
            environment.addProperties(additionalEnvironment.getProperties());
            environment.setInverseClassLoading(environment.isInverseClassLoading() || additionalEnvironment.isInverseClassLoading());
            environment.setSuppressDefaultEnvironment(environment.isSuppressDefaultEnvironment() || additionalEnvironment.isSuppressDefaultEnvironment());
            environment.addHiddenClasses(additionalEnvironment.getHiddenClasses());
            environment.addNonOverrideableClasses(additionalEnvironment.getNonOverrideableClasses());
        }
    }

    public static Environment buildEnvironment(EnvironmentType environmentType, Environment defaultEnvironment) {
        Environment environment = buildEnvironment(environmentType);
        mergeEnvironments(environment, defaultEnvironment);
        return environment;
    }

    public static EnvironmentType buildEnvironmentType(Environment environment) {
        EnvironmentType environmentType = EnvironmentType.Factory.newInstance();
        ArtifactType configId = toArtifactType(environment.getConfigId());
        environmentType.setConfigId(configId);

        if (environment.getProperties().size() >0) {
            PropertiesType propertiesType = environmentType.addNewProperties();
            for (Iterator iterator = environment.getProperties().entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                PropertyType propertyType = propertiesType.addNewProperty();
                propertyType.setName(name);
                propertyType.setValue(value);
            }
        }
        List dependencies = toArtifactTypes(environment.getDependencies());
        ArtifactType[] artifactTypes = (ArtifactType[]) dependencies.toArray(new ArtifactType[dependencies.size()]);
        DependenciesType dependenciesType = environmentType.addNewDependencies();
        dependenciesType.setDependencyArray(artifactTypes);
        if (environment.isInverseClassLoading()) {
            environmentType.addNewInverseClassloading();
        }
        if (environment.isSuppressDefaultEnvironment()) {
            environmentType.addNewSuppressDefaultEnvironment();
        }
        environmentType.setHiddenClasses(toFilterType(environment.getHiddenClasses()));
        environmentType.setNonOverridableClasses(toFilterType(environment.getNonOverrideableClasses()));
        return environmentType;
    }

    private static ClassFilterType toFilterType(Set filters) {
        String[] classFilters = (String[]) filters.toArray(new String[filters.size()]);
        ClassFilterType classFilter  = ClassFilterType.Factory.newInstance();
        classFilter.setFilterArray(classFilters);
        return classFilter;
    }

    private static List toArtifactTypes(Collection artifacts) {
        List dependencies = new ArrayList();
        for (Iterator iterator = artifacts.iterator(); iterator.hasNext();) {
            Dependency dependency = (Dependency) iterator.next();
            ArtifactType artifactType = toArtifactType(dependency);
            dependencies.add(artifactType);
        }
        return dependencies;
    }

    private static ArtifactType toArtifactType(Artifact artifact) {
        ArtifactType artifactType = ArtifactType.Factory.newInstance();
        if (artifact.getGroupId() != null) {
            artifactType.setGroupId(artifact.getGroupId());
        }
        if (artifact.getArtifactId() != null) {
            artifactType.setArtifactId(artifact.getArtifactId());
        }
        if (artifact.getVersion() != null) {
            artifactType.setVersion(artifact.getVersion().toString());
        }
        if (artifact.getType() != null) {
            artifactType.setType(artifact.getType());
        }
        return artifactType;
    }

    private static ArtifactType toArtifactType(Dependency dependency) {
        ArtifactType artifactType = toArtifactType(dependency.getArtifact());

        org.apache.geronimo.kernel.repository.ImportType importType = dependency.getImportType();
        if (importType == org.apache.geronimo.kernel.repository.ImportType.CLASSES) {
            artifactType.setImport(ImportType.CLASSES);
        } else if (importType == org.apache.geronimo.kernel.repository.ImportType.SERVICES) {
            artifactType.setImport(ImportType.SERVICES);
        }

        return artifactType;
    }

    private static Set toFilters(ClassFilterType filterType) {
        Set filters = new HashSet();
        if (filterType != null) {
            String[] filterArray = filterType.getFilterArray();
            for (int i = 0; i < filterArray.length; i++) {
                String filter = filterArray[i].trim();
                filters.add(filter);
            }
        }
        return filters;
    }

    //package level for testing
    static LinkedHashSet toArtifacts(ArtifactType[] artifactTypes) {
        LinkedHashSet artifacts = new LinkedHashSet();
        for (int i = 0; i < artifactTypes.length; i++) {
            ArtifactType artifactType = artifactTypes[i];
            Artifact artifact = toArtifact(artifactType);
            artifacts.add(artifact);
        }
        return artifacts;
    }

    private static LinkedHashSet toDependencies(ArtifactType[] dependencyArray) {
        LinkedHashSet dependencies = new LinkedHashSet();
        for (int i = 0; i < dependencyArray.length; i++) {
            ArtifactType artifactType = dependencyArray[i];
            Dependency dependency = toDependency(artifactType);
            dependencies.add(dependency);
        }
        return dependencies;
    }

    private static Dependency toDependency(ArtifactType artifactType) {
        Artifact artifact = toArtifact(artifactType);
        if (ImportType.CLASSES.equals(artifactType.getImport())) {
            return new Dependency(artifact, org.apache.geronimo.kernel.repository.ImportType.CLASSES);
        } else if (ImportType.SERVICES.equals(artifactType.getImport())) {
            return new Dependency(artifact, org.apache.geronimo.kernel.repository.ImportType.SERVICES);
        } else if (artifactType.getImport() == null) {
            return new Dependency(artifact, org.apache.geronimo.kernel.repository.ImportType.ALL);
        } else {
            throw new IllegalArgumentException("Unknown import type: " + artifactType.getImport());
        }
    }
    
    //TODO make private
    static Artifact toArtifact(ArtifactType artifactType) {
        String groupId = artifactType.isSetGroupId() ? artifactType.getGroupId().trim() : null;
        String type = artifactType.isSetType() ? artifactType.getType().trim() : "jar";
        String artifactId = artifactType.getArtifactId().trim();
        String version = artifactType.isSetVersion() ? artifactType.getVersion().trim() : null;
        return new Artifact(groupId, artifactId, version, type);
    }


    public String getNamespace() {
        return NAMESPACE;
    }

    public Object getValue(XmlObject xmlObject, String type, ClassLoader cl) throws DeploymentException {

        EnvironmentType environmentType;
        if (xmlObject instanceof EnvironmentType) {
            environmentType = (EnvironmentType) xmlObject;
        } else {
            environmentType = (EnvironmentType) xmlObject.copy().changeType(EnvironmentType.type);
        }
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            Collection errors = new ArrayList();
            xmlOptions.setErrorListener(errors);
            if (!environmentType.validate(xmlOptions)) {
                throw new XmlException("Invalid deployment descriptor: " + errors + "\nDescriptor: " + environmentType.toString(), null, errors);
            }
        } catch (XmlException e) {
            throw new DeploymentException(e);
        }

        return buildEnvironment(environmentType);
    }

    //This is added by hand to the xmlAttributeBuilders since it is needed to bootstrap the ServiceConfigBuilder.
}
