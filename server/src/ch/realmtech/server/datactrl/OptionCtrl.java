package ch.realmtech.server.datactrl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public abstract class OptionCtrl {
    public abstract Optional<String> getOptionValue(String optionName);
    public abstract Map<String, String> listOptions();
    protected void putListOptions(Map<String, String> map, String optionName) {
        map.put(optionName, getOptionValue(optionName).orElse("Can not get \"" + optionName + "\""));
    }
    public abstract void save() throws IOException;

    public abstract void setOptionValue(String optionName, String optionValue);
}
