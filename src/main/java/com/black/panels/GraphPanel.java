package com.black.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by Nick_work on 03.02.2016.
 */
public class GraphPanel extends JPanel {
    //Толщина линий
    private float strokeWidth = 2;

    //Отступ осей от границ панеди
    private float indentX = 20;
    private float indentY = 40;

    //Количество стержней
    private int rodsNumber;

    //Половинная длина отметки на осях
    int dashLength = 2;

    //Массив значений для рисования столбиков
    private ArrayList<Float> valueList = new ArrayList<>();

    //Массив значений расположения меток оси ОХ
    private ArrayList<Float> dashLabelX = new ArrayList<>();

    private ArrayList<Integer> equivalentPercentageValueContainer = new ArrayList<>();

    private Float maxBarHigh = 0F;

    //TODO цвета для фона и столбиков
    /*Color.HSBtoRGB(102, 204, 255); //Голубой
    Color.HSBtoRGB(192, 0, 0); //Кирпичный*/

    //Переопределенная функция, которая прорисовывает элементы на панели
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        //Получаем размер панели: высоту и ширину
        Dimension panelSize = this.getSize();
        double panelWidth = panelSize.width;
        double panelHeight = panelSize.height;

        //Исходя из полученных размеров строим оси кооринат
        Line2D lineY = new Line2D.Double( indentY, panelHeight - indentX, indentY, indentY);
        Line2D lineX = new Line2D.Double( indentY, panelHeight - indentX, panelWidth - indentX, panelHeight - indentX);

        //Делаем линию толще
        g2.setStroke(new BasicStroke(strokeWidth));

        //Рисуем линии
        g2.draw(lineX);
        g2.draw(lineY);

        //Точка начала координат для OX
        float lineXX1 = (float) lineX.getX1();
        float lineXY1 = (float) lineX.getY1();

        //Точка начала координат для OY
        float lineYX1 = (float) lineY.getX1();
        float lineYY1 = (float) lineY.getY1();

        //Длина оси ОХ и OY
        double lengthOX = panelWidth - indentX - indentY;
        double lengthOY = panelHeight - indentX - indentY;

        //Рисуем отметки по оси X
        drawMarksOX(lengthOX, lineXX1, lineXY1, g2);
        drawMarksOY(lengthOX, lengthOY, lineYX1, lineYY1, g2);
        drawBars(lengthOY, g2);
    }

    //Получаем количество стержней
    public void setRodsNumber(int rodsNumber){
        this.rodsNumber = rodsNumber;
    }

    //При завершении испытаний очистить контейнер
    public void clearContainer(){
        valueList.clear();
    }

    //Устанавливаем контейнер
    public void setValueList(ArrayList<Float> valueList) {
        this.valueList.clear();
        this.valueList.addAll(valueList);
    }

    //Метод рисует метки на оси ОХ
    private void drawMarksOX(double lengthOX, float lineXX1, float lineXY1, Graphics2D g2){
        //Предварительно очищаем контейнер для координат меток по оси ОХ
        dashLabelX.clear();

        if (rodsNumber > 0){
            //Расстояние между метками
            int delta = (int) lengthOX / rodsNumber;

            //Цикл, который добавляет метки на ось
            for (Integer i = 1; i <= rodsNumber; i++) {
                int rising = delta * i;

                //Создаем линию
                Line2D line = new Line2D.Double(lineXX1 + rising, lineXY1 - dashLength,
                        lineXX1 + rising, lineXY1 + dashLength);

                dashLabelX.add(lineXX1 + rising);

                //Рисуем линию и подпись для неё
                g2.draw(line);
                g2.drawString(i.toString(), lineXX1 + rising - 3, lineXY1 + 15);
            }
        }
    }

    public ArrayList<Integer> getEquivalentPercentageValueContainer() {
        return equivalentPercentageValueContainer;
    }


    //Метод для рисования отметок на оси OY и столбиков
    private void drawMarksOY(double lengthOX, double lengthOY, float lineYX1, float lineYY1, Graphics2D g2) {
        //Цикл, который добавляет метки на ось
        for (Integer i = 1; i <= 10; i++) {
            //Прирост по оси
            int rising = (int) (lengthOY / 10) * i;
            //Преобразование в десятичное число для подписи
            Integer toPrint = i * 10;

            //Создаем линию
            Line2D dashLine = new Line2D.Double(lineYX1 - dashLength, lineYY1 - rising,
                    lineYX1 + dashLength, lineYY1 - rising);

            //Линия сетки поперек графиика
            Line2D line = new Line2D.Double(lineYX1, lineYY1 - rising, lengthOX + indentY, lineYY1 - rising);

            //Рисуем линию и подпись для неё
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.draw(dashLine);
            g2.drawString(toPrint.toString(), lineYX1 - 30, lineYY1 - rising + 5);
            g2.setStroke(new BasicStroke(1));
            g2.draw(line);

            if (i == 10) {
                maxBarHigh = (float) rising;
            }
        }
    }

    //Метод для рисования столбцов
    private void drawBars(double lengthOY, Graphics2D g2){
        equivalentPercentageValueContainer.clear();
        if (valueList.size() > 0) {
            //Берем значение из контейнера
            Float barHigh = 0F;
            Float maxValue = 0F;
            Float koef = 0F;
            int indexOfMax = 0;

            //Находим наибольшее значение
            for (int i = 0; i < valueList.size(); i++) {
                float f = valueList.get(i);
                if (f > maxValue) {
                    maxValue = f;
                    indexOfMax = i;

                    koef = (float) maxBarHigh / maxValue;
                    barHigh = maxBarHigh;
                }
            }

            Float forString = 100F;

            //Рисуем столбец
            for (int i = 0; i < valueList.size(); i++) {
                if (i == indexOfMax) {
                    //Указываем координаты и значения высоты и ширины для прорисовки столбца
                    Rectangle2D rect = new Rectangle2D.Float(dashLabelX.get(i) - dashLength * 2, (float)
                            lengthOY - barHigh + indentY, dashLength * 4, barHigh);
                    g2.fill(rect);
                    g2.draw(rect);

                    if (barHigh == 0) {
                        //Рисуем подпись над столбиком
                        g2.drawString("0", dashLabelX.get(i) - dashLength * 2, (float) lengthOY + indentY - 10);
                    }
                    else {
                        //Рисуем подпись над столбиком
                        g2.drawString(String.format("%.0f", forString), dashLabelX.get(i) - dashLength * 2, indentX + 10);
                    }

                    equivalentPercentageValueContainer.add(Math.round(forString));
                } else {
                    //Берем из контейнера значение и умножаем на коэффициент расчитаный
                    //относительно максимального значения
                    Float insBarHigh = valueList.get(i) * koef;
                    float forStrFlo = (forString*insBarHigh)/barHigh;

                    Rectangle2D rect = new Rectangle2D.Float(dashLabelX.get(i) - dashLength * 2,
                            (float) lengthOY - insBarHigh + indentY, dashLength * 4, insBarHigh);
                    g2.fill(rect);
                    g2.draw(rect);

                    if (barHigh == 0) {
                        //Рисуем подпись над столбиком
                        g2.drawString("0", dashLabelX.get(i) - dashLength * 2, (float) lengthOY + indentY - 10);
                    }
                    else {
                        //Рисуем подпись над столбиком
                        g2.drawString(String.format("%.0f", forStrFlo), dashLabelX.get(i) - dashLength * 4,
                                barHigh - insBarHigh + 30);
                    }

                    equivalentPercentageValueContainer.add(Math.round(forStrFlo));
                }
            }
        }
    }
}
