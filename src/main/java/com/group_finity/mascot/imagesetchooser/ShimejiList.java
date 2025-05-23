package com.group_finity.mascot.imagesetchooser;

import com.group_finity.mascot.display.model.ImageSetCellModel;
import com.group_finity.mascot.display.view.ImageSetCellView;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link JList} that can be populated with {@link ImageSetCellModel} objects.
 *
 * @author Shimeji-ee Group
 * @since 1.0.2
 */
public class ShimejiList extends JList<ImageSetCellModel> {

    public ShimejiList() {
        setCellRenderer(new CustomCellRenderer());
    }

    static class CustomCellRenderer implements ListCellRenderer<ImageSetCellModel> {
        private final Map<ImageSetCellModel, ImageSetCellView> viewCache = new HashMap<>();

        @Override
        public Component getListCellRendererComponent(
            final JList<? extends ImageSetCellModel> list,
            final ImageSetCellModel value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus
        ) {
            final var view = viewCache.computeIfAbsent(value, ImageSetCellModel::createView);
            view.setSelected(isSelected);
            return view;
        }
    }
}