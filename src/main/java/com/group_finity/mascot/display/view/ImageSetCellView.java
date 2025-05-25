package com.group_finity.mascot.display.view;

import com.group_finity.mascot.display.controller.ImageSetCellController;
import com.valkryst.JFileLinkLabel.JFileLinkLabel;
import com.valkryst.JImagePanel.JImagePanel;
import com.valkryst.VMVC.view.View;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.Objects;

public class ImageSetCellView extends View<ImageSetCellController> implements MouseInputListener {
    /**
     * <p>Preferred size of the displayed image.</p>
     *
     * <p>This dictates the view's height as the image is the largest element.</p>
     */
    private static final Dimension PREFERRED_IMAGE_SIZE = new Dimension(128, 128);

    /** {@link JLabel} to visually indicate whether this cell is selected. */
    private final JLabel selectionIndicationLabel = new JLabel();

    /**
     * Constructs a new {@link ImageSetCellView}.
     *
     * @param controller Controller for this view.
     */
    public ImageSetCellView(final ImageSetCellController controller) {
        super(controller);

        selectionIndicationLabel.setFont(super.getFont().deriveFont(32f));
        this.setSelected(controller.isSelected());

        this.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new BorderLayout());

        this.add(this.createImagePanel(), BorderLayout.WEST);
        this.add(this.createInformationPanel(), BorderLayout.CENTER);
        this.add(this.createSelectionIndicationPanel(), BorderLayout.EAST);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        super.controller.toggleSelectionState();
    }

    @Override
    public void mousePressed(final MouseEvent e) {}

    @Override
    public void mouseReleased(final MouseEvent e) {}

    @Override
    public void mouseEntered(final MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setBackground(UIManager.getColor("List.selectionBackground"));
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        if (!this.contains(e.getPoint())) {
            this.setCursor(Cursor.getDefaultCursor());
            this.setBackground(UIManager.getColor("Panel.background"));
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {}

    @Override
    public void mouseMoved(final MouseEvent e) {}

    /**
     * Creates a {@link JPanel} containing the image.
     *
     * @return Created {@link JPanel}.
     */
    public JPanel createImagePanel() {
        final var optImage = controller.getImage();

        final JPanel imagePanel;
        if (optImage.isPresent()) {
            imagePanel = new JImagePanel(optImage.get());
        } else {
            imagePanel = new JPanel();
        }
        imagePanel.setBorder(BorderFactory.createEtchedBorder());
        imagePanel.setPreferredSize(PREFERRED_IMAGE_SIZE);

        return imagePanel;
    }

    /**
     * Creates a {@link JPanel} containing the Shimeji's name and related information.
     *
     * @return Created {@link JPanel}.
     */
    public JPanel createInformationPanel() {
        final var panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Caption as a bold header
        final var nameLabel = new JLabel(super.controller.getCaption());
        nameLabel.setFont(nameLabel.getFont().deriveFont(13f));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(8));

        // Add file link panels
        panel.add(this.createFileLinkLabel("Actions", super.controller.getActionsFilePath()));
        panel.add(Box.createVerticalStrut(4));
        panel.add(this.createFileLinkLabel("Behaviours", super.controller.getBehavioursFilePath()));

        return panel;
    }

    /**
     * Creates a {@link JFileLinkLabel} with the specified suffix and path.
     *
     * @param suffix Text to display before the path.
     * @param path Path to display.
     * @return Created {@link JFileLinkLabel}.
     */
    private JFileLinkLabel createFileLinkLabel(final String suffix, final Path path) {
        Objects.requireNonNull(suffix);
        Objects.requireNonNull(path);

        if (suffix.isBlank()) {
            throw new IllegalArgumentException("Suffix cannot be blank.");
        }

        final var linkLabel = new JFileLinkLabel(suffix + ": " + path, path);
        linkLabel.setMaximumSize(linkLabel.getPreferredSize());
        return linkLabel;

    }

    /**
     * Creates a {@link JPanel} with a {@link JLabel} which indicates the selection state.
     *
     * @return Created {@link JPanel}.
     */
    private JPanel createSelectionIndicationPanel() {
        final var panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        panel.add(selectionIndicationLabel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Sets the selection state of this cell.
     *
     * @param value New selection state.
     */
    public void setSelected(final boolean value) {
        selectionIndicationLabel.setText(value ? "✓": "");
    }
}
