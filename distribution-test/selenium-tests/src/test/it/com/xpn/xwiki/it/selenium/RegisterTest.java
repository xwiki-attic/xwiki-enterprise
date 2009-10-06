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
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Tries to register a new xwiki user
 *
 * @version $Id$
 */
public class RegisterTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tries to register a new XWiki user");
        suite.addTestSuite(RegisterTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();

        // Remove "JohnSmith" user if already exists
        loginAsAdmin();
        deletePage("XWiki", "JohnSmith");

        // Ensure that the user isn't logged in
        logout();

        clickRegister();
        fillFormWithJohnSmithValues();
    }

    private void fillFormWithJohnSmithValues()
    {
        fillRegisterForm("John", "Smith", "JohnSmith", "JohnSmith", "JohnSmith@example.com");
    }

    public void testRegisterJohnSmith()
    {
        submit();
        assertTextPresent("Registration successful");
        clickLinkWithLocator("link=John Smith");
        assertTextPresent("Profile of John Smith");

        // Check that the new user can also login
        login("JohnSmith", "JohnSmith", false);
    }

    public void testRegisterExistingUser()
    {
        setFieldValue("xwikiname", "Admin");
        submit();
        assertTextPresent("User already exists.");
    }

    public void testRegisterWithTwoDifferentPasswords()
    {
        setFieldValue("register_password", "a");
        setFieldValue("register2_password", "b");
        submit();
        assertTextPresent("Passwords are different or password is empty.");
    }

    public void testRegisterWithoutEnteringUserName()
    {
        setFieldValue("xwikiname", "");
        submit();
        assertTextPresent("Invalid username provided");
    }

    public void testRegisterWithoutEnteringPassword()
    {
        setFieldValue("register_password", "");
        setFieldValue("register2_password", "");
        submit();
        assertTextPresent("Passwords are different or password is empty.");
    }
}
