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
import org.xwiki.test.po.xe.HomePage;
import org.xwiki.test.ui.po.FormElement;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.ObjectEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Test Skin Extensions.
 * 
 * @version $Id$
 * @since 4.1
 */
public class SkinxTest extends AbstractAdminAuthenticatedTest
{
    private static final String SCRIPT = "window.document.title = 'script active';";

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        getUtil().deletePage("Test", "SkinxTest");
    }

    /** http://jira.xwiki.org/browse/XWIKI-7913 */
    @Test
    public void testJavascriptExtension()
    {
        // Create a doc
        WikiEditPage wep = WikiEditPage.gotoPage("Test", "SkinxTest");
        wep.setContent("this is the content");
        ViewPage vp = wep.clickSaveAndView();

        // Add an XWikiGroups object
        ObjectEditPage oep = vp.editObjects();
        FormElement objectForm = oep.addObject("XWiki.JavaScriptExtension");
        objectForm.setFieldValue(By.id("XWiki.JavaScriptExtension_0_code"), SCRIPT);
        objectForm.getSelectElement(By.id("XWiki.JavaScriptExtension_0_use")).select("always");
        vp = oep.clickSaveAndView();
        Assert.assertTrue(isScriptActive(vp));
        vp = HomePage.gotoPage();
        Assert.assertTrue(isScriptActive(vp));

        oep = ObjectEditPage.gotoPage("Test", "SkinxTest");
        objectForm = oep.getObjectsOfClass("XWiki.JavaScriptExtension").get(0);
        objectForm.getSelectElement(By.id("XWiki.JavaScriptExtension_0_use")).select("currentPage");
        vp = oep.clickSaveAndView();
        Assert.assertTrue(isScriptActive(vp));
        vp = HomePage.gotoPage();
        Assert.assertFalse(isScriptActive(vp));

        oep = ObjectEditPage.gotoPage("Test", "SkinxTest");
        objectForm = oep.getObjectsOfClass("XWiki.JavaScriptExtension").get(0);
        objectForm.getSelectElement(By.id("XWiki.JavaScriptExtension_0_use")).select("onDemand");
        vp = oep.clickSaveAndView();
        Assert.assertFalse(isScriptActive(vp));
    }

    private static boolean isScriptActive(ViewPage vp)
    {
        return "script active".equals(vp.getPageTitle());
    }
}
