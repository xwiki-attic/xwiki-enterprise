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
import org.xwiki.test.po.administration.AdministrationSectionPage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.AbstractRegistrationPage;
import org.xwiki.test.ui.po.RegistrationPage;

/**
 * Test the user registration feature.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class RegisterTest extends AbstractTest
{
    protected AbstractRegistrationPage registrationPage;

    @Before
    public void setUp()
    {
        deleteUser("JohnSmith");
        switchUser();
        this.registrationPage = this.getRegistrationPage();

        // Switch LiveValidation on or off as needed.
        int x = 0;
        while (this.registrationPage.isLiveValidationEnabled() != useLiveValidation()) {
            AdministrationSectionPage sectionPage = new AdministrationSectionPage("Registration");
            getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(sectionPage.getURL()));
            getUtil().recacheSecretToken();
            getUtil().assertOnPage(sectionPage.getURL());
            sectionPage.getForm().setFieldValue(By.name("XWiki.Registration_0_liveValidation_enabled"),
                Boolean.valueOf(useLiveValidation()).toString());
            sectionPage.clickSave();
            if (x > 2) {
                throw new WebDriverException("Unable to set useLiveValidation to " + useLiveValidation());
            }
            x++;
            this.registrationPage = this.getRegistrationPage();
        }

        // The prepareName javascript function is the cause of endless flickering
        // since it trys to suggest a username every time the field is focused.
        this.registrationPage.executeJavascript("document.getElementById('xwikiname').onfocus = null;");

        this.registrationPage.fillInJohnSmithValues();
    }

    /** Become the user needed for the test. Guest for RegisterTest. */
    protected void switchUser()
    {
        // Fast Logout.
        getUtil().forceGuestUser();
    }

    /** To put the registration page someplace else, subclass this class and change this method. */
    protected AbstractRegistrationPage getRegistrationPage()
    {
        return RegistrationPage.gotoPage();
    }

    /** To test without javascript validation, subclass this class and change this method. */
    protected boolean useLiveValidation()
    {
        return true;
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterJohnSmith()
    {
        Assert.assertTrue(validateAndRegister());
        tryToLogin("JohnSmith", "WeakPassword");
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterExistingUser()
    {
        registrationPage.fillRegisterForm(null, null, "Admin", null, null, null);
        // Can't use validateAndRegister here because user existence is not checked by LiveValidation.
        Assert.assertFalse(tryToRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("User already exists."));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterPasswordTooShort()
    {
        this.registrationPage.fillRegisterForm(null, null, null, "short", "short", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("Please use a longer password."));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterDifferentPasswords()
    {
        this.registrationPage.fillRegisterForm(null, null, null, null, "DifferentPassword", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("The passwords do not match."));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterEmptyPassword()
    {
        this.registrationPage.fillRegisterForm(null, null, null, "", "", null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("This field is required."));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterEmptyUserName()
    {
        // A piece of javascript fills in the username with the first and last names so we will empty them.
        this.registrationPage.fillRegisterForm("", "", "", null, null, null);
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("This field is required."));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testRegisterInvalidEmail()
    {
        this.registrationPage.fillRegisterForm(null, null, null, null, null, "not an email address");
        Assert.assertFalse(validateAndRegister());
        Assert.assertTrue(this.registrationPage.validationFailureMessagesInclude("Please enter a valid email address."));
    }

    /**
     * If LiveValidation is enabled then it will check that there are no failures with that. If no failures then hits
     * register button, it then asserts that hitting the register button did not reveal any failures not caught by
     * LiveValidation. If LiveValidation is disabled then just hits the register button.
     */
    protected boolean validateAndRegister()
    {
        if (useLiveValidation()) {
            this.registrationPage.triggerLiveValidation();
            if (!this.registrationPage.getValidationFailureMessages().isEmpty()) {
                return false;
            }
            boolean result = tryToRegister();

            Assert.assertTrue("LiveValidation did not show a failure message but clicking on the register button did.",
                this.registrationPage.getValidationFailureMessages().isEmpty());

            return result;
        }
        return tryToRegister();
    }

    protected boolean tryToRegister()
    {
        this.registrationPage.clickRegister();
        
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
        getUtil().forceGuestUser();
        getDriver().get(getUtil().getURLToLoginAsAdminAndGotoPage(getUtil().getURLToNonExistentPage()));
        getUtil().recacheSecretToken();
        getUtil().deletePage("XWiki", userName);
        getUtil().setSession(s);
    }

    protected void tryToLogin(String username, String password)
    {
        // Fast logout.
        getUtil().forceGuestUser();
        getDriver().get(getUtil().getURLToLoginAs(username, password));
        Assert.assertTrue(this.registrationPage.isAuthenticated());
        getUtil().recacheSecretToken();
    }
}
