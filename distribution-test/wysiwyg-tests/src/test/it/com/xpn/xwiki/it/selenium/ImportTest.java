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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;

/**
 * Test case for wysiwyg content import plugin.
 * 
 * @version $Id$
 * @since 2.0.1
 */
public class ImportTest extends AbstractWysiwygTestCase
{
    public static final String MENU_IMPORT = "Import";

    public static final String MENU_IMPORT_OFFICE_CONTENT = "Office Content (Copy / Paste)";

    public static final String IMPORT_BUTTON = "Import";

    /**
     * Test importing office content with copy / paste import wizard step.
     */
    public void testOfficePasteImport()
    {
        // Do a normal copy / paste import operation.
        resetContent();
        openImportDialog(MENU_IMPORT_OFFICE_CONTENT);
        populateOfficeContentEditor("<p>Hello <font color=\"#ff0000\">World</font></p>");
        clickButtonWithText(IMPORT_BUTTON);
        waitForDialogToClose();
        assertEquals("<p>Hello <span style=\"color: rgb(255, 0, 0);\">World</span></p><br>", getContent().trim());

        // TODO: need to add a test for copy / paste import operation with style filtering on.
        // This seems difficult without adding a custom 'id' field for the 'filter styles' gwt checkbox so that it can
        // be selected from selenium.
    }

    private void openImportDialog(String menuItemName)
    {
        clickMenu(MENU_IMPORT);
        assertTrue(isMenuEnabled(menuItemName));
        clickMenu(menuItemName);
        waitForDialogToLoad();
    }

    /**
     * Utility method for injecting html content into office import wizard's copy paste area.
     * 
     * @param innerHTML html content.
     */
    private void populateOfficeContentEditor(String innerHTML)
    {
        StringBuffer script = new StringBuffer();
        String locator = "//iframe[contains(@class, 'xImportOfficeContentEditor')]";
        script.append(String.format("var eframe = this.browserbot.findElement(\"%s\")", locator)).append("\n");
        script.append("var rte = eframe.contentDocument.body;").append("\n");
        script.append(String.format("rte.innerHTML = '%s';", innerHTML));
        getSelenium().getEval(script.toString());
    }
}
