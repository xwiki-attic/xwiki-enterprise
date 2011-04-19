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
package org.xwiki.test.ui.framework.elements.editor.wysiwyg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.xwiki.test.ui.framework.elements.BaseElement;

/**
 * Models the rich text area of the WYSIWYG content editor.
 * 
 * @version $Id$
 * @since 3.0M2
 */
public class RichTextAreaElement extends BaseElement
{
    /**
     * The in-line frame element.
     */
    private final WebElement iframe;

    /**
     * Creates a new rich text area element.
     * 
     * @param iframe the in-line frame used by the rich text area
     */
    public RichTextAreaElement(WebElement iframe)
    {
        this.iframe = iframe;
    }

    /**
     * @return the content of the rich text area
     */
    public String getContent()
    {
        String windowHandle = getDriver().getWindowHandle();
        getDriver().switchTo().frame(iframe);
        String content = getDriver().findElement(By.id("body")).getText();
        getDriver().switchTo().window(windowHandle);
        return content;
    }
}
