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
package org.kuali.kfs.module.cg.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.ProposalDao;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * @see ProposalDao
 */
public class ProposalDaoOjb extends PlatformAwareDaoBaseOjb implements ProposalDao {

    /**
     * @see org.kuali.kfs.module.cg.dataaccess.ProposalDao#getProposalsToClose(org.kuali.kfs.module.cg.businessobject.Close)
     */
    public Collection<Proposal> getProposalsToClose(ProposalAwardCloseDocument close) {

        Criteria criteria = new Criteria();
        criteria.addIsNull("proposalClosingDate");
        criteria.addLessOrEqualThan("proposalSubmissionDate", close.getCloseOnOrBeforeDate());

        return (Collection<Proposal>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Proposal.class, criteria));
    }

}
