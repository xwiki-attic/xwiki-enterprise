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
package org.xwiki.test.officeimporter.framework;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;

/**
 * All XWiki Office Importer selenium tests must extend this class.
 * 
 * @version $Id$
 */
public class AbstractOfficeImporterTestCase extends AbstractXWikiTestCase
{
    /**
     * Test space used for importing documents.
     */
    private static final String TEST_SPACE = "Import";

    /**
     * Default page where the imported documents will land in.
     */
    private static final String TEST_PAGE = "Test";

    /**
     * A setting used by continuous build server.
     */
    private static final String XWINDOWFOCUS_BINARY = "/home/maven/xwindowfocus";

    /**
     * Required by continuous build server.
     */
    private class StreamRedirector extends Thread
    {
        private InputStream is;

        private OutputStream os;

        StreamRedirector(InputStream in, OutputStream out)
        {
            is = in;
            os = out;
        }

        public void run()
        {
            byte[] buf = new byte[512];
            int n;

            try {
                while (true) {
                    n = is.read(buf);
                    if (n == 0) {
                        return;
                    }
                    os.write(buf, 0, n);
                }
            } catch (Exception e) {
                LogFactory.getLog(StreamRedirector.class).error("Error while reading/writing: " + e);
            }
        }
    }

    /*
     * HACK. This method is needed by our Continuous Build server : maven.xwiki.org. GWT seems to have an unusual way to
     * manage input events, our WYSIWYG editor needs its container window to have a _real_ focus (Windowing System
     * level) to catch them (at least on Linux and OSX). This method executes a small C program to set the Windowing
     * System (X) focus on the window named : "Editing wysiwyg for WysiwygTest - Iceweasel". More information about this
     * program can be found here : http://dev.xwiki.org/xwiki/bin/view/Community/ContinuousBuild
     */
    private void externalX11WindowFocus() throws Exception
    {
        if ((new File(XWINDOWFOCUS_BINARY)).exists()) {
            ProcessBuilder pb =
                new ProcessBuilder(new String[] {XWINDOWFOCUS_BINARY, "Editing wysiwyg for WysiwygTest - Iceweasel"});
            pb.environment().put("DISPLAY", ":1.0");
            Process shell = pb.start();
            new StreamRedirector(shell.getInputStream(), System.out).start();
            new StreamRedirector(shell.getErrorStream(), System.err).start();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        // Focus on the XWiki window (Seems not to work, at least on Linux and OSX)
        getSelenium().windowFocus();
        // Focus on the XWiki window on our continuous build server (The hard way)
        externalX11WindowFocus();
    }

    /**
     * Imports the document identified by relPath (path relative to the project dir) into the wiki page identified by
     * targetSpace and targetPage.
     * 
     * @param relPath path of the office document relative to the project dir.
     * @param targetSpace target space.
     * @param targetPage target page.
     */
    public void importDocument(String relPath, String targetSpace, String targetPage)
    {
        // Open the office importer application
        open("Import", "WebHome");
        // Fill in the form
        File document = new File("target/test-classes/" + relPath);
        getSelenium().type("filepath", document.getAbsolutePath());
        getSelenium().type("targetSpace", targetSpace);
        getSelenium().type("targetPage", targetPage);
        // Click import button
        getSelenium().click("//input[@value='Import']");
        getSelenium().waitForPageToLoad("30000");
        // Goto the results page
        getSelenium().click("link=result");
        getSelenium().waitForPageToLoad("30000");
    }

    /**
     * Imports the document identified by relPath (path relative to the project dir) into the default test wiki page
     * 
     * @param relPath path of the office document relative to the project dir.
     */
    public void importDocument(String relPath)
    {
        assertTrue(!isExistingPage(TEST_SPACE, TEST_PAGE));
        importDocument(relPath, TEST_SPACE, TEST_PAGE);
        assertTrue(isExistingPage(TEST_SPACE, TEST_PAGE));
        deletePage(TEST_SPACE, TEST_PAGE);
    }
}
