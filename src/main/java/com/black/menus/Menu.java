package com.black.menus;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

/**
 * Created by Nick on 02.02.2016.
 */
public interface Menu {
    void addMenuItem(String name, ActionListener listener, JMenu menu);
    interface Some extends ActionListener, WindowListener {
        void menuMenu(int i);
    }
}
