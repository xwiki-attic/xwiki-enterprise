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
package org.xwiki.test.officeimporter.framework;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.types.Commandline;

/**
 * Starts the open office server before running tests.
 * 
 * @version $Id$
 * @since 1.8M1
 */
public class XWikiOfficeImporterTestSetup extends TestSetup
{
    /**
     * System property containing the path to the openoffice executable.
     */
    private static final String OPENOFFICE_EXECUTABLE = System.getProperty("openOfficeExecutable");

    /**
     * The ant {@link Project}.
     */
    private Project project;

    /**
     * Constructs an {@link XWikiOfficeImporterTestSetup}.
     * 
     * @param test the {@link Test}.
     */
    public XWikiOfficeImporterTestSetup(Test test)
    {
        super(test);
        this.project = new Project();
        this.project.init();
    }

    @Override
    protected void setUp() throws Exception
    {
        Thread startThread = new Thread(new Runnable()
        {
            public void run()
            {
                try {
                    startOpenOfficeServer();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println("Launching openoffice server...");
        startThread.start();
    }

    /**
     * Starts the openoffice server.
     * 
     * @throws Exception Indicates an error executing the target {@link ExecTask}.
     */
    private void startOpenOfficeServer() throws Exception
    {
        ExecTask execTask = (ExecTask) this.project.createTask("exec");
        Commandline commandLine =
            new Commandline(OPENOFFICE_EXECUTABLE
                + " -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard");
        execTask.setCommand(commandLine);
        execTask.execute();
    }
}
