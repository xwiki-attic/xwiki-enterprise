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

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.elements.editor.WikiEditPage;

/**
 * Test various character escaping bugs.
 * <p>
 * NOTE: {@link WebDriver#getPageSource()} XML-escapes the output in some cases, but not inside HTML comments
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class EscapeTest extends AbstractAdminAuthenticatedTest
{
    /** XML significant characters */
    private static final String XML_CHARS = "<>'&\"";

    /** Important SQL characters */
    private static final String SQL_CHARS = "'\\;";

    @Test
    public void testEditReflectedXSS()
    {
        // tests for XWIKI-4758, XML symbols should be escaped
        String page = "<!-- " + XML_CHARS + " -->";
        getUtil().gotoPage("Main", getUtil().escapeURL(page), "edit");
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testErrorTraceEscaping()
    {
        // tests for XWIKI-5170, XML symbols in the error trace should be escaped
        String rev = "</pre><!-- " + XML_CHARS + " -->";
        getUtil().gotoPage("Main", "WebHome", "viewrev", "rev=" + getUtil().escapeURL(rev));
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testEditorEscaping()
    {
        // tests for XWIKI-5164, XML symbols in editor parameter should be escaped
        String str = "\"<!-- " + XML_CHARS + " -->";

        getUtil().gotoPage("Main", "Page", "edit", "editor=" + getUtil().escapeURL(str));
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        getUtil().gotoPage("Main", "Page", "edit", "editor=wysiwyg&section=" + getUtil().escapeURL(str));
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        getUtil().gotoPage("Main", "Page", "edit", "editor=wiki&x-maximized=" + getUtil().escapeURL(str));
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testContentView()
    {
        // XWIKI-5205
        String test = getUtil().escapeURL("\"><pre><!-- " + XML_CHARS + " --></pre>");

        getUtil().gotoPage("Main", test, "view", "xpage=contentview");
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    @Test
    public void testMenuViewPlain()
    {
        // XWIKI-5209
        String space = getUtil().escapeURL("<!-- " + XML_CHARS + " -->");
        getUtil().gotoPage(space, "Test");
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    @Test
    public void testMenuViewLink()
    {
        // XWIKI-5209
        String space = getUtil().escapeURL("\"><pre><!-- " + XML_CHARS + " --></pre>");
        getUtil().gotoPage(space, "Test");
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    @Test
    public void testAdminEditor()
    {
        // XWIKI-5190
        String test = getUtil().escapeURL("\"<!-- " + XML_CHARS + " -->");

        getUtil().gotoPage("XWiki", "AdminSheet", "admin", "editor=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        // same page after redirect
        getUtil().gotoPage("Main", "WebHome", "view", "xpage=admin&editor=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testAdminSection()
    {
        // XWIKI-5190
        String test = getUtil().escapeURL("\"<!-- " + XML_CHARS + " -->");

        getUtil().gotoPage("XWiki", "AdminSheet", "admin", "section=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);

        // same page after redirect
        getUtil().gotoPage("Main", "WebHome", "view", "xpage=admin&section=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testAttachmentsInline()
    {
        // XWIKI-5191
        // the trick with HTML comment doesn't help here for some reason, need to produce correct HTML
        String chars = "\">asdf&nbsp;fdsa</a><a href=\"";
        String test = getUtil().escapeURL(chars);

        // need a page with attachments, Sandbox has an image attached by default
        getUtil().gotoPage("Sandbox", "WebHome", "view", "viewer=attachments&xredirect=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(chars) < 0);
    }

    @Test
    public void testBrowseWysiwygSQL()
    {
        // XWIKI-5193
        String test = getUtil().escapeURL(SQL_CHARS);
        getUtil().gotoPage("Main", "Test", "view", "xpage=browsewysiwyg&text=" + test);
        Assert.assertTrue(getDriver().findElements(By.xpath("//pre[@class='xwikierror']")).isEmpty());
        Assert.assertTrue(getDriver().getPageSource().indexOf(SQL_CHARS) < 0);
    }

    @Test
    public void testBrowseWysiwygPage()
    {
        // XWIKI-5193
        String test = "<!-- " + XML_CHARS + " -->";
        createPage("Main", test, test, "Bla bla");

        getUtil().gotoPage("Main", "Test", "view", "xpage=browsewysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(test) < 0);
    }

    @Test
    public void testWysiwygRecentViewsPage()
    {
        // XWIKI-5193
        String test = "<!-- " + XML_CHARS + " -->";
        createPage("Main", test, test, "Bla bla");

        getUtil().gotoPage("Main", "Test", "view", "xpage=recentdocwysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(test) < 0);
    }

    @Test
    public void testBrowseWysiwygPageLink()
    {
        // XWIKI-5193
        // the trick with HTML comment doesn't help here for some reason, need to produce correct HTML
        String test = "\">asdf&nbsp;fdsa<img onclick=\"'";
        createPage("Main", test, test, "Bla bla");

        getUtil().gotoPage("Main", "Test", "view", "xpage=browsewysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(test) < 0);
    }

    @Test
    public void testBrowseWysiwygSpace()
    {
        // XWIKI-5193
        String test = getUtil().escapeURL("bla\"<!-- " + XML_CHARS + " -->");

        getUtil().gotoPage(test, "Test", "view", "xpage=browsewysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testSearchWysiwygSQL()
    {
        // XWIKI-5344
        String test = getUtil().escapeURL(SQL_CHARS);
        getUtil().gotoPage("Main", "Test", "view", "xpage=searchwysiwyg&space=" + test);
        Assert.assertTrue(getDriver().findElements(By.xpath("//pre[@class='xwikierror']")).isEmpty());
        Assert.assertTrue(getDriver().getPageSource().indexOf(SQL_CHARS) < 0);
        getUtil().gotoPage("Main", "Test", "view", "xpage=searchwysiwyg&page=" + test);
        Assert.assertTrue(getDriver().findElements(By.xpath("//pre[@class='xwikierror']")).isEmpty());
        Assert.assertTrue(getDriver().getPageSource().indexOf(SQL_CHARS) < 0);
    }

    @Test
    public void testSearchWysiwygPageLink()
    {
        // XWIKI-5344
        // the trick with HTML comment doesn't help here for some reason, need to produce correct HTML
        String test = "\">asdf&nbsp;fdsa<img onclick=\"'";
        createPage("Main", test, test, "Bla bla");

        getUtil().gotoPage("Main", "Test", "view", "xpage=searchwysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(test) < 0);
    }

    @Test
    public void testSearchWysiwygSpace()
    {
        // XWIKI-5344
        String test = getUtil().escapeURL("bla\"<!-- " + XML_CHARS + " -->");

        getUtil().gotoPage(test, "Test", "view", "xpage=searchwysiwyg");
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testLoginRedirect()
    {
        String test = getUtil().escapeURL("bla\"<!-- " + XML_CHARS + " -->");
        getUtil().setSession(null);

        getUtil().gotoPage("XWiki", "XWikiLogin", "login", "xredirect=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    @Test
    public void testLoginSrid()
    {
        String test = getUtil().escapeURL("bla\"<!-- " + XML_CHARS + " -->");
        getUtil().setSession(null);

        getUtil().gotoPage("XWiki", "XWikiLogin", "login", "srid=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(XML_CHARS) < 0);
    }

    /**
     * Create a page with the given data if it does not exist and navigate to home page
     * 
     * @param space
     * @param page
     * @param title
     * @param content
     */
    private void createPage(String space, String page, String title, String content)
    {
        WikiEditPage testPage = new WikiEditPage();
        testPage.switchToEdit(space, page);
        if (!testPage.isNewDocument()) {
            return;
        }
        testPage.setTitle(title);
        testPage.setContent(content);
        testPage.clickSaveAndContinue();
        getUtil().gotoPage("Main", "WebHome");
    }

    @Test
    public void testChangesAll()
    {
        // XWIKI-5204
        String test = getUtil().escapeURL("\"><pre><!-- " + XML_CHARS + " --></pre>");

        getUtil().gotoPage("Main", "WebHome", "view", "xpage=changesall&type=" + test);
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    @Test
    public void testVersionSummary()
    {
        try {
            String test = "<!-- " + XML_CHARS + " -->";
            WikiEditPage e = getUtil().gotoPage("Test", "TestVersionSummary").clickEditWiki();
            e.setEditComment(test);
            ViewPage p = e.clickSaveAndView();
            // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
            // will race for completion. Let's wait for comments first.
            p.openCommentsDocExtraPane();
            // getCurrentVersionComment returns the text content, so an actual XML comment will not be included
            Assert.assertEquals(test, p.openHistoryDocExtraPane().getCurrentVersionComment());
        } finally {
            getUtil().deletePage("Test", "TestVersionSummary");
        }
    }

    @Test
    public void testImported()
    {
        String test = getUtil().escapeURL("<!-- " + XML_CHARS + " -->");

        getUtil().gotoPage("Main", "WebHome", "view", "xpage=imported&name=" + test);
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    @Test
    public void testEditActions()
    {
        String test = getUtil().escapeURL("\"><!-- " + XML_CHARS + " -->");

        getUtil().gotoPage("Main", "WebHome", "edit", "editor=wiki&comment=" + test);
        Assert.assertFalse(getDriver().getPageSource().contains(XML_CHARS));
    }

    /**
     * Go to a working page after each test run to prevent failures in {@link #setUp()}
     */
    @After
    public void tearDown()
    {
        getUtil().gotoPage("Main", "WebHome");
    }
}
