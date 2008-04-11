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

    public void testCreateGroup()
    {
        clickLinkWithText("Administration");
        clickLinkWithText("Groups");
        clickLinkWithText("Add new group", false);
        setFieldValue("newgroupi", "NewGroup");
        getSelenium().click("//input[@value='Create group']");
        getSelenium().waitForPageToLoad("10000");

        assertTextPresent("NewGroup");

        //
        // GIVE GROUP "VIEW" RIGHT ON WIKI AND "EDIT" RIGHT ON Main.WebHome PAGE
        //

        clickLinkWithText("Global Rights");
        getSelenium().click("uorg");
        getSelenium().click("//tbody/tr[td/a=\"NewGroup\"]/td[2]/img");

        open("/xwiki/bin/view/Main/WebHome");
        clickLinkWithText("Page access rights");
        getSelenium().click("uorg");
        getSelenium().click("//tbody/tr[td/a=\"NewGroup\"]/td[3]/img");

        //
        // DELETE GROUP
        //

        // FIXME : find a way to delete user using groups administration
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

    public void testCreateAnExistingGroup()
    {
        clickLinkWithText("Administration");
        clickLinkWithText("Groups");
        getSelenium().setSpeed("1000");
        clickLinkWithText("Add new group", false);
        setFieldValue("newgroupi", "Admin");
        getSelenium().click("//input[@value='Create group']");
        assertEquals(
            "Admin cannot be used for the group name, as another document with this name already exists.",
            this.getSelenium().getAlert());
    }

    public void testCreateAndDeleteUser()
    {
        //
        // CREATE USER
        //

        clickLinkWithText("Administration");
        clickLinkWithText("Users");
        getSelenium().setSpeed("1000");
        clickLinkWithText("Add new user", false);
        setFieldValue("register_first_name", "New");
        setFieldValue("register_last_name", "User");
        setFieldValue("xwikiname", "NewUser");
        setFieldValue("register_password", "NewUser");
        setFieldValue("register2_password", "NewUser");
        setFieldValue("register_email", "new.user@xwiki.org");
        getSelenium().click("//input[@value='Save']");
        getSelenium().waitForPageToLoad("10000");

        assertTextPresent("NewUser");

        open("/xwiki/bin/view/XWiki/XWikiAllGroup");

        // Validate XWIKI-2280: Cannot create new users using the Right Management UI
        assertTextPresent("XWiki.NewUser");

        //
        // DELETE USER
        //

        // clickLinkWithText("Administration");
        // clickLinkWithText("Users");
        // getSelenium().chooseOkOnNextConfirmation();
        // open("/xwiki/bin/admin/XWiki/XWikiUsers?editor=users&space=XWiki");
        // getSelenium().click("//tbody/tr[td/a=\"NewUser\"]/td/img[@title='Delete']");

        // FIXME : find a way to delete user using users administration
        open("/xwiki/bin/view/XWiki/NewUser");
        clickDeletePage();
        clickLinkWithLocator("//input[@value='yes']");

        open("/xwiki/bin/view/XWiki/XWikiAllGroup");

        // Validate XWIKI-2281: When a user is removed it's not removed from the groups it belongs
        // to
        assertTextNotPresent("XWiki.NewUser");
    }
}
