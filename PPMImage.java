
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Develop a fully functional class (named PPMImage) that implements the PPM
 * color image file format from the NetPBM image suite (netpbm.sourceforge.net).
 *
 *
 * @author Tianchang Yang
 * @version 06 April 2016
 */
public class PPMImage {

    // Instance variables:
    private int[][][] myPixels; // the dimensions are: rows, cols, rgb
    private int numRows;
    private int numCols;

    /**
     * PPMImage constructor - This constructor will create a new instance of
     * PPMImage based on the single argument corresponding to a filename.
     *
     * @param filename
     *
     */
    public PPMImage(String filename) throws Exception {
        readPPMImage(filename);
    }

    /**
     * PPMImage constructor - This constructor will create a new instance of the
     * PPMImage class based on the contents of a three-dimensional array of
     * integers.
     *
     * @param pixelsIn
     */
    public PPMImage(int[][][] pixelsIn) {
        numRows = pixelsIn.length;
        numCols = pixelsIn[0].length;
        myPixels = new int[numRows][numCols][3];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    myPixels[i][j][rgb] = pixelsIn[i][j][rgb];
                }
            }
        }
    }

    /**
     * PPMImage constructor - This constructor will create a new instance of the
     * PPMImage class based on an existing PPMImage instance.
     *
     * @param imageIn
     */
    public PPMImage(PPMImage imageIn) {
        numRows = imageIn.myPixels.length;
        numCols = imageIn.myPixels[0].length;
        myPixels = new int[numRows][numCols][3];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    myPixels[i][j][rgb] = imageIn.myPixels[i][j][rgb];
                }
            }
        }
    }

    /**
     * getNumRows - return the number of rows
     *
     * @return numRows
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * getNumCols - return the number of columns
     *
     * @return numCols
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * getPixels - return the array indicating pixels
     *
     * @return pixels
     */
    public int[][][] getPixels() {
        int[][][] pixels;
        pixels = new int[numRows][numCols][3];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    pixels[i][j][rgb] = myPixels[i][j][rgb];
                }
            }
        }
        return pixels;
    }

    /**
     * setPixels - description here
     *
     * @param pixelsIn
     */
    public void setPixels(int[][][] pixelsIn) {
        numRows = pixelsIn.length;
        numCols = pixelsIn[0].length;
        myPixels = new int[numRows][numCols][3];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    myPixels[i][j][rgb] = pixelsIn[i][j][rgb];
                }
            }
        }
    }

    /**
     * readPPMImage - read from a PPM Image file
     *
     * This method should read a file (in PPM format) and store the contents
     * into the instance variables.
     *
     * The method should both CONSTRUCT and POPULATE all instance variables from
     * the PPM data stored in the file.
     *
     * Recall that the PPM image format is: P3 # comment width(cols)
     * height(rows) maxColorLevel(probably 255) pixel0R pixel0G pixel0B pixel1R
     * pixel1G pixel1B pixel2R pixel2G pixel2B pixel3R ...
     *
     * @param filename
     */
    private void readPPMImage(String filename) throws Exception {
        
        FileReader inFR = new FileReader(filename);
        Scanner fin = new Scanner(inFR);
        String magicNum = fin.nextLine();
        String comment = fin.nextLine();
        numCols = fin.nextInt();
        numRows = fin.nextInt();
        int maxColorLevel = fin.nextInt();
        myPixels = new int [numRows][numCols][3];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    myPixels [row][col][rgb] = fin.nextInt();
                }
            }
        }
        fin.close();
    }

    /**
     * writePPMImage - write a PPM Image file
     *
     * This method should write to a file, named in the String parameter, the
     * contents of the myPixels instance variable.
     *
     * The contents should be written according to the PPM image format. The
     * pixel values should be written three per line, separated by spaces: P3 #
     * comment with YOUR NAME in it width(cols) height(rows)
     * maxColorLevel(probably 255) pixel0R pixel0G pixel0B pixel1R pixel1G
     * pixel1B pixel2R pixel2G pixel2B pixel3R ...
     *
     * @param filename
     */
    public void writePPMImage(String filename) throws Exception {
        FileWriter outFW = new FileWriter(filename);
        BufferedWriter fout = new BufferedWriter(outFW);
        fout.write("P3\n");
        fout.write("# created by Tianchang Yang \n");
        fout.write(numCols + " " + numRows + "\n");
        fout.write("255\n");
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                for (int rgb = 0; rgb < 3; rgb++) {
                    fout.write ( myPixels[row][col][rgb] + "\n");
                }
            }
        }
        fout.close();
    }  
}
