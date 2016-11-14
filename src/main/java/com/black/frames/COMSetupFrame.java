package com.black.frames;

import com.black.listeners.OpenListener;
import com.black.listeners.SaveListener;
import com.black.panels.COMSetupPanel;
import com.black.panels.CommonPanel;
import com.black.support.ComSetup;
import com.black.support.ModBusConnect;
import org.springframework.beans.factory.annotation.Autowired;

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

    private ComSetup comSetup = new ComSetup();

    @Autowired
    private SaveListener saveListener;

    @Autowired
    private OpenListener openListener;

    @Autowired
    private ModBusConnect modBusConnect;

    //Отступ
    private final int WIDTHINDENT = 200;
    private final int HEIGHTINDENT = 200;

    private String fileName = "comSetup.set";

    public void init(){
        //Установка заголовка фрейма
        setTitle("Настройки COM-порта");

        //Установка расположения фрейма
        setLocation(WIDTHINDENT, HEIGHTINDENT);

        readFile();

        //Создаем объект класс COMSetupPanel
        comSetupPanel = new COMSetupPanel(comSetup);

        //Установка наполнения фрейма
        add(comSetupPanel.$$$getRootComponent$$$());

        //Установка размеровфрейма под размер наполнения
        pack();

        //Запрет на изменение размера
        setResizable(false);

        //Добавление listener'а к кнопке "ок" расположенной на панели
        comSetupPanel.getOkButton().addActionListener(new OkListener(COMSetupFrame.this));

        //Кнопка "Отмена" скрывает фрейм
        comSetupPanel.getCancelButton().addActionListener(e -> {
            COMSetupFrame.this.setVisible(false);
            COMSetupFrame.this.setLocation(WIDTHINDENT, HEIGHTINDENT);
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

    //Метод читающий сохраненные параметры из файла
    private void readFile(){
        try{
            File file = new File(fileName);
            if (!file.exists()){
                comSetup.setPath("0");
                comSetup.setDevAddress(0);
                comSetup.setBitRate(0);
                comSetup.setPort(0);

                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));

                outputStream.writeObject(comSetup);
            }

            //Получаем файл с сохраненными настройками
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));

            comSetup = (ComSetup) inputStream.readObject();

            try {
                modBusConnect.setParameters(comSetup.getPort(), comSetup.getBitRate(), comSetup.getDevAddress());
                saveListener.setPath(comSetup.getPath());
                openListener.setPath(comSetup.getPath());
            }

            //В любом случае закрываем поток
            finally {
                inputStream.close();
            }
        }
        catch (IOException exc){
            exc.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    //Класс отвечающий за нажатие кнопки "ОК"
    private class OkListener implements ActionListener {
        private COMSetupFrame comSetupFrame;

        //При создании объекта класса присваиваим его внутренним полям экземпляры необходимых классов
        OkListener(COMSetupFrame comSetupFrame){
            this.comSetupFrame = comSetupFrame;
        }

        public void actionPerformed(ActionEvent e) {
            //Скрать окно
            comSetupFrame.setVisible(false);

            //Установить начальное положение фрейма на экране
            comSetupFrame.setLocation(comSetupFrame.getWidthIndent(), comSetupFrame.getHeightIndent());

            //Запись выбранных настроек в файл
            try{
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));

                try {
                    //Получаем объкты из полей

                    comSetup.setPort(comSetupPanel.getPortNumber().getSelectedItem());
                    comSetup.setBitRate(comSetupPanel.getBitRate().getSelectedItem());
                    comSetup.setDevAddress(comSetupPanel.getDevAddress().getText());
                    comSetup.setPath(comSetupPanel.getPathField().getText());

                    //Устанавливаем параметры для соединения с
                    modBusConnect.setParameters(comSetup.getPort(), comSetup.getBitRate(), comSetup.getDevAddress());

                    //Записываем объекты в файл
                    outputStream.writeObject(comSetup);
                }
                finally {
                    //Закрываем поток
                    outputStream.close();
                    readFile();
                }
            }
            catch(IOException exc){
                exc.printStackTrace();
            }
        }
    }
}