package com.group_finity.mascot.imagesetchooser;

import com.group_finity.mascot.Main;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.config.Entry;
import com.group_finity.mascot.display.model.ImageSetCellModel;
import com.group_finity.mascot.exception.ConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Chooser used to select the Shimeji image sets in use.
 *
 * @author Shimeji-ee Group
 * @since 1.0.2
 */
public class ImageSetChooser extends JDialog {
    private boolean closeProgram = true; // Whether the program closes on dispose

    private final List<ImageSetCellModel> imageSetModels = new ArrayList<>();

    public ImageSetChooser(Frame owner, boolean modal) {
        super(owner, modal);

        List<String> activeImageSets = readConfigFile();

        // Get list of image sets (directories under img)
        FilenameFilter fileFilter = (dir, name) -> {
            if (name.equalsIgnoreCase("unused") || name.startsWith(".")) {
                return false;
            }
            return Files.isDirectory(dir.toPath().resolve(name));
        };

        File dir = Main.IMAGE_DIRECTORY.toFile();
        String[] children = dir.list(fileFilter);

        // Create ImageSetCellViews for ShimejiList
        if (children != null) {
            for (String imageSet : children) {
                // Determine actions file
                Path filePath = Main.CONFIG_DIRECTORY;
                Path actionsFile = filePath.resolve("actions.xml");
                if (Files.exists(filePath.resolve("\u52D5\u4F5C.xml"))) {
                    actionsFile = filePath.resolve("\u52D5\u4F5C.xml");
                }

                filePath = Main.CONFIG_DIRECTORY.resolve(imageSet);
                if (Files.exists(filePath.resolve("actions.xml"))) {
                    actionsFile = filePath.resolve("actions.xml");
                } else if (Files.exists(filePath.resolve("\u52D5\u4F5C.xml"))) {
                    actionsFile = filePath.resolve("\u52D5\u4F5C.xml");
                } else if (Files.exists(filePath.resolve("\u00D5\u00EF\u00F2\u00F5\u00A2\u00A3.xml"))) {
                    actionsFile = filePath.resolve("\u00D5\u00EF\u00F2\u00F5\u00A2\u00A3.xml");
                } else if (Files.exists(filePath.resolve("\u00A6-\u00BA@.xml"))) {
                    actionsFile = filePath.resolve("\u00A6-\u00BA@.xml");
                } else if (Files.exists(filePath.resolve("\u00F4\u00AB\u00EC\u00FD.xml"))) {
                    actionsFile = filePath.resolve("\u00F4\u00AB\u00EC\u00FD.xml");
                } else if (Files.exists(filePath.resolve("one.xml"))) {
                    actionsFile = filePath.resolve("one.xml");
                } else if (Files.exists(filePath.resolve("1.xml"))) {
                    actionsFile = filePath.resolve("1.xml");
                }

                filePath = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve(Main.CONFIG_DIRECTORY);
                if (Files.exists(filePath.resolve("actions.xml"))) {
                    actionsFile = filePath.resolve("actions.xml");
                } else if (Files.exists(filePath.resolve("\u52D5\u4F5C.xml"))) {
                    actionsFile = filePath.resolve("\u52D5\u4F5C.xml");
                } else if (Files.exists(filePath.resolve("\u00D5\u00EF\u00F2\u00F5\u00A2\u00A3.xml"))) {
                    actionsFile = filePath.resolve("\u00D5\u00EF\u00F2\u00F5\u00A2\u00A3.xml");
                } else if (Files.exists(filePath.resolve("\u00A6-\u00BA@.xml"))) {
                    actionsFile = filePath.resolve("\u00A6-\u00BA@.xml");
                } else if (Files.exists(filePath.resolve("\u00F4\u00AB\u00EC\u00FD.xml"))) {
                    actionsFile = filePath.resolve("\u00F4\u00AB\u00EC\u00FD.xml");
                } else if (Files.exists(filePath.resolve("one.xml"))) {
                    actionsFile = filePath.resolve("one.xml");
                } else if (Files.exists(filePath.resolve("1.xml"))) {
                    actionsFile = filePath.resolve("1.xml");
                }

                // Determine behaviours file
                filePath = Main.CONFIG_DIRECTORY;
                Path behaviorsFile = filePath.resolve("behaviors.xml");
                if (Files.exists(filePath.resolve("\u884C\u52D5.xml"))) {
                    behaviorsFile = filePath.resolve("\u884C\u52D5.xml");
                }

                filePath = Main.CONFIG_DIRECTORY.resolve(imageSet);
                if (Files.exists(filePath.resolve("behaviors.xml"))) {
                    behaviorsFile = filePath.resolve("behaviors.xml");
                } else if (Files.exists(filePath.resolve("behavior.xml"))) {
                    behaviorsFile = filePath.resolve("behavior.xml");
                } else if (Files.exists(filePath.resolve("\u884C\u52D5.xml"))) {
                    behaviorsFile = filePath.resolve("\u884C\u52D5.xml");
                } else if (Files.exists(filePath.resolve("\u00DE\u00ED\u00EE\u00D5\u00EF\u00F2.xml"))) {
                    behaviorsFile = filePath.resolve("\u00DE\u00ED\u00EE\u00D5\u00EF\u00F2.xml");
                } else if (Files.exists(filePath.resolve("\u00AA\u00B5\u00A6-.xml"))) {
                    behaviorsFile = filePath.resolve("\u00AA\u00B5\u00A6-.xml");
                } else if (Files.exists(filePath.resolve("\u00ECs\u00F4\u00AB.xml"))) {
                    behaviorsFile = filePath.resolve("\u00ECs\u00F4\u00AB.xml");
                } else if (Files.exists(filePath.resolve("two.xml"))) {
                    behaviorsFile = filePath.resolve("two.xml");
                } else if (Files.exists(filePath.resolve("2.xml"))) {
                    behaviorsFile = filePath.resolve("2.xml");
                }

                filePath = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve(Main.CONFIG_DIRECTORY);
                if (Files.exists(filePath.resolve("behaviors.xml"))) {
                    behaviorsFile = filePath.resolve("behaviors.xml");
                } else if (Files.exists(filePath.resolve("behavior.xml"))) {
                    behaviorsFile = filePath.resolve("behavior.xml");
                } else if (Files.exists(filePath.resolve("\u884C\u52D5.xml"))) {
                    behaviorsFile = filePath.resolve("\u884C\u52D5.xml");
                } else if (Files.exists(filePath.resolve("\u00DE\u00ED\u00EE\u00D5\u00EF\u00F2.xml"))) {
                    behaviorsFile = filePath.resolve("\u00DE\u00ED\u00EE\u00D5\u00EF\u00F2.xml");
                } else if (Files.exists(filePath.resolve("\u00AA\u00B5\u00A6-.xml"))) {
                    behaviorsFile = filePath.resolve("\u00AA\u00B5\u00A6-.xml");
                } else if (Files.exists(filePath.resolve("\u00ECs\u00F4\u00AB.xml"))) {
                    behaviorsFile = filePath.resolve("\u00ECs\u00F4\u00AB.xml");
                } else if (Files.exists(filePath.resolve("two.xml"))) {
                    behaviorsFile = filePath.resolve("two.xml");
                } else if (Files.exists(filePath.resolve("2.xml"))) {
                    behaviorsFile = filePath.resolve("2.xml");
                }

                // Determine information file
                filePath = Main.CONFIG_DIRECTORY;
                Path infoFile = filePath.resolve("info.xml");

                filePath = Main.CONFIG_DIRECTORY.resolve(imageSet);
                if (Files.exists(filePath.resolve("info.xml"))) {
                    infoFile = filePath.resolve("info.xml");
                }

                filePath = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve(Main.CONFIG_DIRECTORY);
                if (Files.exists(filePath.resolve("info.xml"))) {
                    infoFile = filePath.resolve("info.xml");
                }

                Path imageFile = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve("shime1.png");
                String caption = imageSet;
                try {
                    if (Files.exists(infoFile)) {
                        Configuration configuration = new Configuration();

                        final Document information = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Files.newInputStream(infoFile));

                        configuration.load(new Entry(information.getDocumentElement()), imageSet);

                        if (configuration.containsInformationKey(configuration.getSchema().getString("Name"))) {
                            caption = configuration.getInformation(configuration.getSchema().getString("Name"));
                        }
                        if (configuration.containsInformationKey(configuration.getSchema().getString("PreviewImage"))) {
                            imageFile = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve(configuration.getInformation(configuration.getSchema().getString("PreviewImage")));
                        }
                    }

                } catch (ConfigurationException | ParserConfigurationException | IOException | SAXException ex) {
                    imageFile = Main.IMAGE_DIRECTORY.resolve(imageSet).resolve("shime1.png");
                    caption = imageSet;
                }

                final var model = new ImageSetCellModel(
                    imageSet,
                    actionsFile,
                    behaviorsFile,
                    caption,
                    imageFile
                );
                model.setSelected(activeImageSets.contains(imageSet));
                imageSetModels.add(model);
            }
        }

        this.initComponents();
        this.setLocationRelativeTo(null);
    }

    public List<String> display() {
        setTitle(Main.getInstance().getLanguageBundle().getString("ShimejiImageSetChooser"));
        jLabel1.setText(Main.getInstance().getLanguageBundle().getString("SelectImageSetsToUse"));
        setVisible(true);
        if (closeProgram) {
            return null;
        }

        return this.getSelectedImageSets();
    }

    private List<String> readConfigFile() {
        return new ArrayList<>(Arrays.asList(Main.getInstance().getProperties().getProperty("ActiveShimeji", "").split("/")));
    }

    private void updateConfigFile() {
        final var main = Main.getInstance();
        main.getProperties().setProperty("ActiveShimeji", this.getSelectedImageSets().toString().replace("[", "").replace("]", "").replace(", ", "/"));
        main.updateConfigFile();
    }

    private void initComponents() {
        jLabel1 = new JLabel();
        final JPanel jPanel1 = new JPanel();
        final JPanel jPanel4 = new JPanel();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Shimeji-ee Image Set Chooser");
        setMinimumSize(new Dimension(670, 495));

        final var imageSetPanel = new JPanel();
        imageSetPanel.setLayout(new BoxLayout(imageSetPanel, BoxLayout.Y_AXIS));
        for (final var model : imageSetModels) {
            imageSetPanel.add(model.createView());
        }

        final JScrollPane jScrollPane1 = new JScrollPane(imageSetPanel);
        jScrollPane1.setDoubleBuffered(true);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(8);

        jLabel1.setText("Select Image Sets to Use:");

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(this.createSelectionButtonPanel(), BorderLayout.WEST);
        jPanel1.add(this.createConfirmationButtonPanel(), BorderLayout.EAST);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 384, Short.MAX_VALUE)
                                                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel1)
                                        .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)));

        pack();
    }

    private JPanel createSelectionButtonPanel() {
        final var selectAllButton = new JButton(Main.getInstance().getLanguageBundle().getString("SelectAll"));
        selectAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (ImageSetCellModel model : imageSetModels) {
                    model.setSelected(true);
                }
            }
        });

        final var clearAllButton = new JButton(Main.getInstance().getLanguageBundle().getString("ClearAll"));
        clearAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (ImageSetCellModel model : imageSetModels) {
                    model.setSelected(false);
                }
            }
        });

        final var panel = new JPanel();
        panel.add(selectAllButton);
        panel.add(clearAllButton);
        return panel;
    }

    private JPanel createConfirmationButtonPanel() {
        final var useSelectedButton = new JButton(Main.getInstance().getLanguageBundle().getString("UseSelected"));
        useSelectedButton.addActionListener(this::useSelectedButtonActionPerformed);

        final var useAllButton = new JButton(Main.getInstance().getLanguageBundle().getString("UseAll"));
        useAllButton.addActionListener(this::useAllButtonActionPerformed);

        final var cancelButton = new JButton(Main.getInstance().getLanguageBundle().getString("Cancel"));
        cancelButton.addActionListener(e -> this.dispose());

        final var panel = new JPanel();
        panel.add(useSelectedButton);
        panel.add(useAllButton);
        panel.add(cancelButton);
        return panel;
    }

    private void useSelectedButtonActionPerformed(ActionEvent evt) {
        updateConfigFile();
        closeProgram = false;
        dispose();
    }

    private void useAllButtonActionPerformed(ActionEvent evt) {
        updateConfigFile();
        closeProgram = false;
        dispose();
    }

    public List<String> getSelectedImageSets() {
        return imageSetModels.stream().filter(ImageSetCellModel::isSelected).map(ImageSetCellModel::getImageSet).collect(Collectors.toList());
    }

    private JLabel jLabel1;
}
