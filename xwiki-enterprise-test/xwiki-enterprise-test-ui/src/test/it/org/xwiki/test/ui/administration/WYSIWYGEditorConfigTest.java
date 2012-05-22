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
package org.xwiki.test.ui.administration;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.browser.IgnoreBrowser;
import org.xwiki.test.po.administration.AdministrationPage;
import org.xwiki.test.po.administration.WYSIWYGEditorAdministrationSectionPage;

/**
 * Test the WYSIWYG Editor administration section.
 * 
 * @version $Id$
 * @since 3.3M2
 */
public class WYSIWYGEditorConfigTest extends AbstractAdminAuthenticatedTest
{
    /**
     * The WYSIWYG Editor administration section.
     */
    private WYSIWYGEditorAdministrationSectionPage wysiwygSection;

    @Override
    @Before
    public void setUp()
    {
        super.setUp();

        wysiwygSection = AdministrationPage.gotoPage().clickWYSIWYGEditorSection();
    }

    /**
     * Try to enable a dummy WYSIWYG editor plugin from the administration.
     * 
     * @since 3.3M2
     */
    @Test
    @IgnoreBrowser(value = "internet.*", version = "8\\.*", reason="See http://jira.xwiki.org/browse/XE-1146")
    public void testEnablePlugin()
    {
        String pluginName = RandomStringUtils.randomAlphabetic(5);
        Assert.assertFalse(wysiwygSection.getEnabledPlugins().contains(pluginName));
        wysiwygSection.enablePlugin(pluginName);
        wysiwygSection.clickSave();
        // Reload the administration section.
        getDriver().navigate().refresh();
        wysiwygSection = new WYSIWYGEditorAdministrationSectionPage();
        Assert.assertTrue(wysiwygSection.getEnabledPlugins().contains(pluginName));
    }
}
