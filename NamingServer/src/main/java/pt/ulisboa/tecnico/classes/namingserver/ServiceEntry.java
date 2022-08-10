package pt.ulisboa.tecnico.classes.namingserver;

import java.util.ArrayList;
import java.util.List;

public class ServiceEntry {
    private String service;
    private List<ServerEntry> serverEntries = new ArrayList<>();

    public ServiceEntry(String service, String port, List<String> qualifiers) {
        ServerEntry serverEntry = new ServerEntry(port,qualifiers);
        this.service = service;
        serverEntries.add(serverEntry);
    }
    public void addServerEntries(String port, List<String> qualifiers){
        ServerEntry serverEntry = new ServerEntry(port,qualifiers);
        serverEntries.add(serverEntry);
    }
    public void removeEntry(String host_port){
        serverEntries.removeIf(se -> se.getHost_port().equals(host_port));
    }
    public List<ServerEntry> getServerEntries() {
        return serverEntries;
    }

    public String getServer_name() {
        return service;
    }

    public void setServer_name(String server_name) {
        this.service = server_name;
    }

}
