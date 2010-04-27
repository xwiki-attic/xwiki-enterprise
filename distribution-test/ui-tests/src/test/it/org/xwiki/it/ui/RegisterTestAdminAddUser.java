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
import org.junit.Test;

import org.xwiki.it.ui.elements.RegisterPage;
import org.xwiki.it.ui.elements.HomePage;
import org.xwiki.it.ui.elements.LoginPage;
import org.xwiki.it.ui.elements.AdministrationPage;
import org.xwiki.it.ui.elements.UsersPage;
import org.xwiki.it.ui.elements.LightBoxRegisterPage;

/**
 * Test the Admin->Users->AddNewUser feature.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class RegisterTestAdminAddUser extends RegisterTest
{
    protected RegisterPage getRegisterPage()
    {
        return new LightBoxRegisterPage(getDriver());
    }

    protected void tryToLogin(String username, String password)
    {
        HomePage homePage = new HomePage(getDriver());
        homePage.gotoHomePage();
        homePage.clickLogout();
        LoginPage loginPage = homePage.clickLogin();
        loginPage.loginAs(username, password);
        Assert.assertTrue(registerPage.isAuthenticated());
    }
}
