package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.panels.GraphPanel;
import com.black.support.RunReadChanel;
import com.black.support.ZeroFilter;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import java.util.Collections;

/**
 * Created by Nick on 24.03.2016.
 */
public class TimerSetBarListener implements ActionListener {
    private Float valueToSet = 0F; //Объект для хранения значения нажатия кнопки "Записать"
    private Float value; //Объект для хранения значения высоты столбика

    private Float referenceValueHigh = 4F; //Эталонное значение верхнего нажатия кнопки
    private Float referenceValueLow = 2F; //Эталонное значение верхнего нажатия кнопки

    private boolean setBarFlag; //Флаг нажатия кнопки "Записать"

    private int rodsNumber; //Количество стержней
    private ArrayList<Float> valueList; //Контейнер хранения значений высот столбцов

    private ArrayList<Float> intermediateValuesList = new ArrayList<>();

    @Autowired
    private MainFrame mainFrame;
    private GraphPanel graphPanel;

    @Autowired
    private SaveListener saveListener;

    @Autowired
    private TimerDeleteBarListener timerDeleteBarListener;

    private RunReadChanel setBar;
    private RunReadChanel barVoltage;

    private String stringRodsNumber; //Количество стержней в строковом значении

    public void init(){
        graphPanel = mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel();
    }

    public void actionPerformed(ActionEvent e) {
        //Считываем значения из каналов
        value = barVoltage.getValue();
        valueToSet = setBar.getValue();

        float valueFromLabel = 0;

        if (!mainFrame.getCommonPanel().getFirstChanel().getText().equals("")) {
            valueFromLabel = Float.parseFloat(mainFrame.getCommonPanel().getFirstChanel().getText());
        }

        //Присваиваем значние количества стержней
        stringRodsNumber = mainFrame.getCommonPanel().getDataPanel().getRodsNumberLabel().getText();

        //Если строковое значение не пустое преобразовать в число
        if (stringRodsNumber != null && !stringRodsNumber.equals("")) {
            rodsNumber = Integer.parseInt(stringRodsNumber);
        }

        if (valueToSet > referenceValueHigh && valueList.size() < rodsNumber) {
            if (!setBarFlag) {
                setBarFlag = true;
            }
            intermediateValuesList.add(value);
        }

        if (valueToSet <= referenceValueLow && valueFromLabel <= 0 && setBarFlag){
            setBarFlag = false;
            timerDeleteBarListener.setReMeasure(false);
            Float endedValue = Collections.max(intermediateValuesList) * 20;
            valueList.add(endedValue);
            intermediateValuesList.clear();
            repaintFoo();
        }
    }

    public void setBarVoltage(RunReadChanel barVoltage) {
        this.barVoltage = barVoltage;
    }

    public void setSetBar(RunReadChanel setBar) {
        this.setBar = setBar;
    }

    public void setValueList(ArrayList<Float> valueList) {
        this.valueList = valueList;
    }

    public void clearContainer(){
        valueList.clear();
    }

    private void repaintFoo(){
        //Отмечаем, что файл не сохранен
        if (!mainFrame.getTitle().endsWith("*")) {
            mainFrame.setTitle(mainFrame.getTitle() + "*");
            saveListener.setIsSave(false);
        }

        //Перерисовываем график и главный фрейм
        graphPanel.setValueList(valueList);
        graphPanel.repaint();
        mainFrame.repaint();
    }
}
