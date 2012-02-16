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
package org.kuali.kfs.integration.ar;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.util.KualiDecimal;

public interface AccountsReceivableNonInvoiced extends ExternalizableBusinessObject {

    public void setChartOfAccountsCode(String refundChartCode);

    public void setAccountNumber(String refundAccount);

    public void setFinancialObjectCode(String refundObject);

    public void setFinancialDocumentLineAmount(KualiDecimal amount);

    public void setDocumentNumber(String documentNumber);

    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber);

    public void setRefundIndicator(boolean refundIndicator);

    public void setFinancialDocumentPostingYear(Integer postingYear);

    public KualiDecimal getFinancialDocumentLineAmount();

}
