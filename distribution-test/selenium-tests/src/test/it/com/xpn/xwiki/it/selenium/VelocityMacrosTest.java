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
 *
 */
package com.xpn.xwiki.it.selenium;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Verify proper execution of some Velocity Macros.
 * 
 * @version $Id$
 */
public class VelocityMacrosTest extends AbstractXWikiTestCase
{
    private static final String EXECUTION_DIRECTORY = System.getProperty("xwikiExecutionDirectory");

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests Velocity Macros");
        suite.addTestSuite(VelocityMacrosTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testMacros() throws Exception
    {
        open("Test", "VelocityMacrosTest", "edit", "editor=wiki");

        // TODO: Add more macro tests here (for performance reasons it's much faster to have more
        // tests in a single junit test) and modify thet assert so that it checks for exact content
        setFieldValue("content", "#mimetypeimg(\"image/jpeg\" \"photo.jpeg\")");
        clickEditSaveAndView();
        assertGeneratedHTML("img[@src='/xwiki/skins/albatross/mimetypes/jpg.png' "
            + "and @alt='Image' and @title='Image']");
    }

    /**
     * Verify that we can create macros in a document and including them into another document.
     */
    public void testIncludeMacrosInPage()
    {
        open("Test", "Macro", "edit", "editor=wiki");
        setFieldValue("content", "#macro(testIncludeMacrosInPage)hellomacro#end");
        clickEditSaveAndView();
        open("Test", "IncludeMacroTest", "edit", "editor=wiki");
        setFieldValue("content", "#includeMacros(\"Test.Macro\")\n#testIncludeMacrosInPage()");
        clickEditSaveAndView();
        assertTextPresent("hellomacro");
    }

    /**
     * Verify that a Macro defined in a document is not visible from another document.
     */
    public void testMacrosAreLocal()
    {
        open("Test", "TestMacrosAreLocal1", "edit", "editor=wiki");
        setFieldValue("content", "#macro(testMacrosAreLocal)mymacro#end");
        clickEditSaveAndView();
        open("Test", "TestMacrosAreLocal2", "edit", "editor=wiki");
        setFieldValue("content", "#testMacrosAreLocal()");
        clickEditSaveAndView();
        assertTextNotPresent("mymacro");
    }

    /**
     * Verify that macros declared in custom skin object are usable in page content.
     */
    public void testUsingMacroInGetRenderedContent() throws IOException
    {
        // Get default view.vm template content
        File file = new File(EXECUTION_DIRECTORY + "/webapps/xwiki/templates/view.vm");
        FileReader fileReader = new FileReader(file);
        char[] fileContent = new char[(int) file.length()];
        fileReader.read(fileContent);
        String viewTemplate = String.copyValueOf(fileContent);

        // Overwrite view template in custom skin to add macro definition
        open("XWiki", "DefaultSkin", "edit", "editor=object");
        setFieldValue("XWiki.XWikiSkins_0_view.vm", "#macro(testSkinObjectMacro)skin object macro content#end "
            + viewTemplate);
        clickEditSaveAndContinue();

        // Create a wiki page which use use the defined macro
        open("Test", "VelocitySkinObjectMacrosUseMacro", "edit", "editor=wiki");
        setFieldValue("content", "#testSkinObjectMacro()");
        clickEditSaveAndView();

        // Validate if the macros works
        assertTextPresent("skin object macro content");
    }
}
