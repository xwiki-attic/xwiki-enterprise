/*
 * Copyright 2007, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
package com.xpn.xwiki.it;

import com.xpn.xwiki.it.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.framework.XWikiTestSuite;
import com.xpn.xwiki.it.framework.AlbatrossSkinExecutor;
import junit.framework.Test;

/**
 * Tries to register a new xwiki user
 *
 * @author hritcu
 *
 * @version $Id: $
 */
public class RegisterTest extends AbstractXWikiTestCase {
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tries to register a new xwiki user");
        suite.addTestSuite(RegisterTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    public void setUp() throws Exception
    {
        super.setUp();
        open(getUrl("Main", "WebHome"));

        // Verify that the user isn't logged in
        if (isAuthenticated()) {
            logout();
        }

        // Remove "JohnSmith" user if already exists
        if (documentExists("XWiki", "JohnSmith")) {
            deleteDocument("XWiki", "JohnSmith");
        }

        clickRegister();
    }

    public void tearDown() throws Exception
    {
        // Remove "JohnSmith" user if already exists
         if (documentExists("XWiki", "JohnSmith")) {
            deleteDocument("XWiki", "JohnSmith");
        }
        super.tearDown();
    }

    private void fillFormWithJohnSmithValues() {
        setFieldValue("register_first_name", "John");
        setFieldValue("register_last_name", "Smith");
        setFieldValue("xwikiname", "JohnSmith");
        setFieldValue("register_password", "JohnSmith");
        setFieldValue("register2_password", "JohnSmith");
        setFieldValue("register_email", "JohnSmith@example.com");
    }

    public void testRegisterJohnSmith()
    {
        fillFormWithJohnSmithValues();

        submit();

        assertTextPresent("Registration successful");

        clickLinkWithLocator("link=John Smith");

        assertTextPresent("Profile of John Smith");

        // Check that the new user can also login
        login("JohnSmith", "JohnSmith", false);
    }

    public void testRegisterExistingUser()
    {
        fillFormWithJohnSmithValues();
        setFieldValue("xwikiname", "Admin");

        submit();

        assertTextPresent("User already exists.");
    }

    public void testRegisterWithTwoDifferentPasswords()
    {
        fillFormWithJohnSmithValues();
        setFieldValue("register_password", "a");
        setFieldValue("register2_password", "b");

        submit();

        assertTextPresent("Passwords are different or password is empty.");
    }

    public void testRegisterWithoutEnteringUserName()
    {
        fillFormWithJohnSmithValues();
        setFieldValue("xwikiname", "");

        submit();

        assertTextPresent("Invalid username provided");
    }

    public void testRegisterWithoutEnteringPassword()
    {
        fillFormWithJohnSmithValues();
        setFieldValue("register_password", "");
        setFieldValue("register2_password", "");

        submit();

        assertTextPresent("Passwords are different or password is empty.");
    }

    // TODO move to framework
    private void deleteDocument(String space, String doc)
    {
        loginAsAdmin();
        // use URL factory ?
        // open(getUrl(space, doc, "delete")+ "?confirm=1");
        open("/xwiki/bin/delete/"+space+"/"+doc+"?confirm=1");
        assertTextPresent("The document has been deleted.");
        logout();
    }

    // TODO move to framework
    private boolean documentExists(String space, String doc)
    {
        String saveUrl = getSelenium().getLocation();

        open(getUrl(space, doc));

        // TODO not a reliable way to test this
        // TODO use property files to get the exact string? No
        // TODO so what else then ? XMLRPC ?
        boolean exists = getTitle().equals("XWiki - " + space + " - " + doc) &&
                !getSelenium().isTextPresent("The requested document could not be found.");

        // Restore original URL
        open(saveUrl);

        return exists;
    }


    // TODO decide whether we really want this
    private String getUrl(String space, String doc)
    {
        return getUrl(space, doc, "view");
    }

    private String getUrl(String space, String doc, String action)
    {
        return "/xwiki/bin/"+action+"/"+space+"/"+doc;
    }

    private String getUrl(String space, String doc, String action, String param)
    {
        return getUrl(space, doc, action)+"?"+param;
    }
}
