package org.xwiki.test.selenium;

import junit.framework.Test;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

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
        suite.addTestSuite(SectionTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
    }

    protected void initXWiki10Env()
    {       
        createPage("Test", "SectionEditing", "1 First section\nSection 1 content\n\n"
            + "1 Second section\nSection 2 content\n\n1.1 Subsection\nSubsection content\n\n"
            + "1 Third section\nSection 3 content", "xwiki/1.0");        
    }

    protected void initXWiki20Env()
    {
        createPage("Test", "SectionEditingIncluded",
            "== Included section ==\nFirst Included section content\n{{velocity wiki=true}}\n"
                + "#foreach($h in ['First', 'Second'])\n== $h generated section ==\n\n$h generated paragraph\n"
                + "#end\n{{velocity}}\n");        
        createPage("Test", "SectionEditing20", "= First section =\nSection 1 content\n\n"
            + "= Second section =\nSection 2 content\n\n== Subsection ==\nSubsection content\n\n"
            + "{{include document='Test.SectionEditingIncluded'/}}\n\n" + "= Third section =\nSection 3 content",
            "xwiki/2.0");        
    }

    /**
     * Verify edit section is working in wysiwyg editor (xwiki/1.0). XWIKI-174 : Sectional editing.
     */
    public void testSectionEditInWysiwygEditor()
    {
        initXWiki10Env();
        clickLinkWithLocator("//div[@id='xwikicontent']/span[3]/a"); // Edit the subsection
        assertTextNotPresent("First section");
        assertTextNotPresent("Second section");
        assertTextPresent("Subsection");
        assertTextNotPresent("Third section");
    }

    /**
     * Verify section save does not override the whole document content (xwiki/1.0). XWIKI-4033: When saving after
     * section edit entire page is overwritten.
     */
    public void testSectionSaveDoesNotOverrideTheWholeContent()
    {
        initXWiki10Env();
        clickLinkWithLocator("//div[@id='xwikicontent']/span[3]/a"); // Edit the subsection
        clickLinkWithText("Wiki");
        clickEditSaveAndView();
        assertTextPresent("First section");
        assertTextPresent("Second section");
        assertTextPresent("Subsection");
        assertTextPresent("Third section");
    }

    /**
     * Verify edit section is working in wiki editor (xwiki/2.0). XWIKI-2881 : Implement Section editing.
     */
    public void testSectionEditInWikiEditor_syntax20()
    {
        initXWiki20Env();
        // Since the section edit links are inserted with JS we need to ensure they've been generated
        waitForElement("//div[@id='xwikicontent']/span[2]/a");
        // TODO: I don't understand why the following xpath expression doesn't work:
        // clickLinkWithLocator("//div[@id='xwikicontent']/span/a[contains(@href, 'section=2']");
        clickLinkWithLocator("//div[@id='xwikicontent']/span[2]/a"); // Edit the second editable section
        clickLinkWithText("Wiki");
        assertTextNotPresent("First section");
        assertTextPresent("Second section");
        assertTextPresent("Subsection");
        assertTextNotPresent("Third section");
    }

    /**
     * Verify edit section is working in wysiwyg editor (xwiki/2.0). XWIKI-2881 : Implement Section editing.
     */
    public void testSectionEditInWysiwygEditor_syntax20()
    {
        initXWiki20Env();
        // Since the section edit links are inserted with JS we need to ensure they've been generated
        waitForElement("//div[@id='xwikicontent']/span[4]/a");
        // TODO: I don't understand why the following xpath expression doesn't work:
        // clickLinkWithLocator("//div[@id='xwikicontent']/span/a[contains(@href, 'section=4']");
        clickLinkWithLocator("//div[@id='xwikicontent']/span[4]/a"); // Edit the last editable section
        assertTextNotPresent("First section");
        assertTextNotPresent("Second section");
        assertTextNotPresent("Subsection");
        assertTextPresent("Third section");
    }

    /**
     * Verify section save does not override the whole document content (xwiki/2.0). XWIKI-4033: When saving after
     * section edit entire page is overwritten.
     */
    public void testSectionSaveDoesNotOverrideTheWholeContent_syntax20()
    {
        initXWiki20Env();
        // Since the section edit links are inserted with JS we need to ensure they've been generated
        waitForElement("//div[@id='xwikicontent']/span[4]/a");
        // TODO: I don't understand why the following xpath expression doesn't work:
        // clickLinkWithLocator("//div[@id='xwikicontent']/span/a[contains(@href, 'section=4']");
        clickLinkWithLocator("//div[@id='xwikicontent']/span[4]/a"); // Edit the last editable section
        clickLinkWithText("Wiki");
        clickEditSaveAndView();
        assertTextPresent("First section");
        assertTextPresent("Second section");
        assertTextPresent("Third section");
    }
}
