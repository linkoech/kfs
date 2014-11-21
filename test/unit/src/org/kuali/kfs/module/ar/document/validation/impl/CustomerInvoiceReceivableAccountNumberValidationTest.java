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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;


@ConfigureContext(session = khuntley)
public class CustomerInvoiceReceivableAccountNumberValidationTest extends KualiTestBase {

    private CustomerInvoiceReceivableAccountNumberValidation validation;
    private final static String VALID_ACCOUNT_NUMBER = "1031400";
    private final static String VALID_CHART_OF_ACCOUNTS = "BL";
    private final static String INVALID_ACCOUNT_NUMBER = "XXXXXXX";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceReceivableAccountNumberValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }

    public void testValidReceivableAccountNumber_True() {
        validation.getCustomerInvoiceDocument().setPaymentAccountNumber(VALID_ACCOUNT_NUMBER);
        validation.getCustomerInvoiceDocument().setPaymentChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS);

        assertTrue(validation.validate(null));
    }

    public void testValidReceivableAccountNumber_False() {
        validation.getCustomerInvoiceDocument().setPaymentAccountNumber(INVALID_ACCOUNT_NUMBER);
        validation.getCustomerInvoiceDocument().setPaymentChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS);

        assertFalse(validation.validate(null));
    }

}

