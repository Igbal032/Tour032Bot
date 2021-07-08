package az.code.turalbot.cache;

import az.code.turalbot.models.Action;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache{
    private Map<String, Action> actionCache = new HashMap<>();
    public Action getActionCache(String UUID){
        Action action = actionCache.get(UUID);
        return action;
    }
    public Action setActionCache(String UUID, Action action){
        actionCache.put(UUID,action);
        return getActionCache(UUID);
    }
}
