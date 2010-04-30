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
package com.xpn.xwiki.it;

import java.io.ByteArrayInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.xwiki.validator.ValidationError;
import org.xwiki.validator.Validator;
import org.xwiki.validator.ValidationError.Type;

import com.xpn.xwiki.it.framework.AbstractValidationTest;
import com.xpn.xwiki.it.framework.CustomDutchWebGuidelinesValidator;
import com.xpn.xwiki.it.framework.DocumentReferenceTarget;
import com.xpn.xwiki.it.framework.Target;

/**
 * Verifies that all pages in the default wiki are valid XHTML documents.
 * 
 * @version $Id$
 */
public class DutchWebGuidelinesValidationTest extends AbstractValidationTest
{
    private CustomDutchWebGuidelinesValidator validator;

    public DutchWebGuidelinesValidationTest(Target target, HttpClient client, Validator validator) throws Exception
    {
        super("testDocumentValidity", target, client);

        this.validator = (CustomDutchWebGuidelinesValidator) validator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#getName()
     */
    public String getName()
    {
        return "Validating Dutch Web Guidelines Validity for: " + this.target.getName();
    }

    public void testDocumentValidity() throws Exception
    {
        byte[] responseBody = getResponseBody();

        if (this.target instanceof DocumentReferenceTarget) {
            this.validator.setDocumentReference(((DocumentReferenceTarget) this.target).getDocumentReference());
        }
        this.validator.setDocument((new ByteArrayInputStream(responseBody)));
        this.validator.validate();

        StringBuffer message = new StringBuffer();
        boolean isValid = true;
        message.append("Validation errors in " + this.target.getName());
        for (ValidationError error : this.validator.getErrors()) {
            if (error.getType() != Type.WARNING) {
                isValid = false;
                message.append("\n" + error);
            }
        }

        assertTrue(message.toString(), isValid);
    }

}
