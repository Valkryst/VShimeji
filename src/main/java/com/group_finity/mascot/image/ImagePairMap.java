package com.group_finity.mascot.image;

import lombok.Getter;
import lombok.NonNull;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class ImagePairMap extends ConcurrentHashMap<String, ImagePair> {
    /** The singleton instance. */
    @Getter private static final ImagePairMap instance = new ImagePairMap();

    /** Private constructor to prevent instantiation. */
    private ImagePairMap() {}

    /**
     * Removes all entries where the key's parent directory matches the specified search term.
     *
     * @param searchTerm Directory path to search for in the keys.
     */
    public void removeAll(final String searchTerm) {
        if (this.isEmpty()) {
            return;
        }

        this.keySet().removeIf(key -> searchTerm.equals(Paths.get(key).getParent().toString()));
    }

    /**
     * Retrieves a {@link MascotImage} from an {@link ImagePair}, if it exists within this map.
     *
     * @param filename Filename of the image pair.
     * @param isLookRight Whether to retrieve the right-facing image.
     * @return The {@link MascotImage} corresponding to the filename and direction, or null if not found.
     */
    public MascotImage getImage(final @NonNull String filename, final boolean isLookRight) {
        if (!this.containsKey(filename)) {
            return null;
        }

        return this.get(filename).getImage(isLookRight);
    }
}
