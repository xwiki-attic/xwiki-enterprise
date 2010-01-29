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

import java.util.Date;

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;

/**
 * Functional tests for in-line editing using the WYSIWYG editor.
 * 
 * @version $Id$
 */
public class EditInlineTest extends AbstractWysiwygTestCase
{
    /**
     * Tests if a property whose name contains the underscore character can be edited properly.
     * 
     * @see XWIKI-4746: GWT-editor wont render field's content if it has underscores in it's name.
     */
    public void testEditPropertyWithUnderscore()
    {
        StringBuffer spaceName = new StringBuffer(this.getClass().getSimpleName());
        spaceName.insert(spaceName.length() / 2, "_0_");

        StringBuffer pageName = new StringBuffer(getName());
        pageName.insert(pageName.length() / 2, "_17_");

        // Create a class with a property that has '_' in its name.
        open(spaceName.toString(), pageName.toString(), "edit", "editor=class");
        String propertyName = "my_1_property";
        setFieldValue("propname", propertyName);
        getSelenium().select("proptype", "TextArea");
        getSelenium().click("//input[@value = 'Add Property']");
        waitPage();
        getSelenium().select(propertyName + "_editor", "Wysiwyg");
        clickEditSaveAndContinue();

        // Create an object of the previously created class.
        open(spaceName.toString(), pageName.toString(), "edit", "editor=object");
        getSelenium().select("classname", pageName.toString());
        getSelenium().click("//input[@value = 'Add Object from this Class']");
        waitPage();
        String propertyValue = String.valueOf(new Date().getTime());
        setFieldValue(spaceName + "." + pageName + "_0_" + propertyName, propertyValue);
        clickEditSaveAndContinue();

        // Display the object.
        open(spaceName.toString(), pageName.toString(), "edit", "editor=wiki");
        StringBuffer code = new StringBuffer();
        code.append("{{velocity}}\n");
        code.append("{{html wiki=true}}\n");
        code.append("$doc.use(\"" + pageName + "\")\n");
        code.append("$doc.display(\"" + propertyName + "\")\n");
        code.append("{{/html}}\n");
        code.append("{{velocity}}");
        setFieldValue("content", code.toString());
        clickEditSaveAndView();
        assertTextPresent(propertyValue);

        // Edit the object in-line.
        open(spaceName.toString(), pageName.toString(), "inline");
        waitForEditorToLoad();
        assertEquals(propertyValue, getEval("window.XWE.body.textContent"));

        // Change the property value.
        propertyValue = new StringBuffer(propertyValue).reverse().toString();
        setContent(propertyValue);
        clickEditSaveAndView();
        assertTextPresent(propertyValue);
    }
}
