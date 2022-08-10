package pt.ulisboa.tecnico.classes.namingserver;

import java.util.List;
import java.util.ArrayList;

public class ServerEntry {
    private String host_port;
    private List<String> qualifiers = new ArrayList<>();

    public ServerEntry(String host_port, List<String> qualifiers) {
        this.host_port = host_port;
        this.qualifiers = qualifiers;
    }

    public List<String> getQualifiers() {
        return qualifiers;
    }

    public boolean hasQualifiers(List<String> qualifiersToCheck){
        return qualifiersToCheck.stream().anyMatch(q -> qualifiers.contains(q));
    }

    public String getHost_port() {
        return host_port;
    }

    public void setHost_port(String host_port) {
        this.host_port = host_port;
    }
}
