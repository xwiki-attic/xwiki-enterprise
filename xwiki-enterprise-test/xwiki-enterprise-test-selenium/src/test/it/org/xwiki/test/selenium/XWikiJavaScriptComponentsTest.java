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
package org.xwiki.test.selenium;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Verify the JavaScript components of XWiki.
 * 
 * @version $Id$
 */
public class XWikiJavaScriptComponentsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the JavaScript components of XWiki");
        suite.addTestSuite(XWikiJavaScriptComponentsTest.class, FlamingoSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    private void testResourceFromResourceName(String name, String expected)
    {
        if (getSelenium().getEval("typeof window.XWiki.testResourceFromResourceName") != "function") {
            StringBuffer script = new StringBuffer();
            script.append("window.XWiki.testResourceFromResourceName = function(name, expected) {\n");
            script.append("var res = XWiki.resource.get(name);\n");
            script.append("var expectedObj = eval(expected);\n");
            script.append("var equals = true;\n");
            script.append("for (member in expectedObj) {\n");
            script.append("if (expectedObj[member] != res[member]) {\n");
            script.append("return false;\n");
            script.append("}\n");
            script.append("}\n");
            script.append("return true;\n");
            script.append("};");
            getSelenium().runScript(script.toString());
        }
        String ret = 
            getSelenium().getEval("window.XWiki.testResourceFromResourceName(\"" + name + "\", \"(" + expected 
                + ")\");");
        assertTrue(Boolean.TRUE.toString().equals(ret));
    }

    public void testXWikiResourceGetter()
    {
        // ""
        testResourceFromResourceName("", "{ anchor: '', attachment: '', fullName: 'Main.WebHome', name: 'WebHome', "
            + "prefixedFullName: 'xwiki:Main.WebHome', prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "xwiki:Main"
        testResourceFromResourceName("xwiki:Main", "{ anchor: '', attachment: '', fullName: 'Main.WebHome', " +
        		"name: 'WebHome', prefixedFullName: 'xwiki:Main.WebHome', prefixedSpace: 'xwiki:Main', " +
        		"space: 'Main', wiki: 'xwiki' }");

        // "xwiki:Space"
        testResourceFromResourceName("xwiki:Space", "{ anchor: '', attachment: '', fullName: 'Space.WebHome', " +
        		"name: 'WebHome', prefixedFullName: 'xwiki:Space.WebHome', prefixedSpace: 'xwiki:Space', " +
        		"space: 'Space', wiki: 'xwiki' }");

        // "Space.Test"
        testResourceFromResourceName("Space.Test", "{ anchor: '', attachment: '', fullName: 'Space.Test', " +
        		"name: 'Test', prefixedFullName: 'xwiki:Space.Test', prefixedSpace: 'xwiki:Space', space: 'Space', " +
        		"wiki: 'xwiki' }");

        // "Test"
        testResourceFromResourceName("Test", "{ anchor: '', attachment: '', fullName: 'Main.Test', name: 'Test', "
            + "prefixedFullName: 'xwiki:Main.Test', prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "Main.Test"
        testResourceFromResourceName("Main.Test", "{ anchor: '', attachment: '', fullName: 'Main.Test', name: 'Test', "
            + "prefixedFullName: 'xwiki:Main.Test', prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "xwiki:Main.Test"
        testResourceFromResourceName("xwiki:Main.Test", "{ anchor: '', attachment: '', fullName: 'Main.Test', " +
        		"name: 'Test', prefixedFullName: 'xwiki:Main.Test', prefixedSpace: 'xwiki:Main', space: 'Main', " +
        		"wiki: 'xwiki' }");

        // "Test#Comments"
        testResourceFromResourceName("Test#Comments", "{ anchor: 'Comments', attachment: '', fullName: 'Main.Test', " +
        		"name: 'Test', prefixedFullName: 'xwiki:Main.Test', prefixedSpace: 'xwiki:Main', space: 'Main', " +
        		"wiki: 'xwiki' }");

        // "Main.Test#Comments"
        testResourceFromResourceName("Main.Test#Comments", "{ anchor: 'Comments', attachment: '', " +
        		"fullName: 'Main.Test', name: 'Test', prefixedFullName: 'xwiki:Main.Test', " +
        		"prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "xwiki:Main.Test#Comments"
        testResourceFromResourceName("xwiki:Main.Test#Comments", "{ anchor: 'Comments', attachment: '', " +
        		"fullName: 'Main.Test', name: 'Test', prefixedFullName: 'xwiki:Main.Test', " +
        		"prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "Test@test.gif"
        testResourceFromResourceName("Test@test.gif", "{ anchor: '', attachment: 'test.gif', fullName: 'Main.Test', " +
        		"name: 'Test', prefixedFullName: 'xwiki:Main.Test', prefixedSpace: 'xwiki:Main', space: 'Main', " +
        		"wiki: 'xwiki' }");

        // "Main.Test@test.gif"
        testResourceFromResourceName("Main.Test@test.gif", "{ anchor: '', attachment: 'test.gif', " +
        		"fullName: 'Main.Test', name: 'Test', prefixedFullName: 'xwiki:Main.Test', " +
        		"prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");

        // "xwiki:Main.Test@test.gif"
        testResourceFromResourceName("xwiki:Main.Test@test.gif", "{ anchor: '', attachment: 'test.gif', " +
        		"fullName: 'Main.Test', name: 'Test', prefixedFullName: 'xwiki:Main.Test', " +
        		"prefixedSpace: 'xwiki:Main', space: 'Main', wiki: 'xwiki' }");
    }

}
