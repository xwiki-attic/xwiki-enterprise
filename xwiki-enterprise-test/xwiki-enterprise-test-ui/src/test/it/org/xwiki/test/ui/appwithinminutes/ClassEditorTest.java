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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.appwithinminutes.ApplicationClassEditPage;
import org.xwiki.test.po.appwithinminutes.ClassFieldEditPane;
import org.xwiki.test.po.appwithinminutes.LongTextClassFieldEditPane;

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

        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit",
            "editor=inline&template=AppWithinMinutes.ClassTemplate");
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
}
