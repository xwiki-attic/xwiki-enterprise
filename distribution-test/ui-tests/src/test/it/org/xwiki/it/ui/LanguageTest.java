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

import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.xwiki.it.ui.administration.elements.AdminSectionPage;
import org.xwiki.it.ui.administration.elements.AdministrationPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.elements.FormElement;
import org.xwiki.it.ui.framework.elements.ViewPage;
import org.xwiki.it.ui.framework.elements.editor.WikiEditPage;

/**
 * Verify the ability to change the language.
 * 
 * @version $Id$
 * @since 2.4RC1
 */
public class LanguageTest extends AbstractAdminAuthenticatedTest
{
    /** Administration interface */
    private AdministrationPage adminPage;

    /**
     * Ensure the default language is English and that the wiki is in monolingual mode
     */
    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        this.adminPage = new AdministrationPage();
        setMonoLingualAndEnglish();
    }

    /**
     * Make sure we set back the language to monolingual and english for other tests that come thereafter
     */
    @After
    public void tearDown()
    {
        setMonoLingualAndEnglish();
    }

    @Test
    public void testChangeLanguageInMonolingualModeUsingTheAdministrationPreference()
    {
        WikiEditPage edit = new WikiEditPage();
        edit.switchToEdit("Test", "LanguageTest");
        edit.setContent("{{velocity}}context = ($context.language), doc = ($doc.language), "
            + "default = ($doc.defaultLanguage), tdoc = ($tdoc.language), "
            + "tdocdefault = ($tdoc.defaultLanguage){{/velocity}}");
        ViewPage vp = edit.clickSaveAndView();

        // Current language must be "en"
        Assert.assertEquals("Invalid content", vp.getContent(),
            "context = (en), doc = (), default = (en), tdoc = (), tdocdefault = (en)");

        // Change default language to "fr"
        this.adminPage.gotoPage();
        AdminSectionPage general = this.adminPage.clickGeneralSection();
        FormElement form = general.getForm();
        form.setFieldValue(By.id("XWiki.XWikiPreferences_0_default_language"), "fr");
        general.clickSave();

        // Now language must be "fr"
        vp = getUtil().gotoPage("Test", "LanguageTest");
        Assert.assertTrue("Header doesn't contain \"Quitter la session\"", isPageInFrench());
        Assert.assertEquals("Invalid content", vp.getContent(),
            "context = (fr), doc = (), default = (en), tdoc = (), tdocdefault = (en)");
    }

    @Test
    public void testPassingLanguageInRequestHasNoEffectInMonoligualMode()
    {
        getUtil().gotoPage("Main", "WebHome", "view", "language=fr");
        Assert.assertTrue("Header doesn't contain \"Log-out\"", isPageInEnglish());
    }

    @Test
    public void testChangeLanguageInMultilingualModeUsingTheLanguageRequestParameter()
    {
        setMultilingualAndEnglish();

        getUtil().gotoPage("Main", "WebHome", "view", "language=fr");
        Assert.assertTrue("Header doesn't contain \"Quitter la session\"", isPageInFrench());
    }

    @Test
    public void testUnescapedLanguageInLinks() throws UnsupportedEncodingException
    {
        setMultilingualAndEnglish();

        // XSKINX-36
        String chars = "<>'&"; // no ", since it breaks output elsewhere
        // Since getPageSource returns a cleaned-up version of the source, and not the real HTML source, we must pass
        // something that doesn't break HTML validity; try breaking CSS links
        String test = getUtil().escapeURL("'/><!-- " + chars + " --><link href='");
        getUtil().gotoPage("Main", "Test", "view", "language=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(chars) < 0);

        // Also try breaking script links
        test = getUtil().escapeURL("'><!-- " + chars + " --><script src='");
        getUtil().gotoPage("Main", "Test", "view", "language=" + test);
        Assert.assertTrue(getDriver().getPageSource().indexOf(chars) < 0);
    }

    @Test
    public void testHeaderCorrectLanguage()
    {
        setMultilingualAndEnglish();

        getUtil().gotoPage("Main", "Test");
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

        String content = getDriver().getPageSource();
        Assert.assertTrue(content.contains("<meta name=\"gwt:property\" content=\"locale=" + language + "\">"));
        Assert.assertTrue(content.contains("<meta name=\"language\" content=\"" + language + "\">"));
        Assert.assertTrue(content.contains("language=" + language));
    }

    /**
     * Check if the currently displayed page is in English, by looking at the "Log-Out" link
     */
    private boolean isPageInEnglish()
    {
        return getDriver().findElement(By.xpath("//div[@id='tmLogout']//strong")).getText().contains("Log-out");
    }

    /**
     * Check if the currently displayed page is in French, by looking at the "Log-Out" link
     */
    private boolean isPageInFrench()
    {
        return getDriver().findElement(By.xpath("//div[@id='tmLogout']//strong")).getText().contains(
            "Quitter la session");
    }

    /**
     * Switch to the administration page, go to the "General" section, set wiki to monolingual mode and set the default
     * language to "en"
     */
    private void setMonoLingualAndEnglish()
    {
        this.adminPage.gotoPage();
        AdminSectionPage general = this.adminPage.clickGeneralSection();
        FormElement form = general.getForm();
        form.getSelectElement(By.id("XWiki.XWikiPreferences_0_multilingual")).select("0");
        form.setFieldValue(By.id("XWiki.XWikiPreferences_0_default_language"), "en");
        general.clickSave();

        Assert.assertTrue("Not logged in", this.adminPage.isAuthenticated());
    }

    /**
     * Switch to the administration page, go to the "General" section, enable multilingual mode and set the default
     * language to "en"
     */
    private void setMultilingualAndEnglish()
    {
        this.adminPage.gotoPage();
        AdminSectionPage general = this.adminPage.clickGeneralSection();
        FormElement form = general.getForm();
        form.getSelectElement(By.id("XWiki.XWikiPreferences_0_multilingual")).select("1");
        form.setFieldValue(By.id("XWiki.XWikiPreferences_0_default_language"), "en");
        general.clickSave();

        Assert.assertTrue("Not logged in", this.adminPage.isAuthenticated());
    }
}
