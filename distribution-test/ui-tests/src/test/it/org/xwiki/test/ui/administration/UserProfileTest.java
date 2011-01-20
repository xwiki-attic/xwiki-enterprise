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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.administration.elements.PreferencesUserProfilePage;
import org.xwiki.test.ui.administration.elements.ProfileUserProfilePage;
import org.xwiki.test.ui.framework.AbstractTest;
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
public class UserProfileTest extends AbstractTest
{
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

    private static final String WYSIWYG_EDITOR = "Wysiwyg";

    private static final String TEXT_EDITOR = "Text";

    private static final String DEFAULT_EDITOR = "-";

    private static final String SIMPLE_USER = "Simple";

    private static final String ADVANCED_USER = "Advanced";

    private ProfileUserProfilePage customProfilePage;

    private String userName;

    @Before
    public void setUp()
    {
        userName = RandomStringUtils.randomAlphanumeric(5);
        String password = RandomStringUtils.randomAlphanumeric(6);
        getUtil().registerLoginAndGotoPage(userName, password, "Main");
        customProfilePage = new ProfileUserProfilePage(userName);
        customProfilePage.gotoPage();
    }

    /** Functionality check: changing profile information. */
    @Test
    public void testEditProfile()
    {
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
        Assert.assertEquals(StringUtils.substringBefore(USER_EMAIL, "@"), StringUtils.substringBefore(customProfilePage
            .getUserEmail(), "@"));
        Assert.assertEquals(USER_PHONE, customProfilePage.getUserPhone());
        Assert.assertEquals(USER_ADDRESS, customProfilePage.getUserAddress());
        Assert.assertEquals(USER_BLOG, customProfilePage.getUserBlog());
        Assert.assertEquals(USER_BLOGFEED, customProfilePage.getUserBlogFeed());
    }

    /** Functionality check: changing the profile picture. */
    @Test
    public void testChangeAvatarImage()
    {
        ChangeAvatarPage changeAvatarImage = this.customProfilePage.changeAvatarImage();
        changeAvatarImage.setAvatarImage(IMAGE_LOCATION);
        changeAvatarImage.submit();
        Assert.assertEquals(IMAGE_NAME, this.customProfilePage.getAvatarImageName());
    }

    /** Functionality check: changing the password. */
    @Test
    public void testChangePassword()
    {
        // Change the password
        PreferencesUserProfilePage preferencesPage = this.customProfilePage.switchToPreferences();
        ChangePasswordPage changePasswordPage = preferencesPage.changePassword();
        String newPassword = RandomStringUtils.randomAlphanumeric(6);
        changePasswordPage.changePassword(newPassword, newPassword);
        changePasswordPage.submit();

        // Logout
        getUtil().setSession(null);
        HomePage home = new HomePage();
        home.gotoPage();
        Assert.assertFalse(home.isAuthenticated());

        // Login with the new password
        getDriver().get(getUtil().getURLToLoginAs(userName, newPassword));
        Assert.assertTrue(home.isAuthenticated());
    }

    /** Functionality check: changing the user type. */
    @Test
    public void testChangeUserProfile()
    {
        PreferencesUserProfilePage preferencesPage = this.customProfilePage.switchToPreferences();
        // Setting to Simple user
        PreferencesEditPage preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setSimpleUserType();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.customProfilePage.switchToPreferences();
        Assert.assertEquals(SIMPLE_USER, this.customProfilePage.getUserType());

        // Setting to Advanced user
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setAdvancedUserType();
        preferencesEditPage.clickSaveAndView();
        this.customProfilePage.switchToPreferences();
        Assert.assertEquals(ADVANCED_USER, this.customProfilePage.getUserType());
    }

    /** Functionality check: changing the default editor. */
    @Test
    public void testChangeDefaultEditor()
    {
        PreferencesUserProfilePage preferencesPage = this.customProfilePage.switchToPreferences();

        // Setting to Text Editor
        PreferencesEditPage preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorText();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.customProfilePage.switchToPreferences();
        Assert.assertEquals(TEXT_EDITOR, this.customProfilePage.getDefaultEditorToUse());

        // Setting to WYSIWYG Editor
        this.customProfilePage.gotoPage();
        preferencesPage = this.customProfilePage.switchToPreferences();
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorWysiwyg();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.customProfilePage.switchToPreferences();
        Assert.assertEquals(WYSIWYG_EDITOR, this.customProfilePage.getDefaultEditorToUse());

        // Setting to Default Editor
        this.customProfilePage.gotoPage();
        preferencesPage = this.customProfilePage.switchToPreferences();
        preferencesEditPage = preferencesPage.editPreferences();
        preferencesEditPage.setDefaultEditorDefault();
        preferencesEditPage.clickSaveAndView();
        preferencesPage = this.customProfilePage.switchToPreferences();
        Assert.assertEquals(DEFAULT_EDITOR, this.customProfilePage.getDefaultEditorToUse());
    }

    /**
     * Check that the content of the first comment isn't used as the "About" information in the user profile. See
     * XAADMINISTRATION-157.
     */
    @Test
    public void testCommentDoesntOverrideAboutInformation()
    {
        String commentContent = "this is from a comment";
        ViewPage profile = this.getUtil().gotoPage("XWiki", userName);
        int commentId = -1;
        try {
            commentId = profile.openCommentsDocExtraPane().postComment(commentContent);
            getDriver().navigate().refresh();
            Assert.assertFalse("Comment content was used as profile information", profile.getContent().contains(
                commentContent));
        } finally {
            if (commentId != -1) {
                profile.openCommentsDocExtraPane().deleteComment(commentId);
            }
        }
    }
}
