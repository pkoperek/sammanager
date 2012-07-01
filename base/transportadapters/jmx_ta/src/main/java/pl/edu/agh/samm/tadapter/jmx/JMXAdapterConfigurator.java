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

package pl.edu.agh.samm.tadapter.jmx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Holds configuration for JMX Adapter
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class JMXAdapterConfigurator {

	public static final String ENV_PROPERTIES_FILE = "jmxAdapter.propertiesFile";
	private Properties mappings;
    private Properties defaultMappings;

	public void init() throws FileNotFoundException, IOException {
		String propertiesFileName = System.getProperty(ENV_PROPERTIES_FILE);
		if (propertiesFileName != null) {
			loadConfiguration(propertiesFileName);
		} else {
			mappings = defaultMappings;
		}
	}

	/**
	 * Loads configuration file.
	 */
	public void loadConfiguration(final File file)
			throws FileNotFoundException, IOException {
		mappings = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			mappings.load(inputStream);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

	}

	public void loadConfiguration(String fileName)
			throws FileNotFoundException, IOException {
		loadConfiguration(new File(fileName));
	}

	public String getProperty(String key) {
		return mappings.getProperty(key);
	}

    public void setDefaultMappings(Properties defaultMappings) {
        this.defaultMappings = defaultMappings;
    }
}
