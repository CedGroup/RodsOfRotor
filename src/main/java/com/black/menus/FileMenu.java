package com.black.menus;

import com.black.frames.FormFrame;
import com.black.frames.MainFrame;
import com.black.listeners.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Nick on 02.02.2016.
 */
public class FileMenu extends JMenu implements Menu {
    private NewFileListener newFileListener;
    private OpenListener openListener;
    private SaveListener saveListener;
    private SaveAsListener saveAsListener;
    @Autowired
    private MainFrame mainFrame;
    @Autowired
    private FormFrame formFrame;

    //Добавляет имя меню и его пункты
    public void init(){
        this.setText("Файл");

        addMenuItem("Новый", newFileListener, this);
        addMenuItem("Открыть", openListener, this);
        this.addSeparator();
        addMenuItem("Сохранить", saveListener, this);
        addMenuItem("Сохранить как...", saveAsListener, this);
        this.addSeparator();
        addMenuItem("Выход", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (saveListener.getIsSave())
                    System.exit(0);
                else {
                    int aswn = JOptionPane.showConfirmDialog(mainFrame, "Данный файл не сохранен.\n " +
                                    "Вы действительно хотите создать новый файл испытаний?",
                            "Предупреждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (aswn == JOptionPane.YES_OPTION){
                        //Устанавличаем фрейм формы видимым
                        saveListener.save();
                    }
                    else {
                        System.exit(0);
                    }
                }
            }
        }, this);
    }

    //Добавляет меню пункта
    public void addMenuItem(String name, ActionListener listener, JMenu menu) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(listener);
        menu.add(item);
    }

    //Получает объект класса NewFileListener
    public void setNewFileListener(NewFileListener newFileListener){
        this.newFileListener = newFileListener;
    }

    //Получает объект класса OpenListener
    public void setOpenListener(OpenListener openListener) {
        this.openListener = openListener;
    }

    //Получает объект класса SaveListener
    public void setSaveListener(SaveListener saveListener) {
        this.saveListener = saveListener;
    }

    //Получает объект класса SaveAsListener
    public void setSaveAsListener(SaveAsListener saveAsListener) {
        this.saveAsListener = saveAsListener;
    }
}
