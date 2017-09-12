
import javax.swing.*;
import squint.SImage;
import java.awt.BorderLayout;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Use a simple windowed program (Fauxtoshop) to allow a user to read and
 * display a PPM image, make minor adjustments to the image, and write the image
 * to a new PPM file.
 *
 * @author Tianchang Yang
 * @version 06 April 2016
 */
public class Fauxtoshop extends WindowManager {

    private PPMImage theImage;
    private PPMImage tempImage;
    private PPMImage backUpImage;

    private JLabel imageLabel;
    private JMenuBar myMenuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu toolsMenu;
    private JMenuItem loadMenuItem;
    private JMenuItem revertMenuItem;
    private JMenuItem saveAsMenuItem;
    private JMenuItem quitMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem invertMenuItem;
    private JMenuItem grayscaleMenuItem;
    private JMenuItem doubleSizeMenuItem;
    private JMenuItem halveSizeMenuItem;
    private JMenuItem adjustBrightnessMenuItem;
    private JMenuItem adjustContrastMenuItem;
    private JMenuItem changeLevelsMenuItem;
    private JMenuItem chromaKeyMenuItem;
    private JMenuItem blurMenuItem;
    private JMenuItem cropMenuItem;
    private JMenuItem flipHorizontalMenuItem;
    private JMenuItem flipVerticalMenuItem;

    private JLabel leftSliderLabel;
    private JSlider theSlider;
    private JLabel rightSliderLabel;
    private JButton acceptButton;
    private JButton cancelButton;
    private JPanel bottomPanel;

    String filename;

    boolean save = true;

    private JFileChooser theFileChooser;   // used to make file open/save easier

    /**
     * Fauxtoshop constructor - construct the label for displaying the image and
     * buttons
     */
    public Fauxtoshop() {
        // call the parent class (WindowManager) constructor
        super("Fauxtoshop", 400, 400);

        // tell the window manager how we want the windows managed
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        super.setAlwaysOnTop(true);

        // construct a label to hold the image (SImage)
        imageLabel = new JLabel("", SwingConstants.CENTER);

        //construct the File menu
        myMenuBar = new JMenuBar(); // the empty menu bar
        fileMenu = new JMenu("File"); // the File menu
        myMenuBar.add(fileMenu);
        loadMenuItem = new JMenuItem("Load…"); // the 1st menu item
        fileMenu.add(loadMenuItem);
        loadMenuItem.setActionCommand("load");
        loadMenuItem.addActionListener(this);
        revertMenuItem = new JMenuItem("Revert"); // the 2nd menu item
        fileMenu.add(revertMenuItem);
        revertMenuItem.setActionCommand("revert");
        revertMenuItem.addActionListener(this);
        saveAsMenuItem = new JMenuItem("Save As…"); // the 3rd menu item
        fileMenu.add(saveAsMenuItem);
        saveAsMenuItem.setActionCommand("saveAs");
        saveAsMenuItem.addActionListener(this);
        fileMenu.addSeparator(); // add a thin line between menu items
        quitMenuItem = new JMenuItem("Quit"); // the 4th menu item
        fileMenu.add(quitMenuItem);
        quitMenuItem.setActionCommand("quit");
        quitMenuItem.addActionListener(this);

        //construct the Edit menu
        editMenu = new JMenu("Edit"); // the Undo menu
        myMenuBar.add(editMenu);
        undoMenuItem = new JMenuItem("Undo"); // the 1st menu item
        editMenu.add(undoMenuItem);
        undoMenuItem.setActionCommand("undo");
        undoMenuItem.addActionListener(this);

        //construct the Tools menu
        toolsMenu = new JMenu("Tools"); // the Tools menu
        myMenuBar.add(toolsMenu);
        invertMenuItem = new JMenuItem("Invert"); // the 1st menu item
        toolsMenu.add(invertMenuItem);
        invertMenuItem.setActionCommand("invert");
        invertMenuItem.addActionListener(this);
        grayscaleMenuItem = new JMenuItem("Grayscale"); // the 2nd menu item
        toolsMenu.add(grayscaleMenuItem);
        grayscaleMenuItem.setActionCommand("grayscale");
        grayscaleMenuItem.addActionListener(this);
        doubleSizeMenuItem = new JMenuItem("Double size"); // the 3rd menu item
        toolsMenu.add(doubleSizeMenuItem);
        doubleSizeMenuItem.setActionCommand("doubleSize");
        doubleSizeMenuItem.addActionListener(this);
        halveSizeMenuItem = new JMenuItem("Halve size"); // the 4th menu item
        toolsMenu.add(halveSizeMenuItem);
        halveSizeMenuItem.setActionCommand("halveSize");
        halveSizeMenuItem.addActionListener(this);
        adjustBrightnessMenuItem = new JMenuItem("Adjust brightness..."); // the 5th menu item
        toolsMenu.add(adjustBrightnessMenuItem);
        adjustBrightnessMenuItem.setActionCommand("adjustBrightness");
        adjustBrightnessMenuItem.addActionListener(this);
        adjustContrastMenuItem = new JMenuItem("Adjust contrast..."); // the 6th menu item
        toolsMenu.add(adjustContrastMenuItem);
        adjustContrastMenuItem.setActionCommand("adjustContrast");
        adjustContrastMenuItem.addActionListener(this);
        changeLevelsMenuItem = new JMenuItem("Change levels..."); // the 7th menu item
        toolsMenu.add(changeLevelsMenuItem);
        changeLevelsMenuItem.setActionCommand("changeLevels");
        changeLevelsMenuItem.addActionListener(this);
        chromaKeyMenuItem = new JMenuItem("Chroma Key..."); // the 8th menu item
        toolsMenu.add(chromaKeyMenuItem);
        chromaKeyMenuItem.setActionCommand("chromaKey");
        chromaKeyMenuItem.addActionListener(this);
        blurMenuItem = new JMenuItem("Blur..."); // the 9th menu item
        toolsMenu.add(blurMenuItem);
        blurMenuItem.setActionCommand("blur");
        blurMenuItem.addActionListener(this);
        cropMenuItem = new JMenuItem("Crop..."); // the 10th menu item
        toolsMenu.add(cropMenuItem);
        cropMenuItem.setActionCommand("crop");
        cropMenuItem.addActionListener(this);
        flipHorizontalMenuItem = new JMenuItem("Flip horizontal"); // the 11th menu item
        toolsMenu.add(flipHorizontalMenuItem);
        flipHorizontalMenuItem.setActionCommand("flipHorizontal");
        flipHorizontalMenuItem.addActionListener(this);
        flipVerticalMenuItem = new JMenuItem("Flip vertical"); // the 10th menu item
        toolsMenu.add(flipVerticalMenuItem);
        flipVerticalMenuItem.setActionCommand("flipVertical");
        flipVerticalMenuItem.addActionListener(this);

        revertMenuItem.setEnabled(false); // disable Revert menu item
        saveAsMenuItem.setEnabled(false); // disable Save As... menu item
        editMenu.setEnabled(false); // disable Undo menu item
        toolsMenu.setEnabled(false); // disable Tools menu

        // create a new panel and put the slider and buttons in it
        bottomPanel = new JPanel();
        leftSliderLabel = new JLabel("");
        bottomPanel.add(leftSliderLabel);
        theSlider = new JSlider();
        bottomPanel.add(theSlider);
        theSlider.addChangeListener(this);
        rightSliderLabel = new JLabel("");
        bottomPanel.add(rightSliderLabel);
        acceptButton = new JButton("accept");
        acceptButton.addActionListener(this);
        bottomPanel.add(acceptButton);
        cancelButton = new JButton("cancel");
        cancelButton.addActionListener(this);
        bottomPanel.add(cancelButton);

        bottomPanel.setVisible(false);

        // set the layout of this window, and add in the image (in a scrolling pane) and the button pane
        this.setLayout(new BorderLayout());
        this.add(myMenuBar, BorderLayout.NORTH);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        // construct a new instance of JFileChooser to be used in methods below
        theFileChooser = new JFileChooser(new File("."));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("PPM files", "ppm");
        theFileChooser.setFileFilter(filter);

        JOptionPane.showMessageDialog(imageLabel, "Fauxtoshop\nTiancang Yang, April 2016");
    }

    /**
     * selectionMade - Set the button action
     *
     * @param whichButton the button clicked
     */
    @Override // Indicates that this method is declared elsewhere (in WindowManager.java) but overridden here.
    public void selectionMade(JMenuItem whichItem) {
        String action = whichItem.getActionCommand();

        // identify the menu selection action string
        if (action.equals(loadMenuItem.getActionCommand())) {
            doLoadMenuItem();
        } else if (action.equals(revertMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doRevertMenuItem();
        } else if (action.equals(undoMenuItem.getActionCommand())) {
            doUndoMenuItem();
        } else if (action.equals(saveAsMenuItem.getActionCommand())) {
            doSaveAsMenuItem();
        } else if (action.equals(invertMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doInvertMenuItem();
        } else if (action.equals(quitMenuItem.getActionCommand())) {
            doQuitMenuItem();
        } else if (action.equals(grayscaleMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doGrayscaleMenuItem();
        } else if (action.equals(doubleSizeMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doDoubleSizeMenuItem();
        } else if (action.equals(halveSizeMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doHalveSizeMenuItem();
        } else if (action.equals(adjustBrightnessMenuItem.getActionCommand())) {
            leftSliderLabel.setText("Brightness:");
            theSlider.setMinimum(-255);
            theSlider.setMaximum(255);
            theSlider.setValue(0);
            rightSliderLabel.setText("0");
            bottomPanel.setVisible(true);
            fileMenu.setEnabled(false);
            editMenu.setEnabled(false);
            toolsMenu.setEnabled(false);
        } else if (action.equals(adjustContrastMenuItem.getActionCommand())) {
            leftSliderLabel.setText("Contrast:");
            theSlider.setMinimum(-255);
            theSlider.setMaximum(255);
            theSlider.setValue(0);
            rightSliderLabel.setText("0");
            bottomPanel.setVisible(true);
            fileMenu.setEnabled(false);
            editMenu.setEnabled(false);
            toolsMenu.setEnabled(false);
        } else if (action.equals(changeLevelsMenuItem.getActionCommand())) {
            leftSliderLabel.setText("Levels:");
            theSlider.setMinimum(2);
            theSlider.setMaximum(256);
            theSlider.setValue(256);
            rightSliderLabel.setText("256");
            bottomPanel.setVisible(true);
            fileMenu.setEnabled(false);
            editMenu.setEnabled(false);
            toolsMenu.setEnabled(false);
        } else if (action.equals(chromaKeyMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doChromaKeyMenuItem();
        } else if (action.equals(blurMenuItem.getActionCommand())) {
            leftSliderLabel.setText("Blur:");
            theSlider.setMinimum(0);
            theSlider.setMaximum(10);
            theSlider.setValue(0);
            rightSliderLabel.setText("0");
            bottomPanel.setVisible(true);
            fileMenu.setEnabled(false);
            editMenu.setEnabled(false);
            toolsMenu.setEnabled(false);
        } else if (action.equals(cropMenuItem.getActionCommand())) {
            leftSliderLabel.setText("Crop:");
            theSlider.setMinimum(1);
            theSlider.setMaximum(100);
            theSlider.setValue(100);
            rightSliderLabel.setText("100");
            bottomPanel.setVisible(true);
            fileMenu.setEnabled(false);
            editMenu.setEnabled(false);
            toolsMenu.setEnabled(false);
        } else if (action.equals(flipHorizontalMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doFlipHorizontalMenuItem();
        } else if (action.equals(flipVerticalMenuItem.getActionCommand())) {
            save = false;
            undoMenuItem.setEnabled(true);
            backUpImage = new PPMImage(theImage);
            doFlipVerticalMenuItem();
        }
    }

    /**
     * buttonClicked - Set the button action
     *
     * @param whichButton the button clicked
     */
    @Override // Indicates that this method is declared elsewhere (in WindowManager.java) but overridden here.
    public void buttonClicked(JButton whichButton) {
        String action = whichButton.getActionCommand();
        // identify the clicked button's action string
        if (action.equals(acceptButton.getActionCommand())) {
            // user pressed accept button
            doAcceptButton();
        } else if (action.equals(cancelButton.getActionCommand())) {
            // user pressed cancel button 
            doCancelButton();
        }
    }

    /**
     * sliderChanged - Set the slider action
     *
     * @param theSlider the slider value
     */
    @Override // Indicates that this method is declared elsewhere (in WindowManager.java) but overridden here.
    public void sliderChanged(JSlider whichSlider) {
        int amount = whichSlider.getValue();
        rightSliderLabel.setText(Integer.toString(amount));
        if (leftSliderLabel.getText().equals("Brightness:")) {
            doAdjustBrightnessMenuItem(amount);
        } else if (leftSliderLabel.getText().equals("Contrast:")) {
            doAdjustContrastMenuItem(amount);
        } else if (leftSliderLabel.getText().equals("Levels:")) {
            doChangeLevelsMenuItem(amount);
        } else if (leftSliderLabel.getText().equals("Blur:")) {
            doBlurMenuItem(amount);
        } else if (leftSliderLabel.getText().equals("Crop:")) {
            doCropMenuItem(amount);
        }
    }

    /**
     * doAcceptButton - when user presses the accept button in the slider
     */
    private void doAcceptButton() {
        save = false;
        undoMenuItem.setEnabled(true);
        backUpImage = new PPMImage(theImage);
        theImage = new PPMImage(tempImage);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
        int height = theImage.getNumRows() + 100;
        int width = theImage.getNumCols() + 20;
        if (width < 500) {
            width = 500;  // minimum width needed to see the buttons
        }
        if (height < 200) {
            height = 200; // minimum height
        }
        super.setSize(width, height);
        bottomPanel.setVisible(false);
        fileMenu.setEnabled(true);
        editMenu.setEnabled(true);
        toolsMenu.setEnabled(true);
    }

    /**
     * doCancelButton - when user presses the cancel button in the slider
     */
    private void doCancelButton() {
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
        bottomPanel.setVisible(false);
        fileMenu.setEnabled(true);
        editMenu.setEnabled(true);
        toolsMenu.setEnabled(true);
    }

    /**
     * doLoadMenuItem - Load the wanted image and display on the label
     */
    private void doLoadMenuItem() {

        if (theFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filename = theFileChooser.getSelectedFile().getAbsolutePath();
            if (!(filename.endsWith(".ppm") || filename.endsWith(".PPM"))) {
                // this can't read anything but ppm!
                JOptionPane.showMessageDialog(this, "Can only read PPM!",
                        "Read Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // The selected image is a PPM.  Create a new PPMImage instance.
                    theImage = new PPMImage(filename);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Exception in opening file",
                            "Read Error", JOptionPane.ERROR_MESSAGE);
                }

                // Now that we know the actual image size, resize the window to fit the image
                int height = theImage.getNumRows() + 100;
                int width = theImage.getNumCols() + 20;
                if (width < 500) {
                    width = 500;  // minimum width needed to see the buttons
                }
                if (height < 200) {
                    height = 200; // minimum height
                }
                super.setSize(width, height);

                // Create an SImage from the PPMImage object and display it in the label
                // NOTE: SImage presumes the first dimension is rgb, second is columns, third 
                //       is rows, which is the opposite of how we are storing pixels.  So
                //       need to reverse the order of the three dimensions (xyz -> zyx). 
                imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));

                // Now that an image is displayed; enbable buttons
                revertMenuItem.setEnabled(true); // enable Revert menu item
                saveAsMenuItem.setEnabled(true); // enable Save As... menu item
                toolsMenu.setEnabled(true); // enable Tools menu
                grayscaleMenuItem.setEnabled(true); // enable Grayscale menu item
                editMenu.setEnabled(true);
                save = true;
            }
        }
    }

    /**
     * doSaveAsMenuItem - save the displayed file
     */
    private void doSaveAsMenuItem() {
        if (theFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // we presume that we must write as a ppm
            String filename = theFileChooser.getSelectedFile().getAbsolutePath();
            if (filename.endsWith(".ppm") || filename.endsWith(".PPM")) {
                try {
                    theImage.writePPMImage(filename);
                    save = true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Exception in opening file",
                            "Write Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                filename = filename + ".ppm";
                try {
                    theImage.writePPMImage(filename);
                    save = true;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Exception in opening file",
                            "Write Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    /**
     * doRevertMenuItem - re-read the file from the disk
     */
    private void doRevertMenuItem() {
        try {
            // The selected image is a PPM.  Create a new PPMImage instance.
            theImage = new PPMImage(filename);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Exception in opening file",
                    "Read Error", JOptionPane.ERROR_MESSAGE);
        }

        // Now that we know the actual image size, resize the window to fit the image
        int height = theImage.getNumRows() + 100;
        int width = theImage.getNumCols() + 20;
        if (width < 500) {
            width = 500;  // minimum width needed to see the buttons
        }
        if (height < 200) {
            height = 200; // minimum height
        }
        super.setSize(width, height);

        // Create an SImage from the PPMImage object and display it in the label
        // NOTE: SImage presumes the first dimension is rgb, second is columns, third 
        //       is rows, which is the opposite of how we are storing pixels.  So
        //       need to reverse the order of the three dimensions (xyz -> zyx). 
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
        grayscaleMenuItem.setEnabled(true);
    }

    /**
     * doQuitMenuItem - quit the program
     */
    private void doQuitMenuItem() {
        if (save == false) {
            Object[] option = {"OK", "Cancel"};
            int response = JOptionPane.showOptionDialog(this, "Do you want to quit without saving?", "Save?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, option, option[0]);
            if (response == 0) {
                System.exit(0);
            } else {
                return;
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * doUndoMenuItem - cause the image to return to what it was before the
     * previous modification was accepted
     */
    private void doUndoMenuItem() {
        theImage = new PPMImage(backUpImage);
        int height = theImage.getNumRows() + 100;
        int width = theImage.getNumCols() + 20;
        if (width < 500) {
            width = 500;  // minimum width needed to see the buttons
        }
        if (height < 200) {
            height = 200; // minimum height
        }
        super.setSize(width, height);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
        undoMenuItem.setEnabled(false);
        grayscaleMenuItem.setEnabled(true);
    }

    /**
     * doInvertButton - invert the picture to make it look like a photographic
     * negative
     */
    private void doInvertMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int[][][] pixels = theImage.getPixels();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    pixels[i][j][rgb] = 255 - pixels[i][j][rgb];
                }
            }
        }
        theImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * doGrayScaleMenuItem - Convert the image into gray scale
     */
    private void doGrayscaleMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int[][][] pixels = theImage.getPixels();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int grayVal = (int) ((0.2126 * pixels[i][j][0]) + (0.7152 * pixels[i][j][1]) + (0.0722 * pixels[i][j][2]));
                pixels[i][j][0] = grayVal;
                pixels[i][j][1] = grayVal;
                pixels[i][j][2] = grayVal;
            }
        }
        theImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
        grayscaleMenuItem.setEnabled(false);
    }

    /**
     * doDoubleSizeMenuItem - create a new PPMImage that results from converting
     * the current image to one that has twice the height and twice the width.
     */
    private void doDoubleSizeMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int[][][] pixels = theImage.getPixels();
        int[][][] newPixels = new int[2 * row][2 * col][3];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newPixels[2 * i][2 * j][0] = pixels[i][j][0];
                newPixels[2 * i][2 * j][1] = pixels[i][j][1];
                newPixels[2 * i][2 * j][2] = pixels[i][j][2];
                newPixels[2 * i + 1][2 * j][0] = pixels[i][j][0];
                newPixels[2 * i + 1][2 * j][1] = pixels[i][j][1];
                newPixels[2 * i + 1][2 * j][2] = pixels[i][j][2];
                newPixels[2 * i][2 * j + 1][0] = pixels[i][j][0];
                newPixels[2 * i][2 * j + 1][1] = pixels[i][j][1];
                newPixels[2 * i][2 * j + 1][2] = pixels[i][j][2];
                newPixels[2 * i + 1][2 * j + 1][0] = pixels[i][j][0];
                newPixels[2 * i + 1][2 * j + 1][1] = pixels[i][j][1];
                newPixels[2 * i + 1][2 * j + 1][2] = pixels[i][j][2];
            }
        }
        int height = 2 * theImage.getNumRows() + 100;
        int width = 2 * theImage.getNumCols() + 20;
        if (width < 500) {
            width = 500;  // minimum width needed to see the buttons
        }
        if (height < 200) {
            height = 200; // minimum height
        }
        super.setSize(width, height);
        theImage.setPixels(newPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * doHalveSizeMenuItem - create a new PPMImage that results from converting
     * the current image to one that has half the height and half the width.
     */
    private void doHalveSizeMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int[][][] pixels = theImage.getPixels();
        int[][][] newPixels = new int[row / 2][col / 2][3];
        for (int i = 0; i < row / 2; i++) {
            for (int j = 0; j < col / 2; j++) {
                newPixels[i][j][0] = (pixels[i * 2][j * 2][0] + pixels[i * 2 + 1][j * 2][0] + pixels[i * 2][j * 2 + 1][0] + pixels[i * 2 + 1][j * 2 + 1][0]) / 4;
                newPixels[i][j][1] = (pixels[i * 2][j * 2][1] + pixels[i * 2 + 1][j * 2][1] + pixels[i * 2][j * 2 + 1][1] + pixels[i * 2 + 1][j * 2 + 1][1]) / 4;
                newPixels[i][j][2] = (pixels[i * 2][j * 2][2] + pixels[i * 2 + 1][j * 2][2] + pixels[i * 2][j * 2 + 1][2] + pixels[i * 2 + 1][j * 2 + 1][2]) / 4;
            }
        }
        int height = theImage.getNumRows() / 2 + 100;
        int width = theImage.getNumCols() / 2 + 20;
        if (width < 500) {
            width = 500;  // minimum width needed to see the buttons
        }
        if (height < 200) {
            height = 200; // minimum height
        }
        super.setSize(width, height);
        theImage.setPixels(newPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * doAdjustBrightnessMenuItem - Adjust the image's brightness with the
     * change of the slider
     */
    private void doAdjustBrightnessMenuItem(int input) {
        tempImage = new PPMImage(theImage);
        int row, col;
        row = tempImage.getNumRows();
        col = tempImage.getNumCols();
        int[][][] pixels = tempImage.getPixels();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < 3; k++) {
                    pixels[i][j][k] = pixels[i][j][k] + input;
                    if (pixels[i][j][k] < 0) {
                        pixels[i][j][k] = 0;
                    } else if (pixels[i][j][k] > 255) {
                        pixels[i][j][k] = 255;
                    }
                }
            }
        }
        tempImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(tempImage.getPixels())));
    }

    /**
     * doAdjustContrastMenuItem - Adjust the image's contrst with the change of
     * the slider
     */
    private void doAdjustContrastMenuItem(int input) {
        tempImage = new PPMImage(theImage);
        int row, col;
        row = tempImage.getNumRows();
        col = tempImage.getNumCols();
        int[][][] pixels = tempImage.getPixels();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < 3; k++) {
                    pixels[i][j][k] = (int) (((259.0 * (input + 255)) / (255.0 * (259 - input)) * (pixels[i][j][k] - 128)) + 128.0);
                    if (pixels[i][j][k] < 0) {
                        pixels[i][j][k] = 0;
                    } else if (pixels[i][j][k] > 255) {
                        pixels[i][j][k] = 255;
                    }
                }
            }
        }
        tempImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(tempImage.getPixels())));
    }

    /**
     * doChangeLevelsMenuItem - Adjust the image's color levels with the change
     * of the slider
     */
    private void doChangeLevelsMenuItem(int input) {
        tempImage = new PPMImage(theImage);
        int row, col;
        row = tempImage.getNumRows();
        col = tempImage.getNumCols();
        int[][][] pixels = tempImage.getPixels();
        int binWidth = (int) (256.0 / input);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < 3; k++) {
                    pixels[i][j][k] = ((pixels[i][j][k] / binWidth) * binWidth) + (binWidth / 2);
                }
            }
        }
        tempImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(tempImage.getPixels())));
    }

    /**
     * doChromaKeyMenuItem - superimpose the non-green pixels of an appropriate
     * “green screen” image onto an already loaded regular image.
     */
    private void doChromaKeyMenuItem() {
        int[][][] pixels = theImage.getPixels();
        if (theFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            filename = theFileChooser.getSelectedFile().getAbsolutePath();
            if (!(filename.endsWith(".ppm") || filename.endsWith(".PPM"))) {
                // this can't read anything but ppm!
                JOptionPane.showMessageDialog(this, "Can only read PPM!",
                        "Read Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    // The selected image is a PPM.  Create a new PPMImage instance.
                    tempImage = new PPMImage(filename);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Exception in opening file",
                            "Read Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            int row, col, newRow, newCol;
            row = theImage.getNumRows();
            col = theImage.getNumCols();
            newRow = tempImage.getNumRows();
            newCol = tempImage.getNumCols();
            int[][][] newPixels = tempImage.getPixels();
            if (newRow > row && newCol > col) {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (newPixels[i][j][0] < 10 && newPixels[i][j][1] > 200 && newPixels[i][j][0] < 100) {
                            pixels[i][j][0] = pixels[i][j][0];
                            pixels[i][j][1] = pixels[i][j][1];
                            pixels[i][j][2] = pixels[i][j][2];
                        } else {
                            pixels[i][j][0] = newPixels[i][j][0];
                            pixels[i][j][1] = newPixels[i][j][1];
                            pixels[i][j][2] = newPixels[i][j][2];
                        }
                    }
                }
            } else if (newRow > row && newCol <= col) {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < newCol; j++) {
                        if (newPixels[i][j][0] < 10 && newPixels[i][j][1] > 200 && newPixels[i][j][0] < 100) {
                            pixels[i][j][0] = pixels[i][j][0];
                            pixels[i][j][1] = pixels[i][j][1];
                            pixels[i][j][2] = pixels[i][j][2];
                        } else {
                            pixels[i][j][0] = newPixels[i][j][0];
                            pixels[i][j][1] = newPixels[i][j][1];
                            pixels[i][j][2] = newPixels[i][j][2];
                        }
                    }
                }
            } else if (newRow <= row && newCol > col) {
                for (int i = 0; i < newRow; i++) {
                    for (int j = 0; j < col; j++) {
                        if (newPixels[i][j][0] < 10 && newPixels[i][j][1] > 200 && newPixels[i][j][0] < 100) {
                            pixels[i][j][0] = pixels[i][j][0];
                            pixels[i][j][1] = pixels[i][j][1];
                            pixels[i][j][2] = pixels[i][j][2];
                        } else {
                            pixels[i][j][0] = newPixels[i][j][0];
                            pixels[i][j][1] = newPixels[i][j][1];
                            pixels[i][j][2] = newPixels[i][j][2];
                        }
                    }
                }
            } else {
                for (int i = 0; i < newRow; i++) {
                    for (int j = 0; j < newCol; j++) {
                        if (newPixels[i][j][0] < 10 && newPixels[i][j][1] > 200 && newPixels[i][j][0] < 100) {
                            pixels[i][j][0] = pixels[i][j][0];
                            pixels[i][j][1] = pixels[i][j][1];
                            pixels[i][j][2] = pixels[i][j][2];
                        } else {
                            pixels[i][j][0] = newPixels[i][j][0];
                            pixels[i][j][1] = newPixels[i][j][1];
                            pixels[i][j][2] = newPixels[i][j][2];
                        }
                    }
                }
            }
        } else {
            theImage = new PPMImage(theImage);
        }

        theImage.setPixels(pixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * doBlurMenuItem - blur the image with the different levels of the slider
     */
    private void doBlurMenuItem(int input) {
        tempImage = new PPMImage(theImage);
        int row, col;
        row = tempImage.getNumRows();
        col = tempImage.getNumCols();
        int[][][] pixels = tempImage.getPixels();
        int[][][] tempPixels = new int[row - (2 * input)][col - (2 * input)][3];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                for (int k = 0; k < 3; k++) {
                    if (i > input && j > input && row - i > input && col - j > input) {
                        int addValue = 0;
                        int value = pixels[i][j][k];
                        for (int n = i - input; n <= i + input; n++) {
                            for (int m = j - input; m <= j + input; m++) {
                                int divider = 0;
                                addValue = addValue + pixels[n][m][k];
                                divider = (input + input + 1) * (input + input + 1);
                                value = addValue / divider;
                            }
                        }
                        pixels[i][j][k] = value;
                    }
                }
            }
        }
        for (int i = 0; i < row - (2 * input); i++) {
            for (int j = 0; j < col - (2 * input); j++) {
                for (int k = 0; k < 3; k++) {
                    tempPixels[i][j][k] = pixels[i + input][j + input][k];
                }
            }
        }
        tempImage.setPixels(tempPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(tempImage.getPixels())));
    }

    /**
     * doCropMenuItem - allow the user to specify a section of the original
     * image to be saved, creating a new image whose pixels are a subset of the
     * original image’s pixels.
     */
    private void doCropMenuItem(int input) {
        int row, col, cropRow, cropCol;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int[][][] pixels = theImage.getPixels();
        tempImage = new PPMImage(theImage);
        double percent = 1 - (input / 100.0);
        cropRow = (int) (percent * row) / 2;
        cropCol = (int) (percent * col) / 2;
        int[][][] tempPixels = new int[row - (2 * cropRow)][col - (2 * cropCol)][3];
        for (int i = 0; i < row - (2 * cropRow); i++) {
            for (int j = 0; j < col - (2 * cropCol); j++) {
                for (int k = 0; k < 3; k++) {
                    tempPixels[i][j][k] = pixels[i + cropRow][j + cropCol][k];
                }
            }
        }
        tempImage.setPixels(tempPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(tempImage.getPixels())));

    }

    /**
     * doFlipHorizontalMenuItem - flip the image horizontally
     */
    private void doFlipHorizontalMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int pixels[][][] = theImage.getPixels();
        int tempPixels[][][] = theImage.getPixels();
        int midCol = col / 2;
        if (col % 2 == 0) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    for (int k = 0; k < 3; k++) {
                        tempPixels[i][j][k] = pixels[i][midCol + midCol - j - 1][k];
                    }
                }
            }
        } else {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    for (int k = 0; k < 3; k++) {
                        tempPixels[i][j][k] = pixels[i][midCol + midCol - j][k];
                    }
                }
            }
        }
        theImage.setPixels(tempPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * doFlipVerticalMenuItem - flip the image vertically
     */
    private void doFlipVerticalMenuItem() {
        int row, col;
        row = theImage.getNumRows();
        col = theImage.getNumCols();
        int pixels[][][] = theImage.getPixels();
        int tempPixels[][][] = theImage.getPixels();
        int midRow = row / 2;
        if (row % 2 == 0) {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    for (int k = 0; k < 3; k++) {
                        tempPixels[i][j][k] = pixels[midRow + midRow - i - 1][j][k];
                    }
                }
            }
        } else {
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    for (int k = 0; k < 3; k++) {
                        tempPixels[i][j][k] = pixels[midRow + midRow - i][j][k];
                    }
                }
            }
        }
        theImage.setPixels(tempPixels);
        imageLabel.setIcon(new SImage(transpose3DArray(theImage.getPixels())));
    }

    /**
     * transpose3DArray - This utility method will transpose a 3D array by
     * reversing the indices, such that inArray[x][y][z] is returned as
     * outArray[z][y][x].
     *
     * @param inArray the original 3D array to reverse
     * @return outArray the reversed 3D array
     */
    private int[][][] transpose3DArray(int[][][] inArray) {
        int[][][] outArray = new int[inArray[0][0].length][inArray[0].length][inArray.length];
        for (int x = 0; x < inArray.length; x++) {
            for (int y = 0; y < inArray[0].length; y++) {
                for (int z = 0; z < inArray[0][0].length; z++) {
                    outArray[z][y][x] = inArray[x][y][z];
                }
            }
        }
        return outArray;
    }

    /**
     * main.
     */
    public static void main(String[] args) {
        Fauxtoshop theViewer = new Fauxtoshop();  // construct a new instance of the class
    }
}
