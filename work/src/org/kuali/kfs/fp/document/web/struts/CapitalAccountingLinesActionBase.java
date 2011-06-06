/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.CapitalAccountingLines;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This is the action class for the Advance Deposit document.
 */
public class CapitalAccountingLinesActionBase extends KualiAccountingDocumentActionBase {
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) kualiDocumentFormBase;
        List<CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesFormBase.getCapitalAccountingLines();
        
        AccountingDocument tdoc = (AccountingDocument) kualiDocumentFormBase.getDocument();
        List<SourceAccountingLine> sourceAccountLines = tdoc.getSourceAccountingLines();

        for (SourceAccountingLine line : sourceAccountLines) {
            createCapitalAccountingLine(capitalAccountingLines, line);
        }
        
        List<TargetAccountingLine> targetAccountLines = tdoc.getTargetAccountingLines();

        for (TargetAccountingLine line : targetAccountLines) {
            createCapitalAccountingLine(capitalAccountingLines, line);
        }
    }
    
    /**
     * When user adds an accounting line to the either source or target, if the object code on
     * that line has capital object type code group then a capital accounting line is created.
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertAccountingLine(boolean, org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase, org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    protected void insertAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, AccountingLine line) {
        super.insertAccountingLine(isSource, financialDocumentForm, line);
        
        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) financialDocumentForm;

        List<CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesFormBase.getCapitalAccountingLines();
        createCapitalAccountingLine(capitalAccountingLines, line);
    }
      
    /**
     * When user deletes an accounting line to the either source or target, if the corresponding line in
     * capital accounting line is deleted.
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#deleteAccountingLine(boolean, org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase, int)
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {

        CapitalAccountingLinesFormBase capitalAccountingLinesFormBase = (CapitalAccountingLinesFormBase) financialDocumentForm;
        List<CapitalAccountingLines> capitalAccountingLines = capitalAccountingLinesFormBase.getCapitalAccountingLines();
        
        if (isSource) {
            // remove from capital accounting lines for this source line
            AccountingLine line = (AccountingLine) financialDocumentForm.getFinancialDocument().getSourceAccountingLines().get(deleteIndex);
            deleteCapitalAccountingLine(capitalAccountingLines, line);
        }
        else {
            // remove from document
            AccountingLine line = (AccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLines().get(deleteIndex);
            deleteCapitalAccountingLine(capitalAccountingLines, line);
        }
        
        super.deleteAccountingLine(isSource, financialDocumentForm, deleteIndex);
    }
    
    /**
     * Checks if the accounting line has an object code that belongs to object sub type group codes and
     * if so, creates a capital accounting line that will be displayed on the jsp.
     * 
     * @param capitalAccountingLines
     * @param line
     * @return List of capitalAccountingLines
     */
    protected List<CapitalAccountingLines> createCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, AccountingLine line) {
        Integer sequenceNumber = capitalAccountingLines.size() + 1;

        if (capitalAssetBuilderModuleService.hasCapitalAssetObjectSubType(line)) {
            //capital object code so we need to build the capital accounting line...
            CapitalAccountingLines cal = new CapitalAccountingLines();
            cal.setSequenceNumber(sequenceNumber++);
            cal.setChartOfAccountsCode(line.getChartOfAccountsCode());
            cal.setAccountNumber(line.getAccountNumber());
            cal.setSubAccountNumber(line.getSubAccountNumber());
            cal.setFinancialObjectCode(line.getFinancialObjectCode());
            cal.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
            cal.setProjectCode(line.getProjectCode());
            cal.setFinancialDocumentLineDescription(line.getFinancialDocumentLineDescription());
            cal.setAmount(line.getAmount());
            cal.setSelectLine(false);
            
            capitalAccountingLines.add(cal);
        }
        
        return capitalAccountingLines;
    }

    /**
     * If the line exists in capital accounting lines, that will be deleted.
     * 
     * @param capitalAccountingLines
     * @param line to remove
     * @return List of capitalAccountingLines
     */
    protected List<CapitalAccountingLines> deleteCapitalAccountingLine(List<CapitalAccountingLines> capitalAccountingLines, AccountingLine line) {
        CapitalAccountingLines cal = null;
        
        for (CapitalAccountingLines capitalAccountingLine : capitalAccountingLines) {
            if (capitalAccountingLine.getChartOfAccountsCode().equals(line.getChartOfAccountsCode()) && 
                    capitalAccountingLine.getAccountNumber().equals(line.getAccountNumber()) &&
                    capitalAccountingLine.getFinancialObjectCode().equals(line.getFinancialObjectCode())) {
                cal = capitalAccountingLine;
                break;
            }
        }
        
        if (ObjectUtils.isNotNull(cal)) {
            capitalAccountingLines.remove(cal);
        }
        
        return capitalAccountingLines;
    }
    
    /**
     * creates asset for the accounting line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward createAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CapitalAccountingLinesFormBase calfb = (CapitalAccountingLinesFormBase) form;
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * modify asset for the accounting line to the document
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward modifyAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
