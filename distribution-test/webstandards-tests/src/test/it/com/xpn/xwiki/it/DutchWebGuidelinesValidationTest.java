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
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.validator.ValidationError;
import org.xwiki.validator.Validator;
import org.xwiki.validator.ValidationError.Type;

import com.xpn.xwiki.it.framework.AbstractValidationTest;
import com.xpn.xwiki.it.framework.CustomDutchWebGuidelinesValidator;

/**
 * Verifies that all pages in the default wiki are valid XHTML documents.
 * 
 * @version $Id$
 */
public class DutchWebGuidelinesValidationTest extends AbstractValidationTest
{
    private CustomDutchWebGuidelinesValidator validator;

    public DutchWebGuidelinesValidationTest(String fullPageName, HttpClient client, Validator validator)
        throws Exception
    {
        super("testDocumentValidity");

        this.validator = (CustomDutchWebGuidelinesValidator) validator;
        this.fullPageName = fullPageName;
        this.client = client;
    }

    /**
     * {@inheritDoc}
     * 
     * @see junit.framework.TestCase#getName()
     */
    public String getName()
    {
        return "Validating Dutch Web Guidelines Validity for: " + fullPageName;
    }

    public void testDocumentValidity() throws Exception
    {
        GetMethod method =
            new GetMethod("http://127.0.0.1:8080/xwiki/bin/view/"
                + URLEncoder.encode(this.fullPageName, "UTF-8").replace('.', '/'));

        method.setDoAuthentication(true);
        method.setFollowRedirects(true);
        method.addRequestHeader("Authorization", "Basic " + new String(Base64.encodeBase64("Admin:admin".getBytes())));

        byte[] responseBody;

        // Execute the method.
        try {
            int statusCode = this.client.executeMethod(method);

            assertEquals("Method failed: " + method.getStatusLine(), HttpStatus.SC_OK, statusCode);

            // Read the response body.
            responseBody = method.getResponseBody();
        } finally {
            method.releaseConnection();
        }

        validator.setFullPageName(fullPageName);
        validator.setDocument((new ByteArrayInputStream(responseBody)));
        validator.validate();
               
        StringBuffer message = new StringBuffer();
        boolean isValid = true;
        message.append("Validation errors in " + fullPageName);
        for (ValidationError error : validator.getErrors()) {
            if (error.getType() != Type.WARNING) {
                isValid = false;
                message.append("\n" + error);
            }
        }

        assertTrue(message.toString(), isValid);
    }

}
