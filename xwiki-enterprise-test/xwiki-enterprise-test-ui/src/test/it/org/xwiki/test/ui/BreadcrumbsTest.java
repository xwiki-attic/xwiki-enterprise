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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Test Breadcrumbs.
 * 
 * @version $Id$
 * @since 2.7RC1
 */
public class BreadcrumbsTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil(), getDriver());

    private static final String PARENT_TITLE = "Parent page";

    private static final String CHILD_TITLE = "Child page";

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testBreadcrumbs()
    {
        // Delete the page to reset the rights on it (since the test below modifies them).
        getUtil().deletePage("BreadcrumbsTest", "testBreadcrumbs");

        String parentPageName = getTestMethodName() + "ParentPage";
        String parentPageFullName = getTestClassName() + "." + parentPageName;

        getUtil().createPage(getTestClassName(), parentPageName, null, PARENT_TITLE);
        ViewPage vp = getUtil().createPage(getTestClassName(), getTestMethodName(), null, CHILD_TITLE, null,
            parentPageFullName);

        // Verify standard breadcrumb behavior.
        Assert.assertTrue(vp.hasBreadcrumbContent(PARENT_TITLE, false));
        Assert.assertTrue(vp.hasBreadcrumbContent(CHILD_TITLE, true));
        
        // Remove view rights on the Test.ParentPage page to everyone except Admin user so that we can verify that the
        // breadcrumb of the child page doesn't display pages for which you don't have view rights to.
        getUtil().addObject(getTestClassName(), parentPageName, "XWiki.XWikiRights",
            "levels", "view",
            "users", "XWiki.Admin",
            "allow", "1");
        
        // Log out...
        getUtil().forceGuestUser();

        // Verify breadcrumbs are only displayed for pages for which you have the view right.
        vp = getUtil().gotoPage(getTestClassName(), getTestMethodName());
        Assert.assertFalse(vp.hasBreadcrumbContent(PARENT_TITLE, false));
        Assert.assertTrue(vp.hasBreadcrumbContent(CHILD_TITLE, true));
        Assert.assertTrue(vp.hasBreadcrumbContent(parentPageFullName, false));
    }
}
