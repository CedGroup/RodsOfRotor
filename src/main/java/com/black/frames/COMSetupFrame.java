package com.black.frames;

import com.black.listeners.OpenListener;
import com.black.listeners.SaveListener;
import com.black.panels.COMSetupPanel;
import com.black.panels.CommonPanel;
import com.black.support.ModBusConnect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Created by Nick_work on 04.02.2016.
 */
//Класс создающий фрейм, который содержит панель с настройками
public class COMSetupFrame extends JFrame {
    //Ссылка на объект класса панели для настройки
    private COMSetupPanel comSetupPanel;
    //Контейнер для получения сохраненных настроек для COM-порта
    private ArrayList<String> tokenList = new ArrayList<String>(Arrays.asList("0", "0", "0", "0"));

    private SaveListener saveListener;

    private OpenListener openListener;

    //Класс присоединения к COM-порту
    private ModBusConnect modBusConnect;

    //Отступ
    private final int WIDTHINDENT = 200;
    private final int HEIGHTINDENT = 200;


    public void init(){
        //Установка заголовка фрейма
        setTitle("Настройки COM-порта");

        //Установка расположения фрейма
        setLocation(WIDTHINDENT, HEIGHTINDENT);

        setParameters();

        //Создаем объект класс COMSetupPanel
        comSetupPanel = new COMSetupPanel(tokenList);

        //Установка наполнения фрейма
        add(comSetupPanel.$$$getRootComponent$$$());

        //Установка размеровфрейма под размер наполнения
        pack();

        //Запрет на изменение размера
        setResizable(false);

        //Добавление listener'а к кнопке "ок" расположенной на панели
        comSetupPanel.getOkButton().addActionListener(new OkListener(COMSetupFrame.this, comSetupPanel, modBusConnect));

        //Кнопка "Отмена" скрывает фрейм
        comSetupPanel.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                COMSetupFrame.this.setVisible(false);
                COMSetupFrame.this.setLocation(WIDTHINDENT, HEIGHTINDENT);
            }
        });
    }

    //Вернуть размер отступа по горизонтали
    public int getWidthIndent(){
        return WIDTHINDENT;
    }

    //Вернуть размер отступа по вертикали
    public int getHeightIndent(){
        return HEIGHTINDENT;
    }

    //Получение объекта класса присоединющегося к COM-порту
    public void setModBusConnect(ModBusConnect modBusConnect){
        this.modBusConnect = modBusConnect;
    }

    //Метод читающий сохраненные параметры из файла
    private void readFile(){
        try{
            //Получаем файл с сохраненными настройками
            File file = new File("comsetup.set");
            //Открываем стрим для чтения данных из файла
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                //Создаем пустую ссылку для принятия строки
                String gotString = null;

                tokenList.clear();

                //Пока есть данные в файле принимаем их
                while ((gotString = br.readLine()) != null){
                    //Создаем объект класса разбиваеющего строку на токены
                    StringTokenizer tokenizer = new StringTokenizer(gotString, ";");
                    //Разбиваем строку на токены и заносим в контейнер
                    while (tokenizer.hasMoreTokens()){
                        tokenList.add(tokenizer.nextToken());
                    }
                }
            }
            //В любом случае закрываем поток
            finally {
                br.close();
            }
        }
        catch (IOException exc){
            exc.printStackTrace();
        }
    }

    //Получение объекта класса сохраняющего протоколы испытаний
    public void setSaveListener(SaveListener saveListener) {
         this.saveListener = saveListener;
    }

    //Получение объекта класса открывающего протоколы испытаний
    public void setOpenListener(OpenListener openListener) {
        this.openListener = openListener;
    }

    private void setParameters(){
        //Чтание файла с настройками
        readFile();

        //Передача параметров из файла для настройки COM-пота
        modBusConnect.setParameters(tokenList.get(0),
                Integer.parseInt(tokenList.get(1)),
                Integer.parseInt(tokenList.get(2)));

        //Адрес из файла для сохранения архивов
        saveListener.setPath(tokenList.get(3));
        openListener.setPath(tokenList.get(3));
    }

    //Класс отвечающий за нажатие кнопки "ОК"
    private class OkListener implements ActionListener {
        private COMSetupFrame comSetupFrame;
        private COMSetupPanel comSetupPanel;
        private ModBusConnect modBusConnect;

        //При создании объекта класса присваиваим его внутренним полям экземпляры необходимых классов
        OkListener(COMSetupFrame comSetupFrame, COMSetupPanel comSetupPanel, ModBusConnect modBusConnect){
            this.comSetupFrame = comSetupFrame;
            this.comSetupPanel = comSetupPanel;
            this.modBusConnect = modBusConnect;
        }

        public void actionPerformed(ActionEvent e) {
            //Скрать окно
            comSetupFrame.setVisible(false);

            //Установить начальное положение фрейма на экране
            comSetupFrame.setLocation(comSetupFrame.getWidthIndent(), comSetupFrame.getHeightIndent());

            //Запись выбранных настроек в файл
            try{
                //Создаем файл
                File file = new File("comsetup.set");
                //Присваиваем файлу стрим записи
                FileWriter fw = new FileWriter(file.getName());

                try {
                    //Получаем объкты из полей
                    Object selectedPort = comSetupPanel.getPortNumber().getSelectedItem();
                    Object selectedBiteRate = comSetupPanel.getBitRate().getSelectedItem();
                    String selectedDevAddress = comSetupPanel.getDevAddress().getText();
                    String selectedPath = comSetupPanel.getPathField().getText();

                    //Устанавливаем параметры для соединения с
                    modBusConnect.setParameters(selectedPort.toString(),
                            Integer.parseInt(selectedBiteRate.toString()),
                            Integer.parseInt(selectedDevAddress));

                    //Записываем объекты в файл
                    fw.write(selectedPort.toString() + ";");
                    fw.write(selectedBiteRate.toString() + ";");
                    fw.write(selectedDevAddress + ";");
                    fw.write(selectedPath + ";");
                }
                finally {
                    //Закрываем поток
                    fw.close();
                    setParameters();
                }
            }
            catch(IOException exc){
                exc.printStackTrace();
            }
        }
    }
}