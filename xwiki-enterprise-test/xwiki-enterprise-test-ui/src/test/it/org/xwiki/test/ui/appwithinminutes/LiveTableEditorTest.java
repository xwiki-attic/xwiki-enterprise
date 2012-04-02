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

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.appwithinminutes.ApplicationHomeEditPage;
import org.xwiki.test.po.appwithinminutes.ApplicationHomePage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.LiveTableElement;

/**
 * Tests the last step of the App Within Minutes wizard.
 * 
 * @version $Id$
 * @since 4.0M1
 */
public class LiveTableEditorTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The page being tested.
     */
    private ApplicationHomeEditPage editPage;

    /**
     * The query string parameters passed to the edit action.
     */
    private final Map<String, String> editQueryStringParameters = new HashMap<String, String>();

    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        getUtil().deletePage(getTestClassName(), getTestMethodName());
        editQueryStringParameters.put("editor", "inline");
        editQueryStringParameters.put("template", "AppWithinMinutes.LiveTableTemplate");
        editQueryStringParameters.put("AppWithinMinutes.LiveTableClass_0_class", "XWiki.XWikiUsers");
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit", editQueryStringParameters);
        editPage = new ApplicationHomeEditPage();
    }

    /**
     * Adds, removes and reorders live table columns.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testManageColumns()
    {
        editPage.addLiveTableColumn("First Name");
        Assert.assertTrue(editPage.hasLiveTableColumn("First Name"));
        editPage.moveLiveTableColumnBefore("First Name", "Update date");
        editPage.removeLiveTableColumn("Page name");
        Assert.assertFalse(editPage.hasLiveTableColumn("Page name"));
        LiveTableElement liveTable = ((ApplicationHomePage) editPage.clickSaveAndView()).getEntriesLiveTable();
        liveTable.waitUntilReady();
        Assert.assertFalse(liveTable.hasColumn("Page name"));
        Assert.assertEquals(0, liveTable.getColumnIndex("First Name"));
        Assert.assertEquals(1, liveTable.getColumnIndex("Update date"));
    }

    /**
     * Tests that Save & Continue works fine.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testSaveAndContinue()
    {
        editPage.setDescription("wait for WYSIWYG to load");
        editPage.clickSaveAndContinue();
        editPage.waitForNotificationSuccessMessage("Saved");
        ApplicationHomePage viewPage = editPage.clickCancel();
        LiveTableElement liveTable = viewPage.getEntriesLiveTable();
        liveTable.waitUntilReady();
        Assert.assertTrue(liveTable.hasColumn("Page name"));
    }

    /**
     * Tests how deprecated columns are handled.
     */
    @Test
    public void testDeprecatedColumns()
    {
        // Fake a deprecated column by using a column that doesn't exist.
        editQueryStringParameters.put("AppWithinMinutes.LiveTableClass_0_columns", "doc.name foo");
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit", editQueryStringParameters);
        editPage = new ApplicationHomeEditPage();

        Assert.assertTrue(editPage.isDeprecatedLiveTableColumnsWarningDisplayed());
        Assert.assertFalse(editPage.isLiveTableColumnDeprecated("Page name"));
        Assert.assertTrue(editPage.isLiveTableColumnDeprecated("foo"));

        // Keep deprecated columns.
        editPage.removeAllDeprecatedLiveTableColumns(false);
        Assert.assertFalse(editPage.isDeprecatedLiveTableColumnsWarningDisplayed());
        Assert.assertTrue(editPage.isLiveTableColumnDeprecated("foo"));
        ApplicationHomePage viewPage = editPage.clickSaveAndView();
        LiveTableElement liveTable = viewPage.getEntriesLiveTable();
        liveTable.waitUntilReady();
        Assert.assertTrue(liveTable.hasColumn("foo"));

        // Edit again and remove the deprecated column.
        editPage = viewPage.editInline();
        Assert.assertTrue(editPage.isDeprecatedLiveTableColumnsWarningDisplayed());
        editPage.removeLiveTableColumn("foo");
        Assert.assertFalse(editPage.hasLiveTableColumn("foo"));
        // The warning must disappear if we remove the deprecated column.
        Assert.assertFalse(editPage.isDeprecatedLiveTableColumnsWarningDisplayed());

        // Reload and remove all deprecated columns.
        getDriver().navigate().refresh();
        editPage = new ApplicationHomeEditPage();
        editPage.removeAllDeprecatedLiveTableColumns(true);
        Assert.assertFalse(editPage.isDeprecatedLiveTableColumnsWarningDisplayed());
        Assert.assertTrue(editPage.hasLiveTableColumn("Page name"));
        Assert.assertFalse(editPage.hasLiveTableColumn("foo"));
    }

    /**
     * Tests that the live table isn't generated if the list of columns is empty.
     */
    @Test
    public void testNoColumns()
    {
        // Make sure the list of columns is empty.
        editQueryStringParameters.put("AppWithinMinutes.LiveTableClass_0_columns", "");
        getUtil().gotoPage(getTestClassName(), getTestMethodName(), "edit", editQueryStringParameters);
        ApplicationHomePage viewPage = new ApplicationHomeEditPage().clickSaveAndView();
        Assert.assertFalse(viewPage.hasEntriesLiveTable());
        Assert.assertEquals("", viewPage.editWiki().getContent());
    }
}
