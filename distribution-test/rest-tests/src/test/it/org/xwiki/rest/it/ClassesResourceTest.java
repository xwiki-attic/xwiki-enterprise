package org.xwiki.rest.it;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Classes;
import org.xwiki.rest.resources.classes.ClassesResource;
import org.xwiki.rest.resources.pages.PageResource;

public class ClassesResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        Map<String, String> parametersMap = new HashMap<String, String>();
        parametersMap.put(Constants.WIKI_NAME_PARAMETER, getWiki());

        GetMethod getMethod =
            executeGet(getFullUri(Utils.formatUriTemplate(getUriPatternForResource(ClassesResource.class),
                parametersMap)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Classes classes = (Classes) xstream.fromXML(getMethod.getResponseBodyAsString());

        for (org.xwiki.rest.model.Class theClass : classes.getClassList()) {
            checkLinks(theClass);
        }
    }

}
