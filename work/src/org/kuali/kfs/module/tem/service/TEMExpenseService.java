/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kns.util.KualiDecimal;

public interface TEMExpenseService {

    public Map<String, AccountingDistribution> getAccountingDistribution(TravelDocument document);
    
    public String getExpenseType();
    
    public List<TEMExpense> getExpenseDetails(TravelDocument document);
    
    public KualiDecimal getAllExpenseTotal(TravelDocument document, boolean includeNonReimbursable);
    
    public KualiDecimal getNonReimbursableExpenseTotal(TravelDocument document);
    
    public void processExpense(TravelDocument travelDocument);
    
    public void updateExpense(TravelDocument travelDocument);
}
