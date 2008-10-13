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

/**
 * All XWiki Wysiwyg tests must extend this class.
 *
 * @version $Id: $
 */
public class AbstractWysiwygTestCase extends AbstractXWikiTestCase
{
    private static final String WYSIWYG_LOCATOR_FOR_KEY_EVENTS =
        "document.getElementsByTagName('iframe').item(0).contentDocument.documentElement";

    private static final String WYSIWYG_LOCATOR_FOR_HTML_CONTENT = "content";

    private static final String WYSIWYG_LOCATOR_TO_CLICK_FOR_BLUR_EVENT = "title";

    public static final String WYSIWYG_DEFAULT_CONTENT = "<br>";

    private boolean firstEnterTyped = true;

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();

        // Go to the wysiwyg editor if needed
        if (!getSelenium().getLocation()
            .equals("http://localhost:8080/xwiki/bin/edit/Main/WysiwygTest?editor=wysiwyg"))
        {
            open("Main", "WysiwygTest");
            clickEditPage();
        }

        // Switch the document to xwiki/2.0 syntax if needed
        if (!getSelenium().getValue("syntaxId").equals("xwiki/2.0")) {
            setFieldValue("syntaxId", "xwiki/2.0");
            clickEditSaveAndContinue();
        }

        // Give the focus to the xwiki window
        getSelenium().windowFocus();
        runScript("XWE.focus();");

        // Reset editor's content
        resetContent();

        // See typeEnter for more details
        firstEnterTyped = true;
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
                "  var iframe = window.document.getElementsByTagName('iframe').item(0);\n" +
                "    return {\n" +
                "    document : iframe.contentDocument,\n" +
                "    window : iframe.contentWindow,\n" +
                "    rootNode : iframe.contentDocument.documentElement,\n" +
                "    body : iframe.contentDocument.body,\n" +
                "    innerHTML : iframe.contentDocument.body.innerHTML,\t\t\n" +
                "    selection : iframe.contentWindow.getSelection(),\n" +
                "    focus : function() { iframe.contentWindow.focus() },\t\t\n" +
                "    getRange : function() { \n" +
                "        try {\n" +
                "          var range = iframe.contentDocument.defaultView.getSelection().getRangeAt(0);\n" +
                "        } catch(e) {\n" +
                "          var range = iframe.contentDocument.createRange();   \n" +
                "          iframe.contentDocument.defaultView.getSelection().addRange(range);\t\t\t\t\n" +
                "        }\n" +
                "        return range;\n" +
                "      },\n" +
                "    removeAllRanges : function() { iframe.contentWindow.getSelection().removeAllRanges(); },\n" +
                "    selectAll : function() { iframe.contentWindow.document.execCommand('selectall', false, null) },\n"+
                "  };\n" +
                "}();");
        }
    }

    public void setContent(String html)
    {
        runScript("XWE.body.innerHTML = '" + html + "';");
        // Give the focus to the RTE so that it takes the modification into account.
        runScript("XWE.focus();");
        // We have to click outside of the editor since the "content" input is updated on blur event.
        getSelenium().clickAt(WYSIWYG_LOCATOR_TO_CLICK_FOR_BLUR_EVENT, "0,0");
        // Give the focus back to the RTE
        runScript("XWE.focus();");
    }

    public void resetContent()
    {
        setContent(WYSIWYG_DEFAULT_CONTENT);
    }

    public void selectAllContent()
    {
        runScript("XWE.selectAll();");
    }

    private void selectElement(String tagName, int occurence, boolean includeElement)
    {
        String rangeMethod = "selectNode";

        if (!includeElement) {
            rangeMethod = "selectNodeContents";
        }

        runScript("XWE.focus();" +
            "var elementNumber = " + occurence + ";\n" +
            "var children = XWE.body.childNodes;\n" +
            "var r = XWE.getRange();   \n" +
            "for (var i = 0; i < children.length; i++) {\n" +
            "  if (children[i].tagName == '" + tagName.toUpperCase() + "') {\n" +
            "    if (elementNumber == 1) {\n" +
            "      r." + rangeMethod + "(children[i]);\n" +
            "      XWE.focus();\n" +
            "      break;\n" +
            "    }\n" +
            "    elementNumber--;\n" +
            "  }\n" +
            "}" +
            "if (elementNumber > 1) {\n" +
            "  throw ('There is no element number <" + occurence + "> of type <" + tagName + "> ')\n" +
            "}\n");
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
            keyPress(Character.toString(tChar));
        }
    }

    public void typeTextThenEnter(String text)
    {
        typeText(text);
        typeEnter();
    }

    public void keyPress(String key)
    {
        getSelenium().keyDown(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
        getSelenium().keyPress(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
        getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
    }

    public void specialKeyPress(String key)
    {
        // Can't use keyPress for this events since they aren't catched by GWT RTE's callback (unknown reason).
        getSelenium().keyDown(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
        getSelenium().keyUp(WYSIWYG_LOCATOR_FOR_KEY_EVENTS, key);
    }

    public void typeEnter()
    {
        // I know this looks weird but to obtain a single carriage return we must first use keyDown/KeyUp
        // for all the following ones keyDown/keyPress/KeyUp
        if (firstEnterTyped) {
            specialKeyPress("\\13");
            firstEnterTyped = false;
        } else {
            keyPress("\\13");
        }
    }

    public void typeShiftEnter()
    {
        getSelenium().shiftKeyDown();
        typeEnter();
        getSelenium().shiftKeyUp();
    }

    public void typeBackspace()
    {
        // It would be 'logical' to use specialKeyPress here but for some reason it doesn't work.
        keyPress("\\8");
    }

    public void typeBackspaces(int nb)
    {
        for (; nb > 0; nb--) {
            typeBackspace();
        }
    }

    public void typeLeftArrow()
    {
        specialKeyPress("\\37");
    }

    public void typeUpArrow()
    {
        specialKeyPress("\\38");
    }

    public void typeRightArrow()
    {
        specialKeyPress("\\39");
    }

    public void typeDownArrow()
    {
        specialKeyPress("\\40");
    }

    public void typeDelete()
    {
        specialKeyPress("\\46");
    }

    public void clickUnorderedListButton()
    {
        pushButton("//div[@title='Unordered list']");
    }

    public void clickOrderedListButton()
    {
        pushButton("//div[@title='Ordered list']");
    }

    public void clickIndentButton()
    {
        pushButton("//div[@title='Indent']");
    }

    public void clickOutdentButton()
    {
        pushButton("//div[@title='Outdent']");
    }

    public void clickBoldButton()
    {
        pushButton("//div[@title='Bold (CTRL+B)']");
    }

    public void clickItalicsButton()
    {
        pushButton("//div[@title='Italic (CTRL+I)']");
    }

    public void clickUnderlineButton()
    {
        pushButton("//div[@title='Underline (CTRL+U)']");
    }

    public void clickStrikethroughButton()
    {
        pushButton("//div[@title='Strikethrough']");
    }

    public void clickHRButton()
    {
        pushButton("//div[@title='Insert horizontal ruler']");
    }

    public void clickSubscriptButton()
    {
        pushButton("//div[@title='Subscript']");
    }

    public void clickSuperscriptButton()
    {
        pushButton("//div[@title='Superscript']");
    }

    public void clickUndoButton()
    {
        pushButton("//div[@title='Undo (CTRL+Z)']");
    }

    public void clickRedoButton()
    {
        pushButton("//div[@title='Redo (CTRL+Y)']");
    }

    public void applyStyle(String style)
    {
        getSelenium().select("//select[@title=\"Apply Style\"]", style);
    }

    public void applyStyleNormal()
    {
        applyStyle("Normal");
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
    }

    public void assertXHTML(String xhtml)
    {
        // We have to click outside of the editor since the "content" input is updated on blur event.
        getSelenium().clickAt(WYSIWYG_LOCATOR_TO_CLICK_FOR_BLUR_EVENT, "0,0");
        // System.err.println("\n*********** ASSERTXHTMLDEBUG : " + getSelenium().getValue(WYSIWYG_LOCATOR_FOR_HTML_CONTENT) + "\n");
        Assert.assertEquals(xhtml, getSelenium().getValue(WYSIWYG_LOCATOR_FOR_HTML_CONTENT));
        // Give the focus back to the RTE
        runScript("XWE.focus();");
    }
}
