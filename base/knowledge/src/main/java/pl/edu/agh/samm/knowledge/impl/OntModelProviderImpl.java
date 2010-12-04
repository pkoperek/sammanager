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

package pl.edu.agh.samm.knowledge.impl;

import java.io.InputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import pl.edu.agh.samm.knowledge.IOntModelProvider;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class OntModelProviderImpl implements IOntModelProvider {

	private OntModel ontModel;

	// private static final String DEFAULT_RESOURCE_ONTOLOGY_FILE_URI =
	// "src/main/resources/samm_1.owl";
	private static final String DEFAULT_RESOURCE_ONTOLOGY_FILE_URI = "/samm_1.owl";

	public void init() {
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(DEFAULT_RESOURCE_ONTOLOGY_FILE_URI);

		// InputStream inputStream = null;
		// try {
		// inputStream = new FileInputStream(
		// DEFAULT_RESOURCE_ONTOLOGY_FILE_URI);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		ontModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ontModel);
		ontModel.read(inputStream, "");

	}

	@Override
	public OntModel getOntModel() {
		return ontModel;
	}

}
