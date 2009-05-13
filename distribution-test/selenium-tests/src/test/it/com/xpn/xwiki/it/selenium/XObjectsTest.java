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
 * Verify the structured data features of XWiki.
 * 
 * @version $Id$
 */
public class XObjectsTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the structured data features of XWiki");
        suite.addTestSuite(XObjectsTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    /**
     * Tests that XWIKI-1621 remains fixed.
     */
    public void testChangeMultiselectProperty()
    {
        open("Main", "Class", "edit", "editor=class");
        setFieldValue("propname", "prop");
        setFieldValue("proptype", "com.xpn.xwiki.objects.classes.DBListClass");
        submit("//input[@value='Add Property']");
        setFieldValue("prop_sql", "select doc.fullName from XWikiDocument doc");
        clickEditSaveAndView();
        createPage("Main", "Object", "this is the content");
        open("Main", "Object", "edit", "editor=object");
        setFieldValue("classname", "Main.Class");
        submit("//input[@value='Add Object from this Class']");
        setFieldValue("Main.Class_0_prop", "Main.Class");
        clickEditSaveAndView();
        open("Main", "Class", "edit", "editor=class");
        setFieldValue("prop_multiSelect", "1");
        clickEditSaveAndView();
        open("Main", "Object", "edit", "editor=object");
        setFieldValue("Main.Class_0_prop", "Main.Object");
        clickEditSaveAndView();
        assertTextPresent("this is the content");
    }

    /**
     * Tests that XWIKI-2214 remains fixed.
     */
    public void testChangeNumberType()
    {
        createPage("Main", "Class2", "this is the content");
        open("Main", "Class2", "edit", "editor=class");
        setFieldValue("propname", "prop");
        setFieldValue("proptype", "com.xpn.xwiki.objects.classes.NumberClass");
        submit("//input[@value='Add Property']");
        setFieldValue("prop_numberType", "integer");
        clickEditSaveAndView();
        assertTextPresent("this is the content");
        createPage("Main", "Object2", "this is the content: $doc.display('prop')");
        open("Main", "Object2", "edit", "editor=object");
        setFieldValue("classname", "Main.Class2");
        submit("//input[@value='Add Object from this Class']");
        setFieldValue("Main.Class2_0_prop", "3");
        clickEditSaveAndView();
        assertTextPresent("this is the content: 3");
        open("Main", "Class2", "edit", "editor=class");
        setFieldValue("prop_numberType", "double");
        clickEditSaveAndView();
        assertTextPresent("this is the content");
        open("Main", "Object2", "edit", "editor=object");
        setFieldValue("Main.Class2_0_prop", "2.5");
        clickEditSaveAndView();
        assertTextPresent("this is the content: 2.5");
        open("Main", "Class2", "edit", "editor=class");
        setFieldValue("prop_numberType", "long");
        clickEditSaveAndView();
        assertTextPresent("this is the content");
        open("Main", "Object2");
        // Check that the value was truncated to an int.
        assertTextPresent("this is the content: 2");
    }
}
