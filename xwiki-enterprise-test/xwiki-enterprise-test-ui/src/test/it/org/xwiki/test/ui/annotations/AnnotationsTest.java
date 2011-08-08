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

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.annotations.elements.AnnotableViewPage;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;

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

    private AnnotableViewPage annotableViewPage;

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, DOC_NAME);
        annotableViewPage = new AnnotableViewPage();
        getUtil().createPage(SPACE_NAME, DOC_NAME, CONTENT, DOC_TITLE);
    }

    @Test
    public void AddAndDeleteAnnotationTest()
    {
        annotableViewPage.addAnnotation(ANNOTATED_TEXT_1, ANNOTATION_TEXT_1);
        annotableViewPage.addAnnotation(ANNOTATED_TEXT_2, ANNOTATION_TEXT_2);
        annotableViewPage.addAnnotation(ANNOTATED_TEXT_3, ANNOTATION_TEXT_3);
        annotableViewPage.addAnnotation(ANNOTATED_TEXT_4, ANNOTATION_TEXT_4);

        annotableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_1);
        annotableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_2);
        annotableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_3);
        annotableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_4);
    }
}
