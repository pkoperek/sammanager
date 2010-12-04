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
package pl.edu.agh.samm.eclipse.views.resources;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class ResourcesTreeNode {
	private static final long serialVersionUID = -5381487954211404831L;

	private String uri;
	private List<ResourcesTreeNode> children = null;
	private ResourcesTreeNode parent = null;

	public ResourcesTreeNode(String uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		if (uri != null) {
			return uri.substring(uri.indexOf('/') + 1);
		}
		return "UNKNOWN URI";
	}

	public boolean hasChildren() {
		return children != null;
	}

	public String getURI() {
		return uri;
	}

	public ResourcesTreeNode getParent() {
		return parent;
	}

	public ResourcesTreeNode getChildAt(int idx) {
		if (children == null) {
			return null;
		}

		return children.get(idx);
	}

	public int getChildCount() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	public void addChild(ResourcesTreeNode child) {
		if (children == null) {
			children = new LinkedList<ResourcesTreeNode>();
		}

		children.add(child);
	}

	public List<ResourcesTreeNode> getChildren() {
		return children;
	}
}
