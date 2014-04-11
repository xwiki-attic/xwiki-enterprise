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
package org.xwiki.test.jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xwiki.test.integration.XWikiExecutor;

public class PingTetst
{
    @BeforeClass
    public static void before() throws IOException
    {
        FileUtils.writeByteArrayToFile(new File("target/jmeter/home/bin/httpclient.parameters"),
            IOUtils.toByteArray(PingTetst.class.getResource("/jmeterbin/httpclient.parameters")));
        FileUtils.writeByteArrayToFile(new File("target/jmeter/home/bin/jmeter.properties"),
            IOUtils.toByteArray(PingTetst.class.getResource("/jmeterbin/jmeter.properties")));
        FileUtils.writeByteArrayToFile(new File("target/jmeter/home/bin/saveservice.properties"),
            IOUtils.toByteArray(PingTetst.class.getResource("/jmeterbin/saveservice.properties")));
        FileUtils.writeByteArrayToFile(new File("target/jmeter/home/bin/upgrade.properties"),
            IOUtils.toByteArray(PingTetst.class.getResource("/jmeterbin/upgrade.properties")));
    }

    @Test
    public void ping() throws FileNotFoundException, Exception
    {
        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setName("home");
        httpSampler.setDomain("localhost");
        httpSampler.setPort(Integer.valueOf(XWikiExecutor.DEFAULT_PORT));
        httpSampler.setPath("/xwiki/");
        httpSampler.setMethod("GET");
        httpSampler.setFollowRedirects(true);

        execute(Arrays.asList(httpSampler));
    }

    public void execute(List<HTTPSampler> samplers) throws FileNotFoundException, Exception
    {
        // jmeter.properties
        JMeterUtils.loadJMeterProperties("target/jmeter/home/bin/saveservice.properties");
        JMeterUtils.setLocale(Locale.ENGLISH);
        JMeterUtils.setJMeterHome("target/jmeter/home");

        // Result collector
        ResultCollector resultCollector = new ResultCollector();
        resultCollector.setFilename("target/jmeter/report.jtl");
        SampleSaveConfiguration saveConfiguration = new SampleSaveConfiguration();
        saveConfiguration.setAsXml(true);
        saveConfiguration.setCode(true);
        resultCollector.setSaveConfig(saveConfiguration);

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("xwiki");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        LoopController loopCtrl = new LoopController();
        loopCtrl.setLoops(10);
        loopCtrl.setFirst(true);
        threadGroup.setSamplerController((LoopController) loopCtrl);

        HashTree threadGroupTree = new HashTree();
        threadGroupTree.add(samplers);

        // Test plan
        TestPlan testPlan = new TestPlan("ping");

        HashTree testPlanTree = new HashTree();
        testPlanTree.add(threadGroup, threadGroupTree);
        testPlanTree.add(resultCollector);

        HashTree hashTree = new HashTree();
        hashTree.add(testPlan, testPlanTree);

        // Engine
        StandardJMeterEngine jm = new StandardJMeterEngine("localhost");

        jm.configure(hashTree);

        jm.run();
    }
}
