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

import pl.edu.agh.samm.api.action.ActionExecution;
import pl.edu.agh.samm.api.core.IActionExecutionListener;
import pl.edu.agh.samm.api.db.IStorageService;

/**
 * @author koperek
 * 
 */
public class ActionExecutionDBListener implements IActionExecutionListener {

	private IStorageService storageService = null;
	private IActionExecutor actionExecutor = null;

	public void init() {
		this.actionExecutor.addActionExecutorListener(this);
	}

	public void setActionExecutor(IActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	public void setStorageService(IStorageService storageService) {
		this.storageService = storageService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.api.core.IActionExecutionListener#notifyActionExecution
	 * (pl.edu.agh.samm.api.action.ActionExecution)
	 */
	@Override
	public void notifyActionExecution(ActionExecution actionExecution)
			throws Exception {
		this.storageService.storeActionExecution(actionExecution.getAction(),
				actionExecution.getStartTime(), actionExecution.getEndTime());
	}

}
