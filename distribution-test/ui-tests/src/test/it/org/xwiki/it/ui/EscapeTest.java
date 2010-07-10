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
import org.openqa.selenium.WebDriver;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

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

    /**
     * Go to a working page after each test run to prevent failures in {@link #setUp()}
     */
    @After
    public void tearDown()
    {
        getUtil().gotoPage("Main", "WebHome");
    }
}
