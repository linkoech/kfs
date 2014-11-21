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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.krad.document.Copyable;

/**
 * This is the transactional document that is used to record a modification to the 
 * corpus value of the true endowment
 */
public class CorpusAdjustmentDocument extends EndowmentTransactionLinesDocumentBase implements Copyable, Correctable{

    public CorpusAdjustmentDocument() {
        super();
        setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.NON_CASH);
        this.setSourceTransactionLinesPrincipalCode();
        this.setTargetTransactionLinesPrincipalCode();        
        initializeSubType();
    }
    
    /**
     * This method will iterate through the decrease transactional lines and set the Income or Principal code to Principal (P).
     */
    protected void setSourceTransactionLinesPrincipalCode() {
        List<EndowmentTransactionLine> endowmentTransactionLines = this.getSourceTransactionLines();
        
        for (EndowmentTransactionLine endowmentTransactionLine : endowmentTransactionLines) {
            endowmentTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        }
    }

    /**
     * This method will iterate through the increase transactional lines and set the Income or Principal code to Principal (P).
     */
    protected void setTargetTransactionLinesPrincipalCode() {
        List<EndowmentTransactionLine> endowmentTransactionLines = this.getTargetTransactionLines();
        
        for (EndowmentTransactionLine endowmentTransactionLine : endowmentTransactionLines) {
            endowmentTransactionLine.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);            
        }
    }

    @Override
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceLines) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetLines) {
        // TODO Auto-generated method stub
        
    }
}
