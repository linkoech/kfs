
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package org.kuali.kfs.module.external.kc.service.impl;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.businessobject.UnitDTO;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.businessobject.lookup.KualiUnitDTOLookupableHelperServiceImpl;
import org.kuali.kfs.module.external.kc.service.UnitService;
import org.kuali.kfs.module.external.kc.webService.institutionalUnitService.InstitutionalUnitService;
import org.kuali.kfs.module.external.kc.webService.institutionalUnitService.InstitutionalUnitSoapService;
import org.kuali.kfs.sys.KFSConstants;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 * 
 */
                      
public class UnitServiceImpl implements UnitService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UnitServiceImpl.class);
    private static final QName SERVICE_NAME = new QName("KC", KFSConstants.Research.KC_UNIT_SERVICE);
    private String wsdlLocation;
    private URL wsdlURL;
    
    public List<String> getParentUnits(String unitNumber) {
        InstitutionalUnitSoapService ss = new InstitutionalUnitSoapService(wsdlURL, SERVICE_NAME);
        InstitutionalUnitService port = ss.getInstitutionalUnitServicePort();  
        List<String> parentUnits  = port.getParentUnits(unitNumber);
        return parentUnits;
    }


    public ContractsAndGrantsUnit getUnit(String unitNumber) {
        InstitutionalUnitSoapService ss = new InstitutionalUnitSoapService(wsdlURL, SERVICE_NAME);
        InstitutionalUnitService port = ss.getInstitutionalUnitServicePort();  
        ContractsAndGrantsUnit unitDTO  = port.getUnit(unitNumber);
        return unitDTO;
    }


    public List<ContractsAndGrantsUnit> lookupUnits(Map <String,String>searchCriteria) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();

        for (String key : searchCriteria.keySet()) {
            String val = searchCriteria.get(key);
            if ( KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val); 
                hashMapList.add(hashMapElement);
            }
        }
        InstitutionalUnitSoapService ss = new InstitutionalUnitSoapService(wsdlURL, SERVICE_NAME);
        InstitutionalUnitService port = ss.getInstitutionalUnitServicePort();  
        List lookupUnitsReturn  = port.lookupUnits( hashMapList);
        return lookupUnitsReturn;
    }


    public String getWsdlLocation() {
        return wsdlLocation;
    }



    public void setWsdlLocation(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
        try {
            wsdlURL = new URL(wsdlLocation);
        }
        catch (MalformedURLException ex) {
          
            LOG.error(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            //ex.printStackTrace();
        }

    }

}
