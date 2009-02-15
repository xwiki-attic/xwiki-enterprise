package org.xwiki.rest.it;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.Constants;
import org.xwiki.rest.Utils;
import org.xwiki.rest.model.Classes;
import org.xwiki.rest.model.Link;
import org.xwiki.rest.model.Relations;
import org.xwiki.rest.model.Wiki;
import org.xwiki.rest.model.Wikis;
import org.xwiki.rest.resources.classes.ClassesResource;
import org.xwiki.rest.resources.pages.PageResource;
import org.xwiki.rest.resources.wikis.WikisResource;

public class WikisResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod = executeGet(getFullUri(getUriPatternForResource(WikisResource.class)));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) xstream.fromXML(getMethod.getResponseBodyAsString());
        assertTrue(wikis.getWikiList().size() > 0);

        for (Wiki wiki : wikis.getWikiList()) {
            Link link = wiki.getFirstLinkByRelation(Relations.SPACES);
            assertNotNull(link);

            link = wiki.getFirstLinkByRelation(Relations.CLASSES);
            assertNotNull(link);

            checkLinks(wiki);

        }
    }

}
