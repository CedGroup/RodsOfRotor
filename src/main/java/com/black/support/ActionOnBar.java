package com.black.support;

import com.black.listeners.TimerSetBarListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Nick on 15.02.2016.
 */

public class ActionOnBar {
    private ArrayList<Float> valueList = new ArrayList<Float>(); //Контейнер хранения значений высот столбцов

    private Timer timerSetBar; //Таймер чтения нажатия кнопки "Записать"

    @Autowired
    private TimerSetBarListener timerSetBarListener;

    //Метод для инициализации таймеров и получения экземпляра основного окна
    public void init(){
        //Установка таймера на считывание кнопки "Записать значение"
        timerSetBarListener.setValueList(valueList);
        synchronized (timerSetBarListener) {
            timerSetBar = new Timer(20, timerSetBarListener);
        }
    }

    //Получаем объект считывающий данные из канала, который устанавливает значение высоты столбца в контейнр
    public void setSetBar(RunReadChanel setBar, RunReadChanel deleteBar) {
        timerSetBarListener.setSetBar(setBar);
        //timerSetBarListener.setDeleteBar(deleteBar);
        timerSetBar.start();
    }

    //Получаем объект считывающий данные из канала, который передает значение высоты столбца
    public void setBarVoltage(RunReadChanel barVoltage) {
        timerSetBarListener.setBarVoltage(barVoltage);
    }

    public void stopTimers(){
        timerSetBar.stop();
    }

    public ArrayList<Float> getValueList() {
        return valueList;
    }
}
