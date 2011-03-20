package pl.edu.agh.samm;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
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
				"org.junit, com.springsource.junit, 3.8.2",
				"org.objectweb.asm, com.springsource.org.objectweb.asm, 2.2.3",
				"org.aopalliance, com.springsource.org.aopalliance, 1.0.0",
				"org.slf4j, com.springsource.slf4j.org.apache.commons.logging, 1.6.1",
				"org.slf4j, com.springsource.slf4j.api, 1.6.1",
				"org.slf4j, com.springsource.slf4j.log4j, 1.6.1",
				"org.springframework.osgi, org.springframework.osgi.core, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.extender, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.extensions.annotation, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.io, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.test, 1.2.1",
				"org.springframework.osgi, org.springframework.osgi.mock, 1.2.1",
				"org.springframework, org.springframework.context, 3.0.5.RELEASE",
				"org.springframework, org.springframework.beans, 3.0.5.RELEASE",
				"org.springframework, org.springframework.aop, 3.0.5.RELEASE",
				"org.springframework, org.springframework.test, 3.0.5.RELEASE",
				"org.springframework, org.springframework.core, 3.0.5.RELEASE",
				"org.springframework, org.springframework.asm, 3.0.5.RELEASE",
				"org.springframework, org.springframework.expression, 3.0.5.RELEASE" };
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
