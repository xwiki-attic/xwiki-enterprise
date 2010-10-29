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
import org.xwiki.test.ui.framework.elements.LiveTableElement;

/**
 * Represents the common actions possible on all Pages when using the "edit" action with the "rights" editor.
 * 
 * @version $Id$
 * @since 2.5M1
 */
public class RightsEditPage extends EditPage
{
    /** The known access rights. */
    public static enum Right
    {
        VIEW,
        COMMENT,
        EDIT,
        DELETE;

        int getColumnIndex()
        {
            return this.ordinal() + 2;
        }
    }

    /** The possible states of an access right box. */
    public static enum State
    {
        NONE("/xwiki/resources/js/xwiki/usersandgroups/img/none.png"),
        ALLOW("/xwiki/resources/js/xwiki/usersandgroups/img/allow.png"),
        DENY("/xwiki/resources/js/xwiki/usersandgroups/img/deny1.png");

        String imageURL;

        State(String imageURL)
        {
            this.imageURL = imageURL;
        }

        State getNextState()
        {
            return values()[(ordinal() + 1) % values().length];
        }

        static State getButtonState(WebElement button)
        {
            for (State s : values()) {
                if (s.imageURL.equals(button.getAttribute("src"))) {
                    return s;
                }
            }
            return NONE;
        }
    }

    private LiveTableElement rightsTable;

    @FindBy(id = "uorgu")
    private WebElement showUsersField;

    @FindBy(id = "uorgg")
    private WebElement showGroupsField;

    public void switchToUsers()
    {
        LiveTableElement e = this.getRightsTable();
        this.showUsersField.click();
        e.waitUntilReady();
    }

    public void switchToGroups()
    {
        this.showGroupsField.click();
        this.getRightsTable().waitUntilReady();
    }

    /**
     * Click once on a right button, waiting for the next state to appear.
     * 
     * @param entityName the target user or group name
     * @param right the target right
     * @return the new state of the button
     */
    public State clickRight(String entityName, Right right)
    {
        try {
            executeJavascript("window.__oldConfirm = window.confirm; window.confirm = function() { return true; };");
            final By buttonLocator =
                By.xpath("//*[@id='usersandgroupstable-display']//td[@class='username']/a[contains(@href, '"
                + entityName + "')]/../../td[" + right.getColumnIndex() + "]/img");
            final WebElement button = getDriver().findElement(buttonLocator);
            State expectedState = State.getButtonState(button).getNextState();
            button.click();
            waitUntilElementHasAttributeValue(buttonLocator, "src", expectedState.imageURL);
            return expectedState;
        } finally {
            executeJavascript("window.confirm = window.__oldConfirm;");
        }
    }

    /**
     * Click on a right button until it gets in the wanted state.
     * 
     * @param entityName the target user or group name
     * @param right the target right
     * @param wantedState the wanted state for the right
     */
    public void setRight(String entityName, Right right, State wantedState)
    {
        State currentState;
        do {
            currentState = clickRight(entityName, right);
        } while (currentState != wantedState);
    }

    public String getURL(String space, String page)
    {
        return getUtil().getURL(space, page, "edit", "editor=rights");
    }

    public void switchToEdit(String space, String page)
    {
        getUtil().gotoPage(space, page, "edit", "editor=rights");
    }

    private LiveTableElement getRightsTable()
    {
        if (this.rightsTable == null) {
            this.rightsTable = new LiveTableElement("usersandgroupstable");
        }
        return this.rightsTable;
    }
}
