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

import org.xml.sax.SAXParseException;

/**
 * XHTML validation error.
 * 
 * @version $Id$
 */
public class XHTMLError
{
    public enum Type
    {
        WARNING,
        ERROR,
        FATAL
    }

    private Type type;

    private int line;

    private int column;

    private String message;

    public XHTMLError(Type type, int line, int column, String message)
    {
        this.type = type;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public XHTMLError(Type type, SAXParseException e)
    {
        this.type = type;
        this.line = e.getLineNumber();
        this.column = e.getColumnNumber();
        this.message = e.getMessage();
    }

    public Type getType()
    {
        return this.type;
    }

    public int getLine()
    {
        return this.line;
    }

    public int getColumn()
    {
        return this.column;
    }

    public String getMessage()
    {
        return this.message;
    }
}
