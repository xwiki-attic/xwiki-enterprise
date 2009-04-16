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

    public static final String MENU_ATTACHMENT = "Attached file";

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
        waitForDialogToOpen();

        String selectedSpace = "Blog";
        String selectedPage = "AddCategory";

        typeInExplorerInput(selectedSpace + "." + selectedPage);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + selectedSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + selectedPage + "\"]');");
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

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
        waitForDialogToOpen();

        String space = "Blog";

        typeInExplorerInput(space + ".WebHome");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "WebHome\"]');");

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

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
        waitForDialogToOpen();
        typeInExplorerInput(space + "." + newPageName);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");

        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>xwiki:" + space + "." + newPageName + "]]");
    }

    /**
     * Test the basic feature for adding a link to a new page in a new space.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3511
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
        waitForDialogToOpen();
        typeInExplorerInput(newSpace + "." + newPage);

        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        waitForDialogToClose();

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
        waitForDialogToOpen();
        // ensure wizard step is loaded
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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
        waitForDialogToOpen();
        // ensure wizard step is loaded
        ensureStepIsLoaded("xLinkToUrl");
        String newLabel = "xwiki rox";
        typeInInput("Label of the link to a web page", newLabel);
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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

        waitForDialogToOpen();
        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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
        waitForDialogToOpen();
        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        waitForDialogToClose();
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
        waitForDialogToOpen();

        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        clickMenu(MENU_WEBPAGE);
        waitForDialogToOpen();

        // test that the picked up label of the link is the right text
        assertEquals("ourxwikirox", getInputValue("Label of the link to a web page"));
        typeInInput("Web page address", "www.xwiki.org");
        clickButtonWithText("Create Link");

        waitForDialogToClose();
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
        waitForDialogToOpen();

        typeInInput("Web page address", linkURL);
        typeInInput("Label of the link to a web page", linkLabel);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        waitForDialogToOpen();

        assertEquals(linkLabel, getInputValue("Label of the link to a web page"));
        assertEquals(linkURL, getInputValue("Web page address"));

        typeInInput("Web page address", newLinkURL);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

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
        String newSpaceName = "Main";
        String newPageName = "Dashboard";

        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        waitForDialogToOpen();
        typeInExplorerInput(spaceName + "." + pageName);
        // wait for the space to get selected
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + spaceName
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        assertEquals("photos.png", getInputValue("Label of the created link"));
        // check that the label is readonly
        assertElementPresent("//input[@title=\"Label of the created link\" and @readonly=\"\"]");

        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>xwiki:Blog.Photos]]");

        // move caret at the end and type some more
        moveCaret("XWE.body.firstChild", 1);
        typeText(" foo ");

        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        typeInInput("Label of the link to a web page", "bar");
        typeInInput("Web page address", "http://bar.myxwiki.org");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        // now go on and edit the image
        select("XWE.body.firstChild.firstChild", 0, "XWE.body.firstChild.firstChild", 1);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        waitForDialogToOpen();

        // check the explorer selection
        assertEquals("xwiki:" + spaceName + "." + pageName, getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + spaceName
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");

        typeInExplorerInput(newSpaceName + "." + newPageName);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + newSpaceName
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + newPageName
            + "\"]');");
        clickButtonWithText("Select");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[[[image:XWiki.AdminSheet@photos.png]]>>xwiki:" + newSpaceName + "." + newPageName
            + "]] foo [[bar>>http://bar.myxwiki.org]]");
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
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        assertTrue(isMenuEnabled(MENU_EMAIL_ADDRESS));

        // put selection inside the first link
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 2);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection around the second link, in the parent
        select("XWE.body.firstChild", 3, "XWE.body.firstChild", 4);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // put selection with ends at the end of previous text and at the beginning of the next text
        select("XWE.body.firstChild.childNodes[4]", 1, "XWE.body.firstChild.childNodes[6]", 0);
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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // now unlink it
        clickMenu(MENU_LINK_REMOVE);

        // set selection starting in the text before the link and ending in the link
        select("XWE.body.firstChild.childNodes[12]", 5, "XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4);
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertFalse(isMenuEnabled(MENU_WEBPAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_WIKIPAGE));

        // set selection in two different links
        select("XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4, "XWE.body.firstChild.childNodes[15]", 1);
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
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);

        waitForDialogToOpen();

        // now check if the dialog has loaded correctly
        ensureStepIsLoaded("xLinkToUrl");
        assertEquals("two", getInputValue("Label of the link to a web page"));
        assertEquals("http://www.xwiki.com", getInputValue("Web page address"));
        typeInInput("Web page address", "http://www.xwiki.org");

        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("* one\n* [[two>>http://www.xwiki.org]]\n** three");
    }

    /**
     * Test that the link dialogs are correctly validated and alerts are displayed when mandatory fields are not filled
     * in.
     */
    public void testValidationOnLinkInsert()
    {
        // try to create a link to an existing page without a label
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        clickMenu(MENU_WIKIPAGE);
        waitForDialogToOpen();

        String space = "Main";
        String page = "WebHome";

        typeInExplorerInput(space + "." + page);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + page
            + "\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        // try to create link without filling in the label
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkConfig");
        typeInInput("Label of the created link", "foo");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

        assertWiki("[[foo>>xwiki:" + space + "." + page + "]]");

        // clean up
        resetContent();

        // now try again with a new page link
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        clickMenu(MENU_WIKIPAGE);
        waitForDialogToOpen();

        space = "Main";
        page = "NewPage";

        typeInExplorerInput(space + "." + page);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + space + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + "New page...\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkConfig");
        typeInInput("Label of the created link", "foo");
        clickButtonWithText("Create Link");

        /*
         * Cannot check these, for the moment assertTrue(getSelenium().isAlertPresent());
         * assertEquals("The name of the new space was not set", getSelenium() .getAlert()); ensureDialogIsOpen();
         * typeInInputWithTitle("New space name", "NewSpace"); typeInInputWithTitle("New page name", "NewPage");
         * clickCreateNewPageLinkButton(); ensureDialogIsClosed(); assertWiki("[[foo>>NewSpace.NewPage]]");
         */

        assertWiki("[[foo>>xwiki:Main.NewPage]]");

        // clean up
        resetContent();

        // now create a link to a web page
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WEBPAGE));
        clickMenu(MENU_WEBPAGE);
        waitForDialogToOpen();

        typeInInput("Web page address", "http://www.xwiki.org");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        // fill in the label and create link
        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Label of the link to a web page", "xwiki");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[xwiki>>http://www.xwiki.org]]");

        // clean up
        resetContent();

        // now create a link to an email page
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_EMAIL_ADDRESS));
        clickMenu(MENU_EMAIL_ADDRESS);
        waitForDialogToOpen();

        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Label of the link to an email address", "alice");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The email address was not set", getSelenium().getAlert());

        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Email address", "alice@wonderla.nd");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

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
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_WEBPAGE));
        assertFalse(isMenuEnabled(MENU_WIKIPAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
    }

    /**
     * Test that the location of the link is preserved if we go back from the configuration step to the page selection
     * step.
     */
    public void testLinkLocationIsPreservedOnPrevious()
    {
        String linkLabel = "foo";
        typeText(linkLabel);
        selectAllContent();

        clickMenu(MENU_LINK);
        clickMenu(MENU_WIKIPAGE);
        // wait for dialog to open
        waitForDialogToOpen();

        String selectedSpace = "Blog";
        String selectedPage = "AddCategory";
        String changedSpace = "Main";
        String changedPage = "RecentChanges";

        typeInExplorerInput(selectedSpace + "." + selectedPage);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + selectedSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + selectedPage + "\"]');");
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");

        // now hit previous
        clickButtonWithText("Previous");
        // wait for tree to load
        ensureStepIsLoaded("xExplorerPanel");
        // make sure input and selection in the tree reflect previously inserted values
        assertEquals("xwiki:" + selectedSpace + "." + selectedPage, getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + selectedSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\""
            + selectedPage + "\"]');");

        // and now change it
        typeInExplorerInput(changedSpace + "." + changedPage);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + changedSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + changedPage
            + "\"]');");
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>xwiki:" + changedSpace + "." + changedPage + "]]");
    }

    /**
     * Test the basic feature of adding a link to an attached file with the label from the selected text.
     */
    public void testCreateLinkToAttachment()
    {
        String linkLabel = "boo";
        typeText(linkLabel);
        selectAllContent();

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_ATTACHMENT));
        clickMenu(MENU_ATTACHMENT);
        // wait for dialog to open
        waitForDialogToOpen();

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "lquo.gif";

        typeInExplorerInput(attachSpace + "." + attachPage + "@" + attachment);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachPage
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + attachment
            + "\"]');");

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment + "]]");
    }

    /**
     * Test the basic feature of adding a link to an attached file, configuring its parameters in the parameter panel.
     */
    public void testCreateLinkToAttachmentWithParameters()
    {
        String linkLabel = "rquo";
        String linkTooltip = "Right quote image";
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_ATTACHMENT));
        clickMenu(MENU_ATTACHMENT);
        // wait for dialog to open
        waitForDialogToOpen();

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "rquo.gif";

        typeInExplorerInput(attachSpace + "." + attachPage + "@" + attachment);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachPage
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + attachment
            + "\"]');");

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        // fill in the link label and title
        typeInInput("Label of the created link", linkLabel);
        typeInInput("Tooltip of the created link, which will appear when mouse is over the link", linkTooltip);

        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment + "||title=\""
            + linkTooltip + "\"]]");
    }

    /**
     * Test that he creation of a link to an attached file is validated correctly.
     */
    public void testValidationOnLinkToAttachment()
    {
        String linkLabel = "boo";

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_ATTACHMENT));
        clickMenu(MENU_ATTACHMENT);
        // wait for dialog to open
        waitForDialogToOpen();

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "lquo.gif";

        // get an error from not inserting the attachment name
        typeInExplorerInput(attachSpace + "." + attachPage);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + attachPage
            + "\"]');");

        clickButtonWithText("Select");
        assertTrue(getSelenium().isAlertPresent());
        assertEquals("No attachment was selected", getSelenium().getAlert());

        ensureStepIsLoaded("xExplorerPanel");

        // type correct file reference
        typeInExplorerInput(attachSpace + "." + attachPage + "@" + attachment);
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + attachPage
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"" + attachment
            + "\"]');");

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");

        assertTrue(getSelenium().isAlertPresent());
        assertEquals("The label of the link cannot be empty", getSelenium().getAlert());

        ensureStepIsLoaded("xLinkConfig");
        typeInInput("Label of the created link", linkLabel);
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment + "]]");
    }

    /**
     * Test editing an existing link to an attachment
     */
    public void testEditLinkToAttachment()
    {
        setWikiContent("[[foobar>>attach:Main.RecentChanges@lquo.gif]]");
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        waitForDialogToOpen();
        ensureStepIsLoaded("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals("Main.RecentChanges@lquo.gif", getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"Main\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"RecentChanges\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"lquo.gif"
            + "\"]');");
        // and edit it now
        typeInExplorerInput("XWiki.AdminSheet@photos.png");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"XWiki\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"AdminSheet\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"photos.png"
            + "\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[foobar>>attach:XWiki.AdminSheet@photos.png]]");
    }

    /**
     * Test that editing a link with custom parameters set from wiki syntax preserves the parameters of the link.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3568
     */
    public void testEditLinkPreservesCustomParameters()
    {
        setWikiContent("[[foobar>>Main.RecentChanges||class=\"foobarLink\"]]");
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_EDIT));
        clickMenu(MENU_LINK_EDIT);
        waitForDialogToOpen();
        ensureStepIsLoaded("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals("Main.RecentChanges", getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"Main\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and "
            + "nobr=\"RecentChanges\"]');");
        // and edit it now
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        typeInInput("Label of the created link", "barfoo");
        typeInInput("Tooltip of the created link, which will appear when mouse is over the link", "Foo and bar");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[barfoo>>xwiki:Main.RecentChanges||class=\"foobarLink\" title=\"Foo and bar\"]]");
    }

    /**
     * Test that quotes in link tooltips are correctly escaped.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3569
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3575
     */
    public void testQuoteInLinkTooltip()
    {
        String linkLabel = "rox";
        String url = "http://www.xwiki.org";
        String tooltip = "our xwiki \"rox\"";
        typeText(linkLabel);
        selectAllContent();
        clickMenu(MENU_LINK);
        clickMenu(MENU_WEBPAGE);
        // make sure the dialog is open
        waitForDialogToOpen();
        // ensure wizard step is loaded
        ensureStepIsLoaded("xLinkToUrl");
        typeInInput("Tooltip of the created link, which will appear when mouse is over the link", tooltip);
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        assertWiki("[[" + linkLabel + ">>" + url + "||title=\"our xwiki \\\"rox\\\"\"]]");

        // now test the link is correctly parsed back
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        clickMenu(MENU_LINK);
        clickMenu(MENU_LINK_EDIT);
        waitForDialogToOpen();
        ensureStepIsLoaded("xLinkToUrl");
        assertEquals(tooltip,
            getInputValue("Tooltip of the created link, which will appear when mouse is over the link"));
    }

    /**
     * Test that the default selection is set to the current page when opening the wizard to create a link to a wiki
     * page or attachment.
     */
    public void testDefaultSelection()
    {
        // make sure we reload the current page, to check first display of the wikipage and attachment explorers
        switchToWikiEditor();
        switchToWysiwygEditor();
        String currentSpace = "Main";
        String currentPage = "WysiwygTest";
        // check the wikipage link dialog
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_WIKIPAGE));
        clickMenu(MENU_WIKIPAGE);
        waitForDialogToOpen();
        ensureStepIsLoaded("xExplorerPanel");
        assertEquals(currentSpace + "." + currentPage, getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + currentSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and " + "nobr=\""
            + currentPage + "\"]');");
        closeDialog();
        waitForDialogToClose();

        // check the attachment link dialog
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_ATTACHMENT));
        clickMenu(MENU_ATTACHMENT);
        waitForDialogToOpen();
        ensureStepIsLoaded("xExplorerPanel");
        assertEquals(currentSpace + "." + currentPage + "#Attachments", getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + currentSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and " + "nobr=\""
            + currentPage + "\"]');");
        typeInExplorerInput("XWiki.AdminSheet@photos.png");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"XWiki\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"AdminSheet\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and nobr=\"photos.png"
            + "\"]');");
        clickButtonWithText("Select");
        ensureStepIsLoaded("xLinkConfig");
        typeInInput("Label of the created link", "barfoo");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        // now type something and check second display of the dialog
        typeText("poof");
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_ATTACHMENT));
        clickMenu(MENU_ATTACHMENT);
        waitForDialogToOpen();
        ensureStepIsLoaded("xExplorerPanel");
        assertEquals(currentSpace + "." + currentPage + "#Attachments", getExplorerInputValue());
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cell\") and nobr=\"" + currentSpace
            + "\"]');");
        waitForCondition("selenium.isElementPresent('//td[contains(@class, \"cellSelected\") and " + "nobr=\""
            + currentPage + "\"]');");
        closeDialog();
        waitForDialogToClose();
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
