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
package org.xwiki.it.ui.administration;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.administration.elements.AdministrationPage;
import org.xwiki.it.ui.administration.elements.ImportPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.HistoryPane;
import org.xwiki.it.ui.framework.elements.ViewPage;

/**
 * Test the Import XAR feature.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class ImportTest extends AbstractAdminAuthenticatedTest
{
    private static final String PACKAGE_WITHOUT_HISTORY = "Main.TestPage-no-history.xar";

    private static final String PACKAGE_WITH_HISTORY = "Main.TestPage-with-history.xar";

    private static final String BACKUP_PACKAGE = "Main.TestPage-backup.xar";

    private AdministrationPage adminPage;

    private ImportPage importPage;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        // Delete Test Page we import from XAR to ensure to start with a predefined state.
        getUtil().deletePage("Main", "TestPage");

        this.adminPage = new AdministrationPage();
        this.adminPage.gotoPage();

        this.importPage = this.adminPage.clickImportSection();

        // Remove our packages if they're there already, to ensure to start with a predefined state.
        if (this.importPage.isPackagePresent(PACKAGE_WITH_HISTORY)) {
            this.importPage.deletePackage(PACKAGE_WITH_HISTORY);
        }
        if (this.importPage.isPackagePresent(PACKAGE_WITHOUT_HISTORY)) {
            this.importPage.deletePackage(PACKAGE_WITHOUT_HISTORY);
        }
        if (this.importPage.isPackagePresent(BACKUP_PACKAGE)) {
            this.importPage.deletePackage(BACKUP_PACKAGE);
        }
    }

    @Test
    public void testImportWithHistory() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + PACKAGE_WITH_HISTORY);

        this.importPage.attachPackage(fileUrl);
        this.importPage.selectPackage(PACKAGE_WITH_HISTORY);

        this.importPage.selectReplaceHistoryOption();
        this.importPage.importPackage();

        ViewPage importedPage = this.importPage.clickImportedPage("Main.TestPage");

        // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
        // will race for completion. Let's wait for comments first.
        importedPage.openCommentsDocExtraPane();
        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("4.1", history.getCurrentVersion());
        Assert.assertEquals("Imported from XAR", history.getCurrentVersionComment());
        Assert.assertTrue(history.hasVersionWithSummary("A new version of the document"));
    }

    @Test
    public void testImportWithNewHistoryVersion() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + PACKAGE_WITHOUT_HISTORY);

        this.importPage.attachPackage(fileUrl);
        this.importPage.selectPackage(PACKAGE_WITHOUT_HISTORY);

        this.importPage.importPackage();

        ViewPage importedPage = this.importPage.clickImportedPage("Main.TestPage");

        // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
        // will race for completion. Let's wait for comments first.
        importedPage.openCommentsDocExtraPane();
        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("1.1", history.getCurrentVersion());
        Assert.assertEquals("Imported from XAR", history.getCurrentVersionComment());
    }

    @Test
    public void testImportAsBackup() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + BACKUP_PACKAGE);

        this.importPage.attachPackage(fileUrl);
        this.importPage.selectPackage(BACKUP_PACKAGE);

        this.importPage.importPackage();

        ViewPage importedPage = this.importPage.clickImportedPage("Main.TestPage");

        // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
        // will race for completion. Let's wait for comments first.
        importedPage.openCommentsDocExtraPane();
        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("JohnDoe", history.getCurrentAuthor());
    }
}
