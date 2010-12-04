/**
 * 
 */
package pl.edu.agh.samm.eclipse.views.providers;

import org.eclipse.jface.viewers.LabelProvider;

import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourcesLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		ResourcesTreeNode node = (ResourcesTreeNode) element;
		return node.toString();
	}
}
