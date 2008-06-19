/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import static org.kuali.kfs.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;
import static org.kuali.kfs.KFSPropertyConstants.PERCENT;

import org.kuali.kfs.bo.AccountingLineParserBase;

/**
 * This class is used to parse an PurApItem accounting line.
 */
public class PurApAccountingLineParser extends AccountingLineParserBase {
    private static final String[] PURAP_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, PERCENT };

    /**
     * Constructs a AuxiliaryVoucherAccountingLineParser.java.
     */
    public PurApAccountingLineParser() {
        super();
    }

    /**
     * @see org.kuali.core.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return PURAP_FORMAT;
    }
    
    /**
     * @see org.kuali.core.bo.AccountingLineParser#getTargetAccountingLineFormat()
     */
    @Override
    public String[] getTargetAccountingLineFormat() {
        return PURAP_FORMAT;
    }
}
