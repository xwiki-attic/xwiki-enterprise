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
 */
package com.xpn.xwiki.it.selenium;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xwiki.test.XWikiTestSetup;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.XWikiSeleniumTestSetup;

/**
 * A class listing all the Selenium Functional tests to execute. We need such a class (rather than letting the JUnit
 * Runner discover the different TestCases classes by itself) because we want to start/stop XWiki before and after the
 * tests start (but only once).
 * 
 * @version $Id: $
 */
public class AllTests extends TestCase
{
    private static final String PATTERN = ".*" + System.getProperty("pattern", "");

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite();

        addTestCase(suite, StandardFeaturesTest.class);
        addTestCase(suite, LinkSupportTest.class);
        addTestCase(suite, ListSupportTest.class);
        addTestCase(suite, MacroSupportTest.class);
        addTestCase(suite, ColorSupportTest.class);

        return new XWikiSeleniumTestSetup(new XWikiTestSetup(suite));
    }

    private static void addTestCase(TestSuite suite, Class< ? extends AbstractXWikiTestCase> testClass)
        throws Exception
    {
        if (testClass.getName().matches(PATTERN)) {
            Method method = testClass.getMethod("suite", (Class[]) null);
            suite.addTest((Test) method.invoke(null, (Object[]) null));
        }
    }
}
