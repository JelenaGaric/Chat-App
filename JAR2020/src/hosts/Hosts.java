package hosts;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import model.Host;

@Singleton
@LocalBean
public class Hosts {

	private ArrayList<Host> hosts= new ArrayList<Host>();
	private Host currentHost;
	private Host masterHost;
	
	public ArrayList<Host> getHosts() {
		return hosts;
	}
	public void setHosts(ArrayList<Host> hosts) {
		this.hosts = hosts;
	}
	public Host getCurrentHost() {
		return currentHost;
	}
	public void setCurrentHost(Host currentHost) {
		this.currentHost = currentHost;
	}
	public Host getMasterHost() {
		return masterHost;
	}
	public void setMasterHost(Host masterHost) {
		this.masterHost = masterHost;
	}
	
	
}
