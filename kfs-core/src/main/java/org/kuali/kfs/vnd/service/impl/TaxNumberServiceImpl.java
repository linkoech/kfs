/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.vnd.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.service.TaxNumberService;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

public class TaxNumberServiceImpl implements TaxNumberService {

    public ParameterService parameterService;


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public static List<String> taxNumberFormats;
    public static List<String> feinNumberFormats;
    public static List<String> notAllowedTaxNumbers;


    public String formatToDefaultFormat(String taxNbr) throws FormatException {
        String digits = taxNbr.replaceAll("\\D", "");

        Integer defaultTaxNumberDigits = new Integer(parameterService.getParameterValueAsString(VendorDetail.class, VendorParameterConstants.DEFAULT_TAX_NUMBER_DIGITS));

        if (digits.length() < defaultTaxNumberDigits) {
            throw new FormatException("Tax number has fewer than " + defaultTaxNumberDigits + " digits.", KFSKeyConstants.ERROR_CUSTOM, taxNbr);
        }
        else if (digits.length() > defaultTaxNumberDigits) {
            throw new FormatException("Tax number has more than " + defaultTaxNumberDigits + " digits.", KFSKeyConstants.ERROR_CUSTOM, taxNbr);
        }
        else {
            return digits;
        }
    }

    /**
     * A predicate to determine if a String field is all numbers
     * 
     * @param field A String tax number
     * @return True if String is numeric
     */
    public boolean isStringAllNumbers(String field) {
        if (!isStringEmpty(field)) {
            field = field.trim();
            for (int x = 0; x < field.length(); x++) {
                char c = field.charAt(x);
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * A predicate to determine if a String field is null or empty
     * 
     * @param field A String tax number
     * @return True if String is null or empty
     */
    public boolean isStringEmpty(String field) {
        if (field == null || field.equals("")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * A predicate to determine the validity of tax numbers We're using regular expressions stored in the business rules table to
     * validate whether the tax number is in the correct format. The regular expressions are : (please update this javadoc comment
     * when the regular expressions change) 1. For SSN : (?!000)(?!666)(\d{3})([ \-]?)(?!00)(\d{2})([\-]?)(?!0000)(\d{4}) 2. For
     * FEIN : (?!00)(\d{3})([ \-]?)(\d{2})([\-]?)(?!0000)(\d{4})
     * 
     * @param taxNbr A tax number String (SSN or FEIN)
     * @param taxType determines SSN or FEIN tax number type
     * @return True if the tax number is known to be in a valid format
     */
    public boolean isValidTaxNumber(String taxNbr, String taxType) {
        String[] ssnFormats = parseSSNFormats();
        String[] feinFormats = parseFEINFormats();
        Integer defaultTaxNumberDigits = new Integer(parameterService.getParameterValueAsString(VendorDetail.class, "DEFAULT_TAX_NUMBER_DIGITS"));

        if (taxNbr.length() != defaultTaxNumberDigits || !isStringAllNumbers(taxNbr)) {
            return false;
        }

        if (taxType.equals(VendorConstants.TAX_TYPE_SSN)) {

            for (int i = 0; i < ssnFormats.length; i++) {
                if (taxNbr.matches(ssnFormats[i])) {
                    return true;
                }
            }
            return false;
        }
        else if (taxType.equals(VendorConstants.TAX_TYPE_FEIN)) {
            for (int i = 0; i < feinFormats.length; i++) {
                if (taxNbr.matches(feinFormats[i])) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }


    /**
     * Someday we'll have to use the rules table instead of using constants. This method will return true if the tax number is an
     * allowed tax number and return false if it's not allowed.
     * 
     * @param taxNbr The tax number to be processed.
     * @return boolean true if the tax number is allowed and false otherwise.
     */
    public boolean isAllowedTaxNumber(String taxNbr) {
        String[] notAllowedTaxNumbers = parseNotAllowedTaxNumbers();
        for (int i = 0; i < notAllowedTaxNumbers.length; i++) {
            if (taxNbr.matches(notAllowedTaxNumbers[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits the set of tax number formats which are returned from the rule service as a semicolon-delimeted String into a String
     * array.
     * 
     * @return A String array of the tax number format regular expressions.
     */
    public String[] parseSSNFormats() {
        if (ObjectUtils.isNull(taxNumberFormats)) {
            taxNumberFormats = new ArrayList<String>( parameterService.getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.TAX_SSN_NUMBER_FORMATS) );
        }
        return taxNumberFormats.toArray(new String[] {});
    }

    /**
     * Splits the set of tax fein number formats which are returned from the rule service as a semicolon-delimeted String into a
     * String array.
     * 
     * @return A String array of the tax fein number format regular expressions.
     */
    public String[] parseFEINFormats() {
        if (ObjectUtils.isNull(feinNumberFormats)) {
            feinNumberFormats = new ArrayList<String>( parameterService.getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.TAX_FEIN_NUMBER_FORMATS) );
        }
        return feinNumberFormats.toArray(new String[] {});
    }

    /**
     * Splits the set of not allowed tax number formats which are returned from the rule service as a semicolon-delimeted String
     * into a String array.
     * 
     * @return A String array of the not allowed tax number format regular expressions.
     */
    public String[] parseNotAllowedTaxNumbers() {
        if (ObjectUtils.isNull(notAllowedTaxNumbers)) {
            notAllowedTaxNumbers = new ArrayList<String>( parameterService.getParameterValuesAsString(VendorDetail.class, VendorParameterConstants.NOT_ALLOWED_TAX_NUMBERS) );
        }
        return notAllowedTaxNumbers.toArray(new String[] {});
    }

}
