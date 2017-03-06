import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *@author arman.piloyan@picsart.com
 */
public class ClientBuilder {

    private String cluster_name;
    private String node_name;
    private String adress;
    private int port;


    public ClientBuilder(String cluster_name, String node_name, String adress, int port) {
        this.cluster_name = cluster_name;
        this.node_name = node_name;
        this.adress = adress;
        this.port = port;

    }

    public String getCluster_name() {
        return cluster_name;
    }

    public void setCluster_name(String cluster_name) {
        this.cluster_name = cluster_name;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Client getClient() throws UnknownHostException {

        Settings settings = Settings.builder()
                .put("cluster.name", cluster_name)
                .put("node.name", node_name).build();
        TransportAddress address = new InetSocketTransportAddress(InetAddress.getByName(adress), port);
        Client client = new PreBuiltTransportClient(settings).
                addTransportAddress(address);

        return client;
    }


}
