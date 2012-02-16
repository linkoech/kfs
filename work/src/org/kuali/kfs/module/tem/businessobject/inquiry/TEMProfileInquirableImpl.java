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
package org.kuali.kfs.module.tem.businessobject.inquiry;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.FieldRestriction;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

public class TEMProfileInquirableImpl extends KfsInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TEMProfileInquirableImpl.class);

	/**
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.kns.bo.BusinessObject)
	 */
	@Override
	public List<Section> getSections(BusinessObject bo) {
		
		List<Section> sections = super.getSections(bo);
		Person currUser = GlobalVariables.getUserSession().getPerson();
        InquiryRestrictions inquiryRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService().getInquiryRestrictions(bo, currUser);
        
        // This is to allow users to view their own profile. If the principalId from the profile 
        // matches, the current user then fields are unmasked. Otherwise the other roles will handle the appropriate 
        // masking/unmasking of fields (see TemProfileOrganizationHierarchyRoleTypeServiceImpl).
        TEMProfile profile = (TEMProfile) bo;
        String principalId = profile.getPrincipalId();
        FieldRestriction restriction;
        	
        if (StringUtils.isNotBlank(principalId) && StringUtils.equalsIgnoreCase(currUser.getPrincipalId().trim(), principalId)) {
            for(Section section : sections) {
            	List<Row> rows = section.getRows();
            	for (Row row : rows) {
            		List<Field> rowFields = row.getFields();
            		for (Field field : rowFields) {
            			if(field.getFieldType().equalsIgnoreCase(Field.CONTAINER)) {
            				List<Row> containerRows = field.getContainerRows();
            				for(Row containerRow : containerRows) {
            					List<Field> containerRowFields = containerRow.getFields();
            					for (Field containerRowField : containerRowFields) {
            						restriction = inquiryRestrictions.getFieldRestriction(containerRowField.getPropertyName());
        	            			if(restriction.isMasked() || restriction.isPartiallyMasked()) {
        	            				containerRowField.setSecure(false);
        	            			}
            					}
            				}
            			}
            			restriction = inquiryRestrictions.getFieldRestriction(field.getPropertyName());
	            		if(restriction.isMasked() || restriction.isPartiallyMasked()) {
	            			field.setSecure(false);
	            		}
            		}
            	}
            }
        }

        return sections;
	}

}
