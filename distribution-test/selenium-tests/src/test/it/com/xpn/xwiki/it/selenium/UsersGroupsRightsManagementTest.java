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
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Verify the Users, Groups and Rights Management features of XWiki.
 * 
 * @version $Id: $
 */
public class UsersGroupsRightsManagementTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite =
            new XWikiTestSuite("Verify the Users, Groups and Rights Management features of XWiki");
        suite.addTestSuite(UsersGroupsRightsManagementTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * <ul>
     * <li>Validate group creation.</li>
     * <li>Validate groups administration print "0" members for empty group.</li>
     * <li>Validate group deletion.</li>
     * <li>Validate rights automatically cleaned from deleted groups.</li>
     * </ul>
     */
    public void testCreateAndDeleteGroup()
    {
        clickLinkWithText("Administration");
        clickLinkWithText("Groups");
        clickLinkWithText("Add new group", false);
        // Wait for lightbox
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Create new group') != -1;", "2000");
        setFieldValue("newgroupi", "NewGroup");
        getSelenium().click("//input[@value='Create group']");
        getSelenium().waitForPageToLoad("10000");

        // Validate that group has been created.
        assertTextPresent("NewGroup");

        // Validate XWIKI-1903: New UI - Empty group shows 1 member.
        assertEquals("Group NewGroup which is empty print more than 0 members", "0",
            getSelenium().getText("//tbody/tr[td/a=\"NewGroup\"]/td[2]"));

        // Give "view" global right to NewGroup on wiki
        clickLinkWithText("Global Rights");
        getSelenium().click("uorg");
        getSelenium().click("//tbody/tr[td/a=\"NewGroup\"]/td[2]/img");

        // Give "comment" right to NewGroup on Main.WebHome page
        open("/xwiki/bin/view/Main/WebHome");
        clickLinkWithText("Page access rights");
        getSelenium().click("uorg");
        getSelenium().click("//tbody/tr[td/a=\"NewGroup\"]/td[3]/img");

        //
        // Delete the newly created group and see if rights are cleaned
        //

        // FIXME : find a way to delete user using groups administration. See
        // #testCreateAndDeleteUser() for more.
        open("/xwiki/bin/view/XWiki/NewGroup");
        clickDeletePage();
        clickLinkWithLocator("//input[@value='yes']");

        // Validate XWIKI-2304: When a user or a group is removed it's not removed from rights
        // objects
        open("/xwiki/bin/edit/XWiki/XWikiPreferences?editor=object");
        assertTextNotPresent("NewGroup");
        open("/xwiki/bin/edit/Main/WebHome?editor=object");
        assertTextNotPresent("NewGroup");
    }

    /**
     * Validate that administration show error when trying to create an existing group.
     */
    public void testCreateAnExistingGroup()
    {
        clickLinkWithText("Administration");
        clickLinkWithText("Groups");
        clickLinkWithText("Add new group", false);
        // Wait for lightbox
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Create new group') != -1;", "2000");
        setFieldValue("newgroupi", "Admin");
        getSelenium().click("//input[@value='Create group']");
        getSelenium().setSpeed("1000");
        assertEquals(
            "Admin cannot be used for the group name, as another document with this name already exists.",
            this.getSelenium().getAlert());
    }

    /**
     * <ul>
     * <li>Validate user creation.</li>
     * <li>Validate user deletion.</li>
     * <li>Validate groups automatically cleaned from deleted users.</li>
     * </ul>
     */
    public void testCreateAndDeleteUser()
    {
        // Create the new user
        clickLinkWithText("Administration");
        clickLinkWithText("Users");
        clickLinkWithText("Add new user", false);
        // Wait for lightbox
        getSelenium().waitForCondition("selenium.page().bodyText().indexOf('Registration') != -1;", "2000");
        setFieldValue("register_first_name", "New");
        setFieldValue("register_last_name", "User");
        setFieldValue("xwikiname", "NewUser");
        setFieldValue("register_password", "NewUser");
        setFieldValue("register2_password", "NewUser");
        setFieldValue("register_email", "new.user@xwiki.org");
        getSelenium().click("//input[@value='Save']");
        waitPage();

        // Verify that the user is present in the table
        assertTextPresent("NewUser");

        // Verify that new users are automatically added to the XWikiAllGroup group. 
        open("/xwiki/bin/view/XWiki/XWikiAllGroup");
        assertTextPresent("XWiki.NewUser");

        // Delete the newly created user and see if groups are cleaned

        // FIXME: this is the code that should be use to delete a user but I can't makes it works
        // (the popup does not show up)
        // clickLinkWithText("Administration");
        // clickLinkWithText("Users");
        // getSelenium().chooseOkOnNextConfirmation();
        // open("/xwiki/bin/admin/XWiki/XWikiUsers?editor=users&space=XWiki");
        // getSelenium().click("//tbody/tr[td/a=\"NewUser\"]/td/img[@title='Delete']");

        // FIXME: find a way to delete user using users administration. See previous commented
        // code.
        open("/xwiki/bin/view/XWiki/NewUser");
        clickDeletePage();
        clickLinkWithLocator("//input[@value='yes']");

        // Verify that when a user is removed he's removed from the groups he belongs to.
        open("/xwiki/bin/view/XWiki/XWikiAllGroup");
        assertTextNotPresent("XWiki.NewUser");
    }
}
