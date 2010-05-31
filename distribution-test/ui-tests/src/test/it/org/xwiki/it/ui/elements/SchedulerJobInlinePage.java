package org.xwiki.it.ui.elements;

import org.openqa.selenium.By;

/**
 * Represents a scheduler job edited in inline mode.
 * 
 * @since 2.3.1
 * @since 2.4M1
 * @version $Id$
 */
public class SchedulerJobInlinePage extends InlinePage
{
    public boolean isQuartzDocumentationReferenced()
    {
        return getDriver().findElement(By.partialLinkText("official quartz scheduler documentation")) != null;
    }
}
