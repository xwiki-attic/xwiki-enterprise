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
import org.xwiki.test.ui.framework.elements.editor.RightsEditPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.framework.elements.editor.RightsEditPage.Right;
import org.xwiki.test.ui.framework.elements.editor.RightsEditPage.State;

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
        WikiEditPage e = new WikiEditPage();
        e.switchToEdit("Test", "ViewPage");
        e.setParent("Test.ParentPage");
        e.setTitle("Child page");
        e.clickSaveAndView();

        e.switchToEdit("Test", "ParentPage");
        e.setTitle("Parent page");
        e.clickSaveAndView();

        // Verify standard breadcrumb behavior.
        ViewPage v = getUtil().gotoPage("Test", "ViewPage");
        Assert.assertTrue(v.getHierarchy().getText().contains("Parent page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("Child page"));

        RightsEditPage r = new RightsEditPage();
        r.switchToEdit("Test", "ParentPage");

        // Remove view rights on the Test.ParentPage page to everyone except Admin user so that we can verify that the
        // breadcrumb of the child page doesn't display pages for which you don't have view rights to.
        r.switchToUsers();
        r.setRight("Admin", Right.VIEW, State.ALLOW);
        // Log out...
        getUtil().setSession(null);

        // Verify breadcrumbs are only displayed for pages for which you have the view right.
        v = getUtil().gotoPage("Test", "ViewPage");
        Assert.assertFalse(v.getHierarchy().getText().contains("Parent page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("Child page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("ParentPage"));
    }
}
