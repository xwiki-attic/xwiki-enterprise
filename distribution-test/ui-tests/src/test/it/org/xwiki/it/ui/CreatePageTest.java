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
package org.xwiki.it.ui;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.xwiki.it.ui.elements.CreatePagePanel;
import org.xwiki.it.ui.elements.WYSIWYGEditPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Tests different ways of creating a new page.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class CreatePageTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to access the name of the current test.
     */
    @Rule
    public final TestName testName = new TestName();

    /**
     * Tests if a new page can be created using the create page panel.
     */
    @Test
    public void testCreatePageFromPanel()
    {
        CreatePagePanel createPagePanel = new CreatePagePanel();
        String spaceName = this.getClass().getSimpleName();
        String pageName = testName.getMethodName();
        WYSIWYGEditPage editPage = createPagePanel.createPage(spaceName, pageName);
        Assert.assertEquals(pageName, editPage.getDocumentTitle());
        Assert.assertEquals(pageName, editPage.getMetaDataValue("page"));
        Assert.assertEquals(spaceName, editPage.getMetaDataValue("space"));
    }
}
