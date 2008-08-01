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
package com.xpn.xwiki.it.selenium;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;
import junit.framework.Test;

public class SchedulerTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the Scheduler XWiki feature");
        suite.addTestSuite(SchedulerTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        open("Scheduler", "WebHome");
        assertElementPresent("//h3[text()='Comments']");
        assertElementPresent("//h3[text()='Attachments']");
        if (!getSelenium().isTextPresent("Tester problem")) {
            setFieldValue("title", "xyz");
            clickLinkWithXPath("//input[@value='Add']");
            setFieldValue("XWiki.SchedulerJobClass_0_jobName", "Tester problem");
            setFieldValue("XWiki.SchedulerJobClass_0_jobDescription", "Tester problem");
            setFieldValue("XWiki.SchedulerJobClass_0_cron", "0 10 15 2008");
            clickLinkWithLocator("formactionsave");
            clickLinkWithText("Back to the job list");
            assertElementPresent("//td[text()='Tester problem']");
        }
    }

    /**
     * This method makes the following tests : <p/> <ul> <li>Opens the Scheduler for XWiki instance.</li> <li>Edits the
     * task in top of the list.</li> <li>Modifies the form with details.</li> <li>Goes back to job list and check for
     * the presence of the newly created job.</li> </ul>
     */
    public void testEditJob()
    {
        clickLinkWithXPath("//a[@href='/xwiki/bin/inline/Scheduler/xyz']");
        setFieldValue("XWiki.SchedulerJobClass_0_jobDescription", "Tester problem2");
        setFieldValue("XWiki.SchedulerJobClass_0_cron", "0 10 15 2009");
        clickLinkWithLocator("formactionsave");
        clickLinkWithText("Back to the job list");
        assertElementPresent("//td[text()='Tester problem']");
    }

    /**
     * This method makes the following tests : <p/> <ul> <li>Opens the Scheduler for XWiki instance.</li> <li>Deletes
     * the task in top of the list.</li> <li>Sends it to recycle bin then restores it.</li> <li>Goes back to job
     * list.</li> </ul>
     */
    public void testRestoreJob()
    {
        clickLinkWithXPath("//td/a[@href='/xwiki/bin/view/Scheduler/?do=delete&which=Scheduler.xyz']");
        clickLinkWithXPath("//input[@value='yes']");
        clickLinkWithText("Job Scheduler");
        assertElementNotPresent("//td[text()='Tester job']");
        open("Scheduler", "xyz", "view", "confirm=1");
        clickLinkWithText("Restore");
        clickLinkWithText("Back to the job list");
        assertElementPresent("//td[text()='Tester problem']");
    }

    public void testViewJob()
    {
        clickLinkWithXPath("//td/span/a[@href='/xwiki/bin/view/Scheduler/xyz']");
        clickLinkWithText("Back to the job list");
    }

    public void testScheduleUnschedulePauseResumeJob()
    {
        clickLinkWithText("unschedule");
        clickLinkWithText("schedule");
        clickLinkWithText("pause");
        clickLinkWithText("resume");
    }

    /**
     * This method makes the following tests : <p/> <ul> <li>Opens the Scheduler for XWiki instance.</li> <li>Deletes
     * the task in top of the list.</li> <li>Sends it to recycle bin then deletes it forever.</li> </ul>
     */
    public void testDeleteJob()
    {
        clickLinkWithXPath("//td/a[@href='/xwiki/bin/view/Scheduler/?do=delete&which=Scheduler.xyz']");
        clickLinkWithXPath("//input[@value='yes']");
        clickLinkWithText("Job Scheduler");
        assertElementNotPresent("//td[text()='Tester job']");
        open("Scheduler", "xyz", "view", "confirm=1");
        getSelenium().click("//a[@onclick=\"if (confirm('This action is not reversible. "
            + "Are you sure you wish to continue?')) {this.href += '&confirm=1'; return true;} return false;\"]");
        assertTrue(getSelenium().getConfirmation().matches(
            "^This action is not reversible\\. Are you sure you wish to continue[\\s\\S]$"));
        open("Scheduler", "WebHome");
        assertElementNotPresent("//td[text()='Tester problem']");
    }
}
