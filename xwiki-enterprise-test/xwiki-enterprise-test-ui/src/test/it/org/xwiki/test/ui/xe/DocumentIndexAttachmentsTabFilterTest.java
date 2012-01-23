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
package org.xwiki.test.ui.xe;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.index.test.po.AllDocsPage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.po.LiveTableElement;

/**
 * Test to prove that XE-701 remains fixed.
 * XE-701: Innacurate results when filtering attachments by page in "Document Index"
 * 
 * @version $Id$
 * @since 2.5M2
 */
public class DocumentIndexAttachmentsTabFilterTest extends AbstractAdminAuthenticatedTest
{
    private final String FILTER_STRING = "an";

    private final String PAGE_COLUMN_ID = "xwiki-livetable-allattachments-filter-2";

    // This test is against XWiki Enterprise XE-701 http://jira.xwiki.org/jira/browse/XE-701 (fixed in 2.5M1)
    // WARN: calling isReady() and waitUntilReady() from LiveTableElement.java inside this class fails.
    // Used example from JIRA issue
    @Test
    public void testAttachmentsPane()
    {
        AllDocsPage docsPage = AllDocsPage.gotoPage();
        LiveTableElement liveTable = docsPage.clickAttachmentsTab();
        // Here we test if all the Columns are displayed
        Assert.assertTrue("No Filename column found", liveTable.hasColumn("Filename"));
        Assert.assertTrue("No Space column found", liveTable.hasColumn("Space"));
        Assert.assertTrue("No Date column found", liveTable.hasColumn("Date"));
        Assert.assertTrue("No Author column found", liveTable.hasColumn("Author"));
        Assert.assertTrue("No Type column found", liveTable.hasColumn("Type"));
        Assert.assertTrue("No Page column found", liveTable.hasColumn("Page"));

        // Here we filter the livetable
        liveTable.filterColumn(PAGE_COLUMN_ID, FILTER_STRING);
        List<WebElement> pageResults = getDriver().findElements(By.xpath("//td[@class='pagename']"));

        // Here we get the results that remain after applying the filter
        // and we check if there is a result that doesn't contain the filter, the test will fail
        for (int i = 0; i < pageResults.size(); i++) {
            String text = pageResults.get(i).getText();
            Assert.assertTrue("This [" + text + "] should not be here !", text.toLowerCase().contains("an"));
        }
    }
}
