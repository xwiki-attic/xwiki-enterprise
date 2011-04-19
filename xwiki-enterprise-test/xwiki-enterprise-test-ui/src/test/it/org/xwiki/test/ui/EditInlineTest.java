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

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.test.ui.administration.elements.ProfileUserProfilePage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.InlinePage;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

/**
 * Test Inline editing.
 *
 * @version $Id$
 * @since 3.0M2
 */
public class EditInlineTest extends AbstractAdminAuthenticatedTest
{
    // Note: We're not testing basic inline editing since this is already covered by the User Profile tests

    @Test
    public void testEditButtonTriggersInlineEditing()
    {
        ProfileUserProfilePage pupp = new ProfileUserProfilePage("Admin");
        pupp.gotoPage();
        // Clicking edit should perform inline editing.
        pupp.edit();
        pupp.waitForProfileEditionToLoad();
        Assert.assertTrue(new ViewPage().isInlinePage());
    }

    /* See XE-168 */
    @Test
    public void testInlineEditCanChangeTitle()
    {
        String title = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage("EditInlineTest", "testInlineEditCanChangeTitle", "inline", "title=" + title);
        ViewPage vp = new InlinePage().clickSaveAndView();
        Assert.assertEquals(title, vp.getDocumentTitle());
    }

    /* See XE-168 */
    @Test
    public void testInlineEditCanChangeParent()
    {
        getUtil().gotoPage("EditInlineTest", "testInlineEditCanChangeParent", "inline", "parent=Main.WebHome");
        ViewPage vp = new InlinePage().clickSaveAndView();
        Assert.assertTrue(vp.hasBreadcrumbContent("Welcome to your wiki"));
    }

    /* See XWIKI-2389 */
    @Test
    public void testInlineEditPreservesTitle()
    {
        String title = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage("EditInlineTest", "testInlineEditPreservesTitle", "save", "title=" + title);
        ViewPage vp = new ViewPage();
        Assert.assertEquals(title, vp.getDocumentTitle());
        InlinePage ip = vp.editInline();
        ViewPage vp2 = ip.clickSaveAndView();
        Assert.assertEquals(title, vp2.getDocumentTitle());
    }

    /* See XWIKI-2389 */
    @Test
    public void testInlineEditPreservesParent()
    {
        getUtil().gotoPage("EditInlineTest", "testInlineEditPreservesParent", "save", "parent=Blog.WebHome");
        ViewPage vp = new ViewPage();
        Assert.assertTrue(vp.hasBreadcrumbContent("The Wiki Blog"));
        InlinePage ip = vp.editInline();
        ViewPage vp2 = ip.clickSaveAndView();
        Assert.assertTrue(vp2.hasBreadcrumbContent("The Wiki Blog"));
    }

    /* See XWIKI-2199 */
    @Test
    public void testInlineEditPreservesTags()
    {
        String tag1 = RandomStringUtils.randomAlphanumeric(4);
        String tag2 = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage("EditInlineTest", "testInlineEditPreservesTags", "save", "tags=" + tag1 + "|" + tag2);
        ViewPage vp = new ViewPage();
        Assert.assertTrue(vp.hasTag(tag1));
        Assert.assertTrue(vp.hasTag(tag2));
        InlinePage ip = vp.editInline();
        ViewPage vp2 = ip.clickSaveAndView();
        Assert.assertTrue(vp2.hasTag(tag1));
        Assert.assertTrue(vp2.hasTag(tag2));
    }

    /**
     * Tests that pages can override the default property display mode using $context.setDisplayMode. See XWIKI-2436.
     */
    @Test
    public void testEditModeCanBeSet()
    {
        String initialContent = null;
        try {
            ProfileUserProfilePage pupp = new ProfileUserProfilePage("Admin");
            pupp.gotoPage();
            WikiEditPage wep = pupp.editWiki();
            initialContent = wep.getContent();
            wep.setContent("{{velocity}}$xcontext.setDisplayMode('edit'){{/velocity}}\n" + initialContent);
            wep.clickSaveAndView();
            Assert.assertTrue(getDriver().getPageSource().contains("XWiki.XWikiUsers_0_last_name"));
        } finally {
            if (initialContent != null) {
                getUtil().gotoPage("XWiki", "Admin", "save", "content=" + initialContent);
            }
        }
    }
}
