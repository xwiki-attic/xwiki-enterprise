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
package org.xwiki.test.ui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.FormElement;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.ClassEditPage;
import org.xwiki.test.ui.framework.elements.editor.ObjectEditPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

/**
 * Test XClass editing.
 * 
 * @version $Id$
 * @since 2.4RC1
 */
public class EditClassTest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage("Test", "EditObjectsTestClass");
        getUtil().deletePage("Test", "EditObjectsTestObject");
    }

    @Test
    public void testAddProperty()
    {
        // Create a class with a string property
        ClassEditPage cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.StringClass");
        cep.clickSaveAndView();

        // Create object page
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content: {{velocity}}$doc.display('prop'){{/velocity}}");
        ViewPage vp = wep.clickSaveAndView();

        // Add an object of the class created
        ObjectEditPage oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "testing value");
        vp = oep.clickSaveAndView();

        Assert.assertEquals("this is the content: testing value", vp.getContent());
    }

    @Test
    public void testDeleteProperty()
    {
        // Create a class with two string properties
        ClassEditPage cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.addProperty("prop1", "com.xpn.xwiki.objects.classes.StringClass");
        cep.addProperty("prop2", "com.xpn.xwiki.objects.classes.StringClass");
        cep.clickSaveAndView();

        // Create object page
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content: {{velocity}}$doc.display('prop1')/$doc.display('prop2')/" +
            "$!doc.getObject('Test.EditObjectsTestClass').getProperty('prop1').value{{/velocity}}");
        ViewPage vp = wep.clickSaveAndView();

        // Add an object of the class created
        ObjectEditPage oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop1"), "testing value 1");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop2"), "testing value 2");
        vp = oep.clickSaveAndView();

        Assert.assertEquals("this is the content: testing value 1/testing value 2/testing value 1", vp.getContent());

        // Delete the first property from the class
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.deleteProperty("prop1");
        cep.clickSaveAndView();
        vp = getUtil().gotoPage("Test", "EditObjectsTestObject");
        Assert.assertEquals("this is the content: /testing value 2/testing value 1", vp.getContent());
        oep.switchToEdit("Test", "EditObjectsTestObject");
        Assert.assertNotNull(getDriver().findElement(By.className("deprecatedProperties")));
        Assert.assertNotNull(getDriver().findElement(By.cssSelector(".deprecatedProperties label")));
        Assert.assertEquals("prop1:", getDriver().findElement(By.cssSelector(".deprecatedProperties label")).getText());

        // Remove deprecated properties
        oep.removeAllDeprecatedProperties();
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: /testing value 2/", vp.getContent());
    }

    @Test
    public void addInvalidProperty()
    {
        ClassEditPage cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        Assert.assertFalse(cep.addProperty("a<b c", "com.xpn.xwiki.objects.classes.StringClass"));
        Assert.assertFalse(getDriver().getPageSource().contains("xwikimessage"));
    }
}
