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
package org.xwiki.test.selenium;

import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

public class SchedulerTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Scheduler XWiki feature");
        suite.addTestSuite(SchedulerTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        deletePage("Scheduler", "SchedulerTestJob");
    }

    @Override
    protected void tearDown()
    {
        deletePage("Scheduler", "SchedulerTestJob");
    }

    public void testJobActions()
    {
        // Create Job
        open("Scheduler", "WebHome");
        setFieldValue("title", "SchedulerTestJob");
        clickLinkWithXPath("//input[@value='Add']");
        setFieldValue("XWiki.SchedulerJobClass_0_jobName", "Tester problem");
        setFieldValue("XWiki.SchedulerJobClass_0_jobDescription", "Tester problem");
        setFieldValue("XWiki.SchedulerJobClass_0_cron", "0 15 10 ? * MON-FRI");
        clickEditSaveAndView();
        clickLinkWithText("Back to the job list");
        waitForElement("//td[text()='Tester problem']");

        // View Job
        clickJobAction("view");
        clickLinkWithText("Back to the job list");

        // Edit Job
        clickJobAction("Edit");
        setFieldValue("XWiki.SchedulerJobClass_0_jobDescription", "Tester problem2");
        setFieldValue("XWiki.SchedulerJobClass_0_cron", "0 0/5 14 * * ?");
        clickEditSaveAndView();
        clickLinkWithText("Back to the job list");
        waitForElement("//td[text()='Tester problem']");

        // Delete and Restore Job
        clickJobAction("delete");
        clickLinkWithXPath("//input[@value='yes']");
        open("Scheduler", "WebHome");
        assertElementNotPresent("//td[text()='Tester job']");
        open("Scheduler", "SchedulerTestJob", "view", "confirm=1");
        clickLinkWithText("Restore");
        clickLinkWithText("Back to the job list");
        waitForElement("//td[text()='Tester problem']");

        // Schedule Job
        clickJobAction("schedule");

        // Trigger Job
        clickJobAction("trigger");

        // Pause Job
        clickJobAction("pause");

        // Resume Job
        clickJobAction("resume");

        // Unschedule Job
        clickJobAction("unschedule");
    }

    private void clickJobAction(String actionName)
    {
        clickLinkWithXPath("//tr/td[text()='Tester problem']/parent::tr//td/span/a[text()='" + actionName + "']");
    }
}
