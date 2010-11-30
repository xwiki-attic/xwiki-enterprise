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
package org.xwiki.test.ui.administration;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.administration.elements.ImportAdministrationSectionPage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.HistoryPane;
import org.xwiki.test.ui.framework.elements.ViewPage;

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

    private ImportAdministrationSectionPage sectionPage;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        // Delete Test Page we import from XAR to ensure to start with a predefined state.
        getUtil().deletePage("Main", "TestPage");

        this.adminPage = new AdministrationPage();
        this.adminPage.gotoPage();

        this.sectionPage = this.adminPage.clickImportSection();

        // Remove our packages if they're there already, to ensure to start with a predefined state.
        if (this.sectionPage.isPackagePresent(PACKAGE_WITH_HISTORY)) {
            this.sectionPage.deletePackage(PACKAGE_WITH_HISTORY);
        }
        if (this.sectionPage.isPackagePresent(PACKAGE_WITHOUT_HISTORY)) {
            this.sectionPage.deletePackage(PACKAGE_WITHOUT_HISTORY);
        }
        if (this.sectionPage.isPackagePresent(BACKUP_PACKAGE)) {
            this.sectionPage.deletePackage(BACKUP_PACKAGE);
        }
    }

    /**
     * Verify that the Import page doesn't list any package by default in default XE.
     * @since 2.6RC1
     */
    @Test
    public void testImportHasNoPackageByDefault()
    {
        Assert.assertEquals(0, sectionPage.getPackageNames().size());
    }

    @Test
    public void testImportWithHistory() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + PACKAGE_WITH_HISTORY);

        this.sectionPage.attachPackage(fileUrl);
        this.sectionPage.selectPackage(PACKAGE_WITH_HISTORY);

        this.sectionPage.selectReplaceHistoryOption();
        this.sectionPage.importPackage();

        ViewPage importedPage = this.sectionPage.clickImportedPage("Main.TestPage");

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

        this.sectionPage.attachPackage(fileUrl);
        this.sectionPage.selectPackage(PACKAGE_WITHOUT_HISTORY);

        this.sectionPage.importPackage();

        ViewPage importedPage = this.sectionPage.clickImportedPage("Main.TestPage");

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

        this.sectionPage.attachPackage(fileUrl);
        this.sectionPage.selectPackage(BACKUP_PACKAGE);

        WebElement importAsBackup = getDriver().findElement(By.name("importAsBackup"));
        Assert.assertTrue(importAsBackup.isSelected());
        
        this.sectionPage.importPackage();

        ViewPage importedPage = this.sectionPage.clickImportedPage("Main.TestPage");

        // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
        // will race for completion. Let's wait for comments first.
        importedPage.openCommentsDocExtraPane();
        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("JohnDoe", history.getCurrentAuthor());
    }
    
    @Test
    public void testImportWhenImportAsBackupIsNotSelected() throws IOException
    {
        URL fileUrl = this.getClass().getResource("/administration/" + BACKUP_PACKAGE);

        this.sectionPage.attachPackage(fileUrl);
        this.sectionPage.selectPackage(BACKUP_PACKAGE);

        WebElement importAsBackup = getDriver().findElement(By.name("importAsBackup"));
        importAsBackup.click();
        Assert.assertFalse(importAsBackup.isSelected());
        
        this.sectionPage.importPackage();

        ViewPage importedPage = this.sectionPage.clickImportedPage("Main.TestPage");

        // Since the page by default opens the comments pane, if we instantly click on the history, the two tabs
        // will race for completion. Let's wait for comments first.
        importedPage.openCommentsDocExtraPane();
        HistoryPane history = importedPage.openHistoryDocExtraPane();

        Assert.assertEquals("Admin", history.getCurrentAuthor());
    }
}
