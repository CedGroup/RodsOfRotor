package com.black.listeners;

import com.black.frames.COMSetupFrame;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by Nick on 02.02.2016.
 */
//Класс-listener, отслеживающий Открытия фрейма настроек COM-порт
public class OptionsListener implements ActionListener {
    @Autowired
    //Создание пустой ссылки класса COMSetupFrame
    private COMSetupFrame comSetupFrame;

    //При нажатии на пункт меню открываем фрейм
    public void actionPerformed(ActionEvent e) {
        //Делаем фрейм видимым
        comSetupFrame.setVisible(true);
    }
}
