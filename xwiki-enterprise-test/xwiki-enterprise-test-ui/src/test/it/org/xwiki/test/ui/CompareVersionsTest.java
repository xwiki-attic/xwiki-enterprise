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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.tag.test.po.AddTagsPane;
import org.xwiki.tag.test.po.TaggablePage;
import org.xwiki.test.ui.po.AttachmentsPane;
import org.xwiki.test.ui.po.ChangesPane;
import org.xwiki.test.ui.po.CommentsTab;
import org.xwiki.test.ui.po.FormElement;
import org.xwiki.test.ui.po.HistoryPane;
import org.xwiki.test.ui.po.ViewPage;
import org.xwiki.test.ui.po.editor.ClassEditPage;
import org.xwiki.test.ui.po.editor.ObjectEditPage;
import org.xwiki.test.ui.po.editor.WikiEditPage;

/**
 * Tests the comparison of document versions.
 * 
 * @version $Id$
 * @since 4.2M1
 */
public class CompareVersionsTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil(), getDriver());

    /**
     * The test page.
     */
    private ViewPage testPage;

    @Before
    public void setUp() throws Exception
    {
        String pageName = "PageWithManyVersions";

        // Check if the test page exists.
        testPage = getUtil().gotoPage(getTestClassName(), pageName);
        if (testPage.exists()) {
            // TODO: Remove when XWIKI-6688 (Possible race condition when clicking on a tab at the bottom of a page in
            // view mode) is fixed.
            testPage.waitForDocExtraPaneActive("comments");
            // NOTE: We use the same page for all tests because tests don't modify the test page. They only compare two
            // versions of the test page.
            return;
        }

        // Create the test page.
        testPage = getUtil().createPage(getTestClassName(), pageName, "one\ntwo\nthree", "Test");

        // Change the content and the meta data.
        WikiEditPage wikiEditPage = testPage.editWiki();
        wikiEditPage.setContent("one\n**two**\nfour");
        wikiEditPage.setTitle("Compare verSions test");
        wikiEditPage.setParent("Sandbox.WebHome");
        wikiEditPage.setEditComment("Changed content and meta data.");
        wikiEditPage.clickSaveAndContinue();
        wikiEditPage.setTitle("Compare versions test");
        wikiEditPage.setMinorEdit(true);
        wikiEditPage.setEditComment("Fix typo in title.");
        wikiEditPage.clickSaveAndContinue();

        // Add objects.
        ObjectEditPage objectEditPage = wikiEditPage.editObjects();
        FormElement form = objectEditPage.addObject("XWiki.JavaScriptExtension");
        Map<String, String> assignment = new HashMap<String, String>();
        assignment.put("XWiki.JavaScriptExtension_0_name", "JavaScript code");
        assignment.put("XWiki.JavaScriptExtension_0_code", "var tmp = alice;\nalice = bob;\nbob = tmp;");
        assignment.put("XWiki.JavaScriptExtension_0_use", "onDemand");
        form.fillFieldsByName(assignment);
        objectEditPage.clickSaveAndContinue();
        assignment.put("XWiki.JavaScriptExtension_0_name", "Code snippet");
        assignment.put("XWiki.JavaScriptExtension_0_code", "var tmp = alice;\nalice = 2 * bob;\nbob = tmp;");
        form.fillFieldsByName(assignment);
        objectEditPage.clickSaveAndContinue();

        // Create class.
        ClassEditPage classEditPage = objectEditPage.editClass();
        classEditPage.addProperty("age", "Number");
        classEditPage.addProperty("color", "String");
        classEditPage.getNumberClassEditElement("age").setNumberType("integer");
        classEditPage.clickSaveAndContinue();
        classEditPage.deleteProperty("color");
        testPage = classEditPage.clickSaveAndView();

        // Add tags.
        TaggablePage taggablePage = new TaggablePage();
        AddTagsPane addTagsPane = taggablePage.addTags();
        addTagsPane.setTags("foo,bar");
        addTagsPane.add();
        taggablePage.removeTag("foo");

        // Attach files.
        AttachmentsPane attachmentsPane = testPage.openAttachmentsDocExtraPane();
        // TODO: Update this code when we (re)add support for uploading multiple files at once.
        for (String fileName : new String[] {"SmallAttachment.txt", "SmallAttachment2.txt", "SmallAttachment.txt"}) {
            attachmentsPane.setFileToUpload(this.getClass().getResource('/' + fileName).getPath());
            attachmentsPane.waitForUploadToFinish(fileName);
            attachmentsPane.clickHideProgress();
        }
        attachmentsPane.deleteAttachmentByFileByName("SmallAttachment2.txt");

        // Add comments.
        getUtil().createUserAndLogin("Alice", "ecila");
        testPage = getUtil().gotoPage(getTestClassName(), pageName);
        CommentsTab commentsTab = testPage.openCommentsDocExtraPane();
        commentsTab.postComment("first line\nsecond line", true);
        commentsTab.editCommentByID(0, "first line\nline in between\nsecond line");
        commentsTab.replyToCommentByID(0, "this is a reply");
        commentsTab.deleteCommentByID(1);
    }

    /**
     * Tests that all changes are displayed.
     */
    @Test
    public void testAllChanges()
    {
        HistoryPane historyTab = testPage.openHistoryDocExtraPane().showMinorEdits();
        String currentVersion = historyTab.getCurrentVersion();

        // TODO: If the document has many versions, like in this case, the versions are paginated and currently there's
        // no way to compare two versions from two different pagination pages using the UI. Thus we have to build the
        // URL and load the compare page manually. Update the code when we remove this UI limitation.
        // ChangesPane changesPane = historyTab.compare("1.1", currentVersion).getChangesPane();
        String queryString = String.format("viewer=changes&rev1=1.1&rev2=%s", currentVersion);
        getUtil().gotoPage(getTestClassName(), testPage.getMetaDataValue("page"), "view", queryString);
        ChangesPane changesPane = new ChangesPane();

        // Version summary.
        String today = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        Assert.assertTrue(changesPane.getFromVersionSummary().startsWith(
            "From version 1.1\nedited by Administrator\non " + today));
        Assert.assertTrue(changesPane.getToVersionSummary().startsWith(
            "To version " + currentVersion + "\nedited by Alice\non " + today));
        Assert.assertEquals("Change comment: Deleted object", changesPane.getChangeComment());

        // Meta data changes.
        Assert.assertEquals(Arrays.asList("Title", "Parent", "Document author", "Tags"),
            changesPane.getChangedMetaData());
        Assert.assertEquals("<del>T</del><ins>Compar</ins>e<ins> ver</ins>s<ins>ions </ins>t<ins>est</ins>",
            changesPane.getMetaDataChanges("Title"));
        Assert.assertEquals("<ins>Sandbox.WebHome</ins>", changesPane.getMetaDataChanges("Parent"));
        Assert.assertEquals("XWiki.A<del>dm</del><ins>l</ins>i<del>n</del><ins>ce</ins>",
            changesPane.getMetaDataChanges("Document author"));
        Assert.assertEquals("<ins>bar</ins>", changesPane.getMetaDataChanges("Tags"));

        // Content changes.
        Assert.assertEquals("@@ -1,3 +1,3 @@\n one\n-two\n-<del>th</del>r<del>ee</del>\n"
            + "+<ins>**</ins>two<ins>**</ins>\n+<ins>fou</ins>r", changesPane.getContentChanges());

        // Attachment changes.
        Assert.assertEquals(Arrays.asList("SmallAttachment.txt: Attachment has been added"),
            changesPane.getAttachmentChanges());

        // Comment changes.
        Assert.assertEquals(Arrays.asList("Comment number 0 added"), changesPane.getCommentChangeSummaries());
        Assert.assertEquals("@@ -1,0 +1,3 @@\n+first line\n+line in between\n+second line",
            changesPane.getCommentChanges(0, "Comment content"));

        // Object changes.
        Assert.assertEquals(Arrays.asList("Object number 0 of type XWiki.JavaScriptExtension added"),
            changesPane.getObjectChangeSummaries());
        Assert.assertEquals("<ins>onDemand</ins>",
            changesPane.getObjectChanges("XWiki.JavaScriptExtension", 0, "Use this extension"));

        // Class changes.
        Assert.assertEquals(Arrays.asList("Added property age"), changesPane.getClassChanges());
    }

    /**
     * Tests that a message is displayed when there are no changes.
     */
    @Test
    public void testNoChanges()
    {
        HistoryPane historyTab = testPage.openHistoryDocExtraPane();
        String currentVersion = historyTab.getCurrentVersion();
        Assert.assertTrue(historyTab.compare(currentVersion, currentVersion).getChangesPane().hasNoChanges());
    }

    /**
     * Tests that the unified diff (for multi-line text) shows the inline changes.
     */
    @Test
    public void testUnifiedDiffShowsInlineChanges()
    {
        ChangesPane changesPane =
            testPage.openHistoryDocExtraPane().showMinorEdits().compare("2.2", "2.3").getChangesPane();
        Assert.assertEquals(
            "@@ -1,3 +1,3 @@\n var tmp = alice;\n-alice = bob;\n+alice = <ins>2 * </ins>bob;\n bob = tmp;",
            changesPane.getObjectChanges("XWiki.JavaScriptExtension", 0, "Code"));
    }
}
