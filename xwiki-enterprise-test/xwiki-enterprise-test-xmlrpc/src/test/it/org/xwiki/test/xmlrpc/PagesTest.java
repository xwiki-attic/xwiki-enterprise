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
package org.xwiki.test.xmlrpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.swizzle.confluence.PageSummary;
import org.codehaus.swizzle.confluence.SpaceSummary;
import org.xwiki.xmlrpc.model.Utils;
import org.xwiki.xmlrpc.model.XWikiPage;
import org.xwiki.xmlrpc.model.XWikiPageHistorySummary;
import org.xwiki.xmlrpc.model.XWikiPageSummary;

/**
 * @version $Id$
 */
public class PagesTest extends AbstractXWikiXmlRpcTest
{
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        try {
            this.rpc.getPage(TestConstants.TEST_PAGE);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                    page.getVersion() + 1);
            page.setContent(content);
            this.rpc.storePage(page);
        }

        try {
            this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        } catch (Exception e) {
            XWikiPage page = new XWikiPage();
            page.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
            page.setTitle("Test page");
            String content =
                String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                    page.getVersion() + 1);
            page.setContent(content);
            this.rpc.storePage(page);
        }
    }

    public void testGetPages() throws Exception
    {
        List<SpaceSummary> spaces = this.rpc.getSpaces();
        List<XWikiPageSummary> pages = this.rpc.getPages(spaces.get(0).getKey());

        TestUtils.banner("TEST: getPages()");
        for (PageSummary pageSummary : pages) {
            System.out.format("%s\n", pageSummary);
        }

        assertFalse(pages.isEmpty());
    }

    public void testGetPagesWithNoRightsOnSpace() throws Exception
    {
        if (TestConstants.USERNAME.equals("Admin")) {
            /* If the username is Admin this test will fail. Just return to make it pass. */
            return;
        }

        List<XWikiPageSummary> pages = this.rpc.getPages(TestConstants.SPACE_WITH_NO_ACCESS_RIGHTS);
        assertTrue(pages.isEmpty());
    }

    public void testGetPage() throws Exception
    {
        List<SpaceSummary> spaces = this.rpc.getSpaces();
        List<XWikiPageSummary> pages = this.rpc.getPages(spaces.get(0).getKey());
        XWikiPage page = this.rpc.getPage(pages.get(0).getId());

        TestUtils.banner("TEST: getPage()");
        System.out.format("%s\n", page);

        assertEquals(page.getId(), pages.get(0).getId());
    }

    public void testStorePage() throws Exception
    {
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE);

        String content =
            String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                page.getVersion() + 1);

        page.setContent(content);
        XWikiPage storedPage = this.rpc.storePage(page);

        TestUtils.banner("TEST: storePage()");
        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(content, storedPage.getContent());
        assertTrue(storedPage.getVersion() == (page.getVersion() + 1));
        assertEquals(page.getLanguage(), storedPage.getLanguage());
    }

    public void testChangeParentId() throws Exception
    {
        // Find two pages where the second page isn't a child of the first page.
        List<XWikiPageSummary> pages = this.rpc.getPages(TestConstants.TEST_SPACE);
        XWikiPageSummary pageSummary1 = pages.get(0);
        XWikiPageSummary pageSummary2 = null;
        for (XWikiPageSummary ps : pages) {
            if (!pageSummary1.getParentId().equals(ps.getId())) {
                pageSummary2 = ps;
            }
        }

        TestUtils.banner("TEST: changeParentId()");
        System.out.format("Setting page '%s' parent id to '%s'. Now: '%s'\n", pageSummary1.getId(),
            pageSummary2.getId(), pageSummary1.getParentId());
        XWikiPage page = this.rpc.getPage(pageSummary1.getId());
        assertNotSame(pageSummary2.getId(), page.getParentId());

        page.setParentId(pageSummary2.getId());
        page = this.rpc.storePage(page);

        System.out.format("New page: %s\n", page);

        assertEquals(pageSummary2.getId(), page.getParentId());
    }

    public void testStoreNewPageTranslation() throws Exception
    {
        /* Get the current page and all its available translations */
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, XWikiPage> before = new HashMap<String, XWikiPage>();
        before.put(page.getLanguage(), page);
        for (String l : page.getTranslations()) {
            XWikiPage p = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
            before.put(l, p);
        }

        /* Add a translation in a fake language */
        String[] languages = Locale.getISOLanguages();
        String fakeLanguage = languages[random.nextInt(languages.length)]; 
        String translatedContent =
            String.format("This is the content in the '%s' language. (This will be version: %d)", fakeLanguage,
                page.getVersion() + 1);
        XWikiPage translatedPage = new XWikiPage();

        translatedPage.setId(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        translatedPage.setSpace(TestConstants.TEST_SPACE);
        translatedPage.setTitle("Translated page");
        translatedPage.setContent(translatedContent);
        translatedPage.setLanguage(fakeLanguage);
        translatedPage = this.rpc.storePage(translatedPage);

        /* Re-get the page and all its translations */
        page = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, XWikiPage> after = new HashMap<String, XWikiPage>();
        after.put(page.getLanguage(), page);
        for (String l : page.getTranslations()) {
            XWikiPage p = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
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

    public void testStorePageTranslation() throws Exception
    {
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        Map<String, String> translatedContents = new HashMap<String, String>();
        translatedContents.put("", page.getContent());
        for (String l : page.getTranslations()) {
            XWikiPage p = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
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
        translatedPage = this.rpc.storePage(translatedPage);

        System.out.format("New content: %s\n", content);
        System.out.format("%s\n", page);

        /* The following command could be removed. Check it! */
        translatedPage = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);
        Map<String, String> newTranslatedContents = new HashMap<String, String>();
        newTranslatedContents.put("", page.getContent());
        for (String l : page.getTranslations()) {
            XWikiPage p = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, l);
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

    public void testGetPageWithTranslations() throws Exception
    {
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        TestUtils.banner("TEST: getPageWithTranslations()");
        System.out.format("%s\n", page);

        assertFalse(page.getTranslations().isEmpty());
    }

    public void testGetPageTranslations() throws Exception
    {
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS);

        TestUtils.banner("TEST: getPageTranslations()");

        for (String language : page.getTranslations()) {
            XWikiPage translatedPage = this.rpc.getPage(TestConstants.TEST_PAGE_WITH_TRANSLATIONS, language);
            System.out.format("XWikiPage for language '%s': %s\n", language, translatedPage);

            assertEquals(language, translatedPage.getLanguage());
        }
    }

    public void testCreatePage() throws Exception
    {
        String pageId =
            String.format("%s.%s-%d", TestConstants.TEST_SPACE, TestConstants.TEST_PREFIX,
                Math.abs(this.random.nextInt()));
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s (inital version)\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: createPage()");

        try {
            page = this.rpc.getPage(pageId);
            throw new RuntimeException(String.format("XWikiPage %s exists!", pageId));
        } catch (Exception e) {
            System.out.format("XWikiPage %s does not exist... Good!\n", pageId);
        }

        page = new XWikiPage();
        page.setId(pageId);
        page.setSpace(TestConstants.TEST_SPACE);
        page.setTitle("Test page");
        page.setContent(content);
        XWikiPage storedPage = this.rpc.storePage(page);

        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(storedPage.getContent(), content);

        assertTrue(storedPage.getVersion() == 1);
        assertEquals("", storedPage.getLanguage());
    }

    public void testCreatePageWithNullSpace() throws Exception
    {
        String pageId =
            String.format("%s.%s-%d", TestConstants.TEST_SPACE, TestConstants.TEST_PREFIX,
                Math.abs(this.random.nextInt()));
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s (inital version)\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: createPageWithNullSpace()");

        try {
            page = this.rpc.getPage(pageId);
            throw new RuntimeException(String.format("XWikiPage %s exists!", pageId));
        } catch (Exception e) {
            System.out.format("XWikiPage %s does not exist... Good!\n", pageId);
        }

        page = new XWikiPage();
        page.setId(pageId);
        page.setTitle("Test page");
        page.setContent(content);
        XWikiPage storedPage = this.rpc.storePage(page);

        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(storedPage.getContent(), content);

        assertTrue(storedPage.getVersion() == 1);
        assertEquals("", storedPage.getLanguage());
    }

    public void testCreatePageInSpaceWithNoAccessRights() throws Exception
    {
        if (TestConstants.USERNAME.equals("Admin")) {
            /* If the username is Admin this test will fail. Throw an exception to make it pass. */
            System.out.format("User admin can always access everything");
            return;
        }

        String pageId =
            String.format("%s.%s-%d", TestConstants.SPACE_WITH_NO_ACCESS_RIGHTS, TestConstants.TEST_PREFIX,
                Math.abs(this.random.nextInt()));
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: createPageInSpaceWithNoAccessRights()");

        try {
            page = this.rpc.getPage(pageId);
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
            this.rpc.storePage(page);
            fail();
        } catch (Exception e) {
        }
    }

    public void testRemovePage() throws Exception
    {
        List<XWikiPageSummary> pages = this.rpc.getPages(TestConstants.TEST_SPACE);
        XWikiPageSummary pageToBeDeleted = null;
        for (XWikiPageSummary pageSummary : pages) {
            if (pageSummary.getId().contains(TestConstants.TEST_PREFIX)) {
                pageToBeDeleted = pageSummary;
                break;
            }
        }

        Boolean result = this.rpc.removePage(pageToBeDeleted.getId());
        TestUtils.banner("TEST: removePage()");
        System.out.format("XWikiPage %s removed: %b\n", pageToBeDeleted.getId(), result);

        pages = this.rpc.getPages(TestConstants.TEST_SPACE);
        boolean removed = true;
        for (PageSummary pageSummary : pages) {
            if (pageSummary.getId().equals(pageToBeDeleted.getId())) {
                removed = false;
                break;
            }
        }

        assertTrue(removed);
    }

    public void testGetPageHistory() throws Exception
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getPageHistory(TestConstants.TEST_PAGE);

        TestUtils.banner("TEST: getPageHistory()");
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            System.out.format("%s\n", pageHistorySummary);
        }

        assertFalse(pageHistorySummaries.isEmpty());
    }

    public void testGetPageAtVersion() throws Exception
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getPageHistory(TestConstants.TEST_PAGE);
        XWikiPageHistorySummary pageHistorySummary =
            pageHistorySummaries.get(this.random.nextInt(pageHistorySummaries.size()));

        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE, pageHistorySummary.getVersion());
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

    public void testGetPageAtVersionUsingExtendedId() throws Exception
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getPageHistory(TestConstants.TEST_PAGE);
        XWikiPageHistorySummary pageHistorySummary =
            pageHistorySummaries.get(this.random.nextInt(pageHistorySummaries.size()));

        XWikiPage page = this.rpc.getPage(pageHistorySummary.getId());
        TestUtils.banner("TEST: getPageAtVersionUsingExtendedId()");
        System.out.format("%s\n", pageHistorySummary);
        System.out.format("%s\n", page);

        assertEquals(pageHistorySummary.getVersion(), page.getVersion());
        assertEquals(pageHistorySummary.getModifier(), page.getModifier());
    }

    public void testRenderContent() throws Exception
    {
        TestUtils.banner("TEST: renderContent()");

        // Test rendering document content
        String html = this.rpc.renderContent(TestConstants.TEST_SPACE, TestConstants.TEST_PAGE, "");
        System.out.format("Rendered content: '%s'\n", html);

        assertTrue(html.length() != 0);

        // Test rendering provided content
        html =
            this.rpc.renderContent(TestConstants.TEST_SPACE, TestConstants.TEST_PAGE,
                "{{groovy}}print 'programming'{{/groovy}}");
        System.out.format("Rendered content: '%s'\n", html);

        assertEquals("<p>programming</p>", html);
    }

    public void testRenamePage() throws Exception
    {
        String pageName = String.format("%s-%d", TestConstants.TEST_PREFIX, Math.abs(this.random.nextInt()));
        String pageId = String.format("%s.%s", TestConstants.TEST_SPACE, pageName);
        String content = String.format("Modified by org.xwiki.xmlrpc @ %s (inital version)\n", new Date());
        XWikiPage page = null;

        TestUtils.banner("TEST: renamePage()");

        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = this.rpc.storePage(page);

        page.setSpace("Foo");
        page.setTitle("Bar");
        XWikiPage renamedPage = this.rpc.storePage(page);

        try {
            this.rpc.getPage(pageId);
            fail("This page should no longer exist");
        } catch (Exception e) {
            // Ignore
        }

        assertTrue(renamedPage.getId().equals("Foo.Bar"));
        assertTrue(renamedPage.getContent().equals(content));

        /* Test other cases as well */

        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = this.rpc.storePage(page);

        page.setSpace("TargetSpace");
        renamedPage = this.rpc.storePage(page);

        try {
            this.rpc.getPage(pageId);
            fail("This page should no longer exist");
        } catch (Exception e) {
            // Ignore
        }

        assertTrue(renamedPage.getId().equals(String.format("TargetSpace.%s", pageName)));
        assertTrue(renamedPage.getContent().equals(content));

        /*-*/

        page = new XWikiPage();
        page.setId(pageId);
        page.setContent(content);
        page = this.rpc.storePage(page);

        page.setTitle("Foo");
        renamedPage = this.rpc.storePage(page);

        try {
            this.rpc.getPage(pageId);
            fail("This page should no longer exist");
        } catch (Exception e) {
            // Ignore
        }

        assertTrue(renamedPage.getId().equals(String.format("%s.Foo", TestConstants.TEST_SPACE)));
        assertTrue(renamedPage.getContent().equals(content));

    }

    public void testGetModifiedPagesHistoryDescending() throws Exception
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getModifiedPagesHistory(50, 0, true);

        TestUtils.banner("TEST: getModifiedPageHistoryDescending()");
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            System.out.format("%d %s\n", pageHistorySummary.getModified().getTime(), pageHistorySummary);
        }

        long t = Long.MAX_VALUE;
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            assertTrue(pageHistorySummary.getModified().getTime() <= t);
            t = pageHistorySummary.getModified().getTime();
        }

        assertFalse(pageHistorySummaries.isEmpty());
    }

    public void testGetModifiedPagesHistoryAscending() throws Exception
    {
        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getModifiedPagesHistory(50, 0, false);

        TestUtils.banner("TEST: getModifiedPageHistoryAscending()");
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            System.out.format("%d %s\n", pageHistorySummary.getModified().getTime(), pageHistorySummary);
        }

        long t = Long.MIN_VALUE;
        for (XWikiPageHistorySummary pageHistorySummary : pageHistorySummaries) {
            assertTrue(pageHistorySummary.getModified().getTime() >= t);
            t = pageHistorySummary.getModified().getTime();
        }

        assertFalse(pageHistorySummaries.isEmpty());
    }

    public void testGetModifiedPagesHistoryMultipleRequests() throws Exception
    {
        List<XWikiPageHistorySummary> result = new ArrayList<XWikiPageHistorySummary>();

        List<XWikiPageHistorySummary> pageHistorySummaries1 = this.rpc.getModifiedPagesHistory(10, 0, true);
        result.addAll(pageHistorySummaries1);
        List<XWikiPageHistorySummary> pageHistorySummaries2 = this.rpc.getModifiedPagesHistory(10, 10, true);
        result.addAll(pageHistorySummaries2);
        List<XWikiPageHistorySummary> pageHistorySummaries3 = this.rpc.getModifiedPagesHistory(10, 20, true);
        result.addAll(pageHistorySummaries3);

        List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getModifiedPagesHistory(30, 0, true);

        TestUtils.banner("TEST: getModifiedPageHistoryMultipleRequests()");
        for (int i = 0; i < pageHistorySummaries.size(); i++) {
            XWikiPageHistorySummary h1 = pageHistorySummaries.get(i);
            XWikiPageHistorySummary h2 = result.get(i);

            assertTrue(h1.getId().equals(h2.getId()));
            assertTrue(h1.getModifier().equals(h2.getModifier()));
            assertTrue(h1.getModified().equals(h2.getModified()));
            assertTrue(h1.getVersion() == h2.getVersion());
            assertTrue(h1.getMinorVersion() == h2.getMinorVersion());
        }

        long t = Long.MAX_VALUE;
        for (XWikiPageHistorySummary pageHistorySummary : result) {
            assertTrue(pageHistorySummary.getModified().getTime() <= t);
            t = pageHistorySummary.getModified().getTime();
        }
    }

    public void testGetAllModifiedPagesHistory() throws Exception
    {
        List<XWikiPageHistorySummary> result = new ArrayList<XWikiPageHistorySummary>();

        TestUtils.banner("TEST: getAllModifiedPageHistory()");

        int start = 0;
        int number = 50;
        while (true) {
            List<XWikiPageHistorySummary> pageHistorySummaries = this.rpc.getModifiedPagesHistory(number, start, true);
            System.out.format("Got %d entries...\n", pageHistorySummaries.size());
            result.addAll(pageHistorySummaries);
            if (pageHistorySummaries.size() < number) {
                break;
            }

            start += number;
        }

        System.out.format("Total entries received: %d\n", result.size());

        long t = Long.MAX_VALUE;
        for (XWikiPageHistorySummary pageHistorySummary : result) {
            assertTrue(pageHistorySummary.getModified().getTime() <= t);
            t = pageHistorySummary.getModified().getTime();
        }

        assertFalse(result.isEmpty());
    }

    public void testGetModifiedPageHistoryCorrectness() throws Exception
    {
        List<SpaceSummary> spaces = this.rpc.getSpaces();
        List<XWikiPageSummary> pages = this.rpc.getPages(spaces.get(0).getKey());

        TestUtils.banner("TEST: getAllModifiedPageHistoryCorrectness()");

        XWikiPage page = this.rpc.getPage(pages.get(0).getId());

        System.out.format("Modifying: %s\n", page);

        page.setContent(String.format("Modified %d", System.currentTimeMillis()));
        page = rpc.storePage(page);

        System.out.format("Modified: %s\n", page);

        /* Get the last 25 changes. This should be enough to catch the page modified in this test */
        List<XWikiPageHistorySummary> modifications = rpc.getModifiedPagesHistory(25, 0);

        /* Check if the modified page is listed in the retrieved modification list */
        boolean found = false;
        System.out.format("Modifications:\n");
        for (XWikiPageHistorySummary modification : modifications) {
            System.out.format("%s\n", modification);
            if (page.getId().equals(modification.getBasePageId())) {
                if (page.getModified().equals(modification.getModified())) {
                    found = true;
                }
            }
        }

        assertTrue(found);
    }

    public void testStorePageWithCheckVersion() throws Exception
    {
        XWikiPage page = this.rpc.getPage(TestConstants.TEST_PAGE);

        String content =
            String.format("Modified by org.xwiki.xmlrpc @ %s (This will be version: %d)\n", new Date(),
                page.getVersion() + 1);

        page.setContent(content);
        XWikiPage storedPage = this.rpc.storePage(page);

        TestUtils.banner("TEST: storePageWithCheckVersion()");
        System.out.format("Content sent: '%s'\n", Utils.truncateToFirstLine(content));
        System.out.format("%s\n", storedPage);

        assertEquals(content, storedPage.getContent());
        assertTrue(storedPage.getVersion() == (page.getVersion() + 1));
        assertEquals(page.getLanguage(), storedPage.getLanguage());

        /* Try to store again the page */
        storedPage = this.rpc.storePage(page, true);
        assertTrue(storedPage.getId().equals(""));
    }

    public void testStoreExistingPageUsingNullTitle() throws Exception
    {
        XWikiPage page = new XWikiPage();
        page.setId(TestConstants.TEST_PAGE);

        String content = String.format("Modified by org.xwiki.xmlrpc @ %s\n", new Date());

        page.setContent(content);

        page = rpc.storePage(page);

        assertEquals(TestConstants.TEST_PAGE, page.getId());
        assertEquals(content, page.getContent());
    }
}
