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
import java.util.Map;
import java.util.HashMap;

import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Represents the actions possible on the invitation sender page.
 *
 * @version $Id$
 * @since 2.4M2
 */
public class InvitationSenderPage extends BasePage
{
    @FindBy(id = "invitation-sender-form")
    private WebElement form;

    private FormElement formElement;

    @FindBy(xpath = "//form[@id='invitation-sender-form']/div/div/span/input[@type='submit'][@name='preview']")
    private WebElement previewButton;

    @FindBy(xpath = "//form[@id='invitation-sender-form']/div/div/span/input[@type='submit'][@name='sendMail']")
    private WebElement sendButton;

    @FindBy(id = "invitation-displaymessage")
    private WebElement preview;

    private InvitationMessageDisplayElement previewElement;

    public void gotoPage()
    {
//try{while(true){Thread.sleep(10000);}}catch(Exception e){}
        getDriver().get(getURL());
    }

    public String getURL()
    {
        return getUtil().getURL("Invitation", "WebHome");
    }

    public boolean userIsSpammer()
    {
        for(WebElement error : getDriver().findElements(By.id("invitation-permission-error"))) {
            if (error.getText().equals("A message which you sent was reported as spam and your privilege to send mail"
                                       + " has suspended pending investigation, we apologize for the inconvenience."))
            {
                return true;
            }
        }
        return false;
    }

    public void fillInDefaultValues()
    {
        fillForm("user@localhost.localdomain", "This is a subject line.", "This is my message");
    }

    public void fillForm(final String recipients,
                         final String subjectLine,
                         final String messageBody)
    {
        Map map = new HashMap();
        if (recipients != null) {
            map.put("recipients", recipients);
        }
        if (subjectLine != null) {
            map.put("subjectLine", subjectLine);
        }
        if (messageBody != null) {
            map.put("messageBody", messageBody);
        }
        getForm().fillFieldsByName(map);
    }

    public InvitationSentPage send()
    {
        sendButton.click();
        return this.new InvitationSentPage();
    }

    public InvitationMessageDisplayElement preview()
    {
        previewButton.click();

        if (previewElement == null) {
            previewElement = new InvitationMessageDisplayElement(preview);
        }
        return previewElement;
    }

    public FormElement getForm()
    {
        if (formElement == null) {
            formElement = new FormElement(form);
        }
        return formElement;
    }

    public InvitationFooterElement getFooter()
    {
        return new InvitationFooterElement();
    }

    /** This page represents the invitation app after the send button has been pressed. */
    public class InvitationSentPage extends BasePage
    {
        @FindBy(id = "invitation-action-message")
        private WebElement messageBox;

        @FindBy(xpath = "//div[@class='invitation']/table")
        private WebElement table;

        @FindBy(xpath = "//div[@class='invitation']/div[@class='invitation invitation-preview']")
        private WebElement preview;

        public String getMessageBoxContent()
        {
            return messageBox.getText();
        }

        public TableElement getTable()
        {
            return new TableElement(table);
        }
    }
}
