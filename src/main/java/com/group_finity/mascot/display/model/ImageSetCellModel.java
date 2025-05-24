package com.group_finity.mascot.display.model;

import com.group_finity.mascot.display.controller.ImageSetCellController;
import com.group_finity.mascot.display.view.ImageSetCellView;
import com.valkryst.VMVC.model.Model;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Getter
@Log
public class ImageSetCellModel extends Model<ImageSetCellController, ImageSetCellView> {
    private final String imageSet;
    private final Path actionsFilePath;
    private final Path behavioursFilePath;
    private final String caption;
    private final Path imagePath;

    /** Whether this cell is selected. */
    @Setter private boolean isSelected = false;

    public ImageSetCellModel(
        final @NonNull String imageSet,
        final @NonNull Path actionsFilePath,
        final @NonNull Path behavioursFilePath,
        final @NonNull String caption,
        final @NonNull Path imagePath
    ) {
        this.imageSet = imageSet;
        this.actionsFilePath = actionsFilePath;
        this.behavioursFilePath = behavioursFilePath;
        this.caption = caption;
        this.imagePath = imagePath;
    }

    @Override
    protected ImageSetCellController createController() {
        return new ImageSetCellController(this);
    }

    @Override
    protected ImageSetCellView createView(final @NonNull ImageSetCellController controller) {
        return new ImageSetCellView(controller);
    }

    /**
     * Attempts to load a {@link BufferedImage} from the {@link #imagePath}.
     *
     * @return An {@link Optional} containing the loaded image, if successful.
     */
    public Optional<BufferedImage> getImage() {
        try {
            return Optional.ofNullable(ImageIO.read(this.imagePath.toFile()));
        } catch (final IOException e) {
            log.warning("Failed to load image from '" + this.imagePath + "': " + e.getMessage());
            return Optional.empty();
        }
    }
}
