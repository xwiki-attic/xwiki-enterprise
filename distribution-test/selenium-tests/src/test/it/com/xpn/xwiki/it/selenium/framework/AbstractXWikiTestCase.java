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
package com.xpn.xwiki.it.selenium.framework;

import java.io.IOException;
import java.util.Properties;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.codehaus.plexus.util.StringInputStream;

import com.thoughtworks.selenium.Selenium;

/**
 * All XWiki Selenium tests must extend this class.
 * 
 * @version $Id$
 */
public abstract class AbstractXWikiTestCase extends TestCase implements SkinExecutor
{
    public static final String DOC = "selenium.browserbot.getCurrentWindow().document.";

    private static final int WAIT_TIME = 30000;

    private SkinExecutor skinExecutor;

    private Selenium selenium;

    public void setSkinExecutor(SkinExecutor skinExecutor)
    {
        this.skinExecutor = skinExecutor;
    }

    public SkinExecutor getSkinExecutor()
    {
        if (this.skinExecutor == null) {
            throw new RuntimeException("Skin executor hasn't been initialized. Make sure to wrap " + "your test in a "
                + XWikiTestSuite.class.getName() + " class and call "
                + " addTestSuite(Class testClass, SkinExecutor skinExecutor).");
        }
        return this.skinExecutor;
    }

    public void setSelenium(Selenium selenium)
    {
        this.selenium = selenium;
    }

    public Selenium getSelenium()
    {
        return this.selenium;
    }

    // Convenience methods wrapping Selenium

    public void open(String url)
    {
        getSelenium().open(url);
    }

    public void open(String space, String page)
    {
        open(getUrl(space, page));
    }

    public void open(String space, String page, String action)
    {
        open(getUrl(space, page, action));
    }

    public void open(String space, String page, String action, String queryString)
    {
        open(getUrl(space, page, action, queryString));
    }

    public String getTitle()
    {
        return getSelenium().getTitle();
    }

    public void assertPage(String space, String page)
    {
        assertTrue(getTitle().matches(".*\\(" + space + "." + page + "\\) - XWiki"));
    }

    public boolean isExistingPage(String space, String page)
    {
        String saveUrl = getSelenium().getLocation();

        open(getUrl(space, page));
        boolean exists = !getSelenium().isTextPresent("The requested document could not be found.");

        // Restore original URL
        open(saveUrl);

        return exists;
    }

    public void assertTitle(String title)
    {
        assertEquals(title, getTitle());
    }

    public boolean isElementPresent(String locator)
    {
        return getSelenium().isElementPresent(locator);
    }

    public boolean isLinkPresent(String text)
    {
        return isElementPresent("link=" + text);
    }

    public void clickLinkWithText(String text)
    {
        clickLinkWithText(text, true);
    }

    public void assertTextPresent(String text)
    {
        assertTrue("[" + text + "] isn't present.", getSelenium().isTextPresent(text));
    }

    public void assertTextNotPresent(String text)
    {
        assertFalse("[" + text + "] is present.", getSelenium().isTextPresent(text));
    }

    public void assertElementPresent(String elementLocator)
    {
        assertTrue("[" + elementLocator + "] isn't present.", isElementPresent(elementLocator));
    }

    public void assertElementNotPresent(String elementLocator)
    {
        assertFalse("[" + elementLocator + "] is present.", isElementPresent(elementLocator));
    }

    public void waitPage()
    {
        waitPage(WAIT_TIME);
    }

    /**
     * @deprecated use {@link #waitPage()} instead
     */
    @Deprecated
    public void waitPage(int nbMillisecond)
    {
        getSelenium().waitForPageToLoad(String.valueOf(nbMillisecond));
    }
    
    public void createPage(String space, String page, String content)
    {
        createPage(space, page, content, null);
    }

    public void createPage(String space, String page, String content, String syntax)
    {
        // If the page already exists, delete it first
        deletePage(space, page);
        if (syntax == null) {
            editInWikiEditor(space, page);
        } else {
            editInWikiEditor(space, page, syntax);
        }
        setFieldValue("content", content);
        clickEditSaveAndView();
    }

    public void deletePage(String space, String page)
    {
        open(space, page, "delete", "confirm=1");
    }

    public void restorePage(String space, String page)
    {
        open(space, page, "view");
        if (getSelenium().isTextPresent("Restore")) {
            clickLinkWithText("Restore", true);
        }
    }

    public void clickLinkWithLocator(String locator)
    {
        clickLinkWithLocator(locator, true);
    }

    public void clickLinkWithLocator(String locator, boolean wait)
    {
        assertElementPresent(locator);
        getSelenium().click(locator);
        if (wait) {
            waitPage();
        }
    }

    public void clickLinkWithText(String text, boolean wait)
    {
        clickLinkWithLocator("link=" + text, wait);
    }

    public boolean isChecked(String locator)
    {
        return getSelenium().isChecked(locator);
    }

    public String getFieldValue(String fieldName)
    {
        // Note: We could use getSelenium().getvalue() here. However getValue() is stripping spaces
        // and some of our tests verify that there are leading spaces/empty lines.
        return getSelenium().getEval(
            "selenium.browserbot.getCurrentWindow().document.getElementById(\"" + fieldName + "\").value");
    }

    public void setFieldValue(String fieldName, String value)
    {
        getSelenium().type(fieldName, value);
    }

    public void checkField(String locator)
    {
        getSelenium().check(locator);
    }

    public void submit()
    {
        clickLinkWithXPath("//input[@type='submit']");
    }

    public void submit(String locator)
    {
        clickLinkWithLocator(locator);
    }

    public void submit(String locator, boolean wait)
    {
        clickLinkWithLocator(locator, wait);
    }

    public void clickLinkWithXPath(String xpath)
    {
        clickLinkWithXPath(xpath, true);
    }

    public void clickLinkWithXPath(String xpath, boolean wait)
    {
        clickLinkWithLocator("xpath=" + xpath, wait);
    }

    public void waitForCondition(String condition)
    {
        getSelenium().waitForCondition(condition, "" + WAIT_TIME);
    }

    /**
     * {@inheritDoc}
     * 
     * @see SkinExecutor#clickEditPage()
     */
    public void clickEditPage()
    {
        getSkinExecutor().clickEditPage();
    }

    public void clickDeletePage()
    {
        getSkinExecutor().clickDeletePage();
    }

    public void clickCopyPage()
    {
        getSkinExecutor().clickCopyPage();
    }

    public void clickEditPreview()
    {
        getSkinExecutor().clickEditPreview();
    }

    public void clickEditSaveAndContinue()
    {
        getSkinExecutor().clickEditSaveAndContinue();
    }

    public void clickEditCancelEdition()
    {
        getSkinExecutor().clickEditCancelEdition();
    }

    public void clickEditSaveAndView()
    {
        getSkinExecutor().clickEditSaveAndView();
    }

    public boolean isAuthenticated()
    {
        return getSkinExecutor().isAuthenticated();
    }

    public void logout()
    {
        getSkinExecutor().logout();
    }

    public void login(String username, String password, boolean rememberme)
    {
        getSkinExecutor().login(username, password, rememberme);
    }

    public void loginAsAdmin()
    {
        getSkinExecutor().loginAsAdmin();
    }

    public void clickLogin()
    {
        getSkinExecutor().clickLogin();
    }

    public void clickRegister()
    {
        getSkinExecutor().clickRegister();
    }
    
    public String getEditorSyntax()
    {
        return getSkinExecutor().getEditorSyntax();
    }
    
    public void setEditorSyntax(String syntax)
    {
        getSkinExecutor().setEditorSyntax(syntax);
    }
    
    public void editInWikiEditor(String space, String page)
    {
        getSkinExecutor().editInWikiEditor(space, page);
    }    
    
    public void editInWikiEditor(String space, String page, String syntax)
    {
        getSkinExecutor().editInWikiEditor(space, page, syntax);
    }

    public void editInWysiwyg(String space, String page)
    {
        getSkinExecutor().editInWysiwyg(space, page);                
    }
    
    public void editInWysiwyg(String space, String page, String syntax)
    {
        getSkinExecutor().editInWysiwyg(space, page, syntax);                
    }

    public void clearWysiwygContent()
    {
        getSkinExecutor().clearWysiwygContent();
    }

    public void keyPressAndWait(String element, String keycode) throws InterruptedException
    {
        getSelenium().keyPress(element, keycode);
        waitPage();
    }

    public void typeInWysiwyg(String text)
    {
        getSkinExecutor().typeInWysiwyg(text);
    }

    public void typeInWiki(String text)
    {
        getSkinExecutor().typeInWiki(text);
    }

    public void typeEnterInWysiwyg()
    {
        getSkinExecutor().typeEnterInWysiwyg();
    }

    public void typeShiftEnterInWysiwyg()
    {
        getSkinExecutor().typeShiftEnterInWysiwyg();
    }

    public void clickWysiwygUnorderedListButton()
    {
        getSkinExecutor().clickWysiwygUnorderedListButton();
    }

    public void clickWysiwygOrderedListButton()
    {
        getSkinExecutor().clickWysiwygOrderedListButton();
    }

    public void clickWysiwygIndentButton()
    {
        getSkinExecutor().clickWysiwygIndentButton();
    }

    public void clickWysiwygOutdentButton()
    {
        getSkinExecutor().clickWysiwygOutdentButton();
    }

    public void clickWikiBoldButton()
    {
        getSkinExecutor().clickWikiBoldButton();
    }

    public void clickWikiItalicsButton()
    {
        getSkinExecutor().clickWikiItalicsButton();
    }

    public void clickWikiUnderlineButton()
    {
        getSkinExecutor().clickWikiUnderlineButton();
    }

    public void clickWikiLinkButton()
    {
        getSkinExecutor().clickWikiLinkButton();
    }

    public void clickWikiHRButton()
    {
        getSkinExecutor().clickWikiHRButton();
    }

    public void clickWikiImageButton()
    {
        getSkinExecutor().clickWikiImageButton();
    }

    public void clickWikiSignatureButton()
    {
        getSkinExecutor().clickWikiSignatureButton();
    }

    public void assertWikiTextGeneratedByWysiwyg(String text)
    {
        getSkinExecutor().assertWikiTextGeneratedByWysiwyg(text);
    }

    public void assertHTMLGeneratedByWysiwyg(String xpath) throws Exception
    {
        getSkinExecutor().assertHTMLGeneratedByWysiwyg(xpath);
    }

    public void assertGeneratedHTML(String xpath) throws Exception
    {
        getSkinExecutor().assertGeneratedHTML(xpath);
    }

    public void openAdministrationPage()
    {
        getSkinExecutor().openAdministrationPage();
    }
    
    public void openAdministrationSection(String section)
    {
        getSkinExecutor().openAdministrationSection(section);
    }

    public String getUrl(String space, String doc)
    {
        return getUrl(space, doc, "view");
    }

    public String getUrl(String space, String doc, String action)
    {
        return "/xwiki/bin/" + action + "/" + space + "/" + doc;
    }

    public String getUrl(String space, String doc, String action, String queryString)
    {
        return getUrl(space, doc, action) + "?" + queryString;
    }

    public void pressKeyboardShortcut(String shortcut, boolean withCtrlModifier, boolean withAltModifier,
        boolean withShiftModifier) throws InterruptedException
    {
        getSkinExecutor().pressKeyboardShortcut(shortcut, withCtrlModifier, withAltModifier, withShiftModifier);
    }

    /**
     * Set global xwiki configuration options (as if the xwiki.cfg file had been modified). This is useful for testing
     * configuration options.
     * 
     * @param configuration the configuration in {@link Properties} format. For example "param1=value2\nparam2=value2"
     * @throws IOException if an error occurs while parsing the configuration
     */
    public void setXWikiConfiguration(String configuration) throws IOException
    {
        Properties properties = new Properties();
        properties.load(new StringInputStream(configuration));
        StringBuffer sb = new StringBuffer();

        // Since we don't have access to the XWiki object from Selenium tests and since we don't want to restart XWiki
        // with a different xwiki.cfg file for each test that requires a configuration change, we use the following
        // trick: We create a document and we access the XWiki object with a Velocity script inside that document.
        for (Entry<Object, Object> param : properties.entrySet()) {
            sb.append("$xwiki.xWiki.config.setProperty('").append(param.getKey()).append("', '").append(
                param.getValue()).append("')").append('\n');
        }
        editInWikiEditor("Test", "XWikiConfigurationPageForTest", "xwiki/1.0");
        setFieldValue("content", sb.toString());
        // We can execute the script in preview mode. Thus we don't need to save the document.
        clickEditPreview();
    }
}
