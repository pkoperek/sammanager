/**
 * 
 */
package pl.edu.agh.samm.eclipse.views.providers;

import org.eclipse.jface.viewers.LabelProvider;

import pl.edu.agh.samm.common.action.Action;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionsListLabelProvider extends LabelProvider {
	@Override
	public String getText(Object element) {
		Action action = (Action) element;
		return action.getActionURI();
	}
}
