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
import org.xwiki.it.ui.framework.elements.HistoryPane;
import org.xwiki.it.ui.administration.elements.ImportPage;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

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

    @Before
    public void setUp()
    {
        super.setUp();

        // Delete Test Page we import from XAR to ensure to start with a predefined state.
        getUtil().deletePage("Main", "TestPage");

        adminPage = new AdministrationPage();
        adminPage.gotoPage();

        importPage = adminPage.clickImportSection();

        // Remove our packages if they're there already, to ensure to start with a predefined state.
        if (importPage.isPackagePresent(PACKAGE_WITH_HISTORY)) {
            importPage.deletePackage(PACKAGE_WITH_HISTORY);
        }
        if (importPage.isPackagePresent(PACKAGE_WITHOUT_HISTORY)) {
            importPage.deletePackage(PACKAGE_WITHOUT_HISTORY);
        }
        if (importPage.isPackagePresent(BACKUP_PACKAGE)) {
            importPage.deletePackage(BACKUP_PACKAGE);
        }
    }

    @Test
    public void testImportWithHistory() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + PACKAGE_WITH_HISTORY);

        importPage.attachPackage(fileUrl);
        importPage.selectPackage(PACKAGE_WITH_HISTORY);

        importPage.selectReplaceHistoryOption();
        importPage.importPackage();

        ViewPage importedPage = importPage.clickImportedPage("Main.TestPage");

        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("4.1", history.getCurrentVersion());
        Assert.assertEquals("Imported from XAR", history.getCurrentVersionComment());
        Assert.assertTrue(history.hasVersionWithSummary("A new version of the document"));
    }

    @Test
    public void testImportWithNewHistoryVersion() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + PACKAGE_WITHOUT_HISTORY);

        importPage.attachPackage(fileUrl);
        importPage.selectPackage(PACKAGE_WITHOUT_HISTORY);

        importPage.importPackage();

        ViewPage importedPage = importPage.clickImportedPage("Main.TestPage");

        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("1.1", history.getCurrentVersion());
        Assert.assertEquals("Imported from XAR", history.getCurrentVersionComment());
    }

    @Test
    public void testImportAsBackup() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + BACKUP_PACKAGE);

        importPage.attachPackage(fileUrl);
        importPage.selectPackage(BACKUP_PACKAGE);

        importPage.importPackage();

        ViewPage importedPage = importPage.clickImportedPage("Main.TestPage");

        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("JohnDoe", history.getCurrentAuthor());
    }
}
