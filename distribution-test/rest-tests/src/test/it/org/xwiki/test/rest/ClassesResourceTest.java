package org.xwiki.test.rest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.test.rest.framework.AbstractHttpTest;
import org.xwiki.rest.model.jaxb.Class;
import org.xwiki.rest.model.jaxb.Classes;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.resources.classes.ClassesResource;

public class ClassesResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        GetMethod getMethod = executeGet(getUriBuilder(ClassesResource.class).build(getWiki()).toString());
        assertEquals(getHttpMethodInfo(getMethod), HttpStatus.SC_OK, getMethod.getStatusCode());

        Classes classes = (Classes) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        for (Class clazz : classes.getClazzs()) {
            checkLinks(clazz);

            for (Property property : clazz.getProperties()) {
                checkLinks(property);
            }
        }
    }

}
