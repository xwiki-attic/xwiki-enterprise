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
package org.xwiki.test.ui.framework.elements;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.xwiki.test.ui.administration.elements.AdministrationPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Represents the common actions possible on all Pages when using the "view" action.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class ViewPage extends BasePage
{
    @FindBys( {@FindBy(id = "tmRegister"), @FindBy(tagName = "a")})
    private WebElement registerLink;

    @FindBys( {@FindBy(id = "tmLogin"), @FindBy(tagName = "a")})
    private WebElement loginLink;

    @FindBys( {@FindBy(id = "tmLogout"), @FindBy(tagName = "a")})
    private WebElement logoutLink;

    @FindBys( {@FindBy(id = "tmUser"), @FindBy(tagName = "a")})
    private WebElement userLink;

    @FindBy(id = "tmCreatePage")
    private WebElement createPageMenuLink;

    @FindBy(id = "tmCreateSpace")
    private WebElement createSpaceMenuLink;

    @FindBy(id = "tmAdminWiki")
    private WebElement administerWikiMenuLink;

    @FindBy(id = "xwikicontent")
    private WebElement content;

    @FindBy(id = "hierarchy")
    private WebElement hierarchy;

    /**
     * Logs in the Admin user (move to the home page if the current page has no log in link).
     */
    public void loginAsAdmin()
    {
        if (!isAuthenticated()) {
            // If there's no login link then go to the home page.
            if (!hasLoginLink()) {
                String thisPage = getPageURL();
                HomePage homePage = new HomePage();
                homePage.gotoPage();
                clickLogin().loginAsAdmin();
                getDriver().get(thisPage);
            } else {
                clickLogin().loginAsAdmin();
            }
        }
    }

    public boolean hasLoginLink()
    {
        // Note that we cannot test if the loginLink field is accessible since we're using an AjaxElementLocatorFactory
        // and thus it would wait 15 seconds before considering it's not accessible.
        return !getDriver().findElements(By.id("tmLogin")).isEmpty();
    }

    public LoginPage clickLogin()
    {
        this.loginLink.click();
        return new LoginPage();
    }

    public String getCurrentUser()
    {
        return this.userLink.getText();
    }

    public void clickLogout()
    {
        this.logoutLink.click();
    }

    public RegisterPage clickRegister()
    {
        this.registerLink.click();
        return new RegisterPage();
    }

    public CreatePagePage createPage()
    {
        hoverOverMenu("tmCreate");
        this.createPageMenuLink.click();
        return new CreatePagePage();
    }

    public CreateSpacePage createSpace()
    {
        hoverOverMenu("tmCreate");
        this.createSpaceMenuLink.click();
        return new CreateSpacePage();
    }

    public AdministrationPage administerWiki()
    {
        hoverOverMenu("tmWiki");
        this.administerWikiMenuLink.click();
        return new AdministrationPage();
    }

    // TODO: I don't think we should go through the menus, it's probably faster to to as deletePage() does
    public void deleteCurrentPage()
    {
        getDriver().findElement(By.partialLinkText("More actions")).click();
        getDriver().findElement(By.linkText("Delete")).click();

        getDriver().findElement(By.xpath("//input[@value='yes']")).click();

        // Purge from trash bin
        makeConfirmDialogSilent(true); // temporary, see #makeConfirmDialogSilent
        getDriver().findElement(By.partialLinkText("Delete")).click();
    }

    /**
     * Opens the comments tab.
     * 
     * @return element for controlling the comments tab
     * @since 2.4
     */
    public CommentsPane openCommentsDocExtraPane()
    {
        this.getDriver().findElement(By.id("Commentslink")).click();
        this.waitUntilElementIsVisible(By.id("commentscontent"));

        return new CommentsPane();
    }

    public HistoryPane openHistoryDocExtraPane()
    {
        this.getDriver().findElement(By.id("Historylink")).click();
        this.waitUntilElementIsVisible(By.id("historycontent"));

        return new HistoryPane();
    }

    public AttachmentsPane openAttachmentsDocExtraPane()
    {
        this.getDriver().findElement(By.id("Attachmentslink")).click();
        this.waitUntilElementIsVisible(By.id("attachmentscontent"));

        return new AttachmentsPane();
    }

    /** @return does this page exist. */
    public boolean exists()
    {
        List<WebElement> messages = getDriver().findElements(By.className("xwikimessage"));
        for (WebElement message : messages) {
            if (message.getText().equals("The requested document could not be found.")
                || message.getText().equals("The document has been deleted.")) {
                return false;
            }
        }
        return true;
    }

    /** @return the hierarchy container. */
    public WebElement getHierarchy()
    {
        return this.hierarchy;
    }

    /**
     * @return the page's main content
     * @since 2.4M2
     */
    public String getContent()
    {
        return this.content.getText();
    }
}
