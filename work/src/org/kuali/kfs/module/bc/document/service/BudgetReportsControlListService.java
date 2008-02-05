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
package org.kuali.module.budget.service;

import java.util.List;

import org.kuali.module.budget.bo.BudgetConstructionPullup;


/**
 * This interface defines methods that manipulate objects used by the Organization Selection screens. Manipulated objects include
 * BudgetConstructionPullup with methods that populate and depopulate the associated table for a specific user.
 */
public interface BudgetReportsControlListService {

    /**
     * This method cleans all control list tables.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @return
     */

    public void cleanReportsControlList(String idForSession, String personUserIdentifier);

    /**
     * This method updates all control list tables.
     * 
     * @param idForSession
     * @param personUserIdentifier
     * @param universityFiscalYear
     * @return
     */
    public void updateReportsControlList(String idForSession, String personUserIdentifier, Integer universityFiscalYear, List<BudgetConstructionPullup> budgetConstructionPullup);

    /**
     * This method changes flags in ld_bcn_pullup_t with Organization and Chart code.
     * 
     * @param personUserIdentifier
     * @param budgetConstructionPullup
     * @return
     */
    public void changeFlagOrganizationAndChartOfAccountCodeSelection(String personUserIdentifier, List<BudgetConstructionPullup> budgetConstructionPullup);

    /**
     * This method cleans sub-fund group list.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsSubFundGroupSelectList(String personUserIdentifier);

    /**
     * This method updates sub-fund group list
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateReportsSubFundGroupSelectList(String personUserIdentifier);

    /**
     * This method cleans acount summary table.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void cleanReportsAccountSummaryTable(String personUserIdentifier);

    /**
     * This method updates acount summary table.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateRepotsAccountSummaryTable(String personUserIdentifier);

    /**
     * This method updates acount summary table when users choose consolidation.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateRepotsAccountSummaryTableWithConsolidation(String personUserIdentifier);

    /**
     * This method updates flags in LD_BCN_SUBFUND_PICK_T with selected sub-fund group code.
     * 
     * @param personUserIdentifier
     * @param selectedSubfundGroupCodeList
     * @return
     */
    public void updateReportsSelectedSubFundGroupFlags(String personUserIdentifier, List<String> selectedSubfundGroupCodeList);

}
