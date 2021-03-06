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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class PaymentApplicationInvoiceApply implements Serializable {

    private CustomerInvoiceDocument invoice;
    private List<PaymentApplicationInvoiceDetailApply> detailApplications;
    
    private String payAppDocNumber;
    private boolean quickApply;
    private boolean quickApplyOldValue;
    
    public PaymentApplicationInvoiceApply(String payAppDocNumber, CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
        this.detailApplications = new ArrayList<PaymentApplicationInvoiceDetailApply>();
        for (CustomerInvoiceDetail invoiceDetail : invoice.getCustomerInvoiceDetailsWithoutDiscounts()) {
            this.detailApplications.add(new PaymentApplicationInvoiceDetailApply(payAppDocNumber, invoiceDetail));
        }
        this.quickApply = false;
        this.quickApplyOldValue = false;
        this.payAppDocNumber = payAppDocNumber;
    }

    public KualiDecimal getAmountToApply() {
        KualiDecimal applyAmount = KualiDecimal.ZERO;
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            applyAmount = applyAmount.add(detailApplication.getAmountApplied());
        }
        return applyAmount;
    }
    
    // yes this method name is awkward.  Blame JSP that expects an is* or get*
    public boolean isAnyAppliedAmounts() {
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            if (detailApplication.getAmountApplied().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
        }
        return false;
    }
    
    public List<PaymentApplicationInvoiceDetailApply> getDetailApplications() {
        return detailApplications;
    }
    
    public String getDocumentNumber() {
        return invoice.getDocumentNumber();
    }
    
    public KualiDecimal getOpenAmount() {
        return invoice.getOpenAmount();
    }
    
    public boolean isQuickApply() {
        return quickApply;
    }

    public void setQuickApply(boolean quickApply) {
        this.quickApplyOldValue = this.quickApply;
        this.quickApply = quickApply;
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            detailApplication.setInvoiceQuickApplied(quickApply);
        }
    }

    public boolean isQuickApplyChanged() {
        return quickApply != quickApplyOldValue;
    }
    
    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }
    
}
