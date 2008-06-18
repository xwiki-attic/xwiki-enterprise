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
 * Verify the data validation feature of XWiki.
 * 
 * @version $Id$
 */
public class ValidationTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the data validation feature of XWiki");
        suite.addTestSuite(ValidationTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testSaveActionValidatesWhenXValidateIsPresent()
    {
        open("/xwiki/bin/edit/Main/ValidatedClass?editor=class");
        setFieldValue("propname", "prop");
        setFieldValue("proptype", "com.xpn.xwiki.objects.classes.StringClass");
        submit("//input[@value='Add Property']");
        setFieldValue("prop_validationRegExp", "/^[0-4][0-2]$/");
        setFieldValue("prop_validationMessage", "invalid value for prop");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/ValidatedObject?editor=wiki");
        typeInWiki("value: $doc.display('prop')\n\n#foreach($e in $context.validationStatus.errors)$e #end");
        clickEditSaveAndView();
        open("/xwiki/bin/edit/Main/ValidatedObject?editor=object");
        setFieldValue("classname", "Main.ValidatedClass");
        submit("//input[@value='Add Object from this Class']");
        setFieldValue("Main.ValidatedClass_0_prop", "22");
        clickEditSaveAndView();
        open("/xwiki/bin/save/Main/ValidatedObject?xvalidate=1");
        assertTextPresent("value: 22");
        open("/xwiki/bin/edit/Main/ValidatedObject?editor=object");
        setFieldValue("Main.ValidatedClass_0_prop", "44");
        clickEditSaveAndView();
        open("/xwiki/bin/save/Main/ValidatedObject?xvalidate=1");
        assertTextNotPresent("value: 44");
        assertTextPresent("invalid value for prop");
        open("/xwiki/bin/save/Main/ValidatedObject?xvalidate=1&Main.ValidatedClass_0_prop=11");
        assertTextPresent("value: 11");
    }
}
