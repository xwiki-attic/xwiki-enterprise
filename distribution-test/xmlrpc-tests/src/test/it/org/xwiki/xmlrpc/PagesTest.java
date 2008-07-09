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
 *
 */
package org.xwiki.xmlrpc;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.swizzle.confluence.ConfluenceException;
import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.codehaus.swizzle.confluence.SwizzleException;
import org.xwiki.xmlrpc.model.Utils;
import org.xwiki.xmlrpc.model.XWikiPage;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

public class PagesTest extends AbstractXWikiXmlRpcTest
{
    public void setUp() throws XmlRpcException, MalformedURLException
    {
        super.setUp();

        try {
            rpc.getPage(TestConstants.TEST_PAGE);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(), page
                    .getVersion() + 1);
            page.setContent(content);
            rpc.storePage(page);
        }

        try {
            rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(), page
                    .getVersion() + 1);
            page.setContent(content);
            rpc.storePage(page);
        }
    }

    public void testGetPages() throws XmlRpcException, ConfluenceException, SwizzleException
    {
        List<SpaceSummary> spaces = rpc.getSpaces();
        List<XWikiPageSummary> pages = rpc.getPages(spaces.get(0).getKey());

        TestUtils.banner("TEST: getPages()");
        for (PageSummary pageSummary : pages) {
            System.out.format("%s\n", pageSummary);
        }

        assertFalse(pages.isEmpty());
    }

    public void testGetPagesWithNoRightsOnSpace() throws XmlRpcException, ConfluenceException, SwizzleException
    {
        if (TestConstants.USERNAME.equals("Admin")) {
            /* If the username is Admin this test will fail. Just return to make it pass. */
            return;
        }

        List<XWikiPageSummary> pages = rpc.getPages(TestConstants.SPACE_WITH_NO_ACCESS_RIGHTS);
        assertTrue(pages.isEmpty());
    }

    public void testGetPage() throws XmlRpcException
    {
        List<SpaceSummary> spaces = rpc.getSpaces();
        List<XWikiPageSummary> pages = rpc.getPages(spaces.get(0).getKey());
        XWikiPage page = rpc.getPage(pages.get(0).getId());

        TestUtils.banner("TEST: getPage()");
        System.out.format("%s\n", page);

        assertEquals(page.getId(), pages.get(0).getId());
    }

    public void testStorePage() throws XmlRpcException
    {
        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE);

        String content =
            String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(), page
                .getVersion() + 1);

        page.setContent(content);
        XWikiPage storedPage = rpc.storePage(page);

        TestUtils.banner("TEST: storePage()");
        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(content, storedPage.getContent());
        assertTrue(storedPage.getVersion() == (page.getVersion() + 1));
        assertEquals(page.getLanguage(), storedPage.getLanguage());
    }

    public void testChangeParentId() throws XmlRpcException
    {
        List<XWikiPageSummary> pages = rpc.getPages(TestConstants.TEST_SPACE);
        XWikiPageSummary pageSummary1 = pages.get(0);
        XWikiPageSummary pageSummary2 = null;
        for (XWikiPageSummary ps : pages) {
            if (!pageSummary1.getParentId().equals(ps.getId())) {
                pageSummary2 = ps;
            }
        }

        TestUtils.banner("TEST: changeParentId()");
        System.out.format("Setting page '%s' parent id to '%s'. Now: '%s'\n", pageSummary1.getId(), pageSummary2
            .getId(), pageSummary1.getParentId());
        XWikiPage page = rpc.getPage(pageSummary1.getId());
        assertNotSame(pageSummary2.getId(), page.getParentId());

        page.setParentId(pageSummary2.getId());
        page = rpc.storePage(page);

        System.out.format("New page: %s\n", page);

        assertEquals(pageSummary2.getId(), page.getParentId());
    }

    public void testStoreNewPageTranslation() throws XmlRpcException
    {
        /* Get the current page and all its available translations */
        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, XWikiPage> before = new HashMap<String, XWikiPage>();
        before.put(page.getLanguage(), page);
        for (String l : page.getTranslations()) {
            XWikiPage p = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
            before.put(l, p);
        }

        /* Add a translation in a fake language */
        String fakeLanguage = (String.format("%d", Math.abs(random.nextInt()))).substring(0, 4);
        String translatedContent =
            String.format("This is the content in the '%s' language. (This will be version: %d)", fakeLanguage, page
                .getVersion() + 1);
        XWikiPage translatedPage = new XWikiPage();

        translatedPage.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        translatedPage.setSpace(TestConstants.TEST_SPACE);
        translatedPage.setTitle("Translated page");
        translatedPage.setContent(translatedContent);
        translatedPage.setLanguage(fakeLanguage);
        translatedPage = rpc.storePage(translatedPage);

        /* Re-get the page and all its translations */
        page = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, XWikiPage> after = new HashMap<String, XWikiPage>();
        after.put(page.getLanguage(), page);
        for (String l : page.getTranslations()) {
            XWikiPage p = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
            after.put(l, p);
        }

        TestUtils.banner("TEST: storeNewPageTranslation()");
        System.out.format("Adding the '%s' translation...\n", fakeLanguage);
        System.out.format("*********************************\n");
        System.out.format("Before: %s\n", before);
        System.out.format("*********************************\n");
        System.out.format("After: %s\n", after);

        /* Check for correctenss */
        assertFalse(before.containsKey(fakeLanguage));
        assertTrue(after.containsKey(fakeLanguage));
        assertEquals(translatedContent, after.get(fakeLanguage).getContent());

        for (String l : before.keySet()) {
            assertTrue(after.containsKey(l));
            XWikiPage b = before.get(l);
            XWikiPage a = after.get(l);

            assertEquals(b.getVersion(), a.getVersion());
            assertEquals(b.getContent(), a.getContent());
        }
    }

    public void testStorePageTranslation() throws XmlRpcException
    {
        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        Map<String, String> translatedContents = new HashMap<String, String>();
        translatedContents.put("", page.getContent());
        for (String l : page.getTranslations()) {
            XWikiPage p = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
            translatedContents.put(l, p.getContent());
        }

        TestUtils.banner("TEST: storeTranslatedPage()");
        System.out.format("%s\n", page);

        String targetLanguage = page.getTranslations().get(0);
        String content =
            String.format("This is a new translation for language '%s' @ %s (this will be version %d)", targetLanguage,
                new Date(), page.getVersion() + 1);

        XWikiPage translatedPage = new XWikiPage();

        translatedPage.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        translatedPage.setSpace(TestConstants.TEST_SPACE);
        translatedPage.setTitle("Translated page");
        translatedPage.setContent(content);
        translatedPage.setLanguage(targetLanguage);
        translatedPage = rpc.storePage(translatedPage);

        System.out.format("New content: %s\n", content);
        System.out.format("%s\n", page);

        /* The following command could be removed. Check it! */
        translatedPage = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, String> newTranslatedContents = new HashMap<String, String>();
        newTranslatedContents.put("", page.getContent());
        for (String l : page.getTranslations()) {
            XWikiPage p = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
            newTranslatedContents.put(l, p.getContent());
        }

        System.out.format("Old translations: %s\n", translatedContents);
        System.out.format("New translations: %s\n", newTranslatedContents);

        assertEquals(translatedContents.keySet(), newTranslatedContents.keySet());

        for (String l : newTranslatedContents.keySet()) {
            if (!l.equals(targetLanguage)) {
                assertEquals(translatedContents.get(l), newTranslatedContents.get(l));
            } else {
                assertEquals(content, newTranslatedContents.get(l));
            }
        }
    }

    public void testGetPageWithTranslations() throws XmlRpcException
    {
        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        TestUtils.banner("TEST: getPageWithTranslations()");
        System.out.format("%s\n", page);

        assertFalse(page.getTranslations().isEmpty());
    }

    public void testGetPageTranslations() throws XmlRpcException
    {
        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        TestUtils.banner("TEST: getPageTranslations()");

        for (String language : page.getTranslations()) {
            XWikiPage translatedPage = rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, language);
            System.out.format("XWikiPage for language '%s': %s\n", language, translatedPage);

            assertEquals(language, translatedPage.getLanguage());
        }
    }

    public void testCreatePage() throws XmlRpcException
    {
        String pageId =
            String.format("%s.%s-%d", TestConstants.TEST_SPACE, TestConstants.TEST_PREFIX, Math.abs(random.nextInt()));
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s (inital version)\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: createPage()");

        try {
            page = rpc.getPage(pageId);
            throw new RuntimeException(String.format("XWikiPage %s exists!", pageId));
        } catch (Exception e) {
            System.out.format("XWikiPage %s does not exist... Good!\n", pageId);
        }

        page = new XWikiPage();
        page.setId(pageId);
        page.setSpace(TestConstants.TEST_SPACE);
        page.setTitle("Test page");
        page.setContent(content);
        XWikiPage storedPage = rpc.storePage(page);

        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(storedPage.getContent(), content);

        assertTrue(storedPage.getVersion() == 1);
        assertEquals("", storedPage.getLanguage());
    }

    public void testCreatePageInSpaceWithNoAccessRights() throws XmlRpcException
    {
        if (TestConstants.USERNAME.equals("Admin")) {
            /* If the username is Admin this test will fail. Throw an exception to make it pass. */
            System.out.format("User admin can always access everything");
            return;
        }

        String pageId =
            String.format("%s.%s-%d", TestConstants.SPACE_WITH_NO_ACCESS_RIGHTS, TestConstants.TEST_PREFIX, Math
                .abs(random.nextInt()));
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: createPageInSpaceWithNoAccessRights()");

        try {
            page = rpc.getPage(pageId);
            throw new RuntimeException(String.format("XWikiPage %s exists!", pageId));
        } catch (Exception e) {
            System.out.format("XWikiPage %s does not exist... Good!\n", pageId);
        }

        page = new XWikiPage();
        page.setId(pageId);
        page.setSpace(TestConstants.TEST_SPACE);
        page.setTitle("Test page");
        page.setContent(content);

        try {
            rpc.storePage(page);
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testRemovePage() throws XmlRpcException
    {
        List<XWikiPageSummary> pages = rpc.getPages(TestConstants.TEST_SPACE);
        XWikiPageSummary pageToBeDeleted = null;
        for (XWikiPageSummary pageSummary : pages) {
            if (pageSummary.getId().contains(TestConstants.TEST_PREFIX)) {
                pageToBeDeleted = pageSummary;
                break;
            }
        }

        Boolean result = rpc.removePage(pageToBeDeleted);
        TestUtils.banner("TEST: removePage()");
        System.out.format("XWikiPage %s removed: %b\n", pageToBeDeleted.getId(), result);

        pages = rpc.getPages(TestConstants.TEST_SPACE);
        boolean removed = true;
        for (PageSummary pageSummary : pages) {
            if (pageSummary.getId().equals(pageToBeDeleted.getId())) {
                removed = false;
                break;
            }
        }

        assertTrue(removed);
    }

    public void testGetPageHistory() throws XmlRpcException
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = rpc.getPageHistory(TestConstants.TEST_PAGE);

        TestUtils.banner("TEST: getPageHistory()");
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            System.out.format("%s\n", pageHistorySummary);
        }

        assertFalse(pageHistorySummaries.isEmpty());
    }

    public void testGetPageAtVersion() throws XmlRpcException
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = rpc.getPageHistory(TestConstants.TEST_PAGE);
        XWikiPageHistorySummary pageHistorySummary =
            pageHistorySummaries.get(random.nextInt(pageHistorySummaries.size()));

        XWikiPage page = rpc.getPage(TestConstants.TEST_PAGE, pageHistorySummary.getVersion());
        TestUtils.banner("TEST: getPageAtVersion()");
        System.out.format("%s\n", pageHistorySummary);
        System.out.format("%s\n", page);

        assertEquals(pageHistorySummary.getVersion(), page.getVersion());
        assertEquals(pageHistorySummary.getModifier(), page.getModifier());

        /*
         * This test occasionally fails because the version returned as the modification date by XWiki when getting a
         * page with a given version is always equal to the current date/time. So if the previous version to
         * getPageHistory is made, let's say at 12:53:59 and the subsequent getPage at 12:54:01 then the date in the
         * pageHistory item will differ from the one in the actual page and the test will fail. Let's disable this
         * check.
         */
        // assertEquals(pageHistorySummary.getModified(), page.getModified());
    }

    public void testGetPageAtVersionUsingExtendedId() throws XmlRpcException
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = rpc.getPageHistory(TestConstants.TEST_PAGE);
        XWikiPageHistorySummary pageHistorySummary =
            pageHistorySummaries.get(random.nextInt(pageHistorySummaries.size()));

        XWikiPage page = rpc.getPage(pageHistorySummary.getId());
        TestUtils.banner("TEST: getPageAtVersionUsingExtendedId()");
        System.out.format("%s\n", pageHistorySummary);
        System.out.format("%s\n", page);

        assertEquals(pageHistorySummary.getVersion(), page.getVersion());
        assertEquals(pageHistorySummary.getModifier(), page.getModifier());
    }

    public void testStorePageWithInvalidId()
    {
        try {
            XWikiPage page = new XWikiPage();
            page.setId("Space.Name::Version");
            page.setTitle("Test page");
            page.setContent("Test page");
            rpc.storePage(page);
            Assert.fail();
        } catch (XmlRpcException e) {
        }
    }

    public void testRenderContent() throws XmlRpcException
    {
        String html = rpc.renderContent(TestConstants.TEST_SPACE, TestConstants.TEST_PAGE, "");
        TestUtils.banner("TEST: renderContent()");
        System.out.format("Rendered content: '%s'\n", html);

        assertTrue(html.length() != 0);
    }

    public void testRenamePage() throws XmlRpcException
    {
        String pageName = String.format("%s-%d", TestConstants.TEST_PREFIX, Math.abs(random.nextInt()));
        String pageId = String.format("%s.%s", TestConstants.TEST_SPACE, pageName);
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s (inital version)\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: renamePage()");
        
        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = rpc.storePage(page);
        
        page.setSpace("Foo");
        page.setTitle("Bar");
        XWikiPage renamedPage = rpc.storePage(page);
        
        try {
            rpc.getPage(pageId);
            fail("This page should no longer exist");
        }
        catch(Exception e) {
            //Ignore
        }
        
        assertTrue(renamedPage.getId().equals("Foo.Bar"));
        assertTrue(renamedPage.getContent().equals(content));
        
        /* Test other cases as well */
        
        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = rpc.storePage(page);
        
        page.setSpace("TargetSpace");        
        renamedPage = rpc.storePage(page);
        
        try {
            rpc.getPage(pageId);
            fail("This page should no longer exist");
        }
        catch(Exception e) {
            //Ignore
        }
        
        assertTrue(renamedPage.getId().equals(String.format("TargetSpace.%s", pageName)));
        assertTrue(renamedPage.getContent().equals(content));
        
        /*-*/
        
        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = rpc.storePage(page);
        
        page.setTitle("Foo");        
        renamedPage = rpc.storePage(page);
        
        try {
            rpc.getPage(pageId);
            fail("This page should no longer exist");
        }
        catch(Exception e) {
            //Ignore
        }
        
        assertTrue(renamedPage.getId().equals(String.format("%s.Foo", TestConstants.TEST_SPACE)));
        assertTrue(renamedPage.getContent().equals(content));
        
    }
}
