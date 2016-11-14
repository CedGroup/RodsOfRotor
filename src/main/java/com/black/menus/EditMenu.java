package com.black.menus;

import com.black.listeners.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by Nick on 02.02.2016.
 */
public class EditMenu extends JMenu implements Menu {
    @Autowired
    private OptionsListener optionsListener;

    //Добавляет имя меню и его пункты
    public void init(){
        setText("Правка");

        addMenuItem("Настройки", optionsListener, this);
    }

    //Добавляет меню пункта
    public void addMenuItem(String name, ActionListener listener, JMenu menu) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(listener);
        menu.add(item);
    }
}
