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
package org.xwiki.test.wysiwyg.framework;

import com.thoughtworks.selenium.Wait;

/**
 * Utility class used to write integration tests involving the XWiki Explorer tree.
 * 
 * @version $Id$
 */
public class XWikiExplorer
{
    /**
     * The test being executed.
     */
    private final AbstractWysiwygTestCase test;

    /**
     * Creates a new XWikiExplorer for the specified test.
     * 
     * @param test the test being executed
     */
    public XWikiExplorer(AbstractWysiwygTestCase test)
    {
        this.test = test;
    }

    /**
     * Waits for the node with the specified hint to be selected.
     * <p>
     * Note: We can wait only for the selected node because only the visible tree nodes are present in the DOM tree. If
     * a tree node is not present in the DOM it doesn't mean we can't select it.
     * 
     * @param hint the tool tip of the node to wait for
     */
    private void waitForNodeWithHint(final String hint)
    {
        new Wait()
        {
            public boolean until()
            {
                return test.isElementPresent("//td[contains(@class, 'treeCellSelected')]//span[@title = '" + hint
                    + "']");
            }
        }.wait("The tree node with the specified tool tip, \"" + hint + "\", wasn't selected!");
    }

    /**
     * Waits for the node with the specified hint and label to be selected.
     * 
     * @param hint the tool tip of the node to wait for
     * @param label the label of the node to wait for
     */
    private void waitForNodeWithHintAndLabel(final String hint, final String label)
    {
        new Wait()
        {
            public boolean until()
            {
                return test.isElementPresent("//td[contains(@class, 'treeCellSelected')]//span[@title = '" + hint
                    + "' and . = '" + label + "']");
            }
        }.wait("The tree node with the specified tool tip, \"" + hint + "\", and label, \"" + label
            + "\", wasn't selected!");
    }

    /**
     * Waits until none of the tree nodes is selected.
     */
    public void waitForNoneSelected()
    {
        test.waitForElementNotPresent("//td[contains(@class, 'treeCellSelected')]");
    }

    /**
     * Waits for the specified page to be selected.
     * 
     * @param spaceName the name of the space containing the page
     * @param pageName the name of the page to wait for
     */
    public void waitForPageSelected(String spaceName, String pageName)
    {
        waitForNodeWithHint(String.format("Located in xwiki \u00BB %s \u00BB %s", spaceName, pageName));
    }

    /**
     * Waits for the "New page.." node under the specified space to be selected.
     * 
     * @param spaceName the name of the space where the new page would be created
     */
    public void waitForNewPageSelected(String spaceName)
    {
        waitForNodeWithHint(String.format("New page in xwiki \u00BB %s", spaceName));
    }

    /**
     * Waits for the specified attachment to be selected.
     * 
     * @param spaceName the name of the space containing the page
     * @param pageName the name of the page who has the attachment
     * @param attachment the attachment
     */
    public void waitForAttachmentSelected(String spaceName, String pageName, String attachment)
    {
        waitForNodeWithHintAndLabel(String.format("Attached to xwiki \u00BB %s \u00BB %s", spaceName, pageName),
            attachment);
    }

    /**
     * Waits for the attachments node of the specified page to be selected. The attachments node is the parent node for
     * all the attachments of a page.
     * 
     * @param spaceName the space containing the page
     * @param pageName the name of the page whose attachments node is selected
     */
    public void waitForAttachmentsSelected(String spaceName, String pageName)
    {
        waitForNodeWithHint(String.format("Attachments of xwiki \u00BB %s \u00BB %s", spaceName, pageName));
    }

    /**
     * Types the given text in the input box below the tree. As a result the tree will lookup an entity among its nodes
     * (e.g. page, attachment) that matches the given reference.
     * 
     * @param entityReference the text to be typed in the input box below the tree, usually an entity reference
     */
    public void lookupEntity(String entityReference)
    {
        test.getSelenium().type("//div[contains(@class, 'xExplorerPanel')]//input", entityReference);
    }

    /**
     * @return the value of the input box below the tree; this is the serialized reference of the selected entity (e.g.
     *         page, attachment)
     */
    public String getSelectedEntityReference()
    {
        return test.getSelenium().getValue("//div[contains(@class, 'xExplorerPanel')]//input");
    }

    /**
     * Selects the "New page..." node for the specified space.
     * 
     * @param spaceName the space name
     */
    public void selectNewPageIn(String spaceName)
    {
        lookupEntity(spaceName + ".");
        waitForNewPageSelected(spaceName);
        // The SmartClient tree doesn't react when we simulate a click on the "New page..." node so we simply delete the
        // text from the input, which will leave the "New page..." node selected.
        lookupEntity("");
    }

    /**
     * Selects the "Upload file..." node for the specified page.
     * 
     * @param spaceName the page name
     * @param pageName the space name
     */
    public void selectUploadFileIn(String spaceName, String pageName)
    {
        lookupEntity(spaceName + "." + pageName + "#Attachments");
        waitForAttachmentsSelected(spaceName, pageName);
        // This works if the "Upload file..." node is right below the "Attachments" node.
        test.getSelenium().typeKeys("//*[@class = 'xExplorerPanel']//*[@class = 'listGrid']/div", "\\40");
    }
}
