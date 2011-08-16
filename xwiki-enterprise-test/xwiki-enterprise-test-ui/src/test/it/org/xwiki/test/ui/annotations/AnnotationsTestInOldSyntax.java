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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.administration.elements.AnnotationsPage;
import org.xwiki.test.ui.annotations.elements.AnnotatableViewPage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Annotations in XWiki 1.0 syntax pages.
 * 
 * @since 3.2M2
 * @version $Id$
 */
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

    private AnnotatableViewPage annotatableViewPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        adminPage.gotoPage();
        annotationsAdminPage = adminPage.clickAnnotationsSection();
        // We make sure the annotations are Activated
        annotationsAdminPage.activateAnnotations();
        // We set annotations to be displayed by default
        annotationsAdminPage.displayAnnotationsByDefault();
        // We set annotations to be highlighted
        annotationsAdminPage.displayAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();
        annotatableViewPage = new AnnotatableViewPage();
        adminPage = new AdministrationPage();
        getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, DOC_TITLE, "xwiki/1.0");
    }

    /**
     * This test creates a XWiki 1.0 syntax page, and tries to add annotations to it, and checks if the warning messages
     * are shown This test is against XAANNOTATIONS-17
     */
    @Test
    public void xwikiPageSyntaxAnnotations()
    {
        // Landing directly on this page might result in notification not to be displayed
        getDriver().navigate().refresh();
        annotatableViewPage.showAnnotationsPane();
        // Annotations are disabled in 1.0 Pages. This element should no be here
        Assert.assertTrue(annotatableViewPage.checkIfAnnotationsAreDisabled());
        annotatableViewPage.simulateCTRL_M();
        annotatableViewPage.waitforAnnotationWarningNotification();
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
