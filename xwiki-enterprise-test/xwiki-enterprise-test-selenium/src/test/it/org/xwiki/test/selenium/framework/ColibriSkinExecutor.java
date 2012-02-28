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
package org.xwiki.test.selenium.framework;

import junit.framework.Assert;

/**
 * Implementation of skin-related actions for the Colibri skin.
 * 
 * @version $Id$
 */
public class ColibriSkinExecutor extends AlbatrossSkinExecutor
{
    public ColibriSkinExecutor(AbstractXWikiTestCase test)
    {
        super(test);
    }

    @Override
    public boolean isAuthenticated()
    {
        return getTest().isElementPresent("tmUser");
    }

    @Override
    public boolean isAuthenticated(String username)
    {
        return getTest().isElementPresent(
            "//div[@id='tmUser']/a[contains(@href, '/xwiki/bin/view/XWiki/" + username + "')]");
    }

    @Override
    public boolean isAuthenticationMenuPresent()
    {
        return getTest().isElementPresent("//div[@id='tmLogin' or @id='tmLogout']/a");
    }

    @Override
    public void clickShowComments()
    {
        getTest().clickLinkWithLocator("//span[@id = 'commentsshortcut']/a", false);
    }

    @Override
    public void clickShowAttachments()
    {
        getTest().clickLinkWithLocator("//span[@id = 'attachmentsshortcut']/a", false);
    }

    @Override
    public void clickShowHistory()
    {
        getTest().clickLinkWithLocator("//span[@id = 'historyshortcut']/a", false);
    }

    @Override
    public void clickShowInformation()
    {
        getTest().clickLinkWithLocator("//span[@id = 'informationshortcut']/a", false);
    }

    @Override
    public void loginAsAdmin()
    {
        // Verify if the login or logout links are available and if not go to the home page to make it available
        // (for ex it's not available in edit mode)
        if (!isAuthenticationMenuPresent()) {
            getTest().open("Main", "WebHome");
        }

        super.loginAsAdmin();
    }

    @Override
    public void login(String username, String password, boolean rememberme)
    {
        // If the user is logged in then log out
        if (isAuthenticated()) {
            // If there's no logout button then navigate to the home page
            if (!isAuthenticationMenuPresent()) {
                getTest().open("Main", "WebHome");
            }
            logout();
        }

        // If there's no log-in button, navigate to the home page
        if (!isAuthenticationMenuPresent()) {
            getTest().open("Main", "WebHome");
        }
        clickLogin();

        getTest().setFieldValue("j_username", username);
        getTest().setFieldValue("j_password", password);
        if (rememberme) {
            getTest().checkField("rememberme");
        }
        getTest().submit();

        Assert.assertTrue("User has not been authenticated", isAuthenticated());
    }

    @Override
    public void logout()
    {
        Assert.assertTrue("User wasn't authenticated.", isAuthenticated());
        getTest().clickLinkWithLocator("//div[@id='tmLogout']/a");
        Assert.assertFalse("The user is still authenticated after a logout.", isAuthenticated());
    }

    @Override
    public void clickLogin()
    {
        getTest().clickLinkWithLocator("//div[@id='tmLogin']/a");
        assertIsLoginPage();
    }

    @Override
    public void clickRegister()
    {
        getTest().clickLinkWithLocator("//div[@id='tmRegister']/a");
        assertIsRegisterPage();
    }

    @Override
    public void clickEditPageInWikiSyntaxEditor()
    {
        // Colibri skin uses the same id for the edit Wiki link in view and edit modes.
        getTest().clickLinkWithLocator("tmEditWiki");
    }

    @Override
    public void clickEditPageInWysiwyg()
    {
        // Colibri skin uses the same id for the edit WYSIWYG link in view and edit modes.
        getTest().clickLinkWithLocator("tmEditWysiwyg");
    }
}
