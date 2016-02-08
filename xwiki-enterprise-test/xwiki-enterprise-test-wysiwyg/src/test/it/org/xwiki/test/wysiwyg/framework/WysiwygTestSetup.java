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
package org.xwiki.test.wysiwyg.framework;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.selenium.Wait;

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

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        // Get the first WYSIWYG test to do the setup with its provided API.
        AbstractWysiwygTestCase firstXWikiTest = getFirstWysiwygTest(getTest());

        if (firstXWikiTest == null) {
            return;
        }

        // Make sure the hidden documents are displayed to the Admin user.
        displayHiddenDocumentsForAdmin(firstXWikiTest);

        // Set up the WYSIWYG tests using the first WYSIWYG Test case for Selenium and skin executor API.
        enableAllEditingFeatures(firstXWikiTest);

        // Wait for Solr to finish indexing.
        waitForSolrIndexing(firstXWikiTest);
    }

    private void waitForSolrIndexing(final AbstractWysiwygTestCase firstXWikiTest)
    {
        firstXWikiTest.open("XWiki", "XWikiPreferences", "admin", "section=Search");

        final String solrQueueSizeLocator = "//.[contains(@class, 'solrQueueSize')]";
        final String expectedValue = "0";

        // Wait for 10 minutes max.
        // (timed it at 5:50 minutes on a local machine, jetty+hsqldb instance, i7 + SSD, 08.feb.2016)
        new Wait()
        {
            @Override
            public boolean until()
            {
                String currentValue = firstXWikiTest.getSelenium().getText(solrQueueSizeLocator);
                return currentValue.equals(expectedValue);
            }
        }.wait(firstXWikiTest.getSelenium().isElementPresent(solrQueueSizeLocator) ? "Element [" + solrQueueSizeLocator
            + "] not found" : "Element [" + solrQueueSizeLocator + "] found but it doesn't have the expected value ["
            + expectedValue + "]. Actual value was [" + firstXWikiTest.getSelenium().getText(solrQueueSizeLocator)
            + "]", 600000);
    }

    /**
     * @return the first {@link AbstractWysiwygTestCase} (in a suite) wrapped by this decorator
     */
    private AbstractWysiwygTestCase getFirstWysiwygTest(Test test)
    {
        if (test instanceof TestSuite) {
            if (((TestSuite) test).tests().hasMoreElements()) {
                // Normally this should be the first one.
                Test currentTest = ((TestSuite) test).tests().nextElement();
                if (currentTest instanceof AbstractWysiwygTestCase) {
                    return (AbstractWysiwygTestCase) currentTest;
                }
            }
        } else if (test instanceof TestSetup) {
            return getFirstWysiwygTest(((TestSetup) test).getTest());
        } else if (test instanceof AbstractWysiwygTestCase) {
            return (AbstractWysiwygTestCase) test;
        }

        return null;
    }

    /**
     * Sets the Admin user preference to display the hidden documents.
     * 
     * @param helperTest helper {@link AbstractWysiwygTestCase} instance whose API to use to do the setup
     */
    private void displayHiddenDocumentsForAdmin(AbstractWysiwygTestCase helperTest)
    {
        helperTest.loginAsAdmin();
        helperTest.open("XWiki", "Admin", "edit", "editor=object");
        String propertyId = "XWiki.XWikiUsers_0_displayHiddenDocuments";
        helperTest.expandObject("XWiki.XWikiUsers", 0);
        if (!"1".equals(helperTest.getSelenium().getSelectedValue(propertyId))) {
            helperTest.getSelenium().select(propertyId, "value=1");
            helperTest.clickEditSaveAndContinue();
        }
    }

    /**
     * Enables all editing features so they are accessible for testing.
     * 
     * @param helperTest helper {@link AbstractWysiwygTestCase} instance whose API to use to do the setup
     */
    private void enableAllEditingFeatures(AbstractWysiwygTestCase helperTest)
    {
        Map<String, String> config = new HashMap<String, String>();
        config.put("plugins", "submit readonly line separator embed text valign list "
            + "indent history format symbol link image " + "table macro import color justify font");
        config.put("toolBar", "bold italic underline strikethrough teletype | subscript superscript | "
            + "justifyleft justifycenter justifyright justifyfull | unorderedlist orderedlist | outdent indent | "
            + "undo redo | format | fontname fontsize forecolor backcolor | hr removeformat symbol | "
            + " paste | macro:velocity");
        updateConfiguration(config, helperTest);
    }

    /**
     * Updates the WYSIWYG editor configuration based on the given configuration object. The key in the configuration is
     * the name of a {@code XWiki.WysiwygEditorConfigClass} property and the value is the new value for that property.
     * 
     * @param config configuration object
     * @param helperTest helper {@link AbstractWysiwygTestCase} instance whose API to use to do the setup
     */
    private void updateConfiguration(Map<String, String> config, AbstractWysiwygTestCase helperTest)
    {
        helperTest.open("XWiki", "WysiwygEditorConfig", "edit", "editor=object");
        helperTest.expandObject("XWiki.WysiwygEditorConfigClass", 0);
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String propertyId = "XWiki.WysiwygEditorConfigClass_0_" + entry.getKey();
            if (!entry.getValue().equals(helperTest.getFieldValue(propertyId))) {
                helperTest.setFieldValue(propertyId, entry.getValue());
                helperTest.clickEditSaveAndContinue();
            }
        }
    }
}
