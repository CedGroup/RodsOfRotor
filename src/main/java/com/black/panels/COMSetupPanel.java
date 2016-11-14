package com.black.panels;

import com.black.support.ComSetup;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nick_work on 04.02.2016.
 */
//Класс рисующий панель для настроек COM-порта
public class COMSetupPanel {
    public COMSetupPanel(ComSetup comSetup) {
        this.comSetup = comSetup;

        //Заполняем чек-бокс адресами COM-портов
        for (int i = 1; i <= 10; i++) {
            String str = "COM" + i;
            portNumber.addItem(str);
        }

        //Устанавливаем порт по умолчанию из файла
        for (int i = 0; i < portNumber.getItemCount(); i++) {
            Object obj = comSetup.getPort();
            if (portNumber.getItemAt(i).equals(obj)) {
                portNumber.setSelectedItem(obj);
            }
        }

        //Заполняем чек-бокс скоростями передачи данных
        Integer j = 2400;
        for (int i = 1; i <= 9; i++) {
            if (i == 4) {
                bitRate.addItem(new String("14400"));
            } else if (i == 6) {
                bitRate.addItem(new String("28800"));
            } else if (i == 8) {
                bitRate.addItem(new String("57600"));
            } else if (i == 9) {
                bitRate.addItem(new String("115200"));
            } else {
                bitRate.addItem(j.toString());
                j *= 2;
            }
        }

        //Устанавливаем скорость передачи по умолчанию из файла
        for (int i = 0; i < bitRate.getItemCount(); i++) {
            Object obj = comSetup.getBitRate().toString();
            if (bitRate.getItemAt(i).equals(obj)) {
                bitRate.setSelectedItem(obj);
            }
        }

        //Устанавливаем значение по-умолчанию
        //адресса slave-устройства
        devAddress.setValue(comSetup.getDevAddress());

        //Установка адреса для сохранения
        path = comSetup.getPath();

        //Установка значения в поле
        pathField.setText(path);

        //при нажатии кнопки выбора пути показать окно выбора пути
        getPathbutton.addActionListener(new GetPathListener());
    }

    private JPanel mainPanel;
    private JComboBox portNumber;
    private JComboBox bitRate;
    private JButton okButton;
    private JButton cancelButton;
    private JFormattedTextField devAddress;
    private JTextField pathField;
    private JButton getPathbutton;
    private ComSetup comSetup;
    private String path = "C:\\";

    //Возвращаем созданный объект кнопки OK
    public JButton getOkButton() {
        return okButton;
    }

    //Возвращаем созданный объект кнопки Отмена
    public JButton getCancelButton() {
        return cancelButton;
    }

    //Возвращаем созданный объект выпадающего списка адресов портов
    public JComboBox getPortNumber() {
        return portNumber;
    }

    //Возвращаем созданный объект выпадающего списка скоростей передачи
    public JComboBox getBitRate() {
        return bitRate;
    }

    //Возвращаем созданный объект выпадающего списка скоростей передачи
    public JFormattedTextField getDevAddress() {
        return devAddress;
    }

    //Получение данных из поля с указанным путем сохранения протоколов
    public JTextField getPathField() {
        return pathField;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(10, 10, 10, 10), -1, -1));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("Ок");
        mainPanel.add(okButton, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Отмена");
        mainPanel.add(cancelButton, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portNumber = new JComboBox();
        mainPanel.add(portNumber, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bitRate = new JComboBox();
        mainPanel.add(bitRate, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Номер порта:");
        mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Скорость передачи:");
        mainPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Адрес:");
        mainPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        devAddress = new JFormattedTextField();
        mainPanel.add(devAddress, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Путь для хранения архивов");
        mainPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathField = new JTextField();
        mainPanel.add(pathField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        getPathbutton = new JButton();
        getPathbutton.setText("Выбрать путь...");
        mainPanel.add(getPathbutton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    //Класс отслеживающий нажатие кнопки "Выбор пути"
    private class GetPathListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(path);
            chooser.setFileFilter(new ChooserFilter());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int r = chooser.showOpenDialog(mainPanel);
            if (r == JFileChooser.APPROVE_OPTION) {
                String choosePath = chooser.getSelectedFile().getAbsolutePath();
                pathField.setText(choosePath);
                path = choosePath;
            }
        }
    }

    //Фильтр для окна "Выбор пути"
    private class ChooserFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory();
        }

        @Override
        public String getDescription() {
            return null;
        }
    }

}
