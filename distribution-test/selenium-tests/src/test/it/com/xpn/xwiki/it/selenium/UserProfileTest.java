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
 * Different user profile tests.
 *
 * @version $Id: $
 */
public class UserProfileTest extends AbstractXWikiTestCase {
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Different user profile tests");
        suite.addTestSuite(UserProfileTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();

        open(getUrl("XWiki", "Admin"));
    }

    public void testChangePasswordWithTwoDifferentPasswords()
    {
        clickLinkWithText("Change your password");
        setFieldValue("xwikipassword", "p1");
        setFieldValue("xwikipassword2", "p2");
        clickLinkWithXPath("//input[@type='submit']", false);
        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The two passwords do not match.", getSelenium().getAlert());
    }

    public void testChangePasswordWithoutEnteringPasswords()
    {
        clickLinkWithText("Change your password");
        clickLinkWithXPath("//input[@type='submit']", false);
        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The password cannot be empty.", getSelenium().getAlert());
    }

    public void testChangePasswordOfAnotherUserWithTwoDifferentPasswords()
    {
        open("/xwiki/bin/register/XWiki/Register?register_first_name=Test&register_last_name=User&xwikiname=TestUser" +
                "&register_password=test&register2_password=test&register_email=test@test.com" +
                "&template=XWiki.XWikiUserTemplate&register=1");
        open(getUrl("XWiki", "TestUser"));
        clickLinkWithText("Change password");
        clickLinkWithXPath("//input[@type='submit']", false);
        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The password cannot be empty.", getSelenium().getAlert());
    }
}
