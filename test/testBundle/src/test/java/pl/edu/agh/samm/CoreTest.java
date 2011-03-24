package pl.edu.agh.samm;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

public class CoreTest extends AbstractConfigurableBundleCreatorTests {

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
	}

	@Override
	protected String[] getTestFrameworkBundlesNames() {
		return new String[] {

				// deps
				"org.junit, com.springsource.junit, 3.8.2",
				"org.objectweb.asm, com.springsource.org.objectweb.asm, 2.2.3",
				"org.aopalliance, com.springsource.org.aopalliance, 1.0.0",
				"org.slf4j, com.springsource.slf4j.org.apache.commons.logging, 1.6.1",
				"org.slf4j, com.springsource.slf4j.api, 1.6.1",
				"org.slf4j, com.springsource.slf4j.log4j, 1.6.1",

				// spring osgi
				"org.springframework.osgi, org.springframework.osgi.core, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.extender, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.extensions.annotation, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.io, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.test, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.mock, 1.2.1",
				
				// spring core
				"org.springframework, org.springframework.jdbc, 3.0.5.RELEASE",
				"org.springframework, org.springframework.context, 3.0.5.RELEASE",
				"org.springframework, org.springframework.beans, 3.0.5.RELEASE",
				"org.springframework, org.springframework.aop, 3.0.5.RELEASE",
				"org.springframework, org.springframework.test, 3.0.5.RELEASE",
				"org.springframework, org.springframework.core, 3.0.5.RELEASE",
				"org.springframework, org.springframework.asm, 3.0.5.RELEASE",
				"org.springframework, org.springframework.expression, 3.0.5.RELEASE",
				"org.springframework, org.springframework.transaction, 3.0.5.RELEASE",
				
				// apache
				"org.apache.commons, com.springsource.org.apache.commons.pool, 1.5.3",
				"org.apache.commons, com.springsource.org.apache.commons.dbcp, 1.2.2.osgi",
				"org.apache.commons, commons-math, 2.1",
				
				// hsqldb
				"org.hsqldb, com.springsource.org.hsqldb, 1.8.0.10",
				
				// providers
				"pl.edu.agh.samm.providers,jena, 2.6.3",
				"pl.edu.agh.samm.providers,esper, 4.1.0",
				"pl.edu.agh.samm.providers,weka, 3.6.1",
				"pl.edu.agh.samm.providers,pellet, 2.0.1",
				"pl.edu.agh.samm.providers,awssdk, 1.0.005"
				
		};
	}

	@Override
	protected Resource[] getTestBundles() {
		String SAMM_MANAGER_HOME = "/home/koperek/Projects/phd/sammanager/";
		Resource[] testBundles = {

		};
		return testBundles;
	}

	// protected String[] getTestBundlesNames() {
	// return new String[] { "pl.edu.agh.samm.base, core, 0.0.1-SNAPSHOT",
	// "pl.edu.agh.samm.base, commons, 0.0.1-SNAPSHOT",
	// "pl.edu.agh.samm.base, knowledge, 0.0.1-SNAPSHOT",
	// "pl.edu.agh.samm.base.registries, registryjmx, 0.0.1-SNAPSHOT",
	// "pl.edu.agh.samm.base, dblistener, 0.0.1-SNAPSHOT",
	// "pl.edu.agh.samm.base.registries, eucalyptus_tp, 0.0.1-SNAPSHOT" };
	// }

	public void testOsgiEnvironment() throws Exception {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			System.out.println(String.format("%02d", i) + ": "
					+ OsgiStringUtils.nullSafeName(bundles[i]));
		}
		System.out.println();
	}
}
