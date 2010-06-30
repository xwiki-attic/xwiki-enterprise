package com.xpn.xwiki.it;

import org.apache.commons.httpclient.HttpClient;
import org.xwiki.validator.Validator;

import com.xpn.xwiki.it.framework.DefaultValidationTest;
import com.xpn.xwiki.it.framework.DocumentReferenceTarget;
import com.xpn.xwiki.it.framework.Target;

public class CustomDutchWebGuidelinesValidationTest extends DefaultValidationTest
{
    public CustomDutchWebGuidelinesValidationTest(Target target, HttpClient client, Validator validator,
        String credentials) throws Exception
    {
        super(target, client, validator, credentials);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.it.framework.DefaultValidationTest#testDocumentValidity()
     */
    @Override
    public void testDocumentValidity() throws Exception
    {
        if (this.target instanceof DocumentReferenceTarget) {
            ((CustomDutchWebGuidelinesValidator) this.validator)
                .setDocumentReferenceTarget((DocumentReferenceTarget) this.target);
        }

        super.testDocumentValidity();
    }

}
