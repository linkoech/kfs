/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.util.Iterator;
import java.util.Map;

import org.kuali.module.gl.service.AccountBalanceService;
import org.kuali.module.gl.dao.AccountBalanceDao;

/**
 * This class...
 * @author Bin Gao from Michigan State University
 */
public class AccountBalanceServiceImpl implements AccountBalanceService {
    AccountBalanceDao accountBalanceDao;

    /**
     * Sets the accountBalanceDao attribute value.
     * @param accountBalanceDao The accountBalanceDao to set.
     */
    public void setAccountBalanceDao(AccountBalanceDao accountBalanceDao) {
        this.accountBalanceDao = accountBalanceDao;
    }
    
    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAvailableAccountBalance(java.util.Map, boolean)
     */
    public Iterator findAvailableAccountBalance(Map fieldValues, boolean isConsolidated) {       
        return accountBalanceDao.findAvailableAccountBalance(fieldValues, isConsolidated);
    }
    
    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByConsolidation(java.util.Map, boolean, boolean)
     */
    public Iterator findAccountBalanceByConsolidation(Map fieldValues, boolean isCostShareInclusive, boolean isConsolidated) {
        return accountBalanceDao.findAccountBalanceByConsolidation(fieldValues, isCostShareInclusive, isConsolidated);
    }
    
    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByLevel(java.util.Map, boolean, boolean)
     */
    public Iterator findAccountBalanceByLevel(Map fieldValues, boolean isCostShareInclusive, boolean isConsolidated) {
        return accountBalanceDao.findAccountBalanceByLevel(fieldValues, isCostShareInclusive, isConsolidated);
    }

    /**
     * @see org.kuali.module.gl.service.AccountBalanceService#findAccountBalanceByObject(java.util.Map, boolean, boolean)
     */
    public Iterator findAccountBalanceByObject(Map fieldValues, boolean isCostShareInclusive, boolean isConsolidated) {
        return accountBalanceDao.findAccountBalanceByObject(fieldValues, isCostShareInclusive, isConsolidated);
    }    
}