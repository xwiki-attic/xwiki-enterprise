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
package org.xwiki.it.ui;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.elements.WikiEditPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.TestUtils;

/**
 * Test wiki editing.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class EditWikiTest extends AbstractAdminAuthenticatedTest
{
    /** Page used for testing: Main.WikiEditTest */
    private WikiEditPage editPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        this.editPage = new WikiEditPage(getDriver());
        TestUtils.deletePage("Test", "EditWikiTest", getDriver());
    }

    @After
    public void cleanUp()
    {
        TestUtils.deletePage("Test", "EditWikiTest", getDriver());
    }

    /** Test that save and continue saves as a minor version. */
    @Test
    public void testSaveAndContinueIsMinorEdit()
    {
        this.editPage.switchToEdit("Test", "EditWikiTest");
        Assert.assertTrue(this.editPage.isNewDocument());
        this.editPage.setContent("abc1");
        this.editPage.clickSaveAndView();
        Assert.assertEquals("1.1", this.editPage.getMetaDataValue("version"));

        this.editPage.switchToEdit("Test", "EditWikiTest");
        Assert.assertFalse(this.editPage.isNewDocument());
        this.editPage.setContent("abc2");
        this.editPage.setMinorEdit(false);
        this.editPage.clickSaveAndContinue();
        this.editPage.clickCancel();
        Assert.assertEquals("1.2", this.editPage.getMetaDataValue("version"));
    }
}
