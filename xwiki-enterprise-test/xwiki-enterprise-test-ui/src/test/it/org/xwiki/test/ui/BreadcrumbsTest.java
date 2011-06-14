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

import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

/**
 * Test Breadcrumbs.
 * 
 * @version $Id$
 * @since 2.7RC1
 */
public class BreadcrumbsTest extends AbstractAdminAuthenticatedTest
{
    @Test
    public void testBreadcrumbs()
    {
        // Delete the page to reset the rights on it (since the test below modifies them).
        getUtil().deletePage("BreadcrumbsTest", "testBreadcrumbs");

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("BreadcrumbsTest", "testBreadcrumbs");
        wep.setParent("BreadcrumbsTest.testBreadcrumbsParentPage");
        wep.setTitle("Child page");
        wep.clickSaveAndView();

        wep.switchToEdit("BreadcrumbsTest", "testBreadcrumbsParentPage");
        wep.setTitle("Parent page");
        wep.clickSaveAndView();

        // Verify standard breadcrumb behavior.
        ViewPage vp = getUtil().gotoPage("BreadcrumbsTest", "testBreadcrumbs");
        Assert.assertTrue(vp.hasBreadcrumbContent("Parent page", false));
        Assert.assertTrue(vp.hasBreadcrumbContent("Child page", true));
        
        // Remove view rights on the Test.ParentPage page to everyone except Admin user so that we can verify that the
        // breadcrumb of the child page doesn't display pages for which you don't have view rights to.
        getUtil().addObject("BreadcrumbsTest", "testBreadcrumbsParentPage", "XWiki.XWikiRights",
            "levels", "view",
            "users", "XWiki.Admin",
            "allow", "1");
        
        // Log out...
        getUtil().forceGuestUser();

        // Verify breadcrumbs are only displayed for pages for which you have the view right.
        vp = getUtil().gotoPage("BreadcrumbsTest", "testBreadcrumbs");
        Assert.assertFalse(vp.hasBreadcrumbContent("Parent page", false));
        Assert.assertTrue(vp.hasBreadcrumbContent("Child page", true));
        Assert.assertTrue(vp.hasBreadcrumbContent("BreadcrumbsTest.testBreadcrumbsParentPage", false));
    }
}
