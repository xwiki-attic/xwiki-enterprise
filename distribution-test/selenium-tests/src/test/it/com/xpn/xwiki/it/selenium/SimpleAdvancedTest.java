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

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Verify Advanced and Simple User type settings.
 *
 * @version $Id$
 */
public class SimpleAdvancedTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify Advanced and Simple User type settings.");
        suite.addTestSuite(SimpleAdvancedTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        open("Main", "WebHome");
    }

    public void testSimpleAdvancedUsertype()
    {
        // Remove "JohnSmith" user if already exists
        loginAsAdmin();
        deletePage("XWiki", "JohnSmith");
        getSelenium().waitForPageToLoad("30000");
        // Ensure that the user isn't logged in
        logout();

        // Register new user "JohnSmith"
        getSelenium().click("headerregister");
        getSelenium().waitForPageToLoad("30000");
        getSelenium().type("register_first_name", "John");
        getSelenium().type("register_last_name", "Smith");
        getSelenium().type("xwikiname", "JohnSmith");
        getSelenium().type("register_password", "JohnSmith");
        getSelenium().type("register2_password", "JohnSmith");
        getSelenium().type("register_email", "JohnSmith@example.com");
        getSelenium().click("//input[@value='Register']");
        getSelenium().waitForPageToLoad("30000");

        // Login as "JohnSmith" and chech for the user type. Verify whether the Usertype Switch Link works.
        login("JohnSmith", "JohnSmith", false);
        getSelenium().waitForPageToLoad("30000");
        open("XWiki", "JohnSmith");
        assertTextPresent("Switch to Advanced edit mode");
        getSelenium().click("link=Switch to Advanced edit mode");
        getSelenium().waitForPageToLoad("30000");
        getSelenium().click("link=Switch to Simple edit mode");
        getSelenium().waitForPageToLoad("30000");
        assertTextPresent("Switch to Advanced edit mode");
        logout();

        // Login as "Admin" and Verify whether usertype of "JohnSmith" is Simple.
        loginAsAdmin();
        open("XWiki", "JohnSmith");
        assertTextPresent("Switch to Advanced edit mode");

        // Switch Usertype of "JohnSmith" to Advanced.
        getSelenium().click("link=Switch to Advanced edit mode");
        getSelenium().waitForPageToLoad("30000");
        assertTextPresent("Switch to Simple edit mode");
        logout();

        // Login as "JohnSmith" and verify whether the usertype is Advanced.
        login("JohnSmith", "JohnSmith", false);
        open("XWiki", "JohnSmith");
        assertTextPresent("Switch to Simple edit mode");
        logout();
    }
}
