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
package org.xwiki.it.ui.xe.elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.it.ui.framework.elements.LiveTableElement;
import org.xwiki.it.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible on the AllDocs Page.
 *
 * @version $Id$
 * @since 2.4M2
 */
public class AllDocsPage extends ViewPage
{
    @FindBy(xpath = "//li[@id='xwikiindex']/a")
    private WebElement indexTab;

    public void gotoPage()
    {
        getUtil().gotoPage("Main", "AllDocs");
    }

    public LiveTableElement clickIndexTab()
    {
        this.indexTab.click();
        LiveTableElement lt = new LiveTableElement("alldocs");

        // Since there's a risk that the livetbale has finished dislaying before the listener
        // (defined in LiveTableElement's constructor) has been set up, we force a livetable refresh.
        executeJavascript("livetable.clearCache();livetable.showRows(livetable.currentOffset, livetable.limit);");

        lt.waitUntilReady();
        return lt;
    }
}
