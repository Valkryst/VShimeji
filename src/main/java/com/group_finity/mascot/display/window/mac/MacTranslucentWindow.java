package com.group_finity.mascot.display.window.mac;

import com.group_finity.mascot.display.window.generic.GenericTranslucentWindow;

import javax.swing.*;

class MacTranslucentWindow extends GenericTranslucentWindow {
    MacTranslucentWindow() {
        final var rootPane = ((JWindow) this.asComponent()).getRootPane();

        // The shadow of the window will shift, so avoid drawing the shadow.
        rootPane.putClientProperty("Window.shadow", Boolean.FALSE);

        // Eliminate warnings at runtime
        rootPane.putClientProperty("apple.awt.draggableWindowBackground", Boolean.TRUE);
    }
}
