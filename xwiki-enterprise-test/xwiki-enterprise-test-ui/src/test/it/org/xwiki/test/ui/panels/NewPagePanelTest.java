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
package org.xwiki.test.ui.panels;

import org.junit.Assert;
import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.panels.elements.NewPagePanel;

/**
 * Test page creation using the NewPage Panel.
 * 
 * @version $Id$
 * @since 2.5RC1
 */
public class NewPagePanelTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Tests if a new page can be created using the create page panel.
     */
    @Test
    public void testCreatePageFromPanel()
    {
        NewPagePanel newPagePanel = NewPagePanel.gotoPage();

        WYSIWYGEditPage editPage = newPagePanel.createPage(getTestClassName(), getTestMethodName());

        Assert.assertEquals(getTestMethodName(), editPage.getDocumentTitle());
        Assert.assertEquals(getTestMethodName(), editPage.getMetaDataValue("page"));
        Assert.assertEquals(getTestClassName(), editPage.getMetaDataValue("space"));
    }
}
