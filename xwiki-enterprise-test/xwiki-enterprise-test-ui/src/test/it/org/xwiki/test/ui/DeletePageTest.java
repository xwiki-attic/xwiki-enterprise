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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.po.xe.HomePage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.ConfirmationPage;
import org.xwiki.test.ui.po.DeletePageOutcomePage;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Tests the Delete button present in the top level menu.
 * 
 * @version $Id$
 * @since 3.0M3
 */
public class DeletePageTest extends AbstractAdminAuthenticatedTest
{
    private ViewPage viewPage;

    private static final String LOGGED_USERNAME = "Administrator";

    private static final String CONFIRMATION = "The document has been deleted.";

    private static final String DELETE_ACTION = "delete";

    private static final String SPACE_VALUE = "Test";

    private static final String PAGE_VALUE = "DeletePageTest";

    private static final String PAGE_CONTENT = "This page is used for testing delete functionality";

    private static final String PAGE_TITLE = "Page title that will be deleted";

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        // Create a new Page that will be deleted
        this.viewPage = getUtil().createPage(SPACE_VALUE, PAGE_VALUE, PAGE_CONTENT, PAGE_TITLE);
    }

    @Test
    public void testDeleteOkWhenConfirming()
    {
        ConfirmationPage confirmationPage = this.viewPage.delete();
        // This tests for regression of XWIKI-1388
        Assert.assertNotNull("The interface should not show the user as logged out while deleting page",
            confirmationPage.getCurrentUser());
        confirmationPage.clickYes();
        DeletePageOutcomePage deleteOutcome = new DeletePageOutcomePage();
        Assert.assertEquals(LOGGED_USERNAME, deleteOutcome.getPageDeleter());
        Assert.assertEquals(CONFIRMATION, deleteOutcome.getMessage());
    }

    /**
     * Verify that we can delete a page without showing the confirmation dialog box and that we can redirect to any page
     * we want when the delete is done.
     */
    @Test
    public void testDeletePageCanSkipConfirmationAndDoARedirect()
    {
        HomePage homePage = new HomePage();
        getUtil().gotoPage(SPACE_VALUE, PAGE_VALUE, DELETE_ACTION,
            "confirm=1&xredirect=" + homePage.getURL());
        Assert.assertTrue(homePage.isOnHomePage());
    }

    /**
     * Verify that we can skip the default delete result page and instead redirect to any page we want.
     */
    @Test
    public void testDeletePageCanDoRedirect()
    {
        HomePage homePage = new HomePage();
        getUtil().gotoPage(SPACE_VALUE, PAGE_VALUE, DELETE_ACTION, "xredirect=" + homePage.getURL());
        WebElement yesButton = getDriver().findElement(By.xpath("//input[@value='yes']"));
        yesButton.click();
        Assert.assertTrue(homePage.isOnHomePage());
    }

    /**
     * Verify that hitting cancel on the delete confirmation dialog box goes back to the page being deleted.
     */
    @Test
    public void testDeletePageGoesToOriginalPageWhenCancelled()
    {
        this.viewPage.delete().clickNo();
        Assert.assertTrue(getDriver().getCurrentUrl().equals(getUtil().getURL(SPACE_VALUE, PAGE_VALUE)));
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    public void testDeletePageIsImpossibleWhenNoDeleteRights()
    {
        // Logs out to be guest and not have the right to delete
        this.viewPage.logout();
        Assert.assertFalse(this.viewPage.canDelete());
    }
}
