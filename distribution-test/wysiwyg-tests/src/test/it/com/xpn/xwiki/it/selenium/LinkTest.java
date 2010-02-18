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

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;
import com.xpn.xwiki.it.selenium.framework.XWikiExplorer;

public class LinkTest extends AbstractWysiwygTestCase
{
    public static final String MENU_LINK = "Link";

    public static final String MENU_WEB_PAGE = "Web Page...";

    public static final String MENU_EMAIL_ADDRESS = "Email Address...";

    public static final String MENU_WIKI_PAGE = "Wiki Page...";

    public static final String MENU_ATTACHMENT = "Attached File...";

    public static final String MENU_LINK_EDIT = "Edit Link...";

    public static final String MENU_LINK_REMOVE = "Remove Link";

    public static final String CURRENT_PAGE_TAB = "Current page";

    public static final String ALL_PAGES_TAB = "All pages";

    public static final String RECENT_PAGES_TAB = "My recent changes";

    public static final String SEARCH_TAB = "Search";

    public static final String STEP_EXPLORER = "xExplorerPanel";

    public static final String LABEL_INPUT_TITLE = "Type the label of the created link.";

    public static final String ERROR_MSG_CLASS = "xErrorMsg";

    public static final String ITEMS_LIST = "//div[contains(@class, 'xListBox')]";

    public static final String TREE_EXPLORER = "//div[contains(@class, 'xExplorer')]";

    public static final String FILE_UPLOAD_INPUT = "//input[contains(@class, 'gwt-FileUpload')]";

    /**
     * The object used to assert the state of the XWiki Explorer tree.
     */
    private final XWikiExplorer explorer = new XWikiExplorer(this);

    /**
     * Test the basic feature for adding a link to an existing page.
     */
    public void testCreateLinkToExistingPage()
    {
        String linkLabel = "foo";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");

        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        String selectedSpace = "Blog";
        String selectedPage = "News";

        explorer.lookupEntity(selectedSpace + "." + selectedPage);
        explorer.waitForPageSelected(selectedSpace, selectedPage);
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + selectedSpace + "." + selectedPage + "]]");
    }

    /**
     * Test the basic feature for adding a link to an existing space.
     */
    public void testCreateLinkToSpace()
    {
        String linkLabel = "foobar";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        String space = "Blog";
        String page = "WebHome";

        explorer.lookupEntity(space + "." + page);
        explorer.waitForPageSelected(space, page);

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + space + "." + page + "]]");
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
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);
        explorer.lookupEntity(space + "." + newPageName);
        explorer.waitForPageSelected(space, "New page...");

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + space + "." + newPageName + "]]");
    }

    /**
     * Test the basic feature for adding a link to a new page in a new space.
     * 
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-3511">XWIKI-3511</a>
     */
    public void testCreateLinkToNewPageInNewSpace()
    {
        String linkLabel = "bob";
        String newSpace = "Bob";
        String newPage = "Cat";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);
        explorer.lookupEntity(newSpace + "." + newPage);

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + newSpace + "." + newPage + "]]");
    }

    /**
     * Test the basic feature for adding a link to a web page.
     */
    public void testCreateLinkToWebPage()
    {
        String linkLabel = "xwiki";
        String url = "http://www.xwiki.org";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");

        openLinkDialog(MENU_WEB_PAGE);
        // ensure wizard step is loaded
        waitForStepToLoad("xLinkToUrl");
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + url + "]]");
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
        openLinkDialog(MENU_WEB_PAGE);
        // ensure wizard step is loaded
        waitForStepToLoad("xLinkToUrl");
        String newLabel = "xwiki rox";
        typeInInput(LABEL_INPUT_TITLE, newLabel);
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + newLabel + ">>" + url + "]]");
    }

    /**
     * Test the basic feature for adding a link to an email address.
     */
    public void testCreateLinkToEmailAddress()
    {
        String linkLabel = "carol";
        String email = "mailto:carol@xwiki.org";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_EMAIL_ADDRESS);

        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + email + "]]");
    }

    /**
     * Test adding a link to an email address but not without specifying the mailto: protocol.
     */
    public void testCreateLinkToEmailAddressWithoutMailto()
    {
        String linkLabel = "joe le taxi";
        String email = "joe@xwiki.org";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_EMAIL_ADDRESS);
        typeInInput("Email address", email);
        clickButtonWithText("Create Link");
        waitForDialogToClose();
        switchToSource();
        assertSourceText("[[" + linkLabel + ">>mailto:" + email + "]]");
    }

    /**
     * Test adding a link by typing the link label instead of selecting it.
     */
    public void testCreateLinkWithNewLabel()
    {
        String linkLabel = "xwiki";
        String linkURL = "www.xwiki.org";
        openLinkDialog(MENU_WEB_PAGE);

        typeInInput("Web page address", linkURL);
        typeInInput(LABEL_INPUT_TITLE, linkLabel);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>http://" + linkURL + "]]");
    }

    /**
     * Test that anchor label formatting is preserved.
     */
    public void testCreateLinkPreservesLabelFormatting()
    {
        // Select the bogus BR to overwrite it.
        selectAllContent();
        typeText("our");
        clickBoldButton();
        typeText("xwiki");
        clickBoldButton();
        typeText("rox");
        selectAllContent();
        openLinkDialog(MENU_WEB_PAGE);

        // test that the picked up label of the link is the right text
        assertEquals("ourxwikirox", getInputValue(LABEL_INPUT_TITLE));
        typeInInput("Web page address", "www.xwiki.org");
        clickButtonWithText("Create Link");

        waitForDialogToClose();
        switchToSource();
        assertSourceText("[[our**xwiki**rox>>http://www.xwiki.org]]");
    }

    /**
     * Test creating a link with some text around and then editing it. Test that the link type and parameters are
     * correctly read and that the wiki syntax is correctly generated.
     */
    public void testCreateThenEditLink()
    {
        // put everything in a paragraph because editing in body is sometimes parsed wrong
        applyStyleTitle1();
        applyStylePlainText();
        typeText("this is ");
        String linkLabel = "xwiki";
        String linkURL = "http://www.xwiki.com";
        String newLinkURL = "http://www.xwiki.org";

        openLinkDialog(MENU_WEB_PAGE);

        typeInInput("Web page address", linkURL);
        typeInInput(LABEL_INPUT_TITLE, linkLabel);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 5);
        triggerToolbarUpdate();

        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        // unlink here should only move the caret out
        clickMenu(MENU_LINK_REMOVE);
        typeText(" which rox");
        switchToSource();
        assertSourceText("this is [[" + linkLabel + ">>" + linkURL + "]] which rox");
        switchToWysiwyg();

        select("XWE.body.firstChild", 1, "XWE.body.firstChild.childNodes[1].firstChild", 5);

        openLinkDialog(MENU_LINK_EDIT);

        assertEquals(linkLabel, getInputValue(LABEL_INPUT_TITLE));
        assertEquals(linkURL, getInputValue("Web page address"));

        typeInInput("Web page address", newLinkURL);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("this is [[" + linkLabel + ">>" + newLinkURL + "]] which rox");
    }

    /**
     * Test creating and editing link around an image. Test that the displayed label is the alt text of the image and is
     * not editable, that the link parameters are correctly read, and that the wiki syntax is correctly generated.
     */
    public void testCreateAndEditLinkOnImage()
    {
        // Select the bogus BR to overwrite it.
        selectAllContent();

        // put everything in a paragraph as there are some whitespace trouble parsing outside the paragraph
        applyStyleTitle1();
        applyStylePlainText();

        clickMenu("Image");
        clickMenu("Insert Image...");

        waitForDialogToLoad();

        // switch to "all pages" tab
        clickTab(ALL_PAGES_TAB);

        String imageSpaceSelector = "//div[@class=\"xPageChooser\"]//select[2]";
        String imageSpace = "XWiki";
        waitForCondition("selenium.isElementPresent('" + imageSpaceSelector + "/option[@value=\"" + imageSpace
            + "\"]');");
        getSelenium().select(imageSpaceSelector, imageSpace);

        String imagePageSelector = "//div[@class=\"xPageChooser\"]//select[3]";
        String imagePage = "AdminSheet";
        waitForCondition("selenium.isElementPresent('" + imagePageSelector + "/option[@value=\"" + imagePage + "\"]');");
        getSelenium().select(imagePageSelector, imagePage);

        getSelenium().click("//div[@class=\"xPageChooser\"]//button[text()=\"Update\"]");

        String imageSelector = "//div[@class=\"xImagesSelector\"]//img[@title=\"presentation.png\"]";
        waitForCondition("selenium.isElementPresent('" + imageSelector + "');");
        getSelenium().click(imageSelector);

        clickButtonWithText("Select");
        waitForStepToLoad("xImageConfig");
        clickButtonWithText("Insert Image");

        waitForDialogToClose();

        // now add a link around this image
        String pageName = "Photos";
        String spaceName = "Blog";
        String newSpaceName = "Main";
        String newPageName = "Dashboard";

        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);
        explorer.lookupEntity(spaceName + "." + pageName);
        // wait for the space to get selected
        explorer.waitForPageSelected(spaceName, "New page...");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        assertEquals("presentation.png", getInputValue(LABEL_INPUT_TITLE));
        // check that the label is readonly
        assertElementPresent("//input[@title=\"" + LABEL_INPUT_TITLE + "\" and @disabled=\"\"]");

        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[[[image:XWiki.AdminSheet@presentation.png]]>>Blog.Photos]]");
        switchToWysiwyg();

        // move caret at the end and type some more
        moveCaret("XWE.body.firstChild", 1);
        typeText(" foo ");

        openLinkDialog(MENU_WEB_PAGE);
        typeInInput(LABEL_INPUT_TITLE, "bar");
        typeInInput("Web page address", "http://bar.myxwiki.org");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        // now go on and edit the image
        select("XWE.body.firstChild.firstChild", 0, "XWE.body.firstChild.firstChild", 1);
        openLinkDialog(MENU_LINK_EDIT);

        // check the explorer selection
        assertEquals(spaceName + "." + pageName, explorer.getSelectedEntityReference());
        explorer.waitForPageSelected(spaceName, "New page...");

        explorer.lookupEntity(newSpaceName + "." + newPageName);
        explorer.waitForPageSelected(newSpaceName, newPageName);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[[[image:XWiki.AdminSheet@presentation.png]]>>" + newSpaceName + "." + newPageName
            + "]] foo [[bar>>http://bar.myxwiki.org]]");
    }

    /**
     * Test that the link existence is detected correctly for a couple of cases of the selection around a link, and that
     * unlink executes correctly for these situations.
     */
    public void testDetectAndUnlinkSelectedAnchor()
    {
        switchToSource();
        setSourceText("foo [[bar>>http://xwiki.org]] [[far>>Main.WebHome]] [[alice>>Main.NewPage]] "
            + "[[carol>>mailto:carol@xwiki.org]] [[b**o**b>>http://xwiki.org]] blog webhome [[Blog.WebHome]] "
            + "[[image:XWiki.AdminSheet@presentation.png>>Blog.Photos]]");
        switchToWysiwyg();

        // put selection inside first text
        moveCaret("XWE.body.firstChild.firstChild", 2);
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertTrue(isMenuEnabled(MENU_WIKI_PAGE));
        assertTrue(isMenuEnabled(MENU_WEB_PAGE));
        assertTrue(isMenuEnabled(MENU_EMAIL_ADDRESS));
        clickMenu(MENU_LINK);

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
        assertFalse(isMenuEnabled(MENU_WEB_PAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_WIKI_PAGE));
        clickMenu(MENU_LINK);

        // set selection in two different links
        select("XWE.body.firstChild.childNodes[13].firstChild.firstChild", 4, "XWE.body.firstChild.childNodes[15]", 1);
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_LINK_EDIT));
        assertFalse(isMenuEnabled(MENU_LINK_REMOVE));
        assertFalse(isMenuEnabled(MENU_WEB_PAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_WIKI_PAGE));

        closeMenuContaining(MENU_WEB_PAGE);
        switchToSource();
        assertSourceText("foo bar far alice carol b**o**b blog webhome [[Blog.WebHome]] "
            + "[[image:XWiki.AdminSheet@presentation.png>>Blog.Photos]]");
    }

    /**
     * Test editing a link which is the single text in a list item. This case is special because the delete command is
     * invoked upon replacing the link, which causes clean up of the list.
     */
    public void testEditLinkInList()
    {
        switchToSource();
        setSourceText("* one\n* [[two>>http://www.xwiki.com]]\n** three");
        switchToWysiwyg();

        // now edit the link in the second list item
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild.firstChild", 1);

        openLinkDialog(MENU_LINK_EDIT);

        // now check if the dialog has loaded correctly
        waitForStepToLoad("xLinkToUrl");
        assertEquals("two", getInputValue(LABEL_INPUT_TITLE));
        assertEquals("http://www.xwiki.com", getInputValue("Web page address"));
        typeInInput("Web page address", "http://www.xwiki.org");

        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("* one\n* [[two>>http://www.xwiki.org]]\n** three");
    }

    /**
     * Test that the link dialogs are correctly validated and alerts are displayed when mandatory fields are not filled
     * in.
     */
    public void testValidationOnLinkInsert()
    {
        // try to create a link to an existing page without a label
        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        String space = "Main";
        String page = "WebHome";

        explorer.lookupEntity(space + "." + page);
        explorer.waitForPageSelected(space, page);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        // try to create link without filling in the label
        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkConfig");

        // fill in the label and create link
        typeInInput(LABEL_INPUT_TITLE, "foo");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[foo>>" + space + "." + page + "]]");

        // clean up
        setSourceText("");
        switchToWysiwyg();

        // now try again with a new page link
        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        space = "Main";
        page = "NewPage";

        explorer.lookupEntity(space + "." + page);
        explorer.waitForPageSelected(space, "New page...");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkConfig");

        // fill in the label and create link
        typeInInput(LABEL_INPUT_TITLE, "foo");
        clickButtonWithText("Create Link");

        switchToSource();
        assertSourceText("[[foo>>" + space + "." + page + "]]");

        // clean up
        setSourceText("");
        switchToWysiwyg();

        // now create a link to a web page
        openLinkDialog(MENU_WEB_PAGE);

        // test that initially 2 errors are displayed
        clickButtonWithText("Create Link");
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkToUrl");
        assertFieldErrorIsPresentInStep("The web page address was not set", "//input[@title='Web page address']",
            "xLinkToUrl");

        typeInInput("Web page address", "http://www.xwiki.org");
        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkToUrl");
        // now the web page address error is no longer there
        assertFieldErrorIsNotPresent("The web page address was not set", "//input[@title='Web page address']");

        // fill in the label and create link
        typeInInput(LABEL_INPUT_TITLE, "xwiki");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[xwiki>>http://www.xwiki.org]]");

        // clean up
        setSourceText("");
        switchToWysiwyg();

        // now create a link to an email page
        openLinkDialog(MENU_EMAIL_ADDRESS);

        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkToUrl");
        assertFieldErrorIsPresentInStep("The email address was not set", "//input[@title='Email address']",
            "xLinkToUrl");

        typeInInput(LABEL_INPUT_TITLE, "alice");
        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The email address was not set", "//input[@title='Email address']",
            "xLinkToUrl");
        assertFieldErrorIsNotPresent("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']");

        typeInInput("Email address", "alice@wonderla.nd");
        clickButtonWithText("Create Link");

        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[alice>>mailto:alice@wonderla.nd]]");
    }

    /**
     * Test that the link button is not enabled when the selection contains some block elements.
     */
    public void testCannotCreateLinkAroundBlockElements()
    {
        applyStyleTitle1();
        applyStylePlainText();
        typeText("foo");
        typeEnter();
        typeText("bar");
        assertContent("<p>foo</p><p>bar<br></p>");
        select("XWE.body.firstChild.firstChild", 2, "XWE.body.childNodes[1].firstChild", 2);
        clickMenu(MENU_LINK);
        assertFalse(isMenuEnabled(MENU_WEB_PAGE));
        assertFalse(isMenuEnabled(MENU_WIKI_PAGE));
        assertFalse(isMenuEnabled(MENU_EMAIL_ADDRESS));
        assertFalse(isMenuEnabled(MENU_ATTACHMENT));
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
        selectNodeContents("XWE.body.firstChild");

        openLinkDialog(MENU_WIKI_PAGE);
        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        String selectedSpace = "Blog";
        String selectedPage = "News";
        String changedSpace = "Main";
        String changedPage = "RecentChanges";

        explorer.lookupEntity(selectedSpace + "." + selectedPage);
        explorer.waitForPageSelected(selectedSpace, selectedPage);
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");

        // now hit previous
        clickButtonWithText("Previous");
        // wait for tree to load
        waitForStepToLoad("xExplorerPanel");
        // make sure input and selection in the tree reflect previously inserted values
        assertEquals(selectedSpace + "." + selectedPage, explorer.getSelectedEntityReference());
        explorer.waitForPageSelected(selectedSpace, selectedPage);

        // and now change it
        explorer.lookupEntity(changedSpace + "." + changedPage);
        explorer.waitForPageSelected(changedSpace, changedPage);
        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + changedSpace + "." + changedPage + "]]");
    }

    /**
     * Test the basic feature of adding a link to an attached file with the label from the selected text.
     */
    public void testCreateLinkToAttachment()
    {
        String linkLabel = "boo";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");

        openLinkDialog(MENU_ATTACHMENT);

        // click the tree explorer tab
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad("xExplorerPanel");

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "lquo.gif";

        explorer.lookupEntity(attachSpace + "." + attachPage + "@" + attachment);
        explorer.waitForAttachmentSelected(attachSpace, attachPage, attachment);

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment + "]]");
    }

    /**
     * Test the basic feature of adding a link to an attached file, configuring its parameters in the parameter panel.
     */
    public void testCreateLinkToAttachmentWithParameters()
    {
        String linkLabel = "rquo";
        String linkTooltip = "Right quote image";
        openLinkDialog(MENU_ATTACHMENT);

        // click the tree explorer tab
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad("xExplorerPanel");

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "rquo.gif";

        explorer.lookupEntity(attachSpace + "." + attachPage + "@" + attachment);
        explorer.waitForAttachmentSelected(attachSpace, attachPage, attachment);

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        // fill in the link label and title
        typeInInput(LABEL_INPUT_TITLE, linkLabel);
        typeInInput("Type the tooltip of the created link, which appears when mouse is over the link.", linkTooltip);

        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment
            + "||title=\"" + linkTooltip + "\"]]");
    }

    /**
     * Test that he creation of a link to an attached file is validated correctly.
     */
    public void testValidationOnLinkToAttachment()
    {
        String linkLabel = "boo";

        openLinkDialog(MENU_ATTACHMENT);

        // click the tree explorer tab
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad("xExplorerPanel");

        String attachSpace = "Main";
        String attachPage = "RecentChanges";
        String attachment = "lquo.gif";

        // get an error from not inserting the attachment name
        explorer.lookupEntity(attachSpace + "." + attachPage);
        explorer.waitForPageSelected(attachSpace, attachPage);

        clickButtonWithText("Select");

        assertFieldErrorIsPresentInStep("No attachment was selected", TREE_EXPLORER, "xExplorerPanel");

        // type correct file reference
        explorer.lookupEntity(attachSpace + "." + attachPage + "@" + attachment);
        explorer.waitForAttachmentSelected(attachSpace, attachPage, attachment);

        clickButtonWithText("Select");
        // make sure the existing page config parameters are loaded
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");

        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkConfig");

        typeInInput(LABEL_INPUT_TITLE, linkLabel);
        clickButtonWithText("Create Link");

        // wait for the link dialog to close
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>attach:" + attachSpace + "." + attachPage + "@" + attachment + "]]");
    }

    /**
     * Test editing an existing link to an attachment
     */
    public void testEditLinkToAttachment()
    {
        switchToSource();
        setSourceText("[[foobar>>attach:Main.RecentChanges@lquo.gif]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals("Main.RecentChanges@lquo.gif", explorer.getSelectedEntityReference());
        explorer.waitForAttachmentSelected("Main", "RecentChanges", "lquo.gif");
        // and edit it now
        explorer.lookupEntity("XWiki.AdminSheet@export.png");
        explorer.waitForAttachmentSelected("XWiki", "AdminSheet", "export.png");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[foobar>>attach:XWiki.AdminSheet@export.png]]");
    }

    /**
     * Test that editing a link with custom parameters set from wiki syntax preserves the parameters of the link.
     * 
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-3568">XWIKI-3568</a>
     */
    public void testEditLinkPreservesCustomParameters()
    {
        switchToSource();
        setSourceText("[[foobar>>Main.RecentChanges||class=\"foobarLink\"]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);

        waitForStepToLoad("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals("Main.RecentChanges", explorer.getSelectedEntityReference());
        explorer.waitForPageSelected("Main", "RecentChanges");
        // and edit it now
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, "barfoo");
        typeInInput("Type the tooltip of the created link, which appears when mouse is over the link.", "Foo and bar");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[barfoo>>Main.RecentChanges||class=\"foobarLink\" title=\"Foo and bar\"]]");
    }

    /**
     * Test creating a link to open in a new page.
     */
    public void testCreateLinkToOpenInNewWindow()
    {
        String linkLabel = "XWiki rox";
        String url = "http://www.xwiki.org";

        openLinkDialog(MENU_WEB_PAGE);
        // ensure wizard step is loaded
        waitForStepToLoad("xLinkToUrl");
        typeInInput("Web page address", url);
        typeInInput(LABEL_INPUT_TITLE, linkLabel);
        // open in new window
        getSelenium().check("//div[contains(@class, 'xLinkConfig')]//span[contains(@class, 'gwt-CheckBox')]/input");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + url + "||rel=\"__blank\"]]");
        switchToWysiwyg();

        // now edit
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 4);
        openLinkDialog(MENU_LINK_EDIT);

        assertEquals(linkLabel, getInputValue(LABEL_INPUT_TITLE));
        assertEquals(url, getInputValue("Web page address"));
        assertTrue(isChecked("//div[contains(@class, 'xLinkConfig')]//span[contains(@class, 'gwt-CheckBox')]/input"));
    }

    /**
     * Test that quotes in link tooltips are correctly escaped.
     * 
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-3569">XWIKI-3569</a>
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-3569">XWIKI-3575</a>
     */
    public void testQuoteInLinkTooltip()
    {
        String linkLabel = "rox";
        String url = "http://www.xwiki.org";
        String tooltip = "our xwiki \"rox\"";
        String tooltipTitle = "Type the tooltip of the created link, which appears when mouse is over the link.";
        typeText(linkLabel);
        selectNodeContents("XWE.body.firstChild");
        openLinkDialog(MENU_WEB_PAGE);
        // ensure wizard step is loaded
        waitForStepToLoad("xLinkToUrl");
        typeInInput(tooltipTitle, tooltip);
        typeInInput("Web page address", url);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + linkLabel + ">>" + url + "||title=\"our xwiki ~\"rox~\"\"]]");
        switchToWysiwyg();

        // now test the link is correctly parsed back
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xLinkToUrl");
        assertEquals(tooltip, getInputValue(tooltipTitle));
    }

    /**
     * Test that the default selection is set to the current page when opening the wizard to create a link to a wiki
     * page.
     */
    public void testDefaultWikipageExplorerSelection()
    {
        // make sure this page is saved so that the tree can load the reference to it
        clickEditSaveAndContinue();

        String currentSpace = this.getClass().getSimpleName();
        String currentPage = getName();

        String newSpace = "XWiki";
        String newPage = "AdminSheet";
        // check the wikipage link dialog
        openLinkDialog(MENU_WIKI_PAGE);

        // check the recent changes selection
        waitForStepToLoad("xSelectorAggregatorStep");

        // test that the default open tab is the recent changes tab
        assertElementPresent("//div[contains(@class, 'gwt-TabBarItem-selected')]/div[.='" + RECENT_PAGES_TAB + "']");

        waitForStepToLoad("xPagesSelector");
        // test that the selected element is the new page element
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");

        // get the all pages tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad(STEP_EXPLORER);

        assertEquals("xwiki:" + currentSpace + "." + currentPage, explorer.getSelectedEntityReference());
        explorer.waitForPageSelected(currentSpace, currentPage);
        explorer.lookupEntity(newSpace + "." + newPage);
        explorer.waitForPageSelected(newSpace, newPage);
        closeDialog();
        waitForDialogToClose();

        // now type something and check second display of the dialog, that it stays to the last inserted page
        typeText("poof");
        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xExplorerPanel");
        assertEquals(newSpace + "." + newPage, explorer.getSelectedEntityReference());
        explorer.waitForPageSelected(newSpace, newPage);
        closeDialog();
        waitForDialogToClose();
    }

    /**
     * Test the creation of a link to a recent page (the current page, saved).
     */
    public void testCreateLinkToRecentPage()
    {
        // make sure this page is saved so that the recent pages can load reference to it
        clickEditSaveAndContinue();

        String currentPage = "xwiki:" + getClass().getSimpleName() + "." + getName();
        String label = "barfoo";

        openLinkDialog(MENU_WIKI_PAGE);

        // check the recent changes selection
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesSelector");
        // test that the selected element is the new page element
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");

        // select the current page
        getSelenium()
            .click(
                "//div[contains(@class, 'xPagesSelector')]//div[contains(@class, 'gwt-Label') and .='" + currentPage
                    + "']");

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + getName() + "]]");
    }

    /**
     * Test the creation of a link to a new page in the current space, from the default tab in the link dialog.
     */
    public void testCreateLinkToNewPageInCurrentSpace()
    {
        String newPageName = "NewPage";
        String label = "new page label";

        openLinkDialog(MENU_WIKI_PAGE);

        // check the recent changes selection
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesSelector");
        // test that the selected element is the new page element
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkToNewPage");
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", newPageName);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + newPageName + "]]");
    }

    /**
     * Tests the default selection for the search tab.
     */
    public void testDefaultSearchSelection()
    {
        // check the wikipage link dialog
        openLinkDialog(MENU_WIKI_PAGE);

        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(SEARCH_TAB);
        waitForStepToLoad("xPagesSearch");
        // test that the selected element is the new page element
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");

        closeDialog();
        waitForDialogToClose();
    }

    /**
     * Test adding a link to a page from the search tab.
     */
    public void testCreateLinkToSearchedPage()
    {
        String searchString = "Main.WebHome";
        String expectedPage = "Main.WebHome";
        String label = "foobar";

        // check the wikipage link dialog
        openLinkDialog(MENU_WIKI_PAGE);

        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(SEARCH_TAB);
        waitForStepToLoad("xPagesSearch");
        // perform a search
        typeInInput("Type a keyword to search for a wiki page", searchString);
        clickButtonWithText("Search");
        String selectedPageLocator =
            "//div[contains(@class, 'xListItem')]//div[contains(@class, 'gwt-Label') and .='xwiki:" + expectedPage
                + "']";
        // wait for the element to load in the list
        waitForElement(selectedPageLocator);
        // check selection on the loaded list

        // select the current page
        getSelenium().click(selectedPageLocator);

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + expectedPage + "]]");
    }

    /**
     * Tests the creation of a link in the current space from the search pages dialog.
     */
    public void testCreateLinkToNewPageInCurrentSpaceFromSearch()
    {
        String newPageName = "AnotherNewPage";
        String label = "foo new bar";

        openLinkDialog(MENU_WIKI_PAGE);

        // check the recent changes selection
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(SEARCH_TAB);
        waitForStepToLoad("xPagesSearch");
        // test that the selected element is the new page element
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkToNewPage");
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", newPageName);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + newPageName + "]]");
    }

    /**
     * Tests that the link attachments step is loaded on the current page attachments every time it's displayed.
     */
    public void testDefaultAttachmentSelectorSelection()
    {
        // make sure this page is saved so that the tree can load the reference to it
        clickEditSaveAndContinue();

        String currentSpace = this.getClass().getSimpleName();
        String currentPage = getName();

        // check the wikipage link dialog
        openLinkDialog(MENU_ATTACHMENT);

        waitForStepToLoad("xAttachmentsSelector");
        // test that there is a "new attachment" option
        assertElementPresent("//div[contains(@class, \"xNewFilePreview\")]");

        clickTab(ALL_PAGES_TAB);
        assertEquals("xwiki:" + currentSpace + "." + currentPage + "#Attachments", explorer
            .getSelectedEntityReference());
        explorer.waitForAttachmentsSelected(currentSpace, currentPage);
        closeDialog();
        waitForDialogToClose();

        // now type something and check second display of the dialog, that it opens on the current page
        typeText("poof");
        openLinkDialog(MENU_ATTACHMENT);
        waitForStepToLoad("xAttachmentsSelector");
        // test that there is a "new attachment" option
        assertElementPresent("//div[contains(@class, \"xNewFilePreview\")]");
        closeDialog();
        waitForDialogToClose();
    }

    /**
     * Test that a relative link is correctly edited.
     * 
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-3676">XWIKI-3676</a>
     */
    public void testEditRelativeLink()
    {
        // Edit a page in the Main space because we know pages that already exit there.
        String currentSpace = "Main";
        open(currentSpace, getName(), "edit", "editor=wysiwyg");
        waitForEditorToLoad();

        String pageToLinkTo = "Dashboard";
        switchToSource();
        setSourceText("[[the main page>>" + pageToLinkTo + "]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);

        waitForStepToLoad("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals(pageToLinkTo, explorer.getSelectedEntityReference());
        explorer.waitForPageSelected(currentSpace, pageToLinkTo);
        // and edit it now
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, "the Dashboard");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[the Dashboard>>" + pageToLinkTo + "]]");
    }

    /**
     * Test that a relative link to a file attachment is correctly edited
     */
    public void testEditRelativeLinkToAttachment()
    {
        // Edit a page in the Main space because we know pages that already exit there and have attachments.
        String currentSpace = "Main";
        open(currentSpace, getName(), "edit", "editor=wysiwyg");
        waitForEditorToLoad();

        String pageToLinkTo = "RecentChanges";
        String fileToLinkTo = "lquo.gif";

        switchToSource();
        setSourceText("[[left quote>>attach:" + pageToLinkTo + "@" + fileToLinkTo + "]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xExplorerPanel");
        // assert the content of the suggest and the position on the tree
        assertEquals(pageToLinkTo + "@" + fileToLinkTo, explorer.getSelectedEntityReference());
        explorer.waitForAttachmentSelected(currentSpace, pageToLinkTo, fileToLinkTo);

        // check the current page step is correctly loaded when we switch to it
        clickTab(CURRENT_PAGE_TAB);
        waitForStepToLoad("xAttachmentsSelector");
        // test that there is a "new attachment" option
        assertElementPresent("//div[contains(@class, \"xNewFilePreview\")]");

        // switch back to the tree
        clickTab(ALL_PAGES_TAB);
        waitForStepToLoad("xExplorerPanel");
        // test that the position in the tree was preserved
        assertEquals(pageToLinkTo + "@" + fileToLinkTo, explorer.getSelectedEntityReference());
        explorer.waitForAttachmentSelected(currentSpace, pageToLinkTo, fileToLinkTo);

        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, "quote left");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[quote left>>attach:" + pageToLinkTo + "@" + fileToLinkTo + "]]");

        // ensure this opens on the current page selector
        setSourceText("[[attach.png>>attach:attach.png]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xAttachmentsSelector");
        // make sure no option is selected
        assertElementNotPresent("//div[contains(@class, \"xListItem-selected\")]");
        // test that there is a "new attachment" option
        assertElementPresent("//div[contains(@class, \"xNewFilePreview\")]");
        closeDialog();
        waitForDialogToClose();
    }

    /**
     * Test that when no option is selected in the current page attachments selector and a "select" is tried, an alert
     * is displayed to show the error.
     */
    public void testValidationOnCurrentPageAttachmentsSelector()
    {
        switchToSource();
        setSourceText("[[left quote>>attach:Main.RecentChanges@rquo.gif]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xExplorerPanel");
        assertEquals("Main.RecentChanges@rquo.gif", explorer.getSelectedEntityReference());
        explorer.waitForAttachmentSelected("Main", "RecentChanges", "rquo.gif");

        explorer.lookupEntity("xwiki:Main.RecentChanges@lquo.gif");
        explorer.waitForNode("lquo.gif", true);

        clickTab(CURRENT_PAGE_TAB);

        waitForStepToLoad("xAttachmentsSelector");
        // make sure no option is selected
        assertElementNotPresent("//div[contains(@class, \"xListItem-selected\")]");

        clickButtonWithText("Select");

        assertFieldErrorIsPresentInStep("No attachment was selected", ITEMS_LIST, "xAttachmentsSelector");

        // select the new file option
        getSelenium().click("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, 'xNewFilePreview')]");
        clickButtonWithText("Select");
        waitForStepToLoad("xUploadPanel");
        assertFieldErrorIsNotPresentInStep("xUploadPanel");
        clickButtonWithText("Upload");
        assertFieldErrorIsPresentInStep("The file path was not set", FILE_UPLOAD_INPUT, "xUploadPanel");

        closeDialog();
    }

    /**
     * Test that editing a link and not changing its location preserves a full reference and does not transform it into
     * a relative one.
     */
    public void testEditLinkPreservesFullReferences()
    {
        switchToSource();
        setSourceText("[[bob>>Main.RecentChanges]] [[alice>>Main.NewPage]] "
            + "[[carol>>attach:Main.RecentChanges@lquo.gif]]");
        switchToWysiwyg();

        // first link, a link to an existing page
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        openLinkDialog(MENU_LINK_EDIT);

        waitForStepToLoad("xExplorerPanel");
        assertEquals("Main.RecentChanges", explorer.getSelectedEntityReference());
        explorer.waitForPageSelected("Main", "RecentChanges");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        // second link, a link to a new page
        moveCaret("XWE.body.firstChild.childNodes[2].firstChild", 2);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xExplorerPanel");
        assertEquals("Main.NewPage", explorer.getSelectedEntityReference());
        explorer.waitForPageSelected("Main", "New page...");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        // third link, a link to an existing file
        moveCaret("XWE.body.firstChild.childNodes[4].firstChild", 2);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xExplorerPanel");
        assertEquals("Main.RecentChanges@lquo.gif", explorer.getSelectedEntityReference());
        explorer.waitForAttachmentSelected("Main", "RecentChanges", "lquo.gif");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[bob>>Main.RecentChanges]] [[alice>>Main.NewPage]] "
            + "[[carol>>attach:Main.RecentChanges@lquo.gif]]");
    }

    /**
     * Tests that choosing remove link when the caret is inside an empty link removes the empty link instead of putting
     * the caret outside the link.
     */
    public void testUnlinkInEmptyLink()
    {
        switchToSource();
        // NOTE: The link label must be styled because otherwise the link is deleted when we delete its label.
        setSourceText("[[**1**>>http://www.xwiki.org]]");
        switchToWysiwyg();
        // Move the caret at the start of the link.
        moveCaret("XWE.body.firstChild.firstChild.firstChild.firstChild", 0);
        // Delete the link label.
        typeDelete();
        // We should have an empty link at this point.
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(MENU_LINK_REMOVE));
        clickMenu(MENU_LINK_REMOVE);
        typeText("2");
        // Check the result.
        switchToSource();
        // NOTE: The style is lost but that's a small issue.
        assertSourceText("2");
    }

    /**
     * Test that the error markers are removed for the following displays of an external link step with an error: either
     * the dialog with the error is closed by canceling or by selecting a correct value and continuing, upon return to
     * the error dialog, the error message and markers are now hidden.
     */
    public void testErrorIsHiddenOnNextDisplayOfExternalLink()
    {
        // for a web page
        openLinkDialog(MENU_WEB_PAGE);
        typeInInput("Web page address", "http://www.xwiki.org");
        clickButtonWithText("Create Link");
        // check that an error is present
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkToUrl");
        // cancel everything
        closeDialog();
        // now open a new one
        openLinkDialog(MENU_WEB_PAGE);
        // check that the error is no longer there
        assertElementNotPresent(ERROR_MSG_CLASS);
        closeDialog();

        // for an email
        openLinkDialog(MENU_EMAIL_ADDRESS);
        typeInInput("Email address", "xwiki@xwiki.com");
        clickButtonWithText("Create Link");
        // check that an error is present
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[@title='" + LABEL_INPUT_TITLE
            + "']", "xLinkToUrl");
        // cancel everything
        closeDialog();
        // now open a new one
        openLinkDialog(MENU_EMAIL_ADDRESS);
        // check that the error is no longer there
        assertElementNotPresent(ERROR_MSG_CLASS);
        closeDialog();
    }

    /**
     * Test that the error markers are removed for the following displays of an attachment link step with an error:
     * either the dialog with the error is closed by canceling or by selecting a correct value and continuing, upon
     * return to the error dialog, the error message and markers are now hidden.
     */
    public void testErrorIsHiddenOnNextDisplayOfAttachmentLink()
    {
        // test that no selection in the current page attachment generates an exception: edit a non-existent file link
        switchToSource();
        setSourceText("[[non-existent-attachment.png>>attach:non-existent-attachment.png]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 4);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xAttachmentsSelector");
        // assert that no selection is in the list;
        assertElementNotPresent("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, \"xListItem-selected\")]");
        // try to hit select and wait for the validation message
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No attachment was selected", ITEMS_LIST, "xAttachmentsSelector");
        closeDialog();
        openLinkDialog(MENU_LINK_EDIT);
        assertFieldErrorIsNotPresentInStep("xAttachmentsSelector");
        // get an error and then go to the next step to come back after
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No attachment was selected", ITEMS_LIST, "xAttachmentsSelector");
        // make a selection and check that on the previous button the current page selector dialog will no longer show
        // an error
        getSelenium().click("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, 'xNewFilePreview')]");
        clickButtonWithText("Select");
        waitForStepToLoad("xUploadPanel");
        // now hit previous and check that the error is no longer there
        clickButtonWithText("Previous");
        assertFieldErrorIsNotPresentInStep("xAttachmentsSelector");
        // FIXME: should check that the selection is the correct one (new page), but it's not
        closeDialog();
        resetContent();
        // get an error at the upload step and check that it's hidden on next display
        openLinkDialog(MENU_ATTACHMENT);
        waitForStepToLoad("xAttachmentsSelector");
        getSelenium().click("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, 'xNewFilePreview')]");
        clickButtonWithText("Select");
        waitForStepToLoad("xUploadPanel");
        clickButtonWithText("Upload");
        // get an error
        assertFieldErrorIsPresentInStep("The file path was not set", FILE_UPLOAD_INPUT, "xUploadPanel");
        // back, next and the error should be gone
        clickButtonWithText("Previous");
        // FIXME: should not redo selection here, it should be preserved
        getSelenium().click("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, 'xNewFilePreview')]");
        clickButtonWithText("Select");
        assertFieldErrorIsNotPresentInStep("xUploadPanel");
        // get the error again to check that closing it and re displaying this step makes it go away
        clickButtonWithText("Upload");
        assertFieldErrorIsPresentInStep("The file path was not set", FILE_UPLOAD_INPUT, "xUploadPanel");
        closeDialog();
        // same and check the error is no longer present
        openLinkDialog(MENU_ATTACHMENT);
        waitForStepToLoad("xAttachmentsSelector");
        getSelenium().click("//div[@class=\"xAttachmentsSelector\"]//div[contains(@class, 'xNewFilePreview')]");
        clickButtonWithText("Select");
        assertFieldErrorIsNotPresentInStep("xUploadPanel");
    }

    /**
     * Test that the error markers are removed for the following displays of a wiki page link step with an error: either
     * the dialog with the error is closed by canceling or by selecting a correct value and continuing, upon return to
     * the error dialog, the error message and markers are now hidden.
     */
    public void testErrorIsHiddenOnNextDisplayOfWikipageLink()
    {
        // 1/ get an error on the current page tab then select new page, next, previous => error is not displayed
        // anymore, close. Open, get error again, close dialog. Open back, error not displayed anymore edit link to new
        // page in new space, to be sure there's no selection in RecentChanges nor in Search
        switchToSource();
        setSourceText("[[new page>>NewSpace.NewPage]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 4);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(RECENT_PAGES_TAB);
        waitForStepToLoad("xPagesRecent");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No page was selected", ITEMS_LIST, "xPagesRecent");
        getSelenium().click("//div[contains(@class, 'xPagesRecent')]//div[contains(@class, 'xNewPagePreview')]");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkToNewPage");
        clickButtonWithText("Previous");
        assertFieldErrorIsNotPresentInStep("xPagesRecent");
        closeDialog();
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(RECENT_PAGES_TAB);
        waitForStepToLoad("xPagesRecent");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No page was selected", ITEMS_LIST, "xPagesRecent");
        closeDialog();
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(RECENT_PAGES_TAB);
        assertFieldErrorIsNotPresentInStep("xPagesRecent");
        closeDialog();
        resetContent();

        // TODO: 2/ should run the same scenario for the search page tab but

        // 3/ get an error on the new page step, fix it, go to previous, next => error not displayed anymore. Get
        // another error, go next, previous, error should not be there anymore. Get another error, close dialog. Open
        // again, get there, the error should not be displayed.
        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesRecent");
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkToNewPage");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("The name of the new page was not set", "//input", "xLinkToNewPage");
        clickButtonWithText("Previous");
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesRecent");
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");
        clickButtonWithText("Select");
        // error not present on coming back from previous
        assertFieldErrorIsNotPresentInStep("xLinkToNewPage");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("The name of the new page was not set", "//input", "xLinkToNewPage");
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", "NewPage");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Previous");
        // error not present when coming back from next
        assertFieldErrorIsNotPresentInStep("xLinkToNewPage");
        // check the content of the field
        assertEquals("NewPage", getSelenium().getValue("//div[contains(@class, 'xLinkToNewPage')]//input"));
        // get error again
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", "");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("The name of the new page was not set", "//input", "xLinkToNewPage");
        closeDialog();
        // open again, check the error is not still there
        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesRecent");
        assertElementPresent("//div[contains(@class, 'xListItem-selected')]/div[contains(@class, 'xNewPagePreview')]");
        clickButtonWithText("Select");
        // error not present when re-creating a link
        assertFieldErrorIsNotPresentInStep("xLinkToNewPage");
        closeDialog();
        resetContent();

        // 4/ get to the link config and get an error on the label -> previous, next, error should not be there anymore.
        // close everything, open again, error should not be there anymore. get error, fix it, add the link, on new
        // dialog error should not be there anymore
        switchToSource();
        setSourceText("[[the home>>Main.WebHome]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 4);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xExplorerPanel");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, "");
        clickButtonWithText("Create Link");
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[position() = 1]",
            "xLinkConfig");
        // previous, next => error is not present
        clickButtonWithText("Previous");
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xExplorerPanel");
        clickButtonWithText("Select");
        assertFieldErrorIsNotPresentInStep("xLinkConfig");
        // error, again, to close this time
        typeInInput(LABEL_INPUT_TITLE, "");
        clickButtonWithText("Create Link");
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[position() = 1]",
            "xLinkConfig");
        closeDialog();
        // now again, check that the error is no longer there
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xExplorerPanel");
        clickButtonWithText("Select");
        assertFieldErrorIsNotPresentInStep("xLinkConfig");
        // get an error
        typeInInput(LABEL_INPUT_TITLE, "");
        clickButtonWithText("Create Link");
        assertFieldErrorIsPresentInStep("The label of the link cannot be empty", "//input[position() = 1]",
            "xLinkConfig");
        // now go ahead, edit the link
        typeInInput(LABEL_INPUT_TITLE, "PageNew");
        clickButtonWithText("Create Link");
        // now open again, check error is not there anymore
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xExplorerPanel");
        clickButtonWithText("Select");
        assertFieldErrorIsNotPresentInStep("xLinkConfig");
        closeDialog();

        // 5/ get to the tree explorer, don't select any page, get an error. Fill in, next, previous -> error is hidden.
        // Get error again, close. Open and error is hidden
        switchToSource();
        setSourceText("[[the blog>>Blog.WebHome]]");
        switchToWysiwyg();
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 4);
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xExplorerPanel");
        explorer.waitForPageSelected("Blog", "WebHome");
        explorer.lookupEntity("");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No page was selected", TREE_EXPLORER, "xExplorerPanel");
        explorer.lookupEntity("Blog.WebHome");
        explorer.waitForPageSelected("Blog", "WebHome");
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        clickButtonWithText("Previous");
        // The previously selected page should still be selected.
        explorer.waitForPageSelected("Blog", "WebHome");
        assertFieldErrorIsNotPresentInStep("xExplorerPanel");
        explorer.lookupEntity("");
        clickButtonWithText("Select");
        assertFieldErrorIsPresentInStep("No page was selected", TREE_EXPLORER, "xExplorerPanel");
        closeDialog();
        openLinkDialog(MENU_LINK_EDIT);
        waitForStepToLoad("xSelectorAggregatorStep");
        assertFieldErrorIsNotPresentInStep("xExplorerPanel");
        closeDialog();
    }

    /**
     * Test fast navigation for adding a link to an attachment: double click and enter in the list of attachments
     * advance to the next step.
     */
    public void testFastNavigationToSelectAttachment()
    {
        // can't test but the current page attachment selector, the tree doesn't receive the click events
        // double click
        openLinkDialog(MENU_ATTACHMENT);
        waitForStepToLoad("xAttachmentsSelector");
        getSelenium().click("//div[contains(@class, 'xListItem')]//div[contains(@class, 'xNewFilePreview')]");
        getSelenium().doubleClick("//div[contains(@class, 'xListItem')]//div[contains(@class, 'xNewFilePreview')]");
        waitForStepToLoad("xUploadPanel");
        closeDialog();

        // enter
        openLinkDialog(MENU_ATTACHMENT);
        waitForStepToLoad("xAttachmentsSelector");
        getSelenium().click("//div[contains(@class, 'xListItem')]//div[contains(@class, 'xNewFilePreview')]");
        getSelenium().keyUp(ITEMS_LIST, "\\13");
        waitForStepToLoad("xUploadPanel");
        closeDialog();
    }

    /**
     * Test fast navigation for adding a link to a recent page: double click and enter on a page advance to the next
     * step.
     */
    public void testFastNavigationToSelectRecentPage()
    {
        // 1. link to existing page, double click
        // make sure this page is saved so that the recent pages can load reference to it
        clickEditSaveAndContinue();
        String currentPage = "xwiki:" + this.getClass().getSimpleName() + "." + getName();
        String label = "barfoo";
        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xPagesRecent");
        getSelenium()
            .click(
                "//div[contains(@class, 'xPagesSelector')]//div[contains(@class, 'gwt-Label') and .='" + currentPage
                    + "']");
        getSelenium()
            .doubleClick(
                "//div[contains(@class, 'xPagesSelector')]//div[contains(@class, 'gwt-Label') and .='" + currentPage
                    + "']");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + getName() + "]]");

        setSourceText("");
        switchToWysiwyg();

        // 2. link to new page in current space, with enter
        String newPageName = "NewPage";
        label = "foobar";

        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xSelectorAggregatorStep");
        waitForStepToLoad("xPagesRecent");
        // select the current page
        getSelenium().click("//div[contains(@class, 'xListItem')]/div[contains(@class, 'xNewPagePreview')]");
        getSelenium().keyUp(ITEMS_LIST, "\\13");
        waitForStepToLoad("xLinkToNewPage");
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", newPageName);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + newPageName + "]]");
    }

    /**
     * Test fast navigation for adding a link to a searched for page: double click and enter on a page advance to the
     * next step.
     */
    public void testFastNavigationToSelectSearchedPage()
    {
        // 1. link to existing page, enter
        String searchString = "Main.WebHome";
        String expectedPage = "Main.WebHome";
        String label = "foobar";

        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(SEARCH_TAB);
        waitForStepToLoad("xPagesSearch");
        typeInInput("Type a keyword to search for a wiki page", searchString);
        clickButtonWithText("Search");
        // wait for desired page to load
        String selectedPageLocator =
            "//div[contains(@class, 'xPagesSelector')]//div[contains(@class, 'gwt-Label') and .='xwiki:" + expectedPage
                + "']";
        // wait for the element to load in the list
        waitForElement(selectedPageLocator);
        getSelenium().click(selectedPageLocator);
        getSelenium().keyUp(ITEMS_LIST, "\\13");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + expectedPage + "]]");

        setSourceText("");
        switchToWysiwyg();
        // 2. link to a new page, double click
        String newPageName = "PageNew";
        label = "barfoo";
        openLinkDialog(MENU_WIKI_PAGE);
        waitForStepToLoad("xSelectorAggregatorStep");
        clickTab(SEARCH_TAB);
        waitForStepToLoad("xPagesSearch");
        getSelenium().click("//div[contains(@class, 'xListItem')]/div[contains(@class, 'xNewPagePreview')]");
        getSelenium().doubleClick("//div[contains(@class, 'xListItem')]/div[contains(@class, 'xNewPagePreview')]");
        waitForStepToLoad("xLinkToNewPage");
        getSelenium().type("//div[contains(@class, 'xLinkToNewPage')]//input", newPageName);
        clickButtonWithText("Select");
        waitForStepToLoad("xLinkConfig");
        typeInInput(LABEL_INPUT_TITLE, label);
        clickButtonWithText("Create Link");
        waitForDialogToClose();

        switchToSource();
        assertSourceText("[[" + label + ">>" + newPageName + "]]");
    }

    /**
     * Tests if an empty link is filtered.
     */
    public void testFilterEmptyLink()
    {
        // Select the bogus BR to overwrite it.
        selectAllContent();
        typeText("ab");
        // Select the text.
        selectNodeContents("XWE.body.firstChild");
        // Make it bold.
        clickBoldButton();
        // Make it a link to a web page.
        openLinkDialog(MENU_WEB_PAGE);
        // Ensure wizard step is loaded.
        waitForStepToLoad("xLinkToUrl");
        typeInInput("Web page address", "http://www.xwiki.org");
        clickButtonWithText("Create Link");
        waitForDialogToClose();
        // Place the caret inside the text.
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        // Remove the bold style around the caret (not for the entire link).
        clickBoldButton();
        // The link must have been split in three.
        assertEquals("3", getEval("window.XWE.body.getElementsByTagName('a').length"));
        // Check the source text.
        switchToSource();
        assertSourceText("**[[a>>http://www.xwiki.org]][[b>>http://www.xwiki.org]]**");
    }

    protected void waitForStepToLoad(String name)
    {
        waitForElement("//*[contains(@class, '" + name + "')]");
    }

    private void clickTab(String tabName)
    {
        String tabSelector = "//div[.='" + tabName + "']";
        getSelenium().click(tabSelector);
    }

    private void openLinkDialog(String menuName)
    {
        clickMenu(MENU_LINK);
        assertTrue(isMenuEnabled(menuName));
        clickMenu(menuName);
        waitForDialogToLoad();
    }

    /**
     * In addition to {@link #assertFieldErrorIsPresent(String, String)}, this function does the checks in the specified
     * step.
     * 
     * @param errorMessage the expected error message
     * @param fieldXPathLocator the locator for the field in error
     * @param step the step in which the error should appear
     * @see {@link #assertFieldErrorIsPresent(String, String)}
     */
    public void assertFieldErrorIsPresentInStep(String errorMessage, String fieldXPathLocator, String step)
    {
        waitForStepToLoad(step);
        assertFieldErrorIsPresent(errorMessage, fieldXPathLocator);
    }

    /**
     * In addition to {@link #assertFieldErrorIsNotPresent()}, this function does the checks in the specified step.
     * 
     * @param step the step to check for errors
     */
    public void assertFieldErrorIsNotPresentInStep(String step)
    {
        waitForStepToLoad(step);
        assertFieldErrorIsNotPresent();
    }
}
