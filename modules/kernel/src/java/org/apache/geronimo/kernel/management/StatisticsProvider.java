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

package org.apache.geronimo.kernel.management;

import javax.management.j2ee.statistics.Stats;

/**
 * This is a representation of the StatisticsProvider type defined in the J2EE Management specification.
 *
 * @version $Revision: 1.2 $ $Date: 2004/02/25 09:57:50 $
 */
public interface StatisticsProvider {
    /**
     * Gets the statistics collected for this class.
     *
     * @return gets collected for this class
     */
    Stats getStats();
}
