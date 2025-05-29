package com.group_finity.mascot.display.window.mac;

import com.group_finity.mascot.display.window.generic.GenericWindowFactory;
import com.group_finity.mascot.environment.Environment;

/**
 * @author nonowarn
 * @author Valkryst
 */
public class MacWindowFactory extends GenericWindowFactory {
    private final Environment environment = new MacEnvironment();

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}
