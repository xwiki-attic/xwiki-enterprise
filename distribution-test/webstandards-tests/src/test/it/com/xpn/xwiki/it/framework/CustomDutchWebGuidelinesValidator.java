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
package com.xpn.xwiki.it.framework;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xwiki.validator.DutchWebGuidelinesValidator;
import org.xwiki.validator.ValidationError.Type;
import org.xwiki.validator.framework.NodeListIterable;

public class CustomDutchWebGuidelinesValidator extends DutchWebGuidelinesValidator
{
    /**
     * CSS should be placed in linked files and not mixed with the HTML source code. XWiki exception: in the ColorTheme
     * application we have to allow the use of inline styles for background colors, this is the only way to offer a
     * preview of the themes colors.
     */
    @Override
    public void validateRpd9s1()
    {
        String exprString = "//meta[@name='space']";
        NodeList spaceMetas = (NodeList) evaluate(getElement(ELEM_BODY), exprString, XPathConstants.NODESET);

        exprString = "//*[@style]";
        
        if (getAttributeValue(spaceMetas.item(0), ATTR_CONTENT).equals("ColorThemes")) {
            // We allow usage of the style attribute to set background-color in the ColorThemes space.
            NodeListIterable styledElements =
                new NodeListIterable((NodeList) evaluate(getElement(ELEM_BODY), exprString, XPathConstants.NODESET));

            for (Node styledElement : styledElements) {
                assertTrue(Type.ERROR, "rpd9s1.attr", 
                    getAttributeValue(styledElement, "style").matches("^background-color:\\s?(#[0-9a-fA-F]{6})?;?$"));
            }
        } else {
            // Usage of the style attribute is strictly forbidden in the other spaces.
            assertFalse(Type.ERROR, "rpd9s1.attr", ((Boolean) evaluate(getElement(ELEM_BODY), exprString,
                XPathConstants.BOOLEAN)));
        }
        
        // <style> tags are forbidden.
        assertFalse(Type.ERROR, "rpd9s1.tag",
            getChildren(getElement(ELEM_BODY), "style").getNodeList().getLength() > 0);
    }
}
