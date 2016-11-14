package com.black.support;

import com.black.frames.MainFrame;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.SerialConnection;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nick on 11.02.2016.
 */
public class RunReadChanel implements Runnable {
    private SerialConnection connection; //Подключение к ком порту

    private JLabel label; //страка, в которой высвечивается измерянное знаяение из канала
    private MainFrame mainFrame;
    private int reference; //Начальный регистр чтения данных
    private int count = 2; //Количество регистров для чтения
    private int unitId; //Адрес устройства
    private boolean isStop; //Флаг остановки/запуска чтения данных

    private Float value = 0F;

    //Установка основных параметров для запуска чтения данных
    public void setParameters(SerialConnection connection, MainFrame mainFrame, JLabel label,
                              int reference, int unitId){
        this.connection = connection;
        this.reference = reference;
        this.mainFrame = mainFrame;
        this.label = label;
        this.unitId = unitId;
    }

    //Метод читающий данные из канала
    public void run() {
        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(reference, count);
        request.setUnitID(unitId);
        request.setHeadless();

        try {
            //Делаем запросы к регистрам
            ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection); //Инициализируем передачу данных
            transaction.setTransDelayMS(50); //Задержка между запросом-ответом
            transaction.setRequest(request); //Установка запроса для передачи данных
            transaction.setRetries(1);

            while (!isStop) {
                transaction.execute();
                ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();

                String doubleWord = "";

                //Из двух двухбайтных чисел создаем одно число с плавающей запятой
                for (int n = 0; n < response.getWordCount(); n++) {
                    if (n == 0) {
                        doubleWord += MakeWord.make(response.getRegisterValue(n));
                    }
                    if (n == 1) {
                        if (!doubleWord.equals("")) {
                            doubleWord += MakeWord.make(response.getRegisterValue(n));
                            value = makeDouble(doubleWord);
                            if (value <= 0.1) {
                                value = 0F;
                            }
                        } else {
                            value = 0F;
                        }
                        //Если есть где отображать данные, отображаем
                        if (label != null) {
                            Float textValue = value * 20;
                            label.setText(textValue.toString());
                        }
                    }
                }
            }
        } catch (ModbusIOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Нет ответа от модуля",
                    "Ошибка подключения к модулю", JOptionPane.ERROR_MESSAGE);
            System.out.println("Выкинуло ModbusIOException");

        } catch (ModbusSlaveException ex) {
            ex.printStackTrace();
            System.out.println("Выкинуло ModbusSlaveException");
        } catch (ModbusException ex) {
            ex.printStackTrace();
            System.out.println("Выкинуло ModbusException");
        }
    }

    //Установка флага остановки/запуска чтения
    public void setIsStop(Boolean isStop){
        this.isStop = isStop;
    }

    //Метод, который возвращает мгновенное измерянное значение
    public Float getValue() {
        return value;
    }

    //Преобразуем четырех байтное шестнадцатиричное число в значение с плавающей запятой
    private Float makeDouble(String s) {
        Long i = Long.parseLong(s, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        return f;
    }
}
