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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IKnowledgeProvider;
import pl.edu.agh.samm.common.core.IResourceInstancesManager;
import pl.edu.agh.samm.common.impl.MapHelper;
import pl.edu.agh.samm.common.impl.VariationGenerator;
import pl.edu.agh.samm.common.knowledge.IKnowledge;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public abstract class CommonActionOperations {

	private static final Logger logger = LoggerFactory.getLogger(CommonActionOperations.class);

	protected IKnowledgeProvider knowledgeProvider;

	protected IResourceInstancesManager resourceInstancesManager;

	protected void visitAllPossibleActionsWithParameters(IActionVisitor visitor) {
		IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();
		Map<String, List<String>> resourcesOfTypeCache = new HashMap<String, List<String>>();
		List<String> actionsWithFilledParameters = new LinkedList<String>();
		findPossibleActionsWithParamaters(actionsWithFilledParameters, resourcesOfTypeCache);

		logger.debug("Got following actions with parameters: "
				+ Arrays.toString(actionsWithFilledParameters.toArray()));

		for (String actionURI : actionsWithFilledParameters) {

			// get uris of parameters
			List<String> parameterURIs = knowledge.getParametersOfAction(actionURI);

			// determine the number of resources of each type - which will be
			// used as parameters
			int[] parameterNumbers = new int[parameterURIs.size()];
			List<String>[] resourcesLists = new List[parameterURIs.size()];
			for (int i = 0; i < parameterURIs.size(); i++) {
				String parameterURI = parameterURIs.get(i);
				String type = knowledge.getParameterType(parameterURI);
				List<String> resourcesOfType = resourcesOfTypeCache.get(type);
				parameterNumbers[i] = resourcesOfType.size();
				resourcesLists[i] = resourcesOfType;
			}

			// iterate over each variation of parameters
			VariationGenerator variationGenerator = new VariationGenerator(parameterNumbers);
			while (variationGenerator.hasNext()) {
				// here we have a variation of parameters
				int[] variation = variationGenerator.getNext();

				// generate map containing Concrete resource mapping ->
				// Parameter
				Map<String, String> resourceToParameter = new HashMap<String, String>();
				for (int i = 0; i < parameterNumbers.length; i++) {
					// here we have the resource for i-th parameter
					String resourceURI = resourcesLists[i].get(variation[i]);
					String parameterURI = parameterURIs.get(i);
					resourceToParameter.put(resourceURI, parameterURI);
				}

				Action act = new Action();
				act.setActionURI(actionURI);
				act.setParameterValues(MapHelper.mapRevert(resourceToParameter));

				visitor.visitAction(act);

			}
		}

	}

	private void findPossibleActionsWithParamaters(List<String> actionsWithFilledParameters,
			Map<String, List<String>> resourcesOfTypeCache) {
		IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();

		List<String> allAvailableActions = knowledge.getAllAvailableActions();
		actionLoop: for (String actionURI : allAvailableActions) {
			List<String> parameterURIs = knowledge.getParametersOfAction(actionURI);
			for (String parameterURI : parameterURIs) {
				String type = knowledge.getParameterType(parameterURI);
				List<String> resourcesOfType = resourceInstancesManager.getResourcesOfType(type);

				if (resourcesOfType == null) {
					logger.debug("No resources of type: " + type + " for " + actionURI + "|" + parameterURI);
					break actionLoop;
				} else {
					resourcesOfTypeCache.put(type, resourcesOfType);
				}
			}

			// if we'll get here - ok - this action may pass!
			actionsWithFilledParameters.add(actionURI);
		}
	}

	/**
	 * @param resourceInstancesManager
	 *            the resourceInstancesManager to set
	 */
	public void setResourceInstancesManager(IResourceInstancesManager resourceInstancesManager) {
		this.resourceInstancesManager = resourceInstancesManager;
	}

	/**
	 * @param knowledgeProvider
	 *            the knowledgeProvider to set
	 */
	public void setKnowledgeProvider(IKnowledgeProvider knowledgeProvider) {
		this.knowledgeProvider = knowledgeProvider;
	}

}
