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
package org.xwiki.test.ui.xe.elements;

import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible on the Home Page.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class HomePage extends ViewPage
{
    /**
     * The part of the home page that lists the existing spaces and allows the user to create a new space.
     */
    private final SpacesPane spacesPane = new SpacesPane();

    public void gotoPage()
    {
        getUtil().gotoPage("Main", "WebHome");
    }

    public String getURL()
    {
        return getUtil().getURL("Main", "WebHome");
    }

    public boolean isOnHomePage()
    {
        return getDriver().getCurrentUrl().equals(getURL());
    }

    /**
     * @return the part of the home page that lists the existing spaces and allows the user to create a new space
     */
    public SpacesPane getSpacesPane()
    {
        return spacesPane;
    }
}
