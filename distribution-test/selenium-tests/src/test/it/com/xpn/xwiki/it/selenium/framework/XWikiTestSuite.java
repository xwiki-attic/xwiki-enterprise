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
package com.xpn.xwiki.it.selenium.framework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import junit.framework.TestSuite;

/**
 * A JUnit TestSuite extension that understands Skin Executors and which can pass a defined Skin
 * Executor to the underlying test it wraps. This is to let TestCases extending
 * AbstractXWikiTestCase the ability to tell under what Skin Executors it should execute. For
 * example to run the MyTestCase class under both an Albatross skin and a Dodo skin you would write:
 * 
 * <pre><code>
 * public static Test suite()
 * {
 *     XWikiTestSuite suite = new XWikiTestSuite(&quot;My Suite&quot;);
 *     suite.addTestSuite(MyTestCase.class, AlbatrossSkinExecutor.class);
 *     suite.addTestSuite(MyTestCase.class, DodoSkinExecutor.class);
 *     return suite;
 * }
 * </code></pre>
 */
public class XWikiTestSuite extends TestSuite
{
    /**
     * The property that can be specified by users to only run some specified test from the
     * TestCase class.
     */
    private static final String PATTERN_METHOD = ".*" + System.getProperty("patternMethod", "");

    public XWikiTestSuite(String name)
    {
        super(name);
    }

    public void addTestSuite(Class testClass, Class skinExecutorClass)
    {
        if (!AbstractXWikiTestCase.class.isAssignableFrom(testClass)) {
            throw new RuntimeException("Test case must extend ["
                + AbstractXWikiTestCase.class.getName() + "]");
        }

        try {
            addSkinExecutorToSuite(testClass, skinExecutorClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add skin executor class ["
                + skinExecutorClass.getName() + "] for test case class [" + testClass.getName()
                + "]", e);
        }
    }

    private void addSkinExecutorToSuite(Class testClass, Class skinExecutorClass)
        throws Exception
    {
        // Find all methods starting with "test" and add them
        Method[] methods = testClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("test")) {
                // Only add test methods that matches the filter specified by the user who
                // started the test. This is to allow running only a single test of a single
                // TestCase class.
                if (methods[i].getName().matches(PATTERN_METHOD)) {
                    addSkinExecutorToTest(methods[i].getName(), testClass, skinExecutorClass);
                }
            }
        }
    }

    private void addSkinExecutorToTest(String testName, Class testClass, Class skinExecutorClass)
        throws Exception
    {
        Constructor constructor = testClass.getConstructor(null);
        AbstractXWikiTestCase test = (AbstractXWikiTestCase) constructor.newInstance(null);

        Constructor skinExecutorConstructor =
            skinExecutorClass.getConstructor(new Class[] {AbstractXWikiTestCase.class});
        SkinExecutor skinExecutor =
            (SkinExecutor) skinExecutorConstructor
                .newInstance(new Object[] {(AbstractXWikiTestCase) test});

        test.setSkinExecutor(skinExecutor);
        test.setName(testName);

        addTest(test);
    }
}
