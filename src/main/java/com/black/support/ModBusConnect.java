package com.black.support;

import com.black.frames.COMSetupFrame;
import com.black.frames.MainFrame;
import net.wimpi.modbus.*;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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

    @Autowired
    private COMSetupFrame comSetupFrame;
    @Autowired
    private MainFrame mainFrame;

    private RunReadChanel firstChanel;
    private RunReadChanel secondChanel;
    private RunReadChanel thirdChanel;
    private RunReadChanel fourthChanel;

    @Autowired
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

    //Метод, который делает запрос и получает ответ
    public void makeRequestTakeResponse() {
        //Создаем для получения данных через COM-порт отдельный поток
        if(makeResponse == null) {
            makeResponse = new MakeResponse();
            executorService.execute(makeResponse);
            System.out.println(executorService);
        }
        else {
            changeChannelsIsStop(false);
            executorService.shutdown();
            executorService = Executors.newCachedThreadPool();
            executorService.execute(makeResponse);
            System.out.println(executorService);
        }

        //При удачном подключении уведомляем об этом в главном окне
        mainFrame.getCommonPanel().getStatusLabel().setText("Испытание начато");
    }

    public void stopRequestResponse(){
        //Остановка чтения данных поканально
        changeChannelsIsStop(true);
        actionOnBar.stopTimers();
        //При удачном подключении уведомляем об этом в главном окне
        System.out.println(executorService);
        mainFrame.getCommonPanel().getStatusLabel().setText("Испытание остановлено");
    }

    //метод, который закрывает подключение к COM-порту
    public void closeConnection(){
        int timerDelay = 1000;

        //Закрытие подключение к COM-порту и сброс флагов чтения данных в каналах
        Timer delay = new Timer(timerDelay, e -> {
            con.close();
            changeChannelsIsStop(false);
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

    //метод, который подсвечивает завершение испытания
    private void testEnd(){
        mainFrame.getCommonPanel().getStatusLabel().setText("Испытание законченно");

        //Таймер, который стирает надпись про завершение испытания
        Timer timer = new Timer(1500, e -> mainFrame.getCommonPanel().getStatusLabel().setText(""));

        //Единоразовый запус таймера
        timer.setRepeats(false);
        timer.start();
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
            firstChanel.setParameters(con, mainFrame, mainFrame.getCommonPanel().getFirstChanel(), 0x100, unitId);
            executorService.execute(firstChanel);

            //Создаем объект класса для считывания данных из второго канала
            secondChanel = new RunReadChanel();
            secondChanel.setParameters(con, mainFrame, null, 0x200, unitId);
            executorService.execute(secondChanel);

            //Создаем объект класса для считывания данных из третьего канала
            thirdChanel = new RunReadChanel();
            thirdChanel.setParameters(con, mainFrame, null, 0x300, unitId);
            executorService.execute(thirdChanel);

            //Создаем объект класса для считывания данных из четвертого канала
            fourthChanel = new RunReadChanel();
            fourthChanel.setParameters(con, mainFrame, null, 0x400, unitId);
            executorService.execute(fourthChanel);

            //Объект получающий объекты каналов и обрабатывающий значения из них
            actionOnBar.setBarVoltage(firstChanel); //Измерение напряжения
            actionOnBar.setSetBar(secondChanel); //Занести измерение
            actionOnBar.setDeleteBar(thirdChanel); //Сделать повторное измерение
        }
    }
}