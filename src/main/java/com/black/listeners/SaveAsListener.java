package com.black.listeners;

import com.black.frames.MainFrame;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by Nick on 02.02.2016.
 */
public class SaveAsListener implements ActionListener {
    @Autowired
    private MainFrame mainFrame;

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new Filter());
        chooser.showSaveDialog(mainFrame);
    }
}

