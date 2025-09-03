package com.group_finity.mascot.display.window;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.display.window.mac.MacWindowFactory;
import com.group_finity.mascot.display.window.virtual.VirtualWindowFactory;
import com.group_finity.mascot.display.window.win.WindowsWindowFactory;
import com.group_finity.mascot.display.window.x11.X11WindowFactory;
import com.group_finity.mascot.environment.Environment;
import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;
import lombok.Getter;
import lombok.extern.java.Log;

import java.awt.image.BufferedImage;

/**
 * Provides access to the native environment.
 * {@link #getInstance()} returns an instance of a Windows, Mac, Linux (X11), or general-purpose subclass depending on the execution environment.
 *
 * @author Yuki Yamada
 */
@Log
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

        System.out.println(WindowFactory.isX11Available());
        if (environment.equals("generic")) {
            if (Platform.isWindows()) {
                instance = new WindowsWindowFactory();
            } else if (Platform.isMac()) {
                instance = new MacWindowFactory();
            } else if (WindowFactory.isX11Available()) {
                instance = new X11WindowFactory();
            } else {
                log.warning("The detected operating system is not Mac or Windows, and X11 is not available. Defaulting to a virtual window. Please install X11, if possible, and relaunch the application.");
                instance = new VirtualWindowFactory();
            }
        } else if (environment.equals("virtual")) {
            instance = new VirtualWindowFactory();
        }
    }

    /**
     * Determines whether X11 is available.
     *
     * @return Whether X11 is available
     */
    private static boolean isX11Available() {
        if (!Platform.isX11()) {
            return false;
        }

        try {
            NativeLibrary.getInstance("X11");
            return true;
        } catch (final Exception | UnsatisfiedLinkError e) {
            return false;
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
