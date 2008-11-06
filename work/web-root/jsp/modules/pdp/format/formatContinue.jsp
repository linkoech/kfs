<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app"%>
<c:set var="disbursementNumberRangeAttributes"
	value="${DataDictionary.DisbursementNumberRange.attributes}" />
<c:set var="formatResultAttributes"
	value="${DataDictionary.FormatResult.attributes}" />
<c:set var="customerProfileAttributes"
	value="${DataDictionary.CustomerProfile.attributes}" />
<c:set var="dummyAttributes"
	value="${DataDictionary.AttributeReferenceDummy.attributes}" />

<kul:page headerTitle="Format Disbursement Summary"
	transactionalDocument="false" showDocumentInfo="false"
	htmlFormAction="pdp/formatrice" docTitle="Format Disbursement Summary">
	<html:hidden property="formatProcessSummary.processId" />
	<html:hidden property="paymentTypes" />
	
	<pdp:formatSelectedPayments
		disbursementNumberRangeAttributes="${disbursementNumberRangeAttributes}"
		customerProfileAttributes="${customerProfileAttributes}"
		formatResultAttributes="${formatResultAttributes}" />
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif"
			styleClass="globalbuttons" property="methodToCall.continueFormat"
			title="begin format" alt="begin format" />
		<html:image
			src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
			styleClass="globalbuttons" property="methodToCall.cancel"
			title="cancel" alt="cancel" />
	</div>

</kul:page>
