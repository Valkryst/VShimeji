package com.group_finity.mascot.display.window.win;

import com.group_finity.mascot.image.NativeImage;
import com.group_finity.mascot.image.TranslucentWindow;

import javax.swing.*;
import java.awt.*;

/**
 * Image window with alpha value.
 * {@link WindowsNativeImage} set with {@link #setImage(NativeImage)} can be displayed on the desktop.
 *
 * @author Yuki Yamada of <a href="http://www.group-finity.com/Shimeji/">Group Finity</a>
 * @author Shimeji-ee Group
 * @author Valkryst
 */
class WindowsTranslucentWindow extends JWindow implements TranslucentWindow {
    /**
     * Image to display.
     */
    private WindowsNativeImage image;

    public WindowsTranslucentWindow() {
        super();

        setBackground(new Color(0, 0, 0, 0));
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public String toString() {
        return "LayeredWindow[hashCode=" + hashCode() + ",bounds=" + getBounds() + "]";
    }

    @Override
    public void paint(final Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;

            // Higher-quality image
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        super.paint(g);

        if (image != null) {
            g.drawImage(image.getManagedImage(), 0, 0, null);
        }
    }

    @Override
    public void setImage(final NativeImage image) {
        this.image = (WindowsNativeImage) image;
    }

    @Override
    public void updateImage() {
        repaint();
    }
}