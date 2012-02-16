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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.MileageRate;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.module.tem.document.validation.event.AddActualExpenseLineEvent;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class TravelDocumentActualExpenseLineValidation extends TEMDocumentExpenseLineValidation {
    DictionaryValidationService dictionaryValidationService;
    
    public TravelDocumentActualExpenseLineValidation() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        final ActualExpense actualExpense = (ActualExpense) ((AddActualExpenseLineEvent<ActualExpense>) event).getExpenseLine();
        TravelDocument document = (TravelDocument) ((AddActualExpenseLineEvent<ActualExpense>) event).getDocument();

        success = getDictionaryValidationService().isBusinessObjectValid(actualExpense);
        
        if (success) {
            success = (validateGeneralRules(actualExpense, document) &&
                    validateAirfareRules(actualExpense, document) &&                  
                    validateLodgingRules(actualExpense, document) &&
                    validateLodgingAllowanceRules(actualExpense, document) &&
                    validatePerDiemRules(actualExpense, document) &&
                    validateMaximumAmountRules(actualExpense, document));
        }

        return success;
    }

    /**
     * This method validates following rules 1.Validates notes field if indicator set to true 2.Validates expense amount if expense
     * type is not mileage 3.Validates currency rate 4.Validates detail records total expense amount to see whether total exceeds
     * the parent record expense amount(if expense type is not mileage) 5.Validates duplicate entries
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateGeneralRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        // setting timestamp to date to match the comparison.
        /*
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = null;
        Date endDate = null;
        Date expenseDate = null;

        try {
            // strip time component
            if (document.getTripBegin() != null) {
                beginDate = sdf.parse(sdf.format(document.getTripBegin()));
            }
            if (document.getTripEnd() != null) {
                endDate = sdf.parse(sdf.format(document.getTripEnd()));
            }
            if (actualExpense.getExpenseDate() != null) {
                expenseDate = sdf.parse(sdf.format(actualExpense.getExpenseDate()));
            }
        }
        catch (ParseException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        if (endDate != null && beginDate != null && expenseDate != null && (expenseDate.before(beginDate) || expenseDate.after(endDate))) {
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_OTHER_EXP_DATE, TemKeyConstants.ERROR_EXPENSE_DATE_BEFORE_AFTER);
            success = false;
        }*/

        if (success) {
            if (ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getNoteRequired()) 
                    && expenseTypeCode.getNoteRequired() 
                    && (ObjectUtils.isNull(actualExpense.getDescription()) || actualExpense.getDescription().length() == 0)) {
                success = false;

                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, KFSKeyConstants.ERROR_REQUIRED, "Notes for expense type " + actualExpense.getTravelExpenseTypeCodeCode());
            }
            else if (!actualExpense.isMileage()) {
                success = actualExpense.getExpenseAmount().isGreaterThan(new KualiDecimal(0));
                List errorpath = GlobalVariables.getMessageMap().getErrorPath();
                if (!success) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Expense Amount");
                }
                else {
                    if (actualExpense.getCurrencyRate().isLessEqual(new KualiDecimal(0))) {
                        GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_REIMB_CUR_RATE, KFSKeyConstants.ERROR_ZERO_OR_NEGATIVE_AMOUNT, "Currency Rate");
                        success = false;
                    }
                    
                    else if (isDuplicateEntry(actualExpense, document)) {
                        success = false;
                        if (expenseTypeCode != null && expenseTypeCode.isPerDaily()) {
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY_DAILY, actualExpense.getTravelExpenseTypeCodeCode());
                        }
                        else {
                            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_DUPLICATE_ENTRY, actualExpense.getTravelExpenseTypeCodeCode(), actualExpense.getExpenseDate().toString());
                        }
                    }
                }
            }
        }

        return success;
    }

    /**
     * This method validates following rules 1.Validates user entered amount with max amount & max amount per configured in
     * database(daily & occurrence).
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateMaximumAmountRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        KualiDecimal maxAmount = getMaximumAmount(actualExpense, document);
        if (maxAmount.isNonZero() && maxAmount.subtract(getTotalExpenseAmount(actualExpense, document)).isNegative()) {
            if (expenseTypeCode.isPerDaily()) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_DAILY, expenseTypeCode.getMaximumAmount().toString());
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_AUTH_OTHER_EXP_AMT, TemKeyConstants.ERROR_ACTUAL_EXPENSE_MAX_AMT_PER_OCCU, expenseTypeCode.getMaximumAmount().toString());
            }
        }
        return success;
    }

    /**
     * This method validates following rules 1.Validates if lodging allowance is entered for the same day
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateLodgingRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;

        if (actualExpense.isLodging()) {
            if (isLodgingAllowanceEntered(actualExpense, document)) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_REIMB_CUR_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }
        }

        return success;
    }

    /**
     * This method validated following rules 1.Validates whether lodging entered for the same day
     * 
     * @param actualExpense
     * @param document
     * @return boolean
     */
    public boolean validateLodgingAllowanceRules(ActualExpense actualExpense, TravelDocument document) {
        boolean success = true;
        
        if (actualExpense.isLodgingAllowance()) {
            TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

            KualiDecimal maxAmount = ObjectUtils.isNotNull(expenseTypeCode) && ObjectUtils.isNotNull(expenseTypeCode.getMaximumAmount()) ? expenseTypeCode.getMaximumAmount() : KualiDecimal.ZERO;
            GlobalVariables.getMessageMap().putInfo(TemPropertyConstants.TEM_ACTUAL_EXPENSE_NOTCE, "info.document.tem.actualexpense.lodgingallowance", maxAmount.toString());


            if (isLodgingEntered(actualExpense, document)) {
                success = false;
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRVL_REIMB_CUR_RATE, TemKeyConstants.ERROR_ACTUAL_EXPENSE_LODGING_ENTERED);
            }
        }

        return success;
    }

    private Boolean isLodgingAllowanceEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodgingAllowance() 
                    && (!ote.equals(actualExpense))
                    && (ote.getExpenseDate() != null 
                            && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean isLodgingEntered(ActualExpense ote, TravelDocument document) {
        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if (actualExpense.isLodging()
                    && (!ote.equals(actualExpense))
                    && (ote.getExpenseDate() != null  
                            && ote.getExpenseDate().equals(actualExpense.getExpenseDate()))) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    private Boolean isDuplicateEntry(ActualExpense ote, TravelDocument document) {
        TemTravelExpenseTypeCode expenseTypeCode = ote.getTravelExpenseTypeCode();

        for (ActualExpense actualExpense : document.getActualExpenses()) {

            if (expenseTypeCode != null && expenseTypeCode.isPerDaily()) {
                if (actualExpense.getTravelExpenseTypeCodeCode().equals(ote.getTravelExpenseTypeCodeCode())) {
                    return Boolean.TRUE;
                }
            }
            else {
                if (ote.getExpenseDate() != null 
                        && !ote.equals(actualExpense)
                        && ote.getExpenseDate().equals(actualExpense.getExpenseDate()) 
                        && actualExpense.getTravelExpenseTypeCodeCode().equals(ote.getTravelExpenseTypeCodeCode())) {
                    return Boolean.TRUE;
                }
            }

        }

        return Boolean.FALSE;
    }

    private KualiDecimal getMaximumAmount(ActualExpense actualExpense, TravelDocument document) {
        KualiDecimal maxAmount = KualiDecimal.ZERO;
        TemTravelExpenseTypeCode expenseTypeCode = actualExpense.getTravelExpenseTypeCode();

        if (expenseTypeCode != null && expenseTypeCode.getMaximumAmount() != null) {
            if (expenseTypeCode.isPerDaily()) {
                if (document.getTripBegin() != null && document.getTripEnd() != null) {
                    double noOfDays = DateUtils.getDifferenceInDays(new Timestamp(document.getTripBegin().getTime()), new Timestamp(document.getTripEnd().getTime()));
                    // TODO: Need to implement multiply with no of travelers if group travel
                    maxAmount = expenseTypeCode.getMaximumAmount().multiply(new KualiDecimal(noOfDays));
                }
            }
            else if (expenseTypeCode.isPerOccurrence()) {
                // TODO: Need to implement multiply with no of travelers if group travel
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
            else {
                maxAmount = expenseTypeCode.getMaximumAmount();
            }
        }

        return maxAmount;
    }

    private KualiDecimal getTotalExpenseAmount(ActualExpense ote, TravelDocument document) {
        KualiDecimal totalExpenseAmount = KualiDecimal.ZERO;

        for (ActualExpense actualExpense : document.getActualExpenses()) {
            if ((!ote.equals(actualExpense)) && ote.getTravelExpenseTypeCodeCode().equals(actualExpense.getTravelExpenseTypeCodeCode())) {
                totalExpenseAmount = totalExpenseAmount.add(actualExpense.getExpenseAmount());
            }
        }

        return totalExpenseAmount.add(ote.getExpenseAmount());
    }
}
