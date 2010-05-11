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

        // We start at localhost:4444/selenium-server... and isExistingPage reloads current location 
        // so navigate to main page.
        if (!isAuthenticationMenuPresent()) {
            open("Main", "WebHome");
        }

        // Remove "JohnSmith" user if already exists
        if (isExistingPage("XWiki", "JohnSmith")) {
            loginAsAdmin();
            deletePage("XWiki", "JohnSmith");
            logout();
        }

        // Ensure that the user isn't logged in
        if (isAuthenticated()) {
            logout();
        }

        // Usually we are already at the registration page, this makes it more fragile but quicker.
        if (!getSelenium().isTextPresent("Welcome to the registration form. This will allow you to edit pages,")) {
            clickRegister();
        }

        fillFormWithJohnSmithValues();
    }


    /* The Tests */

    /* LiveValidation should have no influence on this test so it's only run once (with LiveValidation on) */
    public void testRegisterJohnSmith()
    {
        setUseLiveValidation(true);
        submit();
        assertTextPresent("Registration successful");
        clickLinkWithLocator("link=John Smith");
        assertTextPresent("Profile of John Smith");

        // Check that the new user can also login
        login("JohnSmith", "JohnSmith", false);
    }

    public void testRegisterWithoutEnteringPasswordAndPasswordTooShortWithLiveValidation()
    {
        setUseLiveValidation(true);
        tryRegisterWithoutEnteringPassword(false);
    }

    public void testRegisterWithTwoDifferentPasswordsWithLiveValidation()
    {
        setUseLiveValidation(true);
        tryRegisterWithTwoDifferentPasswords(false);
    }

    public void testRegisterWithoutEnteringUserNameWithLiveValidation()
    {
        setUseLiveValidation(true);
        tryRegisterWithoutEnteringUserName(false);
    }

    /* LiveValidation should have no influence on this test so it's only run once (with LiveValidation on) */
    public void testRegisterExistingUser()
    {
        setUseLiveValidation(true);
        setFieldValue("xwikiname", "Admin");
        submit();
        assertTextPresent("User already exists.");
    }

    public void testSwitchLiveValidationOff()
    {
        setUseLiveValidation(true);
        assertElementPresent("xpath=/html/body/div/div/div[3]/div/div/div/div/div/script");
        setUseLiveValidation(false);
        assertElementNotPresent("xpath=/html/body/div/div/div[3]/div/div/div/div/div/script");
    }

    public void testRegisterWithoutEnteringPasswordNoLiveValidation()
    {
        setUseLiveValidation(false);
        tryRegisterWithoutEnteringPassword(true);
    }

    public void testRegisterWithTwoDifferentPasswordsNoLiveValidation()
    {
        setUseLiveValidation(false);
        tryRegisterWithTwoDifferentPasswords(true);
    }

    public void testRegisterWithoutEnteringUserNameNoLiveValidation()
    {
        setUseLiveValidation(false);
        tryRegisterWithoutEnteringUserName(true);
    }

    /* Helper methods */

    private void fillFormWithJohnSmithValues()
    {
        fillRegisterForm("John", "Smith", "JohnSmith", "JohnSmith", "JohnSmith@example.com");
    }

    /* Assumes we are at the registration page and leaves us there. */
    public void setUseLiveValidation(boolean useLiveValidation)
    {
        if (useLiveValidation != isElementPresent("xpath=/html/body/div/div/div[3]/div/div/div/div/div/script")) {       
            loginAsAdmin();
            open("XWiki", "XWikiPreferences", "admin", "section=Registration&editor=globaladmin");
            if (useLiveValidation == true) {
                getSelenium().check("//input[@name='XWiki.Registration_0_liveValidation_enabled'][@type='checkbox']");
            } else {
                getSelenium().uncheck("//input[@name='XWiki.Registration_0_liveValidation_enabled'][@type='checkbox']");
            }
            // Copied from AlbatrossSkinExecutor.clickEditSaveAndContinue() 
            // because we have to click a specific saveAndContinue button.
            submit("//input[@value='Save XWiki.RegistrationConfig'][@name='action_saveandcontinue']", false);
            waitForCondition("(window.document.getElementsByClassName('xnotification-done')[0] != null "
                             + "&& window.document.getElementsByClassName('xnotification-done')[0].innerHTML == 'Saved')");
            // Ensure that the user isn't logged in
            logout();
            clickRegister();
            fillFormWithJohnSmithValues();
        }
    }

    public void tryRegisterWithTwoDifferentPasswords(boolean submit)
    {
        setFieldValue("register_password", "a");
        setFieldValue("register2_password", "b");
        if (submit) {
            clickLinkWithXPath("//input[@type='submit']", false);
        } else {
            // Blur event to make livevalidation react.
            getSelenium().fireEvent("//div/dl/dd/input[@id='register_password']", "blur");
            getSelenium().fireEvent("//div/dl/dd/input[@id='register2_password']", "blur");
        }
        waitForElement("xpath=//form[@id='register']/div/dl/dd[5]/span");
        waitForTextPresent("xpath=//form[@id='register']/div/dl/dd[4]/span", "Please use a longer password.");
        waitForTextPresent("xpath=//form[@id='register']/div/dl/dd[5]/span", "Your passwords aren't the same.");
    }

    public void tryRegisterWithoutEnteringUserName(boolean submit)
    {
        setFieldValue("xwikiname", "");
        if (submit) {
            clickLinkWithXPath("//input[@type='submit']", false);
        } else {
            // Blur event to make livevalidation react.
            getSelenium().fireEvent("//div/dl/dd/input[@id='xwikiname']", "blur");
        }
        waitForElement("xpath=//form[@id='register']/div/dl/dd[3]/span");
        waitForTextPresent("xpath=//form[@id='register']/div/dl/dd[3]/span", "This field is mandatory.");
    }

    public void tryRegisterWithoutEnteringPassword(boolean submit)
    {
        setFieldValue("register_password", "");
        setFieldValue("register2_password", "");
        if (submit) {
            clickLinkWithXPath("//input[@type='submit']", false);
        } else {
            // Blur event to make livevalidation react.
            getSelenium().fireEvent("//div/dl/dd/input[@id='register_password']", "blur");
        }
        waitForElement("xpath=//form[@id='register']/div/dl/dd[4]/span");
        waitForTextPresent("xpath=//form[@id='register']/div/dl/dd[4]/span", "This field is mandatory.");
    }
}
