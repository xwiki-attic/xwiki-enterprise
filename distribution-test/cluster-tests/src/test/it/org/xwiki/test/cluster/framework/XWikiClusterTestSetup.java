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
package org.xwiki.test.cluster.framework;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Test;

import org.xwiki.test.XWikiExecutor;
import org.xwiki.test.XWikiTestSetup;

/**
 * Set clustering configuration and start two instances of XWiki.
 * <p>
 * I also enable debug log in remote observation manager to get more useful when something failed on CI server.
 * 
 * @version $Id$
 */
public class XWikiClusterTestSetup extends XWikiTestSetup
{
    private static final String WEBINF_PATH = "/observation/remote/jgroups";

    public XWikiClusterTestSetup(Test test)
    {
        super(test, 2);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.test.XWikiTestSetup#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        initChannel(0, "tcp1");
        initChannel(1, "tcp2");

        super.setUp();
    }

    private void initChannel(int index, String channelName) throws Exception
    {
        XWikiExecutor executor = getXWikiExecutor(index);

        Properties properties = executor.loadXWikiProperties();
        properties.setProperty("observation.remote.enabled", "true");
        properties.setProperty("observation.remote.channels", channelName);
        executor.saveXWikiProperties(properties);

        Properties log4JProperties = executor.loadLog4JProperties();
        log4JProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        log4JProperties.setProperty("log4j.appender.stdout.Target", "System.out");
        log4JProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        log4JProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
            "%d [%X{url}] [%t] %-5p %-30.30c{2} %x - %m %n");
        log4JProperties.setProperty("log4j.rootLogger", "warn, stdout");
        log4JProperties.setProperty("log4j.logger.org.xwiki.observation.remote", "debug");
        log4JProperties.setProperty("log4j.logger.com.xpn.xwiki.internal", "debug");
        executor.saveLog4JProperties(log4JProperties);

        String filename = channelName + ".xml";

        InputStream is = getClass().getResourceAsStream("/" + filename);
        try {
            FileOutputStream fos = new FileOutputStream(executor.getWebInfDirectory() + WEBINF_PATH + "/" + filename);

            byte[] buffer = new byte[1024];

            for (int nb; (nb = is.read(buffer)) > 0;) {
                fos.write(buffer, 0, nb);
            }
        } finally {
            is.close();
        }
    }
}
