package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.support.ReadPattern;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nick on 02.02.2016.
 */
public class OpenListener implements ActionListener {
    @Autowired
    private MainFrame mainFrame;
    private String path;
    private ArrayList<Row> rowsList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Cell>> cellsMap = new HashMap<>();
    private ArrayList<Float> valueList = new ArrayList<>();

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(path);
        chooser.setFileFilter(new Filter());
        int r = chooser.showOpenDialog(mainFrame);

        if (r == JOptionPane.YES_OPTION){
            String selectedFile = chooser.getSelectedFile().getAbsolutePath();
            ReadPattern.readWithoutStyle(selectedFile, rowsList, cellsMap);

            loadToDataPanel(rowsList, cellsMap);
        }
    }

    private void loadToDataPanel(ArrayList<Row> rowsList, HashMap<Integer, ArrayList<Cell>> cellsMap){
        valueList.clear();

        //Считываем полученный контейнер строк
        for (Row r : rowsList){
            //Получаем из карты все ячейки соответствующие текущей строке
            ArrayList<Cell> cells = cellsMap.get(r.getRowNum());

            //Если это первая строка, то заполняем поля "Номер заказа" и "Заказчик"
            if (r.getRowNum() == 0) {
                for (int i = 0; i < cells.size(); i++) {
                    switch (i) {
                        case 1:
                            mainFrame.getCommonPanel().getDataPanel().getOrderLabel().setText(cells.get(i).getStringCellValue());
                            break;
                        case 3:
                            mainFrame.getCommonPanel().getDataPanel().getCustomerLabel().setText(cells.get(i).getStringCellValue());
                            break;
                    }
                }
            }

            //Если это вторая строка, то заполняем поля "Тип ротора" и "Количество стержней"
            else if (r.getRowNum() == 1) {
                for (int i = 0; i < cells.size(); i++) {
                    switch (i) {
                        case 1:
                            mainFrame.getCommonPanel().getDataPanel().getRotorTypeLabel().setText(cells.get(i).getStringCellValue());
                            break;
                        case 3:
                            mainFrame.getCommonPanel().getDataPanel().getRodsNumberLabel().setText(cells.get(i).getStringCellValue());

                            //Также отправляем количество стержней для рисования на панели
                            mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel().setRodsNumber(Integer.parseInt(cells.get(i).getStringCellValue()));
                            break;
                    }
                }
            }

            //Для всех остальных строк, начиная с пятой, заполняем контейнер значениями из столбца со значениями
            else if (r.getRowNum() >= 4){
                valueList.add((float) cells.get(1).getNumericCellValue());
            }
        }

        //Передаем контейнер с данными панели с графиком
        mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel().setValueList(valueList);
        //Перерисовываем панель
        mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel().repaint();

        //Составляем заголовок основного фрейма
        String testTitle = "Стержни ротора" + " - " + mainFrame.getCommonPanel().getDataPanel().getCustomerLabel().getText()
                + "_" + mainFrame.getCommonPanel().getDataPanel().getOrderLabel().getText();

        //Устанавливаем овый заголовок
        mainFrame.setTitle(testTitle);

        //Перерисовываем основное и запрещаем использовать пункты меню "Сохранить" и "Сохранить как"
        mainFrame.getFileMenu().getItem(3).setEnabled(false);
        mainFrame.getFileMenu().getItem(4).setEnabled(false);
        mainFrame.repaint();
    }

    public void setPath(String path) {
        this.path = path;
    }
}

class Filter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory()
                || f.getName().endsWith(".xls")
                || f.getName().endsWith(".XLS");
    }

    @Override
    public String getDescription() {
        return ".xls";
    }
}
