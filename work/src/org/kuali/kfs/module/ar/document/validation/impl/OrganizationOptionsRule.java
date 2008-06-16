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
package org.kuali.module.ar.rules;

import org.apache.log4j.Logger;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.SystemInformationService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.financial.service.UniversityDateService;

public class OrganizationOptionsRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationOptionsRule.class);
    
    private OrganizationOptions newOrganizationOptions;
    private OrganizationOptions oldOrganizationOptions;

    @Override
    public void setupConvenienceObjects() {
        newOrganizationOptions = (OrganizationOptions) super.getNewBo();
        oldOrganizationOptions = (OrganizationOptions) super.getOldBo();
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        success &= doesSystemInformationExistForProcessingChartAngOrg(newOrganizationOptions);
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;

    }    
    
    /**
     * This method this returns true if system information row exists for processing chart and org
     * 
     * @param organizationOptions
     * @return
     */
    public boolean doesSystemInformationExistForProcessingChartAngOrg(OrganizationOptions organizationOptions){
        boolean success = true;
        
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        String chartOfAccountsCode = organizationOptions.getProcessingChartOfAccountCode();
        String organizationCode = organizationOptions.getOrganizationCode();
        
        SystemInformation systemInformation = SpringContext.getBean(SystemInformationService.class).getByPrimaryKey(currentFiscalYear, chartOfAccountsCode, organizationCode);
        
        if( ObjectUtils.isNull(systemInformation) ){
            putFieldError(ArConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, ArConstants.OrganizationOptionsErrors.SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG, new String[]{ chartOfAccountsCode, organizationCode } );
            success = false;
        }
        return success;
    }
}
