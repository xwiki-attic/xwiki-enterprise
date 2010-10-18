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
package org.xwiki.test.selenium;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;
import junit.framework.AssertionFailedError;

/**
 * Verify deletion of pages.
 *
 * @version $Id$
 */
public class DeletePageTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify deletion of pages");
        suite.addTestSuite(DeletePageTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testDeleteOkWhenConfirming()
    {
        createPage("Test", "DeleteTest", "some content");
        clickDeletePage();

        // This tests for regression of XWIKI-1388
        assertTrue("The interface should not show the user as logged out while deleting page", isAuthenticated());
        
        clickLinkWithLocator("//input[@value='yes']");
        assertTextPresent("The document has been deleted.");
    }

    /**
     * Verify that we can delete a page without showing the confirmation dialog box and that we
     * can redirect to any page we want when the delete is done.
     */
    public void testDeletePageCanSkipConfirmationAndDoARedirect()
    {
        createPage("Test", "DeleteTest", "some content");
        open("Test", "DeleteTest", "delete", "confirm=1&xredirect=" + getUrl("Main", "WebHome"));
        assertPage("Main", "WebHome");
    }

    /**
     * Verify that we can skip the default delete result page and instead redirect to any page we
     * want.
     */
    public void testDeletePageCanDoRedirect()
    {
        createPage("Test", "DeleteTest", "some content");
        open("Test", "DeleteTest", "delete", "xredirect=" + getUrl("Main", "WebHome"));
        clickLinkWithLocator("//input[@value='yes']");
        assertPage("Main", "WebHome");
    }

    /**
     * Verify that hitting cancel on the delete confirmation dialog box goes back to the page being
     * deleted.
     */
    public void testDeletePageGoesToOriginalPageWhenCancelled()
    {
        createPage("Test", "DeleteTestNoDelete", "some content");
        // Note: We call the page with a unique name as we're not going to delete it and it should
        // not interefere with others tests. We could always remove the DeleteTest page before any
        // test but it would take longer.
        open("Test", "DeleteTestNoDelete", "delete");
        clickLinkWithLocator("//input[@value='no']");
        assertPage("Test", "DeleteTestNoDelete");
    }

    public void testDeletePageIsImpossibleWhenNoDeleteRights()
    {
        // Ensure the user isn't logged in
        if (isAuthenticated()) {
            logout();
        }

        open("Main", "WebHome");

        // Note: Ideally we should have tested for the non existence of the Delete button element.
        // However, in order to isolate skin implementation from the test this would have required
        // adding a new isDeleteButtonPresent() method to the Skin Executor API. This would have
        // been a bit awkward as testing for the Delete button is only going to happen in this
        // test case and thus there's no need to share it with all the oher tests. This is why I
        // have chosen to reuse the existing clickDeletePage() method and test for an exception.
        try {
            clickDeletePage();
            fail("Should have failed here as the Delete button shouldn't be present");
        } catch (AssertionFailedError expected) {
            assertTrue(expected.getMessage().endsWith("isn't present."));
        }
    }
}
