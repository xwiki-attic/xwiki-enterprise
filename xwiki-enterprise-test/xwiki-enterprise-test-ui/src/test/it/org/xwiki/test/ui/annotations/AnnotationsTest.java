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

import org.junit.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.annotation.test.po.AnnotatableViewPage;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.AdminAuthenticationRule;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.ui.browser.IgnoreBrowsers;

/**
 * Annotation Test.
 * 
 * @version $Id$
 * @since 2.7RC1
 */
public class AnnotationsTest extends AbstractTest
{
    @Rule
    public AdminAuthenticationRule adminAuthenticationRule = new AdminAuthenticationRule(getUtil());

    private static final String CONTENT =
        "It's an easy-to-edit website that will help you work better together. This Wiki is made of pages "
            + "sorted by spaces. You're currently in the Main space, looking at its home page (WebHome).";

    private static final String ANNOTATED_TEXT_1 = "work better together";

    private static final String ANNOTATION_TEXT_1 = "XWiki motto";

    private static final String ANNOTATED_TEXT_2 = "WebHome";

    private static final String ANNOTATION_TEXT_2 = "Every Space has it's own webhome";

    private static final String ANNOTATED_TEXT_3 = "Main space";

    private static final String ANNOTATION_TEXT_3 = "Each XWiki instance has a Main space";

    private static final String ANNOTATED_TEXT_4 = "easy-to-edit website";

    private static final String ANNOTATION_TEXT_4 = "Yes, we have our WYSIWYG";

    private AnnotatableViewPage annotatableViewPage;

    @Before
    public void setUp() throws Exception
    {
        getUtil().deletePage(getTestClassName(), getTestMethodName());
        annotatableViewPage = new AnnotatableViewPage(
            getUtil().createPage(getTestClassName(), getTestMethodName(), CONTENT, null));
    }

    @Test
    @IgnoreBrowsers({
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146"),
    @IgnoreBrowser(value = "internet.*", version = "9\\.*", reason="See http://jira.xwiki.org/browse/XE-1177")
    })
    public void AddAndDeleteAnnotations()
    {
        annotatableViewPage.addAnnotation(ANNOTATED_TEXT_1, ANNOTATION_TEXT_1);
        Assert.assertEquals(ANNOTATION_TEXT_1, annotatableViewPage.getAnnotationContentByText(ANNOTATED_TEXT_1));
        annotatableViewPage.addAnnotation(ANNOTATED_TEXT_2, ANNOTATION_TEXT_2);
        Assert.assertEquals(ANNOTATION_TEXT_2, annotatableViewPage.getAnnotationContentByText(ANNOTATED_TEXT_2));
        annotatableViewPage.addAnnotation(ANNOTATED_TEXT_3, ANNOTATION_TEXT_3);
        Assert.assertEquals(ANNOTATION_TEXT_3, annotatableViewPage.getAnnotationContentByText(ANNOTATED_TEXT_3));
        annotatableViewPage.addAnnotation(ANNOTATED_TEXT_4, ANNOTATION_TEXT_4);
        Assert.assertEquals(ANNOTATION_TEXT_4, annotatableViewPage.getAnnotationContentByText(ANNOTATED_TEXT_4));

        // It seems that there are some issues refreshing content while this tab is not open. This might be a bug in the
        // Annotations Application
        annotatableViewPage.showAnnotationsPane();
        annotatableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_1);
        annotatableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_2);
        annotatableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_3);
        annotatableViewPage.deleteAnnotationByText(ANNOTATED_TEXT_4);
    }
}
