package org.xwiki.it.ui;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.it.ui.elements.SchedulerJobInlinePage;
import org.xwiki.it.ui.elements.ViewPage;
import org.xwiki.it.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.it.ui.framework.TestUtils;

/**
 * Tests Scheduler application features.
 * 
 * @since 2.3.1
 * @since 2.4M1
 * @version $Id$
 */
public class SchedulerTest extends AbstractAdminAuthenticatedTest
{

    @Before
    public void setUp()
    {
        super.setUp();
    }

    /**
     * Tests that a scheduler job page default edit mode is "inline"
     */
    @Test
    public void testSchedulerJobDefaultEditMode()
    {
        TestUtils.gotoPage("Scheduler", "WatchListDailyNotifier", getDriver());

        ViewPage page = new ViewPage();
        Assert.assertTrue(page.exists());
        page.clickEdit();

        SchedulerJobInlinePage inlineJob = new SchedulerJobInlinePage();
        // The edit sheet of scheduler jobs points to Quartz documentation/
        // Make sure this documentation is referenced to prove we are indeed in inline edit mode.
        Assert.assertTrue(inlineJob.isQuartzDocumentationReferenced());
    }

}
