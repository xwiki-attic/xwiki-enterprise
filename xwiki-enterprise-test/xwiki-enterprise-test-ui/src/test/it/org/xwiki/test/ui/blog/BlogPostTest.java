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
package org.xwiki.test.ui.blog;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.po.blog.BlogHomePage;
import org.xwiki.test.po.blog.BlogPostInlinePage;
import org.xwiki.test.po.blog.BlogPostViewPage;
import org.xwiki.test.po.blog.CreateBlogPostPane;

/**
 * Functional tests for blog posts.
 * 
 * @version $Id$
 * @since 3.2M3
 */
public class BlogPostTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Tests how a blog post is created and then edited.
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testCreateAndEditBlogPost()
    {
        getUtil().deletePage("Blog", getTestMethodName());

        // Create the blog post.
        CreateBlogPostPane createBlogPostPane = BlogHomePage.gotoPage().getCreateBlogPostPane();
        createBlogPostPane.setTitle(getTestMethodName());
        BlogPostInlinePage blogPostInlinePage = createBlogPostPane.clickCreateButton();
        blogPostInlinePage.waitToLoad();

        Assert.assertEquals(getTestMethodName(), blogPostInlinePage.getTitle());

        blogPostInlinePage.setTitle("Test blog title");
        blogPostInlinePage.setContent("Test blog content");
        blogPostInlinePage.setCategories(Collections.singletonList("Personal"));
        BlogPostViewPage blogPostViewPage = blogPostInlinePage.clickSaveAndView();

        // Assert the result.
        Assert.assertEquals("Test blog title", blogPostViewPage.getDocumentTitle());
        Assert.assertEquals("Test blog content", blogPostViewPage.getContent());
        Assert.assertEquals(Collections.singletonList("Personal"), blogPostViewPage.getCategories());
        Assert.assertFalse(blogPostViewPage.isPublished());

        // Edit the blog post.
        blogPostInlinePage = blogPostViewPage.clickEditBlogPostIcon();
        blogPostInlinePage.waitToLoad();

        Assert.assertEquals("Test blog title", blogPostInlinePage.getTitle());
        Assert.assertEquals("Test blog content", blogPostInlinePage.getContent());
        Assert.assertEquals(Collections.singletonList("Personal"), blogPostInlinePage.getCategories());
        Assert.assertFalse(blogPostInlinePage.isPublished());

        // Modify the blog post.
        blogPostInlinePage.setTitle("Modified title");
        blogPostInlinePage.setContent("Modified content");
        blogPostInlinePage.setCategories(Arrays.asList("News", "Personal"));
        blogPostInlinePage.setPublished(true);

        // Assert the result.
        blogPostViewPage = blogPostInlinePage.clickSaveAndView();

        Assert.assertEquals("Modified title", blogPostViewPage.getDocumentTitle());
        Assert.assertEquals("Modified content", blogPostViewPage.getContent());
        Assert.assertEquals(Arrays.asList("News", "Personal"), blogPostViewPage.getCategories());
        Assert.assertTrue(blogPostViewPage.isPublished());
        Assert.assertFalse(blogPostViewPage.isHidden());
    }
}
