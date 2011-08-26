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
package org.xwiki.test.ui.scheduler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.xwiki.test.po.AbstractAdminAuthenticatedTest;
import org.xwiki.test.po.platform.DeletePage;
import org.xwiki.test.po.platform.ViewPage;
import org.xwiki.test.po.scheduler.SchedulerHomePage;
import org.xwiki.test.po.scheduler.SchedulerPage;
import org.xwiki.test.po.scheduler.editor.SchedulerEditPage;

/**
 * Tests Scheduler application features.
 * 
 * @version $Id$
 */
public class SchedulerTest extends AbstractAdminAuthenticatedTest
{
    @Before
    public void setUp()
    {
        super.setUp();
        
        getUtil().deletePage("Scheduler", "SchedulerTestJob");
    }

    @After
    public void tearDown()
    {
        getUtil().deletePage("Scheduler", "SchedulerTestJob");
    }

    /**
     * Tests that a scheduler job page default edit mode is "inline"
     */
    @Test
    public void testSchedulerJobDefaultEditMode()
    {
        getUtil().gotoPage("Scheduler", "WatchListDailyNotifier");
        Assert.assertTrue(new ViewPage().getEditURL().contains("/inline/"));
    }

    @Test
    public void testJobActions()
    {
        // Create Job
        SchedulerHomePage schedulerHomePage = SchedulerHomePage.gotoPage();
        schedulerHomePage.setJobName("SchedulerTestJob");
        SchedulerEditPage schedulerEdit = schedulerHomePage.clickAdd();

        String jobName = "Tester problem";
        schedulerEdit.setJobName(jobName);
        schedulerEdit.setJobDescription(jobName);
        schedulerEdit.setCron("0 15 10 ? * MON-FRI");
        SchedulerPage schedulerPage = schedulerEdit.clickSaveAndView();
        schedulerHomePage = schedulerPage.backToHome();

        // View Job
        schedulerPage = schedulerHomePage.clickJobActionView(jobName);
        schedulerHomePage = schedulerPage.backToHome();

        // Edit Job
        schedulerEdit = schedulerHomePage.clickJobActionEdit(jobName);
        schedulerEdit.setJobDescription("Tester problem2");
        schedulerEdit.setCron("0 0/5 14 * * ?");
        schedulerPage = schedulerEdit.clickSaveAndView();
        schedulerHomePage = schedulerPage.backToHome();

        // Delete and Restore Job
        DeletePage deletePage = schedulerHomePage.clickJobActionDelete(jobName);
        deletePage.confirm();
        schedulerHomePage = SchedulerHomePage.gotoPage();
        Assert.assertFalse(getUtil().hasElement(By.linkText(jobName)));
        getUtil().gotoPage("Scheduler", "SchedulerTestJob");
        getDriver().findElement(By.linkText("Restore")).click();
        schedulerPage = new SchedulerPage();
        schedulerPage.backToHome();

        // Trigger Job
        schedulerHomePage.clickJobActionTrigger(jobName);
        Assert.assertFalse("Failed to trigger job. Error [" + schedulerHomePage.getErrorMessage() + "]",
            schedulerHomePage.hasError());

        // Schedule Job
        schedulerHomePage.clickJobActionScheduler(jobName);
        Assert.assertFalse("Failed to schedule job. Error [" + schedulerHomePage.getErrorMessage() + "]",
            schedulerHomePage.hasError());

        // Pause Job
        schedulerHomePage.clickJobActionPause(jobName);
        Assert.assertFalse("Failed to pause job. Error [" + schedulerHomePage.getErrorMessage() + "]",
            schedulerHomePage.hasError());

        // Resume Job
        schedulerHomePage.clickJobActionResume(jobName);
        Assert.assertFalse("Failed to resume job. Error [" + schedulerHomePage.getErrorMessage() + "]",
            schedulerHomePage.hasError());

        // Unschedule Job
        schedulerHomePage.clickJobActionUnschedule(jobName);
        Assert.assertFalse("Failed to unschedule job.  Error [" + schedulerHomePage.getErrorMessage() + "]",
            schedulerHomePage.hasError());
    }
}
