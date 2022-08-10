package pt.ulisboa.tecnico.classes.namingserver;



import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class NamingServices {
    private ConcurrentHashMap<String,ServiceEntry> map_serviceEntries = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ServiceEntry> getMap_serviceEntries() {
        return map_serviceEntries;
    }

    public void register(String service, String port, List<String> qualifiers){
        ServiceEntry serviceEntry;
        if(map_serviceEntries.containsKey(service)){
            serviceEntry = map_serviceEntries.get(service);
            serviceEntry.addServerEntries(port,qualifiers);

        }else{
            serviceEntry = new ServiceEntry(service,port,qualifiers);
            map_serviceEntries.put(service,serviceEntry);}
    }
    public List<ServerEntry> lookup(String service,List<String> qualifiers) {
        if (map_serviceEntries.containsKey(service)) {
            List<ServerEntry> serverEntries = map_serviceEntries.get(service).getServerEntries();
            if(qualifiers.isEmpty()){return serverEntries;}
            List<ServerEntry> validEntries =  serverEntries.stream().filter(s -> s.hasQualifiers(qualifiers)).collect(Collectors.toList());
            if( validEntries == null){ return Collections.emptyList();}
            return validEntries;
        }
        return Collections.emptyList();
    }
    public void delete(String service, String host_port) {
        if(map_serviceEntries.containsKey(service)){
            map_serviceEntries.get(service).removeEntry(host_port);
        }
    }
}
