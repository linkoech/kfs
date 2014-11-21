<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Lookup and Maintenance" />
<div class="body">
	<c:if test="${ConfigProperties.module.capital.asset.enabled == 'true'}">
	    <strong>Capital Asset Builder</strong><br />
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Pre-Asset Tagging" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cab.businessobject.Pretag&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>
	    <strong>Capital Asset Management</strong><br />
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Asset" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.Asset&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Fabrication" url="kr/maintenance.do?maintenanceAction=New&methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetFabrication" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Global (Add)" url="kr/lookup.do?businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType&conversionFields=acquisitionTypeCode:acquisitionTypeCode&returnLocation=portal.do&docFormKey=88888888" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Location Global" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetLocationGlobal" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Payment" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Asset Retirement Global" url="kr/lookup.do?businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetRetirementReason&conversionFields=retirementReasonCode:retirementReasonCode&returnLocation=portal.do&docFormKey=88888888" /></li>
		</ul>
	</c:if>
    <strong>Chart of Accounts</strong><br />
    <ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.Account&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Account Global" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountGlobal" /></li>	
	    <li><portal:portalLink displayTitle="true" title="Account Delegate" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountDelegate&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Account Delegate Global" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountDelegateGlobal" /></li>
	    <li><portal:portalLink displayTitle="true" title="Account Delegate Model" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountDelegateModel&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Account Delegate Global From Model" url="kr/lookup.do?businessObjectClassName=org.kuali.kfs.coa.businessobject.AccountDelegateModel&conversionFields=chartOfAccountsCode:modelChartOfAccountsCode,organizationCode:modelOrganizationCode,accountDelegateModelName:modelName&returnLocation=portal.do&docFormKey=88888888" /></li>
	    <li><portal:portalLink displayTitle="true" title="Object Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Object Code Global" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ObjectCodeGlobal" /></li>
	    <li><portal:portalLink displayTitle="true" title="Organization" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.Organization&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Organization Review" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.identity.OrgReviewRole&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Project Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.ProjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Sub-Account" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SubAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Sub-Object Code" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SubObjectCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    <li><portal:portalLink displayTitle="true" title="Sub-Object Code Global" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.SubObjectCodeGlobal" /></li>
	</ul>
	<c:choose>
 	<c:when test="${ConfigProperties['module.external.kuali.coeus.enabled'] == 'true'}"> 
    </c:when>
    <c:when test="${ConfigProperties['module.contracts.and.grants.enabled'] == 'true'}">
	    <strong>Contracts & Grants</strong><br />
		<ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Award" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.Award&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Proposal" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.Proposal&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>
    </c:when>
    </c:choose>
	<c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">
    	<strong>Endowment</strong><br />
		<ul class="chan">
		    <li><portal:portalLink displayTitle="true" title="KEMID" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.endow.businessobject.KEMID&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Recurring Cash Transfer" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Security" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.endow.businessobject.Security&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		    <li><portal:portalLink displayTitle="true" title="Tickler" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.endow.businessobject.Tickler&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>
	</c:if>		
    <strong>Financial Processing</strong><br />
	<ul class="chan">
	    <li><portal:portalLink displayTitle="true" title="Disbursement Voucher Travel Company" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.TravelCompanyCode&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	<c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
	    <strong>Travel</strong><br/>
	    <ul class="chan">
	    	<li><portal:portalLink displayTitle="true" title="Corporate Card Application" url="temCorporateCardApplication.do?methodToCall=checkExistingCard&docTypeName=CCAP" /></li>
	        <li><portal:portalLink displayTitle="true" title="Credit Card Imported Expense Clearing Document" url="kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.CreditCardImportedExpenseClearingObject" /></li>	
	    	<li><portal:portalLink displayTitle="true" title="Credit Card Staging Data" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.CreditCardStagingData&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	    	<li><portal:portalLink displayTitle="true" title="CTS Card Application" url="temCTSCardApplication.do?methodToCall=checkExistingCard&docTypeName=CTAP" /></li>
	        <li><portal:portalLink displayTitle="true" title="TEM Cardholder Lookup" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.TemProfileAccount&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	        <li><portal:portalLink displayTitle="true" title="TEM Profile" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.tem.businessobject.TemProfile&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	        </ul>
	</c:if>	
    <strong>Vendor</strong><br />
		<ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Vendor" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.vnd.businessobject.VendorDetail&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Contracts" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.vnd.businessobject.VendorContract&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
	</ul>
	
    </div>
<channel:portalChannelBottom />
