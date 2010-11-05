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
package org.xwiki.test.ui.annotations;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.administration.elements.AnnotationsPage;
import org.xwiki.test.ui.annotations.elements.AnnotationsLabel;
import org.xwiki.test.ui.annotations.elements.AnnotationsPane;
import org.xwiki.test.ui.annotations.elements.AnnotationsWindow;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Annotation Test
 * 
 * @version $Id$
 * @since 2.5M2
 */
public class AnnotationsTest extends AbstractAdminAuthenticatedTest
{

    private static final String CONTENT =
        "It's an easy-to-edit website that will help you work better together. This Wiki is made of pages sorted by spaces. You're currently in the Main space, looking at its home page (WebHome).";

    private static final String DOC_NAME = "AnnotationsTestPage";

    private static final String SPACE_NAME = "Main";

    private static final String DOC_TITLE = "AnnotationsTest";

    private static final String ANNOTATED_TEXT_1 = "work better together";

    private static final String ANNOTATION_TEXT_1 = "XWiki motto";

    private static final String ANNOTATED_TEXT_2 = "WebHome";

    private static final String ANNOTATION_TEXT_2 = "Every Space has it's own webhome";

    private static final String ANNOTATED_TEXT_3 = "Main space";

    private static final String ANNOTATION_TEXT_3 = "Each XWiki instante has a Main space";

    private static final String ANNOTATED_TEXT_4 = "easy-to-edit website";

    private static final String ANNOTATION_TEXT_4 = "Yes, we have our WYSIWYG";

    private static final String XWIKI_SYNTAX_1_WARNING =
        "Annotations are not available for documents in XWiki/1.0 syntax.";

    private static final String XWIKI_ANNOTATION_ADD_SUCCESS = "Annotation has been successfully added";

    private static final String XWIKI_ANNOTATION_DELETE_SUCCESS = "Annotation deleted";

    private ViewPage vp;

    private HomePage homePage;

    private AnnotationsPane annotationsPane;

    private AnnotationsWindow annotationsWindow;

    private AnnotationsLabel annotationsLabel;

    AdministrationPage adminPage;

    AnnotationsPage annotationsAdminPage;

    WebElement save;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);

    }

    @Test
    public void defaultAnnotationsTest()
    {
        homePage = new HomePage();
        homePage.gotoPage();
        annotationsPane = new AnnotationsPane();
        annotationsWindow = new AnnotationsWindow();
        annotationsLabel = new AnnotationsLabel();

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit(SPACE_NAME, DOC_NAME);
        wep.setTitle(DOC_TITLE);
        wep.setContent(CONTENT);
        vp = wep.clickSaveAndView();

        annotationsPane.toggleAnnotationsPane();
        annotationsPane.showAnnotations();

        // add first annotation
        annotationsWindow.addAnnotation(ANNOTATED_TEXT_1, ANNOTATION_TEXT_1);
        // waiting for the notification that the operation is done
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        // check is the saved successfully message is displayed
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(save.getText(), XWIKI_ANNOTATION_ADD_SUCCESS);
        // wait until the notification disappears
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);
        Assert.assertEquals(ANNOTATION_TEXT_1, annotationsLabel.getAnnotationContentByText(ANNOTATED_TEXT_1));

        // Add the second annotation
        annotationsWindow.addAnnotation(ANNOTATED_TEXT_2, ANNOTATION_TEXT_2);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(save.getText(), XWIKI_ANNOTATION_ADD_SUCCESS);
        // wait until the notification disappears
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);
        Assert.assertEquals(ANNOTATION_TEXT_2, annotationsLabel.getAnnotationContentByText(ANNOTATED_TEXT_2));

        // System.out.println(annotationsLabel.getAnnotationAuthorById(annotationsLabel.getAnnotationIdByText("WebHome")));
        // System.out.println(annotationsLabel.getAnnotationsAuthorByText(ANNOTATED_TEXT_2));
        // System.out.println(annotationsLabel.getAnnotationContentByText(ANNOTATED_TEXT_1));

        // Add the third annotation
        annotationsWindow.addAnnotation(ANNOTATED_TEXT_3, ANNOTATION_TEXT_3);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(save.getText(), XWIKI_ANNOTATION_ADD_SUCCESS);
        // wait until the notification disappears
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);
        Assert.assertEquals(ANNOTATION_TEXT_3, annotationsLabel.getAnnotationContentByText(ANNOTATED_TEXT_3));

        // Add the fourth annotation
        annotationsWindow.addAnnotation(ANNOTATED_TEXT_4, ANNOTATION_TEXT_4);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(save.getText(), XWIKI_ANNOTATION_ADD_SUCCESS);
        // wait until the notification disappears
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);
        Assert.assertEquals(ANNOTATION_TEXT_4, annotationsLabel.getAnnotationContentByText(ANNOTATED_TEXT_4));

        // delete first annotation
        annotationsLabel.deleteAnnotationById("ID0");
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        WebElement delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(delete.getText(), XWIKI_ANNOTATION_DELETE_SUCCESS);
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);

        // delete second annotation
        annotationsLabel.deleteAnnotationByText(ANNOTATED_TEXT_2);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(delete.getText(), XWIKI_ANNOTATION_DELETE_SUCCESS);
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);

        // delete third annotation
        annotationsLabel.deleteAnnotationByText(ANNOTATED_TEXT_3);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(delete.getText(), XWIKI_ANNOTATION_DELETE_SUCCESS);
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);

        // delete fourth annotation
        annotationsLabel.deleteAnnotationByText(ANNOTATED_TEXT_4);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(delete.getText(), XWIKI_ANNOTATION_DELETE_SUCCESS);
        vp.waitUntilElementDisappears(By.className("xnotification-wrapper"), 20);
    }

    // This test creates a XWiki 1.0 syntax page, and tries to add annotations to it, and checks if the warning messages
    // are shown
    // This test is against XAANNOTATIONS-17
    @Test
    public void xwikiPageSyntaxAnnotationsTest()
    {
        homePage = new HomePage();
        homePage.gotoPage();
        adminPage = new AdministrationPage();
        annotationsPane = new AnnotationsPane();
        annotationsWindow = new AnnotationsWindow();
        annotationsLabel = new AnnotationsLabel();
        homePage.gotoPage();
        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit(SPACE_NAME, DOC_NAME);
        wep.setTitle(DOC_TITLE);
        wep.setContent(CONTENT);
        Select select = new Select(getDriver().findElement(By.xpath("//select[@id='xwikidocsyntaxinput2']")));
        select.selectByVisibleText("XWiki 1.0");
        ViewPage vp = wep.clickSaveAndView();
        annotationsPane.toggleAnnotationsPane();
        // annotationsPane.showAnnotations();
        try {
            getDriver().findElement(By.id("annotationsdisplay"));
            Assert.fail("Annotations are disabled in 1.0 Pages. This element should no be here");
        } catch (NoSuchElementException e) {
            // This is normal, test passes
        }
        // annotationsWindow.addAnnotation(ANNOTATED_TEXT_1, ANNOTATION_TEXT_1);
        annotationsWindow.simulateCTRL_M();
        try {
            vp = new ViewPage();
            vp.waitUntilElementIsVisible(By.className("xnotification-warning"), 20);
            WebElement warning = getDriver().findElement(By.className("xnotification-warning"));
            Assert.assertEquals(warning.getText(), XWIKI_SYNTAX_1_WARNING);
        } catch (NoSuchElementException e) {
            // This should not happen. THe warning that Annotations are disabled in 1.0 syntax should appear.
            Assert.fail("There is no warning that annotations are disabled for 1.0 pages");
        }
        adminPage.gotoPage();
        annotationsAdminPage = adminPage.clickAnnotationsSection();
        annotationsAdminPage.activateAnnotations();
        annotationsAdminPage.displayAnnotationsByDefault();
        annotationsAdminPage.displayAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();

        getUtil().gotoPage(SPACE_NAME, DOC_NAME);
        // Landing directly on this page might result in notification not to be displayed
        getDriver().navigate().refresh();
        vp.waitUntilElementIsVisible(By.id("body"), 20);
        try {
            vp = new ViewPage();
            vp.waitUntilElementIsVisible(By.className("xnotification-warning"), 20);
            WebElement warning = getDriver().findElement(By.className("xnotification-warning"));
            Assert.assertEquals(warning.getText(), XWIKI_SYNTAX_1_WARNING);
        } catch (NoSuchElementException e) {
            // This should not happen. THe warning that Annotations are disabled in 1.0 syntax should appear.
            Assert.fail("There is no warning that annotations are disabled for 1.0 pages");
        }

    }
}
