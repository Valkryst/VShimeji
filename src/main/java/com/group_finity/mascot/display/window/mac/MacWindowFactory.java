package com.group_finity.mascot.display.window.mac;

import com.group_finity.mascot.display.window.WindowFactory;
import com.group_finity.mascot.display.window.generic.GenericWindowFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;

import java.awt.image.BufferedImage;

/**
 * @author nonowarn
 */
public class MacWindowFactory extends WindowFactory {
    private final WindowFactory delegate = new GenericWindowFactory();
    private final Environment environment = new MacEnvironment();

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public NativeImage newNativeImage(final BufferedImage src) {
        return delegate.newNativeImage(src);
    }

    @Override
    public TranslucentWindow newTransparentWindow() {
        return new MacTranslucentWindow(delegate);
    }
}
