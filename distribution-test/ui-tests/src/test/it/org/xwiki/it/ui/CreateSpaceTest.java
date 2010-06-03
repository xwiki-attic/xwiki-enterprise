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
import org.xwiki.it.ui.elements.HomePage;
import org.xwiki.it.ui.elements.WYSIWYGEditPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;

/**
 * Tests different ways of creating a new space.
 * 
 * @version $Id$
 * @since 2.4M1
 */
public class CreateSpaceTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The object used to access the name of the current test.
     */
    @Rule
    public final TestName testName = new TestName();

    /**
     * Tests if a new space can be created from the home page's Space dashboard.
     */
    @Test
    public void testCreateSpaceFromHomePage()
    {
        HomePage homePage = new HomePage();
        homePage.gotoPage();
        String spaceName = testName.getMethodName();
        WYSIWYGEditPage editPage = homePage.getSpacesPane().createSpace(spaceName);

        // Verify that space creation uses the space name as the space home page's title
        Assert.assertEquals(spaceName, editPage.getDocumentTitle());

        // Verify that the space created is correct by looking at the generate metadata in the HTML header
        // (they contain the space name amongst other data).
        Assert.assertEquals(spaceName, editPage.getMetaDataValue("space"));
    }
}
