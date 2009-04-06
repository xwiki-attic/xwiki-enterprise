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
    public static final String MENU_LINK = "Link";

    public static final String MENU_WEBPAGE = "Web page";

    public static final String MENU_EMAIL_ADDRESS = "Email address";

    public static final String MENU_WIKIPAGE = "Wiki page";

    public static final String MENU_LINK_EDIT = "Edit link";

    public static final String MENU_LINK_REMOVE = "Remove link";

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

        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        // wait for dialog to open
        ensureDialogIsOpen();

        String selectedSpace = "Blog";
        String selectedPage = "AddCategory";

        typeInExplorerInput(selectedSpace + "." + selectedPage);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + selectedSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + selectedPage + "\"]');");
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkToWikiPage");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        ensureDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>xwiki:" + selectedSpace + "." + selectedPage + "]]");
    }

    /**
     * Test the basic feature for adding a link to an existing space.
     */
    public void testCreateLinkToSpace()
    {
        String linkLabel = "foobar";
        typeText(linkLabel);
        selectAllContent();
        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        // wait for dialog to open
        ensureDialogIsOpen();

        String space = "Blog";

        typeInExplorerInput(space + ".WebHome");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "WebHome\"]');");

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkToWikiPage");
        clickButtonWithText("Create Link");

        ensureDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>xwiki:" + space + ".WebHome]]");
    }

    /**
     * Test the basic feature for adding a link to a new page.
     */
    public void testCreateLinkToNewPage()
    {
        String linkLabel = "alice";
        String space = "Main";
        String newPageName = "AliceInWonderwiki";
        typeText(linkLabel);
        selectAllContent();
        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        // make sure dialog is open
        ensureDialogIsOpen();
        typeInExplorerInput(space + "." + newPageName);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");

        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkToWikiPage");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        ensureDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>xwiki:" + space + "." + newPageName + "]]");
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
        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        // make sure dialog is open
        ensureDialogIsOpen();
        typeInExplorerInput(newSpace + "." + newPage);

        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkToWikiPage");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        ensureDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>xwiki:" + newSpace + "." + newPage + "]]");
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

        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        // make sure the dialog is open
        ensureDialogIsOpen();
        // ensure wizard step is loaded
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        assertWiki("[[" + linkLabel + ">>" + url + "]]");
    }

    /**
     * Test adding a link to a web page with a different label than the selected text.
     */
    public void testCreateLinkToWebPageWithChangedLabel()
    {
        String linkLabel = "rox";
        String url = "http://www.xwiki.org";
        typeText(linkLabel);
        selectAllContent();
        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        // make sure the dialog is open
        ensureDialogIsOpen();
        // ensure wizard step is loaded
        ensureStepIsLoaded("xLinkToUrl");
        String newLabel = "xwiki rox";
        typeInInput("Label of the link to a web page", newLabel);
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

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
        clickMenu(MENU_LINK);
        clickMenu(MENU_EMAIL_ADDRESS);

        ensureDialogIsOpen();
        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

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
        clickMenu(MENU_LINK);
        clickMenu(MENU_EMAIL_ADDRESS);
        ensureDialogIsOpen();
        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();
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
        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        ensureDialogIsOpen();

        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        clickMenu(MENU_WEBPAGE);
        ensureDialogIsOpen();

        // test that the picked up label of the link is the right text
        assertEquals("ourxwikirox", getInputValue("Label of the link to a web page"));
        typeInInput("Web page address", "www.xwiki.org");
        clickButtonWithText("Create Link");

        ensureDialogIsClosed();
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

        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        ensureDialogIsOpen();

        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        assertXHTML("<p>this is <!--startwikilink:http://www.xwiki.com--><span class=\"wikiexternallink\">"
            + "<a href=\"http://www.xwiki.com\">xwiki</a></span><!--stopwikilink--><br class=\"spacer\"></p>");
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 5);
        // type things to trigger toolbar update
        typeBackspace();
        typeText("i");

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // unlink here should only move the caret out
        clickMenu(MENU_LINK_REMOVE);
        typeText(" which rox");
        assertWiki("this is [[" + linkLabel + ">>" + linkURL + "]] which rox");

        select("XWE.body.firstChild", 1, "XWE.body.firstChild.childNodes[1].firstChild", 5);
        triggerToolbarUpdate();

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        ensureDialogIsOpen();

        assertEquals(linkLabel, getInputValue("Label of the link to a web page"));
        assertEquals(linkURL, getInputValue("Web page address"));

        typeInInput("Web page address", newLinkURL);
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

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
        String newPageName = "Images";

        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        ensureDialogIsOpen();
        typeInExplorerInput(spaceName + "." + pageName);
        // wait for the space to get selected
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + spaceName
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkToWikiPage");
        assertEquals("photos.png", getInputValue("Label of the link to an existing page"));
        // check that the label is readonly
        assertElementPresent("//input[@title=\"Label of the link to an existing page\" and @readonly=\"\"]");

        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>xwiki:Blog.Photos]]");

        // move caret at the end and type some more
        moveCaret("XWE.body.firstChild", 1);
        typeText(" foo ");

        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        typeInInput("Label of the link to a web page", "bar");
        typeInInput("Web page address", "http://bar.myxwiki.org");
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        // now go on and edit the image
        select("XWE.body.firstChild.firstChild", 0, "XWE.body.firstChild.firstChild", 1);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        ensureDialogIsOpen();

        assertEquals(spaceName + "." + pageName, getExplorerInputValue());

        typeInExplorerInput(spaceName + "." + newPageName);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + spaceName
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");
        clickButtonWithText("Select");
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>xwiki:Blog.Images]] foo [[bar>>http://bar.myxwiki.org]]");
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
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        assertTrue(isMenuEnabled(MENU_EMAIL_ADDRESS));

        // put selection inside the first link
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 2);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection around the second link, in the parent
        select("XWE.body.firstChild", 3, "XWE.body.firstChild", 4);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection with ends at the end of previous text and at the beginning of the next text
        select("XWE.body.firstChild.childNodes[4]", 1, "XWE.body.firstChild.childNodes[6]", 0);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection with one end inside the anchor and one end at the end of the text before or after
        select("XWE.body.firstChild.childNodes[6]", 1, "XWE.body.firstChild.childNodes[7].firstChild", 5);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection around the bold text inside a link label
        select("XWE.body.firstChild.childNodes[9].childNodes[1].firstChild", 0,
            "XWE.body.firstChild.childNodes[9].childNodes[1].firstChild", 1);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // set selection starting in the text before the link and ending in the link
        select("XWE.body.firstChild.childNodes[12]", 5, "XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertFalse(isMenuEnabled(MENU_WEBPAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_WIKIPAGE));

        // set selection in two different links
        select("XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4, "XWE.body.firstChild.childNodes[15]", 1);
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertFalse(isMenuEnabled(MENU_WEBPAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_WIKIPAGE));

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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);

        ensureDialogIsOpen();

        // now check if the dialog has loaded correctly
        ensureStepIsLoaded("xLinkToUrl");
        assertEquals("two", getInputValue("Label of the link to a web page"));
        assertEquals("http://www.xwiki.com", getInputValue("Web page address"));
        typeInInput("Web page address", "http://www.xwiki.org");

        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        clickMenu(MENU_WIKIPAGE);
        ensureDialogIsOpen();

        String space = "Main";
        String page = "WebHome";

        typeInExplorerInput(space + "." + page);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + page
            + "\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkToWikiPage");
        // try to create link without filling in the label
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureDialogIsOpen();
        ensureStepIsLoaded("xLinkToWikiPage");
        typeInInput("Label of the link to an existing page", "foo");
        clickButtonWithText("Create Link");

        ensureDialogIsClosed();

        assertWiki("[[foo>>xwiki:" + space + "." + page + "]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now try again with a new page link
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        clickMenu(MENU_WIKIPAGE);
        ensureDialogIsOpen();

        space = "Main";
        page = "NewPage";

        typeInExplorerInput(space + "." + page);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkToWikiPage");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureDialogIsOpen();
        ensureStepIsLoaded("xLinkToWikiPage");
        typeInInput("Label of the link to an existing page", "foo");
        clickButtonWithText("Create Link");

        /*
         * Cannot check these, for the moment assertTrue(getSelenium().isAlertPresent());
         * assertEquals("The name of the new space was not set", getSelenium().getAlert()); ensureDialogIsOpen();
         * typeInInputWithTitle("New space name", "NewSpace"); typeInInputWithTitle("New page name", "NewPage");
         * clickCreateNewPageLinkButton(); ensureDialogIsClosed(); assertWiki("[[foo>>NewSpace.NewPage]]");
         */

        assertWiki("[[foo>>xwiki:Main.NewPage]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now create a link to a web page
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        clickMenu(MENU_WEBPAGE);
        ensureDialogIsOpen();

        typeInInput("Web page address", "http://www.xwiki.org");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        ensureDialogIsOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Label of the link to a web page", "xwiki");
        clickButtonWithText("Create Link");
        ensureDialogIsClosed();

        assertWiki("[[xwiki>>http://www.xwiki.org]]");

        // clean up
        selectAllContent();
        typeDelete();

        // now create a link to an email page
        triggerToolbarUpdate();
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_EMAIL_ADDRESS));
        clickMenu(MENU_EMAIL_ADDRESS);
        ensureDialogIsOpen();

        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        ensureDialogIsOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Label of the link to an email address", "alice");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The email address was not set", getSelenium().getAlert());

        ensureDialogIsOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Email address", "alice@wonderla.nd");
        clickButtonWithText("Create Link");

        ensureDialogIsClosed();

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
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_WEBPAGE));
        assertFalse(isMenuEnabled(MENU_WIKIPAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
    }

    /**
     * Make sure the link dialog is opened.
     */
    protected void ensureDialogIsOpen()
    {
        waitForCondition("selenium.isElementPresent('//div[contains(@class, \"xDialogBox\")]')");
    }

    /**
     * Make sure that the link dialog is closed.
     */
    protected void ensureDialogIsClosed()
    {
        waitForCondition("!selenium.isElementPresent('//div[contains(@class, \"xDialogBox\")]')");
    }

    protected void ensureStepIsLoaded(String divClass)
    {
        waitForCondition("selenium.isElementPresent('//div[contains(@class, \"" + divClass + "\")]');");
    }

    protected void typeInExplorerInput(String text)
    {
        getSelenium().type("//div[contains(@class, 'xExplorerPanel')]/input", text);
    }

    protected String getExplorerInputValue()
    {
        return getSelenium().getValue("//div[contains(@class, 'xExplorerPanel')]/input");
    }
}
