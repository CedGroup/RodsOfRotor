package com.black.support;

import com.black.listeners.TimerDeleteBarListener;
import com.black.listeners.TimerSetBarListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Nick on 15.02.2016.
 */

public class ActionOnBar {
    private ArrayList<Float> valueList = new ArrayList<>(); //Контейнер хранения значений высот столбцов

    private Timer timerSetBar; //Таймер чтения нажатия кнопки "Записать"

    private Timer timerDeleteBar; //Таймер чтения нажатия кнопки "Перезапись"

    @Autowired
    private TimerSetBarListener timerSetBarListener;

    @Autowired
    private TimerDeleteBarListener timerDeleteBarListener;

    private int delayTime = 10;

    //Метод для инициализации таймеров и получения экземпляра основного окна
    public void init(){
        //Установка таймера на считывание кнопки "Записать значение"
        timerSetBarListener.setValueList(valueList);
        timerDeleteBarListener.setValueList(valueList);
        timerSetBar = new Timer(delayTime, timerSetBarListener);
        timerDeleteBar = new Timer(delayTime, timerDeleteBarListener);
    }

    //Получаем объект считывающий данные из канала, который устанавливает значение высоты столбца в контейнр
    public void setSetBar(RunReadChanel setBar) {
        timerSetBarListener.setSetBar(setBar);
        timerSetBar.start();
    }

    public void setDeleteBar(RunReadChanel deleteBar) {
        timerDeleteBarListener.setDeleteBar(deleteBar);
        timerDeleteBar.start();
    }

    //Получаем объект считывающий данные из канала, который передает значение высоты столбца
    public void setBarVoltage(RunReadChanel barVoltage) {
        timerSetBarListener.setBarVoltage(barVoltage);
    }

    public void stopTimers(){
        timerSetBar.stop();
        timerDeleteBar.stop();
    }

    public void startTimers(){
        timerSetBar.start();
        timerDeleteBar.start();
    }

    public ArrayList<Float> getValueList() {
        return valueList;
    }
}
