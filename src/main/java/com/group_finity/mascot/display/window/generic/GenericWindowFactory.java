package com.group_finity.mascot.display.window.generic;

import com.group_finity.mascot.display.window.WindowFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;

import java.awt.image.BufferedImage;

/**
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 */
public class GenericWindowFactory extends WindowFactory {
    private final Environment environment = new GenericEnvironment();

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public NativeImage newNativeImage(final BufferedImage src) {
        return new GenericNativeImage(src);
    }

    @Override
    public TranslucentWindow newTransparentWindow() {
        return new GenericTranslucentWindow();
    }
}
