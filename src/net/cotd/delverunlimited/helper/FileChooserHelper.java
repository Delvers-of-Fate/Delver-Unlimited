package net.cotd.delverunlimited.helper;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Source:
 * https://www.reddit.com/r/javahelp/comments/2lypn4/trying_to_use_javafxstagefilechooser_in_a_swing/cm07xyo/
 *
 */

final public class FileChooserHelper {

    static {
        // To prevent automatic JavaFX launcher thread shutdown after last JavaFX window is closed.
        // Probably not necessary for the FileChooser situation, but better safe than sorry.
        Platform.setImplicitExit(false);
    }


    /**
     * Does not only check if JavaFX is still ready for use but also initializes the JavaFX Toolkit, if that hasn't
     * happend yet.
     *
     * @return True if JavaFX stuff can be used right after this method call. False if the JavaFX launcher thread has
     * already shut down.
     */
    private static boolean isJavaFXStillUsable() {

        try {
            final JFXPanel dummyForToolkitInitialization = new JFXPanel(); // Initializes the Toolkit required by JavaFX, as stated in the docs of Platform.runLater()
        } catch (IllegalStateException ise) {
            return false;
        }
        return true;
    }

    final private static Object LOCK = new Object();

    public static File selectFileDialog(File workingDir) {

        if (!isJavaFXStillUsable()) { // Necessary, or the LOCK.wait() further down will never end.
            System.err.println("Problem in chooseFileWithJavaFXDialog(): JavaFX launcher thread has already shut down, can't do anything JavaFX any more.");
            return null;
        }

        synchronized (LOCK) {
            final File[] chosenFile = new File[1]; // dirty hack to evade usage of a class variable (aka field)
            final boolean[] keepWaiting = new boolean[1]; // same
            keepWaiting[0] = true;

            Platform.runLater(() -> {

                synchronized (LOCK) {
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Load Level");
                    fileChooser.setInitialDirectory(workingDir);

                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("BIN file", "*.bin"),
                            new FileChooser.ExtensionFilter("DAT file", "*.dat"),
                            new FileChooser.ExtensionFilter("PNG file", "*.png")

                    );

                    chosenFile[0] = fileChooser.showOpenDialog(null);
                    keepWaiting[0] = false;
                    LOCK.notifyAll();
                }
            });

            // Wait for runLater to start and complete its thing.
            do {
                try {
                    LOCK.wait();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            } while (keepWaiting[0]);

            return chosenFile[0];
        }

    }

    public static File saveFileDialog(File workingDir, String suggestedName) {

        if (!isJavaFXStillUsable()) { // Necessary, or the LOCK.wait() further down will never end.
            System.err.println("Problem in chooseFileWithJavaFXDialog(): JavaFX launcher thread has already shut down, can't do anything JavaFX any more.");
            return null;
        }

        synchronized (LOCK) {
            final File[] chosenFile = new File[1]; // dirty hack to evade usage of a class variable (aka field)
            final boolean[] keepWaiting = new boolean[1]; // same
            keepWaiting[0] = true;

            Platform.runLater(() -> {

                synchronized (LOCK) {
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Level");
                    fileChooser.setInitialDirectory(workingDir);
                    fileChooser.setInitialFileName(suggestedName);

                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("BIN file", "*.bin"),
                            new FileChooser.ExtensionFilter("DAT file", "*.dat")

                    );

                    chosenFile[0] = fileChooser.showSaveDialog(null);
                    //                    Platform.exit(); // Can't be called here, or JavaFX is gone for good. So, System.exit() HAS TO BE used at the end, no simply dying possible.
                    keepWaiting[0] = false;
                    LOCK.notifyAll();
                }
            });

            // Wait for runLater to start and complete its thing.
            do {
                try {
                    LOCK.wait();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            } while (keepWaiting[0]);

            return chosenFile[0];
        }

    }
}