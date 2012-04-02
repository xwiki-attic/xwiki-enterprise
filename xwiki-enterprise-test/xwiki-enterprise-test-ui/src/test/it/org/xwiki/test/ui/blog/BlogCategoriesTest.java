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
package org.xwiki.test.ui.blog;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.po.blog.ManageCategoriesPage;

/**
 * Test Blog categories. Tested features: add, rename, delete.
 * 
 * @version $Id$
 * @since 2.3M2
 */
public class BlogCategoriesTest extends AbstractAdminAuthenticatedTest
{
    /**
     * We make sure to have spaces and special chars to ensure categories can be named with any char.
     */
    private static final String CATEGORY = "The \"Do\"s & Don'ts";

    private static final String CATEGORY_RENAME = "New \"categor'y\"";

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        // clean up
        getUtil().deletePage("Blog", CATEGORY);
        getUtil().deletePage("Blog", CATEGORY_RENAME);
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testCategoryAddRenameRemove()
    {
        categoryAdd(CATEGORY);
        categoryRename(CATEGORY, CATEGORY_RENAME);
        categoryRemove(CATEGORY_RENAME);
    }

    /**
     * Helper method that adds a new category and checks for success
     * 
     * @param name
     */
    private void categoryAdd(String name)
    {
        ManageCategoriesPage categoriesPage = ManageCategoriesPage.gotoPage();
        Assert.assertFalse(categoriesPage.isCategoryPresent(name));

        categoriesPage.clickAddCategory();
        categoriesPage.addCategory(name);
        Assert.assertTrue(categoriesPage.isCategoryPresent(name));
    }

    /**
     * Helper method that renames a category and checks for success
     * 
     * @param fromName source name, must exist
     * @param toName target name, must not exist
     */
    private void categoryRename(String fromName, String toName)
    {
        ManageCategoriesPage categoriesPage = ManageCategoriesPage.gotoPage();
        Assert.assertTrue(categoriesPage.isCategoryPresent(fromName));
        Assert.assertFalse(categoriesPage.isCategoryPresent(toName));

        categoriesPage.renameCategory(fromName, toName);
        Assert.assertFalse(categoriesPage.isCategoryPresent(fromName));
        Assert.assertTrue(categoriesPage.isCategoryPresent(toName));
    }

    /**
     * Helper method that removes a category and checks for success
     * 
     * @param name category name, must exist
     */
    private void categoryRemove(String name)
    {
        ManageCategoriesPage categoriesPage = ManageCategoriesPage.gotoPage();
        Assert.assertTrue(categoriesPage.isCategoryPresent(name));

        categoriesPage.deleteCategory(name);
        Assert.assertFalse(categoriesPage.isCategoryPresent(name));
    }
}
