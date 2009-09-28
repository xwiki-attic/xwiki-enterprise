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
package com.xpn.xwiki.it.xhtml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Validate provided input.
 * 
 * @version $Id$
 */
public class XHTMLValidator
{
    private DocumentBuilder documentBuilder;

    private XHTMLErrorHandler errorHandler = new XHTMLErrorHandler();

    public XHTMLValidator() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        this.documentBuilder = factory.newDocumentBuilder();
        this.documentBuilder.setEntityResolver(new ResourcesEntityResolver());
        this.documentBuilder.setErrorHandler(this.errorHandler);
    }

    public List<XHTMLError> getErrors()
    {
        return this.errorHandler.getErrors();
    }

    public List<XHTMLError> validate(InputStream in)
    {
        try {
            this.errorHandler.clear();
            this.documentBuilder.parse(in);
        } catch (SAXException e) {
            // Ignore - Let XhtmlErrorHandler handle it
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.errorHandler.getErrors();
    }
}
