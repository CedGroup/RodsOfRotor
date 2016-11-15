package com.black.listeners;

import com.black.frames.FormFrame;
import com.black.frames.MainFrame;
import com.black.support.ModBusConnect;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nick on 02.02.2016.
 */
//Класс отслеживающий нажатия на пункт меню "Новый"
public class NewFileListener implements ActionListener {
    @Autowired
    private FormFrame formFrame;
    @Autowired
    private MainFrame mainFrame;
    @Autowired
    private SaveListener saveListener;
    @Autowired
    private TimerSetBarListener timerSetBarListener;
    //При нажатии на пункт меню, каждый раз создается новая форма
    public void actionPerformed(ActionEvent e) {
        if (saveListener.getIsSave()) {
            prepareNewForm();
        }
        else {
            int aswn = JOptionPane.showConfirmDialog(formFrame, "Данный файл не сохранен.\n " +
                            "Вы действительно хотите создать новый файл испытаний?",
                    "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (aswn == JOptionPane.YES_OPTION){
                prepareNewForm();
            }
        }
    }

    private void prepareNewForm(){
        //Устанавличаем фрейм формы видимым
        formFrame.setVisible(true);

        //При создани нового фрейма очищаем контейнер со значениями для прошлых столбиков
        mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel().clearContainer();

        timerSetBarListener.clearContainer();

        //Перерисоваваем график и главное окно
        mainFrame.getCommonPanel().getAdditionalPanel().getGraphPanel().repaint();
        mainFrame.repaint();
    }
}
