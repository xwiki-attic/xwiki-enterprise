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
 * Annotation Test.
 * 
 * @version $Id$
 * @since 2.7RC1
 */
public class AnnotationsTest extends AbstractAdminAuthenticatedTest
{
    private static final String CONTENT =
        "It's an easy-to-edit website that will help you work better together. This Wiki is made of pages "
            + "sorted by spaces. You're currently in the Main space, looking at its home page (WebHome).";

    private static final String DOC_NAME = "AnnotationsTestPage";

    private static final String SPACE_NAME = "Main";

    private static final String DOC_TITLE = "AnnotationsTest";

    private static final String ANNOTATED_TEXT_1 = "work better together";

    private static final String ANNOTATION_TEXT_1 = "XWiki motto";

    private static final String ANNOTATED_TEXT_2 = "WebHome";

    private static final String ANNOTATION_TEXT_2 = "Every Space has it's own webhome";

    private static final String ANNOTATED_TEXT_3 = "Main space";

    private static final String ANNOTATION_TEXT_3 = "Each XWiki instance has a Main space";

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

    void addAnnotation(String annotatedText, String annotationText)
    {
        annotationsWindow.addAnnotation(annotatedText, annotationText);
        // waiting for the notification that the operation is done
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        // check is the saved successfully message is displayed
        save = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(XWIKI_ANNOTATION_ADD_SUCCESS, save.getText());
        save.click();
        Assert.assertEquals(annotationText, annotationsLabel.getAnnotationContentByText(annotatedText));
    }

    void deleteAnnotationByID(String id)
    {
        annotationsLabel.deleteAnnotationById(id);
        vp.waitUntilElementIsVisible(By.className("xnotification-done"), 20);
        WebElement delete = getDriver().findElement(By.className("xnotification-done"));
        Assert.assertEquals(XWIKI_ANNOTATION_DELETE_SUCCESS, delete.getText());
        delete.click();
    }

    void deleteAnnotationByText(String annotatedText)
    {
        deleteAnnotationByID(this.annotationsLabel.getAnnotationIdByText(annotatedText));
    }

    @Test
    public void defaultAnnotationsTest()
    {
        homePage = new HomePage();
        homePage.gotoPage();

        WikiEditPage wep = new WikiEditPage();
        wep.switchToEdit(SPACE_NAME, DOC_NAME);
        wep.setTitle(DOC_TITLE);
        wep.setContent(CONTENT);
        vp = wep.clickSaveAndView();
        annotationsPane = new AnnotationsPane();

        annotationsPane.toggleAnnotationsPane();
        annotationsPane.showAnnotations();

        annotationsWindow = new AnnotationsWindow();
        annotationsLabel = new AnnotationsLabel();
        this.addAnnotation(ANNOTATED_TEXT_1, ANNOTATION_TEXT_1);
        this.addAnnotation(ANNOTATED_TEXT_2, ANNOTATION_TEXT_2);
        this.addAnnotation(ANNOTATED_TEXT_3, ANNOTATION_TEXT_3);
        this.addAnnotation(ANNOTATED_TEXT_4, ANNOTATION_TEXT_4);

        // delete annotations
        this.deleteAnnotationByID("ID0");
        this.deleteAnnotationByText(ANNOTATED_TEXT_2);
        this.deleteAnnotationByText(ANNOTATED_TEXT_3);
        this.deleteAnnotationByText(ANNOTATED_TEXT_4);

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
        // Annotations are disabled in 1.0 Pages. This element should no be here
        Assert.assertEquals(0, getDriver().findElements(By.id("annotationsdisplay")).size());
        annotationsWindow.simulateCTRL_M();

        vp = new ViewPage();
        vp.waitUntilElementIsVisible(By.className("xnotification-warning"), 20);
        WebElement warning = getDriver().findElement(By.className("xnotification-warning"));
        Assert.assertEquals(XWIKI_SYNTAX_1_WARNING, warning.getText());

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

        vp = new ViewPage();
        vp.waitUntilElementIsVisible(By.className("xnotification-warning"), 20);
        warning = getDriver().findElement(By.className("xnotification-warning"));
        Assert.assertEquals(XWIKI_SYNTAX_1_WARNING, warning.getText());
    }
}
