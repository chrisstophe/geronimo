/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.geronimo.buildsupport

import org.codehaus.gmaven.mojo.GroovyMojo

import org.apache.maven.project.MavenProject

/**
 * Helper to fix generate Eclipse project files.
 *
 * @goal fix-eclipse-projects
 * @phase validate
 *
 * @version $Rev$ $Date$
 */
class FixEclipseProjectsMojo
    extends GroovyMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    void execute() {
        def file = new File(project.basedir, '.classpath')
        def dir = new File(project.basedir, 'target/generated-sources/xmlbeans')
        
        //
        // FIXME: Change this to reflect its a hack for xmlbeans not clover
        //
        def targetPath = 'target/clover/classes'
        
        if (file.exists() && dir.exists()) {
            def classpath = new XmlParser().parse(file)
            if (!classpath.classpathentry.findAll { it.'@path' == targetPath }) {
                log.info('Updating Eclipse .classpath for XMLBeans muck...')
                
                def node = new Node(classpath, 'classpathentry', [ kind: 'lib', path: targetPath ])
                new XmlNodePrinter(file.newPrintWriter()).print(classpath)
            }
        }
    }
}

