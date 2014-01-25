package pl.edu.agh.samm.testapp.core;

public interface ISlaveResolver {
	IRemoteSlave getSlaveProxy(String id);

	String registerSlave(IRemoteSlave slave);

	void unregisterSlave(String id);

	int getSlavesCount();

	IRemoteSlave getNextSlave();
	
	boolean hasNextSlave();

	String getId(IRemoteSlave slave);

	void unregisterSlave(IRemoteSlave slave);
}
