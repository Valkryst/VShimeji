package com.group_finity.mascot.display.view;

import com.group_finity.mascot.display.controller.ImageSetCellController;
import com.valkryst.JIconLabel.JIconLabel;
import com.valkryst.VMVC.view.View;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

import javax.swing.*;
import java.awt.*;

public class ImageSetCellView extends View<ImageSetCellController> {
    /** The preferred size of this view. */
    private static final Dimension PREFERRED_SIZE = new Dimension(248, 80);

    /** Checkbox for keeping track of the selected state of this view. */
    private final JCheckBox checkBox = new JCheckBox();

    /**
     * Constructs a new {@link ImageSetCellView}.
     *
     * @param controller Controller for this view.
     */
    public ImageSetCellView(final ImageSetCellController controller) {
        super(controller);

        this.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new AbsoluteLayout());
        this.setPreferredSize(PREFERRED_SIZE);

        this.add(checkBox, new AbsoluteConstraints(10, 30, -1, -1));
        this.add(new JLabel(controller.getCaption()), new AbsoluteConstraints(110, 10, -1, -1));
        this.add(new JLabel(controller.getActionsFilePath()), new AbsoluteConstraints(110, 30, -1, -1));
        this.add(new JLabel(controller.getBehavioursFilePath()), new AbsoluteConstraints(110, 50, -1, -1));

        final var image = controller.getImage();
        final JLabel imageLabel;
        if (image.isPresent()) {
            imageLabel = new JIconLabel(new ImageIcon(image.get()));
        } else {
            imageLabel = new JLabel();
        }
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.add(imageLabel, new AbsoluteConstraints(40, 10, 60, 60));
    }

    /**
     * Sets the selected state of the {@link #checkBox}.
     *
     * @param value New selected state.
     */
    public void setSelected(final boolean value) {
        checkBox.setSelected(value);
    }
}
