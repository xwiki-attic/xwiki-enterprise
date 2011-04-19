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
package org.xwiki.test.xmlrpc.confluence;

import java.util.List;

import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.PageHistorySummary;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.Space;

import org.xwiki.test.xmlrpc.confluence.framework.AbstractXmlRpcTestCase;

public class PagesTest extends AbstractXmlRpcTestCase
{
    private String spaceKey;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        this.spaceKey = "ContainerSpace";
        Space space = new Space();
        space.setKey(this.spaceKey);
        space.setName("Some Name");
        this.rpc.addSpace(space);
    }

    @Override
    public void tearDown() throws Exception
    {
        this.rpc.removeSpace(this.spaceKey);

        super.tearDown();
    }

    public void testAddModifyRemovePage() throws Exception
    {
        String title = "SomeNewPage";
        String content = "Some Content";

        // add the page
        Page p = new Page();
        p.setSpace(this.spaceKey);
        p.setTitle(title);
        p.setContent(content);
        // no id in p means storePage will add
        Page resultPage = this.rpc.storePage(p);

        String id = resultPage.getId();

        assertEquals(title, resultPage.getTitle());
        assertEquals(this.spaceKey, resultPage.getSpace());
        assertEquals(content, resultPage.getContent());
        assertNotNull(id);

        // check that the page was added using getPages
        List pages = this.rpc.getPages(this.spaceKey);
        boolean found = false;
        for (int i = 0; i < pages.size() && !found; i++) {
            PageSummary summary = (PageSummary) pages.get(i);
            if (summary.getTitle().equals(title)) {
                found = true;
                assertEquals(this.spaceKey, summary.getSpace());
            }
        }
        assertTrue("Adding page failed. There should be a page entitled \"" + title + "\" in this space", found);

        // also check that the page was added using getPage
        Page page = this.rpc.getPage(id);
        assertEquals(id, page.getId());
        assertEquals(title, page.getTitle());
        assertEquals(this.spaceKey, page.getSpace());
        assertEquals(content, page.getContent());

        // modify the page
        String newContent = "Some Other Content";
        resultPage.setContent(newContent);
        Page modifiedPage = this.rpc.storePage(resultPage);

        // check that the page was modified
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(this.spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertTrue(resultPage.getVersion() < modifiedPage.getVersion());

        // check again in a different way
        modifiedPage = this.rpc.getPage(id);
        assertEquals(id, modifiedPage.getId());
        assertEquals(title, modifiedPage.getTitle());
        assertEquals(this.spaceKey, modifiedPage.getSpace());
        assertEquals(newContent, modifiedPage.getContent());
        assertTrue(resultPage.getVersion() < modifiedPage.getVersion());

        // check page history
        List oldVersions = this.rpc.getPageHistory(id);
        assertEquals(2, oldVersions.size());

        PageHistorySummary phs0 = (PageHistorySummary) oldVersions.get(0);
        assertEquals(resultPage.getVersion(), phs0.getVersion());
        assertNotNull(phs0.getModified());
        assertNotNull(phs0.getId());

        Page page0 = this.rpc.getPage(phs0.getId());
        assertEquals(page.getContent(), page0.getContent());
        assertEquals(page.getVersion(), page0.getVersion());

        // search for the page
        // List searchResults = rpc.search(title, 1);
        // assertEquals(1, searchResults.size());
        // SearchResult searchResult = (SearchResult)searchResults.get(0);
        // assertEquals(id, searchResult.getId());
        // assertNotNull(searchResult.getExcerpt());
        // assertEquals(title, searchResult.getTitle());
        // assertEquals("page", searchResult.getType());
        // assertNotNull(searchResult.getUrl());

        // remove the page
        this.rpc.removePage(id);

        // check that the page was really removed
        pages = this.rpc.getPages(this.spaceKey);
        found = false;
        for (int i = 0; i < pages.size() && !found; i++) {
            PageSummary summary = (PageSummary) pages.get(i);
            assertFalse("Remove page failed. Page still present.", summary.getId().equals(id));
        }
    }

    public void testGetPageHistory() throws Exception
    {
        String title = "SomeOtherPage";
        String content1 = "Content v1";

        // add the page
        Page p = new Page();
        p.setSpace(this.spaceKey);
        p.setTitle(title);
        p.setContent(content1);
        p.setParentId("Main.WebHome");
        Page page1 = this.rpc.storePage(p);

        // modify the page
        String content2 = "Content v2";
        p.setContent(content2);
        p.setId(page1.getId());
        p.setVersion(page1.getVersion());
        Page page2 = this.rpc.storePage(p);

        // modify the page again
        String content3 = "Content v3";
        p.setContent(content3);
        p.setId(page2.getId());
        p.setVersion(page2.getVersion());
        Page page3 = this.rpc.storePage(p);

        // get page history
        List historyObjs = this.rpc.getPageHistory(page3.getId());
        assertEquals(3, historyObjs.size());
        PageHistorySummary phs1 = (PageHistorySummary) historyObjs.get(1);
        assertEquals(page1.getVersion() + 1, phs1.getVersion());

        // This ensures that getting the page from XmlRpc uses the date of the version, and not the current date
        // See XWIKI-2821
        Thread.sleep(2000);

        Page p1 = this.rpc.getPage(phs1.getId());
        assertEquals(page2.getVersion(), p1.getVersion());
        assertEquals(page2.getContent(), p1.getContent());
        assertEquals(page2.getCreated(), p1.getCreated());
        assertEquals(page2.getCreator(), p1.getCreator());
        assertEquals(page2.getModified(), p1.getModified());
        assertEquals(page2.getModifier(), p1.getModifier());
        assertEquals(page2.getParentId(), p1.getParentId());
        
        assertEquals(page2.getSpace(), p1.getSpace());
        assertEquals(page2.getTitle(), p1.getTitle());
        // assertFalse(page2.getUrl().equals(p1.getUrl()));

        PageHistorySummary phs2 = (PageHistorySummary) historyObjs.get(0);
        // assertEquals(page3.getVersion(), phs2.getVersion());
        Page p2 = this.rpc.getPage(phs2.getId());
        // assertEquals(page3.getVersion(), p2.getVersion());
        // assertEquals(page3.getContent(), p2.getContent());
        // assertEquals(page3.getCreated(), p2.getCreated());
        // assertEquals(page3.getCreator(), p2.getCreator());
        // assertEquals(page3.getModified(), p2.getModified());
        // assertEquals(page3.getModifier(), p2.getModifier());
        // assertEquals(page3.getParentId(), p2.getParentId());
        // assertEquals(page3.getSpace(), p2.getSpace());
        // assertEquals(page3.getTitle(), p2.getTitle());
        // assertFalse(page3.getUrl().equals(p2.getUrl()));

        // get history of page from history
        // confluence does not allow this
        // ("This is not the most recent version of this page")
        historyObjs = this.rpc.getPageHistory(p2.getId());
        // assertEquals(1, historyObjs.size());
        phs1 = (PageHistorySummary) historyObjs.get(0);
        assertEquals(page1.getVersion(), phs1.getVersion());
        p1 = this.rpc.getPage(phs1.getId());
        assertEquals(page1.getVersion(), p1.getVersion());
        assertEquals(page1.getContent(), p1.getContent());
        assertEquals(page1.getCreated(), p1.getCreated());
        assertEquals(page1.getCreator(), p1.getCreator());
        // assertEquals(page1.getModified(), p1.getModified());
        assertEquals(page1.getModifier(), p1.getModifier());
        assertEquals(page1.getParentId(), p1.getParentId());
        assertEquals(page1.getSpace(), p1.getSpace());
        assertEquals(page1.getTitle(), p1.getTitle());

        /*
         * This doesn't make sense since when we pass an id to the storePage, it automatically discards all version info
         * and takes the latest version of the page.
         */
        // p2.setContent("New content");
        // try {
        // rpc.storePage(p2);
        // fail("You should only be able to edit the latest version of a page");
        // } catch (Exception ce) {
        // // ok, continue
        // }
        // try {
        // rpc.removePage(p2.getId());
        // fail("You should not be able to remove an old version of a page");
        // } catch (Exception ce) {
        // // ok, continue
        // }
    }

    /**
     * Verify that pageId must be of the form Space.Page when the xmlrpc server is XWiki.
     */
    public void testRenderContentWithInvalidPageId()
    {
        try {
            this.rpc.renderContent("unused", "InvalidPageId", "Dummy content");
            fail("Should have received an exception here since the page id format is invalid");
        } catch (Exception expected) {
            //
        }
    }
}
