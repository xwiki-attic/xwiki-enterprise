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
package org.xwiki.test.ui.watchlist;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.po.AbstractTest;
import org.xwiki.test.po.administration.ProfileUserProfilePage;
import org.xwiki.test.po.watchlist.WatchlistUserProfilePage;

/**
 * Tests Watchlist application features.
 * 
 * @version $Id$
 */
public class AutoWatchTest extends AbstractTest
{
    private WatchlistUserProfilePage watchlistPage;

    private String testSpace;

    @Before
    public void setUp()
    {
        String userName = RandomStringUtils.randomAlphanumeric(5);

        ProfileUserProfilePage profilePage = new ProfileUserProfilePage(userName);

        getUtil().registerLoginAndGotoPage(profilePage.getUsername(), "password", profilePage.getURL());

        this.watchlistPage = profilePage.switchToWatchlist();

        this.testSpace = this.watchlistPage.getUsername() + "Test";
    }

    /**
     * Tests that a scheduler job page default edit mode is "inline".
     */
    @Test
    public void testAutomaticWatchNewPage()
    {
        // create a new page
        getUtil().createPage(this.testSpace, "testpage", null, null);

        // go back to watchlist profile
        this.watchlistPage = WatchlistUserProfilePage.gotoPage(this.watchlistPage.getUsername());

        // check if it's registered in the watchlist
        Assert
            .assertTrue("Newly created page is not watched", this.watchlistPage.isWatched(this.testSpace, "testpage"));
    }
}
