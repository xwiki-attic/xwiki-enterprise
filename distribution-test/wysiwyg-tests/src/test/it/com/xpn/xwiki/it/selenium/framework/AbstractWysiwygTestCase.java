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

import junit.framework.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * All XWiki WYSIWYG tests must extend this class.
 *
 * @version $Id$
 */
public class AbstractWysiwygTestCase extends AbstractXWikiTestCase
{
    private static final String WYSIWYG_LOCATOR_FOR_KEY_EVENTS =
        "document.getElementsByTagName('iframe')[0].contentWindow.document.documentElement";

    private static final String WYSIWYG_LOCATOR_FOR_HTML_CONTENT = "content";

    protected void setUp() throws Exception
    {
        super.setUp();

        loginAsAdmin();
        open("Main", "WysiwygTest");
        clickLinkWithText("WYSIWYG");

        // Switch the document to xwiki/2.0 syntax if needed.
        if (!getSelenium().getValue("syntaxId").equals("xwiki/2.0")) {
            setFieldValue("syntaxId", "xwiki/2.0");
            if (getSelenium().isConfirmationPresent()) {
                assertEquals("Do you want to also convert the document's content and objects to the selected syntax?"
                    + " Choosing 'cancel' will reset the syntax to the previous one and do nothing."
                    + " Note that if you choose 'ok' you will loose modifications and this will save the document"
                    + " automatically, you can cancel this modification by going to the document history interface"
                    + " and revert the last version.", getSelenium().getConfirmation());
            }
            // In order to change the syntax the page has to be reloaded.
            waitPage();
        }

        // Focus the rich text area in order to enter design mode.
        focusRichTextArea();

        // Reset editor's content.
        resetContent();
    }

    protected void runScript(String script)
    {
        initJavascriptEnv();
        getSelenium().runScript(script);
    }

    protected String getEval(String script)
    {
        initJavascriptEnv();
        return getSelenium().getEval(script);
    }

    protected void initJavascriptEnv()
    {
        if (!getSelenium().getEval("typeof window.XWE").equals("object")) {
            getSelenium().runScript("var XWE = function() {\n" +
                "  var iframe = window.document.getElementsByTagName('iframe')[0];\n" +
                "  var iwnd = iframe.contentWindow;\n" +
                "  var idoc = iwnd.document;\n" +
                "  return {\n" +
                "    document : idoc,\n" +
                "    window : iwnd,\n" +
                "    rootNode : idoc.documentElement,\n" +
                "    body : idoc.body,\n" +
                "    innerHTML : idoc.body.innerHTML,\t\t\n" +
                "    selection : iwnd.getSelection(),\n" +
                "    getRange : function() { \n" +
                "      if (iwnd.getSelection().rangeCount > 0) {\n" +
                "        return iwnd.getSelection().getRangeAt(0);\n" +
                "      } else {\n" +
                "        var range = idoc.createRange();\n" +
                "        range.selectNodeContents(idoc.body);\n" +
                "        iwnd.getSelection().addRange(range);\t\t\t\t\n" +
                "        return range;\n" +
                "      }\n" +
                "    },\n" +
                "    removeAllRanges : function() { iwnd.getSelection().removeAllRanges(); },\n" +
                "    selectAll : function() { idoc.execCommand('selectall', false, null) },\n"+
                "  };\n" +
                "}();");
        }
    }

    public void setContent(String html)
    {
        runScript("XWE.body.innerHTML = '" + html + "';");
        updateRichTextAreaFormField();
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
     * Select the nth element of the given tagName (p, div, etc), including the element himself.
     * This method will throw a Javascript Exception (which are reported) if the element does not exist.
     * Example :
     *
     * selectElement("p", 2);
     * -------------------
     * <p>first paragraph</p>
     * <ul>...</ul>
     * [<p>second paragraph</p>]
     * <p>third
     * paragraph</p>
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
     * Select the content of the nth element of the given tagName (p, div, etc).
     * This method will throw a Javascript Exception (which are reported) if the element does not exist.
     * Example :
     *
     * selectElement("p", 2);
     * -------------------
     * <p>first paragraph</p>
     * <ul>...</ul>
     * <p>[second paragraph]</p>
     * <p>third paragraph</p>
     * -------------------
     *
     * @param tagName tagName (p, div, etc) to look for
     * @param occurence number of the occurence to select (from 1 to n).
     */
    public void selectElementContent(String tagName, int occurence)
    {
        selectElement(tagName, occurence, false);
    }

    public void resetSelection()
    {
        runScript("XWE.removeAllRanges();");
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
     */
    public void typeKey(String key, boolean fireKeyPress, int count, boolean hold)
    {
        for (int i = 0; i < count; i++) {
            getSelenium().keyDown(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
            if (fireKeyPress) {
                getSelenium().keyPress(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
            }
            if (!hold) {
                getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
            }
        }
        if (hold && count > 0) {
            getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
        }
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
        pushToolBarButton("Unordered list");
    }

    public void clickOrderedListButton()
    {
        pushToolBarButton("Ordered list");
    }

    public void clickIndentButton()
    {
        pushToolBarButton("Indent");
    }

    public boolean isIndentButtonEnabled()
    {
        return isButtonEnabled("Indent");
    }

    public void clickOutdentButton()
    {
        pushToolBarButton("Outdent");
    }

    public boolean isOutdentButtonEnabled()
    {
        return isButtonEnabled("Outdent");
    }

    public void clickBoldButton()
    {
        pushToolBarButton("Bold (CTRL+B)");
    }

    public void clickItalicsButton()
    {
        pushToolBarButton("Italic (CTRL+I)");
    }

    public void clickUnderlineButton()
    {
        pushToolBarButton("Underline (CTRL+U)");
    }

    public void clickStrikethroughButton()
    {
        pushToolBarButton("Strikethrough");
    }

    public void clickHRButton()
    {
        pushToolBarButton("Insert horizontal ruler");
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
        pushToolBarButton("Undo (CTRL+Z)");
    }

    public void clickUndoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickUndoButton();
        }
    }

    public void clickRedoButton()
    {
        pushToolBarButton("Redo (CTRL+Y)");
    }

    public void clickRedoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickRedoButton();
        }
    }

    public void clickSymbolButton()
    {
        pushToolBarButton("Insert custom character");
    }

    public void clickInsertImageButton()
    {
        pushToolBarButton("Insert or edit image");
    }

    public void clickInsertLinkButton()
    {
        pushToolBarButton("Insert link");
    }

    public void clickUnlinkButton()
    {
        pushToolBarButton("Unlink");
    }

    public void clickOfficeImporterButton()
    {
        pushToolBarButton("Import office content");
    }

    public boolean isUnlinkButtonEnabled()
    {
        return isButtonEnabled("Unlink");
    }

    public boolean isInsertLinkButtonEnabled()
    {
        return isButtonEnabled("Insert link");

    }

    public void clickInsertTableButton()
    {
        pushToolBarButton("Inserts a new table");
    }

    public void clickBackToEdit()
    {
        submit("//button[text()='Back To Edit']");
        focusRichTextArea();
    }

    public void applyStyle(String style)
    {
        getSelenium().select("//select[@title=\"Apply Style\"]", style);
    }

    public void applyStyleInLine()
    {
        applyStyle("Inline");
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

    /**
     * Clicks the button with the specified title.
     * 
     * @param buttonTitle the value of the {@code title} attribute of the {@code button} element to click
     */
    public void clickButtonWithTitle(String buttonTitle)
    {
        getSelenium().click("//button[@title=\"" + buttonTitle + "\"]");
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
        String selector = "//td[contains(@class, 'gwt-MenuItem') and . = '" + menuLabel + "']";
        // We select the menu item first.
        getSelenium().mouseOver(selector);
        // And then we click on it.
        getSelenium().click(selector);
    }

    /**
     * Closes the menu containing the specified menu item by pressing the escape key.
     * 
     * @param menuLabel a menu item from the menu to be closed
     */
    public void closeMenuContaining(String menuLabel)
    {
        getSelenium().keyDown("//td[contains(@class, 'gwt-MenuItem') and . = '" + menuLabel + "']", "\\27");
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

    public boolean isButtonEnabled(String buttonTitle)
    {
        return getSelenium().isElementPresent(
            "//div[@title='" + buttonTitle + "' and @class='gwt-PushButton gwt-PushButton-up']");
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
            "//td[contains(@class, 'gwt-MenuItem') and not(contains(@class, 'gwt-MenuItem-disabled')) and . = '"
                + menuLabel + "']");
    }

    public void assertXHTML(String xhtml)
    {
        updateRichTextAreaFormField();
        Assert.assertEquals(xhtml, getSelenium().getValue(WYSIWYG_LOCATOR_FOR_HTML_CONTENT));
    }

    public void switchToWikiEditor()
    {
        updateRichTextAreaFormField();
        clickLinkWithText("Wiki", true);
    }

    public void switchToWysiwygEditor()
    {
        clickLinkWithText("WYSIWYG", true);
        if (getSelenium().isConfirmationPresent()) {
            assertEquals("Your content contains HTML or special code that might be lost in the WYSIWYG Editor."
                + " Are you sure you want to switch editors?", getSelenium().getConfirmation());
        }
        focusRichTextArea();
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
     * {@inheritDoc}
     * 
     * @see AbstractXWikiTestCase#assertWikiTextGeneratedByWysiwyg(String)
     */
    public void assertWikiTextGeneratedByWysiwyg(String text)
    {
        switchToWikiEditor();
        assertEquals(text, getFieldValue("content"));
    }

    /**
     * Switches to the wiki editor and checks the content of the wiki edit area against the passed expected content. At
     * the end, it the editor is switched back to wysiwyg.
     * 
     * @param expected the expected content of the wiki editor
     */
    public void assertWiki(String expected)
    {
        assertWikiTextGeneratedByWysiwyg(expected);
        switchToWysiwygEditor();
    }

    /**
     * Sets the passed wiki text as the editor content, leaving the wysiwyg editor active. To be used from the wysiwyg
     * editor to set the text as wiki text.
     * 
     * @param wikiText the wiki text to set
     */
    public void setWikiContent(String wikiText)
    {
        switchToWikiEditor();
        setFieldValue("content", wikiText);
        switchToWysiwygEditor();
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
        waitForCondition("selenium.isElementPresent('//div[contains(@class, \"xDialogBox\")"
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
     * @return {@code true} if the WYSIWYG editor detects the bold style on the current selection, {@code false}
     *         otherwise
     */
    public boolean isBoldDetected()
    {
        return isToggleButtonDown("Bold (CTRL+B)");
    }

    /**
     * @return {@code true} if the WYSIWYG editor detects the underline style on the current selection, {@code false}
     *         otherwise
     */
    public boolean isUnderlineDetected()
    {
        return isToggleButtonDown("Underline (CTRL+U)");
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
        // We dont't use getSelenium.focus(locator) because it uses the focus method when the target of the locator has
        // it and in our case the target is a window object which has the focus method. Moreover, the focus event
        // doesn't propagate from an inner element to the host window, meaning we are forced to trigger the focus event
        // on the window object. We haven't found a way to call triggerEvent from the scope of runScript and thus we use
        // getEval.
        getSelenium().getEval("triggerEvent(window." + getDOMLocator("defaultView") + ", 'focus', false);");
        // Wait till the rich text area enters design mode.
        waitForCondition("window." + getDOMLocator("defaultView") + ".getSelection().rangeCount > 0");
        // Update the state of the tool bar buttons.
        triggerToolbarUpdate();
    }

    /**
     * Simulates a blur event on the rich text area. We don't use the blur method because it fails to notify our
     * listeners when the browser window is not focused, preventing us from running the tests in background.
     */
    protected void blurRichTextArea()
    {
        // We haven't found a way to call triggerEvent from the scope of runScript and thus we use getEval.
        getSelenium().getEval("triggerEvent(window." + getDOMLocator("defaultView") + ", 'blur', false);");
    }
}
