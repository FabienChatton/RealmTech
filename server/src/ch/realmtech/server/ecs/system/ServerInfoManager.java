package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.mod.options.server.ServerEphemeralNameOptionEntry;
import ch.realmtech.server.mod.options.server.ServerNameOptionEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// can be merged with runtime info command
public class ServerInfoManager extends Manager {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ServerNameOptionEntry serverName;
    private ServerEphemeralNameOptionEntry serverEphemeralName;

    @Override
    protected void initialize() {
        super.initialize();
        serverName = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), ServerNameOptionEntry.class);
        serverEphemeralName = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), ServerEphemeralNameOptionEntry.class);
    }

    public Map<String, Object> getServerInfoMap() {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("realmtechVersion", ServerContext.REALMTECH_VERSION);
        infoMap.put("serverName", serverName.getValue());
        infoMap.put("serverEphemeralName", serverEphemeralName.getValue());

        return infoMap;
    }

    public String getServerInfoJsonString() {
        return new JSONObject(getServerInfoMap()).toString();
    }
}
