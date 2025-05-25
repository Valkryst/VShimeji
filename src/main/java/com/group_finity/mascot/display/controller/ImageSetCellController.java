package com.group_finity.mascot.display.controller;

import com.group_finity.mascot.display.model.ImageSetCellModel;
import com.valkryst.VMVC.controller.Controller;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Optional;

public class ImageSetCellController extends Controller<ImageSetCellModel> {
    /**
     * Constructs a new {@link MascotInfoController}.
     *
     * @param model Model for this controller.
     */
    public ImageSetCellController(final ImageSetCellModel model) {
        super(model);
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#isSelected()}.
     *
     * @return Result of {@link ImageSetCellModel#isSelected()}.
     */
    public boolean isSelected() {
        return super.model.isSelected();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getImageSet()}.
     *
     * @return Result of {@link ImageSetCellModel#getImageSet()}.
     */
    public String getImageSet() {
        return super.model.getImageSet();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getActionsFilePath()}.
     *
     * @return Result of {@link ImageSetCellModel#getActionsFilePath()}.
     */
    public Path getActionsFilePath() {
        return super.model.getActionsFilePath();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getBehavioursFilePath()}.
     *
     * @return Result of {@link ImageSetCellModel#getBehavioursFilePath()}.
     */
    public Path getBehavioursFilePath() {
        return super.model.getBehavioursFilePath();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getCaption()}.
     *
     * @return Result of {@link ImageSetCellModel#getCaption()}.
     */
    public String getCaption() {
        return super.model.getCaption();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getImage()}.
     *
     * @return Result of {@link ImageSetCellModel#getImage()}.
     */
    public Optional<BufferedImage> getImage() {
        return super.model.getImage();
    }

    /** Toggles the selection state of this cell. */
    public void toggleSelectionState() {
        super.model.setSelected(!super.model.isSelected());
    }
}
