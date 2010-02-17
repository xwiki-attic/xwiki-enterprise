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
package com.xpn.xwiki.it.selenium.framework;

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
     * Waits for the specified node to be present.
     * 
     * @param nodeLabel the label of the node to wait for; this can be for instance the name of space or page
     * @param selected whether the specified node should be selected or not
     */
    public void waitForNode(final String nodeLabel, boolean selected)
    {
        final String className = selected ? "treeCellSelected" : "treeCell";
        new Wait()
        {
            public boolean until()
            {
                return test.isElementPresent("//td[contains(@class, '" + className + "')]//nobr[. = '" + nodeLabel
                    + "']");
            }
        }.wait("The specified tree node, " + nodeLabel + ", is not present!");
    }

    /**
     * @param nodeLabel the node label
     * @return {@code true} if the specified node is present in the tree, {@code false} otherwise
     */
    public boolean isNodePresent(String nodeLabel)
    {
        return test.isElementPresent("//td[contains(@class, 'treeCell')]//nobr[. = '" + nodeLabel + "']");
    }

    /**
     * Waits for the specified page to be selected.
     * 
     * @param spaceName the name of the space containing the page
     * @param pageName the name of the page to wait for
     */
    public void waitForPageSelected(String spaceName, String pageName)
    {
        waitForNode(spaceName, false);
        waitForNode(pageName, true);
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
        waitForNode(spaceName, false);
        waitForNode(pageName, false);
        waitForNode(attachment, true);
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
}
