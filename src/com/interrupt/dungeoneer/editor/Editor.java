package com.interrupt.dungeoneer.editor;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.editor.history.EditorHistory;
import com.interrupt.dungeoneer.editor.ui.menu.Scene2dMenuBar;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.serializers.KryoSerializer;
import net.cotd.delverunlimited.helper.FileChooserHelper;
import net.cotd.delverunlimited.helper.history.History;
import net.cotd.delverunlimited.helper.history.HistoryHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;

public class Editor
{
    private static EditorFrame editorFrame;
    private final JFrame frame;
    private static String currentFileName = null;
    public static String currentDirectory = null;
    public ActionListener saveAction;
    public ActionListener saveAsAction;
    public ActionListener openAction;
    public ActionListener rotateLeftAction;
    public ActionListener rotateRightAction;
    public ActionListener playAction;
    public ActionListener carveAction;
    public ActionListener paintAction;
    public ActionListener deleteAction;
    public ActionListener planeHeightAction;
    public ActionListener vertexHeightAction;
    public ActionListener vertexToggleAction;
    public ActionListener undoAction;
    public ActionListener redoAction;
    public ActionListener toggleCollisionBoxesAction;
    public ActionListener toggleLightsAction;
    public ActionListener escapeAction;
    public ActionListener rotateCeilTexAction;
    public ActionListener rotateFloorTexAction;
    public ActionListener rotateWallAngle;
    public ActionListener copyAction;
    public ActionListener pasteAction;
    public ActionListener xDragMode;
    public ActionListener yDragMode;
    public ActionListener zDragMode;
    public ActionListener rotateMode;
    public ActionListener flattenFloor;
    public ActionListener flattenCeiling;
    public ActionListener toggleSimulation;
    public ActionListener exitAction;

    public enum EditorMode
    {
        Carve,  Paint;

        EditorMode() {}
    }

    private static Timer saveMessageTimer = new Timer();

    public Editor(LwjglApplicationConfiguration config)
    {
        this.frame = new JFrame("DelvEdit");
        this.frame.setDefaultCloseOperation(2);


        editorFrame = new EditorFrame(this.frame, this);
        new LwjglApplication(editorFrame, config);

        initActions();
    }

    private void initActions()
    {
        Editor editor = this;

        this.flattenFloor = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.flattenFloor();
            }
        };
        this.flattenCeiling = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.flattenCeiling();
            }
        };
        /* D-U */
        this.exitAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Gdx.app.log("DelvEdit", "Bye!");
                Gdx.app.exit();
            }
        };
        this.saveAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(currentFileName != null && currentDirectory != null) {

                    editorFrame.save(currentDirectory + File.separator + currentFileName);
                    Editor.this.frame.setTitle("DelvEdit - " + currentFileName);
                } else {
                    Editor.this.saveAsAction.actionPerformed(event);
                }
            }
        };
        this.saveAsAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                if(currentDirectory == null) {
                    currentDirectory = (new FileHandle(".")).file().getAbsolutePath();
                    currentDirectory = currentDirectory.substring(0, currentDirectory.length() - 2);
                }

                String suggestedName = currentFileName;
                if(suggestedName == null) {
                    suggestedName = "level.bin";
                }

                File selectedDir = FileChooserHelper.saveFileDialog(new File(currentDirectory), suggestedName);
                try {
                    if (selectedDir == null) throw new NullPointerException();
                    editorFrame.save(selectedDir.getAbsolutePath());
                    currentDirectory = selectedDir.getParent();
                    currentFileName = selectedDir.getName();
                    Scene2dMenuBar.setTitleLabel(currentFileName);
                } catch (NullPointerException ex) {
                    Gdx.app.log("DelvEdit", "Selected null!");
                } catch (Exception var4) {
                    Gdx.app.error("DelvEdit", var4.getMessage());
                }
            }
        };
        this.openAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                openInEditor(null, false);
            }
        };
        this.rotateLeftAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                editorFrame.level.rotate90();
                editorFrame.refresh();
            }
        };
        this.rotateRightAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                editorFrame.level.rotate90();
                editorFrame.level.rotate90();
                editorFrame.level.rotate90();
                editorFrame.refresh();
            }
        };
        this.playAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                editorFrame.testLevel();
            }
        };
        this.carveAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.doCarve();
            }
        };
        this.paintAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.doPaint();
            }
        };
        this.deleteAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.doDelete();
            }
        };
        this.planeHeightAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setPlaneHeightMode();
            }
        };
        this.vertexHeightAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setVertexHeightMode();
            }
        };
        this.vertexToggleAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.toggleVertexHeightMode();
            }
        };
        this.undoAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.undo();
            }
        };
        this.redoAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.redo();
            }
        };
        this.toggleCollisionBoxesAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.toggleCollisionBoxes();
            }
        };
        this.toggleLightsAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.toggleLights();
            }
        };
        this.escapeAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.clearSelection();
            }
        };
        this.rotateCeilTexAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.rotateCeilTex(1);
            }
        };
        this.rotateFloorTexAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.rotateFloorTex(1);
            }
        };
        this.rotateWallAngle = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.rotateAngle();
            }
        };
        this.copyAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.copy();
            }
        };
        this.pasteAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.paste();
            }
        };
        this.toggleSimulation = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.toggleSimulation();
            }
        };
        this.xDragMode = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setDragMode(EditorFrame.DragMode.X);
            }
        };
        this.yDragMode = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setDragMode(EditorFrame.DragMode.Y);
            }
        };
        this.zDragMode = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setDragMode(EditorFrame.DragMode.Z);
            }
        };
        this.rotateMode = new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                editorFrame.setMoveMode(EditorFrame.MoveMode.ROTATE);
            }
        };
    }

    public void createdNewLevel()
    {
        currentDirectory = null;
        currentFileName = null;
        Scene2dMenuBar.setTitleLabel("New Level");
    }

    public static void openInEditor(File levelFile, boolean fromHistory) {
        if(currentDirectory == null) {
            currentDirectory = (new FileHandle(".")).file().getAbsolutePath();
            currentDirectory = currentDirectory.substring(0, currentDirectory.length() - 2);
        }

        File selectedFile = null;
        if (levelFile == null) {
            selectedFile = FileChooserHelper.selectFileDialog(new File(currentDirectory));
            if (selectedFile != null) {
                HistoryHelper.addHistory(new History(selectedFile.getName(), selectedFile.getAbsolutePath()));
            }
        } else {
            if (fromHistory) {
                selectedFile = levelFile;
            }
        }

        try {
            if (selectedFile == null) throw new NullPointerException();
            currentDirectory = selectedFile.getParent(); // C:\current_directory
            currentFileName = selectedFile.getName();    // file_name.bin
            saveMessageTimer.cancel();
            String file = currentFileName;
            String dir = currentDirectory;
            FileHandle level = Gdx.files.getFileHandle(selectedFile.getAbsolutePath(), Files.FileType.Absolute);

            if (level.exists()) {
                editorFrame.curFileName = level.path();

                Scene2dMenuBar.setTitleLabel(currentFileName);
                if (file.endsWith(".png")) {
                    String heightFile = dir + file.replace(".png", "-height.png");
                    if (!Gdx.files.getFileHandle(heightFile, Files.FileType.Absolute).exists()) {
                        heightFile = dir + file.replace(".png", "_height.png");
                        if (!Gdx.files.getFileHandle(heightFile, Files.FileType.Absolute).exists()) {
                            heightFile = null;
                        }
                    }
                    Level openLevel = new Level();
                    openLevel.loadForEditor(dir + file, heightFile);
                    editorFrame.level = openLevel;
                    editorFrame.refresh();

                    editorFrame.camX = (float) (openLevel.width / 2);
                    editorFrame.camZ = 4.5F;
                    editorFrame.camY = (float) (openLevel.height / 2);
                } else if (file.endsWith(".bin")) {
                    Level openLevel = KryoSerializer.loadLevel(level);

                    openLevel.init(Level.Source.EDITOR);

                    editorFrame.level = openLevel;
                    editorFrame.refresh();

                    editorFrame.camX = (float) (openLevel.width / 2);
                    editorFrame.camZ = 4.5F;
                    editorFrame.camY = (float)(openLevel.height / 2);
                } else {
                    Level openLevel = Game.fromJson(Level.class, level);
                    openLevel.init(Level.Source.EDITOR);

                    editorFrame.level = openLevel;
                    editorFrame.refresh();

                    editorFrame.camX = (float)(openLevel.width / 2);
                    editorFrame.camZ = 4.5F;
                    editorFrame.camY = (float)(openLevel.height / 2);
                }
                editorFrame.history = new EditorHistory();
            }

        } catch (NullPointerException ex) {
            Gdx.app.log("DelvEdit", "Canceled file picker!");
        } catch (Exception ex) {
            Gdx.app.error("DelvEdit", ex.getMessage());
        }
    }
}
