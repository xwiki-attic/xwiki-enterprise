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
package org.xwiki.test.ui.framework.elements.editor;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

/**
 * Represents the actions possible in WYSIWYG edit mode.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class WYSIWYGEditPage extends PreviewableEditPage
{
    @FindBy(id = "xwikidoctitleinput")
    private WebElement titleField;

    @FindBy(name = "action_save")
    private WebElement saveAndViewSubmit;

    public String getDocumentTitle()
    {
        return this.titleField.getValue();
    }

    public ViewPage save()
    {
        this.saveAndViewSubmit.submit();
        return new ViewPage();
    }

    /**
     * Get the <code>content</code> of the page.
     */
    public String getContent()
    {
        String content;

        // Handle both the TinyMCE editor and the GWT Editor depending on the syntax
        if ("xwiki/1.0".equals(getSyntaxId())) {
            String windowHandle = getDriver().getWindowHandle();
            getDriver().switchTo().frame("mce_editor_0");
            content = getDriver().findElement(By.id("mceSpanFonts")).getText();
            getDriver().switchTo().window(windowHandle);
        } else {
            throw new RuntimeException("Not implemented yet");
        }
        return content;
    }
}
