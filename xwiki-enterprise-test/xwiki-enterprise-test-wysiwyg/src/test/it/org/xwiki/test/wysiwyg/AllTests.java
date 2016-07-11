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
package org.xwiki.test.wysiwyg;

import org.xwiki.test.integration.XWikiTestSetup;
import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiSeleniumTestSetup;
import org.xwiki.test.wysiwyg.framework.WysiwygTestSetup;
import org.xwiki.test.wysiwyg.framework.WysiwygTestSuite;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * A class listing all the Selenium Functional tests to execute. We need such a class (rather than letting the JUnit
 * Runner discover the different TestCases classes by itself) because we want to start/stop XWiki before and after the
 * tests start (but only once).
 * 
 * @version $Id$
 */
public class AllTests extends TestCase
{
    private static final String PATTERN = ".*" + System.getProperty("pattern", "");

    public static Test suite() throws Exception
    {
        // create a wysiwyg test suite for all the wysiwyg tests to be executed with the Colibri skin executor
        // FIXME: the skin executor setting should be in a ColibriTestSetup, so that the provider for skin functions is
        // injected at setup time, and can be changed by changing the decorator
        WysiwygTestSuite suite = new WysiwygTestSuite("WYSIWYG Selenium Tests", FlamingoSkinExecutor.class);

        addTestCase(suite, SubmitTest.class);
        addTestCase(suite, StandardFeaturesTest.class);
        addTestCase(suite, HistoryTest.class);
        addTestCase(suite, LineTest.class);
        addTestCase(suite, TableTest.class);
        addTestCase(suite, LinkTest.class);
        addTestCase(suite, ListTest.class);
        addTestCase(suite, MacroTest.class);
        addTestCase(suite, ImageTest.class);
        addTestCase(suite, TabsTest.class);
        addTestCase(suite, NativeJavaScriptApiTest.class);
        addTestCase(suite, ColorTest.class);
        addTestCase(suite, AlignmentTest.class);
        addTestCase(suite, RemoveFormattingTest.class);
        addTestCase(suite, FontTest.class);
        addTestCase(suite, CacheTest.class);
        addTestCase(suite, RegularUserTest.class);
        addTestCase(suite, ImportTest.class);
        addTestCase(suite, EmbedTest.class);
        addTestCase(suite, EditInlineTest.class);

        return new XWikiTestSetup(new XWikiSeleniumTestSetup(new WysiwygTestSetup(suite)));
    }

    private static void addTestCase(WysiwygTestSuite suite, Class< ? extends AbstractXWikiTestCase> testClass)
        throws Exception
    {
        if (testClass.getName().matches(PATTERN)) {
            suite.addTestSuite(testClass);
        }
    }
}
