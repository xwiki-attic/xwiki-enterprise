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
package org.xwiki.test.ui.history;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.framework.elements.editor.WikiEditPage;
import org.xwiki.test.ui.history.elements.HistoryTab;

/**
 * @version $Id$
 * @since 3.1M2
 */
public class VersionTest extends AbstractAdminAuthenticatedTest
{
    private static final String PAGE_NAME = "HistoryTest";

    private static final String SPACE_NAME = "HistorySpaceTest";

    private static final String TITLE = "Page Title";

    private static final String CONTENT1 = "First version of Content";

    private static final String CONTENT2 = "Second version of Content";

    HistoryTab historyTab = new HistoryTab();

    @Before
    @Override
    public void setUp()
    {
        super.setUp();
        getUtil().deletePage(SPACE_NAME, PAGE_NAME);
        getUtil().createPage(SPACE_NAME, PAGE_NAME, CONTENT1, TITLE);
        getUtil().gotoPage(SPACE_NAME, PAGE_NAME);
        this.historyTab.loadHistoryTab();
    }

    @Test
    public void testRollbackToFirstVersion() throws Exception
    {
        getUtil().gotoPage(SPACE_NAME, PAGE_NAME);
        ViewPage vp = new ViewPage();
        WikiEditPage wikiEditPage = vp.editWiki();
        wikiEditPage.setContent(CONTENT2);
        wikiEditPage.clickSaveAndView();
        this.historyTab.loadHistoryTab();
        this.historyTab.rollbackToVersion("1.1");
        this.historyTab.loadHistoryTab();
        Assert.assertEquals("Rollback to version 1.1", this.historyTab.getCurrentVersionComment());
        Assert.assertEquals("Administrator", this.historyTab.getCurrentAuthor());
    }
}
