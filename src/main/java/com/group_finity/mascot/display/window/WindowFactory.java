package com.group_finity.mascot.display.window;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.display.window.mac.MacWindowFactory;
import com.group_finity.mascot.display.window.virtual.VirtualWindowFactory;
import com.group_finity.mascot.display.window.win.WindowsWindowFactory;
import com.group_finity.mascot.display.window.x11.X11WindowFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;
import com.sun.jna.Platform;
import lombok.Getter;

import java.awt.image.BufferedImage;

/**
 * Provides access to the native environment.
 * {@link #getInstance()} returns an instance of a Windows, Mac, Linux (X11), or general-purpose subclass depending on the execution environment.
 *
 * @author Yuki Yamada
 */
public abstract class WindowFactory {
    /** An instance of the subclass, according to the execution environment. */
    @Getter private static WindowFactory instance;

    static {
        resetInstance();
    }

    /**
     * Creates an instance of the subclass.
     */
    public static void resetInstance() {
        String environment = Main.getInstance().getProperties().getProperty("Environment", "generic");

        if (environment.equals("generic")) {
            if (Platform.isWindows()) {
                instance = new WindowsWindowFactory();
            } else if (Platform.isMac()) {
                instance = new MacWindowFactory();
            } else if (/* Platform.isLinux() */ Platform.isX11()) {
                // Because Linux uses X11, this functions as the Linux support.
                instance = new X11WindowFactory();
            }
        } else if (environment.equals("virtual")) {
            instance = new VirtualWindowFactory();
        }
    }

    /**
     * Gets the {@link Environment} object.
     *
     * @return the {@link Environment} object
     */
    public abstract Environment getEnvironment();

    /**
     * Creates a {@link NativeImage} with the specified {@link BufferedImage}.
     * This image can be used for masking {@link TranslucentWindow}.
     *
     * @param src the image to use to create the {@link NativeImage}
     * @return the new native image
     */
    public abstract NativeImage newNativeImage(BufferedImage src);

    /**
     * Creates a window that can be displayed semi-transparently.
     *
     * @return the new window
     */
    public abstract TranslucentWindow newTransparentWindow();
}
