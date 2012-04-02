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
package org.xwiki.test.ui;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.xwiki.test.po.administration.ProfileUserProfilePage;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.po.editor.ProfileEditPage;
import org.xwiki.test.ui.po.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.po.editor.wysiwyg.EditorElement;
import org.xwiki.test.ui.po.editor.wysiwyg.RichTextAreaElement;
import org.xwiki.test.ui.po.editor.wysiwyg.TableConfigPane;
import org.xwiki.test.ui.po.editor.wysiwyg.UploadImagePane;

/**
 * Test WYSIWYG content editing.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class EditWYSIWYGTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The edited page.
     */
    private WYSIWYGEditPage editPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();

        this.editPage = WYSIWYGEditPage.gotoPage(getTestClassName(), getTestMethodName());
        this.editPage.getContentEditor().waitToLoad();
    }

    /**
     * Tests that images are uploaded fine after a preview.
     * 
     * @see <a href="http://jira.xwiki.org/jira/browse/XWIKI-5895">XWIKI-5895</a>: Adding an image in the WYSIWYG editor
     *      and previewing it without saving the page first makes the XWiki page corrupt.
     **/
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testUploadImageAfterPreview()
    {
        this.editPage.clickPreview().clickBackToEdit();
        this.editPage.getContentEditor().waitToLoad();
        UploadImagePane uploadImagePane = this.editPage.insertAttachedImage().selectFromCurrentPage().uploadImage();
        uploadImagePane.setImageToUpload(this.getClass().getResource("/administration/avatar.png").getPath());
        // Fails if the image configuration step doesn't load in a decent amount of time.
        uploadImagePane.configureImage();
    }

    /**
     * @see XWIKI:7028: Strange behaviour when pressing back and forward on a page that has 2 WYSIWYG editors displayed.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testBackForwardCache()
    {
        ProfileEditPage profileEditPage = ProfileUserProfilePage.gotoPage("Admin").editProfile();
        String about = profileEditPage.getUserAbout();
        String address = profileEditPage.getUserAddress();
        getDriver().navigate().back();
        getDriver().navigate().forward();
        new ProfileUserProfilePage("Admin").waitForProfileEditionToLoad();
        profileEditPage = new ProfileEditPage();
        Assert.assertEquals(about, profileEditPage.getUserAbout());
        Assert.assertEquals(address, profileEditPage.getUserAddress());
    }

    /**
     * Test that the content of the rich text area is preserved when the user refreshes the page.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testPreserveUnsavedRichContentAgainstRefresh()
    {
        // Type text and refresh the page.
        this.editPage.getContentEditor().getRichTextArea().sendKeys("2");
        this.editPage.sendKeys(Keys.F5);

        this.editPage = new WYSIWYGEditPage();
        EditorElement editor = this.editPage.getContentEditor();
        editor.waitToLoad();

        // Type more text and check the result.
        RichTextAreaElement textArea = editor.getRichTextArea();
        textArea.sendKeys("1");
        Assert.assertEquals("12", textArea.getText());
    }

    /**
     * Test that the content of the source text area is preserved when the user refreshes the page.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testPreserveUnsavedSourceAgainstRefresh()
    {
        EditorElement editor = this.editPage.getContentEditor();
        editor.switchToSource();

        // Type text and refresh the page.
        editor.getSourceTextArea().sendKeys("1" + Keys.F5);

        this.editPage = new WYSIWYGEditPage();
        editor = this.editPage.getContentEditor();
        editor.waitToLoad();
        editor.switchToSource();

        // Type more text and check the result.
        editor.getSourceTextArea().sendKeys("2");
        Assert.assertEquals("12", editor.getSourceTextArea().getAttribute("value"));
    }

    /**
     * Tests that the currently active editor (WYSIWYG or Source) is preserved when the user refreshes the page.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testPreserveSelectedEditorAgainstRefresh()
    {
        // The WYSIWYG editor should be initially active.
        EditorElement editor = this.editPage.getContentEditor();
        Assert.assertFalse(editor.getSourceTextArea().isEnabled());

        // Switch to Source editor and refresh the page.
        editor.switchToSource();
        editor.getSourceTextArea().sendKeys(Keys.F5);

        this.editPage = new WYSIWYGEditPage();
        editor = this.editPage.getContentEditor();
        editor.waitToLoad();

        // The Source editor should be active now because it was selected before the refresh.
        Assert.assertTrue(editor.getSourceTextArea().isEnabled());

        // Switch to WYSIWYG editor and refresh the page again.
        editor.switchToWysiwyg();
        this.editPage.sendKeys(Keys.F5);

        this.editPage = new WYSIWYGEditPage();
        editor = this.editPage.getContentEditor();
        editor.waitToLoad();

        // The WYSIWYG editor should be active now because it was selected before the refresh.
        Assert.assertFalse(editor.getSourceTextArea().isEnabled());
    }

    /**
     * Test if an undo step reverts only one paste operation from a sequence, and not all of them.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testUndoRepeatedPaste()
    {
        EditorElement editor = this.editPage.getContentEditor();
        RichTextAreaElement textArea = editor.getRichTextArea();
        // Type text, select it (Shift+LeftArrow), copy it (Control+C) and paste it 4 times (Control+V).
        // NOTE: We don't use Control+A to select the text because it selects also the BR element.
        textArea.sendKeys("q" + Keys.chord(Keys.SHIFT, Keys.ARROW_LEFT) + Keys.chord(Keys.CONTROL, "cvvvv"));
        // Undo the last paste.
        editor.getToolBar().clickUndoButton();
        Assert.assertEquals("qqq", textArea.getText());
    }

    /**
     * @see XWIKI-4230: "Tab" doesn't work in the Table Dialog in FF 3.5.2
     */
    @Test
    public void testTabInTableConfigDialog()
    {
        // FIXME: Remove the following check when native events will be supported across all major platforms.
        WebDriver driver = ((XWikiWrappingDriver) getDriver()).getWrappedDriver();
        if (((RemoteWebDriver) driver).getCapabilities().getVersion().startsWith("3.6.")) {
            // Currently we don't run this test on Firefox 3.6.x because the Tab key is not properly simulated when
            // native events aren't enabled. See http://code.google.com/p/selenium/issues/detail?id=368 .
            return;
        }

        TableConfigPane tableConfig = this.editPage.insertTable();

        // Assert that the row count input has the focus.
        Assert.assertEquals(tableConfig.getRowCountInput(), getDriver().switchTo().activeElement());
        getDriver().switchTo().defaultContent();

        // Press Tab to move the focus to the next input.
        tableConfig.getRowCountInput().sendKeys(Keys.TAB);

        // Assert that the column count input has the focus.
        Assert.assertEquals(tableConfig.getColumnCountInput(), getDriver().switchTo().activeElement());
        getDriver().switchTo().defaultContent();
    }

    /**
     * Test that hitting the . (dot) key at the end of a list item does not act as delete.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3304
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testDotAtEndDoesNotDelete()
    {
        EditorElement editor = this.editPage.getContentEditor();

        // Create a list with two items.
        editor.switchToSource();
        editor.getSourceTextArea().sendKeys("* foo\n* bar");
        editor.switchToWysiwyg();

        // Place the caret at the end of the first item and type dot.
        RichTextAreaElement textArea = editor.getRichTextArea();
        textArea.sendKeys(Keys.chord(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT) + ".");

        Assert.assertEquals("foo.\nbar", textArea.getText());
    }
}
