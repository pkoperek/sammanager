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

package pl.edu.agh.samm.core;

import java.util.HashMap;
import java.util.Map;

import pl.edu.agh.samm.common.core.IKnowledgeProvider;
import pl.edu.agh.samm.common.knowledge.IKnowledge;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class KnowledgeProviderImpl implements IKnowledgeProvider {

	private Map<String, IKnowledge> knowledgeSources = new HashMap<String, IKnowledge>();

	public void onBind(IKnowledge knowledgeSource, Map properties) {
		knowledgeSources.put(knowledgeSource.getOntologyURI(), knowledgeSource);
	}

	public void onUnbind(IKnowledge knowledgeSource, Map properties) {
		for (String key : knowledgeSources.keySet()) {
			IKnowledge storedKnowledgeSource = knowledgeSources.get(key);
			if (storedKnowledgeSource.equals(knowledgeSource)) {
				knowledgeSources.remove(key);
			}
		}
	}

	@Override
	public IKnowledge getDefaultKnowledgeSource() {
		return knowledgeSources.values().toArray(new IKnowledge[0])[0];
	}

	@Override
	public IKnowledge getKnowledgeSourceForMetricURI(String metricUri) {
		IKnowledge retVal = null;
		int index = metricUri.indexOf('#');

		if (index > 0) {
			String uri = metricUri.substring(0, index);
			retVal = knowledgeSources.get(uri);
		}

		return retVal;
	}

	@Override
	public IKnowledge getKnowledgeSourceForURI(String uri) {
		return knowledgeSources.get(uri);
	}

}
