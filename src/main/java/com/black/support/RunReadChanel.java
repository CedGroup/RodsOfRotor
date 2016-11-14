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
import java.util.ArrayList;

/**
 * Created by Nick on 11.02.2016.
 */
public class RunReadChanel implements Runnable {
    private ModbusSerialTransaction transaction; //Передача данных
    private ReadMultipleRegistersRequest request; //Запрос к slave-устройству
    private ReadMultipleRegistersResponse response; //Ответ slave-устройства
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

    //Установка основных параметров для запуска чтения данных
    public void setParameters(SerialConnection connection, MainFrame mainFrame,
                              int reference, int unitId){
        this.connection = connection;
        this.reference = reference;
        this.mainFrame = mainFrame;
        this.unitId = unitId;
        this.label = null;
    }

    //Метод читающий данные из канала
    public void run() {
        //Подготовка запроса
        request = new ReadMultipleRegistersRequest(reference, count);
        request.setUnitID(unitId);
        request.setDataLength(20);
        request.setHeadless();
        request.setReference(reference);

        try {
            //Делаем запросы к регистрам
            while (!isStop) {
                transaction = new ModbusSerialTransaction(connection); //Инициализируем передачу данных
                transaction.setTransDelayMS(30); //Задержка между запросом-ответом
                transaction.setRequest(request); //Установка запроса для передачи данных

                transaction.execute();
                response = (ReadMultipleRegistersResponse) transaction.getResponse();

                String doubleWord = "";

                //Из двух двухбайтных чисел создаем одно число с плавающей запятой
                for (int n = 0; n < response.getWordCount(); n++) {
                    if (n == 0) {
                        doubleWord += makeWord(response.getRegisterValue(n));
                    }
                    if (n == 1) {
                        if(!doubleWord.equals("")) {
                            doubleWord += makeWord(response.getRegisterValue(n));
                            value = makeDouble(doubleWord);
                            if (value <= 0.1){
                                value = 0F;
                            }
                        }
                        else {
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
            ex.setEOF(true);
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

    //Метод создает шестнадцатиричное представление для двухбайтного десятичного числа
    private String makeWord(int word){
        //получаем входное число
        int s = word;
        //Создаем внутреннюю переменную
        String helpWord = "";
        //Массив символов char
        ArrayList<Character> charList = new ArrayList<Character>();

        //переменная для вычисления остатка
        int rest = 0;

        //Пока s > 0 вычисляем эквивалентное шестнадцатиричное число
        //Результат получается "перевернутым"
        while (s > 0) {
            rest = s%16;
            s = s/16;
            //Полученное значение остатка преобразовываем в символ
            char c = Character.forDigit(rest, Character.MAX_RADIX);
            charList.add(Character.toUpperCase(c));
        }

        //Устанавливаем нормальное следование чисел
        for (int i = 0; i < charList.size() / 2; i++){
            char first = charList.get(i);
            int point = charList.size() - 1 - i;
            char last = charList.get(point);

            charList.set(i, last);
            charList.set(point, first);
        }

        //Конкастенируем переменные из контейнера в одну строку
        for (char c : charList){
            helpWord += c;
        }

        return helpWord;
    }

    //Преобразуем четырех байтное шестнадцатиричное число в значение с плавающей запятой
    private Float makeDouble(String s) {
        Long i = Long.parseLong(s, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        return f;
    }
}
