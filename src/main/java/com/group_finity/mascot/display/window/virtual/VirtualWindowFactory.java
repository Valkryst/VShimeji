package com.group_finity.mascot.display.window.virtual;

import com.group_finity.mascot.display.window.WindowFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;

import java.awt.image.BufferedImage;

/**
 * Virtual desktop factory.
 *
 * @author Kilkakon
 * @since 1.0.20
 */
public class VirtualWindowFactory extends WindowFactory {
    private final VirtualEnvironment environment = new VirtualEnvironment();

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public NativeImage newNativeImage(final BufferedImage src) {
        return new VirtualNativeImage(src);
    }

    @Override
    public TranslucentWindow newTransparentWindow() {
        VirtualTranslucentPanel panel = new VirtualTranslucentPanel();
        environment.addShimeji(panel);
        return panel;
    }
}
