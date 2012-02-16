/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.report.service;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface for travel reports.
 *
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
@Transactional
public interface TravelReportService {
    /**
     *
     * @param report
     * @param ByteArrayOutputStream
     */
    ByteArrayOutputStream buildReport(final ReportInfo report) throws Exception;
    
}
