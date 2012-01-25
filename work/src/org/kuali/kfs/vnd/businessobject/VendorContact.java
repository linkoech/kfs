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

package org.kuali.kfs.vnd.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * Container for information about how to get in Contact with a person at a Vendor for a particular purpose.
 */
public class VendorContact extends PersistableBusinessObjectBase implements Inactivatable {

    protected Integer vendorContactGeneratedIdentifier;
    protected Integer vendorHeaderGeneratedIdentifier;
    protected Integer vendorDetailAssignedIdentifier;
    protected String vendorContactTypeCode;
    protected String vendorContactName;
    protected String vendorContactEmailAddress;
    protected String vendorContactCommentText;
    protected String vendorLine1Address;
    protected String vendorLine2Address;
    protected String vendorCityName;
    protected String vendorStateCode;
    protected String vendorZipCode;
    protected String vendorCountryCode;
    protected String vendorAttentionName;
    protected String vendorAddressInternationalProvinceName;
    protected boolean active;

    // These aren't persisted in db, only for lookup page
    protected String phoneNumberForLookup;
    protected String tollFreeForLookup;
    protected String faxForLookup;

    protected List<VendorContactPhoneNumber> vendorContactPhoneNumbers;

    protected VendorDetail vendorDetail;
    protected ContactType vendorContactType;
    protected StateEbo vendorState;
    protected CountryEbo vendorCountry;

    public VendorContact() {
        vendorContactPhoneNumbers = new ArrayList<VendorContactPhoneNumber>();
    }

    public Integer getVendorContactGeneratedIdentifier() {
        return vendorContactGeneratedIdentifier;
    }

    public void setVendorContactGeneratedIdentifier(Integer vendorContactGeneratedIdentifier) {
        this.vendorContactGeneratedIdentifier = vendorContactGeneratedIdentifier;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorContactTypeCode() {
        return vendorContactTypeCode;
    }

    public void setVendorContactTypeCode(String vendorContactTypeCode) {
        this.vendorContactTypeCode = vendorContactTypeCode;
    }

    public String getVendorContactName() {
        return vendorContactName;
    }

    public void setVendorContactName(String vendorContactName) {
        this.vendorContactName = vendorContactName;
    }

    public String getVendorContactEmailAddress() {
        return vendorContactEmailAddress;
    }

    public void setVendorContactEmailAddress(String vendorContactEmailAddress) {
        this.vendorContactEmailAddress = vendorContactEmailAddress;
    }

    public String getVendorContactCommentText() {
        return vendorContactCommentText;
    }

    public void setVendorContactCommentText(String vendorContactCommentText) {
        this.vendorContactCommentText = vendorContactCommentText;
    }

    public ContactType getVendorContactType() {
        return vendorContactType;
    }

    public void setVendorContactType(ContactType vendorContactType) {
        this.vendorContactType = vendorContactType;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getVendorAttentionName() {
        return vendorAttentionName;
    }

    public void setVendorAttentionName(String vendorAttentionName) {
        this.vendorAttentionName = vendorAttentionName;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }

    public String getVendorZipCode() {
        return vendorZipCode;
    }

    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    public CountryEbo getVendorCountry() {
        vendorCountry = (vendorCountryCode == null)?null:( vendorCountry == null || !StringUtils.equals( vendorCountry.getCode(),vendorCountryCode))?CountryEbo.from(SpringContext.getBean(CountryService.class).getCountry(vendorCountryCode)): vendorCountry;
        return vendorCountry;
    }

    public void setVendorCountry(CountryEbo vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public StateEbo getVendorState() {
        vendorState = (StringUtils.isBlank(vendorCountryCode) || StringUtils.isBlank( vendorStateCode))?null:( vendorState == null || !StringUtils.equals( vendorState.getCountryCode(),vendorCountryCode)|| !StringUtils.equals( vendorState.getCode(), vendorStateCode))?StateEbo.from(SpringContext.getBean(StateService.class).getState(vendorCountryCode, vendorStateCode)): vendorState;
        return vendorState;
    }

    public void setVendorState(StateEbo vendorState) {
        this.vendorState = vendorState;
    }

    public String getFaxForLookup() {
        return faxForLookup;
    }

    public void setFaxForLookup(String faxForLookup) {
        this.faxForLookup = faxForLookup;
    }

    public String getPhoneNumberForLookup() {
        return phoneNumberForLookup;
    }

    public void setPhoneNumberForLookup(String phoneNumberForLookup) {
        this.phoneNumberForLookup = phoneNumberForLookup;
    }

    public String getTollFreeForLookup() {
        return tollFreeForLookup;
    }

    public void setTollFreeForLookup(String tollFreeForLookup) {
        this.tollFreeForLookup = tollFreeForLookup;
    }

    public List<VendorContactPhoneNumber> getVendorContactPhoneNumbers() {
        return vendorContactPhoneNumbers;
    }

    public void setVendorContactPhoneNumbers(List<VendorContactPhoneNumber> vendorContactPhoneNumbers) {
        this.vendorContactPhoneNumbers = vendorContactPhoneNumbers;
    }

}
