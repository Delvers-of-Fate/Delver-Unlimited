package com.interrupt.dungeoneer.editor;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.interrupt.dungeoneer.editor.EditorFrame.DragMode;
import com.interrupt.dungeoneer.editor.EditorFrame.MoveMode;
import com.interrupt.dungeoneer.editor.history.EditorHistory;
import com.interrupt.dungeoneer.editor.ui.menu.Scene2dMenuBar;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.Level.Source;
import com.interrupt.dungeoneer.serializers.KryoSerializer;
import net.cotd.delverunlimited.helper.FileChooserHelper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;

public class Editor {
    private EditorFrame editorFrame;
    private final JFrame frame = new JFrame("DelvEdit");
    private String currentFileName = null;
    private String currentDirectory = null;
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
    private Timer saveMessageTimer = new Timer();

    public Editor(LwjglApplicationConfiguration config) {
        this.frame.setDefaultCloseOperation(2);
        this.editorFrame = new EditorFrame(this.frame, this);
        new LwjglApplication(this.editorFrame, config);
        this.initActions();
    }


    private void initActions() {
        this.flattenFloor = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.flattenFloor();
            }
        };
        this.flattenCeiling = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.flattenCeiling();
            }
        };
        this.saveAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(Editor.this.currentFileName != null && Editor.this.currentDirectory != null) {

                    Editor.this.editorFrame.save(Editor.this.currentDirectory + File.separator + Editor.this.currentFileName);
                    Editor.this.frame.setTitle("DelvEdit - " + Editor.this.currentFileName);
                } else {
                    Editor.this.saveAsAction.actionPerformed(event);
                }

            }
        };
        this.saveAsAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(Editor.this.currentDirectory == null) {
                    Editor.this.currentDirectory = (new FileHandle(".")).file().getAbsolutePath();
                    Editor.this.currentDirectory = Editor.this.currentDirectory.substring(0, Editor.this.currentDirectory.length() - 2);
                }

                String suggestedName = currentFileName;
                if(suggestedName == null) {
                    suggestedName = "level.bin";
                }

                File selectedDir = FileChooserHelper.saveFileDialog(new File(currentDirectory), suggestedName);
                try {
                    if (selectedDir == null) throw new NullPointerException();
                    Editor.this.editorFrame.save(selectedDir.getAbsolutePath());
                    Editor.this.currentDirectory = selectedDir.getParent();
                    Editor.this.currentFileName = selectedDir.getName();
                    Scene2dMenuBar.setTitleLabel(Editor.this.currentFileName);
                } catch (NullPointerException ex) {
                    Gdx.app.log("DelvEdit", "Selected null!");
                } catch (Exception var4) {
                    Gdx.app.error("DelvEdit", var4.getMessage());
                }
            }
        };
        this.openAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(Editor.this.currentDirectory == null) {
                    Editor.this.currentDirectory = (new FileHandle(".")).file().getAbsolutePath();
                    Editor.this.currentDirectory = Editor.this.currentDirectory.substring(0, Editor.this.currentDirectory.length() - 2);
                }

                File selectedFile = FileChooserHelper.selectFileDialog(new File(currentDirectory));

                try {
                    if (selectedFile == null) throw new NullPointerException();
                    Editor.this.currentDirectory = selectedFile.getParent(); // FULL PATH
                    Editor.this.currentFileName = selectedFile.getName();
                    Editor.this.saveMessageTimer.cancel();
                    String file = Editor.this.currentFileName;
                    String dir = Editor.this.currentDirectory;
                    FileHandle level = Gdx.files.getFileHandle(selectedFile.getAbsolutePath(), FileType.Absolute);
                    if (level.exists()) {
                        Editor.this.editorFrame.curFileName = level.path();
                        Scene2dMenuBar.setTitleLabel(Editor.this.currentFileName);
                        if (file.endsWith(".png")) {
                            String heightFile = dir + file.replace(".png", "-height.png");
                            if (!Gdx.files.getFileHandle(heightFile, FileType.Absolute).exists()) {
                                heightFile = dir + file.replace(".png", "_height.png");
                                if (!Gdx.files.getFileHandle(heightFile, FileType.Absolute).exists()) {
                                    heightFile = null;
                                }
                            }

                            Level openLevel = new Level();
                            openLevel.loadForEditor(dir + file, heightFile);
                            Editor.this.editorFrame.level = openLevel;
                            Editor.this.editorFrame.refresh();
                            Editor.this.editorFrame.camX = (float) (openLevel.width / 2);
                            Editor.this.editorFrame.camZ = 4.5F;
                            Editor.this.editorFrame.camY = (float) (openLevel.height / 2);
                        } else {
                            Level openLevelx;
                            if (file.endsWith(".bin")) {
                                openLevelx = KryoSerializer.loadLevel(level);
                                openLevelx.init(Source.EDITOR);
                                Editor.this.editorFrame.level = openLevelx;
                                Editor.this.editorFrame.refresh();
                                Editor.this.editorFrame.camX = (float) (openLevelx.width / 2);
                                Editor.this.editorFrame.camZ = 4.5F;
                                Editor.this.editorFrame.camY = (float) (openLevelx.height / 2);
                            } else {
                                openLevelx = (Level) Game.fromJson(Level.class, level);
                                openLevelx.init(Source.EDITOR);
                                Editor.this.editorFrame.level = openLevelx;
                                Editor.this.editorFrame.refresh();
                                Editor.this.editorFrame.camX = (float) (openLevelx.width / 2);
                                Editor.this.editorFrame.camZ = 4.5F;
                                Editor.this.editorFrame.camY = (float) (openLevelx.height / 2);
                            }
                        }

                        Editor.this.editorFrame.history = new EditorHistory();
                    }
                } catch (NullPointerException ex) {
                    Gdx.app.log("DelvEdit", "Selected null!");
                } catch (Exception var8) {
                    Gdx.app.error("DelvEdit", var8.getMessage());
                }
            }
        };
        this.rotateLeftAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Editor.this.editorFrame.level.rotate90();
                Editor.this.editorFrame.refresh();
            }
        };
        this.rotateRightAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Editor.this.editorFrame.level.rotate90();
                Editor.this.editorFrame.level.rotate90();
                Editor.this.editorFrame.level.rotate90();
                Editor.this.editorFrame.refresh();
            }
        };
        this.playAction = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Editor.this.editorFrame.testLevel();
            }
        };
        this.carveAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.doCarve();
            }
        };
        this.paintAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.doPaint();
            }
        };
        this.deleteAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.doDelete();
            }
        };
        this.planeHeightAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setPlaneHeightMode();
            }
        };
        this.vertexHeightAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setVertexHeightMode();
            }
        };
        this.vertexToggleAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.toggleVertexHeightMode();
            }
        };
        this.undoAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.undo();
            }
        };
        this.redoAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.redo();
            }
        };
        this.toggleCollisionBoxesAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.toggleCollisionBoxes();
            }
        };
        this.toggleLightsAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.toggleLights();
            }
        };
        this.escapeAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.clearSelection();
            }
        };
        this.rotateCeilTexAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.rotateCeilTex(1);
            }
        };
        this.rotateFloorTexAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.rotateFloorTex(1);
            }
        };
        this.rotateWallAngle = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.rotateAngle();
            }
        };
        this.copyAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.copy();
            }
        };
        this.pasteAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.paste();
            }
        };
        this.toggleSimulation = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.toggleSimulation();
            }
        };
        this.xDragMode = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setDragMode(DragMode.X);
            }
        };
        this.yDragMode = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setDragMode(DragMode.Y);
            }
        };
        this.zDragMode = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setDragMode(DragMode.Z);
            }
        };
        this.rotateMode = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Editor.this.editorFrame.setMoveMode(MoveMode.ROTATE);
            }
        };

        /* D-U */
        this.exitAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Gdx.app.exit();
            }
        };
    }

    public void createdNewLevel() {
        this.currentDirectory = null;
        this.currentFileName = null;
        Scene2dMenuBar.setTitleLabel("New Level");
    }

    public static enum EditorMode {
        Carve,
        Paint;

        private EditorMode() {
        }
    }
}
