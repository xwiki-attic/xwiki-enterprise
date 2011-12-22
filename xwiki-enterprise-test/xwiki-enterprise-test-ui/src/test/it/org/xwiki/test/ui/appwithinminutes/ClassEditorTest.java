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
package org.xwiki.test.ui.appwithinminutes;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.appwithinminutes.ApplicationClassEditPage;
import org.xwiki.test.po.appwithinminutes.ClassFieldEditPane;
import org.xwiki.test.po.appwithinminutes.EntryEditPage;
import org.xwiki.test.po.appwithinminutes.LongTextClassFieldEditPane;
import org.xwiki.test.po.platform.ViewPage;
import org.xwiki.test.po.platform.editor.ObjectEditPage;
import org.xwiki.test.po.xe.ClassSheetPage;

/**
 * Tests the application class editor.
 * 
 * @version $Id$
 * @since 3.4M1
 */
public class ClassEditorTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The message displayed when the canvas is empty.
     */
    private static final String EMPTY_CANVAS_HINT = "Drag fields from the palette and drop them in this area.";

    /**
     * The page being tested.
     */
    private ApplicationClassEditPage editor;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        getUtil().deletePage(getTestClassName(), getTestMethodName());
        getUtil().deletePage(getTestClassName(), getTestMethodName() + "Sheet");
        getUtil().deletePage(getTestClassName(), getTestMethodName() + "Template");
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit",
            "editor=inline&template=AppWithinMinutes.ClassTemplate&title=" + getTestMethodName() + " Class");
        editor = new ApplicationClassEditPage();
    }

    /**
     * Tests that the hint is displayed only when the canvas is empty.
     */
    @Test
    public void testEmptyCanvasHint()
    {
        Assert.assertTrue(editor.getContent().contains(EMPTY_CANVAS_HINT));
        ClassFieldEditPane field = editor.addField("Short Text");
        Assert.assertFalse(editor.getContent().contains(EMPTY_CANVAS_HINT));
        field.delete().clickYes();
        Assert.assertTrue(editor.getContent().contains(EMPTY_CANVAS_HINT));
    }

    /**
     * Tests that the field display is updated when the configuration panel is closed.
     */
    @Test
    public void testApplyConfigurationChanges()
    {
        LongTextClassFieldEditPane longTextField =
            new LongTextClassFieldEditPane(editor.addField("Long Text").getName());
        longTextField.openConfigPanel();
        longTextField.setRows(3);
        longTextField.setEditor("Text");
        longTextField.closeConfigPanel();
        Assert.assertEquals(3, longTextField.getPreviewRows());
    }

    /**
     * Tests that class fields can be deleted and that documents having objects of that class are updated.
     */
    @Test
    public void testDeleteField()
    {
        // Add two fields.
        editor.addField("Boolean").setPrettyName("Available");
        editor.addField("Date").setPrettyName("Day");

        // Save and assert they are present.
        ViewPage classView = editor.clickSaveAndView();
        Assert.assertTrue(classView.getContent().contains("Available (boolean1: Boolean)"));
        Assert.assertTrue(classView.getContent().contains("Day (date1: Date)"));

        // Edit again and delete one of the fields.
        classView.edit();
        new ClassFieldEditPane("boolean1").delete().clickYes();

        // Save and check if the field was removed.
        classView = new ApplicationClassEditPage().clickSaveAndView();
        Assert.assertFalse(classView.getContent().contains("Available (boolean1: Boolean)"));
        Assert.assertTrue(classView.getContent().contains("Day (date1: Date)"));

        // Edit the class template and see if the deleted field is now deprecated.
        ObjectEditPage objectEditor = new ClassSheetPage().clickTemplateLink().editObjects();
        String className = String.format("%s.%s", getTestClassName(), getTestMethodName());
        Assert.assertTrue(objectEditor.isPropertyDeprecated(className, "boolean1"));
        Assert.assertFalse(objectEditor.isPropertyDeprecated(className, "date1"));
    }

    /**
     * Tests that class fields can be reordered.
     */
    @Test
    public void testReorderFields()
    {
        // Add two class fields.
        editor.addField("Date").setPrettyName("Start Date");
        editor.addField("Date").setPrettyName("End Date");

        // Save and edit the class template in in-line edit mode.
        editor.clickSaveAndView();
        new ClassSheetPage().clickTemplateLink().edit();

        // Assert the order of the form fields.
        List<String> fieldNames = new EntryEditPage().getFieldNames();
        Assert.assertEquals("date1", fieldNames.get(0));
        Assert.assertEquals("date2", fieldNames.get(1));

        // Go back to the class editor.
        getDriver().navigate().back();
        getDriver().navigate().back();
        new ViewPage().edit();
        editor = new ApplicationClassEditPage();

        // Change the order of the class fields.
        editor.moveFieldBefore("date2", "date1");

        // Save and edit the class template again.
        editor.clickSaveAndView();
        new ClassSheetPage().clickTemplateLink().edit();

        // Assert the order of the form fields.
        fieldNames = new EntryEditPage().getFieldNames();
        Assert.assertEquals("date2", fieldNames.get(0));
        Assert.assertEquals("date1", fieldNames.get(1));
    }

    /**
     * Tests that class fields can be renamed.
     */
    @Test
    public void testRenameField()
    {
        // Add a class field.
        editor.addField("Number").setDefaultValue("13");

        // Save and edit the class template.
        editor.clickSaveAndView();
        new ClassSheetPage().clickTemplateLink().edit();

        // Change the field value.
        EntryEditPage inlineEditor = new EntryEditPage();
        Assert.assertEquals("13", inlineEditor.getValue("number1"));
        inlineEditor.setValue("number1", "27");

        // Save and edit again the class.
        inlineEditor.clickSaveAndView().clickBreadcrumbLink(getTestMethodName() + " Class").edit();

        // Rename the class field.
        ClassFieldEditPane field = new ClassFieldEditPane("number1");
        field.openConfigPanel();
        field.setName("age");

        // Save and edit again the class template.
        new ApplicationClassEditPage().clickSaveAndView();
        new ClassSheetPage().clickTemplateLink().edit();
        Assert.assertEquals("27", new EntryEditPage().getValue("age"));
    }

    /**
     * Tests that invalid field names are not allowed.
     */
    @Test
    public void testInvalidFieldName()
    {
        String invalidFieldNameErrorMessage = "Property names must follow these naming rules:";

        ClassFieldEditPane field = editor.addField("Static List");
        field.openConfigPanel();
        field.setName("3times");
        editor.clickSaveAndView();
        Assert.assertTrue(getDriver().getPageSource().contains(invalidFieldNameErrorMessage));

        getDriver().navigate().back();
        editor = new ApplicationClassEditPage();
        field = editor.addField("User");
        field.openConfigPanel();
        // Unfortunately we don't allow Unicode letters because they are not fully supported in tag names.
        // See XWIKI-7306: The class editor doesn't validate properly the field names
        field.setName("\u021Bar\u0103");
        editor.clickSaveAndView();
        Assert.assertTrue(getDriver().getPageSource().contains(invalidFieldNameErrorMessage));

        getDriver().navigate().back();
        editor = new ApplicationClassEditPage();
        field = editor.addField("Group");
        field.openConfigPanel();
        field.setName("alice>bob");
        editor.clickSaveAndView();
        Assert.assertTrue(getDriver().getPageSource().contains(invalidFieldNameErrorMessage));
    }

    /**
     * Tests that two class fields can't have the same name.
     */
    @Test
    public void testDuplicateFieldName()
    {
        ClassFieldEditPane field = editor.addField("Short Text");
        field.setPrettyName("Alice");
        field.openConfigPanel();
        field.setName("carol");

        field = editor.addField("Short Text");
        field.setPrettyName("Bob");
        field.openConfigPanel();
        field.setName("carol");

        editor.clickSaveAndView();
        Assert.assertTrue(getDriver().getPageSource().contains("The class has two fields with the same name: carol"));
    }

    /**
     * Tests that swapping field names is not allowed.
     */
    @Test
    public void testSwapFieldNames()
    {
        ClassFieldEditPane field = editor.addField("Short Text");
        field.openConfigPanel();
        field.setName("alice");

        field = editor.addField("Number");
        field.openConfigPanel();
        field.setName("bob");

        editor.clickSaveAndView().edit();
        editor = new ApplicationClassEditPage();

        field = new ClassFieldEditPane("alice");
        field.openConfigPanel();
        field.setName("bob");

        field = new ClassFieldEditPane("bob");
        field.openConfigPanel();
        field.setName("alice");

        editor.clickSaveAndView();
        Assert.assertTrue(getDriver().getPageSource().contains("The class has two fields with the same name: alice"));
    }

    /**
     * Tests the options to update the class sheet and the class template.
     */
    @Test
    public void testUpdateSheetAndTemplate()
    {
        // The options panel is not displayed if the class template and sheet don't exists.
        Assert.assertFalse(editor.getContent().contains("Update class template"));

        // Add a class field.
        editor.addField("Number");

        // Save and edit again.
        editor.clickSaveAndView().edit();
        editor = new ApplicationClassEditPage();

        // Set default value for the previously added field.
        new ClassFieldEditPane("number1").setDefaultValue("9");

        // Add a new field
        editor.addField("Database List");

        // The options panel should be displayed now.
        editor.setUpdateClassSheet(false);

        // Save and edit the template.
        editor.clickSaveAndView();
        new ClassSheetPage().clickTemplateLink().edit();

        // The sheet should display only the first field.
        EntryEditPage inlineEditor = new EntryEditPage();
        List<String> fieldNames = inlineEditor.getFieldNames();
        Assert.assertEquals(1, fieldNames.size());
        Assert.assertEquals("number1", fieldNames.get(0));

        // Assert the value of the first field. The class template should have been updated.
        Assert.assertEquals("9", inlineEditor.getValue("number1"));
    }

    /**
     * Tests the Save & Continue button.
     */
    @Test
    public void testSaveAndContinue()
    {
        editor.addField("Date");
        editor.clickSaveAndContinue();
        editor.waitForNotificationSuccessMessage("Saved");

        // Check if the field was added.
        ViewPage viewer = editor.clickCancel();
        Assert.assertTrue(viewer.getContent().contains("Date (date1: Date)"));

        // Edit again. This time check the error message.
        viewer.edit();
        editor = new ApplicationClassEditPage();

        // Try to set the field name to an invalid value.
        ClassFieldEditPane field = new ClassFieldEditPane("date1");
        field.openConfigPanel();
        field.setName("-delta");

        editor.clickSaveAndContinue();
        editor.waitForNotificationErrorMessage("Failed to save the document.");

        // Double check that the field wasn't renamed.
        Assert.assertTrue(editor.clickCancel().getContent().contains("Date (date1: Date)"));
    }

    /**
     * Tests that fields names are auto-generated properly.
     */
    @Test
    public void testFieldNameAutoGeneration()
    {
        // Add a class field and set its name to an auto-generated field name for a different type.
        ClassFieldEditPane field = editor.addField("Short Text");
        field.openConfigPanel();
        field.setName("number1");

        // Save and continue.
        editor.clickSaveAndContinue();
        editor.waitForNotificationSuccessMessage("Saved");

        // Add a new field of the type implied by the name set to the previous field.
        field = editor.addField("Number");
        field.openConfigPanel();
        Assert.assertEquals("number2", field.getName());

        // Save and assert both fields have been added.
        ViewPage viewer = editor.clickSaveAndView();
        Assert.assertTrue(viewer.getContent().contains("Short Text (number1: String)"));
        Assert.assertTrue(viewer.getContent().contains("Number (number2: Number)"));
    }

    /**
     * Test that Save And Continue supports field renames.
     */
    @Test
    public void testRenameWithSaveAndContinue()
    {
        // Add a class field.
        ClassFieldEditPane field = editor.addField("Short Text");

        // Save and continue.
        editor.clickSaveAndContinue();
        editor.waitForNotificationSuccessMessage("Saved");

        // Rename the field.
        field.openConfigPanel();
        field.setName("title");

        // Save and continue, again.
        editor.clickSaveAndContinue();
        editor.waitForNotificationSuccessMessage("Saved");

        // Rename the field for a second time.
        // NOTE: The IDs have been changed so we must recreate the class field edit pane.
        new ClassFieldEditPane("title").setName("city");

        // Save and assert the field was added with the right name.
        Assert.assertTrue(editor.clickSaveAndView().getContent().contains("Short Text (city: String)"));
    }
}
