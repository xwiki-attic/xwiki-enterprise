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
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Verify the keyboard shortcuts feature of XWiki.
 * 
 * @version $Id$
 * @since 2.6RC1
 */
public class KeyboardShortcutsTest extends AbstractAdminAuthenticatedTest
{
    private TestUtils util = new TestUtils();

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void testKeyboardShortcuts()
    {
        ViewPage vp = util.gotoPage("Sandbox", "WebHome");

        // Test default edit mode (WYSIWYG for Sandbox.WebHome) key
        vp.sendKeys("e");

        // Test Cancel key
        vp.sendKeys(Keys.chord(Keys.ALT, "c"));
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInViewMode());

        // Test Wiki edit key
        vp.sendKeys("k");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInWikiEditMode());

        // Test WYSIWYG edit mode key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("g");

        // Test Inline Form edit mode key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("f");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInInlineEditMode());

        // Test Rights edit mode key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("r");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInRightsEditMode());

        // Test Object edit mode key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("o");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInObjectEditMode());

        // Test Class edit mode key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("s");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInClassEditMode());

        // Test Delete key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys(Keys.DELETE);
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInDeleteMode());

        // Test Rename key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys(Keys.F2);
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInRenameMode());

        // Test View Source key
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("d");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(util.isInSourceViewMode());
    }
}
