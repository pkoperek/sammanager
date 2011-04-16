/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.agh.samm.fileconfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetric;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author koperek
 * 
 */
public class FileConfigurator {
	private static final Logger logger = LoggerFactory
			.getLogger(FileConfigurator.class);

	private ICoreManagement coreManagement = null;

	private XStream xstream = null;

	public static final String PROPERTIES_FILENAME_KEY = "configFile";

	public void init() {
		logger.info("Starting Property File Configurator!");

		xstream = new XStream(new DomDriver());
		configureXStream(xstream);

		String configFilePath = System.getProperty(PROPERTIES_FILENAME_KEY);

		if (configFilePath == null) {
			logger.warn("No config file specified! Please use the -D"
					+ PROPERTIES_FILENAME_KEY + " VM option!");
		} else {
			File configFile = new File(configFilePath);
			try {
				Configuration configuration = (Configuration) xstream
						.fromXML(new FileReader(configFile));

				RuleSet ruleSet = configuration.getRuleSet();

				if (ruleSet != null && ruleSet.getRules() != null) {
					for (Rule rule : ruleSet.getRules()) {
						logger.info("Adding rule: " + rule);
						coreManagement.addRule(rule);
					}
				} else {
					logger.warn("No rules specified!");
				}

				// configure resources
				ConfigurationResourceSet resourceSet = configuration
						.getResourceSet();

				if (resourceSet != null && resourceSet.getResources() != null) {
					for (ConfigurationResource configurationResource : resourceSet
							.getResources()) {
						try {
							coreManagement
									.registerResource(configurationResource
											.getResource());
						} catch (ResourceAlreadyRegisteredException e) {
							logger.error("Cannot add resource: "
									+ configurationResource, e);
						}
					}
				}

				// configure metrics
				ConfigurationMetricSet metricSet = configuration.getMetricSet();
				if (metricSet != null && metricSet.getMetrics() != null) {
					for (ConfigurationMetric metric : metricSet.getMetrics()) {
						logger.info("Adding metric: " + metric.getMetricUri()
								+ " for " + metric.getResourceUri());

						// create metric instance
						IMetric metricInstance = coreManagement
								.createMetricInstance(metric.getMetricUri(),
										metric.getResourceUri());

						logger.info("Starting metric: " + metricInstance);
						coreManagement.startMetric(metricInstance);
					}
				}
			} catch (FileNotFoundException e) {
				logger.error("File (" + configFilePath + ") doesn't exist! ", e);
			}
		}

		logger.info("Starting Property File Configurator finished!");
	}

	public static void configureXStream(XStream xstream) {
		// resources
		xstream.alias("resourceSet", ConfigurationResourceSet.class);
		xstream.addImplicitCollection(ConfigurationResourceSet.class,
				"resources");
		xstream.alias("resource", ConfigurationResource.class);
		xstream.alias("property", ConfigurationResourceProperty.class);
		xstream.useAttributeFor(ConfigurationResource.class, "uri");
		xstream.addImplicitCollection(ConfigurationResource.class, "properties");
		
		// rules
		xstream.alias("ruleSet", RuleSet.class);
		xstream.addImplicitCollection(RuleSet.class, "rules");
		xstream.alias("rule", Rule.class);
		xstream.useAttributeFor(Rule.class, "name");
		
		// action
		xstream.alias("action", Action.class);
		
		// configuration
		xstream.alias("configuration", Configuration.class);
		
		// metrics
		xstream.alias("metric", ConfigurationMetric.class);
		xstream.useAttributeFor(ConfigurationMetric.class, "metricUri");
		xstream.useAttributeFor(ConfigurationMetric.class, "resourceUri");
		xstream.alias("metricSet", ConfigurationMetricSet.class);
		xstream.addImplicitCollection(ConfigurationMetricSet.class, "metrics");
	}

	XStream getXstream() {
		return xstream;
	}

	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

}
