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
import org.xwiki.test.ui.framework.elements.editor.StaticListClassEditElement;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;

/**
 * Test XObject editing.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class EditObjectsTest extends AbstractAdminAuthenticatedTest
{
    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage("Test", "EditObjectsTestClass");
        getUtil().deletePage("Test", "EditObjectsTestObject");
    }

    /**
     * XWIKI-5832: Cannot create groups or add members to existing groups using the object editor.
     */
    @Test
    public void testEmptyGroupObjects()
    {
        // Create a doc
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add an XWikiGroups object
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("XWiki.XWikiGroups");
        vp = oep.clickSaveAndView();

        oep = vp.editObjects();
        Assert.assertEquals(1, oep.getObjectsOfClass("XWiki.XWikiGroups").size());
    }

    /**
     * Tests that XWIKI-1621 remains fixed.
     */
    @Test
    public void testChangeMultiselectProperty()
    {
        // Create a class with a database list property set to return all documents
        ClassEditPage cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.DBListClass");
        cep.getDatabaseListClassEditElement("prop").setHibernateQuery(
            "select doc.fullName from XWikiDocument doc where doc.space = 'Test'");
        cep.clickSaveAndView();

        // Create a second page to hold the Object and set its content
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add an object of the class created and set the value to be the test page
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "Test.EditObjectsTestClass");
        oep.clickSaveAndView();

        // Set multiselect to true
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getDatabaseListClassEditElement("prop").setMultiSelect(true);
        cep.clickSaveAndView();

        // Select a second document in the DB list select field.
        oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "Test.EditObjectsTestObject");
        vp = oep.clickSaveAndView();

        Assert.assertEquals("this is the content", vp.getContent());
    }

    /**
     * Tests that XWIKI-2214 remains fixed.
     */
    @Test
    public void testChangeNumberType()
    {
        // Create class page
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestClass");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add class
        ClassEditPage cep = vp.editClass();
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.NumberClass");
        cep.getNumberClassEditElement("prop").setNumberType("integer");
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Create object page
        wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content: {{velocity}}$doc.display('prop'){{/velocity}}");
        vp = wep.clickSaveAndView();

        // Add object
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "3");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: 3", vp.getContent());

        // Change number to double type
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getNumberClassEditElement("prop").setNumberType("double");
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Verify conversion
        oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "2.5");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: 2.5", vp.getContent());

        // Change number to long type
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getNumberClassEditElement("prop").setNumberType("long");
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Verify conversion
        oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: 2", vp.getContent());
    }

    /**
     * Prove that a list can be changed from relational storage to plain storage and back without database corruption.
     * XWIKI-299 test
     */
    @Test
    public void testChangeListMultipleSelect()
    {
        // Create class page
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestClass");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add class
        ClassEditPage cep = vp.editClass();
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.StaticListClass");
        StaticListClassEditElement slcee = cep.getStaticListClassEditElement("prop");
        slcee.setMultiSelect(false);
        slcee.setValues("choice 1|choice 2|choice 3|choice 4|choice 5");
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Create object page
        wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content: {{velocity}}$doc.display('prop'){{/velocity}}");
        vp = wep.clickSaveAndView();

        // Add object
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "choice 3");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: choice 3", vp.getContent());

        // Change list to a multiple select.
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getStaticListClassEditElement("prop").setMultiSelect(true);
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Verify conversion
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "choice 3");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "choice 4");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: choice 3 choice 4", vp.getContent());
    }

    /**
     * Prove that a list can be changed from relational storage to plain storage and back without database corruption.
     * XWIKI-1621: Changing parameters of list properties fails
     */
    @Test
    public void testChangeListTypeRelationalStorage()
    {
        // Create class page
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestClass");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add class
        ClassEditPage cep = vp.editClass();
        cep.addProperty("prop", "com.xpn.xwiki.objects.classes.StaticListClass");
        StaticListClassEditElement slcee = cep.getStaticListClassEditElement("prop");
        slcee.setMultiSelect(true);
        slcee.setDisplayType(StaticListClassEditElement.DisplayType.INPUT);
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Create object page
        wep = new WikiEditPage();
        wep.switchToEdit("Test", "EditObjectsTestObject");
        wep.setContent("this is the content: {{velocity}}$doc.display('prop'){{/velocity}}");
        vp = wep.clickSaveAndView();

        // Add object
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("Test.EditObjectsTestClass");
        objectForm.setFieldValue(By.id("Test.EditObjectsTestClass_0_prop"), "this|that|other");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: this that other", vp.getContent());

        // Change list to relational storage.
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getStaticListClassEditElement("prop").setRelationalStorage(true);
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Make sure we can still edit the object.
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "this|other");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: this other", vp.getContent());

        // Change list to non-relational storage.
        cep = new ClassEditPage();
        cep.switchToEdit("Test", "EditObjectsTestClass");
        cep.getStaticListClassEditElement("prop").setRelationalStorage(false);
        vp = cep.clickSaveAndView();
        Assert.assertEquals("this is the content", vp.getContent());

        // Make sure we can still edit the object.
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.getObjectsOfClass("Test.EditObjectsTestClass").get(0).setFieldValue(
            By.id("Test.EditObjectsTestClass_0_prop"), "that|other");
        vp = oep.clickSaveAndView();
        Assert.assertEquals("this is the content: that other", vp.getContent());
    }

    @Test
    public void testObjectAddAndRemove()
    {
        ObjectEditPage oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        FormElement object = oep.addObject("XWiki.XWikiUsers");
        object.setFieldValue(By.id("XWiki.XWikiUsers_0_first_name"), "John");

        // Add another object
        FormElement object2 = oep.addObject("XWiki.XWikiUsers");

        // Check that the unsaved value from the first object wasn't lost
        Assert.assertEquals("John", object.getFieldValue(By.id("XWiki.XWikiUsers_0_first_name")));
        // Check that the value from the second object is unset
        Assert.assertEquals("", object2.getFieldValue(By.id("XWiki.XWikiUsers_1_first_name")));

        // Delete the second object
        oep.deleteObject("XWiki.XWikiUsers", 1);

        // Let's save the form and check that changes were persisted.
        oep = oep.clickSaveAndView().editObjects();
        Assert.assertEquals(1, oep.getObjectsOfClass("XWiki.XWikiUsers").size());
        object = oep.getObjectsOfClass("XWiki.XWikiUsers").get(0);
        Assert.assertEquals("John", object.getFieldValue(By.id("XWiki.XWikiUsers_0_first_name")));
    }

    @Test
    public void testInlineObjectAddButton()
    {
        ObjectEditPage oep = new ObjectEditPage();
        oep.switchToEdit("Test", "EditObjectsTestObject");
        oep.addObject("XWiki.XWikiUsers");
        oep.addObjectFromInlineLink("XWiki.XWikiUsers");
    }
}
