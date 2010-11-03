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
import org.openqa.selenium.Keys;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.TestUtils;
import org.xwiki.test.ui.framework.elements.ViewPage;

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
    public void testKeyboardShortcuts()
    {
        ViewPage vp = this.util.gotoPage("Sandbox", "WebHome");

        // Test default edit mode (WYSIWYG for sandbox webhome)
        vp.sendKeys("e");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInWYSIWYGEditMode());

        // Test Cancel shortcut
        vp.sendKeys(Keys.ALT, "c");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInViewMode());

        // Test Wiki edit shortcut
        vp.sendKeys("k");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInWikiEditMode());

        // Test WYSIWYG edit mode
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("e");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInWYSIWYGEditMode());

        // Test Inline edit mode
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("f");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInInlineEditMode());

        // Test Rights edit mode
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("r");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInRightsEditMode());

        // Test Object edit mode
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("o");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInObjectEditMode());

        // Test Class edit mode
        vp = this.util.gotoPage("Sandbox", "WebHome");
        vp.sendKeys("s");
        vp.waitUntilPageIsLoaded();
        Assert.assertTrue(vp.isInClassEditMode());
    }
}
