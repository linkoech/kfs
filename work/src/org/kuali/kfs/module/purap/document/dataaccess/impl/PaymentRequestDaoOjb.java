/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.dao.ojb;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class PaymentRequestDaoOjb extends PersistenceBrokerDaoSupport implements PaymentRequestDao {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDaoOjb.class);

    /**
     * 
     * @param paymentRequestDocument - a populated REQUISITION object to be saved
     * @throws IllegalObjectStateException
     * @throws ValidationErrorList
     */
    public void save(PaymentRequestDocument paymentRequestDocument) {
        getPersistenceBrokerTemplate().store(paymentRequestDocument);
    }

    public PaymentRequestDocument getPaymentRequestById(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);

        PaymentRequestDocument pReq = (PaymentRequestDocument) getPersistenceBrokerTemplate().getObjectByQuery(
            new QueryByCriteria(PaymentRequestDocument.class, criteria));
        if (pReq != null) {
            pReq.refreshAllReferences();
        }
        return pReq;
      }
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo( PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poDocId );

        List<PaymentRequestDocument> pReqs = (List<PaymentRequestDocument>)this.getPersistenceBrokerTemplate().getCollectionByQuery(
                new QueryByCriteria(PaymentRequestDocument.class, criteria));
 
        return pReqs; 
    }
}
