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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.administration.test.po.ProfileUserProfilePage;
import org.xwiki.tag.test.po.TaggablePage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.InlinePage;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.ObjectEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

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
        ProfileUserProfilePage pupp = ProfileUserProfilePage.gotoPage("Admin");
        // Clicking edit should perform inline editing.
        pupp.edit();
        pupp.waitForProfileEditionToLoad();
        Assert.assertTrue(new ViewPage().isInlinePage());
    }

    /* See XE-168 and XWIKI-6992 */
    @Test
    public void testInlineEditCanChangeTitle()
    {
        String title = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit", "editor=inline&title=" + title);
        InlinePage inlinePage = new InlinePage();
        // Check if the title specified on the request is properly displayed.
        Assert.assertEquals(title, inlinePage.getDocumentTitle());
        // Check if the title specified on the request is displayed in the document hierarchy.
        Assert.assertTrue(inlinePage.getBreadcrumbContent().contains(title));
        // Save the document and check again the displayed title and the document hierarchy.
        ViewPage viewPage = inlinePage.clickSaveAndView();
        Assert.assertEquals(title, viewPage.getDocumentTitle());
        Assert.assertTrue(viewPage.getBreadcrumbContent().contains(title));
    }

    /* See XE-168 */
    @Test
    public void testInlineEditCanChangeParent()
    {
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit", "editor=inline&parent=Main.WebHome");
        ViewPage vp = new InlinePage().clickSaveAndView();
        Assert.assertTrue(vp.hasBreadcrumbContent("Wiki Home", false));
    }

    /* See XWIKI-2389 */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    public void testInlineEditPreservesTitle()
    {
        String title = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "save", "title=" + title);
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
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "save", "parent=Blog.WebHome");
        ViewPage vp = new ViewPage();
        Assert.assertTrue(vp.hasBreadcrumbContent("The Wiki Blog", false));
        InlinePage ip = vp.editInline();
        ViewPage vp2 = ip.clickSaveAndView();
        Assert.assertTrue(vp2.hasBreadcrumbContent("The Wiki Blog", false));
    }

    /* See XWIKI-2199 */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    public void testInlineEditPreservesTags()
    {
        String tag1 = RandomStringUtils.randomAlphanumeric(4);
        String tag2 = RandomStringUtils.randomAlphanumeric(4);
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "save", "tags=" + tag1 + "|" + tag2);
        TaggablePage taggablePage = new TaggablePage();
        Assert.assertTrue(taggablePage.hasTag(tag1));
        Assert.assertTrue(taggablePage.hasTag(tag2));
        taggablePage.editInline().clickSaveAndView();
        taggablePage = new TaggablePage();
        Assert.assertTrue(taggablePage.hasTag(tag1));
        Assert.assertTrue(taggablePage.hasTag(tag2));
    }

    /**
     * Tests that pages can override the default property display mode using $context.setDisplayMode. See XWIKI-2436.
     */
    @Test
    public void testEditModeCanBeSet()
    {
        int step = 0;
        try {
            ProfileUserProfilePage pupp = ProfileUserProfilePage.gotoPage("Admin");

            // Overwrite the sheet that is automatically applied.
            ObjectEditPage objectEditor = pupp.editObjects();
            objectEditor.addObject("XWiki.DocumentSheetBinding");
            step++;

            WikiEditPage wep = objectEditor.editWiki();
            // Overwrite the default display mode and manually call the sheet.
            wep.setContent("{{velocity}}$xcontext.setDisplayMode('edit'){{/velocity}}\n\n"
                + "{{include document=\"XWiki.XWikiUserSheet\" /}}");
            wep.clickSaveAndView();
            step++;

            Assert.assertTrue(getDriver().getPageSource().contains("XWiki.XWikiUsers_0_last_name"));
        } finally {
            if (step > 0) {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("classname", "XWiki.DocumentSheetBinding");
                parameters.put("classid", "0");
                if (step > 1) {
                    // Reset the content.
                    parameters.put("xredirect", getUtil().getURL("XWiki", "Admin", "save", "content="));
                }
                // Reset the default sheet.
                getUtil().gotoPage("XWiki", "Admin", "objectremove", parameters);
            }
        }
    }
}
