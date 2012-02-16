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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataValidation;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypes;
import org.kuali.kfs.module.tem.TemConstants.ReconciledCodes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class ExpenseImportByTripServiceImpl extends ExpenseImportServiceBase implements ExpenseImportByTripService  {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExpenseImportByTripServiceImpl.class);
    
    private TravelAuthorizationService travelAuthorizationService;
    private TemProfileService temProfileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TravelExpenseService travelExpenseService;

    List<String> errorMessages=new ArrayList<String>();

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean areMandatoryFieldsPresent(AgencyStagingData agencyData) {

        boolean requiredFieldsValid = true;
        
        if (StringUtils.isEmpty(agencyData.getTripId())) {
            LOG.error("Agency Data Missing Required Field: tripId");
            requiredFieldsValid = false;
        }
        if (isAccountingInfoMissing(agencyData)) {
            requiredFieldsValid = false;
        }
        if (isAmountEmpty(agencyData.getTripExpenseAmount())) {
            LOG.error("Agency Data Missing Required Field: tripExpenseAmount. tripId: " + agencyData.getTripId());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getTripInvoiceNumber())) {
            LOG.error("Agency Data Missing Required Field: tripInvoiceNumber. tripId: " + agencyData.getTripId());
            requiredFieldsValid = false;
        }
        if (ObjectUtils.isNull(agencyData.getTransactionPostingDate())) {
            LOG.error("Agency Data Missing Required Field: transactionPostingDate. tripId: " + agencyData.getTripId());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getAlternateTripId())) {
            LOG.error("Agency Data Missing Required Field: alternateTripId. tripId: " + agencyData.getTripId());
            requiredFieldsValid = false;
        }
        if (isTripDataMissing(agencyData)) {
            LOG.error("Agency Data Missing Trip Data. tripId: " + agencyData.getTripId());
            requiredFieldsValid = false;
        }
        
        if (!requiredFieldsValid) {
            LOG.error("Missing one or more required fields.");
            errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS);
        }
        
        return requiredFieldsValid;
    }

    /**
     * 
     * This method loops through the {@link TripAccountingInformation} and 
     * verifies that each one has a Chart Code and Account Number.
     * @param agencyData
     * @return
     */
    public boolean isAccountingInfoMissing(AgencyStagingData agencyData) {
        
        boolean accountInfoMissing = false;
        List<TripAccountingInformation> accountingInfoList = agencyData.getTripAccountingInformation();
        for (TripAccountingInformation account : accountingInfoList) {
            if (StringUtils.isEmpty(account.getTripChartCode())) {
                LOG.error("Agency Data Missing Required Field: tripChartCode. tripId: " + agencyData.getTripId());
                errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_COA);
                accountInfoMissing = true;
                 
            }
            if (StringUtils.isEmpty(account.getTripAccountNumber())) {
               LOG.error("Agency Data Missing Required Field: tripAccountNumber. tripId: " + agencyData.getTripId());
               errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_ACCOUNT_NUM);
               accountInfoMissing = true;
                
            }
        }
        
        return accountInfoMissing;
    }

    /**
     * 
     * This method verifies there is either Airfare, Lodging or Rental Car data in the Agency Data
     * @param agencyData
     * @return
     */
    public boolean isTripDataMissing(AgencyStagingData agencyData) {

        if (StringUtils.isEmpty(agencyData.getAirTicketNumber()) && 
            StringUtils.isEmpty(agencyData.getLodgingItineraryNumber()) && 
            StringUtils.isEmpty(agencyData.getRentalCarItineraryNumber())) {
            LOG.error("Agency Data Missing Air Ticket Number or Lodging Itinerary Number or Rental Car Itinerary Number. tripId: " + agencyData.getTripId());
            errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_MISSING_TRIP_DATA);
            
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public AgencyStagingData validateAgencyData(AgencyStagingData agencyData) {
        
        LOG.info("Validating agency data.");
        
        errorMessages.clear();

        // Perform a duplicate check first. 
        if (isDuplicate(agencyData)) {
            LOG.error("Duplicate Agency Data record. tripId: " + agencyData.getTripId());
            return null;
        }
        
        // see if there's a TA for trip id (travelDocumentIdentifier)
        TravelAuthorizationDocument ta = validateTripId(agencyData);
        if (ObjectUtils.isNotNull(ta)) {

            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

            TEMProfile profile = temProfileService.findTemProfileById(ta.getProfileId());
            agencyData.setTemProfileId(profile.getProfileId());
            agencyData = validateAccountingInfo(profile, agencyData, ta);
        }
        
        if (!isCreditCardAgencyValid(agencyData)) {
            errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_CCA);
        }
        
        LOG.info("Finished validating agency data. tripId:" + agencyData.getTripId());
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        return agencyData;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateTripId(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public TravelAuthorizationDocument validateTripId(AgencyStagingData agencyData) {
        
        TravelAuthorizationDocument ta = getTravelAuthorizationDocument(agencyData.getTripId());
        if (ObjectUtils.isNotNull(ta)) {
            return ta;
        }
        LOG.error("Unable to retrieve TA document for tripId: " + agencyData.getTripId());
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID);
        errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID);
        
        return null;
    }
    
    /**
     * 
     * This method gets the TA by travel document identifier
     * @param travelDocumentIdentifier
     * @return
     */
    protected TravelAuthorizationDocument getTravelAuthorizationDocument(String travelDocumentIdentifier) {
        List<TravelAuthorizationDocument> taDocs = (List<TravelAuthorizationDocument>) travelAuthorizationService.find(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(taDocs) && taDocs.size() == 1) {
            return taDocs.get(0);
        }
        return null;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public AgencyStagingData validateAccountingInfo(TEMProfile profile, AgencyStagingData agencyData, TravelAuthorizationDocument ta) {

        // Get VALIDATION_ACCOUNTING_LINE parameter to determine which fields to validate
        List<String> validationParams = getParameterValues(TravelParameters.VALIDATION_ACCOUNTING_LINE, AgencyStagingDataValidation.AGENCY_DATA_VALIDATION_DTL);
        if (ObjectUtils.isNull(validationParams)) {
            return agencyData;
        }
        
        List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
        
        for (TripAccountingInformation account : accountingInfo) {
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
                if (!isAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber())) {
                    
                    LOG.error("Invalid Account in Agency Data record. tripId: " + agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber());
                    account.setTripAccountNumber(profile.getDefaultAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
                    errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM);
                }
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
                // sub account is optional
                if (StringUtils.isNotEmpty(account.getTripSubAccountNumber()) && 
                    !isSubAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getTripSubAccountNumber())) {
                    
                    LOG.error("Invalid SubAccount in Agency Data record. tripId: " + agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber() + " subaccount: " + account.getTripSubAccountNumber());
                    errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT);
                    account.setTripSubAccountNumber(profile.getDefaultSubAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
                }
            }
            
            // project code is optional
            if (StringUtils.isNotEmpty(account.getProjectCode()) &&
                !isProjectCodeValid(account.getProjectCode())) {
                
                LOG.error("Invalid Project in Agency Data record. tripId: " + agencyData.getTripId() + " project code: " + account.getProjectCode());
                errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE);
                account.setProjectCode(profile.getDefaultProjectCode());
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_OBJECT_CODE)) {
                // object code is optional
                if (StringUtils.isNotEmpty(account.getObjectCode()) &&
                    !isObjectCodeValid(account.getTripChartCode(), account.getObjectCode())) {
                    
                    LOG.error("Invalid Object Code in Agency Data record. tripId: " + agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode());
                    errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_OBJECT_CODE);
                    account.setObjectCode(getObjectCode(agencyData));
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT);
                }
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBOBJECT_CODE)) {
                // sub object code is optional
                if (StringUtils.isNotEmpty(account.getSubObjectCode()) && 
                    !isSubObjectCodeValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode())) {
                    
                    LOG.error("Invalid SubObject Code in Agency Data record. tripId: " + agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode() + " subobject code: " + account.getSubObjectCode());
                    errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUB_OBJECT_CODE);
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT);
                }
            }
        }
                
        return agencyData;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#isDuplicate(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean isDuplicate(AgencyStagingData agencyData) {
        
        // Verify that this isn't a duplicate entry based on the following: 
        // Trip ID, Agency Name, Transaction Date, Transaction Amount
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        if (StringUtils.isNotEmpty(agencyData.getTripId())) {
            fieldValues.put(TemPropertyConstants.TRIP_ID, agencyData.getTripId());
        }
        if (StringUtils.isNotEmpty(agencyData.getAgency())) {
            fieldValues.put(TemPropertyConstants.AGENCY, agencyData.getAgency());
        }
        if (ObjectUtils.isNotNull(agencyData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyData.getTripExpenseAmount());
        }
                
        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
        if (ObjectUtils.isNull(agencyDataList) || agencyDataList.size() == 0) {
            return false;
        }
        LOG.error("Found a duplicate entry for agencyData with tripId: " + agencyData.getTripId() + " matching agency id: " + agencyDataList.get(0).getId());
        //grab the id of the record that this is a dupe of (just the first one if multiple
        Integer dupeId = agencyDataList.get(0).getId();
        agencyData.setDuplicateRecordId(dupeId);
        errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_DUPLICATE_RECORD);
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#reconciliateExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("Reconciling expense for agency data: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
        
        String expenseType = agencyData.getExpenseType();
        
        // If there's no object code, this param will is set by expense type and used to lookup the expense type and get an object code based on it.
        String expenseTypeParamCode = null;
        
        // This is the "match process" - see if there's credit card data that matches the agency data
        CreditCardStagingData ccData = null;
        if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.AIRFARE)) {
            // see if there's a CC that matches ticket number, service fee number, amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getAirTicketNumber(), agencyData.getAirServiceFeeNumber());   
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_AIRFARE;
        }
        else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.LODGING)) {
            // see if there's a CC that matches lodging itinerary number and amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getLodgingItineraryNumber());
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_LODGING;
        }
        else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.RENTAL_CAR)) {
            // see if there's a CC that matches rental car itinerary number and amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getRentalCarItineraryNumber());            
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_RENTAL_CAR;
        }
        
        if (ObjectUtils.isNotNull(ccData)) {
            LOG.info("Found a match for Agency: " + agencyData.getId() + " Credit Card: " + ccData.getId() + " tripId: " + agencyData.getTripId());
            HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData, ccData);
            expense.setReconciled(ReconciledCodes.AUTO_RECONCILED);
            AgencyServiceFee serviceFee = getAgencyServiceFee(agencyData.getDistributionCode());
            
            List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
            List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
            
            // Need to split up the amounts if there are multiple accounts
            KualiDecimal remainingAmount = agencyData.getTripExpenseAmount();
            KualiDecimal numAccounts = new KualiDecimal(accountingInfo.size());
            KualiDecimal currentAmount = agencyData.getTripExpenseAmount().divide(numAccounts);
            KualiDecimal remainingFeeAmount = new KualiDecimal(0);
            KualiDecimal currentFeeAmount = new KualiDecimal(0);
            if (ObjectUtils.isNotNull(serviceFee)) {
                remainingFeeAmount = serviceFee.getServiceFee();
                currentFeeAmount = serviceFee.getServiceFee().divide(numAccounts);
            }
            
            String creditObjectCode = getParameter(AgencyMatchProcessParameter.AP_CLEARING_CTS_PAYMENT_OBJECT_CODE, AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE);

            boolean allGlpesCreated = true;

            for (int i = 0; i < accountingInfo.size(); i++) {
                TripAccountingInformation info = accountingInfo.get(i);
                
                // If its the last account, use the remainingAmount to resolve rounding
                if (i < accountingInfo.size() - 1) {
                    remainingAmount = remainingAmount.subtract(currentAmount);
                    remainingFeeAmount = remainingFeeAmount.subtract(currentFeeAmount);
                }
                else {
                    currentAmount = remainingAmount;
                    currentFeeAmount = remainingFeeAmount;
                }
                
                String objectCode = info.getObjectCode();
                if (StringUtils.isEmpty(objectCode)) {
                    objectCode = lookupObjectCode(expenseTypeParamCode, agencyData.getTripId());
                }

                // set the amount on the accounting info for documents pulling in imported expenses
                info.setAmount(currentAmount);
                
                if (ObjectUtils.isNotNull(serviceFee)) {
                    // Agency Data has a DI Code that maps to an Agency Service Fee, create GLPEs for it.
                    GeneralLedgerPendingEntry debitServiceFeeGlpe = buildDebitServiceFeeGLPE(agencyData, info, sequenceHelper, objectCode, serviceFee, currentFeeAmount);
                    if (ObjectUtils.isNull(debitServiceFeeGlpe)) {
                        LOG.error("Failed to create a debit Service Fee GLPE for agency: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                        allGlpesCreated = false;
                    }
                    else {
                        entries.add(debitServiceFeeGlpe);
                        LOG.debug("Created debit service fee GLPE: " + debitServiceFeeGlpe.getDocumentNumber() + " for agency imported expense: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                    }
                    
                    GeneralLedgerPendingEntry creditServiceFeeGlpe = buildCreditServiceFeeGLPE(agencyData, info, sequenceHelper, serviceFee, currentFeeAmount);
                    if (ObjectUtils.isNull(debitServiceFeeGlpe)) {
                        LOG.error("Failed to create a credit Service Fee GLPE for agency: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                        allGlpesCreated = false;
                    }
                    else {
                        entries.add(creditServiceFeeGlpe);
                        LOG.debug("Created credit service fee GLPE: " + creditServiceFeeGlpe.getDocumentNumber() + " for agency imported expense: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                    }
                }
                
                GeneralLedgerPendingEntry debitGlpe = buildDebitGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, objectCode, currentAmount);
                if (ObjectUtils.isNull(debitGlpe)) {
                    LOG.error("Failed to create a debit GLPE for agency: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                    allGlpesCreated = false;
                }
                else {
                    entries.add(debitGlpe);
                    LOG.debug("Created debit GLPE: " + debitGlpe.getDocumentNumber() + " for agency imported expense: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                }
                           
                GeneralLedgerPendingEntry creditGlpe = buildCreditGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, creditObjectCode, currentAmount);
                if (ObjectUtils.isNull(creditGlpe)) {
                    LOG.error("Failed to create a credit GLPE for agency: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                    allGlpesCreated = false;
                }
                else {
                    entries.add(creditGlpe);
                    LOG.debug("Created credit GLPE: " + creditGlpe.getDocumentNumber() + " for agency imported expense: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
                }
            }
            
            if (entries.size() > 0 && allGlpesCreated) {
                businessObjectService.save(entries);
                businessObjectService.save(expense);
                ccData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_MOVED_TO_HISTORICAL);
                businessObjectService.save(ccData);
                agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);
            }
            else {
                LOG.error("An errror occured while creating GLPEs for agency: " + agencyData.getId() + " tripId"+ agencyData.getTripId() + " Not creating historical expense for this agency record.");
                return false;
            }
            
        }
        else {
            LOG.info("No match found for agency data: " + agencyData.getId() + " tripId:" + agencyData.getTripId());
        }
        
        LOG.info("Finished reconciling expense for agency data: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
        return true;
    }

    /**
     * 
     * This method looks up the parameter, and gets the object code from the associated {@link TravelExpenseTypeExtension}
     * @param expenseTypeParamCode
     * @param travelDocumentIdentifier
     * @return
     */
    protected String lookupObjectCode(String expenseTypeParamCode, String travelDocumentIdentifier) {
        
        // get the expense type parameter
        String expenseTypeCode = getParameter(expenseTypeParamCode, TravelParameters.DOCUMENT_DTL_TYPE);
        if (StringUtils.isNotEmpty(expenseTypeCode)) {
        
            // Need to get the Doc Type, Traveler Type and Trip Type from the TA
            // Note the tripId has already been validated at this point, so the TA should be there.
            TravelAuthorizationDocument ta = getTravelAuthorizationDocument(travelDocumentIdentifier);
            if (ObjectUtils.isNotNull(ta)) {
                TemTravelExpenseTypeCode travelExenseType = travelExpenseService.getExpenseType(expenseTypeCode, ta.getDocumentTypeName(), ta.getTripTypeCode(), ta.getTraveler().getTravelerTypeCode());
                return travelExenseType.getFinancialObjectCode();
            }
        }
        return null;
    }
    
    /**
     * 
     * This method returns the {@link AgencyServiceFee} by distribution code (diCode).
     * @param distributionCode
     * @return
     */
    private AgencyServiceFee getAgencyServiceFee(String distributionCode) {
        if (StringUtils.isNotEmpty(distributionCode)) {
            Map<String,String> criteria = new HashMap<String,String>(1);
            criteria.put(TemPropertyConstants.DISTRIBUTION_CODE, distributionCode);
            List<AgencyServiceFee> serviceFee = (List<AgencyServiceFee>) getBusinessObjectService().findMatching(AgencyServiceFee.class, criteria);
            if (ObjectUtils.isNotNull(serviceFee) && serviceFee.size() > 0) {
                return serviceFee.get(0);
            }
        }
        return null;
    }

    /**
     * 
     * This method creates a debit General Ledger Pending Entry for the Service Fee.
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param objectCode
     * @param serviceFee
     * @param amount
     * @return
     */
    protected GeneralLedgerPendingEntry buildDebitServiceFeeGLPE(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String objectCode, AgencyServiceFee serviceFee, KualiDecimal amount) {
        GeneralLedgerPendingEntry glpe = buildDebitGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, objectCode, amount);
        return glpe;
    }
    
    /**
     * 
     * This method creates a credit General Ledger Pending Entry for the Service Fee.
     * @param agencyData
     * @param info
     * @param sequenceHelper
     * @param serviceFee
     * @param amount
     * @return
     */
    protected GeneralLedgerPendingEntry buildCreditServiceFeeGLPE(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AgencyServiceFee serviceFee, KualiDecimal amount) {
        GeneralLedgerPendingEntry glpe = buildCreditGeneralLedgerPendingEntry(agencyData, info, sequenceHelper,serviceFee.getCreditObjectCode(), amount);
        glpe.setChartOfAccountsCode(serviceFee.getCreditChartCode());
        glpe.setAccountNumber(serviceFee.getCreditAccountNumber());
        glpe.setFinancialObjectCode(serviceFee.getCreditObjectCode());
       return glpe;
    }
    
    /**
     * Gets the travelAuthorizationService attribute. 
     * @return Returns the travelAuthorizationService.
     */
    public TravelAuthorizationService getTravelAuthorizationService() {
        return travelAuthorizationService;
    }

    /**
     * Sets the travelAuthorizationService attribute value.
     * @param travelAuthorizationService The travelAuthorizationService to set.
     */
    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }

    /**
     * Gets the temProfileService attribute. 
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the travelExpenseService attribute. 
     * @return Returns the travelExpenseService.
     */
    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    /**
     * Sets the travelExpenseService attribute value.
     * @param travelExpenseService The travelExpenseService to set.
     */
    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

}
