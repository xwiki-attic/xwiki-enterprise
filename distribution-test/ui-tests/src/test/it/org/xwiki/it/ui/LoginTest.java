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
package org.xwiki.it.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.elements.HomePage;
import org.xwiki.it.ui.elements.LoginPage;
import org.xwiki.it.ui.framework.AbstractTest;

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
        homePage = new HomePage();
        homePage.gotoHomePage();

        // Make sure we log out if we're already logged in since we're testing the log in...
        if (homePage.isAuthenticated()) {
            homePage.clickLogout();
        }
    }

    @Test
    public void testLoginLogout()
    {
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAsAdmin();

        // Verify that after logging in we're redirected to the page on which the login button was clicked, i.e. the
        // home page here.
        Assert.assertTrue(homePage.isOnHomePage());

        Assert.assertTrue(homePage.isAuthenticated());
        Assert.assertEquals("Administrator", homePage.getCurrentUser());

        // Test Logout and verify we stay on the home page
        homePage.clickLogout();
        Assert.assertFalse(homePage.isAuthenticated());
        Assert.assertTrue(homePage.isOnHomePage());
    }

    @Test
    public void testLoginWithInvalidCredentials()
    {
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAs("Admin", "wrong password");
        Assert.assertTrue(loginPage.hasInvalidCredentialsErrorMessage());
    }

    @Test
    public void testLoginWithInvalidUsername()
    {
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAs("non existent user", "admin");
        Assert.assertTrue(loginPage.hasInvalidCredentialsErrorMessage());
    }
}
