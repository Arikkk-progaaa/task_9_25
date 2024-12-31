import util.ArrayUtils;
import util.JTableUtils;
import util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FrameMain extends JFrame {
    private JTable tableOutputArray;
    private JTable tableInputArray;
    private JButton buttonLoadFromFile;
    private JButton buttonSaveIntoFile;
    private JButton buttonCreateRandomArray;
    private JButton buttonOutputArray;
    private JPanel panelMain;
    private JScrollPane scrollPaneTableInput;

    private final JFileChooser fileChooserOpen;
    private final JFileChooser fileChooserSave;
    private JMenuBar menuBarMain;
    private JMenu menuLookAndFeel;

    public FrameMain() {
        this.setTitle("Task 9");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(tableInputArray, 60, true, true, false, true);
        JTableUtils.initJTableForArray(tableOutputArray, 60, true, true, false, false);

        tableInputArray.setRowHeight(30);
        tableOutputArray.setRowHeight(30);

        fileChooserOpen = new JFileChooser();
        fileChooserSave = new JFileChooser();
        fileChooserOpen.setCurrentDirectory(new File("."));
        fileChooserSave.setCurrentDirectory(new File("."));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Текстовые файлы", "txt");
        fileChooserOpen.addChoosableFileFilter(filter);
        fileChooserSave.addChoosableFileFilter(filter);

        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Сохранить");

        JTableUtils.writeArrayToJTable(tableInputArray, new int[][]{{0, 1, 2, 3}, {5, -1, 4, 2, 2}});

        buttonLoadFromFile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (fileChooserOpen.showOpenDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                        int[][] arr = ArrayUtils.readIntArray2FromFile(fileChooserOpen.getSelectedFile().getPath());
                        JTableUtils.writeArrayToJTable(tableInputArray, arr);
                    }
                } catch (Exception e) {
                    SwingUtils.showErrorMessageBox(e);
                }
            }
        });

        buttonSaveIntoFile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (fileChooserSave.showSaveDialog(panelMain) == JFileChooser.APPROVE_OPTION) {
                        int[][] arr = JTableUtils.readIntMatrixFromJTable(tableOutputArray);
                        String file = fileChooserSave.getSelectedFile().getPath();
                        if (!file.toLowerCase().endsWith(".txt")) {
                            file += ".txt";
                        }
                        ArrayUtils.writeArrayToFile(file, arr);
                    }
                } catch (Exception e) {
                    SwingUtils.showErrorMessageBox(e);
                }
            }
        });

        buttonCreateRandomArray.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int[][] arr = ArrayUtils.createRandomIntMatrix(tableInputArray.getRowCount(), tableInputArray.getColumnCount(), -100, 100);
                    JTableUtils.writeArrayToJTable(tableInputArray, arr);
                } catch (Exception e) {
                    SwingUtils.showErrorMessageBox(e);
                }
            }
        });

        buttonOutputArray.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    List<List<Integer>> list = JTableUtils.readIntList2FromJTable(tableInputArray);
                    assert list != null;
                    JTableUtils.writeListToJTable(tableOutputArray, Main.createNewList(list.get(0), list.get(1)));
                } catch (Exception e) {
                    SwingUtils.showErrorMessageBox(e);
                }
            }
        });
    }
}

