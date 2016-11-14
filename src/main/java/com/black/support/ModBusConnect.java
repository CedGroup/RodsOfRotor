package com.black.support;

import com.black.frames.COMSetupFrame;
import com.black.frames.MainFrame;
import net.wimpi.modbus.*;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.util.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Nick on 05.02.2016.
 */

//Класс который совершает подключение к прибору по ModBus
public class ModBusConnect {
    private SerialConnection con; //the connection
    private SerialParameters params;

    /* Variables for storing the parameters */
    private String portName = "COM1"; //the name of the serial port to be used
    private int unitId = 1; //the unit identifier we will be talking to
    private int baudRate = 9600;

    private COMSetupFrame comSetupFrame;
    private MainFrame mainFrame;

    private RunReadChanel firstChanel;
    private RunReadChanel secondChanel;
    private RunReadChanel thirdChanel;
    private RunReadChanel fourthChanel;

    private ActionOnBar actionOnBar;

    private ExecutorService executorService;

    private MakeResponse makeResponse;

    public ModBusConnect(){
        //2. Set master identifier
        ModbusCoupler.getReference().setUnitID(1);

        //3. Setup serial parameters
        params = new SerialParameters();
        params.setPortName(portName);
        params.setBaudRate(baudRate);
        params.setDatabits(8);
        params.setParity("None");
        params.setStopbits(1);
        params.setEncoding("rtu");
        params.setEcho(false);

        //Создаем кэш потоков
        executorService = Executors.newCachedThreadPool();
    }

    public void connectToCOM(){
        try {
            //Создаем COM-порт и открываем подключение
            con = new SerialConnection(params);
            con.open();
        } catch (Exception ex) {
            ex.printStackTrace();
            con.close();
            JOptionPane.showMessageDialog(comSetupFrame, "Проверьте номер COM-порта и повторите попытку",
                    "Ошибка связи с COM-портом", JOptionPane.ERROR_MESSAGE);
            System.out.println("Выкинуло openConnection");

            mainFrame.getCommonPanel().getConnectToCOMButton().setVisible(true);
            mainFrame.getCommonPanel().getDisconnectToCOMButton().setVisible(false);
            mainFrame.getCommonPanel().getStartTestButton().setEnabled(false);
            mainFrame.getCommonPanel().getStopTestButton().setEnabled(false);
            mainFrame.getCommonPanel().getCOMStatus().setText("COM: отключено");

            mainFrame.setMenusEnable(true);
            mainFrame.repaint();
        }
    }

    //Метод, который открывает соединение с COM-портом, делает запрос и получает ответ
    public void makeRequestTakeResponse() {
        //Создаем для получения данных через COM-порт отдельный поток
        if(makeResponse == null) {
            makeResponse = new MakeResponse();
            executorService.execute(makeResponse);
        }
        else {
            changeChannelsIsStop(false);
            executorService.execute(makeResponse);
        }
    }

    public void stopRequestResponse(){
        //Остановка чтения данных поканально
        changeChannelsIsStop(true);
        actionOnBar.stopTimers();
        //При удачном подключении уведомляем об этом в главном окне
        mainFrame.getCommonPanel().getStatusLabel().setText("Испытание остановлено");
    }

    //метод, который закрывает подключение к COM-порту
    public void closeConnection(){
        int timerDelay = 1000;

        //Закрытие подключение к COM-порту и сброс флагов чтения данных в каналах
        Timer delay = new Timer(timerDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.close();
                changeChannelsIsStop(false);
            }
        });

        //Запуск таймера для закрытия COM-порта
        delay.start();
        delay.setRepeats(false);
        testEnd();
    }

    //Установка праметров COM-порта из вне
    public void setParameters(String portName, int baudRate, int unitId){
        params.setPortName(portName);
        params.setBaudRate(baudRate);

        this.unitId = unitId;
    }

    //Получение экземпляра класса, который создает фрейм для размещения настроек
    //для COM-порта
    public void setComSetupFrame(COMSetupFrame comSetupFrame) {
        this.comSetupFrame = comSetupFrame;
    }

    //Получает объект главного фрейма
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void setActionOnBar(ActionOnBar actionOnBar) {
        this.actionOnBar = actionOnBar;
    }

    //метод, который подсвечивает завершение испытания
    private void testEnd(){
        mainFrame.getCommonPanel().getStatusLabel().setText("Испытание законченно");

        //Таймер, который стирает надпись про завершение испытания
        Timer timer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainFrame.getCommonPanel().getStatusLabel().setText("");
            }
        });

        //Единоразовый запус таймера
        timer.setRepeats(false);
        timer.start();
    }

    //Создание потока для каналов с отображением значений
    private void makeChannelThreadWithLabel(RunReadChanel channel, SerialConnection con,
                                           JLabel label, int regAddress, int devAddress){
        //Задаем параметры для канала
        channel.setParameters(con, mainFrame, label, regAddress, devAddress);
        //Запускаем считывание отдельным потоком
        executorService.execute(channel);
    }

    private void makeChannelThreadWithOutLabel(RunReadChanel channel, SerialConnection con,
                                              int regAddress, int devAddress){
        //Задаем параметры для канала
        channel.setParameters(con, mainFrame, regAddress, devAddress);
        //Запускаем считывание отдельным потоком
        executorService.execute(channel);
    }

    private void changeChannelsIsStop(boolean isStop) {
        firstChanel.setIsStop(isStop);
        secondChanel.setIsStop(isStop);
        thirdChanel.setIsStop(isStop);
        fourthChanel.setIsStop(isStop);
    }

    private class MakeResponse implements Runnable {
        public void run() {
            //Создаем объект класса для считывания данных из первого канала
            firstChanel = new RunReadChanel();
            makeChannelThreadWithLabel(firstChanel, con, mainFrame.getCommonPanel().getFirstChanel(),
                    0x100, unitId);

            //Создаем объект класса для считывания данных из второго канала
            secondChanel = new RunReadChanel();
            makeChannelThreadWithOutLabel(secondChanel, con, 0x200, unitId);

            //Создаем объект класса для считывания данных из третьего канала
            thirdChanel = new RunReadChanel();
            makeChannelThreadWithOutLabel(thirdChanel, con, 0x300, unitId);

            //Создаем объект класса для считывания данных из четвертого канала
            fourthChanel = new RunReadChanel();
            makeChannelThreadWithOutLabel(fourthChanel, con, 0x400, unitId);

            //Объект получающий объекты каналов и обрабатывающий значения из них
            actionOnBar.setBarVoltage(firstChanel); //Измерение напряжения
            actionOnBar.setSetBar(secondChanel, thirdChanel); //Занести измерение

            //При удачном подключении уведомляем об этом в главном окне
            mainFrame.getCommonPanel().getStatusLabel().setText("Испытание начато");
        }
    }
}