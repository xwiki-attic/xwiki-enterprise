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
package org.xwiki.it.ui;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.elements.editor.RightsEditPage;
import org.xwiki.it.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.it.ui.framework.elements.editor.RightsEditPage.Right;
import org.xwiki.it.ui.framework.elements.editor.RightsEditPage.State;

/**
 * Test various parts of the UI.
 * 
 * @version $Id$
 * @since 2.5M1
 */
public class UITest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage("Test", "ViewPage");
    }

    @Test
    public void testBreadcrumbs()
    {
        // Create a class with a string property
        WikiEditPage e = new WikiEditPage();
        e.switchToEdit("Test", "ViewPage");
        e.setParent("Test.ParentPage");
        e.setTitle("Child page");
        e.clickSaveAndView();

        e.switchToEdit("Test", "ParentPage");
        e.setTitle("Parent page");
        e.clickSaveAndView();

        ViewPage v = getUtil().gotoPage("Test", "ViewPage");
        Assert.assertTrue(v.getHierarchy().getText().contains("Parent page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("Child page"));

        RightsEditPage r = new RightsEditPage();
        r.switchToEdit("Test", "ParentPage");
        r.switchToUsers();
        r.setRight("Admin", Right.VIEW, State.ALLOW);
        getUtil().setSession(null);

        v = getUtil().gotoPage("Test", "ViewPage");
        Assert.assertFalse(v.getHierarchy().getText().contains("Parent page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("Child page"));
        Assert.assertTrue(v.getHierarchy().getText().contains("ParentPage"));
    }
}
