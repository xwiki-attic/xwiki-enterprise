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
package org.xwiki.test.ui.xe;

import org.junit.Assert;
import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.LiveTableElement;
import org.xwiki.test.ui.xe.elements.AllDocsPage;

/**
 * Tests for the AllDocs page.
 * 
 * @version $Id$
 * @since 2.4M2
 */
public class AllDocsTest extends AbstractAdminAuthenticatedTest
{
    @Test
    public void testTableViewActions() throws Exception
    {
        AllDocsPage page = new AllDocsPage();
        page.gotoPage();

        // Test 1: Verify that the Action column is displayed only for administrators.
        LiveTableElement livetable = page.clickIndexTab();
        Assert.assertTrue("No Actions column found", livetable.hasColumn("Actions"));
        page.logout();
        livetable = page.clickIndexTab();
        Assert.assertFalse("Actions column shouldn't be visible for guests", livetable.hasColumn("Actions"));
        page.loginAsAdmin();

        // Test 2: Verify filtering works by filtering on the document name
        // TODO: the line below fails from time to time. Fix it.
        livetable = page.clickIndexTab();
        // TODO: it seems that it doesn't work sometimes. Fix it.
        livetable.filterColumn("xwiki-livetable-alldocs-filter-1", "Treeview");
    }
}
