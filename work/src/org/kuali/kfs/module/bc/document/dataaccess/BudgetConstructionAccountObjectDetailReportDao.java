/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.dataaccess;


public interface BudgetConstructionAccountObjectDetailReportDao {


   /**
   * 
   * adds rows with object detail to the temporary table used for budget construction account balance reporting.
   * @param principalName: the id of the user initiating the report
   */
    public void updateReportsAccountObjectDetailTable(String principalName);
    
    /**
     * 
     * adds rows consolidated at the object code level to the temporary table used for budget construction account balance reporting.
     * @param principalName: the id of the user initiating the report
     */
    public void updateReportsAccountObjectConsolidatedTable(String principalName);

    
}

