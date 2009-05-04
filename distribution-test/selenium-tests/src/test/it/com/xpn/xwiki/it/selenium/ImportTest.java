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

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiSeleniumTestSetup;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify the XAR import feature. This is a simple test that does not actually tries to import a XAR file in the wiki.
 * It just verifies the list of files available to import is empty. This should be included later on in a more complete
 * test that does import a XAR and verifies its documents are well imported. For this, the test should be written using
 * Selenium's Chrome launcher (see http://seleniumhq.org/documentation/remote-control/experimental.html).
 */
public class ImportTest extends AbstractXWikiTestCase
{

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Verify the XAR import feature");
        suite.addTestSuite(ImportTest.class, AlbatrossSkinExecutor.class);
        //return suite;
        return new XWikiSeleniumTestSetup(suite);
    }

    /**
     * Verify the list of available files for import is empty on a fresh distribution. When adding real import tests, we
     * should refactor this test (moving its code it the test setup for example), or make sure each test cleans it's
     * uploaded file not to make this one fail.
     */
    public void testListOfAvailableFilesForImportIsEmpty()
    {
        this.loginAsAdmin();
        
        this.openAdministrationSection("Import");

        this.assertTextPresent("No attachments for this document");
    }

}
