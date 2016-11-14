package com.black.listeners;

import com.black.frames.MainFrame;
import com.black.panels.GraphPanel;
import com.black.support.RunReadChanel;
import com.black.support.ZeroFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Nick on 24.03.2016.
 */
public class TimerDeleteBarListener implements ActionListener {
    private Float valueToDelete = 0F; //Объект для хранения значения нажатия кнопки "Стереть"

    private Float referenceValueHigh = 3F; //Эталонное значение верхнего нажатия кнопки
    private Float referenceValueLow = 2F; //Эталонное значение нижнего нажатия кнопки

    private boolean deleteBarFlag; //Флаг нажатия значения "Стереть"

    private ArrayList<Float> valueList; //Контейнер хранения значений высот столбцов

    @Autowired
    private MainFrame mainFrame;
    private GraphPanel graphPanel;

    @Autowired
    private SaveListener saveListener;

    private RunReadChanel deleteBar;

    private ZeroFilter zeroFilter;

    private boolean reMeasure = false;

    public void init(){
        graphPanel = mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel();

        zeroFilter = new ZeroFilter();

        this.mainFrame.getCommonPanel().getDeleteLastBar().addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(mainFrame, "Вы точно хотите удалить последнее измерение?",
                    "Удаление последнего измерения", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.OK_OPTION && valueList.size() > 0) {
                valueList.remove(valueList.size() - 1);
                //Перерисовываем график и главный фрейм
                graphPanel.setValueList(valueList);
                graphPanel.repaint();
                mainFrame.repaint();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        valueToDelete = deleteBar.getValue();

        valueToDelete = zeroFilter.filtered(valueToDelete);

        if (valueToDelete > referenceValueHigh && !deleteBarFlag && valueList.size() > 0 && !reMeasure){
                if (!deleteBarFlag){
                    valueList.remove(valueList.size() - 1);
                    deleteBarFlag = true;
                    repaintFoo();
                }
        }

        if (valueToDelete <= referenceValueLow && deleteBarFlag){
            deleteBarFlag = false;
        }
    }

    public void setDeleteBar(RunReadChanel deleteBar) {
        this.deleteBar = deleteBar;
    }

    public void setValueList(ArrayList<Float> valueList) {
        this.valueList = valueList;
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

    public void setReMeasure(boolean reMeasure){
        this.reMeasure = reMeasure;
    }
}
