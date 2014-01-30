package pl.edu.agh.samm.testapp.core;

public interface ISlaveManager {

    void addNextSlave();

    void removeSlave();

    String registerSlave(ISlave slave);

	void unregisterSlave(String id);

	int getSlavesCount();

	ISlave getNextSlave();

    String getId(ISlave slave);

	void unregisterSlave(ISlave slave);
}
