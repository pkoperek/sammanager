package pl.edu.agh.samm.fileconfig;

import java.util.LinkedList;
import java.util.List;

public class ConfigurationResourceSet {
	private List<ConfigurationResource> resources = new LinkedList<ConfigurationResource>();

	public List<ConfigurationResource> getResources() {
		return resources;
	}

	public void addResource(ConfigurationResource resource) {
		resources.add(resource);
	}

	public void removeResource(ConfigurationResource resource) {
		resources.remove(resource);
	}
}
