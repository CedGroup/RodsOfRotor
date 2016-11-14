package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.panels.GraphPanel;
import com.black.support.RunReadChanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Nick on 24.03.2016.
 */
public class TimerDeleteBarListener implements ActionListener {
    private Float valueToDelete = 0F; //Объект для хранения значения нажатия кнопки "Стереть"
    private Float value; //Объект для хранения значения высоты столбика

    private Float referenceValueHigh = 3F; //Эталонное значение верхнего нажатия кнопки
    private Float referenceValueLow = 1F; //Эталонное значение нижнего нажатия кнопки

    private boolean deleteBarFlag; //Флаг нажатия значения "Стереть"

    private ArrayList<Float> valueList; //Контейнер хранения значений высот столбцов

    private MainFrame mainFrame;
    private GraphPanel graphPanel;
    private SaveListener saveListener;

    private RunReadChanel deleteBar;
    private RunReadChanel barVoltage;

    public TimerDeleteBarListener(MainFrame mainFrame, SaveListener saveListener){
        this.mainFrame = mainFrame;
        this.saveListener = saveListener;

        graphPanel = mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel();
    }

    public void actionPerformed(ActionEvent e) {
        //Считываем значения из каналов
        value = barVoltage.getValue();
        valueToDelete = deleteBar.getValue();

        //Если выполняются условия удалить последнее значение из контейнера
        if (valueToDelete > referenceValueHigh && !deleteBarFlag && valueList.size() > 0) {
            //Удаляем последнее значение из контейнера
            if(!deleteBarFlag) {
                valueList.remove(valueList.size() - 1);
            }

            //Устанавличаем флаг нажатия кнопка
            deleteBarFlag = true;

            //Отмечаем, что файл не сохранен
            if (!mainFrame.getTitle().endsWith("*")){
                mainFrame.setTitle(mainFrame.getTitle() + "*");
                saveListener.setIsSave(false);
            }

            //Перерисовываем график и главный фрейм
            graphPanel.setValueList(valueList);
            graphPanel.repaint();
            mainFrame.repaint();
        }

        //Если кнопка не нажата сбрасываем флаг её нажатия
        else if (valueToDelete <= referenceValueLow && deleteBarFlag) {
            deleteBarFlag = false;
        }
    }

    public void setDeleteBar(RunReadChanel deleteBar) {
        this.deleteBar = deleteBar;
    }

    public void setBarVoltage(RunReadChanel barVoltage) {
        this.barVoltage = barVoltage;
    }

    public void setValueList(ArrayList<Float> valueList) {
        this.valueList = valueList;
    }
}
