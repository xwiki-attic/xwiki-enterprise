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
 * @version $Id: $
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
        open("/xwiki/bin/edit/Main/Class?editor=class");
        setFieldValue("propname", "prop");
        setFieldValue("proptype", "com.xpn.xwiki.objects.classes.DBListClass");
        submit("//input[@value='Add Property']");
        setFieldValue("prop_sql", "select doc.fullName from XWikiDocument doc");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/Object?editor=wiki");
        typeInWiki("this is the content");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/Object?editor=object");
        setFieldValue("classname", "Main.Class");
        submit("//input[@value='Add Object from this Class']");
        setFieldValue("Main.Class_0_prop", "Main.Class");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/Class?editor=class");
        setFieldValue("prop_multiSelect", "1");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/Object?editor=object");
        setFieldValue("Main.Class_0_prop", "Main.Object");
        clickEditSaveAndView();
        assertTextPresent("this is the content");
    }
}
