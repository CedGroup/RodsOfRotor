package com.black;

import com.black.frames.MainFrame;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Nick on 02.02.2016.
 */
//Класс, который запускает все приложение
public class RunApplication {
    public static void main(String [] args){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        MainFrame frame = (MainFrame) context.getBean("mainFrameBean");
    }
}
