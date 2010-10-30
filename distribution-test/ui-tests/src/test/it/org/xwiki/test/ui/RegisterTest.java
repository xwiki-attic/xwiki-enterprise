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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.xwiki.test.ui.administration.elements.AdministrationSectionPage;
import org.xwiki.test.ui.framework.elements.RegisterPage;
import org.xwiki.test.ui.framework.AbstractTest;
import org.xwiki.test.ui.framework.TestUtils;

/**
 * Test the user registration feature.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class RegisterTest extends AbstractTest
{
    protected RegisterPage registerPage;

    @Before
    public void setUp()
    {
        registerPage = getRegisterPage();

        deleteUser("JohnSmith");
        switchUser();
        registerPage.gotoPage();

        // Switch LiveValidation on or off as needed.
        int x = 0;
        while (registerPage.liveValidationEnabled() != useLiveValidation()) {
            AdministrationSectionPage sectionPage = new AdministrationSectionPage("Registration");
            getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(sectionPage.getURL()));
            getUtil().assertOnPage(sectionPage.getURL());
            sectionPage.getForm().setFieldValue(By.name("XWiki.Registration_0_liveValidation_enabled"),
                Boolean.valueOf(useLiveValidation()).toString());
            sectionPage.clickSave();
            if (x > 2) {
                throw new WebDriverException("Unable to set useLiveValidation to " + useLiveValidation());
            }
            x++;
            registerPage.gotoPage();
        }
        registerPage.fillInJohnSmithValues();
    }

    /** Become the user needed for the test. Guest for RegisterTest. */
    protected void switchUser()
    {
        // Fast Logout.
        getUtil().setSession(null);
    }

    /** To put the registration page someplace else, subclass this class and change this method. */
    protected RegisterPage getRegisterPage()
    {
        return new RegisterPage();
    }

    /** To test without javascript validation, subclass this class and change this method. */
    protected boolean useLiveValidation()
    {
        return true;
    }

    @Test
    public void testRegisterJohnSmith()
    {
        Assert.assertTrue(validateAndRegister());
        tryToLogin("JohnSmith", "WeakPassword");
    }

    @Test
    public void testRegisterExistingUser()
    {
        registerPage.fillRegisterForm(null, null, "Admin", null, null, null);
        // Can't use validateAndRegister here because user existance is not checked by LiveValidation.
        Assert.assertFalse(tryToRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("User already exists."));
    }

    @Test
    public void testRegisterPasswordTooShort()
    {
        registerPage.fillRegisterForm(null, null, null, "short", "short", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("Please use a longer password."));
    }

    @Test
    public void testRegisterDifferentPasswords()
    {
        registerPage.fillRegisterForm(null, null, null, null, "DifferentPassword", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("Your passwords aren't the same."));
    }

    @Test
    public void testRegisterEmptyPassword()
    {
        registerPage.fillRegisterForm(null, null, null, "", "", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("This field is mandatory."));
    }

    @Test
    public void testRegisterEmptyUserName()
    {
        // A piece of javascript fills in the username with the first and last names so we will empty them.
        registerPage.fillRegisterForm("", "", "", null, null, null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("This field is mandatory."));
    }

    @Test
    public void testRegisterInvalidEmail()
    {
        registerPage.fillRegisterForm(null, null, null, null, null, "not an email address");
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(registerPage.validationFailureMessagesInclude("Please give a valid email address."));
    }

    /**
     * If LiveValidation is enabled then it will check that there are no failures with that. If no failures then hits
     * register button, it then asserts that hitting the register button did not reveal any failures not caught by
     * LiveValidation. If LiveValidation is disabled then just hits the register button.
     */
    protected boolean validateAndRegister()
    {
        if (useLiveValidation()) {
            registerPage.triggerLiveValidation();
            if (!registerPage.getValidationFailureMessages().isEmpty()) {
                return false;
            }
            boolean result = tryToRegister();

            Assert.assertTrue("LiveValidation did not show a failure message but clicking on the register button did.",
                              registerPage.getValidationFailureMessages().isEmpty());

            return result;
        }
        return tryToRegister();
    }

    protected boolean tryToRegister()
    {
        registerPage.clickRegister();
        
        List<WebElement> infos = getDriver().findElements(By.className("infomessage"));
        for (WebElement info : infos) {
            if (info.getText().contains("Registration successful.")) {
                return true;
            }
        }
        return false;
    }

    /** Deletes specified user if it exists, leaves the driver on undefined page. */
    private void deleteUser(final String userName)
    {
        TestUtils.Session s = getUtil().getSession();
        getUtil().setSession(null);
        getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(getUtil().getURLToDeletePage("XWiki", userName)));
        getUtil().setSession(s);
    }

    protected void tryToLogin(String username, String password)
    {
        // Fast logout.
        getUtil().setSession(null);
        getDriver().get(getUtil().getURLToLoginAs(username, password));
        Assert.assertTrue(registerPage.isAuthenticated());
    }
}
