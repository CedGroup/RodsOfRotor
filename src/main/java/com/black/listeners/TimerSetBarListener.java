package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.panels.GraphPanel;
import com.black.support.RunReadChanel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Nick on 24.03.2016.
 */
public class TimerSetBarListener implements ActionListener {
    private Float valueToSet = 0F; //Объект для хранения значения нажатия кнопки "Записать"
    private Float value; //Объект для хранения значения высоты столбика
    private Float valueToBlock = 0F;

    private Float referenceValueHigh = 3F; //Эталонное значение верхнего нажатия кнопки
    private Float referenceValueLow = 1.4F; //Эталонное значение верхнего нажатия кнопки

    private boolean setBarFlag; //Флаг нажатия кнопки "Записать"

    private int rodsNumber; //Количество стержней
    private ArrayList<Float> valueList; //Контейнер хранения значений высот столбцов

    @Autowired
    private MainFrame mainFrame;
    private GraphPanel graphPanel;

    @Autowired
    private SaveListener saveListener;

    private RunReadChanel setBar;
    private RunReadChanel deleteBar;
    private RunReadChanel barVoltage;

    private Timer timer; //Задержка времени измерения

    private static final int delayTime = 200;

    private String stringRodsNumber; //Количество стержней в строковом значении

    private ActionListener addListener;

    private ActionListener deleteListener;

    public void init(){
        graphPanel = mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel();

        this.mainFrame.getCommonPanel().getDeleteLastBar().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int r = JOptionPane.showConfirmDialog(mainFrame, "Вы точно хотите удалить последнее измерение?",
                        "Удаление последнего измерения",JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (r == JOptionPane.OK_OPTION && valueList.size() > 0) {
                    valueList.remove(valueList.size() - 1);
                    //Перерисовываем график и главный фрейм
                    graphPanel.setValueList(valueList);
                    graphPanel.repaint();
                    mainFrame.repaint();
                }
            }
        });

        timer =  new Timer(delayTime, null);
        timer.setRepeats(false);

        addListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Добавляем значение в контейнер
                if (!setBarFlag) {
                    valueList.add(value);
                    System.out.println("addListener");
                }

                setBarFlag = true;
                System.out.println("addListener setBarFlag = " + setBarFlag);

                repaintFoo();
            }
        };

        deleteListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(valueList.size() > 0) {
                    valueList.remove(valueList.size() - 1);
                    System.out.println("deleteListener");
                }

                valueList.add(value);

                setBarFlag = true;
                System.out.println("deleteListener setBarFlag = " + setBarFlag);

                repaintFoo();
            }
        };
    }


    public void actionPerformed(ActionEvent e) {
        //Считываем значения из каналов
        value = barVoltage.getValue();
        valueToSet = setBar.getValue();
        valueToBlock = deleteBar.getValue();

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

        if (valueToSet > referenceValueHigh && valueFromLabel > 0 && valueToBlock < referenceValueLow && !setBarFlag && valueList.size() < rodsNumber && !timer.isRunning()) {
            timerAddListenerFoo(addListener);
            System.out.println("valueFromLabel > 0");
        }
        else if (valueToBlock >= referenceValueLow && valueFromLabel > 0 && !setBarFlag && valueList.size() <= rodsNumber && !timer.isRunning()) {
            timerAddListenerFoo(deleteListener);
            System.out.println("valueToBlock >= referenceValueLow && valueFromLabel > 0");
        }


        if (valueToSet > referenceValueHigh && valueFromLabel == 0 && valueToBlock < referenceValueLow && !setBarFlag && valueList.size() < rodsNumber && !timer.isRunning()) {
            timerAddListenerFoo(addListener);
            System.out.println("valueFromLabel == 0");
        }
        else if (valueToBlock >= referenceValueLow && valueFromLabel == 0 && !setBarFlag && valueList.size() <= rodsNumber && !timer.isRunning()) {
            timerAddListenerFoo(deleteListener);
            System.out.println("valueToBlock >= referenceValueLow && valueFromLabel == 0");
        }

        if (valueToSet <= referenceValueHigh && valueToBlock <= referenceValueLow && setBarFlag && !timer.isRunning()){
            setBarFlag = false;
            System.out.println("setBarFlag = false;\n");
            timer.stop();
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

    public void setDeleteBar(RunReadChanel deleteBar){
        this.deleteBar = deleteBar;
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

    private void timerAddListenerFoo(ActionListener listener){
        ActionListener [] listeners = timer.getActionListeners();

        for (ActionListener listenerIns: listeners){
            timer.removeActionListener(listenerIns);
        }

        timer.addActionListener(listener);
        timer.start();
    }
}
