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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

public class LinkSupportTest extends AbstractWysiwygTestCase
{
    /**
     * The XPath selector for the existing spaces selector.
     */
    private String existingSpacesSelector = "//select[@title=\"Select a space\"]";

    /**
     * The XPath selector for spaces selector in the "New Page" link creation tab.
     */
    private String existingOrNewSpacesSelector = "//select[@title=\"Select or create a space\"]";

    /**
     * The XPath selector for the pages selector.
     */
    private String pagesSelector = "//select[@title=\"Select a page\"]";

    /**
     * Creates the test suite for this test class.
     * 
     * @return the test suite corresponding to this class
     */
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests insert link feature");
        suite.addTestSuite(LinkSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * Test the basic feature for adding a link to an existing page.
     */
    public void testCreateLinkToExistingPage()
    {
        String linkLabel = "foo";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        // wait for dialog to open
        ensureLinkDialogIsOpen();
        selectExistingPageTab();
        // wait for selectors to load in the dialog
        String selectedSpace = "Blog";
        String selectedPage = "AddCategory";
        waitForCondition("selenium.isElementPresent('" + existingSpacesSelector + "/option[@value=\"" + selectedSpace
            + "\"]" + "')");
        getSelenium().select(existingSpacesSelector, selectedSpace);
        // wait for pages to reload
        waitForCondition("selenium.isElementPresent('" + pagesSelector + "/option[@value=\"" + selectedPage + "\"]"
            + "')");
        getSelenium().select(pagesSelector, selectedPage);
        clickCreateExistingPageLinkButton();
        // wait for the link dialog to close
        ensureLinkDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>" + selectedSpace + "." + selectedPage + "]]");
    }

    /**
     * Test the basic feature for adding a link to an existing space.
     */
    public void testCreateLinkToSpace()
    {
        String linkLabel = "foobar";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectExistingPageTab();
        // wait for selectors to load in the dialog
        waitForCondition("selenium.isElementPresent('" + existingSpacesSelector + "/option[1]');");
        String space = getSelenium().getValue(existingSpacesSelector);
        clickCreateExistingSpaceLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>" + space + ".WebHome]]");
    }

    /**
     * Test the basic feature for adding a link to a new page.
     */
    public void testCreateLinkToNewPage()
    {
        String linkLabel = "alice";
        String newPageName = "AliceInWonderwiki";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        // make sure dialog is open
        ensureLinkDialogIsOpen();
        selectNewPageTab();
        typeInInput("New page name", newPageName);
        // wait for the spaces selector to load
        waitForCondition("selenium.isElementPresent('" + existingOrNewSpacesSelector + "/option[1]');");
        String space = getSelenium().getValue(existingOrNewSpacesSelector);
        clickCreateNewPageLinkButton();
        // wait for the link dialog to close
        ensureLinkDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>" + space + "." + newPageName + "]]");
    }

    /**
     * Test the basic feature for adding a link to a new page in a new space.
     */
    public void testCreateLinkToNewPageInNewSpace()
    {
        String linkLabel = "bob";
        String newSpace = "Bob";
        String newPage = "Cat";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        // make sure dialog is open
        ensureLinkDialogIsOpen();
        selectNewPageTab();
        typeInInput("New page name", newPage);
        waitForCondition("selenium.isElementPresent('" + existingOrNewSpacesSelector + "/option[1]');");
        getSelenium().select(existingOrNewSpacesSelector, "index=0");
        typeInInput("New space name", newSpace);
        clickCreateNewPageLinkButton();
        // wait for the link dialog to close
        ensureLinkDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>" + newSpace + "." + newPage + "]]");
    }

    /**
     * Test the basic feature for adding a link to a web page.
     */
    public void testCreateLinkToWebPage()
    {
        String linkLabel = "xwiki";
        String url = "http://www.xwiki.org";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        // make sure the dialog is open
        ensureLinkDialogIsOpen();
        selectWebPageTab();
        typeInInput("Web page address", url);
        clickCreateWebPageLinkButton();

        assertWiki("[[" + linkLabel + ">>" + url + "]]");
    }

    /**
     * Test adding a link to a web page with a different label than the selected text.
     */
    public void testCreateLinkToWebPageWithChangedLabel()
    {
        String linkLabel = "rox";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectWebPageTab();
        String newLabel = "xwiki rox";
        String url = "http://www.xwiki.org";
        typeInInput("Label of the link to a web page", newLabel);
        typeInInput("Web page address", url);
        clickCreateWebPageLinkButton();

        assertWiki("[[" + newLabel + ">>" + url + "]]");
    }

    /**
     * Test the basic feature for adding a link to an email address.
     */
    public void testCreateLinkToEmailAddress()
    {
        String linkLabel = "carol";
        String email = "mailto:carol@xwiki.org";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectEmailTab();
        typeInInput("Email address", email);
        clickCreateEmailLinkButton();

        assertWiki("[[" + linkLabel + ">>" + email + "]]");
    }

    /**
     * Test adding a link to an email address but not without specifying the mailto: protocol.
     */
    public void testCreateLinkToEmailAddressWithoutMailto()
    {
        String linkLabel = "joe le taxi";
        String email = "joe@xwiki.org";
        typeText(linkLabel);
        selectAllContent();
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectEmailTab();
        typeInInput("Email address", email);
        clickCreateEmailLinkButton();
        // Note: the new line here because, although we remove initial <br> in the editor content by typing here after
        // setUp selected all content, typing a space in the editor causes browser (FF) to add a <br> at the end,
        // which we then select and add inside the anchor label.
        assertWiki("[[" + linkLabel + "\n>>mailto:" + email + "]]");
    }

    /**
     * Test adding a link by typing the link label instead of selecting it.
     */
    public void testCreateLinkWithNewLabel()
    {
        String linkLabel = "xwiki";
        String linkURL = "www.xwiki.org";
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectWebPageTab();
        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickCreateWebPageLinkButton();

        assertWiki("[[" + linkLabel + ">>http://" + linkURL + "]]");
    }

    /**
     * Test that anchor label formatting is preserved.
     */
    public void testCreateLinkPreservesLabelFormatting()
    {
        typeText("our");
        clickBoldButton();
        typeText("xwiki");
        clickBoldButton();
        typeText("rox");
        selectAllContent();
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectWebPageTab();
        // test that the picked up label of the link is the right text
        assertEquals("ourxwikirox", getInputValue("Label of the link to a web page"));
        typeInInput("Web page address", "www.xwiki.org");
        clickCreateWebPageLinkButton();

        ensureLinkDialogIsClosed();
        assertWiki("[[our**xwiki**rox>>http://www.xwiki.org]]");
    }

    /**
     * Test creating a link with some text around and then editing it. Test that the link type and parameters are
     * correctly read and that the wiki syntax is correctly generated.
     */
    public void testCreateThenEditLink()
    {
        // put everything in a paragraph because editing in body is sometimes parsed wrong
        applyStyleParagraph();
        typeText("this is ");
        String linkLabel = "xwiki";
        String linkURL = "http://www.xwiki.com";
        String newLinkURL = "http://www.xwiki.org";

        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectWebPageTab();
        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickCreateWebPageLinkButton();
        ensureLinkDialogIsClosed();

        // now edit
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        assertEquals(linkLabel, getInputValue("Label of the link to a web page"));
        assertEquals(linkURL, getInputValue("Web page address"));

        selectExistingPageTab();
        assertEquals(linkLabel, getInputValue("Label of the link to an existing page"));

        String selectedSpace = "Main";
        String selectedPage = "WebHome";

        waitForCondition("selenium.isElementPresent('" + existingSpacesSelector + "/option[@value=\"" + selectedSpace
            + "\"]" + "')");
        getSelenium().select(existingSpacesSelector, selectedSpace);
        // wait for pages to reload
        waitForCondition("selenium.isElementPresent('" + pagesSelector + "/option[@value=\"" + selectedPage + "\"]"
            + "')");
        getSelenium().select(pagesSelector, selectedPage);

        clickCreateExistingPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("this is [[" + linkLabel + ">>" + selectedSpace + "." + selectedPage + "]]");

        assertXHTML("<p>this is <!--startwikilink:Main.WebHome--><span class=\"wikilink\">"
            + "<a href=\"/xwiki/bin/view/Main/\">xwiki</a></span><!--stopwikilink--></p>");
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 5);
        // type things to trigger toolbar update
        typeBackspace();
        typeText("i");

        assertTrue(isUnlinkButtonEnabled());
        // unlink here should only move the caret out
        clickUnlinkButton();
        typeText(" which rox");
        assertWiki("this is [[" + linkLabel + ">>" + selectedSpace + "." + selectedPage + "]] which rox");

        select("XWE.body.firstChild", 1, "XWE.body.firstChild.childNodes[1].firstChild", 5);
        triggerToolbarUpdate();

        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        waitForCondition("selenium.isElementPresent('" + existingSpacesSelector + "/option[@value=\"" + selectedSpace
            + "\"]" + "')");
        waitForCondition("selenium.isElementPresent('" + pagesSelector + "/option[@value=\"" + selectedPage + "\"]"
            + "')");

        assertEquals(linkLabel, getInputValue("Label of the link to an existing page"));
        assertEquals(getSelenium().getValue(existingSpacesSelector), selectedSpace);
        assertEquals(getSelenium().getValue(pagesSelector), selectedPage);

        selectWebPageTab();
        assertEquals(linkLabel, getInputValue("Label of the link to a web page"));
        typeInInput("Web page address", newLinkURL);
        clickCreateWebPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("this is [[" + linkLabel + ">>" + newLinkURL + "]] which rox");
    }

    /**
     * Test creating and editing link around an image. Test that the displayed label is the alt text of the image and is
     * not editable, that the link parameters are correctly read, and that the wiki syntax is correctly generated.
     */
    public void testCreateAndEditLinkOnImage()
    {
        // put everything in a paragraph as there are some whitespace trouble parsing outside the paragraph
        applyStyleParagraph();

        clickInsertImageButton();

        String imageSpaceSelector = "//div[@class=\"xImageChooser\"]//select[2]";
        String imageSpace = "XWiki";
        waitForCondition("selenium.isElementPresent('" + imageSpaceSelector + "/option[@value=\"" + imageSpace
            + "\"]');");
        getSelenium().select(imageSpaceSelector, imageSpace);

        String imagePageSelector = "//div[@class=\"xImageChooser\"]//select[3]";
        String imagePage = "AdminSheet";
        waitForCondition("selenium.isElementPresent('" + imagePageSelector + "/option[@value=\"" + imagePage + "\"]');");
        getSelenium().select(imagePageSelector, imagePage);

        getSelenium().click("//div[@class=\"xImageChooser\"]//button[text()=\"Update\"]");

        String imageSelector = "//div[@class=\"xImagesContainerPanel\"]//img[@title=\"photos.png\"]";
        waitForCondition("selenium.isElementPresent('" + imageSelector + "');");
        getSelenium().click(imageSelector);

        getSelenium().click("//div[@class=\"xImageDialogMain\"]/button[text()=\"OK\"]");

        // wait for the image dialog to be closed
        waitForCondition("!selenium.isElementPresent('//div[@class=\"xImageDialogMain\"]')");

        // now add a link around this image
        String pageName = "Photos";
        String spaceName = "Blog";
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectNewPageTab();
        assertEquals("photos.png", getInputValue("Label of the link to a new page"));
        // check that the label is readonly
        assertElementPresent("//input[@title=\"Label of the link to a new page\" and @readonly=\"\"]");

        // wait for the spaces selector to load
        waitForCondition("selenium.isElementPresent('" + existingOrNewSpacesSelector + "/option[@value=\"" + spaceName
            + "\"]');");
        getSelenium().select(existingOrNewSpacesSelector, spaceName);
        typeInInput("New page name", pageName);

        clickCreateNewPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>Blog.Photos]]");

        // move caret at the end and type some more
        moveCaret("XWE.body.firstChild", 1);
        typeText(" foo ");

        clickInsertLinkButton();
        ensureLinkDialogIsOpen();
        selectWebPageTab();
        typeInInput("Label of the link to a web page", "bar");
        typeInInput("Web page address", "http://bar.myxwiki.org");
        clickCreateWebPageLinkButton();
        ensureLinkDialogIsClosed();

        // now go on and edit the image
        select("XWE.body.firstChild.firstChild", 0, "XWE.body.firstChild.firstChild", 1);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());

        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        // assert the state of the dialog:
        // wait for the spaces selector to load
        waitForCondition("selenium.isElementPresent('" + existingOrNewSpacesSelector + "/option[@value=\"" + spaceName
            + "\"]');");
        assertEquals("photos.png", getInputValue("Label of the link to a new page"));
        // check that the label is readonly
        assertElementPresent("//input[@title=\"Label of the link to a new page\" and @readonly=\"\"]");
        assertEquals("Blog", getSelenium().getValue(existingOrNewSpacesSelector));
        assertEquals("Photos", getInputValue("New page name"));
        typeInInput("New page name", "Images");

        clickCreateNewPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>Blog.Images]] foo [[bar>>http://bar.myxwiki.org]]");
    }

    /**
     * Test that the link existence is detected correctly for a couple of cases of the selection around a link, and that
     * unlink executes correctly for these situations.
     */
    public void testDetectAndUnlinkSelectedAnchor()
    {
        setWikiContent("foo [[bar>>http://xwiki.org]] [[far>>Main.WebHome]] [[alice>>Main.NewPage]] "
            + "[[carol>>mailto:carol@xwiki.org]] [[b**o**b>>http://xwiki.org]] blog webhome [[Blog.WebHome]] "
            + "[[image:XWiki.AdminSheet@photos.png>>Blog.Photos]]");

        // put selection inside first text
        moveCaret("XWE.body.firstChild.firstChild", 2);
        triggerToolbarUpdate();
        assertFalse(isUnlinkButtonEnabled());
        assertTrue(isInsertLinkButtonEnabled());

        // put selection inside the first link
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 2);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        // now unlink it
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 2);
        triggerToolbarUpdate();
        assertTrue(isUnlinkButtonEnabled());
        clickUnlinkButton();

        // put selection around the second link, in the parent
        select("XWE.body.firstChild", 3, "XWE.body.firstChild", 4);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        assertTrue(isUnlinkButtonEnabled());
        // now unlink it
        clickUnlinkButton();

        // put selection with ends at the end of previous text and at the beginning of the next text
        select("XWE.body.firstChild.childNodes[4]", 1, "XWE.body.firstChild.childNodes[6]", 0);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        assertTrue(isUnlinkButtonEnabled());
        clickUnlinkButton();

        // put selection with one end inside the anchor and one end at the end of the text before or after
        select("XWE.body.firstChild.childNodes[6]", 1, "XWE.body.firstChild.childNodes[7].firstChild", 5);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        assertTrue(isUnlinkButtonEnabled());
        clickUnlinkButton();

        // put selection around the bold text inside a link label
        select("XWE.body.firstChild.childNodes[9].childNodes[1].firstChild", 0,
            "XWE.body.firstChild.childNodes[9].childNodes[1].firstChild", 1);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        assertTrue(isUnlinkButtonEnabled());
        clickUnlinkButton();

        // set selection starting in the text before the link and ending in the link
        select("XWE.body.firstChild.childNodes[12]", 5, "XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4);
        triggerToolbarUpdate();
        assertFalse(isInsertLinkButtonEnabled());
        assertFalse(isUnlinkButtonEnabled());

        // set selection in two different links
        select("XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4, "XWE.body.firstChild.childNodes[15]", 1);
        triggerToolbarUpdate();
        assertFalse(isInsertLinkButtonEnabled());
        assertFalse(isUnlinkButtonEnabled());

        assertWiki("foo bar far alice carol b**o**b blog webhome [[Blog.WebHome]] "
            + "[[image:XWiki.AdminSheet@photos.png>>Blog.Photos]]");
    }

    /**
     * Test editing a link which is the single text in a list item. This case is special because the delete command is
     * invoked upon replacing the link, which causes clean up of the list.
     */
    public void testEditLinkInList()
    {
        setWikiContent("* one\n* [[two>>http://www.xwiki.com]]\n** three");
        
        // now edit the link in the second list item
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild.firstChild", 1);
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());

        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        // now check if the dialog has loaded correctly
        assertEquals("two", getInputValue("Label of the link to a web page"));
        assertEquals("http://www.xwiki.com", getInputValue("Web page address"));
        typeInInput("Web page address", "http://www.xwiki.org");

        clickCreateWebPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("* one\n* [[two>>http://www.xwiki.org]]\n** three");
    }

    /**
     * Test that the link dialogs are correctly validated and alerts are displayed when mandatory fields are not filled
     * in.
     */
    public void testValidationOnLinkInsert()
    {
        // try to create a link to an existing page without a label
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectExistingPageTab();
        waitForCondition("selenium.isElementPresent('" + existingSpacesSelector + "/option[1]');");
        waitForCondition("selenium.isElementPresent('" + pagesSelector + "/option[1]');");
        String space = getSelenium().getValue(existingSpacesSelector);
        String page = getSelenium().getValue(pagesSelector);

        clickCreateExistingPageLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureLinkDialogIsOpen();
        typeInInput("Label of the link to an existing page", "foo");
        clickCreateExistingPageLinkButton();

        ensureLinkDialogIsClosed();

        assertWiki("[[foo>>" + space + "." + page + "]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now try again with a new page link
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectNewPageTab();
        waitForCondition("selenium.isElementPresent('" + existingOrNewSpacesSelector + "/option[1]');");
        getSelenium().select(existingOrNewSpacesSelector, "index=0");

        clickCreateNewPageLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureLinkDialogIsOpen();
        typeInInput("Label of the link to a new page", "foo");
        clickCreateNewPageLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The name of the new space was not set", getSelenium().getAlert());

        ensureLinkDialogIsOpen();
        typeInInput("New space name", "NewSpace");
        typeInInput("New page name", "NewPage");
        clickCreateNewPageLinkButton();

        ensureLinkDialogIsClosed();

        assertWiki("[[foo>>NewSpace.NewPage]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now create a link to a web page
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectWebPageTab();
        typeInInput("Web page address", "http://www.xwiki.org");
        clickCreateWebPageLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureLinkDialogIsOpen();
        typeInInput("Label of the link to a web page", "xwiki");
        clickCreateWebPageLinkButton();
        ensureLinkDialogIsClosed();

        assertWiki("[[xwiki>>http://www.xwiki.org]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now create a link to a web page
        triggerToolbarUpdate();
        assertTrue(isInsertLinkButtonEnabled());
        clickInsertLinkButton();
        ensureLinkDialogIsOpen();

        selectEmailTab();

        clickCreateEmailLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        ensureLinkDialogIsOpen();
        typeInInput("Label of the link to an email address", "alice");
        clickCreateEmailLinkButton();

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The email address was not set", getSelenium().getAlert());

        ensureLinkDialogIsOpen();
        typeInInput("Email address", "alice@wonderla.nd");
        clickCreateEmailLinkButton();

        ensureLinkDialogIsClosed();

        assertWiki("[[alice>>mailto:alice@wonderla.nd]]");
    }

    /**
     * Test that the link button is not enabled when the selection contains some block elements.
     */
    public void testCannotCreateLinkAroundBlockElements()
    {
        applyStyleParagraph();
        typeText("foo");
        typeEnter(2);
        typeText("bar");
        assertXHTML("<p>foo</p><p>bar<br class=\"spacer\"></p>");
        select("XWE.body.firstChild.firstChild", 2, "XWE.body.childNodes[1].firstChild", 2);
        triggerToolbarUpdate();
        assertFalse(isInsertLinkButtonEnabled());
        assertFalse(isUnlinkButtonEnabled());
    }

    /**
     * Make sure the link dialog is opened.
     */
    protected void ensureLinkDialogIsOpen()
    {
        waitForCondition("selenium.isElementPresent('//div[@class=\"gwt-PopupPanel linkDialog\"]')");
    }

    /**
     * Make sure that the link dialog is closed.
     */
    protected void ensureLinkDialogIsClosed()
    {
        waitForCondition("!selenium.isElementPresent('//div[@class=\"gwt-PopupPanel linkDialog\"]')");
    }

    /**
     * Select the existing page link tab.
     */
    protected void selectExistingPageTab()
    {
        getSelenium().click("//div[text()='Existing page']");
    }

    /**
     * Select the new page link tab.
     */
    protected void selectNewPageTab()
    {
        getSelenium().click("//div[text()='New page']");
    }

    /**
     * Select the web page link tab.
     */
    protected void selectWebPageTab()
    {
        getSelenium().click("//div[text()='Web page']");
    }

    /**
     * Select the email address link tab.
     */
    protected void selectEmailTab()
    {
        getSelenium().click("//div[text()='Email address']");
    }

    /**
     * Click the existing page link submit button.
     */
    protected void clickCreateExistingPageLinkButton()
    {
        clickButton("Create a link to an existing page");
    }

    /**
     * Click the existing space link submit button.
     */
    protected void clickCreateExistingSpaceLinkButton()
    {
        clickButton("Create a link to the space main page");
    }

    /**
     * Click the new page link submit button.
     */
    protected void clickCreateNewPageLinkButton()
    {
        clickButton("Create a link to a new page");
    }

    /**
     * Click the web page link submit button.
     */
    protected void clickCreateWebPageLinkButton()
    {
        clickButton("Create a link to a web page");
    }

    /**
     * Click the email address link submit button.
     */
    protected void clickCreateEmailLinkButton()
    {
        clickButton("Create a link to an email address");
    }
}
