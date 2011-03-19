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
package org.xwiki.test.storage.framework;

import org.xwiki.test.XWikiExecutor;
import org.xwiki.test.storage.profiles.Profile;

/**
 * This is a container for holding all of the information which should persist throughout all of the tests.
 *
 * @version $Id$
 * @since 3.0M3
 */
public class PersistentTestContext
{
    /** This starts and stops the wiki engine. */
    private final XWikiExecutor executor;

    private final Profile profile;

    public PersistentTestContext(final Profile profile) throws Exception
    {
        this.executor = new XWikiExecutor(0);
        this.profile = profile;
        profile.applyProfile(this.executor);
        executor.start();
    }

    public PersistentTestContext(PersistentTestContext toClone)
    {
        this.executor = toClone.executor;
        this.profile = toClone.profile;
    }

    public void shutdown() throws Exception
    {
        this.profile.removeProfile(this.executor);
        executor.stop();
    }

    /**
     * Get a clone of this context which cannot be stopped by calling shutdown.
     * this is needed so that individual tests don't shutdown the wiki engine when AllTests are being run.
     */
    public PersistentTestContext getUnstoppable()
    {
        return new PersistentTestContext(this)
        {
            public void shutdown()
            {
                // Do nothing, that's why it's unstoppable.
            }
        };
    }
}
