/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class returns list of travel expense company value pairs.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class CompanyNameValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<TravelCompanyCode> boList = (List<TravelCompanyCode>) 
            getKeyValuesService().findAllOrderBy(TravelCompanyCode.class, "name", true);
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();
        keyValues.add(new KeyLabelPair("", ""));
        for (TravelCompanyCode element : boList) {
            if(element.isActive()) {
                keyValues.add(new KeyLabelPair(element.getName(), element.getName()));
            }
        }

        return keyValues;
    }

    protected KeyValuesService getKeyValuesService() {
        return SpringContext.getBean(KeyValuesService.class);
    }
}
