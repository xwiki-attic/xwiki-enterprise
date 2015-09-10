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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.administration.test.po.AdministrationPage;
import org.xwiki.administration.test.po.LocalizationAdministrationSectionPage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Verify the ability to change the language.
 * 
 * @version $Id$
 * @since 2.4RC1
 */
public class LanguageTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil());

    /**
     * Ensure the default language is English and that the wiki is in monolingual mode
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception
    {
        setLanguageSettings(false, "en");
    }

    /**
     * Make sure we set back the language to monolingual and english for other tests that come thereafter
     */
    @After
    public void tearDown()
    {
        setLanguageSettings(false, "en");
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testChangeLanguageInMonolingualModeUsingTheAdministrationPreference()
    {
        WikiEditPage edit = WikiEditPage.gotoPage("Test", "LanguageTest");
        edit.setContent("{{velocity}}context = ($xcontext.language), doc = ($doc.language), "
            + "default = ($doc.defaultLanguage), tdoc = ($tdoc.language), "
            + "tdocdefault = ($tdoc.defaultLanguage){{/velocity}}");
        ViewPage vp = edit.clickSaveAndView();

        // Current language must be "en"
        Assert.assertEquals("Invalid content", vp.getContent(),
            "context = (en), doc = (), default = (en), tdoc = (), tdocdefault = (en)");

        // Change default language to "fr"
        AdministrationPage adminPage = AdministrationPage.gotoPage();
        LocalizationAdministrationSectionPage sectionPage = adminPage.clickLocalizationSection();
        sectionPage.setDefaultLanguage("fr");
        sectionPage.clickSave();

        // Now language must be "fr"
        vp = getUtil().gotoPage("Test", "LanguageTest");
        Assert.assertTrue("Page not in French!", isPageInFrench());
        Assert.assertEquals("Invalid content", vp.getContent(),
            "context = (fr), doc = (), default = (en), tdoc = (), tdocdefault = (en)");
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testPassingLanguageInRequestHasNoEffectInMonoligualMode()
    {
        getUtil().gotoPage("Main", "WebHome", "view", "language=fr");
        Assert.assertTrue("Page not in English!", isPageInEnglish());
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testChangeLanguageInMultilingualModeUsingTheLanguageRequestParameter()
    {
        setLanguageSettings(true, "en");

        getUtil().gotoPage("Main", "WebHome", "view", "language=fr");
        Assert.assertTrue("Page not in French!", isPageInFrench());
    }

    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testHeaderCorrectLanguage()
    {
        setLanguageSettings(true, "en");

        // if we don't use language=default, the value stored in cookies is used, which might be wrong
        getUtil().gotoPage("Main", "Test", "view", "language=default");
        checkLanguageTagsArePresent("en");

        getUtil().gotoPage("Main", "Test", "view", "language=fr");
        checkLanguageTagsArePresent("fr");
    }

    /**
     * Assert that the given <code>language</code> is present in various attributes and tags on the page
     * 
     * @param language the language to use, should be a valid language, e.g. "en"
     */
    private void checkLanguageTagsArePresent(String language)
    {
        WebElement html = getDriver().findElement(By.tagName("html"));
        Assert.assertEquals(language, html.getAttribute("lang"));
        Assert.assertEquals(language, html.getAttribute("xml:lang"));

        ViewPage vp = new ViewPage();
        Assert.assertEquals("locale=" + language, vp.getHTMLMetaDataValue("gwt:property"));
        // For retro-compatibility only
        Assert.assertEquals(language, vp.getHTMLMetaDataValue("language"));

        String content = getDriver().getPageSource();
        Assert.assertTrue(content.contains("language=" + language));
    }

    /**
     * Check if the currently displayed page is in English, by looking at the "Log-Out" link
     */
    private boolean isPageInEnglish()
    {
        return getDriver().findElement(By.className("xdocLastModification")).getText().toLowerCase().contains(
            "last modified by");
    }

    /**
     * Check if the currently displayed page is in French, by looking at the "Log-Out" link
     */
    private boolean isPageInFrench()
    {
        return getDriver().findElement(By.className("xdocLastModification")).getText().toLowerCase().contains(
            "modifié par");
    }

    private void setLanguageSettings(boolean isMultiLingual, String defaultLanguage)
    {
        AdministrationPage adminPage = AdministrationPage.gotoPage();
        LocalizationAdministrationSectionPage sectionPage = adminPage.clickLocalizationSection();
        sectionPage.setMultiLingual(isMultiLingual);
        sectionPage.setDefaultLanguage(defaultLanguage);
        sectionPage.clickSave();
    }
}
