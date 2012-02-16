/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.kfs.module.tem.test.infrastructure.lifecycle;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.*;

import org.kuali.kfs.sys.context.*;

import org.kuali.kfs.module.tem.test.infrastructure.JettyServer;

import static org.kuali.kfs.module.tem.util.BufferedLogger.*;

public class TemUnitTestSeleniumLifecycle extends TemUnitTestBaseLifecycle {
    
    private WebDriver driver;
    private JettyServerLifecycle jetty;

    /**
     * Returns the current Web Driver.
     * @return the current Web Driver
     */
    public WebDriver getWebDriver() {
        return driver;
    }

    /**
     * @see org.kuali.kfs.module.tem.test.infrastructure.lifecycle.TemUnitTestBaseLifecycle#doPerSuiteStart()
     */
    @Override
    protected void doPerSuiteStart() throws Throwable {
        /*
        driver = new HtmlUnitDriver();
        ((HtmlUnitDriver) driver).setJavascriptEnabled(true);
        */
        driver = new FirefoxDriver();
        int port = 8080;
        try {
            info("Loading Jetty Server...");
            // port = HtmlUnitUtil.getPort();
            jetty = new JettyServerLifecycle(port, TemUnitTestMainLifecycle.CONTEXT_NAME, TemUnitTestMainLifecycle.RELATIVE_WEB_ROOT);
            jetty.setConfigMode(JettyServerLifecycle.ConfigMode.MERGE);
            jetty.start();
        }
        catch (Throwable e) {
            error(e.getMessage());
            e.printStackTrace();
            Throwable e1 = e.getCause();
            while (e1 != null) {
                error("Caused by: ");
                e.printStackTrace();
            }
            throw e;
        }
    }
        
   /** {@inheritDoc} */
    public void startPerSuite() {
        Log4jConfigurer.configureLogging(false);
        info("Loading Configuration");
        
        super.startPerSuite();
    }

    @Override
    protected void doPerSuiteStop() throws Throwable {
        System.out.println("Stopping suite");
        driver.close();
        driver.quit();
    }

    @Override
    protected void doPerClassStart() throws Throwable {
    }

    @Override
    protected void doPerClassStop() throws Throwable {
    }

    @Override
    protected void doPerTestStart(boolean transactional) throws Throwable {
    }

    @Override
    protected void doPerTestStop() throws Throwable {
    }

}