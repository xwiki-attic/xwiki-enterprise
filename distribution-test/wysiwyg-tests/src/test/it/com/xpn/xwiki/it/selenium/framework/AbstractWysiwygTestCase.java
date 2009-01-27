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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.apache.commons.logging.LogFactory;

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

    private static final String XWINDOWFOCUS_BINARY = "/home/maven/xwindowfocus";

    private class StreamRedirector extends Thread
    {
        private InputStream is;

        private OutputStream os;

        StreamRedirector(InputStream in, OutputStream out)
        {
            is = in;
            os = out;
        }

        public void run()
        {
            byte[] buf = new byte[512];
            int n;

            try {
                while (true) {
                    n = is.read(buf);
                    if (n == 0) {
                        return;
                    }
                    os.write(buf, 0, n);
                }
            } catch (Exception e) {
                LogFactory.getLog(StreamRedirector.class).error("Error while reading/writing: " + e);
            }
        }
    }

    /*
     * HACK. This method is needed by our Continuous Build server : maven.xwiki.org.
     * GWT seems to have an unusual way to manage input events, our WYSIWYG editor needs its container window to have
     * a _real_ focus (Windowing System level) to catch them (at least on Linux and OSX).
     * This method executes a small C program to set the Windowing System (X) focus on the window named :
     * "Editing wysiwyg for WysiwygTest - Iceweasel". More information about this program can be found here :
     * http://dev.xwiki.org/xwiki/bin/view/Community/ContinuousBuild
     */
    private void externalX11WindowFocus() throws Exception
    {
        if ((new File(XWINDOWFOCUS_BINARY)).exists()) {
            ProcessBuilder pb =
                new ProcessBuilder(new String[]{XWINDOWFOCUS_BINARY, "Editing wysiwyg for WysiwygTest - Iceweasel"});
            pb.environment().put("DISPLAY", ":1.0");
            Process shell = pb.start();            
            new StreamRedirector(shell.getInputStream(), System.out).start();
            new StreamRedirector(shell.getErrorStream(), System.err).start();
        }
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();

        // Go to the wysiwyg editor if needed
        if (!getSelenium().getLocation()
            .equals("http://localhost:8080/xwiki/bin/edit/Main/WysiwygTest?editor=wysiwyg"))
        {
            open("Main", "WysiwygTest");
            clickLinkWithText("WYSIWYG");
        }

        // Switch the document to xwiki/2.0 syntax if needed
        if (!getSelenium().getValue("syntaxId").equals("xwiki/2.0")) {
            // The syntax can be changed only from Wiki mode at this point.
            clickLinkWithText("Wiki");
            setFieldValue("syntaxId", "xwiki/2.0");
            clickEditSaveAndContinue();
            // We changed the syntax. Let's go back to WYSIWYG mode.
            clickLinkWithText("WYSIWYG");
        }

        // Focus on the XWiki window (Seems not to work, at least on Linux and OSX)
        getSelenium().windowFocus();

        // Focus on the XWiki window on our continuous build server (The hard way)
        externalX11WindowFocus();

        // Reset editor's content
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
        updateRichTextAreaFormField();
    }

    /**
     * Resets the content of the rich text area by selecting all the text like CTRL+A and deleting it using Backspace.
     */
    public void resetContent()
    {
        // We try to mimic as much as possible the user behavior.
        selectAllContent();
        typeBackspace();
        // We select again all the content. In Firefox, the selection will include the annoying br tag. Further typing
        // will overwrite it. See XWIKI-2732.
        selectAllContent();
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

    public void typeTextThenEnterTwice(String text)
    {
        typeTextThenEnter(text);
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
        keyPress("\\13");
    }
    
    public void typeEnter(int nb)
    {
        for (; nb > 0; nb--) {
            typeEnter();
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
        // Although Delete is not a printable key, it affects the displayed text so we must fire KeyPress event too.
        keyPress("\\46");
    }

    public void typeTab()
    {
        keyPress("\\9");
    }

    public void typeTab(int count)
    {
        for (int i = 0; i < count; i++) {
            typeTab();
        }
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

    public void clickUndoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickUndoButton();
        }
    }

    public void clickRedoButton()
    {
        pushButton("//div[@title='Redo (CTRL+Y)']");
    }

    public void clickRedoButton(int count)
    {
        for (int i = 0; i < count; i++) {
            clickRedoButton();
        }
    }

    public void clickSymbolButton()
    {
        pushButton("//div[@title='Insert custom character']");
    }

    public void clickInsertImageButton()
    {
        pushButton("//div[@title='Insert/Edit Image']");
    }

    public void clickInsertTableButton()
    {
        pushButton("//div[@title='Inserts a new table']");
    }

    public void clickBackToEdit()
    {
        submit("//button[text()='Back To Edit']");
    }

    public void applyStyle(String style)
    {
        getSelenium().select("//select[@title=\"Apply Style\"]", style);
    }

    public void applyStyleInLine()
    {
        applyStyle("Inline");
    }

    public void applyStyleParagraph()
    {
        applyStyle("Paragraph");
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
        // Blur the rich text area.
        getSelenium().clickAt("title", "0,0");
        // Give the focus back.
        runScript("XWE.focus();");
    }
}
