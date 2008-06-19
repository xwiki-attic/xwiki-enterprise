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
import org.openqa.selenium.server.SeleniumServer;

/**
 * Starts the Browser only once per Test Suite.
 */
public class XWikiSeleniumTestSetup extends TestSetup
{
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
        this.selenium = new DefaultSelenium("localhost", SeleniumServer.DEFAULT_PORT, BROWSER, BASE_URL);

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
