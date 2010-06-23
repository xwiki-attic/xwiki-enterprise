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
package com.xpn.xwiki.it.selenium.framework;

import java.util.HashMap;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Prepares the WYSIWYG test suite by setting up general WYSIWYG preferences (such as enabling all editing features in
 * the editor or logging in as admin).
 * 
 * @version $Id$
 */
public class WysiwygTestSetup extends TestSetup
{
    /**
     * Create a new setup, for the passed tests.
     * 
     * @param tests the tests to decorate with this setup
     */
    public WysiwygTestSetup(Test test)
    {
        super(test);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        // get the first xwiki test to do the setup with its provided API
        AbstractXWikiTestCase firstXWikiTest = getFirstXWikiTest(getTest());

        if (firstXWikiTest == null) {
            return;
        }

        // set up the WYSIWYG tests using the first XWiki Test case for selenium and skin executor API
        enableAllEditingFeatures(firstXWikiTest);
    }

    /**
     * @return the first {@link AbstractXWikiTestCase} (in a suite) wrapped by this decorator
     */
    private AbstractXWikiTestCase getFirstXWikiTest(Test test)
    {
        if (test instanceof TestSuite) {
            if (((TestSuite) test).tests().hasMoreElements()) {
                // normally this should be the first one
                Test currentTest = ((TestSuite) test).tests().nextElement();
                if (currentTest instanceof AbstractXWikiTestCase) {
                    return (AbstractXWikiTestCase) currentTest;
                }
            }
        } else if (test instanceof TestSetup) {
            return getFirstXWikiTest(((TestSetup) test).getTest());
        } else if (test instanceof AbstractXWikiTestCase) {
            return (AbstractXWikiTestCase) test;
        }

        return null;
    }

    /**
     * Enables all editing features so they are accessible for testing.
     * 
     * @param helperTest helper {@link AbstractXWikiTestCase} instance whose API to use to do the setup
     */
    private void enableAllEditingFeatures(AbstractXWikiTestCase helperTest)
    {
        // login as admin and enable editing features.
        helperTest.loginAsAdmin();
        Map<String, String> config = new HashMap<String, String>();
        config.put("wysiwyg.plugins", "submit line separator embed text valign list "
            + "indent history format symbol link image " + "table macro import color justify font");
        config.put("wysiwyg.toolbar", "bold italic underline strikethrough teletype | subscript superscript | "
            + "justifyleft justifycenter justifyright justifyfull | unorderedlist orderedlist | outdent indent | "
            + "undo redo | format | fontname fontsize forecolor backcolor | hr removeformat symbol | "
            + " paste | macro:velocity");
        updateXWikiPreferences(config, helperTest);
    }

    /**
     * Updates XWiki preferences based on the given configuration object. The key in the configuration is the name of a
     * XWiki preference and the value is the new value for that preference.
     * 
     * @param config configuration object
     * @param helperTest helper {@link AbstractXWikiTestCase} instance whose API to use to do the setup
     */
    private void updateXWikiPreferences(Map<String, String> config, AbstractXWikiTestCase helperTest)
    {
        helperTest.open("XWiki", "XWikiPreferences", "edit", "editor=object");
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String propertyId = "XWiki.XWikiPreferences_0_" + entry.getKey();
            if (!helperTest.isElementPresent(propertyId)) {
                addXWikiStringPreference(entry.getKey(), helperTest);
            }
            if (!entry.getValue().equals(helperTest.getFieldValue(propertyId))) {
                helperTest.setFieldValue(propertyId, entry.getValue());
                helperTest.clickEditSaveAndContinue();
            }
        }
    }

    /**
     * Adds a string property to the XWiki.XWikiPreferences class.
     * 
     * @param name the property name
     * @param helperTest helper {@link AbstractXWikiTestCase} instance whose API to use to do the setup
     */
    private void addXWikiStringPreference(String name, AbstractXWikiTestCase helperTest)
    {
        String location = helperTest.getSelenium().getLocation();
        helperTest.open("XWiki", "XWikiPreferences", "edit", "editor=class");
        helperTest.setFieldValue("propname", name);
        helperTest.getSelenium().select("proptype", "String");
        helperTest.getSelenium().click("//input[@value = 'Add']");
        helperTest.waitForCondition("(window.document.getElementsByClassName('xnotification-done')[0] != null "
            + "&& window.document.getElementsByClassName('xnotification-done')[0].innerHTML == 'Property added')");
        helperTest.getSelenium().open(location);
    }
}
