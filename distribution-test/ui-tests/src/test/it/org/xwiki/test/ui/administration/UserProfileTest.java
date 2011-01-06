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
package org.xwiki.test.ui.administration;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.xwiki.test.ui.administration.elements.PreferencesUserProfilePage;
import org.xwiki.test.ui.administration.elements.ProfileUserProfilePage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.ChangeAvatarPage;
import org.xwiki.test.ui.framework.elements.editor.ChangePasswordPage;
import org.xwiki.test.ui.framework.elements.editor.PreferencesEditPage;
import org.xwiki.test.ui.framework.elements.editor.ProfileEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Test the User Profile.
 * 
 * @version $Id$
 * @since 2.4
 */
public class UserProfileTest extends AbstractAdminAuthenticatedTest
{
    private static final String TEST_USERNAME = "ProfileTestUser";

    private static final String IMAGE_NAME = "avatar.png";

    private static final String IMAGE_LOCATION = "administration/" + IMAGE_NAME;

    private static final String USER_FIRST_NAME = "User";

    private static final String USER_LAST_NAME = "of this Wiki";

    private static final String USER_COMPANY = "XWiki.org";

    private static final String USER_ABOUT = "This is some example text to type into the text area";

    private static final String USER_EMAIL = "webmaster@xwiki.org";

    private static final String USER_PHONE = "0000-000-000";

    private static final String USER_ADDRESS = "1600 No Street";

    private static final String USER_BLOG = "http://xwiki.org/";

    private static final String USER_BLOGFEED = "http://xwiki.org/";

    private ProfileUserProfilePage profilePage = new ProfileUserProfilePage("Admin");

    /** Functionality check: changing profile information. */
    @Test
    public void EditProfile()
    {
        ProfileUserProfilePage customProfilePage = new ProfileUserProfilePage(TEST_USERNAME);
        try {
            // Create clean profile
            getUtil().deletePage("XWiki", TEST_USERNAME);
            getUtil().registerLoginAndGotoPage(TEST_USERNAME, "abcdef123", null);

            // Change the profile information
            customProfilePage.gotoPage();
            ProfileEditPage profileEditPage = customProfilePage.editProfile();
            profileEditPage.setUserFirstName(USER_FIRST_NAME);
            profileEditPage.setUserLastName(USER_LAST_NAME);
            profileEditPage.setUserCompany(USER_COMPANY);
            profileEditPage.setUserAbout(USER_ABOUT);
            profileEditPage.setUserEmail(USER_EMAIL);
            profileEditPage.setUserPhone(USER_PHONE);
            profileEditPage.setUserAddress(USER_ADDRESS);
            profileEditPage.setUserBlog(USER_BLOG);
            profileEditPage.setUserBlogFeed(USER_BLOGFEED);
            profileEditPage.clickSaveAndView();

            // Check that the information was updated
            Assert.assertEquals(USER_FIRST_NAME, customProfilePage.getUserFirstName());
            Assert.assertEquals(USER_LAST_NAME, customProfilePage.getUserLastName());
            Assert.assertEquals(USER_COMPANY, customProfilePage.getUserCompany());
            Assert.assertEquals(USER_ABOUT, customProfilePage.getUserAbout());
            // The page will show webmaster@---- for security reasons, just check the first part of the email
            Assert.assertEquals(StringUtils.substringBefore(USER_EMAIL, "@"),
                StringUtils.substringBefore(customProfilePage.getUserEmail(), "@"));
            Assert.assertEquals(USER_PHONE, customProfilePage.getUserPhone());
            Assert.assertEquals(USER_ADDRESS, customProfilePage.getUserAddress());
            Assert.assertEquals(USER_BLOG, customProfilePage.getUserBlog());
            Assert.assertEquals(USER_BLOGFEED, customProfilePage.getUserBlogFeed());
        } finally {
            // Cleanup: delete the dummy user
            getUtil().setSession(null);
            getDriver().get(getUtil().getURLToLoginAsAdmin());
            getUtil().deletePage("XWiki", TEST_USERNAME);
        }
    }

    /** Functionality check: changing the profile picture. */
    @Test
    public void changeAvatarImage()
    {
        this.profilePage.gotoPage();
        ChangeAvatarPage changeAvatarImage = this.profilePage.changeAvatarImage();
        changeAvatarImage.setAvatarImage(IMAGE_LOCATION);
        changeAvatarImage.submit();
        Assert.assertEquals(IMAGE_NAME, this.profilePage.getAvatarImageName());
    }

    /** Functionality check: changing the password. */
    @Test
    public void changePassword()
    {
        try {
            // Change the password
            this.profilePage.gotoPage();
            PreferencesUserProfilePage preferencesPage = this.profilePage.switchToPreferences();
            ChangePasswordPage changePasswordPage = preferencesPage.changePassword();
            changePasswordPage.changePassword("xwikitest", "xwikitest");
            changePasswordPage.submit();

            // Logout
            getUtil().setSession(null);
            HomePage home = new HomePage();
            home.gotoPage();
            Assert.assertFalse(home.isAuthenticated());

            // Login with the new password
            getDriver().get(getUtil().getURLToLoginAs("Admin", "xwikitest"));
            Assert.assertTrue(home.isAuthenticated());
        } finally {
            // Reset the default password
            this.profilePage.gotoPage();
            PreferencesUserProfilePage preferencesPage = this.profilePage.switchToPreferences();
            ChangePasswordPage changePasswordPage = preferencesPage.changePassword();
            changePasswordPage.changePasswordToDefault();
            changePasswordPage.submit();
            getUtil().setSession(null);
            getDriver().get(getUtil().getURLToLoginAsAdmin());
        }
    }

    /** Functionality check: changing the user type. */
    @Test
    public void testChangeUserProfile()
    {
        this.profilePage.gotoPage();
        PreferencesUserProfilePage preferencesPage = this.profilePage.switchToPreferences();

        // Setting to Simple user
        PreferencesEditPage preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setSimpleUserType();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.profilePage.switchToPreferences();
        Assert.assertFalse(getDriver().getPageSource().contains("?editor=object"));

        // Setting to Advanced user
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setAdvancedUserType();
        preferencesEditPage.clickSaveAndView();
        this.profilePage.switchToPreferences();
        Assert.assertTrue(getDriver().getPageSource().contains("?editor=object"));
    }

    /** Functionality check: changing the default editor. */
    @Test
    public void testChangeDefaultEditor()
    {
        this.profilePage.gotoPage();
        PreferencesUserProfilePage preferencesPage = this.profilePage.switchToPreferences();

        // Setting to Text Editor
        PreferencesEditPage preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorText();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.profilePage.switchToPreferences();
        this.profilePage.clickEdit();
        RenderedWebElement about = ((RenderedWebElement) getDriver().findElement(By.id("XWiki.XWikiUsers_0_comment")));
        Assert.assertNotNull(about);
        Assert.assertTrue(about.isDisplayed());
        Assert.assertTrue(getDriver().findElements(By.className("xRichTextEditor")).isEmpty());

        // Setting to WYSIWYG Editor
        this.profilePage.gotoPage();
        preferencesPage = this.profilePage.switchToPreferences();
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorWysiwyg();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.profilePage.switchToPreferences();
        this.profilePage.clickEdit();
        about = ((RenderedWebElement) getDriver().findElement(By.id("XWiki.XWikiUsers_0_comment")));
        Assert.assertNotNull(about);
        Assert.assertFalse(about.isDisplayed());
        Assert.assertTrue(getDriver().findElement(By.className("xRichTextEditor")) != null);

        // Setting to Default Editor
        this.profilePage.gotoPage();
        preferencesPage = this.profilePage.switchToPreferences();
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorDefault();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.profilePage.switchToPreferences();
        this.profilePage.clickEdit();
        about = ((RenderedWebElement) getDriver().findElement(By.id("XWiki.XWikiUsers_0_comment")));
        Assert.assertNotNull(about);
        Assert.assertFalse(about.isDisplayed());
        Assert.assertTrue(getDriver().findElement(By.className("xRichTextEditor")) != null);
    }

    /**
     * Check that the content of the first comment isn't used as the "About" information in the user profile. See
     * XAADMINISTRATION-157.
     */
    @Test
    public void testCommentDoesntOverrideAboutInformation()
    {
        String commentContent = "this is from a comment";
        ViewPage profile = this.getUtil().gotoPage("XWiki", "Admin");
        int commentId = -1;
        try {
            commentId = profile.openCommentsDocExtraPane().postComment(commentContent);
            getDriver().navigate().refresh();
            Assert.assertFalse("Comment content was used as profile information",
                profile.getContent().contains(commentContent));
        } finally {
            if (commentId != -1) {
                profile.openCommentsDocExtraPane().deleteComment(commentId);
            }
        }
    }
}
