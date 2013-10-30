package lukasync.job;

import lukasync.client.MagentoClient;
import lukasync.client.ServiceClient;
import lukasync.client.ZurmoClient;

import org.json.JSONObject;

public class ZurmoToMagentoJob extends Job<ZurmoClient, ServiceClient> {

    public ZurmoToMagentoJob(ZurmoClient source, MagentoClient destination, JSONObject jobMeta) {
        super(source, destination, jobMeta);
    }

    @Override
    public JSONObject execute() {
        // TODO Auto-generated method stub
        return null;
    }
}