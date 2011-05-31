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
package org.xwiki.test.wysiwyg.framework;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.Wait;

/**
 * All XWiki WYSIWYG tests must extend this class.
 * 
 * @version $Id$
 */
public class AbstractWysiwygTestCase extends AbstractXWikiTestCase
{
    private static final String WYSIWYG_LOCATOR_FOR_KEY_EVENTS =
        "document.getElementsByTagName('iframe')[0].contentWindow.document.documentElement";

    private static final String WYSIWYG_LOCATOR_FOR_WYSIWYG_TAB = "//div[@role='tab'][@tabIndex=0]/div[.='WYSIWYG']";

    private static final String WYSIWYG_LOCATOR_FOR_SOURCE_TAB = "//div[@role='tab'][@tabIndex=0]/div[.='Source']";

    /**
     * Locates the text area used in the Source tab.
     */
    public static final String WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA = "//textarea[contains(@class, 'xPlainTextEditor')]";

    /**
     * The title of the indent tool bar button. This title is used in XPath locators to access the indent button.
     */
    public static final String TOOLBAR_BUTTON_INDENT_TITLE = "Increase Indent";

    /**
     * The title of the outdent tool bar button. This title is used in XPath locators to access the outdent button.
     */
    public static final String TOOLBAR_BUTTON_OUTDENT_TITLE = "Decrease Indent";

    /**
     * The title of the undo tool bar button.
     */
    public static final String TOOLBAR_BUTTON_UNDO_TITLE = "Undo (Ctrl+Z)";

    /**
     * The title of the redo tool bar button.
     */
    public static final String TOOLBAR_BUTTON_REDO_TITLE = "Redo (Ctrl+Y)";

    /**
     * The locator for the tool bar list box used to change the style of the current selection.
     */
    public static final String TOOLBAR_SELECT_STYLE = "//select[@title=\"Apply Style\"]";

    /**
     * Locates a menu item by its label.
     */
    public static final String MENU_ITEM_BY_LABEL =
        "//td[contains(@class, 'gwt-MenuItem')]/div[@class = 'gwt-MenuItemLabel' and . = '%s']";

    /**
     * {@inheritDoc}
     * 
     * @see AbstractXWikiTestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        login();
        open(this.getClass().getSimpleName(), getName(), "edit", "editor=wysiwyg");
        waitForEditorToLoad();
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractXWikiTestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        // Make sure there is no interference between tests. When tests are executed at high speed it can happen that a
        // test starts before the page used by the previous test unloads and there are cases when we want a test to
        // start before its initial page finishes loading.
        open("about:blank");
        waitPage();
    }

    /**
     * Logs in with the default user for this test case.
     */
    protected void login()
    {
        // Nothing here. Use the default login in the WYSIWYG test setup.
    }

    protected void runScript(String script)
    {
        maybeInitializeJavaScriptApi();
        getSelenium().runScript(script);
    }

    protected String getEval(String script)
    {
        maybeInitializeJavaScriptApi();
        return getSelenium().getEval(script);
    }

    /**
     * Initializes the {@code XWE} object if it doesn't exist already. This object can be used to quickly access the
     * edited document or the selection from JavaScript code when using {@link #getEval(String)} or
     * {@link #runScript(String)} methods. Note that the {@code XWE} object caches some references and thus needs to be
     * updated whenever those reference become obsolete. To force an update please call
     * {@link #invalidateJavaScriptApi()}.
     */
    protected void maybeInitializeJavaScriptApi()
    {
        // Don't recreate the XWE object if it exists already, to speed up the tests.
        if ("undefined".equals(getSelenium().getEval("typeof window.XWE"))) {
            StringBuffer script = new StringBuffer();
            script.append("var XWE = function() {\n");
            script.append("  var iwnd = window." + getDOMLocator("defaultView") + ";\n");
            script.append("  var idoc = iwnd.document;\n");
            script.append("  return {\n");
            script.append("    document : idoc,\n");
            script.append("    body : idoc.body,\n");
            script.append("    selection : iwnd.getSelection(),\n");
            script.append("    selectAll : function() { idoc.execCommand('selectall', false, null) }\n");
            script.append("  };\n");
            script.append("}();");
            getSelenium().runScript(script.toString());
        }
    }

    /**
     * Invalidates the JavaScript API previously initialized by {@link #maybeInitializeJavaScriptApi()}. This method
     * must be called whenever one of the references cached by the {@code XWE} object becomes obsolete. There are three
     * cases when this can happen:
     * <ul>
     * <li>The rich text area is replaced or renewed. In this case the reference to the edited document becomes
     * obsolete.</li>
     * <li>The rich text area is reloaded. In this case either the reference to the edited document or the reference to
     * the edited document body becomes obsolete.</li>
     * <li>The rich text area is redisplayed. In this case the reference to the selection object becomes obsolete.</li>
     * </ul>
     */
    protected void invalidateJavaScriptApi()
    {
        getSelenium().runScript("window.XWE = undefined");
    }

    /**
     * Sets the content of the rich text area.
     * 
     * @param html the new content of the rich text area
     */
    public void setContent(String html)
    {
        runScript("XWE.body.innerHTML = '" + html + "';");
    }

    /**
     * @return the content of the rich text area
     */
    public String getContent()
    {
        return getEval("window.XWE.body.innerHTML");
    }

    /**
     * Resets the content of the rich text area by selecting all the text like CTRL+A and deleting it using Backspace.
     */
    public void resetContent()
    {
        // We try to mimic as much as possible the user behavior.
        // First, we select all the content.
        selectAllContent();
        // Delete the selected content.
        typeBackspace();
        // We select again all the content. In Firefox, the selection will include the annoying br tag. Further typing
        // will overwrite it. See XWIKI-2732.
        selectAllContent();
    }

    public void selectAllContent()
    {
        runScript("XWE.selectAll();");
        triggerToolbarUpdate();
    }

    private void selectElement(String tagName, int occurence, boolean includeElement)
    {
        String locator = getDOMLocator("getElementsByTagName('" + tagName + "')[" + (occurence - 1) + "]");
        if (includeElement) {
            selectNode(locator);
        } else {
            selectNodeContents(locator);
        }
    }

    /**
     * Select the nth element of the given tagName (p, div, etc), including the element himself. This method will throw
     * a Javascript Exception (which are reported) if the element does not exist. Example : selectElement("p", 2);
     * -------------------
     * <p>
     * first paragraph
     * </p>
     * <ul>
     * ...
     * </ul>
     * [
     * <p>
     * second paragraph
     * </p>
     * ]
     * <p>
     * third paragraph
     * </p>
     * -------------------
     * 
     * @param tagName tagName (p, div, etc) to look for
     * @param occurence number of the occurence to select (from 1 to n).
     */
    public void selectElement(String tagName, int occurence)
    {
        selectElement(tagName, occurence, true);
    }

    /**
     * Select the content of the nth element of the given tagName (p, div, etc). This method will throw a Javascript
     * Exception (which are reported) if the element does not exist. Example : selectElement("p", 2);
     * -------------------
     * <p>
     * first paragraph
     * </p>
     * <ul>
     * ...
     * </ul>
     * <p>
     * [second paragraph]
     * </p>
     * <p>
     * third paragraph
     * </p>
     * -------------------
     * 
     * @param tagName tagName (p, div, etc) to look for
     * @param occurence number of the occurence to select (from 1 to n).
     */
    public void selectElementContent(String tagName, int occurence)
    {
        selectElement(tagName, occurence, false);
    }

    public void typeText(String text)
    {
        // We have to simulate each keyPress so that the RTE get them well.
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char tChar = chars[i];
            typeKey(Character.toString(tChar), true);
        }
    }

    public void typeTextThenEnter(String text)
    {
        typeText(text);
        typeEnter();
    }

    /**
     * Presses the specified key for the given number of times.
     * 
     * @param key the key to be pressed
     * @param fireKeyPress {@code true} if the specified key should generate a key press event, {@code false} otherwise.
     *            Normally only printable keys generate a key press event.
     * @param count the number of times to press the specified key
     * @param hold {@code false} if the key should be released after each key press, {@code true} if it should be hold
     *            down and released just at the end
     * @param locator the locator for type keys
     */
    public void typeKey(String key, boolean fireKeyPress, int count, boolean hold, String locator)
    {
        for (int i = 0; i < count; i++) {
            getSelenium().keyDown(locator, key);
            if (fireKeyPress) {
                getSelenium().keyPress(locator, key);
            }
            if (!hold) {
                getSelenium().keyUp(locator, key);
            }
        }
        if (hold && count > 0) {
            getSelenium().keyUp(locator, key);
        }
    }

    /**
     * Presses the specified key for the given number of times in WYSIWYG source editor.
     * 
     * @param key the key to be pressed
     * @param fireKeyPress {@code true} if the specified key should generate a key press event, {@code false} otherwise.
     *            Normally only printable keys generate a key press event.
     * @param count the number of times to press the specified key
     * @param hold {@code false} if the key should be released after each key press, {@code true} if it should be hold
     *            down and released just at the end
     */

    public void typeKeyInSource(String key, boolean fireKeyPress, int count, boolean hold)
    {
        typeKey(key, fireKeyPress, count, hold, WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA);
    }

    /**
     * Presses the specified key for the given number of times in WYSIWYG rich text editor.
     * 
     * @param key the key to be pressed
     * @param fireKeyPress {@code true} if the specified key should generate a key press event, {@code false} otherwise.
     *            Normally only printable keys generate a key press event.
     * @param count the number of times to press the specified key
     * @param hold {@code false} if the key should be released after each key press, {@code true} if it should be hold
     *            down and released just at the end
     */
    public void typeKey(String key, boolean fireKeyPress, int count, boolean hold)
    {
        typeKey(key, fireKeyPress, count, hold, WYSIWYG_LOCATOR_FOR_KEY_EVENTS);
    }

    /**
     * Presses the specified key.
     * 
     * @param key the key to be pressed
     * @param fireKeyPress {@code true} to fire a KeyPress event, {@code false} otherwise. Normally only printable keys
     *            generate a key press event.
     */
    public void typeKey(String key, boolean fireKeyPress)
    {
        typeKey(key, fireKeyPress, 1, false);
    }

    public void typeEnter()
    {
        typeEnter(1);
    }

    public void typeEnter(int nb)
    {
        typeKey("\\13", true, nb, false);
    }

    public void typeShiftEnter()
    {
        getSelenium().shiftKeyDown();
        typeEnter();
        getSelenium().shiftKeyUp();
    }

    public void typeControlEnter()
    {
        getSelenium().controlKeyDown();
        typeEnter();
        getSelenium().controlKeyUp();
    }

    public void typeMetaEnter()
    {
        getSelenium().metaKeyDown();
        typeEnter();
        getSelenium().metaKeyUp();
    }

    public void typeBackspace()
    {
        typeBackspace(1);
    }

    public void typeBackspace(int count)
    {
        typeBackspace(count, false);
    }

    public void typeBackspace(int count, boolean hold)
    {
        // Although Backspace is not a printable key, it affects the displayed text so we must fire KeyPress event too.
        typeKey("\\8", true, count, hold);
    }

    public void typeLeftArrow()
    {
        typeKey("\\37", false);
    }

    public void typeUpArrow()
    {
        typeKey("\\38", false);
    }

    public void typeRightArrow()
    {
        typeKey("\\39", false);
    }

    public void typeDownArrow()
    {
        typeKey("\\40", false);
    }

    public void typeDelete()
    {
        typeDelete(1);
    }

    public void typeDelete(int count)
    {
        typeDelete(count, false);
    }

    public void typeDelete(int count, boolean hold)
    {
        // Although Delete is not a printable key, it affects the displayed text so we must fire KeyPress event too.
        typeKey("\\46", true, count, hold);
    }

    public void typeTab()
    {
        typeTab(1);
    }

    public void typeTab(int count)
    {
        typeKey("\\9", true, count, false);
    }

    public void typeShiftTab()
    {
        getSelenium().shiftKeyDown();
        typeTab();
        getSelenium().shiftKeyUp();
    }

    public void typeShiftTab(int count)
    {
        for (int i = 0; i < count; i++) {
            typeShiftTab();
        }
    }

    public void clickUnorderedListButton()
    {
        pushToolBarButton("Bullets On/Off");
    }

    public void clickOrderedListButton()
    {
        pushToolBarButton("Numbering On/Off");
    }

    public void clickIndentButton()
    {
        pushToolBarButton(TOOLBAR_BUTTON_INDENT_TITLE);
    }

    public boolean isIndentButtonEnabled()
    {
        return isPushButtonEnabled(TOOLBAR_BUTTON_INDENT_TITLE);
    }

    public void clickOutdentButton()
    {
        pushToolBarButton(TOOLBAR_BUTTON_OUTDENT_TITLE);
    }

    public boolean isOutdentButtonEnabled()
    {
        return isPushButtonEnabled(TOOLBAR_BUTTON_OUTDENT_TITLE);
    }

    public void clickBoldButton()
    {
        pushToolBarButton("Bold (Ctrl+B)");
    }

    public void clickItalicsButton()
    {
        pushToolBarButton("Italic (Ctrl+I)");
    }

    public void clickUnderlineButton()
    {
        pushToolBarButton("Underline (Ctrl+U)");
    }

    public void clickStrikethroughButton()
    {
        pushToolBarButton("Strikethrough");
    }

    public void clickHRButton()
    {
        pushToolBarButton("Insert Horizontal Ruler");
    }

    public void clickSubscriptButton()
    {
        pushToolBarButton("Subscript");
    }

    public void clickSuperscriptButton()
    {
        pushToolBarButton("Superscript");
    }

    public void clickUndoButton()
    {
        pushToolBarButton(TOOLBAR_BUTTON_UNDO_TITLE);
    }

    public void clickUndoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickUndoButton();
        }
    }

    public void clickRedoButton()
    {
        pushToolBarButton(TOOLBAR_BUTTON_REDO_TITLE);
    }

    public void clickRedoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickRedoButton();
        }
    }

    public void clickSymbolButton()
    {
        pushToolBarButton("Insert Custom Character");
    }

    public void clickOfficeImporterButton()
    {
        pushToolBarButton("Import Office Content");
    }

    public void clickBackToEdit()
    {
        submit("//input[@type = 'submit' and @value = 'Back To Edit']");
        waitForEditorToLoad();
    }

    public void applyStyle(final String style)
    {
        // Wait until the given style is not selected (because the tool bar might not be updated).
        new Wait()
        {
            public boolean until()
            {
                return !getSelenium().isSomethingSelected(TOOLBAR_SELECT_STYLE)
                    || !style.equals(getSelenium().getSelectedLabel(TOOLBAR_SELECT_STYLE));
            }
        }.wait("The specified style, '" + style + "', is already applied!");
        getSelenium().select(TOOLBAR_SELECT_STYLE, style);
    }

    /**
     * Waits for the specified style to be detected.
     * 
     * @param style the expected style
     */
    public void waitForStyleDetected(final String style)
    {
        new Wait()
        {
            public boolean until()
            {
                return Integer.valueOf(getSelenium().getSelectedIndex(TOOLBAR_SELECT_STYLE)) >= 0
                    && style.equals(getSelenium().getSelectedLabel(TOOLBAR_SELECT_STYLE));
            }
        }.wait("The specified style, '" + style + "', wasn't detected!");
    }

    public void applyStylePlainText()
    {
        applyStyle("Plain text");
    }

    public void applyStyleTitle1()
    {
        applyStyle("Title 1");
    }

    public void applyStyleTitle2()
    {
        applyStyle("Title 2");
    }

    public void applyStyleTitle3()
    {
        applyStyle("Title 3");
    }

    public void applyStyleTitle4()
    {
        applyStyle("Title 4");
    }

    public void applyStyleTitle5()
    {
        applyStyle("Title 5");
    }

    public void applyStyleTitle6()
    {
        applyStyle("Title 6");
    }

    public void pushButton(String locator)
    {
        // Can't use : selenium.click(locator);
        // A GWT PushButton is not a standard HTML <input type="submit" ...> or a <button ...>
        // rather it is a styled button constructed from DIV and other HTML tags.
        // Source :
        // http://www.blackpepper.co.uk/black-pepper-blog/Simulating-clicks-on-GWT-push-buttons-with-Selenium-RC.html
        getSelenium().mouseOver(locator);
        getSelenium().mouseDown(locator);
        getSelenium().mouseUp(locator);
        getSelenium().mouseOut(locator);
    }

    /**
     * Pushes the tool bar button with the specified title.
     * 
     * @param title the title of the tool bar button to be pushed
     */
    public void pushToolBarButton(String title)
    {
        pushButton("//div[@title='" + title + "']");
    }

    public void clickButtonWithText(String buttonText)
    {
        getSelenium().click("//button[. = \"" + buttonText + "\"]");
    }

    /**
     * Clicks on the menu item with the specified label.
     * 
     * @param menuLabel a {@link String} representing the label of a menu item
     */
    public void clickMenu(String menuLabel)
    {
        String selector = String.format(MENU_ITEM_BY_LABEL, menuLabel);
        // We select the menu item first.
        getSelenium().mouseOver(selector);
        // And then we click on it.
        getSelenium().click(selector);
    }

    /**
     * Waits for the specified menu to be present.
     * 
     * @param menuLabel the menu label
     */
    public void waitForMenu(String menuLabel)
    {
        waitForElement(String.format(MENU_ITEM_BY_LABEL, menuLabel));
    }

    /**
     * Closes the menu containing the specified menu item by pressing the escape key.
     * 
     * @param menuLabel a menu item from the menu to be closed
     */
    public void closeMenuContaining(String menuLabel)
    {
        getSelenium().keyDown(String.format(MENU_ITEM_BY_LABEL, menuLabel), "\\27");
    }

    /**
     * Switch the WYSIWYG editor by clicking on the "WYSIWYG" tab item and waits for the rich text area to be
     * initialized.
     */
    public void switchToWysiwyg()
    {
        switchToWysiwyg(true);
    }

    /**
     * Switch the WYSIWYG editor by clicking on the "WYSIWYG" tab item.
     * 
     * @param wait {@code true} to wait for the rich text area to be initialized, {@code false} otherwise
     */
    public void switchToWysiwyg(boolean wait)
    {
        getSelenium().click(WYSIWYG_LOCATOR_FOR_WYSIWYG_TAB);
        if (wait) {
            new Wait()
            {
                public boolean until()
                {
                    return !getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA);
                }
            }.wait("Source text area is still editable!");
        }
        // The rich text area is redisplayed (and possibly reloaded) so we have to invalidate the JavaScript API.
        invalidateJavaScriptApi();
    }

    /**
     * Switch the Source editor by clicking on the "Source" tab item and waits for the plain text area to be
     * initialized.
     */
    public void switchToSource()
    {
        switchToSource(true);
    }

    /**
     * Switch the Source editor by clicking on the "Source" tab item.
     * 
     * @param wait {@code true} to wait for the plain text area to be initialized, {@code false} otherwise
     */
    public void switchToSource(boolean wait)
    {
        getSelenium().click(WYSIWYG_LOCATOR_FOR_SOURCE_TAB);
        if (wait) {
            new Wait()
            {
                public boolean until()
                {
                    return getSelenium().isEditable(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA);
                }
            }.wait("Source text area is not editable!");
            getSelenium().fireEvent(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, "focus");
        }
    }

    /**
     * Types the specified text in the input specified by its title.
     * 
     * @param inputTitle the {@code title} attribute of the {@code} input element to type in
     * @param text the text to type in the input
     */
    public void typeInInput(String inputTitle, String text)
    {
        getSelenium().type("//input[@title=\"" + inputTitle + "\"]", text);
    }

    /**
     * @param inputTitle the title of the input whose value to return.
     * @return the value of an input specified by its title.
     */
    public String getInputValue(String inputTitle)
    {
        return getSelenium().getValue("//input[@title=\"" + inputTitle + "\"]");
    }

    public boolean isPushButtonEnabled(String pushButtonTitle)
    {
        return getSelenium().isElementPresent(
            "//div[@title='" + pushButtonTitle + "' and @class='gwt-PushButton gwt-PushButton-up']");
    }

    /**
     * @param toggleButtonTitle the tool tip of a toggle button from the WYSIWYG tool bar
     * @return {@code true} if the specified toggle button is enabled, {@code false} otherwise
     */
    public boolean isToggleButtonEnabled(String toggleButtonTitle)
    {
        return getSelenium().isElementPresent(
            "//div[@title='" + toggleButtonTitle
                + "' and contains(@class, 'gwt-ToggleButton') and not(contains(@class, '-disabled'))]");
    }

    /**
     * Waits for the specified push button to have the specified state i.e. enabled or disabled.
     * 
     * @param pushButtonTitle identifies the button to wait for
     * @param enabled {@code true} to wait for the specified button to become enabled, {@code false} to wait for it to
     *            become disabled
     */
    public void waitForPushButton(final String pushButtonTitle, final boolean enabled)
    {
        new Wait()
        {
            public boolean until()
            {
                return enabled == isPushButtonEnabled(pushButtonTitle);
            }
        }.wait(pushButtonTitle + " button is not " + (enabled ? "enabled" : "disabled") + "!");
    }

    /**
     * Waits for the specified toggle button be enabled or disabled, based on the given state parameter.
     * 
     * @param toggleButtonTitle identifies the button to wait for
     * @param enabled {@code true} to wait for the specified toggle button to become enabled, {@code false} to wait for
     *            it to become disabled
     */
    public void waitForToggleButton(final String toggleButtonTitle, final boolean enabled)
    {
        new Wait()
        {
            public boolean until()
            {
                return enabled == isToggleButtonEnabled(toggleButtonTitle);
            }
        }.wait(toggleButtonTitle + " button is not " + (enabled ? "enabled" : "disabled") + "!");
    }

    /**
     * Waits until the specified toggle button has the given state. This method is useful to wait until a toggle button
     * from the tool bar is updated.
     * 
     * @param toggleButtonTitle the tool tip of a toggle button
     * @param down {@code true} to wait until the specified toggle button is down, {@code false} to wait until it is up
     */
    public void waitForToggleButtonState(final String toggleButtonTitle, final boolean down)
    {
        new Wait()
        {
            public boolean until()
            {
                return down == isToggleButtonDown(toggleButtonTitle);
            }
        }.wait("The state of the '" + toggleButtonTitle + "' toggle button didn't change!");
    }

    /**
     * @param toggleButtonTitle the tool tip of a toggle button
     * @return {@code true} if the specified toggle button is down, {@code false} otherwise
     */
    public boolean isToggleButtonDown(String toggleButtonTitle)
    {
        return getSelenium().isElementPresent(
            "//div[@title='" + toggleButtonTitle + "' and @class='gwt-ToggleButton gwt-ToggleButton-down']");
    }

    /**
     * Checks if a menu item is enabled or disabled. Menu items have {@code gwt-MenuItem} CSS class. Disabled menu items
     * have and additional {@code gwt-MenuItem-disabled} CSS class.
     * 
     * @param menuLabel a {@link String} representing the label of a menu item
     * @return {@code true} if the menu with the specified label is enabled, {@code false} otherwise
     */
    public boolean isMenuEnabled(String menuLabel)
    {
        return getSelenium().isElementPresent(
            "//td[contains(@class, 'gwt-MenuItem') and not(contains(@class, 'gwt-MenuItem-disabled'))]"
                + "/div[@class = 'gwt-MenuItemLabel' and . = '" + menuLabel + "']");
    }

    /**
     * Asserts that the rich text area has the expected inner HTML.
     * 
     * @param expectedHTML the expected inner HTML of the rich text area
     */
    public void assertContent(String expectedHTML)
    {
        assertEquals(expectedHTML, getContent());
    }

    /**
     * {@inheritDoc}
     * 
     * @see AbstractXWikiTestCase#clickEditPreview()
     */
    public void clickEditPreview()
    {
        updateRichTextAreaFormField();
        super.clickEditPreview();
    }

    /**
     * The rich text area is not a ordinary HTML input. To be able to submit its value we use a hidden HTML input which
     * is updated each time the rich text area looses the focus. Let's update this hidden input by clicking outside of
     * the rich text area.
     */
    public void updateRichTextAreaFormField()
    {
        blurRichTextArea();
        focusRichTextArea();
    }

    /**
     * Places the caret in the specified container, at the specified offset.
     * 
     * @param containerJSLocator the JavaScript code used to access the container node
     * @param offset the offset within the container node
     */
    public void moveCaret(String containerJSLocator, int offset)
    {
        StringBuffer script = new StringBuffer();
        script.append("var range = XWE.document.createRange();\n");
        script.append("range.setStart(");
        script.append(containerJSLocator);
        script.append(", ");
        script.append(offset);
        script.append(");\n");
        script.append("range.collapse(true);\n");
        script.append("XWE.selection.removeAllRanges();\n");
        script.append("XWE.selection.addRange(range);");
        runScript(script.toString());
        triggerToolbarUpdate();
    }

    /**
     * Selects the content between the specified points in the DOM tree.
     * 
     * @param startContainerJSLocator the node containing the start of the selection
     * @param startOffset the offset within the start container where the selection starts
     * @param endContainerJSLocator the node containing the end of the selection
     * @param endOffset the offset within the end container where the selection ends
     */
    public void select(String startContainerJSLocator, int startOffset, String endContainerJSLocator, int endOffset)
    {
        StringBuffer script = new StringBuffer();
        script.append("var range = XWE.document.createRange();\n");
        script.append("range.setStart(");
        script.append(startContainerJSLocator);
        script.append(", ");
        script.append(startOffset);
        script.append(");\n");
        script.append("range.setEnd(");
        script.append(endContainerJSLocator);
        script.append(", ");
        script.append(endOffset);
        script.append(");\n");
        script.append("XWE.selection.removeAllRanges();\n");
        script.append("XWE.selection.addRange(range);");
        runScript(script.toString());
        triggerToolbarUpdate();
    }

    /**
     * Selects the specified DOM node.
     * 
     * @param jsLocator a JavaScript locator for the node to be selected
     */
    public void selectNode(String jsLocator)
    {
        StringBuffer script = new StringBuffer();
        script.append("var range = XWE.document.createRange();\n");
        script.append("range.selectNode(");
        script.append(jsLocator);
        script.append(");\n");
        script.append("XWE.selection.removeAllRanges();\n");
        script.append("XWE.selection.addRange(range);");
        runScript(script.toString());
        triggerToolbarUpdate();
    }

    /**
     * Selects the contents of the specified DOM node.
     * 
     * @param jsLocator a JavaScript locator for the node whose content are to be selected
     */
    public void selectNodeContents(String jsLocator)
    {
        StringBuffer script = new StringBuffer();
        script.append("var range = XWE.document.createRange();\n");
        script.append("range.selectNodeContents(");
        script.append(jsLocator);
        script.append(");\n");
        script.append("XWE.selection.removeAllRanges();\n");
        script.append("XWE.selection.addRange(range);");
        runScript(script.toString());
        triggerToolbarUpdate();
    }

    /**
     * Converts a DOM locator relative to the document edited by the WYSIWYG editor to a DOM locator relative to the
     * document hosting the WYSIWYG editor. The returned locator can be used in Selenium methods like
     * {@link Selenium#click(String)}.
     * 
     * @param domLocator a Selenium DOM locator relative to the document edited with the WYSIWYG editor
     * @return a Selenium DOM locator relative to the document hosting the WYSIWYG editor
     */
    public String getDOMLocator(String domLocator)
    {
        return "document.getElementsByTagName('iframe')[0].contentWindow.document." + domLocator;
    }

    /**
     * Triggers the wysiwyg toolbar update by typing a key. To be used after programatically setting the selection (with
     * {@link AbstractWysiwygTestCase#select(String, int, String, int)} or
     * {@link AbstractWysiwygTestCase#moveCaret(String, int)}): it will not influence the selection but it will cause
     * the toolbar to update according to the new selection.
     */
    protected void triggerToolbarUpdate()
    {
        typeLeftArrow();
    }

    /**
     * Wait for a WYSIWYG dialog to close. The test checks for a {@code div} element with {@code xDialogBox} value of
     * {@code class} to not be present.
     */
    public void waitForDialogToClose()
    {
        waitForCondition("!selenium.isElementPresent('//div[contains(@class, \"xDialogBox\")]')");
    }

    /**
     * Waits until a WYSIWYG modal dialog is fully loaded. While loading, the body of the dialog has the {@code loading}
     * CSS class besides the {@code xDialogBody} one.
     */
    public void waitForDialogToLoad()
    {
        waitForCondition("selenium.isElementPresent('//div[contains(@class, \"xDialogBody\")"
            + " and not(contains(@class, \"loading\"))]')");
    }

    /**
     * Close the dialog by clicking the close icon in the top right.
     */
    public void closeDialog()
    {
        getSelenium().click("//img[contains(@class, \"gwt-Image\") and contains(@class, \"xDialogCloseIcon\")]");
        waitForDialogToClose();
    }

    /**
     * Waits until the WYSIWYG editor detects the bold style on the current selection. The bold style is detected when
     * the associated tool bar button is updated. The update is delayed to increase the typing speed.
     */
    public void waitForBoldDetected(boolean down)
    {
        waitForToggleButtonState("Bold (Ctrl+B)", down);
    }

    /**
     * Waits until the WYSIWYG editor detects the underline style on the current selection. The underline style is
     * detected when the associated tool bar button is updated. The update is delayed to increase the typing speed.
     */
    public void waitForUnderlineDetected(boolean down)
    {
        waitForToggleButtonState("Underline (Ctrl+U)", down);
    }

    /**
     * Asserts that the specified error message exists, and the element passed through its XPath locator is marked as in
     * error.
     * 
     * @param errorMessage the expected error message
     * @param fieldXPathLocator the XPath locator of the field which is in error
     */
    public void assertFieldErrorIsPresent(String errorMessage, String fieldXPathLocator)
    {
        // test that the error field is present through this method because the isVisible stops at first encouter of the
        // matching element and fails if it's not visible. However, multiple matching elements might exist and we're
        // interested in at least one of them visible
        assertTrue(getSelenium().getXpathCount(
            "//*[contains(@class, \"xErrorMsg\") and . = '" + errorMessage + "' and @style='']").intValue() > 0);
        assertElementPresent(fieldXPathLocator + "[contains(@class, 'xErrorField')]");
    }

    /**
     * Asserts that the specified error message does not exist and that the field passed through the XPath locator is
     * not in error. Note that this function checks that the passed field is present, but without an error marker.
     * 
     * @param errorMessage the error message
     * @param fieldXPathLocator the XPath locator of the field to check that it's not in error
     */
    public void assertFieldErrorIsNotPresent(String errorMessage, String fieldXPathLocator)
    {
        assertFalse(getSelenium().isVisible("//*[contains(@class, \"xErrorMsg\") and . = \"" + errorMessage + "\"]"));
        assertTrue(isElementPresent(fieldXPathLocator + "[not(contains(@class, 'xFieldError'))]"));
    }

    /**
     * Asserts that no error message or field marked as in error is present.
     */
    public void assertFieldErrorIsNotPresent()
    {
        // no error is visible
        assertFalse(getSelenium().isVisible("//*[contains(@class, \"xErrorMsg\")]"));
        // no field with error markers should be present
        assertFalse(isElementPresent("//*[contains(@class, 'xFieldError')]"));
    }

    /**
     * Simulates a focus event on the rich text area. We don't use the focus method because it fails to notify our
     * listeners when the browser window is not focused, preventing us from running the tests in background.
     * <p>
     * NOTE: The initial range CAN differ when the browser window is focused from when it isn't! Make sure you place the
     * caret where you want it to be at the beginning of you test and after switching back to WYSIWYG editor.
     */
    protected void focusRichTextArea()
    {
        // The focus event doesn't propagate from an inner element to the host window, meaning we are forced to trigger
        // the focus event on the window object.
        focus(getDOMLocator("defaultView"));
    }

    /**
     * Simulates a blur event on the rich text area. We don't use the blur method because it fails to notify our
     * listeners when the browser window is not focused, preventing us from running the tests in background.
     */
    protected void blurRichTextArea()
    {
        blur(getDOMLocator("defaultView"));
    }

    /**
     * Inserts a table in place of the current selection or at the caret position, using the default table settings.
     */
    protected void insertTable()
    {
        openInsertTableDialog();
        getSelenium().click("//button[text()=\"Insert Table\"]");
    }

    /**
     * Opens the insert table dialog.
     */
    protected void openInsertTableDialog()
    {
        clickMenu("Table");
        clickMenu("Insert Table...");
        waitForDialogToLoad();
    }

    /**
     * Focuses the specified element by triggering a focus event instead of calling its {@code focus()} method. This
     * method manages to focus the specified element even if the browser window doesn't have the focus which happens
     * when the tests are ran in background.
     * 
     * @param locator identifies the element to focus
     */
    protected void focus(String locator)
    {
        getSelenium().fireEvent(locator, "focus");
    }

    /**
     * Blurs the specified element by triggering a blur event instead of calling its {@code blur()} method. This method
     * manages to blur the specified element even if the browser window doesn't have the focus which happens when the
     * tests are ran in background.
     * 
     * @param locator identifies the element to blur
     */
    protected void blur(String locator)
    {
        getSelenium().fireEvent(locator, "blur");
    }

    /**
     * Use this method to detect if the tests are ran in background.<br/>
     * NOTE: This method works <strong>only</strong> in edit mode (both Wiki and WYSIWYG) because it uses the title
     * input to detect if the browser window is focused or not.
     * 
     * @return {@code true} if the browser window in which the tests are ran is focused, {@code false} otherwise
     */
    protected boolean isBrowserWindowFocused()
    {
        String titleLocator = "xwikidoctitleinput";
        // Focus the title input so that it catches the native key press.
        focus(titleLocator);
        // Save the current value to be able to check if it changes with the key press and to be able to restore it.
        String beforeValue = getSelenium().getValue(titleLocator);
        // Make sure the text is not selected, otherwise it might be overwritten by the next key press.
        getSelenium().setCursorPosition(titleLocator, "0");
        // Type 0 (zero).
        getSelenium().keyPressNative("48");
        // Check if the input value has changed.
        boolean focused = true;
        if (beforeValue.equals(getSelenium().getValue(titleLocator))) {
            focused = false;
        }
        // Restore the input value.
        getSelenium().type(titleLocator, beforeValue);
        // Return the result.
        return focused;
    }

    /**
     * @return the text from the source text area
     */
    protected String getSourceText()
    {
        // Note: We could use getSelenium().getValue() here. However getValue() is stripping spaces
        // and some of our tests verify that there are leading spaces/empty lines.
        return getSelenium().getEval(
            "getInputValue(selenium.browserbot.findElement(\"" + WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA + "\"))");
    }

    /**
     * Sets the value of the source text area.
     * 
     * @param sourceText the new value for the source text area
     */
    protected void setSourceText(String sourceText)
    {
        getSelenium().type(WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA, sourceText);
    }

    /**
     * Asserts that the source text area has the given value.
     * 
     * @param expectedSourceText the expected value of the source text area
     */
    protected void assertSourceText(String expectedSourceText)
    {
        assertEquals(expectedSourceText, getSourceText());
    }

    /**
     * Waits for the WYSIWYG editor to load.
     */
    protected void waitForEditorToLoad()
    {
        final String sourceTabSelected = "//div[@class = 'gwt-TabBarItem gwt-TabBarItem-selected']/div[. = 'Source']";
        final String richTextArea = "//div[@class = 'xRichTextEditor']";
        final String richTextAreaLoader = richTextArea + "//div[@class = 'loading']";
        new Wait()
        {
            public boolean until()
            {
                // Either the source tab is present and selected and the plain text area can be edited or the rich text
                // area is not loading (with or without tabs).
                return (getSelenium().isElementPresent(sourceTabSelected) && getSelenium().isEditable(
                    WYSIWYG_LOCATOR_FOR_SOURCE_TEXTAREA))
                    || (getSelenium().isElementPresent(richTextArea) && !getSelenium().isElementPresent(
                        richTextAreaLoader));
            }
        }.wait("The WYSIWYG editor failed to load in a decent amount of time!");
        // The rich text area has been (re)loaded so we have to invalidate the JavaScript API.
        invalidateJavaScriptApi();
    }

    /**
     * Switches to full screen editing mode.
     */
    protected void clickEditInFullScreen()
    {
        getSelenium().click("//img[@title = 'Maximize']");
        waitForElement("//div[@class = 'fullScreenWrapper']");
    }

    /**
     * Exists full screen editing mode.
     */
    protected void clickExitFullScreen()
    {
        getSelenium().click("//input[@value = 'Exit full screen']");
        waitForElementNotPresent("//div[@class = 'fullScreenWrapper']");
    }

    /**
     * Waits until the specified element is not present.
     * 
     * @param locator specifies the element to wait for
     */
    protected void waitForElementNotPresent(final String locator)
    {
        new Wait()
        {
            public boolean until()
            {
                return !isElementPresent(locator);
            }
        }.wait("The specified element, " + locator + ", is still present!");
    }

    /**
     * Creates a new space with the specified name.
     * 
     * @param spaceName the name of the new space to create
     */
    public void createSpace(String spaceName)
    {
        clickLinkWithLocator("tmCreateSpace");
        getSelenium().type("space", spaceName);
        clickLinkWithLocator("//input[@value='Create']");
        clickEditSaveAndView();
    }

    /**
     * Creates a page in the specified space, with the specified name.
     * <p>
     * NOTE: We overwrite the method from the base class because it creates the new page using the URL and thus requires
     * special characters in space and page name to be escaped. We use instead the create page form.
     * 
     * @param spaceName the name of the space where to create the page
     * @param pageName the name of the page to create
     * @param content the content of the new page
     * @see AbstractXWikiTestCase#createPage(String, String, String)
     */
    public void createPage(String spaceName, String pageName, String content)
    {
        clickLinkWithLocator("tmCreatePage");
        getSelenium().type("space", spaceName);
        getSelenium().type("page", pageName);
        clickLinkWithLocator("//input[@value='Create']");
        String location = getSelenium().getLocation();
        if (location.endsWith("?xpage=docalreadyexists")) {
            open(location.substring(0, location.length() - 23));
            clickEditPageInWysiwyg();
        }
        waitForEditorToLoad();
        switchToSource();
        setSourceText(content);
        clickEditSaveAndView();
    }

    /**
     * Selects the rich text area frame. Selectors are relative to the edited document after calling this method.
     */
    public void selectRichTextAreaFrame()
    {
        getSelenium().selectFrame("document.getElementsByClassName('gwt-RichTextArea')[0]");
    }

    /**
     * Selects the top frame.
     */
    public void selectTopFrame()
    {
        getSelenium().selectFrame("relative=top");
    }
}
