package com.xpn.xwiki.it.selenium;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractXWikiTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Tests the document edit section feature
 * 
 * @version $Id: $
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
        open("Sandbox", "WebHome", "edit", "editor=wiki");
        createPage("Test", "SectionEditing", "1 First section\nSection 1 content\n\n"
            + "1 Second section\nSection 2 content\n\n1.1 Subsection\nSubsection content\n\n"
            + "1 Third section\nSection 3 content");
        assertTextPresent("First section");
        assertTextPresent("Subsection content");
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        deletePage("Test", "SectionEditing");
    }

    /**
     * Verify edit section is working in wiki editor
     */
    public void testSectionEditInWikiEditor()
    {
        clickLinkWithLocator("//div[@id='xwikicontent']/span[2]/a"); // Edit the second section
        clickLinkWithText("Wiki");
        assertTextNotPresent("First section");
        assertTextPresent("Second section");
        assertTextPresent("Subsection content");
        assertTextNotPresent("Third section");
    }

    /**
     * Verify edit section is working in wysiwyg editor
     */
    public void testSectionEditInWysiwygEditor()
    {
        clickLinkWithLocator("//div[@id='xwikicontent']/span[3]/a"); // Edit the subsection
        assertTextNotPresent("First section");
        assertTextNotPresent("Second section");
        assertTextPresent("Subsection content");
        assertTextNotPresent("Third section");
    }
}
