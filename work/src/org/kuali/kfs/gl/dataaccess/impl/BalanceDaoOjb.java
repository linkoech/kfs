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
package org.kuali.module.gl.dao.ojb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.util.OJBUtility;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: BalanceDaoOjb.java,v 1.41 2006-09-01 15:11:14 bgao Exp $
 */
public class BalanceDaoOjb extends PersistenceBrokerDaoSupport implements BalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);
    private KualiConfigurationService kualiConfigurationService;

    public BalanceDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getGlSummary(int, java.util.List)
     */
    public Iterator getGlSummary(int universityFiscalYear, List<String> balanceTypeCodes) {
        LOG.debug("getGlSummary() started");

        Criteria c = new Criteria();
        c.addEqualTo("universityFiscalYear", universityFiscalYear);
        c.addIn("balanceTypeCode", balanceTypeCodes);

        String[] attributes = new String[] { "account.subFundGroup.fundGroupCode", "sum(accountLineAnnualBalanceAmount)", "sum(beginningBalanceLineAmount)", "sum(contractsGrantsBeginningBalanceAmount)", "sum(month1Amount)", "sum(month2Amount)", "sum(month3Amount)", "sum(month4Amount)", "sum(month5Amount)", "sum(month6Amount)", "sum(month7Amount)", "sum(month8Amount)", "sum(month9Amount)", "sum(month10Amount)", "sum(month11Amount)", "sum(month12Amount)", "sum(month13Amount)" };

        String[] groupby = new String[] { "account.subFundGroup.fundGroupCode" };

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, c);

        query.setAttributes(attributes);
        query.addGroupBy(groupby);
        query.addOrderByAscending("account.subFundGroup.fundGroupCode");

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<Balance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");
        
        Criteria c = new Criteria();
        c.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        
        QueryByCriteria query = QueryFactory.newQuery(Balance.class, c);
        query.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(PropertyConstants.OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.SUB_OBJECT_CODE);
        query.addOrderByAscending(PropertyConstants.BALANCE_TYPE_CODE);
        query.addOrderByAscending(PropertyConstants.OBJECT_TYPE_CODE);
        
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#save(org.kuali.module.gl.bo.Balance)
     */
    public void save(Balance b) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(b);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.dao.BalanceDao#getBalanceByTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public Balance getBalanceByTransaction(Transaction t) {
        LOG.debug("getBalanceByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(PropertyConstants.OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * This method adds to the given criteria if the given collection is non-empty. It uses an EQUALS if there is exactly one
     * element in the collection; otherwise, its uses an IN
     * 
     * @param criteria - the criteria that might have a criterion appended
     * @param name - name of the attribute
     * @param collection - the collection to inspect
     * 
     */
    private void criteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, false);
    }

    /**
     * Similar to criteriaBuilder, this adds a negative criterion (NOT EQUALS, NOT IN)
     * 
     */
    private void negatedCriteriaBuilder(Criteria criteria, String name, Collection collection) {
        criteriaBuilderHelper(criteria, name, collection, true);
    }


    /**
     * This method provides the implementation for the conveniences methods criteriaBuilder & negatedCriteriaBuilder
     * 
     * @param negate - the criterion will be negated (NOT EQUALS, NOT IN) when this is true
     * 
     */
    private void criteriaBuilderHelper(Criteria criteria, String name, Collection collection, boolean negate) {
        if (collection != null) {
            int size = collection.size();
            if (size == 1) {
                if (negate) {
                    criteria.addNotEqualTo(name, collection.iterator().next());
                }
                else {
                    criteria.addEqualTo(name, collection.iterator().next());
                }
            }
            if (size > 1) {
                if (negate) {
                    criteria.addNotIn(name, collection);
                }
                else {
                    criteria.addIn(name, collection);

                }
            }
        }

    }

    public Iterator<Balance> findBalances(Account account, Integer fiscalYear, Collection includedObjectCodes, Collection excludedObjectCodes, Collection objectTypeCodes, Collection balanceTypeCodes) {
        LOG.debug("findBalances() started");

        Criteria criteria = new Criteria();

        criteria.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        criteria.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());

        criteria.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        criteriaBuilder(criteria, "FIN_OBJ_TYP_CD", objectTypeCodes);
        criteriaBuilder(criteria, "FIN_BALANCE_TYP_CD", balanceTypeCodes);
        criteriaBuilder(criteria, "FIN_OBJECT_CD", includedObjectCodes);
        negatedCriteriaBuilder(criteria, "FIN_OBJECT_CD", excludedObjectCodes);

        ReportQueryByCriteria query = new ReportQueryByCriteria(Balance.class, criteria);

        // returns an iterator of all matching balances
        Iterator balances = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return balances;
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findCashBalance(java.util.Map, boolean)
     */
    public Iterator<Balance> findCashBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findCashBalance() started");

        Query query = this.getCashBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);       
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }
    
    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getDetailedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getDetailedCashBalanceRecordCount() started");

        Query query = this.getCashBalanceQuery(fieldValues, false);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCashBalanceRecordSize(java.util.Map, boolean)
     */
    public Iterator getConsolidatedCashBalanceRecordCount(Map fieldValues) {
        LOG.debug("getCashBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getCashBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<Balance> findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    private ReportQueryByCriteria getCashBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        criteria.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE, "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", PropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        List groupByList = buildGroupByList();
        groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(PropertyConstants.SUB_OBJECT_CODE);
        groupByList.remove(PropertyConstants.OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        return query;
    }

    // build the query for cash balance search
    private Query getCashBalanceQuery(Map fieldValues, boolean isConsolidated) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        criteria.addEqualTo(PropertyConstants.BALANCE_TYPE_CODE, "AC");
        criteria.addEqualToField("chart.financialCashObjectCode", PropertyConstants.OBJECT_CODE);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);
        List attributeList = buildAttributeList(false);
        List groupByList = buildGroupByList();

        // if consolidated, then ignore the following fields
        if (isConsolidated) {
            attributeList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(PropertyConstants.SUB_OBJECT_CODE);
            groupByList.remove(PropertyConstants.SUB_OBJECT_CODE);
            attributeList.remove(PropertyConstants.OBJECT_TYPE_CODE);
            groupByList.remove(PropertyConstants.OBJECT_TYPE_CODE);
        }
        
        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        
        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        return query;
    }

    // build the query for balance search
    private Query getBalanceQuery(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceQuery(Map, boolean) started");

        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);
        
        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            List attributeList = buildAttributeList(true);
            List groupByList = buildGroupByList();

            // ignore subaccount number, sub object code and object type code
            attributeList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
            attributeList.remove(PropertyConstants.SUB_OBJECT_CODE);
            groupByList.remove(PropertyConstants.SUB_OBJECT_CODE);
            attributeList.remove(PropertyConstants.OBJECT_TYPE_CODE);
            groupByList.remove(PropertyConstants.OBJECT_TYPE_CODE);

            // set the selection attributes
            String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
            query.setAttributes(attributes);

            // add the group criteria into the selection statement
            String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
            query.addGroupBy(groupBy);
        }
        
        return query;
    }

    // build the query for balance search
    private ReportQueryByCriteria getBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new Balance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(Balance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });
        
        List groupByList = buildGroupByList();
        groupByList.remove(PropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(PropertyConstants.SUB_OBJECT_CODE);
        groupByList.remove(PropertyConstants.OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        return query;
    }

    /**
     * This method builds the query criteria based on the input field map
     * 
     * @param fieldValues
     * @param balance
     * @return a query criteria
     */
    private Criteria buildCriteriaFromMap(Map fieldValues, Balance balance) {
        Criteria criteria = new Criteria();
        
        // handle encumbrance balance type
        String propertyName = PropertyConstants.BALANCE_TYPE_CODE;
        if(fieldValues.containsKey(propertyName)){    
            String propertyValue = (String) fieldValues.get(propertyName);        
            if (Constants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                fieldValues.remove(PropertyConstants.BALANCE_TYPE_CODE);
                criteria.addIn(PropertyConstants.BALANCE_TYPE_CODE, this.getEncumbranceBalanceTypeCodeList());
            }
        }
        
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new Balance()));
        return criteria;
    }
    
    private List<String> getEncumbranceBalanceTypeCodeList() {         
        String[] balanceTypesAsArray = 
            kualiConfigurationService.getApplicationParameterValues(
                    "Kuali.GeneralLedger.AvailableBalanceInquiry", 
                    "GeneralLedger.BalanceInquiry.AvailableBalances.EncumbranceDrillDownBalanceTypes");
        return Arrays.asList(balanceTypesAsArray);
    }

    /**
     * This method builds the atrribute list used by balance searching
     * 
     * @param isExtended
     * @return List an attribute list
     */
    private List<String> buildAttributeList(boolean isExtended) {
        List attributeList = this.buildGroupByList();

        attributeList.add("sum(accountLineAnnualBalanceAmount)");
        attributeList.add("sum(beginningBalanceLineAmount)");
        attributeList.add("sum(contractsGrantsBeginningBalanceAmount)");

        // add the entended elements into the list
        if (isExtended) {
            attributeList.add("sum(month1Amount)");
            attributeList.add("sum(month2Amount)");
            attributeList.add("sum(month3Amount)");
            attributeList.add("sum(month4Amount)");
            attributeList.add("sum(month5Amount)");
            attributeList.add("sum(month6Amount)");
            attributeList.add("sum(month7Amount)");
            attributeList.add("sum(month8Amount)");
            attributeList.add("sum(month9Amount)");
            attributeList.add("sum(month10Amount)");
            attributeList.add("sum(month11Amount)");
            attributeList.add("sum(month12Amount)");
            attributeList.add("sum(month13Amount)");
        }
        return attributeList;
    }

    /**
     * This method builds group by attribute list used by balance searching
     * 
     * @return List an group by attribute list
     */
    private List<String> buildGroupByList() {
        List attributeList = new ArrayList();

        attributeList.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(PropertyConstants.ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        attributeList.add(PropertyConstants.BALANCE_TYPE_CODE);
        attributeList.add(PropertyConstants.OBJECT_CODE);
        attributeList.add(PropertyConstants.SUB_OBJECT_CODE);
        attributeList.add(PropertyConstants.OBJECT_TYPE_CODE);

        return attributeList;
    }

    public Balance getBalanceByPrimaryId(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("getBalanceByPrimaryId() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(SufficientFundBalances.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }


    /**
     * @see org.kuali.module.gl.dao.BalanceDao#getCurrentBudgetForObjectCode(java.lang.Integer, java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public Balance getCurrentBudgetForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String objectCode) {
        LOG.debug("getCurrentBudgetForObjectCode() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);
        crit.addEqualTo("objectCode", objectCode);
        crit.addEqualTo("balanceTypeCode", Constants.BALANCE_TYPE_CURRENT_BUDGET);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        return (Balance) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Find all matching account balances.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @return balances sorted by object code
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber) {
        LOG.debug("findAccountBalances() started");
        return this.findAccountBalances(universityFiscalYear, chartOfAccountsCode, accountNumber, Constants.SF_TYPE_OBJECT);
    }

    /**
     * Find all matching account balances. The Sufficient funds code is used to determine the sort of the results.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param sfCode
     * @return
     */
    public Iterator<Balance> findAccountBalances(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String sfCode) {
        LOG.debug("findAccountBalances() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);
        crit.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        crit.addEqualTo("accountNumber", accountNumber);

        QueryByCriteria qbc = QueryFactory.newQuery(Balance.class, crit);
        if (Constants.SF_TYPE_OBJECT.equals(sfCode)) {
            qbc.addOrderByAscending("objectCode");
        }
        else if (Constants.SF_TYPE_LEVEL.equals(sfCode)) {
            qbc.addOrderByAscending("financialObject.financialObjectLevel.financialObjectLevelCode");
        }
        else if (Constants.SF_TYPE_CONSOLIDATION.equals(sfCode)) {
            qbc.addOrderByAscending("financialObject.financialObjectLevel.financialConsolidationObject.finConsolidationObjectCode");
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }


    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addLessThan("universityFiscalYear", new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Balance.class, criteria));

        // This is required because if any deleted account balances are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }
    
    /**
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
    
}
