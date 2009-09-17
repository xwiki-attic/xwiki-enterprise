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

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the login and logout features of XWiki.
 * 
 * @version $Id$
 */
public class LoginLogoutTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the login and logout features of XWiki");
        suite.addTestSuite(LoginLogoutTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        open("Main","WebHome");

        // Verify that the user isn't logged in
        if (isAuthenticated()) {
            logout();
        }

        clickLogin();
    }

    public void testLogAsAdmin()
    {
        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "admin");
        checkField("rememberme");
        submit();

        assertTrue("Admin user has not been authenticated", isAuthenticated());
    }

    public void testLogWithWrongPassword()
    {
        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "wrong password");
        submit();

        assertTextPresent("Wrong password");
    }

    public void testLogWithInvalidUsername()
    {
        setFieldValue("j_username", "non existent user");
        setFieldValue("j_password", "admin");
        submit();

        assertTextPresent("Wrong user name");
    }

    public void testLogout()
    {
        loginAsAdmin();
        logout();

        this.assertTextPresent("Log-in");
    }

    /**
     * Tests that in case the authentication is lost, the data is restored after the login.
     */
    public void testDataIsPreservedAfterLogin()
    {
        boolean wasAuthenticated = isAuthenticated();
        if (wasAuthenticated) {
            logout();
        }
        open("Test", "TestData", "save", "content=this+should+not+be+saved");
        open("Test", "TestData", "save", "content=this+should+be+saved+instead&parent=Main.WebHome");

        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "admin");
        checkField("rememberme");
        submit();

        assertPage("Test", "TestData");
        assertTextPresent("this should be saved instead");
        assertTextPresent("Welcome to your wiki");
        // Preserve the initial state
        if (!wasAuthenticated) {
            logout();
        }
    }

    /**
     * Tests that in case the authentication is lost, the data is restored after the login.
     */
    public void testCorrectUrlIsAccessedAfterLogin()
    {
        boolean wasAuthenticated = isAuthenticated();
        if (wasAuthenticated) {
            logout();
        }
        open("Test", "TestData", "save", "content=this+should+be+the+new+value");
        waitForCondition(
            "new Ajax.Request('" + getUrl("Test", "TestData", "cancel", "ajax=1") + "', {asynchronous: false});");

        setFieldValue("j_username", "Admin");
        setFieldValue("j_password", "admin");
        checkField("rememberme");
        submit();

        assertPage("Test", "TestData");
        assertTextPresent("this should be the new value");
        // Preserve the initial state
        if (!wasAuthenticated) {
            logout();
        }
    }
}
