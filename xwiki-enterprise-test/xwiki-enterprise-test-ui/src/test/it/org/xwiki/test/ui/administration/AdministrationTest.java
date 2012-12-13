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

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.administration.test.po.AdministrablePage;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.administration.test.po.PageElementsAdministrationSectionPage;
import org.xwiki.panels.test.po.PageWithPanels;
import org.xwiki.panels.test.po.PanelEditPage;
import org.xwiki.panels.test.po.PanelsHomePage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;

/**
 * Verify the overall Administration application features.
 *
 * @version $Id$
 * @since 4.3M1
 */
public class AdministrationTest extends AbstractAdminAuthenticatedTest
{
    /**
     * This method makes the following tests :
     *
     * <ul>
     * <li>Login as global admin.</li>
     * <li>Validate presence of default sections for global and space sections.</li>
     * <li>Validate presence of application administration sections at global level only.</li>
     * </ul>
     */
    @Test
    public void globalAndSpaceSections()
    {
        // Go to any page. Note that we go to a not existing page for 2 reasons:
        // - verify that it has a menu action to administer the wiki
        // - (more importantly) it's faster than going to the wiki's home page which will take longer to display... ;)
        getUtil().gotoPage(getTestClassName(), getTestMethodName());
        AdministrablePage page = new AdministrablePage();
        AdministrationPage administrationPage = page.clickAdministerWiki();

        // TODO: Move these tests in their own modules, i.e. the modules that brought the Admin UI extension
        administrationPage.hasSection("Editing");
        administrationPage.hasSection("Localization");
        administrationPage.hasSection("Email");
        administrationPage.hasSection("Presentation");
        administrationPage.hasSection("Elements");
        administrationPage.hasSection("Registration");
        administrationPage.hasSection("Users");
        administrationPage.hasSection("Groups");
        administrationPage.hasSection("Rights");
        administrationPage.hasSection("Registration");
        administrationPage.hasSection("Import");
        administrationPage.hasSection("Export");
        administrationPage.hasSection("Templates");
        administrationPage.hasSection("MessageStream");
        administrationPage.hasSection("Panels.PanelWizard");

        // Select space administration (Main space)
        AdministrationPage spaceAdministrationPage = administrationPage.selectSpaceToAdminister("Main");

        // Note: I'm not sure this is good enough since waitUntilPageIsLoaded() tests for the existence of the footer
        // but if the page hasn't started reloading then the footer will be present... However I ran this test 300
        // times in a row without any failure...
        spaceAdministrationPage.waitUntilPageIsLoaded();

        spaceAdministrationPage.hasSection("Presentation");
        spaceAdministrationPage.hasSection("Elements");
        spaceAdministrationPage.hasSection("Rights");
        spaceAdministrationPage.hasSection("Panels.PanelWizard");

        // All those sections should not be present
        spaceAdministrationPage.hasNotSection("Editing");
        spaceAdministrationPage.hasNotSection("Localization");
        spaceAdministrationPage.hasNotSection("Email");
        spaceAdministrationPage.hasNotSection("Registration");
        spaceAdministrationPage.hasNotSection("Users");
        spaceAdministrationPage.hasNotSection("Groups");
        spaceAdministrationPage.hasNotSection("Registration");
        spaceAdministrationPage.hasNotSection("Import");
        spaceAdministrationPage.hasNotSection("Export");
        spaceAdministrationPage.hasNotSection("Templates");
        spaceAdministrationPage.hasNotSection("MessageStream");
    }

    /**
     * @see "XWIKI-8591: Cannot use a panel with a name containing spaces"
     */
    @Test
    public void addPanelWithSpacesInName()
    {
        // Create a panel whose name contain spaces.
        String panelName = "My First Panel";
        getUtil().deletePage("Panels", panelName);
        PanelEditPage panelEditPage = PanelsHomePage.gotoPage().createPanel(panelName);
        panelEditPage.setContent(String.format(PanelEditPage.DEFAULT_CONTENT_FORMAT, panelName, getTestMethodName()));
        panelEditPage.clickSaveAndContinue();

        // Add the panel to the right column from the administration.
        PageElementsAdministrationSectionPage pageElements =
            new AdministrablePage().clickAdministerWiki().clickPageElementsSection();
        String rightPanels = pageElements.getRightPanels();
        pageElements.setRightPanels(rightPanels + ",Panels." + panelName);
        try {
            pageElements.clickSave();
            Assert.assertTrue(new PageWithPanels().hasPanel(panelName));
        } finally {
            // Restore the right panels.
            pageElements = PageElementsAdministrationSectionPage.gotoPage();
            pageElements.setRightPanels(rightPanels);
            pageElements.clickSave();
        }
    }
}
