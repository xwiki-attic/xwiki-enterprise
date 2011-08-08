package org.xwiki.test.ui.annotations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.administration.elements.AnnotationsPage;
import org.xwiki.test.ui.annotations.elements.AnnotableViewPage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;

public class AnnotationsTestInOldSyntax extends AbstractAdminAuthenticatedTest
{
    private static final String CONTENT =
        "It's an easy-to-edit website that will help you work better together. This Wiki is made of pages "
            + "sorted by spaces. You're currently in the Main space, looking at its home page (WebHome).";

    private static final String DOC_NAME = "AnnotationsTestPageIn10Syntax";

    private static final String SPACE_NAME = "Main";

    private static final String DOC_TITLE = "AnnotationsTest in XWiki 1.0 Syntax";

    private AdministrationPage adminPage = new AdministrationPage();

    private AnnotationsPage annotationsAdminPage = new AnnotationsPage();

    private AnnotableViewPage annotableViewPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        adminPage.gotoPage();
        annotationsAdminPage = adminPage.clickAnnotationsSection();
        annotationsAdminPage.activateAnnotations();
        annotationsAdminPage.displayAnnotationsByDefault();
        annotationsAdminPage.displayAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();
        annotableViewPage = new AnnotableViewPage();
        adminPage = new AdministrationPage();
        getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, DOC_TITLE, "xwiki/1.0");
    }

    // This test creates a XWiki 1.0 syntax page, and tries to add annotations to it, and checks if the warning messages
    // are shown
    // This test is against XAANNOTATIONS-17
    @Test
    public void xwikiPageSyntaxAnnotationsTest()
    {
        // Landing directly on this page might result in notification not to be displayed
        getDriver().navigate().refresh();
        annotableViewPage.showAnnotationsPane();
        // Annotations are disabled in 1.0 Pages. This element should no be here
        annotableViewPage.checkIfAnnotationsAreDisabled();
        annotableViewPage.simulateCTRL_M();
        annotableViewPage.waitforAnnotationWarningNotification();
    }

    @After
    public void tearDown()
    {
        // We restore the original factory settings of the Annotation Application
        adminPage.gotoPage();
        annotationsAdminPage = adminPage.clickAnnotationsSection();
        annotationsAdminPage.activateAnnotations();
        annotationsAdminPage.hideAnnotationsByDefault();
        annotationsAdminPage.hideAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();
    }
}
