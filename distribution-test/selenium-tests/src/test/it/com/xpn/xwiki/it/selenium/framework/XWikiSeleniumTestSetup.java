/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */package com.xpn.xwiki.it.selenium.framework;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Starts the Browser only once per Test Suite.
 */
public class XWikiSeleniumTestSetup extends TestSetup
{
    private static final int SELENIUM_PORT = Integer.parseInt(System.getProperty("seleniumPort", "4444"));

    private static final String PORT = System.getProperty("xwikiPort", "8080");

    private static final String BASE_URL = "http://localhost:" + PORT;

    private static final String BROWSER = System.getProperty("browser", "*firefox"); 

    private Selenium selenium;

    public XWikiSeleniumTestSetup(Test test)
    {
        super(test);
    }

    protected void setUp() throws Exception
    {
        this.selenium = new DefaultSelenium("localhost", SELENIUM_PORT, BROWSER, BASE_URL) {
            /**
             * Selenium RC Java Client Driver has introduced a non-backward compatible change: open() nows checks
             * for error code and throw an error if a non 200 is found. Since our XWiki pages return non 200 codes
             * in some cases our tests now fail. See http://jira.openqa.org/browse/SEL-684
             * What's strange is that the source code shows that a new open(String, String ignoreErrorCode) has been
             * introduced (see http://bit.ly/cU58WO). However we don't get it in the released 1.0.2 version for
             * some unknown reason.
             */
            // TODO: Remove this when Selenium fixes the problem.
            @Override public void open(String url)
            {
                commandProcessor.doCommand("open", new String[] {url, "true"});
            }
        };

        // Sets the Selenium object in all tests
        for (AbstractXWikiTestCase test: getTests(getTest())) {
            test.setSelenium(this.selenium);
        }

        this.selenium.start();
    }

    protected void tearDown() throws Exception
    {
        this.selenium.stop();
    }
    
    private List<AbstractXWikiTestCase> getTests(Test test)
    {
        List<AbstractXWikiTestCase> tests = new ArrayList<AbstractXWikiTestCase>();

        if (TestSuite.class.isAssignableFrom(test.getClass())) {
            TestSuite suite = (TestSuite) test;
            Enumeration nestedTests = suite.tests();
            while (nestedTests.hasMoreElements()) {
                tests.addAll(getTests((Test) nestedTests.nextElement()));
            }
        } else if (TestSetup.class.isAssignableFrom(test.getClass())) {
            TestSetup setup = (TestSetup) test;
            tests.addAll(getTests(setup.getTest()));
        } else if (AbstractXWikiTestCase.class.isAssignableFrom(test.getClass())) {
            tests.add((AbstractXWikiTestCase) test);
        }

        return tests;
    }
}
