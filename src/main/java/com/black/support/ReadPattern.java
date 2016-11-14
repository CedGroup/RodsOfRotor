package com.black.support;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Nick on 19.02.2016.
 */
public class ReadPattern {
    public static String read(String path, ArrayList<Row> rowsList, HashMap<Integer, ArrayList<Cell>> cellsMap,
                                     HashMap<Integer, ArrayList<CellStyle>> cellsStyleMap){
        //Считываем файл шаблона
        File xlsBook = new File(path);

        //Очищаем понтейнер строк
        rowsList.clear();

        Sheet readSheet = null;
        try {
            //Открываем поток для считывания
            FileInputStream inputStream = new FileInputStream(xlsBook);
            try {

                //Считываем книгу-шаблон
                Workbook workbook = new HSSFWorkbook(inputStream);

                //Стиваем лист-шаблон
                readSheet = workbook.getSheetAt(0);

                //Считываем все строки
                Iterator<Row> rows = readSheet.iterator();
                while (rows.hasNext()){
                    Row row = rows.next();
                    //Создаем контейнеры для заполнения значениями ячеек и стилями ячеек
                    ArrayList<Cell> cellsList = new ArrayList<>();
                    ArrayList<CellStyle> cellStyles = new ArrayList<>();

                    //Добавляем считанную строку для дальнейшей работы с ней
                    rowsList.add(row);

                    //Из каждой строки заносим значение и стиль ячеек в контейнеры
                    Iterator<Cell> cells = row.iterator();
                    while (cells.hasNext()){
                        Cell cell = cells.next();
                        cellStyles.add(cell.getCellStyle());
                        cellsList.add(cell);
                    }

                    //Заносим ячейки и их стили в карту для последующей работы с ними
                    cellsMap.put(row.getRowNum(), cellsList);
                    cellsStyleMap.put(row.getRowNum(), cellStyles);
                }
            }
            finally {
                //Закрываем потк считывания файла
                inputStream.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        //Возвращаем имя листа книги
        return readSheet.getSheetName();
    }

    public static void readWithoutStyle(String path, ArrayList<Row> rowsList,
                                        HashMap<Integer, ArrayList<Cell>> cellsMap) {
        //Считываем файл шаблона
        File xlsBook = new File(path);

        //Очищаем понтейнер строк
        rowsList.clear();

        Sheet readSheet = null;
        try {
            //Открываем поток для считывания
            FileInputStream inputStream = new FileInputStream(xlsBook);
            try {

                //Считываем книгу-шаблон
                Workbook workbook = new HSSFWorkbook(inputStream);

                //Стиваем лист-шаблон
                readSheet = workbook.getSheetAt(0);

                //Считываем все строки
                Iterator<Row> rows = readSheet.iterator();
                while (rows.hasNext()){
                    Row row = rows.next();
                    //Создаем контейнеры для заполнения значениями ячеек и стилями ячеек
                    ArrayList<Cell> cellsList = new ArrayList<>();

                    //Добавляем считанную строку для дальнейшей работы с ней
                    rowsList.add(row);

                    //Из каждой строки заносим значение и стиль ячеек в контейнеры
                    Iterator<Cell> cells = row.iterator();
                    while (cells.hasNext()){
                        Cell cell = cells.next();
                        cellsList.add(cell);
                    }

                    //Заносим ячейки и их стили в карту для последующей работы с ними
                    cellsMap.put(row.getRowNum(), cellsList);
                }
            }
            finally {
                //Закрываем потк считывания файла
                inputStream.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

    }
}
