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
import pl.edu.agh.samm.common.core.Rule;

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

	private XStream xstream = null;;

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
				RuleSet ruleSet = (RuleSet) xstream.fromXML(new FileReader(
						configFile));

				for (Rule rule : ruleSet.getRules()) {
					logger.info("Adding rule: " + rule);
					coreManagement.addRule(rule);
				}
			} catch (FileNotFoundException e) {
				logger.error("File (" + configFilePath + ") doesn't exist! ", e);
			}
		}

		logger.info("Starting Property File Configurator finished!");
	}

	public static void configureXStream(XStream xstream) {
		xstream.alias("ruleset", RuleSet.class);
		xstream.addImplicitCollection(RuleSet.class, "rules");
		xstream.alias("rule", Rule.class);
		xstream.useAttributeFor(Rule.class, "name");
		xstream.alias("action", Action.class);
	}

	XStream getXstream() {
		return xstream;
	}

	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

}