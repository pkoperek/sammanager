/**
 * 
 */
package pl.edu.agh.samm.eclipse.views.providers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourcesTreeContentProvider implements ITreeContentProvider {

	private List<ResourcesTreeNode> roots = new LinkedList<ResourcesTreeNode>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object arg0) {
		ResourcesTreeNode node = (ResourcesTreeNode) arg0;
		List<ResourcesTreeNode> children = node.getChildren();
		if (children == null) {
			return new Object[0];
		}
		return children.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object arg0) {
		ResourcesTreeNode node = (ResourcesTreeNode) arg0;
		return node.getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object arg0) {
		ResourcesTreeNode node = (ResourcesTreeNode) arg0;
		return node.hasChildren();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object objroots) {
		return roots.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer arg0, Object oldInput, Object newInput) {
		Collection<String> input = (Collection<String>) newInput;
		if (input == null) {
			return;
		}
		List<String> sortedList = new LinkedList<String>(input);
		Collections.sort(sortedList);

		roots.clear();
		Map<String, ResourcesTreeNode> uriToNode = new HashMap<String, ResourcesTreeNode>();

		for (String uri : sortedList) {
			int lastIndexOfSlash = uri.lastIndexOf("/");
			String parentUri = uri.substring(0, lastIndexOfSlash);
			ResourcesTreeNode childNode = new ResourcesTreeNode(uri);

			if (uriToNode.containsKey(parentUri)) {
				uriToNode.get(parentUri).addChild(childNode);
			} else {
				roots.add(childNode);
			}

			uriToNode.put(uri, childNode);
		}
	}
}
