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
package org.xwiki.it.ui.blog;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.blog.elements.ManageCategoriesPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Test Blog categories. Tested features: add, rename, delete.
 * 
 * @version $Id$
 * @since 2.3M2
 */
public class BlogCategoriesTest extends AbstractAdminAuthenticatedTest
{
    private ManageCategoriesPage categoriesPage;

    private static final String WORD_CATEGORY = "SomeCategory";

    private static final String SPACE_CATEGORY = "Another Category";

    private static final String SPECIAL_CATEGORY = "The \"Do\"s & Don'ts";

    @Before
    public void setUp()
    {
        super.setUp();

        categoriesPage = new ManageCategoriesPage();

        // clean up
        getUtil().deletePage("Blog", WORD_CATEGORY);
        getUtil().deletePage("Blog", SPACE_CATEGORY);
        getUtil().deletePage("Blog", SPECIAL_CATEGORY);
    }

    @Test
    public void testCategoryAdd()
    {
        categoryAdd(WORD_CATEGORY);
    }

    @Test
    public void testCategoryRename()
    {
        categoryAdd(WORD_CATEGORY);
        categoryRename(WORD_CATEGORY, SPACE_CATEGORY);
    }

    @Test
    public void testCategoryRemove()
    {
        categoryAdd(WORD_CATEGORY);
        categoryRemove(WORD_CATEGORY);
    }

    @Test
    public void testCategoryAddSpace()
    {
        categoryAdd(SPACE_CATEGORY);
    }

    @Test
    public void testCategoryRenameSpace()
    {
        categoryAdd(SPACE_CATEGORY);
        categoryRename(SPACE_CATEGORY, SPECIAL_CATEGORY);
    }

    @Test
    public void testCategoryRemoveSpace()
    {
        categoryAdd(SPACE_CATEGORY);
        categoryRemove(SPACE_CATEGORY);
    }

    @Test
    public void testCategoryAddSpecial()
    {
        categoryAdd(SPECIAL_CATEGORY);
    }

    @Test
    public void testCategoryRenameSpecial()
    {
        categoryAdd(SPECIAL_CATEGORY);
        categoryRename(SPECIAL_CATEGORY, WORD_CATEGORY);
    }

    @Test
    public void testCategoryRemoveSpecial()
    {
        categoryAdd(SPECIAL_CATEGORY);
        categoryRemove(SPECIAL_CATEGORY);
    }

    /**
     * Helper method that adds a new category and checks for success
     * 
     * @param name
     */
    private void categoryAdd(String name)
    {
        categoriesPage.gotoPage();
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
        categoriesPage.gotoPage();
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
        categoriesPage.gotoPage();
        Assert.assertTrue(categoriesPage.isCategoryPresent(name));

        categoriesPage.deleteCategory(name);
        Assert.assertFalse(categoriesPage.isCategoryPresent(name));
    }
}
