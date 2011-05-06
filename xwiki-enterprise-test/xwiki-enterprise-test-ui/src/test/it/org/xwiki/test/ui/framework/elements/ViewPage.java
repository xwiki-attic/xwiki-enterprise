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
import org.xwiki.test.ui.comments.elements.CommentsTab;
import org.xwiki.test.ui.framework.elements.editor.WYSIWYGEditPage;
import org.xwiki.test.ui.xe.elements.HomePage;

/**
 * Represents the common actions possible on all Pages when using the "view" action.
 * 
 * @version $Id$
 * @since 2.3M1
 */
public class ViewPage extends BasePage
{
    @FindBys({@FindBy(id = "tmRegister"), @FindBy(tagName = "a")})
    private WebElement registerLink;

    @FindBys({@FindBy(id = "tmLogin"), @FindBy(tagName = "a")})
    private WebElement loginLink;

    @FindBys({@FindBy(id = "tmLogout"), @FindBy(tagName = "a")})
    private WebElement logoutLink;

    @FindBys({@FindBy(id = "tmUser"), @FindBy(tagName = "a")})
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

    @FindBy(id = "tmActionCopy")
    private WebElement copyPageLink;

    @FindBy(id = "tmActionDelete")
    private WebElement deletePageLink;

    @FindBy(id = "document-title")
    private WebElement documentTitle;

    @FindBy(id = "hierarchy")
    private WebElement breadcrumbDiv;

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
                login().loginAsAdmin();
                getDriver().get(thisPage);
            } else {
                login().loginAsAdmin();
            }
        }
    }

    public boolean hasLoginLink()
    {
        // Note that we cannot test if the loginLink field is accessible since we're using an AjaxElementLocatorFactory
        // and thus it would wait 15 seconds before considering it's not accessible.
        return !getDriver().findElements(By.id("tmLogin")).isEmpty();
    }

    public LoginPage login()
    {
        this.loginLink.click();
        return new LoginPage();
    }

    public String getCurrentUser()
    {
        return this.userLink.getText();
    }

    public void logout()
    {
        this.logoutLink.click();
    }

    public RegisterPage register()
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

    /**
     * Opens the comments tab.
     * 
     * @return element for controlling the comments tab
     * @since 2.4
     */
    public CommentsTab openCommentsDocExtraPane()
    {
        this.getDriver().findElement(By.id("Commentslink")).click();
        this.waitUntilElementIsVisible(By.id("commentscontent"));

        return new CommentsTab();
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
     * @return the page's main content as text (no HTML)
     * @since 2.4M2
     */
    public String getContent()
    {
        return this.content.getText();
    }

    /**
     * @since 2.6RC1
     */
    public WYSIWYGEditPage editSection(int sectionNumber)
    {
        By sectionBy = By.xpath("//span[contains(@class, 'edit_section')][" + sectionNumber + "]/a");

        // Since Section Edit links are generated by JS (for XWiki Syntax 2.0) after the page has loaded make sure
        // we wait for them.
        waitUntilElementIsVisible(sectionBy);

        getDriver().findElement(sectionBy).click();
        return new WYSIWYGEditPage();
    }

    /**
     * Clicks on a wanted link in the page.
     * 
     * @since 2.6RC1
     */
    public void clickWantedLink(String spaceName, String pageName, boolean waitForTemplateDisplay)
    {
        WebElement brokenLink =
            getDriver().findElement(
                By.xpath("//span[@class='wikicreatelink']/a[contains(@href,'/create/" + spaceName + "/" + pageName
                    + "')]"));
        brokenLink.click();
        if (waitForTemplateDisplay) {
            // Ensure that the template choice popup is displayed. Since this is done using JS we need to wait till
            // it's displayed. For that we wait on the Create button since that would mean the template radio buttons
            // will all have been displayed.
            waitUntilElementIsVisible(By.xpath("//div[@class='modal-popup']//input[@type='submit']"));
        }
    }

    public CopyPage copy()
    {
        hoverOverMenu("tmPage");
        this.copyPageLink.click();
        return new CopyPage();
    }

    public DeletePage delete()
    {
        hoverOverMenu("tmPage");
        this.deletePageLink.click();
        return new DeletePage();
    }

    public boolean canDelete()
    {
        if (getDriver().findElements(By.xpath("//div[@id='tmPage']//span[@class='menuarrow']")).size() > 0) {
            hoverOverMenu("tmPage");
            return getDriver().findElements(By.id("tmActionDelete")).size() > 0;
        } else {
            return false;
        }
    }

    public String getDocumentTitle()
    {
        return this.documentTitle.getText();
    }

    public String getBreadcrumbContent()
    {
        return this.breadcrumbDiv.getText();
    }

    public boolean hasBreadcrumbContent(String breadcrumbItem)
    {
        return this.breadcrumbDiv.findElements(By.xpath("a[text() = '" + breadcrumbItem + "']")).size() > 0;
    }

    public boolean isInlinePage()
    {
        return getDriver().findElements(By.xpath("//form[@id = 'inline']")).size() > 0;
    }
}
