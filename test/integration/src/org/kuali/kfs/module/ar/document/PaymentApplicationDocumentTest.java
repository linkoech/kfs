/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext(session = khuntley)
public class PaymentApplicationDocumentTest extends KualiTestBase {

    public void testCreatePaymentApplicationDocument() throws Exception {
        DocumentService service = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument document = (PaymentApplicationDocument) service.getNewDocument(PaymentApplicationDocument.class);
        document.getDocumentHeader().setDocumentDescription("Testing");
        service.saveDocument(document);
    }
    
}

