package com.matvey.perelman.grapher_for_android.ui.grapher.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.matvey.perelman.grapher_for_android.calculator2.calculator.executors.actors.Expression;
import com.matvey.perelman.grapher_for_android.model.BoolAsk;
import com.matvey.perelman.grapher_for_android.model.GraphType;
import com.matvey.perelman.grapher_for_android.ui.grapher.CoordinateSystem;

import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.GRAPH_WIDTH;
import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.HEIGHT;
import static com.matvey.perelman.grapher_for_android.ui.grapher.CoordinateSystem.mod;

public class Translation extends Graphic {
    private double[][] dataX;
    private double[][] dataY;
    private int endY;
    private final CoordinateSystem cs;
    private Expression<Double> yFunc;
    private FuncVariable<Double> xy;
    private FuncVariable<Double> yx;
    private FuncVariable<Double> yy;
    private int multiplyer = 2;
    private int max_lines;
    private final BoolAsk mousePressed;
    private boolean willNeedResize;
    private double cOX, cOY, cSX, cSY;
    public Translation(BoolAsk mousePressed, CoordinateSystem cs, int map_size, boolean feelsTime) {
        this.mousePressed = mousePressed;
        this.cs = cs;
        MAP_SIZE = map_size;
        this.feelsTime = feelsTime;
        super.type = GraphType.TRANSLATION;
    }

    @Override
    public void resize(double offsetX, double offsetY, double scaleX, double scaleY) {
        willNeedResize = willNeedResize || needResize
                || offsetX != this.offsetX || this.scaleX != scaleX
                || offsetY != this.offsetY || this.scaleY != scaleY
                || this.graph_height != HEIGHT || this.graph_width != GRAPH_WIDTH;
        cOX = offsetX;
        cOY = offsetY;
        cSX = scaleX;
        cSY = scaleY;
        if (mousePressed.ask() && !needResize || !willNeedResize) {
            return;
        }
        if (dataX.length / multiplyer < cs.MAX_LINES)
            resetMAP_SIZE();
        if (needResize || offsetX != this.offsetX || this.scaleX != scaleX
                || offsetY != this.offsetY || this.scaleY != scaleY || this.graph_height != HEIGHT || this.graph_width != GRAPH_WIDTH || this.max_lines != cs.MAX_LINES) {
            this.max_lines = cs.MAX_LINES;
            this.offsetY = offsetY;
            this.scaleY = scaleY;
            this.offsetX = offsetX;
            this.scaleX = scaleX;
            this.graph_width = GRAPH_WIDTH;
            this.graph_height = HEIGHT;
            needResize = false;
            double deltaX = cs.getDeltaX() / multiplyer;
            double deltaY = cs.getDeltaY() / multiplyer;
            endY = (int) (GRAPH_WIDTH / scaleX / deltaX) + 3;
            int endX = (int) (HEIGHT / scaleY / deltaY) + 3;
            double lineStart = -mod(offsetX, deltaX) + offsetX;
            double lineEnd = lineStart + (endY - 1) * deltaX;
            for (int n = 0; n < endX; ++n) {
                double[] mapX = dataX[n];
                double[] mapY = dataY[n];
                double y = -(mod(offsetY, deltaY) + (endX - n - 2) * deltaY) + offsetY;
                for (int i = 0; i < MAP_SIZE; ++i) {
                    double x = (i * lineStart + (MAP_SIZE - i - 1) * lineEnd) / (MAP_SIZE - 1);
                    var.setValue(x);
                    xy.setValue(y);
                    mapX[i] = func.calculate();
                    yx.setValue(x);
                    yy.setValue(y);
                    mapY[i] = yFunc.calculate();
                }
            }
            lineStart = -mod(offsetY, deltaY) + offsetY + deltaX;
            lineEnd = lineStart - (endX - 1) * deltaY;
            for (int n = 0; n < endY; ++n) {
                double[] mapX = dataX[n + endX];
                double[] mapY = dataY[n + endX];
                double x = (-mod(offsetX, deltaX) + n * deltaX) + offsetX;
                for (int i = 0; i < MAP_SIZE; ++i) {
                    double y = (i * lineStart + (MAP_SIZE - i - 1) * lineEnd) / (MAP_SIZE - 1);
                    var.setValue(x);
                    xy.setValue(y);
                    mapX[i] = func.calculate();
                    yx.setValue(x);
                    yy.setValue(y);
                    mapY[i] = yFunc.calculate();
                }
            }
            endY = endX + endY;
        }
    }

    public void setMultiplyer(int multiplyer) {
        if (this.multiplyer != multiplyer) {
            this.multiplyer = multiplyer;
            this.needResize = true;
        }
    }

    public int getMultiplyer() {
        return multiplyer;
    }

    @Override
    public void paint(Canvas g, Paint p) {
        p.setColor(color);
        for (int n = 0; n < endY; ++n) {
            double[] map = dataY[n];
            double[] xMap = dataX[n];
            for (int i = 0; i < MAP_SIZE - 1; ++i) {
                if (Double.isNaN(map[i] + map[i + 1] + xMap[i] + xMap[i + 1]))
                    continue;
                int y1 = (int) ((cOY - map[i]) * cSY);
                int y2 = (int) ((cOY - map[i + 1]) * cSY);
                if (y1 < 0 && y2 < 0 || y1 > HEIGHT && y2 > HEIGHT)
                    continue;
                int x1 = (int) ((-cOX + xMap[i]) * cSX);
                int x2 = (int) ((-cOX + xMap[i + 1]) * cSX);
                if (x1 < 0 && x2 < 0 || x1 > GRAPH_WIDTH && x2 > GRAPH_WIDTH)
                    continue;
                g.drawLine(x1, y1, x2, y2, p);
            }
        }
    }

    private void resetMAP_SIZE() {
        dataX = new double[cs.MAX_LINES * multiplyer][dataX[0].length];
        dataY = new double[cs.MAX_LINES * multiplyer][dataY[0].length];
        needResize = true;
    }

    public void updateY(Expression<Double> funcY, FuncVariable<Double> xy, FuncVariable<Double> yx, FuncVariable<Double> yy) {
        this.yFunc = funcY;
        this.xy = xy;
        this.yx = yx;
        this.yy = yy;
    }

    @Override
    public void setMAP_SIZE(int map_size) {
        int size = 1;
        if (dataX != null) {
            size = dataX[0].length;
            if (size == map_size)
                return;
        }
        while (size < map_size)
            size *= 2;
        this.MAP_SIZE = map_size;
        needResize = true;
        dataX = new double[cs.MAX_LINES * multiplyer][size];
        dataY = new double[cs.MAX_LINES * multiplyer][size];
    }
}
