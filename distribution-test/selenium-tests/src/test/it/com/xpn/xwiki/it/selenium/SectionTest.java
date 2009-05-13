package com.xpn.xwiki.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Tests the document edit section feature
 *
 * @version $Id$
 */
public class SectionTest extends AbstractXWikiTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the document edit section feature");
        suite.addTestSuite(SectionTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        open("Sandbox", "WebHome");
        assertTextPresent("Training Zone");
        assertTextPresent("Heading 1");
    }

    /**
     * Verify edit section is working in wiki editor
     */
    public void testSectionEditInWikiEditor()
    { 
        clickLinkWithLocator("//div[@id='xwikicontent']/span[2]/a"); // Edit the second section
        clickLinkWithText("Wiki");
        assertTextNotPresent("Training Zone");
        assertTextPresent("Heading 1");
        clickEditSaveAndContinue();
        assertTextNotPresent("Training Zone");
        assertTextPresent("Heading 1");
    }
    
    /**
     * Verify edit section is working in wysiwyg editor
     */    
    public void testSectionEditInWysiwygEditor()
    {
        clickLinkWithLocator("//div[@id='xwikicontent']/span[2]/a"); // Edit the second section
        assertTextNotPresent("Training Zone");
        assertTextPresent("Heading 1");
        clickEditSaveAndContinue();
        assertTextNotPresent("Training Zone");
        assertTextPresent("Heading 1");
    }
}
