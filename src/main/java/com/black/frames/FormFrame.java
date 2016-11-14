package com.black.frames;

import com.black.listeners.SaveListener;
import com.black.panels.CommonPanel;
import com.black.panels.DataPanel;
import com.black.panels.FormPanel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Nick on 02.02.2016.
 */
public class FormFrame extends JFrame {
    //Создает объект класс FormPanel
    private FormPanel formPanel = new FormPanel();

    @Autowired
    //Основной фрейм
    private MainFrame mainFrame;

    //Общая панель в основном фрейме
    private CommonPanel commonPanel;

    //Отступы фрейма от края
    private int widthIndent = 150;
    private int heightIndent = 150;

    @Autowired
    //Класс сохраняющий протоколы
    private SaveListener saveListener;

    //Метод, который создаёт наполнение фрейма
    public void init(){
        //Название фрейма
        setTitle("Форма");

        //Наполнение фрейма
        setContentPane(formPanel.$$$getRootComponent$$$());

        //Запрет на изменени размера фрейма
        setResizable(false);

        //Расположение окна при открытии
        setLocation(widthIndent, heightIndent);

        //Получение экземплаяра класса CommonPanel
        commonPanel = mainFrame.getCommonPanel();

        //Кнопка "Отмена" скрывает фрейм
        formPanel.getCancelButton().addActionListener(e -> {
            setVisible(false);
            destroy();
        });

        //Создаем listener'а для кнопки "Ок"
        final OkButtonListener okButtonListener = new OkButtonListener(mainFrame, formPanel, this, saveListener);

        //Кнопка "Ок" заполняет панель с данными в главном фрейме
        formPanel.getOkButton().addActionListener(okButtonListener);

        //При нажатии кнопки "Изменить" вызывается этот фрейм
        commonPanel.getDataPanel().getChangeButton().addActionListener(e -> {
            FormFrame.this.setVisible(true);
            okButtonListener.setIsChangeButtonPress(true);
        });

        //Компановка панели во фрейме
        pack();

        //Действие при закрытии окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Очищаем поля в панели
                destroy();
                //Вызов метода суперкласса
                super.windowClosed(e);
            }
        });
    }


    //Метод возвращающий первоначальные настроки окна и панели
    protected void destroy() {
        formPanel.getCustomerField().setText("");
        formPanel.getOrderField().setText("");
        formPanel.getRodsNumberField().setText("0");
        formPanel.getRotorTypeField().setText("");
        setLocation(widthIndent, heightIndent);
        dispose();
    }
}

//Класс отслеживающий нажатие на кнопку "Ок"
class OkButtonListener implements ActionListener {
    //Панель с формой для заполнения новых данных для испытаний
    private FormPanel formPanel;

    //Флаг отслеживающий была ли нажата кнопка "Изменить"
    private Boolean isChangeButtonPress = new Boolean(false);

    //Осносвной фрейм
    private MainFrame mainFrame;

    //Фрейм в котором находится панель с формой
    private FormFrame formFrame;

    //Общая панель, которая назодится в основном фрейме
    private CommonPanel commonPanel;

    //Класс, сохраняющий протоколы испытаний
    private SaveListener saveListener;

    //Конструктор, который получает все необходимые объекты для работты
    public OkButtonListener(MainFrame mainFrame, FormPanel formPanel, FormFrame formFrame, SaveListener saveListener){
        this.formPanel = formPanel;
        this.commonPanel = mainFrame.getCommonPanel();
        this.mainFrame = mainFrame;
        this.formFrame = formFrame;
        this.saveListener = saveListener;
    }

    //При нажатии на кнопку "ОК"
    public void actionPerformed(ActionEvent e) {
        //Получаем номер заказа
        String orderText = formPanel.getOrderField().getText();

        //Получаем имя заказчика
        String customerText = formPanel.getCustomerField().getText();

        //Получаем тип заказчика
        String rotorTypeText = formPanel.getRotorTypeField().getText();

        //Получаем количество стержней в роторе
        String rodsNumberText = formPanel.getRodsNumberField().getText();

        //Получаем экземпляр панели, в которую мы будем заносить полученные выше данные
        DataPanel dataPanel = commonPanel.getDataPanel();

        //Если этот фрейм вызвался при на жатии пункта меню "Новый"
        if (!isChangeButtonPress) {
            //Устанавливаем данные на панели с данными в центральном фрейме
            //Номер заказа
            dataPanel.setOrderLabel(checkEmptyField(orderText));
            //Заказчик
            dataPanel.setCustomerLabel(checkEmptyField(customerText));
            //Тип ротора
            dataPanel.setRotorTypeLabel(checkEmptyField(rotorTypeText));
            //Количество стержней в роторе
            //Для панели данных
            dataPanel.setRodsNumberLabel(rodsNumberText);
            //Для прорисовки графика
            commonPanel.getAdditionalPanel().getGraphPanel().setRodsNumber(Integer.parseInt(rodsNumberText));

            //Убираем окно
            formFrame.destroy();

            //Отмечаем, что новый файл не сохранен
            saveListener.setIsSave(false);

            //Перерисовывает оси координат
            commonPanel.getAdditionalPanel().getGraphPanel().repaint();
            mainFrame.repaint();

            //Разрешаем изменять значения кнопкой "Изменить"
            mainFrame.getCommonPanel().getDataPanel().getChangeButton().setEnabled(true);

            //Составляем заголовок основного фрейма
            String testTitle = "Стержни ротора" + " - " + commonPanel.getDataPanel().getCustomerLabel().getText()
                    + "_" + commonPanel.getDataPanel().getOrderLabel().getText() + "*";

            //Устанавливаем овый заголовок
            mainFrame.setTitle(testTitle);
        }
        //Если этот фрейм вызвался при на жатии кнопки "Изменить"
        else {
            //Меняем данные на панели с данными в центральном фрейме если произошли изменения
            //Номер заказа
            dataPanel.setOrderLabel(checkChangeField(orderText, dataPanel.getOrderLabel()));
            //Заказчик
            dataPanel.setCustomerLabel(checkChangeField(customerText, dataPanel.getCustomerLabel()));
            //Тип ротора
            dataPanel.setRotorTypeLabel(checkChangeField(rotorTypeText, dataPanel.getRotorTypeLabel()));
            //Количество стержней в роторе
            //Для панели данных
            commonPanel.getDataPanel().setRodsNumberLabel(checkNumberOfRods(rodsNumberText, dataPanel.getRodsNumberLabel()));
            //Для прорисовки графика
            commonPanel.getAdditionalPanel()
                    .getGraphPanel()
                    .setRodsNumber(Integer.parseInt(checkNumberOfRods(rodsNumberText,
                            dataPanel.getRodsNumberLabel())));

            //Убираем окно
            formFrame.destroy();

            //Перерисовывает оси координат
            commonPanel.getAdditionalPanel().getGraphPanel().repaint();
            mainFrame.repaint();

            String testTitle = "Стержни ротора" + " - " + commonPanel.getDataPanel().getCustomerLabel().getText()
                    + "_" + commonPanel.getDataPanel().getOrderLabel().getText() + "*";

            //Составляем заголовок основного фрейма
            mainFrame.setTitle(testTitle);

            //Сбрасываем флаг нажатия кнопки "Изменить"
            isChangeButtonPress = false;
        }

        mainFrame.getFileMenu().getItem(3).setEnabled(true);
        mainFrame.getFileMenu().getItem(4).setEnabled(true);
    }

    //Получаем объект флага, показывающего нажата ли кнопке "Изменить"
    public void setIsChangeButtonPress(Boolean isChangeButtonPress){
        this.isChangeButtonPress = isChangeButtonPress;
    }

    //Метод, проверяющий заполнение поля при создании новой формы
    private String checkEmptyField(String str) {
        //Если ытрока пустая возвращает стандартное значение
        if (str.equals("")) {
            return "Не заполнено";
        }
        //Если нет возвращает строку
        else {
            return str;
        }
    }

    //Метод проверяющий, изменились ли строковые переменные
    //Если да, то изменившееся поле заносим в панель данных
    private String checkChangeField(String str, JLabel label) {
        //Если ытрока пустая возвращает стандартное значение
        if (!str.equals("")) {
            return str;
        }
        else {
            return label.getText();
        }
    }

    //Метод проверяющий, изменились ли поле с количеством стержней
    //Если да, то передаем изменение, если нет - возвращаем старое значение
    private String checkNumberOfRods(String str, JLabel label){
        if (!str.equals("0")) {
            return str;
        }
        else {
            return label.getText();
        }
    }
}