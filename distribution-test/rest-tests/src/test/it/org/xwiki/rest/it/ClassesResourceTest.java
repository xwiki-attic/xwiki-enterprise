package org.xwiki.rest.it;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.it.framework.AbstractHttpTest;
import org.xwiki.rest.it.framework.TestUtils;
import org.xwiki.rest.model.jaxb.Class;
import org.xwiki.rest.model.jaxb.Classes;
import org.xwiki.rest.model.jaxb.Property;
import org.xwiki.rest.resources.classes.ClassesResource;

public class ClassesResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod =
            executeGet(getUriBuilder(ClassesResource.class).build(
                getWiki()).toString());
        TestUtils.printHttpMethodInfo(getMethod);
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());

        Classes classes = (Classes) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());

        for (Class clazz : classes.getClazzs()) {
            checkLinks(clazz);

            for (Property property : clazz.getProperties()) {
                checkLinks(property);
            }
        }
    }

}
