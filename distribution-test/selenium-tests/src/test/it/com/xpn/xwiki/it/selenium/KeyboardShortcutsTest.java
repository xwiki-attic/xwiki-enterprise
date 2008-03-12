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
package com.xpn.xwiki.it.selenium;

import junit.framework.Test;
import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the keyboard shortcuts feature of XWiki.
 *
 * @version $Id: $
 */
public class KeyboardShortcutsTest extends AbstractXWikiTestCase
{
    private static String mainHtmlElement = "xwikimaincontainer";

    private static int msToWaitAfterKeypress = 10000;

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the keyboard shortcuts feature of XWiki");
        suite.addTestSuite(KeyboardShortcutsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    private void testShortcutFromResultingTitle(String origURL, String shortcut,
        String expectedTitle) throws InterruptedException
    {
        testShortcutFromResultingTitle(origURL, shortcut, expectedTitle, false, false, false);
    }

    private void testShortcutFromResultingTitle(String origURL, String shortcut,
        String expectedTitle, boolean withCtrlModifier, boolean withAltModifier,
        boolean withShiftModifier) throws InterruptedException
    {
        testShortcut(origURL, shortcut, expectedTitle, withCtrlModifier, withAltModifier,
            withShiftModifier, true);
    }

    private void testShortcutFromTextPresent(String origURL, String shortcut,
        String text) throws InterruptedException
    {
        testShortcutFromTextPresent(origURL, shortcut, text, false, false, false);
    }

    private void testShortcutFromTextPresent(String origURL, String shortcut,
        String text, boolean withCtrlModifier, boolean withAltModifier,
        boolean withShiftModifier) throws InterruptedException
    {
        testShortcut(origURL, shortcut, text, withCtrlModifier, withAltModifier,
            withShiftModifier, false);
    }

    private void testShortcut(String origURL, String shortcut,
        String text, boolean withCtrlModifier, boolean withAltModifier,
        boolean withShiftModifier, boolean fromTitle) throws InterruptedException
    {
        open(origURL);
        if (withCtrlModifier) {
            getSelenium().controlKeyDown();
        }
        if (withAltModifier) {
            getSelenium().altKeyDown();
        }
        if (withShiftModifier) {
            getSelenium().shiftKeyDown();
        }
        keyPressAndWait(mainHtmlElement, shortcut, msToWaitAfterKeypress);
        if (withCtrlModifier) {
            getSelenium().controlKeyUp();
        }
        if (withAltModifier) {
            getSelenium().altKeyUp();
        }
        if (withShiftModifier) {
            getSelenium().shiftKeyUp();
        }
        if (fromTitle) {
            assertTitle(text);
        } else {
            assertTextPresent(text);
        }
    }

    public void testAllKeyboardShortcuts() throws InterruptedException
    {
        loginAsAdmin();

        String viewURL = "/xwiki/bin/view/Main/WebHome";
        String editURL = "/xwiki/bin/edit/Main/WebHome?editor=wiki";

        // e : default edit wysiswyg
        testShortcutFromResultingTitle(viewURL, "e", "Editing wysiwyg for Welcome to your wiki");
        // k : edit wiki
        testShortcutFromResultingTitle(viewURL, "k", "Editing Wiki for Welcome to your wiki");
        // g : edit wysiwyg
        testShortcutFromResultingTitle(viewURL, "g", "Editing wysiwyg for Welcome to your wiki");
        // f : edit inline
        testShortcutFromTextPresent(viewURL, "f", "Is minor edit:");
        // r : edit rights
        testShortcutFromTextPresent(viewURL, "r", "Welcome to the rights editor");
        // o : edit objects
        testShortcutFromTextPresent(viewURL, "o", "Welcome to the objects editor");
        // s : edit class
        testShortcutFromTextPresent(viewURL, "s",
            "Choose a property to edit or add a property to the class");
        // c : comments
        testShortcutFromTextPresent(viewURL, "c", "Comments for Welcome to your wiki");
        // a : attachments
        testShortcutFromTextPresent(viewURL, "a", "Attachments for Welcome to your wiki");        
        // h : history
        testShortcutFromTextPresent(viewURL, "h", "History of Welcome to your wiki");
        // d : code
        testShortcutFromTextPresent(viewURL, "d", "Wiki code for Welcome to your wiki");
        // Delete : delete
        testShortcutFromTextPresent(viewURL, "\\46",
            "Are you sure you wish to move the document to recycle bin");
        // F2 : rename
        testShortcutFromTextPresent(viewURL, "\\113", "Renaming Main.WebHome");

        // Alt+C : cancel edit
        // open(editURL);
        // testShortcutFromResultingTitle(editURL, "c", "XWiki - Main - WebHome", false, true, false);
        // This test and is commented (and there should be a bunch of others similar to this one)
        // since Alt+key combination seems to be buggy in selenium
        // http://jira.openqa.org/browse/SEL-437
        // The feature has been manualy tested with Firefox 2, IE6 and IE7
    }
}
