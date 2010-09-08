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
package org.xwiki.escaping.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xwiki.validator.ValidationError;
import org.xwiki.validator.ValidationError.Type;
import org.xwiki.validator.Validator;


/**
 * A validator that checks for proper XML escaping. The document must be constructed using the special
 * test input string (see {@link #getTestString()}).
 * 
 * @version $Id$
 * @since 2.5M1
 */
public class XMLEscapingValidator implements Validator
{
    /** Unescaped test string containing XML significant characters. */
    private static final String INPUT_STRING = "aaa\"bbb'ccc>ddd<eee";

    /** Test for unescaped apostrophe. */
    private static final String TEST_APOS = "bbb'ccc";

    /** Test for unescaped quote. */
    private static final String TEST_QUOT = "aaa\"bbb";

    /** Expect an empty or non-empty document. */
    private boolean shouldBeEmpty = false;

    /** Source of the XML document to validate. */
    private List<String> document = new ArrayList<String>();

    /** List of validation errors. */
    private List<ValidationError> errors = new ArrayList<ValidationError>();

    /**
     * Get the input string containing XML significant characters that should be used.
     * 
     * @return test string to use
     */
    public static String getTestString()
    {
        return INPUT_STRING;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Clears previous list of validation errors.</p>
     * 
     * @see org.xwiki.validator.Validator#setDocument(java.io.InputStream)
     */
    public void setDocument(InputStream document)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(document));
        String line;
        this.document = new ArrayList<String>();
        try {
            while ((line = reader.readLine()) != null) {
                this.document.add(line);
            }
        } catch (IOException exception) {
            throw new RuntimeException("Could not read document: ", exception);
        }
        clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Throws {@link EscapingError} on errors.</p>
     * 
     * @see org.xwiki.validator.Validator#validate()
     */
    public List<ValidationError> validate()
    {
        clear();
        if (this.document.size() == 0 && !this.shouldBeEmpty) {
            this.errors.add(new ValidationError(Type.WARNING, 0, 0, "Unexpected empty response"));
        }
        if (this.document.size() > 0 && this.shouldBeEmpty) {
            this.errors.add(new ValidationError(Type.WARNING, 0, 0, "Unexpected non-empty content"));
        }
        int lineNr = 1;
        for (String line : this.document) {
            int idx = 0;
            while ((idx = line.indexOf(TEST_APOS, idx)) >= 0) {
                this.errors.add(new ValidationError(Type.ERROR, lineNr, idx, "Unescaped apostrophe character"));
                idx++;
            }
            idx = 0;
            while ((idx = line.indexOf(TEST_QUOT, idx)) >= 0) {
                this.errors.add(new ValidationError(Type.ERROR, lineNr, idx, "Unescaped quote character"));
                idx++;
            }
            if ((idx = line.indexOf("Error while parsing velocity page")) >= 0) {
                this.errors.add(new ValidationError(Type.WARNING, lineNr, idx,
                    "Parse error in the response. The template was not evaluated correctly."));
            }
            if ((idx = line.indexOf("org.xwiki.rendering.macro.MacroExecutionException")) >= 0) {
                this.errors.add(new ValidationError(Type.WARNING, lineNr, idx,
                    "Macro execution exception in the response."));
            }
            if ((idx = line.indexOf("Wrapped Exception: unexpected char:")) >= 0) {
                this.errors.add(new ValidationError(Type.WARNING, lineNr, idx, "Possible SQL error trace."));
            }
            // TODO also check <> and \ for JavaScript
            // TODO check for overescaping
            lineNr++;
        }
        return this.errors;
    }

    /**
     * {@inheritDoc}
     * @see org.xwiki.validator.Validator#getErrors()
     */
    public List<ValidationError> getErrors()
    {
        return this.errors;
    }

    /**
     * {@inheritDoc}
     * @see org.xwiki.validator.Validator#clear()
     */
    public void clear()
    {
        this.errors = new ArrayList<ValidationError>();
    }

    /**
     * {@inheritDoc}
     * @see org.xwiki.validator.Validator#getName()
     */
    public String getName()
    {
        return "XML ESCAPING";
    }

    /**
     * Set to true if empty document is valid. A validation error will be thrown if document is empty,
     * but {@link #shouldBeEmpty} is false and vice versa.
     * 
     * @param value new value
     */
    public void setShouldBeEmpty(boolean value)
    {
        this.shouldBeEmpty = value;
    }
}

