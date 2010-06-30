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
package com.xpn.xwiki.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.ColibriSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the ability to change the wiki language.
 * 
 * @version $Id$
 * @todo refactor after creating the APIs for each skin so that we don't have to use getSelenium() at all
 */
public class LanguageTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/1.0";
    
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the ability to change the wiki language");
        suite.addTestSuite(LanguageTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();

        // Ensure the default language is English and that the wiki is in monolingual mode
        setMonoLingualAndEnglish();
    }

    /**
     * Make sure we set back the language to monolingual and english for other tests that come thereafter.
     */
    @Override
    protected void tearDown()
    {
        setMonoLingualAndEnglish();
    }

    public void testChangeLanguageInMonolingualModeUsingTheAdministrationPreference()
    {        
        editInWikiEditor("Test", "LanguageTest", SYNTAX);
        setFieldValue("content", "context = ($context.language), doc = ($doc.language), "
            + "default = ($doc.defaultLanguage), tdoc = ($tdoc.language), " + "tdocdefault = ($tdoc.defaultLanguage)");
        clickEditSaveAndView();

        assertTrue("Invalid content", getSelenium().getText("xwikicontent").contains(
            "context = (en), doc = (), default = (en), tdoc = (), tdocdefault = (en)"));

        openAdministrationPage();
        clickLinkWithText("General");
        setFieldValue("XWiki.XWikiPreferences_0_default_language", "fr");
        clickEditSaveAndContinue();

        open("Test", "LanguageTest");
        assertTrue("Header doesn't contain \"Quitter la session\"",
            getSelenium().getBodyText().contains("Quitter la session"));
        assertTrue("Invalid content", getSelenium().getText("xwikicontent").contains(
            "context = (fr), doc = (), default = (en), tdoc = (), tdocdefault = (en)"));
    }

    public void testVerifyPassingLanguageInRequestHasNotEffectInMonoligualMode()
    {
        open("Main", "WebHome", "view", "language=fr");

        assertTrue("Header doesn't contain \"Log-out\"", getSelenium().getBodyText().contains("Log-out"));
    }

    public void testChangeLanguageInMultilingualModeUsingTheLanguageRequestParameter()
    {
        openAdministrationPage();
        clickLinkWithText("General");
        getSelenium().select("XWiki.XWikiPreferences_0_multilingual", "value=1");
        clickEditSaveAndContinue();
        open("Main", "WebHome", "view", "language=fr");

        assertTrue("Header doesn't contain \"Quitter la session\"",
            getSelenium().getBodyText().contains("Quitter la session"));
    }

    private void setMonoLingualAndEnglish()
    {
        // Ensure the default language is English and that the wiki is in monolingual mode
        openAdministrationPage();
        clickLinkWithText("General");

        // Note: We cannot use "label=No" as some tests below might have changed the language...
        getSelenium().select("XWiki.XWikiPreferences_0_multilingual", "value=0");

        setFieldValue("XWiki.XWikiPreferences_0_default_language", "en");
        clickEditSaveAndContinue();

        assertTrue("Header doesn't contain \"Log-out\"", getSelenium().getBodyText().contains("Log-out"));
    }
}
