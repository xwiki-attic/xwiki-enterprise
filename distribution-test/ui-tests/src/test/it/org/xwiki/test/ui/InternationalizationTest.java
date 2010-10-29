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
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.framework.elements.CreatePagePage;
import org.xwiki.test.ui.framework.elements.CreateSpacePage;
import org.xwiki.test.ui.xe.elements.HomePage;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Validates the support for non-ASCII characters.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class InternationalizationTest extends AbstractAdminAuthenticatedTest
{
    private HomePage homePage;

    @Before
    public void setUp()
    {
        super.setUp();

        getUtil().deletePage("\u0219", "WebHome");
        getUtil().deletePage("Main", "\u0219");

        this.homePage = new HomePage();
        this.homePage.gotoPage();
    }

    /**
     * Checks that non-ASCII characters are allowed in the space name.
     */
    @Test
    public void testCreateNonAsciiSpace()
    {

        CreateSpacePage createSpacePage = this.homePage.createSpace();
        WYSIWYGEditPage editPage = createSpacePage.createSpace("\u0219");

        // Verify the title field
        Assert.assertEquals("\u0219", editPage.getDocumentTitle());

        // Verify the document space in the metadata
        Assert.assertEquals("\u0219", editPage.getMetaDataValue("space"));

        // Save the space to verify it can be saved with a non-ascii name
        ViewPage savedPage = editPage.save();
        Assert.assertEquals("\u0219", savedPage.getMetaDataValue("space"));
    }

    /**
     * Checks that non-ASCII characters are allowed in the page name.
     */
    @Test
    public void testCreateNonAsciiPage()
    {
        CreatePagePage createPagePage = this.homePage.createPage();
        WYSIWYGEditPage editPage = createPagePage.createPage("Main", "\u0219");

        // Verify the title field
        Assert.assertEquals("\u0219", editPage.getDocumentTitle());

        // Verify the document name in the metadata
        Assert.assertEquals("\u0219", editPage.getMetaDataValue("page"));

        // Save the page to verify it can be saved with a non-ascii name
        ViewPage savedPage = editPage.save();
        Assert.assertEquals("\u0219", savedPage.getMetaDataValue("page"));
    }
}
