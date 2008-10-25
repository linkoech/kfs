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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.rice.kns.util.AssertionUtils.assertThat;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.ServiceBillingControl;
import org.kuali.kfs.fp.document.service.ServiceBillingControlService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedUpdateAccountingLineEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class ServiceBillingAccountingLineAccessibilityValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        AccountingDocument financialDocument = (AccountingDocument)event.getDocument();
        AccountingLine accountingLine = getAccountingLineForValidation();
        // Duplicate code from accountIsAccessible() to avoid unnecessary calls to SB control and Workgroup services.
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return accountingLine.isTargetAccountingLine() || serviceBillingIncomeAccountIsAccessible(accountingLine, event, GlobalVariables.getUserSession().getPerson());
        }
       
        return true;
    }

    /**
     * Checks the account and user against the service billing control table.
     * 
     * @param accountingLine The accounting line from the income section of the service billing document.
     * @param action The type of error messages to generate, if not null.
     * @param user The user for whom to check accessibility.
     * @return Whether the given user is authorized to use the given account in the service billing income section.
     */
    protected boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine,AttributedDocumentEvent event, Person user) {
        assertThat(accountingLine.isSourceAccountingLine(), accountingLine);
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            // Ignore empty key because hasAccessibleAccountingLines() may not validate beforehand.
            return false;
        }
        ServiceBillingControl control = SpringContext.getBean(ServiceBillingControlService.class).getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (ObjectUtils.isNull(control)) {
            if (event != null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, noServiceBillingControlErrorKey(event), accountingLine.getAccountNumber());
            }
            return false;
        }

        if (user.isMember(control.getWorkgroupName())) {
            return true;
        }
        else {
            if (event != null) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, notControlGroupMemberErrorKey(event), accountingLine.getAccountNumber(), user.getPrincipalName(), control.getWorkgroupName());
            }
            return false;
        }
    }
    
    /**
     * This method determines what error key to use when posting the associated error.  The error key is determined 
     * based on the constants passed in, which are values defined in the AccountingDocumentRuleBase.AccountingLineAction.
     * 
     * @param action The constant used to identify which error key to return.
     * @return The error key for not having a service billing control.
     * 
     * @see AccountingDocumentRuleBase.AccountingLineAction
     */
    protected static String noServiceBillingControlErrorKey(AttributedDocumentEvent event) {
        if (event instanceof AttributedAddAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NO_SB_CTRL;
        } else  if (event instanceof AttributedUpdateAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NO_SB_CTRL;
        } else  if (event instanceof AttributedDeleteAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NO_SB_CTRL;
        } else
            throw new AssertionError(event);
    }

    /**
     * This method determines what error key to use when posting the associated error.  The error key is determined 
     * based on the constants passed in, which are values defined in the AccountingDocumentRuleBase.AccountingLineAction.
     * 
     * @param action The constant used to identify which error key to return.
     * @return The error key for not being a member of the workgroup of the necessary service billing control.
     * 
     * @see AccountingDocumentRuleBase.AccountingLineAction
     */
    protected static String notControlGroupMemberErrorKey(AttributedDocumentEvent event) {
        if (event instanceof AttributedAddAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NOT_IN_SB_CTRL_GRP;
        } else  if (event instanceof AttributedUpdateAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NOT_IN_SB_CTRL_GRP;
        } else  if (event instanceof AttributedDeleteAccountingLineEvent) {
            return KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NOT_IN_SB_CTRL_GRP;
        } else
            throw new AssertionError(event);
        
    }
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}

