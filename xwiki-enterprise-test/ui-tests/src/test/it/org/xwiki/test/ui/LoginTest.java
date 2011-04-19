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
package org.xwiki.test.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.administration.elements.GlobalRightsAdministrationSectionPage;
import org.xwiki.test.ui.framework.AbstractTest;
import org.xwiki.test.ui.framework.elements.LoginPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Test the Login feature.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class LoginTest extends AbstractTest
{
    private HomePage homePage;

    @Before
    public void setUp()
    {
        this.homePage = new HomePage();
        this.homePage.gotoPage();

        // Make sure we log out if we're already logged in since we're testing the log in...
        if (this.homePage.isAuthenticated()) {
            this.homePage.logout();
        }
    }

    @Test
    public void testLoginLogout()
    {
        LoginPage loginPage = this.homePage.login();
        loginPage.loginAsAdmin();

        // Verify that after logging in we're redirected to the page on which the login button was clicked, i.e. the
        // home page here.
        Assert.assertTrue(this.homePage.isOnHomePage());

        Assert.assertTrue(this.homePage.isAuthenticated());
        Assert.assertEquals("Administrator", this.homePage.getCurrentUser());

        // Test Logout and verify we stay on the home page
        this.homePage.logout();
        Assert.assertFalse(this.homePage.isAuthenticated());
        Assert.assertTrue(this.homePage.isOnHomePage());
    }

    @Test
    public void testLoginWithInvalidCredentials()
    {
        LoginPage loginPage = this.homePage.login();
        loginPage.loginAs("Admin", "wrong password");
        Assert.assertTrue(loginPage.hasInvalidCredentialsErrorMessage());
    }

    @Test
    public void testLoginWithInvalidUsername()
    {
        LoginPage loginPage = this.homePage.login();
        loginPage.loginAs("non existent user", "admin");
        Assert.assertTrue(loginPage.hasInvalidCredentialsErrorMessage());
    }

    @Test
    public void testRedirectBackAfterLogin()
    {
        try {
            LoginPage loginPage = this.homePage.login();
            loginPage.loginAsAdmin();

            AdministrationPage admin = new AdministrationPage();
            admin.gotoPage();
            GlobalRightsAdministrationSectionPage sectionPage = admin.clickGlobalRightsSection();
            sectionPage.forceAuthenticatedView();

            getUtil().gotoPage("Blog", "Categories");
            loginPage.logout();
            getDriver().manage().deleteAllCookies();
            loginPage.loginAsAdmin();
            // We use startsWith since the URL contains a jsessionid and a srid.
            Assert.assertTrue(getDriver().getCurrentUrl().startsWith(getUtil().getURL("Blog", "Categories")));
        } finally {
            AdministrationPage admin = new AdministrationPage();
            admin.gotoPage();
            if (!admin.isAuthenticated()) {
                admin.login().loginAsAdmin();
                admin.gotoPage();
            }
            GlobalRightsAdministrationSectionPage sectionPage = admin.clickGlobalRightsSection();
            sectionPage.unforceAuthenticatedView();
        }
    }

    @Test
    public void testRedirectPreservesPOSTParameters() throws InterruptedException
    {
        String test = "Test string " + System.currentTimeMillis();
        final String space = "Main";
        final String page = "POSTTest";
        LoginPage loginPage = this.homePage.login();
        loginPage.loginAsAdmin();
        // start editing a page
        WikiEditPage editPage = new WikiEditPage();
        editPage.switchToEdit(space, page);
        editPage.setTitle(test);
        editPage.setContent(test);
        // emulate expired session: delete the cookies
        getDriver().manage().deleteAllCookies();
        // try to save
        editPage.clickSaveAndView();
        // we should have been redirected to login
        Assert.assertTrue(getDriver().getCurrentUrl().startsWith(getUtil().getURL("XWiki", "XWikiLogin", "login")));
        loginPage.loginAsAdmin();
        // we should have been redirected back to view, and the page should have been saved
        Assert.assertTrue(getDriver().getCurrentUrl().startsWith(getUtil().getURL(space, page)));
        editPage.switchToEdit(space, page);
        Assert.assertEquals(test, editPage.getTitle());
        Assert.assertEquals(test, editPage.getContent());
    }
}
