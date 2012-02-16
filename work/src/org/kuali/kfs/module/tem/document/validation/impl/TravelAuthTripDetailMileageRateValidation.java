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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.DOCUMENT_DTL_TYPE;
import static org.kuali.kfs.module.tem.TemConstants.TravelParameters.ENABLE_PER_DIEM_CATEGORIES;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;

public class TravelAuthTripDetailMileageRateValidation extends GenericValidation {

    //@Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        boolean showMileage = false;
        
        TravelDocumentBase document = (TravelDocumentBase) event.getDocument();
        //check to see if mileage=Y in ENABLE_PER_DIEM_CATEGORIES param
        ParameterService paramService = SpringContext.getBean(ParameterService.class);
        List<String> perDiemCats = paramService.getParameterValues(PARAM_NAMESPACE, DOCUMENT_DTL_TYPE, ENABLE_PER_DIEM_CATEGORIES);
        for (String category : perDiemCats) {
            String[] pair = category.split("=");
            if (pair[0].equalsIgnoreCase(TemConstants.MILEAGE) && pair[1].equalsIgnoreCase(TemConstants.YES)) {
                showMileage = true;
            }
        }
        if (showMileage == true) {
            for (PerDiemExpense estimate : document.getPerDiemExpenses()) {
                if (estimate.getMileageRateId() == null) {
                    GlobalVariables.getMessageMap().putError("document.perDiemExpenses", TemKeyConstants.ERROR_TA_NO_MILEAGE_RATE);
                    rulePassed = false;
                }
            }
        }

        return rulePassed;
    }

}