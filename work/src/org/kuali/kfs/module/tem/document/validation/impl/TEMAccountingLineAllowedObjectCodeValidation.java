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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TEMAccountingLineAllowedObjectCodeValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        TemSourceAccountingLine line  = null;
        if (event instanceof UpdateAccountingLineEvent){
            line = (TemSourceAccountingLine) ((UpdateAccountingLineEvent)event).getUpdatedAccountingLine();
        }
        else{
            line = (TemSourceAccountingLine) ((AccountingLineEvent)event).getAccountingLine();
        }
        List errors = GlobalVariables.getMessageMap().getErrorPath();
        GlobalVariables.getMessageMap().clearErrorPath();
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        // Skip object code validation if the travel document is of TravelAuthorizationDocument type
        //if (event.getDocument() instanceof TravelAuthorizationDocument) {
        //    return true;
        //}
                
        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(travelDocument);
        boolean canUpdate = documentPresentationController.enableForTravelManager(travelDocument.getDocumentHeader().getWorkflowDocument());
        
        boolean valid = true;
        String errorPath = TemPropertyConstants.NEW_SOURCE_ACCTG_LINE;
        if (travelDocument.getSourceAccountingLines().contains(line)) {
            errorPath = "document." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + travelDocument.getSourceAccountingLines().indexOf(line) + "]";
        }
        
        // Test added accounting lines for null values and if there is an access change.
        valid = SpringContext.getBean(TravelDocumentService.class).validateSourceAccountingLines(travelDocument, false);

        if ((!travelDocument.getAppDocStatus().equalsIgnoreCase("Initiated")) 
                && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.IN_PROCESS)) 
                && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.CHANGE_IN_PROCESS))) {
            if (!line.getAccount().getAccountFiscalOfficerUser().getPrincipalId().equals(currentUser.getPrincipalId())
                    && !canUpdate) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TA_FISCAL_OFFICER_ACCOUNT, line.getAccountNumber());
                return false;
            }
        }
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);

        // skip accounting line validation for TA 
        if (!(event.getDocument() instanceof TravelAuthorizationDocument)) {
            if (ObjectUtils.isNotNull(line.getObjectTypeCode())) {
                // check to make sure they're the same
                List<AccountingDistribution> list = SpringContext.getBean(AccountingDistributionService.class).buildDistributionFrom(travelDocument);
                List<String> codes = new ArrayList<String>();
                for (AccountingDistribution dist : list) {
                    codes.add(dist.getObjectCode() + "_" + dist.getCardType());
                }
                
                if (!codes.contains(line.getFinancialObjectCode() + "_" + line.getCardType())) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE, line.getFinancialObjectCode(), line.getCardType());
                    valid &= false;
                }
            }
        }
        
        //Fly America validation
        if (event.getDocument() instanceof TravelAuthorizationDocument) {
            TravelAuthorizationDocument document = (TravelAuthorizationDocument) event.getDocument();
            if (document.getImportedExpenses().size() > 0){
                boolean hasAttachment = false;
                for (Note note : (List<Note>)document.getBoNotes()){
                    if (note.getAttachment() != null){
                        hasAttachment = true;
                        break;
                    }
                }
                for (ImportedExpense importedExpense : document.getImportedExpenses()){
                    if (importedExpense.getTravelCompanyCodeCode().equals(TemConstants.ExpenseTypes.PREPAID_AIRFARE)){
                        Map<String,Object> fieldValues = new HashMap<String, Object>();
                        fieldValues.put(KNSPropertyConstants.CODE,TemConstants.ExpenseTypes.PREPAID_AIRFARE);
                        fieldValues.put(KNSPropertyConstants.NAME,importedExpense.getTravelCompanyCodeName());
                        TravelCompanyCode travelCompany = (TravelCompanyCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TravelCompanyCode.class, fieldValues);
                        if (travelCompany != null && travelCompany.isForeignCompany()){
                            String financialObjectCode = importedExpense.getTravelExpenseTypeCode() != null ? importedExpense.getTravelExpenseTypeCode().getFinancialObjectCode() : null;
                            if (financialObjectCode != null && financialObjectCode.equals(line.getFinancialObjectCode())){
                                boolean isAccount = SpringContext.getBean(ParameterService.class).getIndicatorParameter(KFSConstants.ParameterNamespaces.CHART, KFSConstants.RouteLevelNames.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG);
                                if (isAccount){
                                    String cgAccount = SpringContext.getBean(ParameterService.class).getParameterValue(KFSConstants.ParameterNamespaces.CHART, KFSConstants.RouteLevelNames.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE);
                                    if (cgAccount.equals(line.getAccountNumber())){
                                        if (!hasAttachment){
                                            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_ACCOUNTING_LINE_CG);
                                            valid &= false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (line.getAmount().isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_CUSTOM, "Amount must be greater than zero.");
            valid &= false;
        }
        
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().getErrorPath().addAll(errors);

        return valid;
    }

    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }
    
}
