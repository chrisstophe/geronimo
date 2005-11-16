/**
 *
 * Copyright 2005 The Apache Software Foundation
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
package org.apache.geronimo.management.geronimo;

import java.io.Serializable;
import java.util.Date;

/**
 * A web container access log manager.
 *
 * @version $Rev: 46019 $ $Date: 2004-09-14 05:56:06 -0400 (Tue, 14 Sep 2004) $
 */
public interface WebAccessLog {
    /**
     * The most search lines that will ever be returned, no matter what you
     * ask for.  This is to conserve memory and transfer bandwidth.
     */
    public final static int MAX_SEARCH_RESULTS = 1000;

    /**
     * Gets the name of all logs used by this system.  Typically there
     * is only one, but specialized cases may use more.
     *
     * @return An array of all log names
     *
     */
    String[] getLogNames();

    /**
     * Gets the name of all log files used by this log.  Typically there
     * is only one, but specialized cases may use more.
     *
     * @param log The name of the log for which to return the specific file names. 
     *
     * @return An array of all log file names
     *
     */
    String[] getLogFileNames(String log);

    /**
     * Searches the log for records matching the specified parameters.  The
     * maximum results returned will be the lesser of 1000 and the
     * provided maxResults argument.
     *
     * @see #MAX_SEARCH_RESULTS
     */
    SearchResults getMatchingItems(String logName, String host, String user, String method,
                                   String uri, Date startDate, Date endDate,
                                   Integer skipResults, Integer maxResults);

    public static class LogMessage implements Serializable {
        private final int lineNumber;
        private final String lineContent;

        public LogMessage(int lineNumber, String lineContent) {
            this.lineNumber = lineNumber;
            this.lineContent = lineContent;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getLineContent() {
            return lineContent;
        }
    }

    public static class SearchResults implements Serializable {
        private final int lineCount; // total lines in log file
        private final LogMessage[] results;
        private final boolean capped; // whether there were more matched than are returned here

        public SearchResults(int lineCount, LogMessage[] results, boolean capped) {
            this.lineCount = lineCount;
            this.results = results;
            this.capped = capped;
        }

        public int getLineCount() {
            return lineCount;
        }

        public LogMessage[] getResults() {
            return results;
        }

        public boolean isCapped() {
            return capped;
        }
    }
}
