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
    private AnnotatableViewPage annotatableViewPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(getTestClassName(), getTestMethodName());

        AdministrationPage adminPage = new AdministrationPage();
        adminPage.gotoPage();
        AnnotationsPage annotationsAdminPage = adminPage.clickAnnotationsSection();
        // We make sure the annotations are Activated
        annotationsAdminPage.activateAnnotations();
        // We set annotations to be displayed by default
        annotationsAdminPage.displayAnnotationsByDefault();
        // We set annotations to be highlighted
        annotationsAdminPage.displayAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();

        annotatableViewPage = new AnnotatableViewPage(
            getUtil().createPage(getTestClassName(), getTestMethodName(), "Some content",
                "AnnotationsTest in XWiki 1.0 Syntax", "xwiki/1.0"));
    }

    /**
     * This test creates a XWiki 1.0 syntax page, and tries to add annotations to it, and checks if the warning messages
     * are shown This test is against XAANNOTATIONS-17
     */
    @Test
    public void xwikiPageSyntaxAnnotations()
    {
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
        AdministrationPage adminPage = new AdministrationPage();
        adminPage.gotoPage();
        AnnotationsPage annotationsAdminPage = adminPage.clickAnnotationsSection();
        annotationsAdminPage.activateAnnotations();
        annotationsAdminPage.hideAnnotationsByDefault();
        annotationsAdminPage.hideAnnotationsHighlightByDefault();
        annotationsAdminPage.clickSave();
    }
}
