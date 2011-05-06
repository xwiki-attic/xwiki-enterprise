package org.xwiki.test.ui.comments.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.framework.elements.ViewPage;

public class CommentDeleteConfirmationModal extends ViewPage
{
    @FindBy(xpath = "//div[@class='buttons']//input[@value='Yes']")
    private WebElement buttonOk;

    @FindBy(xpath = "//div[@class='buttons']//input[@value='No']")
    private WebElement buttonCancel;

    public void clickOk()
    {
        waitUntilElementIsVisible(By.className("xdialog-box-confirmation"));
        this.buttonOk.click();
    }

    public void clickCancel()
    {
        waitUntilElementIsVisible(By.className("xdialog-box-confirmation"));
        this.buttonCancel.click();
    }
}
