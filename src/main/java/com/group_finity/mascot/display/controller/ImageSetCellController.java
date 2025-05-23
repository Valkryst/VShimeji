package com.group_finity.mascot.display.controller;

import com.group_finity.mascot.display.model.ImageSetCellModel;
import com.valkryst.VMVC.controller.Controller;

import java.awt.image.BufferedImage;
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
     * Retrieves the result of {@link ImageSetCellModel#getImageSet()}.
     *
     * @return Result of {@link ImageSetCellModel#getImageSet()}.
     */
    public String getImageSet() {
        return super.model.getImageSet();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getActionsFilePath()}, cast to a {@link String}.
     *
     * @return Result of {@link ImageSetCellModel#getActionsFilePath()}, cast to a {@link String}.
     */
    public String getActionsFilePath() {
        return super.model.getActionsFilePath().toString();
    }

    /**
     * Retrieves the result of {@link ImageSetCellModel#getBehavioursFilePath()}, cast to a {@link String}.
     *
     * @return Result of {@link ImageSetCellModel#getBehavioursFilePath()}, cast to a {@link String}.
     */
    public String getBehavioursFilePath() {
        return super.model.getBehavioursFilePath().toString();
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
}
