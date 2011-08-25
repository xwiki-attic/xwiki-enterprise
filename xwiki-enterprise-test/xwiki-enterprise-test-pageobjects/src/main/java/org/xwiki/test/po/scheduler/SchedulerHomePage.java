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
package org.xwiki.test.po.scheduler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.po.platform.DeletePage;
import org.xwiki.test.po.platform.ViewPage;
import org.xwiki.test.po.scheduler.editor.SchedulerEditPage;

public class SchedulerHomePage extends ViewPage
{
    @FindBy(xpath = "//form[@id='newdoc']//input[@type='submit' and @value='Add']")
    private WebElement addButton;

    @FindBy(id = "page")
    private WebElement nameInput;

    public static SchedulerHomePage gotoPage()
    {
        getUtil().gotoPage("Scheduler", "WebHome", "view");
        return new SchedulerHomePage();
    }

    public String getURL()
    {
        return getUtil().getURL("Scheduler", "WebHome");
    }

    public void setJobName(String jobName)
    {
        this.nameInput.clear();
        this.nameInput.sendKeys(jobName);
    }

    public SchedulerPage clickJobActionView(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='view']"))
            .click();

        return new SchedulerPage();
    }

    public SchedulerEditPage clickJobActionEdit(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='Edit']"))
            .click();

        return new SchedulerEditPage();
    }

    public DeletePage clickJobActionDelete(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='delete']"))
            .click();

        return new DeletePage();
    }

    public void clickJobActionScheduler(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='schedule']"))
            .click();
    }

    public void clickJobActionTrigger(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='trigger']"))
            .click();
    }

    public void clickJobActionPause(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='pause']"))
            .click();
    }

    public void clickJobActionResume(String jobName)
    {
        getDriver().findElement(By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='resume']"))
            .click();
    }

    public void clickJobActionUnschedule(String jobName)
    {
        getDriver().findElement(
            By.xpath("//tr/td[text()='" + jobName + "']/parent::tr//td/span/a[text()='unschedule']")).click();
    }

    public SchedulerEditPage clickAdd()
    {
        this.addButton.click();

        return new SchedulerEditPage();
    }
}
