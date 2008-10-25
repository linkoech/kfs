/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.AlternateOrgReviewRouting;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * See functional documentation.
 */
public class Proposal extends PersistableBusinessObjectBase implements AlternateOrgReviewRouting, Inactivateable, ContractAndGrantsProposal {

    private Long proposalNumber;
    private Date proposalBeginningDate;
    private Date proposalEndingDate;

    /**
     * This field is for write-only to the database via OJB, not the corresponding property of this BO. OJB uses reflection to read
     * it, so the compiler warns because it doesn't know.
     * 
     * @see #getProposalTotalAmount
     * @see #setProposalTotalAmount
     */
    @SuppressWarnings( { "unused" })
    private KualiDecimal proposalTotalAmount;

    private KualiDecimal proposalDirectCostAmount;
    private KualiDecimal proposalIndirectCostAmount;
    private Date proposalRejectedDate;
    private Date proposalLastUpdateDate;
    private Date proposalDueDate;
    private KualiDecimal proposalTotalProjectAmount;
    private Date proposalSubmissionDate;
    private boolean proposalFederalPassThroughIndicator;
    private String oldProposalNumber;
    private String grantNumber;
    private Date proposalClosingDate;
    private String proposalAwardTypeCode;
    private String agencyNumber;
    private String proposalStatusCode;
    private String federalPassThroughAgencyNumber;
    private String cfdaNumber;
    private String proposalFellowName;
    private String proposalPurposeCode;
    private String proposalProjectTitle;
    private boolean active;
    private List<ProposalSubcontractor> proposalSubcontractors;
    private List<ProposalOrganization> proposalOrganizations;
    private List<ProposalProjectDirector> proposalProjectDirectors;
    private List<ProposalResearchRisk> proposalResearchRisks;

    private ProposalAwardType proposalAwardType;
    private Agency agency;
    private ProposalStatus proposalStatus;
    private Agency federalPassThroughAgency;
    private ProposalPurpose proposalPurpose;
    private Cfda cfda;
    private ProposalOrganization primaryProposalOrganization;
    private String routingOrg;
    private String routingChart;
    private LookupService lookupService;
    private Award award;

    /**
     * Default constructor.
     */
    @SuppressWarnings( { "unchecked" })
    public Proposal() {
        // Must use TypedArrayList because its get() method automatically grows
        // the array for Struts.
        proposalSubcontractors = new TypedArrayList(ProposalSubcontractor.class);
        proposalOrganizations = new TypedArrayList(ProposalOrganization.class);
        proposalProjectDirectors = new TypedArrayList(ProposalProjectDirector.class);
        proposalResearchRisks = new TypedArrayList(ProposalResearchRisk.class);
    }

    /**
     * Gets the award awarded to a proposal instance.
     * 
     * @return the award corresponding to a proposal instance if the proposal has been awarded.
     */
    public Award getAward() {
        return award;
    }

    /**
     * Sets the award awarding a proposal instance.
     * 
     * @param award the award awarding a proposal instance
     */
    public void setAward(Award award) {
        this.award = award;
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getProposalSubcontractors());
        managedLists.add(getProposalOrganizations());
        managedLists.add(getProposalProjectDirectors());
        // research risks cannot be deleted (nor added)
        return managedLists;
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the proposalBeginningDate attribute.
     * 
     * @return Returns the proposalBeginningDate
     */
    public Date getProposalBeginningDate() {
        return proposalBeginningDate;
    }

    /**
     * Sets the proposalBeginningDate attribute.
     * 
     * @param proposalBeginningDate The proposalBeginningDate to set.
     */
    public void setProposalBeginningDate(Date proposalBeginningDate) {
        this.proposalBeginningDate = proposalBeginningDate;
    }

    /**
     * Gets the proposalEndingDate attribute.
     * 
     * @return Returns the proposalEndingDate
     */
    public Date getProposalEndingDate() {
        return proposalEndingDate;
    }

    /**
     * Sets the proposalEndingDate attribute.
     * 
     * @param proposalEndingDate The proposalEndingDate to set.
     */
    public void setProposalEndingDate(Date proposalEndingDate) {
        this.proposalEndingDate = proposalEndingDate;
    }

    /**
     * Gets the proposalTotalAmount attribute.
     * 
     * @return Returns the proposalTotalAmount
     */
    public KualiDecimal getProposalTotalAmount() {
        KualiDecimal direct = getProposalDirectCostAmount();
        KualiDecimal indirect = getProposalIndirectCostAmount();
        return ObjectUtils.isNull(direct) || ObjectUtils.isNull(indirect) ? null : direct.add(indirect);
    }

    /**
     * Does nothing. This property is determined by the direct and indirect cost amounts. This setter is here only because without
     * it, the maintenance framework won't display this attribute.
     * 
     * @param proposalTotalAmount The proposalTotalAmount to set.
     */
    public void setProposalTotalAmount(KualiDecimal proposalTotalAmount) {
        // do nothing
    }

    /**
     * OJB calls this method as the first operation before this BO is inserted into the database. The database contains
     * CGPRPSL_TOT_AMT, a denormalized column that Kuali does not use but needs to maintain with this method because OJB bypasses
     * the getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException
     */
    @Override
    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeInsert(persistenceBroker);
        proposalTotalAmount = getProposalTotalAmount();
    }

    /**
     * OJB calls this method as the first operation before this BO is updated to the database. The database contains
     * CGPRPSL_TOT_AMT, a denormalized column that Kuali does not use but needs to maintain with this method because OJB bypasses
     * the getter.
     * 
     * @param persistenceBroker from OJB
     * @throws PersistenceBrokerException
     */
    @Override
    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        super.beforeUpdate(persistenceBroker);
        proposalTotalAmount = getProposalTotalAmount();
    }

    /**
     * Gets the proposalDirectCostAmount attribute.
     * 
     * @return Returns the proposalDirectCostAmount
     */
    public KualiDecimal getProposalDirectCostAmount() {
        return proposalDirectCostAmount;
    }

    /**
     * Sets the proposalDirectCostAmount attribute.
     * 
     * @param proposalDirectCostAmount The proposalDirectCostAmount to set.
     */
    public void setProposalDirectCostAmount(KualiDecimal proposalDirectCostAmount) {
        this.proposalDirectCostAmount = proposalDirectCostAmount;
    }

    /**
     * Gets the proposalIndirectCostAmount attribute.
     * 
     * @return Returns the proposalIndirectCostAmount
     */
    public KualiDecimal getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }

    /**
     * Sets the proposalIndirectCostAmount attribute.
     * 
     * @param proposalIndirectCostAmount The proposalIndirectCostAmount to set.
     */
    public void setProposalIndirectCostAmount(KualiDecimal proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }

    /**
     * Gets the proposalRejectedDate attribute.
     * 
     * @return Returns the proposalRejectedDate
     */
    public Date getProposalRejectedDate() {
        return proposalRejectedDate;
    }

    /**
     * Sets the proposalRejectedDate attribute.
     * 
     * @param proposalRejectedDate The proposalRejectedDate to set.
     */
    public void setProposalRejectedDate(Date proposalRejectedDate) {
        this.proposalRejectedDate = proposalRejectedDate;
    }

    /**
     * Gets the proposalLastUpdateDate attribute.
     * 
     * @return Returns the proposalLastUpdateDate
     */
    public Date getProposalLastUpdateDate() {
        return proposalLastUpdateDate;
    }

    /**
     * Sets the proposalLastUpdateDate attribute.
     * 
     * @param proposalLastUpdateDate The proposalLastUpdateDate to set.
     */
    public void setProposalLastUpdateDate(Date proposalLastUpdateDate) {
        this.proposalLastUpdateDate = proposalLastUpdateDate;
    }

    /**
     * Gets the proposalDueDate attribute.
     * 
     * @return Returns the proposalDueDate
     */
    public Date getProposalDueDate() {
        return proposalDueDate;
    }

    /**
     * Sets the proposalDueDate attribute.
     * 
     * @param proposalDueDate The proposalDueDate to set.
     */
    public void setProposalDueDate(Date proposalDueDate) {
        this.proposalDueDate = proposalDueDate;
    }

    /**
     * Gets the proposalTotalProjectAmount attribute.
     * 
     * @return Returns the proposalTotalProjectAmount
     */
    public KualiDecimal getProposalTotalProjectAmount() {
        return proposalTotalProjectAmount;
    }

    /**
     * Sets the proposalTotalProjectAmount attribute.
     * 
     * @param proposalTotalProjectAmount The proposalTotalProjectAmount to set.
     */
    public void setProposalTotalProjectAmount(KualiDecimal proposalTotalProjectAmount) {
        this.proposalTotalProjectAmount = proposalTotalProjectAmount;
    }

    /**
     * Gets the proposalSubmissionDate attribute.
     * 
     * @return Returns the proposalSubmissionDate
     */
    public Date getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }

    /**
     * Sets the proposalSubmissionDate attribute.
     * 
     * @param proposalSubmissionDate The proposalSubmissionDate to set.
     */
    public void setProposalSubmissionDate(Date proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
    }

    /**
     * Gets the proposalFederalPassThroughIndicator attribute.
     * 
     * @return Returns the proposalFederalPassThroughIndicator
     */
    public boolean getProposalFederalPassThroughIndicator() {
        return proposalFederalPassThroughIndicator;
    }

    /**
     * Sets the proposalFederalPassThroughIndicator attribute.
     * 
     * @param proposalFederalPassThroughIndicator The proposalFederalPassThroughIndicator to set.
     */
    public void setProposalFederalPassThroughIndicator(boolean proposalFederalPassThroughIndicator) {
        this.proposalFederalPassThroughIndicator = proposalFederalPassThroughIndicator;
    }

    /**
     * Gets the oldProposalNumber attribute.
     * 
     * @return Returns the oldProposalNumber
     */
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    /**
     * Sets the oldProposalNumber attribute.
     * 
     * @param oldProposalNumber The oldProposalNumber to set.
     */
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }

    /**
     * Gets the grantNumber attribute.
     * 
     * @return Returns the grantNumber
     */
    public String getGrantNumber() {
        return grantNumber;
    }

    /**
     * Sets the grantNumber attribute.
     * 
     * @param grantNumber The grantNumber to set.
     */
    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    /**
     * Gets the proposalClosingDate attribute.
     * 
     * @return Returns the proposalClosingDate
     */
    public Date getProposalClosingDate() {
        return proposalClosingDate;
    }

    /**
     * Sets the proposalClosingDate attribute.
     * 
     * @param proposalClosingDate The proposalClosingDate to set.
     */
    public void setProposalClosingDate(Date proposalClosingDate) {
        this.proposalClosingDate = proposalClosingDate;
    }

    /**
     * Gets the proposalAwardTypeCode attribute.
     * 
     * @return Returns the proposalAwardTypeCode
     */
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * Sets the proposalAwardTypeCode attribute.
     * 
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the proposalStatusCode attribute.
     * 
     * @return Returns the proposalStatusCode
     */
    public String getProposalStatusCode() {
        return proposalStatusCode;
    }

    /**
     * Sets the proposalStatusCode attribute.
     * 
     * @param proposalStatusCode The proposalStatusCode to set.
     */
    public void setProposalStatusCode(String proposalStatusCode) {
        this.proposalStatusCode = proposalStatusCode;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber
     */
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute.
     * 
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    /**
     * Gets the cfdaNumber attribute.
     * 
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     * 
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    /**
     * Gets the proposalFellowName attribute.
     * 
     * @return Returns the proposalFellowName
     */
    public String getProposalFellowName() {
        return proposalFellowName;
    }

    /**
     * Sets the proposalFellowName attribute.
     * 
     * @param proposalFellowName The proposalFellowName to set.
     */
    public void setProposalFellowName(String proposalFellowName) {
        this.proposalFellowName = proposalFellowName;
    }

    /**
     * Gets the proposalPurposeCode attribute.
     * 
     * @return Returns the proposalPurposeCode
     */
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * Sets the proposalPurposeCode attribute.
     * 
     * @param proposalPurposeCode The proposalPurposeCode to set.
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }

    /**
     * Gets the proposalProjectTitle attribute.
     * 
     * @return Returns the proposalProjectTitle
     */
    public String getProposalProjectTitle() {
        return proposalProjectTitle;
    }

    /**
     * Sets the proposalProjectTitle attribute.
     * 
     * @param proposalProjectTitle The proposalProjectTitle to set.
     */
    public void setProposalProjectTitle(String proposalProjectTitle) {
        this.proposalProjectTitle = proposalProjectTitle;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the {@link ProposalAwardType} attribute.
     * 
     * @return Returns the {@link ProposalAwardType}
     */
    public ProposalAwardType getProposalAwardType() {
        return proposalAwardType;
    }

    /**
     * Sets the {@link ProposalAwardType} attribute.
     * 
     * @param proposalAwardType The {@link ProposalAwardType} to set.
     * @deprecated
     */
    public void setProposalAwardType(ProposalAwardType proposalAwardType) {
        this.proposalAwardType = proposalAwardType;
    }

    /**
     * Gets the {@link Agency} attribute.
     * 
     * @return Returns the {@link Agency}
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the {@link Agency} attribute.
     * 
     * @param agency The {@link Agency} to set.
     * @deprecated
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * Gets the {@link ProposalStatus} attribute.
     * 
     * @return Returns the {@link ProposalStatus}
     */
    public ProposalStatus getProposalStatus() {
        return proposalStatus;
    }

    /**
     * Sets the {@link ProposalStatus} attribute.
     * 
     * @param proposalStatus The {@link ProposalStatus} to set.
     * @deprecated
     */
    public void setProposalStatus(ProposalStatus proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThrough {@link Agency} attribute.
     * 
     * @param federalPassThroughAgency The federalPassThrough {@link Agency} to set.
     * @deprecated
     */
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the {@link ProposalPurpose} attribute.
     * 
     * @return Returns the proposalPurpose
     */
    public ProposalPurpose getProposalPurpose() {
        return proposalPurpose;
    }

    /**
     * Sets the {@link ProposalPurpose} attribute.
     * 
     * @param proposalPurpose The {@link ProposalPurpose} to set.
     * @deprecated
     */
    public void setProposalPurpose(ProposalPurpose proposalPurpose) {
        this.proposalPurpose = proposalPurpose;
    }

    /**
     * Gets the {@link Cfda} attribute.
     * 
     * @return Returns the {@link Cfda}
     */
    public Cfda getCfda() {
        return cfda;
    }

    /**
     * Sets the {@link Cfda} attribute.
     * 
     * @param cfda The {@link Cfda} to set.
     * @deprecated
     */
    public void setCfda(Cfda cfda) {
        this.cfda = cfda;
    }

    /**
     * Gets the {@link List} of {@link ProposalSubcontractor}s associated with a {@link Proposal} instance.
     * 
     * @return Returns the proposalSubcontractors list
     */
    public List<ProposalSubcontractor> getProposalSubcontractors() {
        return proposalSubcontractors;
    }

    /**
     * Sets the {@link ProposalSubcontractor}s {@link List}.
     * 
     * @param proposalSubcontractors The {@link ProposalSubcontractor}s {@link List} to set.
     */
    public void setProposalSubcontractors(List<ProposalSubcontractor> proposalSubcontractors) {
        this.proposalSubcontractors = proposalSubcontractors;
    }

    /**
     * Gets the {@link List} of {@link ProposalOrganization}s associated with a {@link Proposal} instance.
     * 
     * @return Returns the {@link ProposalOrganization}s.
     */
    public List<ProposalOrganization> getProposalOrganizations() {
        return proposalOrganizations;
    }

    /**
     * @param proposalOrganizations The proposalOrganizations to set.
     */
    public void setProposalOrganizations(List<ProposalOrganization> proposalOrganizations) {
        this.proposalOrganizations = proposalOrganizations;
    }

    /**
     * @return Returns the proposalProjectDirectors.
     */
    public List<ProposalProjectDirector> getProposalProjectDirectors() {
        return proposalProjectDirectors;
    }

    /**
     * @param proposalProjectDirectors The proposalProjectDirectors to set.
     */
    public void setProposalProjectDirectors(List<ProposalProjectDirector> proposalProjectDirectors) {
        this.proposalProjectDirectors = proposalProjectDirectors;
    }

    /**
     * @return Returns the proposalResearchRisks.
     */
    public List<ProposalResearchRisk> getProposalResearchRisks() {
        return proposalResearchRisks;
    }

    /**
     * @param proposalResearchRisks The proposalResearchRisks to set.
     */
    public void setProposalResearchRisks(List<ProposalResearchRisk> proposalResearchRisks) {
        this.proposalResearchRisks = proposalResearchRisks;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }

    /** Dummy value used to facilitate lookups */
    private transient String lookupPersonUniversalIdentifier;
    private transient Person lookupPerson;

    /**
     * Gets the lookup {@link Person}.
     * 
     * @return the lookup {@link Person}
     */
    public Person getLookupPerson() {
        return lookupPerson;
    }

    /**
     * Sets the lookup {@link Person}
     * 
     * @param lookupPerson
     */
    public void setLookupPerson(Person lookupPerson) {
        this.lookupPerson = lookupPerson;
    }

    /**
     * Gets the universal user id of the lookup person.
     * 
     * @return the id of the lookup person
     */
    public String getLookupPersonUniversalIdentifier() {
        lookupPerson = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(lookupPersonUniversalIdentifier, lookupPerson);
        return lookupPersonUniversalIdentifier;
    }

    /**
     * Sets the universal user id of the lookup person
     * 
     * @param lookupPersonId the id of the lookup person
     */
    public void setLookupPersonUniversalIdentifier(String lookupPersonId) {
        this.lookupPersonUniversalIdentifier = lookupPersonId;
    }

    /**
     * I added this getter to the BO to resolve KULCG-300. I'm not sure if this is actually needed by the code, but the framework
     * breaks all lookups on the proposal maintenance doc without this getter.
     * 
     * @return the {@link LookupService} used by the instance.
     */
    public LookupService getLookupService() {
        return lookupService;
    }


    /**
     * Gets the id of the routing {@link Chart}
     * 
     * @return the id of the routing {@link Chart}
     */
    public String getRoutingChart() {
        return routingChart;
    }

    /**
     * Sets the id of the routing {@link Chart}.
     * 
     * @return the id of the routing {@link Chart}.
     */
    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }

    /**
     * Gets the id of the routing {@link Org}.
     * 
     * @return the id of the routing {@link Org}
     */
    public String getRoutingOrg() {
        return routingOrg;
    }

    /**
     * Sets the id of the routing {@link Org}.
     * 
     * @param the id of the routing {@link Org}
     */
    public void setRoutingOrg(String routingOrg) {
        this.routingOrg = routingOrg;
    }

    /**
     * Gets the primary {@link ProposalOrganization} for a proposal.
     * 
     * @return the primary {@link ProposalOrganization} for a proposal
     */
    public ProposalOrganization getPrimaryProposalOrganization() {
        for (ProposalOrganization po : proposalOrganizations) {
            if (po != null && po.isProposalPrimaryOrganizationIndicator()) {
                setPrimaryProposalOrganization(po);
                break;
            }
        }

        return primaryProposalOrganization;
    }

    /**
     * Sets the {@link LookupService}. For Spring compatibility.
     * 
     * @param lookupService
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Sets the primary {@link ProposalOrganization} for a proposal
     * 
     * @param primaryProposalOrganization
     */
    public void setPrimaryProposalOrganization(ProposalOrganization primaryProposalOrganization) {
        this.primaryProposalOrganization = primaryProposalOrganization;
        this.routingChart = primaryProposalOrganization.getChartOfAccountsCode();
        this.routingOrg = primaryProposalOrganization.getOrganizationCode();
    }

}

