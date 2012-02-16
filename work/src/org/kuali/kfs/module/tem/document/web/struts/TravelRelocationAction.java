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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.COVERSHEET_FILENAME_FORMAT;
import static org.kuali.kfs.module.tem.TemConstants.REMAINING_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_FILE_EXTENSION;
import static org.kuali.kfs.sys.KFSConstants.ReportGeneration.PDF_MIME_TYPE;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.WireCharge;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.web.struts.DisbursementVoucherForm;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelRelocationAuthorizer;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.document.web.bean.TravelRelocationMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.SummaryByDayReport;
import org.kuali.kfs.module.tem.report.service.ExpenseSummaryReportService;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.SummaryByDayReportService;
import org.kuali.kfs.module.tem.report.service.TravelReportService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

public class TravelRelocationAction extends TravelActionBase {
    private static final String[] reloMethodToCallExclusionArray = { "recalculate", "calculate", "recalculateTripDetailTotal" };

    /**
     * method used for doc handler actions. Typically assumes that this is the entry point for the document when it is first
     * created. A number of things are done hear assuming the document is created at this point.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.docHandler(mapping, form, request, response);
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final TravelRelocationDocument document = reloForm.getTravelRelocationDocument();

        refreshCollectionsFor(document);

        final String identifierStr = request.getParameter(TRVL_IDENTIFIER_PROPERTY);
        if (identifierStr != null) {

            debug("Creating relocation for document number ", identifierStr);

            final TravelRelocationDocument oldRelocation = getTravelRelocation(identifierStr);
            if (oldRelocation != null) {
                debug("Setting traveler with id ", oldRelocation.getTravelerDetailId());
                document.setTravelerDetailId(oldRelocation.getTravelerDetailId());
                document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
                debug("Traveler is ", document.getTraveler(), " with customer number ", document.getTraveler().getCustomerNumber());

                if (document.getTraveler().getPrincipalId() != null) {
                    document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
                }

                document.setTripDescription(oldRelocation.getTripDescription());
                document.setTripType(oldRelocation.getTripType());
                document.setTripTypeCode(oldRelocation.getTripTypeCode());
                document.setPrimaryDestinationName(oldRelocation.getPrimaryDestinationName());
                document.setTripBegin(oldRelocation.getTripBegin());
                document.setTripEnd(oldRelocation.getTripEnd());
                document.setJobClsCode(oldRelocation.getJobClsCode());
                document.setReasonCode(oldRelocation.getReasonCode());
                document.setFromAddress1(oldRelocation.getFromAddress1());
                document.setFromAddress2(oldRelocation.getFromAddress2());
                document.setFromCity(oldRelocation.getFromCity());
                document.setFromCountryCode(oldRelocation.getFromCountryCode());
                document.setFromStateCode(oldRelocation.getFromStateCode());
                document.setToAddress1(oldRelocation.getToAddress1());
                document.setToAddress2(oldRelocation.getToAddress2());
                document.setToCity(oldRelocation.getToCity());
                document.setToCountryCode(oldRelocation.getToCountryCode());
                document.setToStateCode(oldRelocation.getToStateCode());
            }
        }

        return retval;
    }

    // /**
    // * Adds special circumstances answers from a {@link old TravelRelocaitonDocument} instance to the {@link
    // TravelRelocationDocument} for
    // * viewing.
    // *
    // * @param relocation to add special circumstances answers
    // * @param oldRelocation {@link TravelRelocaiton}
    // */
    // protected void initializeSpecialCircumstances(final TravelRelocationDocument relocation,TravelRelocationDocument
    // oldRelocation) {
    // for (SpecialCircumstances oldSpCircumstances : oldRelocation.getSpecialCircumstances()) {
    // Long oldQuestionId = oldSpCircumstances.getQuestionId();
    //
    // for(SpecialCircumstances reloSpCircumstances : relocation.getSpecialCircumstances()) {
    // Long reloQuestionId = reloSpCircumstances.getQuestionId();
    //
    // if(reloQuestionId != null && reloQuestionId.equals(oldQuestionId)){
    // reloSpCircumstances.setText(oldSpCircumstances.getText());
    // }
    // }
    //
    // }
    // }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.execute(mapping, form, request, response);

        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final TravelRelocationDocument document = ((TravelRelocationForm) form).getTravelRelocationDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();

        setButtonPermissions(reloForm);
        setContactMasking(reloForm);
        setTaxSelectable(reloForm);
        debug("Found ", document.getActualExpenses().size(), " other expenses");

        if (reloForm.getBoNotes() == null || reloForm.getBoNotes().size() == 0) {
            reloForm.setBoNotes(getNoteService().getByRemoteObjectId(document.getDocumentHeader().getObjectId()));
            // document.getDocumentHeader().setBoNotes(reloForm.getBoNotes());
            document.setBoNotes(reloForm.getBoNotes());
        }

        if (ObjectUtils.isNotNull(document.getActualExpenses())) {
            document.enableExpenseTypeSpecificFields(document.getActualExpenses());
        }

        if (!reloForm.getMethodToCall().equalsIgnoreCase("dochandler")) {
            if (!getCalculateIgnoreList().contains(reloForm.getMethodToCall())) {
                recalculateTripDetailTotalOnly(mapping, form, request, response);
            }

            KualiDecimal totalRemaining = KualiDecimal.ZERO;
            for (final AccountingDistribution dist : reloForm.getDistribution()) {
                totalRemaining = totalRemaining.add(dist.getRemainingAmount());
            }

            request.setAttribute(REMAINING_DISTRIBUTION_ATTRIBUTE, totalRemaining);
        }

        showAccountDistribution(request, document);

        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().stateIsInitiated());

        return retval;
    }

    protected void setContactMasking(TravelRelocationForm reloForm) {
        reloForm.setCanUnmask(reloForm.isUserDocumentInitiator());
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelRelocationForm form) {
        final TravelRelocationAuthorizer authorizer = getDocumentAuthorizer(form);
        form.setCanCertify(authorizer.canCertify(form.getTravelRelocationDocument(), GlobalVariables.getUserSession().getPerson()));
    }

    protected void setTaxSelectable(final TravelRelocationForm form) {
        final TravelRelocationAuthorizer authorizer = getDocumentAuthorizer(form);
        form.getTravelRelocationDocument().setTaxSelectable(authorizer.canTaxSelectable(GlobalVariables.getUserSession().getPerson()));
    }

    @Override
    protected Class getMvcWrapperInterface() {
        return TravelRelocationMvcWrapperBean.class;
    }

    /**
     * Do initialization for a new {@link TravelRelocationDocument}
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        final TravelRelocationForm travelForm = (TravelRelocationForm) kualiDocumentFormBase;
        final TravelRelocationDocument document = (TravelRelocationDocument) travelForm.getDocument();
        getTravelRelocationService().addListenersTo(document);
    }

    protected void createDVDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Document doc = getDocumentService().getNewDocument(kualiDocumentFormBase.getDocTypeName());

        kualiDocumentFormBase.setDocument(doc);
        kualiDocumentFormBase.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentType());

        ((DisbursementVoucherDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

        // set wire charge message in form
        ((DisbursementVoucherForm) kualiDocumentFormBase).setWireChargeMessage(retrieveWireChargeMessage());
    }

    protected String retrieveWireChargeMessage() {
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.MESSAGE_DV_WIRE_CHARGE);
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        Object[] args = { wireCharge.getDomesticChargeAmt(), wireCharge.getForeignChargeAmt() };

        return MessageFormat.format(message, args);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.refresh(mapping, form, request, response);
    }

    protected TravelRelocationService getTravelRelocationService() {
        return SpringContext.getBean(TravelRelocationService.class);
    }

    protected DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    public PersonService getPersonService() {
        return SpringContext.getBean(PersonService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    protected KualiRuleService getRulesService() {
        return SpringContext.getBean(KualiRuleService.class);
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected ReportGenerationService getReportGenerationService() {
        return SpringContext.getBean(ReportGenerationService.class);
    }

    protected TravelReportService getTravelReportService() {
        return SpringContext.getBean(TravelReportService.class);
    }

    /**
     * Action method for adding an {@link ActualExpense} instance to the {@link TravelDocument}
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addActualExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.addActualExpenseLine(mapping, form, request, response);
        // recalculate(mapping, form, request, response);

        return retval;
    }

    /**
     * This method removes an other travel expense from this collection
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return the page to forward back to
     * @throws Exception
     */
    public ActionForward deleteActualExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.deleteActualExpenseLine(mapping, form, request, response);
        // recalculate(mapping, form, request, response);

        return retval;
    }

    /**
     * Recalculates the Expenses Total Tab
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return recalculateTripDetailTotal(mapping, form, request, response);
    }

    protected void refreshCollectionsFor(final TravelRelocationDocument relocation) {
        if (!relocation.getDocumentHeader().getWorkflowDocument().stateIsInitiated()) {
            debug("Refreshing objects in relocation");
            relocation.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            relocation.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            relocation.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            relocation.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    /**
     * Uses the {@link TravelRelocationService} to lookup a {@link TravelRelocationDocument} instance via its
     * <code>travelDocumentIdentifier</code>
     * 
     * @param travelDocumentIdentifier to location a {@link TravelRelocationDocument} with
     * @return {@link TravelRelocationDocument} instance
     */
    protected TravelRelocationDocument getTravelRelocation(final String travelDocumentIdentifier) {
        Collection<TravelRelocationDocument> reloList = getTravelRelocationService().findByIdentifier(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(reloList) && reloList.iterator().hasNext()) {
            return reloList.iterator().next();
        }
        
        return null;
    }

    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createREQSForVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
         * TravelRelocationForm reloForm = (TravelRelocationForm) form; TravelRelocationDocument relocation =
         * (TravelRelocationDocument) reloForm.getDocument(); getTravelRelocationService().createREQS(relocation); return
         * mapping.findForward(KFSConstants.MAPPING_BASIC);
         */

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Action method for creating a {@link ExpenseSummaryReport} and producing a PDF from it
     */
    public ActionForward viewExpenseSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final ExpenseSummaryReport report = getExpenseSummaryReportService().buildReport(relocation);
        report.setReportTitle("Moving and Relocation");

        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, PDF_MIME_TYPE, baos, "ExpenseSummary" + PDF_FILE_EXTENSION);
        
        return null;
    }

    /**
     * Action method for creating a {@link SummaryByDayReport} and producing a PDF from it.
     */
    public ActionForward viewSummaryByDay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final SummaryByDayReport report = getSummaryByDayReportService().buildReport(relocation);

        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, PDF_MIME_TYPE, baos, "SummaryByDay" + PDF_FILE_EXTENSION);
        
        return null;
    }

    public ActionForward printCoversheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            reloForm.setDocument(getTravelRelocationService().find(documentNumber));
        }
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();

        final Coversheet cover = getTravelRelocationService().generateCoversheetFor(relocation);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cover.print(stream);

        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", stream, String.format(COVERSHEET_FILENAME_FORMAT, relocation.getDocumentNumber()));

        return null;
    }

    /**
     * Action method for creating a {@link Non Employee forms} and producing a PDF from it
     */
    public ActionForward viewNonEmployeeForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final NonEmployeeCertificationReport report = getNonEmployeeCertificationReportService().buildReport(relocation);
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(relocation.getDocumentNumber()));
        File reportFile = getNonEmployeeCertificationReportService().generateReport(report);

        StringBuilder fileName = new StringBuilder();
        fileName.append(reloForm.getDocument().getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        displayPDF(request, response, reportFile, fileName);

        return null;
    }
    
    protected NonEmployeeCertificationReportService getNonEmployeeCertificationReportService() {
        return SpringContext.getBean(NonEmployeeCertificationReportService.class);
    }

    protected ExpenseSummaryReportService getExpenseSummaryReportService() {
        return SpringContext.getBean(ExpenseSummaryReportService.class);
    }

    protected SummaryByDayReportService getSummaryByDayReportService() {
        return SpringContext.getBean(SummaryByDayReportService.class);
    }

    @Override
    protected List<String> getCalculateIgnoreList() {
        return Arrays.asList(reloMethodToCallExclusionArray);
    }
}
