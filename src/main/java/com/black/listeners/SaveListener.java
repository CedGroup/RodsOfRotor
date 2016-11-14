package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.support.ActionOnBar;
import com.black.support.ReadPattern;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nick on 02.02.2016.
 */
public class SaveListener implements ActionListener {
    private String name;
    private ArrayList<Float> valueList;
    private ActionOnBar actionOnBar;
    private Boolean isSave = true;
    private Boolean isFirstCreated = true;
    private String fileExtension = ".xls";

    private MainFrame mainFrame;

    private String readSheet;
    private ArrayList<Row> rowsList = new ArrayList<Row>();
    private HashMap<Integer, ArrayList<Cell>> cellsMap = new HashMap<Integer, ArrayList<Cell>>();
    private HashMap<Integer, ArrayList<CellStyle>> cellsStyleMap = new HashMap<Integer, ArrayList<CellStyle>>();
    private File file;
    private String path = "C:\\";

    //При нажатии на кнопку "Сохранить" в графе меню, сохранить данный файл
    public void actionPerformed(ActionEvent e) {
        save();
        isSave = true;
        ifEndIsStar();
    }

    //Метод, который сохраняем данные испытания в файл с именем "Заказчик-номер заказа"
    public void save() {

        //Составление имени для названия файла. При первом проходе используются поля "зказчик" и "номер заказа"
        if (isFirstCreated) {
            String customerLabel = mainFrame.getCommonPanel().getDataPanel().getCustomerLabel().getText();

            //Создание папки для архивов
            file = new File(path + "\\Протоколы испытаний\\" + customerLabel);

            //Папка, где будут храниться архивы
            file.mkdirs();

            //Берем имена всех файлов в папке архивов, для того, чтобы проверить на уникальность
            String [] nameList = file.list();

            //Создаем имя из внесенных данных
            name = mainFrame.getCommonPanel().getDataPanel().getCustomerLabel().getText() + "_"
                    + mainFrame.getCommonPanel().getDataPanel().getOrderLabel().getText() + fileExtension;

            //Считываем шаблон отчета для заполнения
            readSheet = ReadPattern.read("..\\RodsOfRotor\\Pattern\\шаблон.xls", rowsList, cellsMap, cellsStyleMap);

            //Проверяем имя на уникальность
            existName(nameList, file.getAbsolutePath());
        }

        //Записываем данные в книгу
        writeFunction();

        JOptionPane.showMessageDialog(mainFrame, "Сохранено");
    }

    //Метод, устанавливающий флаг о том сохранен файл или нет
    public void setIsSave(Boolean isSave) {
        this.isSave = isSave;
    }

    //Возвращаем значение флага сохранения
    public Boolean getIsSave() {
        return isSave;
    }

    //Получаем экземпляр основного фрейма
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    //Получаем экземпляр класса добавляющего значения в контейнер
    public void setActionOnBar(ActionOnBar actionOnBar) {
        this.actionOnBar = actionOnBar;
    }

    //Метод, который снимает отметку о том что файл не сохранен
    private void ifEndIsStar(){
        if (mainFrame.getTitle().endsWith("*")) {
            mainFrame.setTitle(mainFrame.getTitle().substring(0, mainFrame.getTitle().length() - 1));
        }
    }

    //Метод, зписывающий файл
    private void writeFunction(){
        //Открываем поток для записи
        try {
            //Создаем новый экземпляр книги
            Workbook workbook = new HSSFWorkbook();
            //Создаем поток, который внесет данные в новый файл .xls
            FileOutputStream outputStream = new FileOutputStream(file);
            //Создаем новый лист в книге
            Sheet sheet = workbook.createSheet(readSheet);

            try {
                //Получаем контейнер с данными
                valueList = actionOnBar.getValueList();

                //Вносим данные шаблона в новую книгу
                for (Row r : rowsList) {
                    //Берем значение строки из шаблона
                    Row row = sheet.createRow(r.getRowNum());

                    //Берем ячейки и стили ячеек соответствующие строке
                    ArrayList<Cell> cells = new ArrayList<Cell>(cellsMap.get(row.getRowNum()));
                    ArrayList<CellStyle> cellStyles = new ArrayList<CellStyle>(cellsStyleMap.get(row.getRowNum()));

                    int count = 0;

                    //Перебираем ячейки
                    for (int i = 0; i < cells.size(); i++){
                        //Локальнче переменные для работы
                        String value = "";
                        Cell cell = null;
                        CellStyle cellStyle = null;

                        //Берем тип ячейки
                        switch (cells.get(i).getCellType()){
                            //Создаем её как пустую с определенным стилем
                            case Cell.CELL_TYPE_BLANK:
                                if (row.getRowNum() == 0) {
                                    switch (count) {
                                        case 0:
                                            value = mainFrame.getCommonPanel().getDataPanel().getOrderLabel().getText();
                                            count++;
                                            break;
                                        case 1:
                                            value = mainFrame.getCommonPanel().getDataPanel().getCustomerLabel().getText();
                                            count++;
                                            break;
                                        default:
                                            value = "";
                                            break;
                                    }
                                }
                                if (row.getRowNum() == 1){
                                    switch (count) {
                                        case 0:
                                            value = mainFrame.getCommonPanel().getDataPanel().getRotorTypeLabel().getText();
                                            count++;
                                            break;
                                        case 1:
                                            value = mainFrame.getCommonPanel().getDataPanel().getRodsNumberLabel().getText();
                                            count++;
                                            break;
                                        default:
                                            value = "";
                                            break;
                                    }
                                }

                                cell = row.createCell(i);
                                cell.setCellValue(value);

                                cellStyle = workbook.createCellStyle();
                                cellStyle.cloneStyleFrom(cellStyles.get(i));

                                cell.setCellStyle(cellStyle);
                                break;

                            //Создаем её заполненную строками с определенным стилем
                            case Cell.CELL_TYPE_STRING:
                                value = cells.get(i).getStringCellValue();

                                cell = row.createCell(i);
                                cell.setCellValue(value);

                                cellStyle = workbook.createCellStyle();
                                cellStyle.cloneStyleFrom(cellStyles.get(i));

                                cell.setCellStyle(cellStyle);
                                break;
                        }
                    }
                }

                //Вносим измерянные данные в книгу
                for (int i = 0; i < valueList.size(); i++){
                    int nextRow = i + rowsList.size();
                    Row row = sheet.createRow(nextRow);
                    CellStyle style = workbook.createCellStyle();
                    style.cloneStyleFrom(cellsStyleMap.get(rowsList.size()-1).get(0));

                    row.createCell(0).setCellStyle(style);
                    row.getCell(0).setCellValue(i+1);
                    row.createCell(1).setCellStyle(style);
                    row.getCell(1).setCellValue(valueList.get(i));
                }

                //Закрываем поток, отмечаем, что файл сохранен и что ему присвоено уникально имя
            } finally {
                workbook.write(outputStream);
                outputStream.close();
                isSave = true;
                ifEndIsStar();
                isFirstCreated = false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Метод, присваивающий уникальное имя
    private void existName(String [] list, String path) {
        int count = 1;

        //Если папка пустая создаем
        if (list.length == 0) {
            file = new File(path + "\\" + name);
        }

        //Проверяем список существующих имен файлов на совпадение с созданным именем
        for (String s : list) {
            if (s.equals(name)) {

                //Разбиваем строку для проверки есть ли в имени число, показывающее повторение
                for (char c : name.toCharArray()) {
                    if (Character.isDigit(c)) {
                        count = Character.getNumericValue(c);
                        ++count;
                    }
                }

                //Если подходящее имя под паттерн существует, то
                Pattern pattern = Pattern.compile(".+\\d{1,2}\\W(xls)");
                Matcher matcher = pattern.matcher(name);

                //Заменяем его новым уникальным именем
                if (matcher.matches()) {
                    //Создаем новый файл с уникальным именем
                    name = name.substring(0, name.length() - 5) + count + fileExtension;
                    file = new File(path + "\\" + name);
                } else {
                    //Создаем новый файл с уникальным именем
                    name = name.substring(0, name.length() - 4) + count + fileExtension;
                    file = new File(path + "\\" + name);
                }
                //Если совпадений с существующем именем нет, то присваиваем файлу текущее имя
            } else {
                file = new File(path + "\\" + name);
            }
        }
    }

    //Установка пути сохранения протокола из внешнего файла
    public void setPath(String path){
        this.path = path;
    }
}
