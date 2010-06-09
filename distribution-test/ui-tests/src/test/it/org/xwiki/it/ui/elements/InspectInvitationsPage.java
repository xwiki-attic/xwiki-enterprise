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
package org.xwiki.it.ui.elements;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

/**
 * Represents the actions possible when inspecting invitations.
 *
 * @version $Id$
 * @since 2.4M2
 */
public abstract class InspectInvitationsPage extends BasePage
{
    private InvitationFooterElement footer = new InvitationFooterElement();

    @FindBy(tagName = "table")
    private WebElement tableWebEl;

    public TableElement getTable()
    {
        return new TableElement(tableWebEl);
    }

    public InvitationFooterElement getFooter() {
        return footer;
    }

    /** If there is a message box telling the status and memo the content is returned. */
    public String getStatusAndMemo()
    {
        List<WebElement> elements = getDriver().findElements(By.id("message-status-and-memo"));
        if (elements.size() > 0) {
            return elements.get(0).getText();
        }
        return null;
    }

    public OneMessage getMessageWhere(String columnName, String value)
    {
        List<String> columnEntries = new ArrayList<String>();
        List<WebElement> column = getTable().getColumn(columnName);
        for (WebElement cell : column) {
            if (cell.getText().equals(value)) {
                // Get the Subject element in the same row and look inside for a link.
                WebElement link = 
                    getTable().getColumn("Subject").get(column.indexOf(cell)).findElements(By.tagName("a")).get(0);
                link.click();
                return null;
            }
            columnEntries.add(cell.getText());
        }
        throw new WebDriverException("Could not find message with " + column + " equal to "
                                     + value + "\nIn columbn with entries: " + columnEntries.toString());
    }

    /** Should only be made available to OneMessage implementations. */
    protected TableElement clickMessageHistory()
    {
        getTable().getColumn("Message History").get(1).findElements(By.tagName("a")).get(0).click();
        return new TableElement(getDriver().findElement(By.id("message-history-table")).findElement(By.tagName("table")));
    }

    public static interface OneMessage
    {
        public InvitationMessageDisplayElement getMessage();

        public void notSpam(String message);

        public String getStatusAndMemo();

        public TableElement clickMessageHistory();
    }

    public static class AsAdmin extends InspectInvitationsPage
    {
        public OneMessage getMessageWhere(String column, String value)
        {
            super.getMessageWhere(column, value);
            return this.new OneMessage();
        }

        public class OneMessage extends AsAdmin implements InspectInvitationsPage.OneMessage
        {
            @FindBy(id = "invitation-displaymessage")
            private WebElement preview;

            @FindBy(name = "doAction_notSpam")
            private WebElement notSpamButton;

            public InvitationMessageDisplayElement getMessage()
            {
                return new InvitationMessageDisplayElement(preview);
            }

            public TableElement clickMessageHistory()
            {
                return super.clickMessageHistory();
            }

            public void notSpam(String message)
            {
                notSpamButton.click();
                InvitationActionConfirmationElement confirm = new InvitationActionConfirmationElement();
                // We can't go forward unless we are on the right form.
                if (!confirm.getLabel().equals("Synopsis of findings and/or action taken")) {
                    throw new WebDriverException("Not on 'not spam' confirm page, message says: " + confirm.getLabel());
                }
                confirm.setMemo(message);
                confirm.confirm();
            }
        }
    }

    public static class AsUser extends InspectInvitationsPage
    {
        @FindBy(xpath = "//div[@id='invitation-footer']//a[@href='/xwiki/bin/view/Invitation/InvitationMembersActions?inspect=all']")
        private WebElement cancelButton;

        public OneMessage getMessageWhere(String column, String value)
        {
            super.getMessageWhere(column, value);
            return this.new OneMessage();
        }

        public class OneMessage extends AsUser implements InspectInvitationsPage.OneMessage
        {
            @FindBy(id = "invitation-displaymessage")
            private WebElement preview;

            public InvitationMessageDisplayElement getMessage()
            {
                return new InvitationMessageDisplayElement(preview);
            }

            public TableElement clickMessageHistory()
            {
                return super.clickMessageHistory();
            }

            public void notSpam(String message)
            {
                throw new WebDriverException("Function only possible for admin.");
            }
        }
    }
}
