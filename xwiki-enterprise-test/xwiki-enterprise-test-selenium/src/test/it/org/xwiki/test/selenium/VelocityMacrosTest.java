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
package org.xwiki.test.selenium;

import java.io.IOException;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.FlamingoSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Verify proper execution of some Velocity Macros.
 * 
 * @version $Id$
 */
public class VelocityMacrosTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/2.1";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests Velocity Macros");
        suite.addTestSuite(VelocityMacrosTest.class, FlamingoSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    public void testMacros() throws Exception
    {
        editInWikiEditor("Test", "VelocityMacrosTest", SYNTAX);

        // TODO: Add more macro tests here (for performance reasons it's much faster to have more
        // tests in a single junit test) and modify thet assert so that it checks for exact content
        setFieldValue("content", "{{velocity}}{{html}}#mimetypeimg('image/jpeg' 'photo.jpeg'){{/html}}{{/velocity}}");
        clickEditSaveAndView();
        assertGeneratedHTML("p/img[@src='/xwiki/resources/icons/silk/picture.png' "
            + "and @alt='Image' and @title='Image']");
    }

    /**
     * Verify that we can create macros in a document and including them into another document.
     */
    public void testIncludeMacrosInPage()
    {
        editInWikiEditor("Test", "Macro", SYNTAX);
        setFieldValue("content", "{{velocity}}#macro(testIncludeMacrosInPage)hellomacro#end{{/velocity}}");
        clickEditSaveAndView();
        editInWikiEditor("Test", "IncludeMacroTest", SYNTAX);
        setFieldValue("content", "{{velocity}}#includeMacros(\"Test.Macro\")\n#testIncludeMacrosInPage(){{/velocity}}");
        clickEditSaveAndView();
        assertTextPresent("hellomacro");
    }

    /**
     * Verify that a Macro defined in a document is not visible from another document (using XWiki Syntax 1.0).
     * Note that for XWiki Syntax 2.0 this is verified in a unit test in the Velocity Macro module.
     */
    public void testMacrosAreLocal()
    {
        editInWikiEditor("Test", "TestMacrosAreLocal1", SYNTAX);
        setFieldValue("content", "{{velocity}}#macro(testMacrosAreLocal)mymacro#end{{/velocity}}");
        clickEditSaveAndView();
        editInWikiEditor("Test", "TestMacrosAreLocal2", SYNTAX);
        setFieldValue("content", "{{velocity}}#testMacrosAreLocal(){{/velocity}}");
        clickEditSaveAndView();
        assertTextNotPresent("mymacro");
    }

    /**
     * Verify that macros declared in custom skin object are usable in page content.
     */
    public void testUsingMacroInGetRenderedContent() throws IOException
    {
        // Copy the default skin page to a new page to not impact other tests.
        // Note: We have to use an existing space because the copy page form doesn't allow us to copy to a new space.
        deletePage("Sandbox", "testUsingMacroInGetRenderedContentSkin");
        assertTrue(copyPage("XWiki", "DefaultSkin", "Sandbox", "testUsingMacroInGetRenderedContentSkin"));

        // Overwrite view template in custom skin to add macro definition.
        open("Sandbox", "testUsingMacroInGetRenderedContentSkin", "edit", "editor=object");
        setFieldValue("XWiki.XWikiSkins_0_view.vm",
            "#macro(testSkinObjectMacro)skin object macro content#end\n$cdoc.getRenderedContent()");
        clickEditSaveAndContinue();

        // Create a wiki page which uses the defined macro.
        editInWikiEditor("Test", "testUsingMacroInGetRenderedContent", SYNTAX);
        setFieldValue("content", "{{velocity}}#testSkinObjectMacro(){{/velocity}}");
        clickEditSaveAndContinue();

        // Validate if the macros works
        assertTextNotPresent("skin object macro content");
        open("Test", "testUsingMacroInGetRenderedContent", "view",
            "skin=Sandbox.testUsingMacroInGetRenderedContentSkin");
        assertTextPresent("skin object macro content");
    }
}
