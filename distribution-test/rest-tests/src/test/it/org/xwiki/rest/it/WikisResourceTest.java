package org.xwiki.rest.it;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xwiki.rest.Relations;
import org.xwiki.rest.model.jaxb.Link;
import org.xwiki.rest.model.jaxb.Wiki;
import org.xwiki.rest.model.jaxb.Wikis;
import org.xwiki.rest.resources.wikis.WikisResource;

public class WikisResourceTest extends AbstractHttpTest
{
    @Override
    public void testRepresentation() throws Exception
    {
        TestUtils.banner("testRepresentation()");

        GetMethod getMethod = executeGet(getFullUri(WikisResource.class));
        assertEquals(HttpStatus.SC_OK, getMethod.getStatusCode());
        TestUtils.printHttpMethodInfo(getMethod);

        Wikis wikis = (Wikis) unmarshaller.unmarshal(getMethod.getResponseBodyAsStream());
        assertTrue(wikis.getWikis().size() > 0);

        for (Wiki wiki : wikis.getWikis()) {
            Link link = getFirstLinkByRelation(wiki, Relations.SPACES);
            assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.CLASSES);
            assertNotNull(link);

            link = getFirstLinkByRelation(wiki, Relations.MODIFICATIONS);
            assertNotNull(link);

            checkLinks(wiki);

        }
    }

}
