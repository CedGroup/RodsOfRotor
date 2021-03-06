package com.black.frames;

import com.black.menus.EditMenu;
import com.black.menus.FileMenu;
import com.black.panels.CommonPanel;
import com.black.support.ModBusConnect;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;


/**
 * Created by Nick on 02.02.2016.
 */
//Класс создающий основное окно приложения
public class MainFrame extends JFrame {
    private final int WIDTH = 1200;
    private final int HEIGHT = 600;
    private JMenuBar menuBar = new JMenuBar();
    private CommonPanel commonPanel = new CommonPanel();

    @Autowired
    private ModBusConnect modBusConnect;

    private FileMenu fileMenu;

    private String title = "Стержни ротора";

    public void init(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exc){
            exc.printStackTrace();
        }

        //Делаем окно видимым
        setVisible(true);

        //Присваиваем название окна
        setTitle(title);

        //Установка размеров окна
        setSize(WIDTH, HEIGHT);

        //Установка расположения окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        setLocation(screenWidth / 6, screenHeight / 7);

        //Устанавливаем стандартное закрытие окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Добавляем строку меню к фрейму
        setJMenuBar(menuBar);

        //Добавляем основную панель в главное окно
        setContentPane(commonPanel.$$$getRootComponent$$$());

        //Запускаем испытание нажатием кнопки на главной панели
        commonPanel.getStartTestButton().addActionListener(e -> {
            //Открываем COM-порт и запрашиваем данные из каналов
            modBusConnect.makeRequestTakeResponse();

            //Запрещаем изменять внесенные даныне
            commonPanel.getDataPanel().getChangeButton().setEnabled(false);
            setMenusEnable(false);
            commonPanel.getDisconnectToCOMButton().setEnabled(false);

            commonPanel.getDeleteLastBar().setEnabled(true);

            //Очищаем контейнер с данными высот столбцов при запуске нового испытания
            commonPanel.getAdditionalPanel().getGraphPanel().clearContainer();
        });

        //Останавливаем испытание нажатием кнопки на главной панели
        commonPanel.getStopTestButton().addActionListener(e -> {
            //Останавличаем запрос данных
            modBusConnect.stopRequestResponse();
            commonPanel.getDisconnectToCOMButton().setEnabled(true);
            commonPanel.getDataPanel().getChangeButton().setEnabled(true);

            setMenusEnable(true);
            commonPanel.getDeleteLastBar().setEnabled(false);
        });

        commonPanel.getConnectToCOMButton().addActionListener(e -> {
            commonPanel.getConnectToCOMButton().setVisible(false);
            commonPanel.getDisconnectToCOMButton().setVisible(true);

            commonPanel.getStartTestButton().setEnabled(true);
            commonPanel.getStopTestButton().setEnabled(true);


            commonPanel.getCOMStatus().setText("COM: подключено");

            modBusConnect.connectToCOM();
        });

        commonPanel.getDisconnectToCOMButton().addActionListener(e -> {
            modBusConnect.closeConnection();

            commonPanel.getConnectToCOMButton().setVisible(true);
            commonPanel.getDisconnectToCOMButton().setVisible(false);

            commonPanel.getStartTestButton().setEnabled(false);
            commonPanel.getStopTestButton().setEnabled(false);

            commonPanel.getCOMStatus().setText("COM: отключено");
        });
    }

    //Добавляет меню "Файл" в строку меню
    public void setFileMenu(FileMenu menu){
        menuBar.add(menu);
        fileMenu = menu;
    }

    //Добавляет меню "Правка" в строку меню
    public void setEditMenu(EditMenu menu){
        menuBar.add(menu);
    }

    //Возвращаем объект класса CommonPanel
    public CommonPanel getCommonPanel() {
        return commonPanel;
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    //Установка доступными\недоступными пунктов меню
    public void setMenusEnable(Boolean isEnable){
        fileMenu.getItem(0).setEnabled(isEnable);
        fileMenu.getItem(1).setEnabled(isEnable);
    }
}
