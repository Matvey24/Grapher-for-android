package com.mathway.perelman.grapher_for_android.ui.grapher.graphics;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.FuncVariable;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.executors.actors.BinaryActor;
import com.mathway.perelman.grapher_for_android.model.BoolAsk;
import com.mathway.perelman.grapher_for_android.model.GraphType;
import com.mathway.perelman.grapher_for_android.model.MainModel;

import static com.mathway.perelman.grapher_for_android.controller.ModelUpdater.GRAPH_WIDTH;
import static com.mathway.perelman.grapher_for_android.controller.ModelUpdater.HEIGHT;

public class Implicit extends Graphic {
    private static final int VOID = Color.argb(0, 0, 0, 0);
    private float[][] data;
    private Bitmap data1;
    private int yMAP_SIZE;
    private FuncVariable<Double> yVar;
    private int c;
    private double sensitivity = 1;
    private int type;
    private static final int SPECTRUM = 0;
    private static final int INEQUALITY = 1;
    private static final int EQUALITY = 2;
    public int viewType;
    public static final int RAY_SPECTRUM = 0;
    public static final int INFRARED_IMAGER = 1;
    private final BoolAsk mousePressed;
    private boolean willNeedResize;
    private double cOX, cOY, cSX, cSY;
    private final float[] tmp = {1, 1, 1};
    private final Matrix matrix;

    public Implicit(BoolAsk mousePressed, int map_size, boolean feelsTime) {
        this.mousePressed = mousePressed;
        this.feelsTime = feelsTime;
        MAP_SIZE = map_size;
        super.type = GraphType.IMPLICIT;
        viewType = INFRARED_IMAGER;
        matrix = new Matrix();
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
        if(data == null)
            setMAP_SIZE(MAP_SIZE);
        willNeedResize = false;
        this.graph_width = GRAPH_WIDTH;
        this.graph_height = HEIGHT;
        needResize = false;
        double deltaX = GRAPH_WIDTH / scaleX / MAP_SIZE;
        double deltaY = HEIGHT / scaleY / yMAP_SIZE;
        if (type == SPECTRUM) {
            if (viewType == INFRARED_IMAGER)
                for (int i = 0; i < MAP_SIZE; ++i) {
                    for (int j = 0; j < yMAP_SIZE; ++j) {
                        var.setValue(offsetX + i * deltaX);
                        yVar.setValue(offsetY - j * deltaY);
                        tmp[0] = (float) ((360 * 2f / 3) * (1 - 1 / (1 + Math.exp(-func.calculate() * sensitivity))));
                        data1.setPixel(i, j, Color.HSVToColor(0xb6, tmp));
                    }
                }
            else if (viewType == RAY_SPECTRUM)
                for (int i = 0; i < MAP_SIZE; ++i) {
                    for (int j = 0; j < yMAP_SIZE; ++j) {
                        var.setValue(offsetX + i * deltaX);
                        yVar.setValue(offsetY - j * deltaY);
                        tmp[0] = (float) ((360 * 5f / 6) * (1 / (1 + Math.exp(-func.calculate() * sensitivity))));
                        data1.setPixel(i, j, Color.HSVToColor(0xb6, tmp));
                    }
                }
        } else if (type == INEQUALITY) {
            for (int i = 0; i < MAP_SIZE; ++i) {
                for (int j = 0; j < yMAP_SIZE; ++j) {
                    var.setValue(offsetX + i * deltaX);
                    yVar.setValue(offsetY - j * deltaY);
                    data1.setPixel(i, j, ((func.calculate() != 0) ? c : VOID));
                }
            }
        } else {
            boolean nsign;
            for (int i = 0; i < MAP_SIZE; ++i) {
                for (int j = 0; j < yMAP_SIZE; ++j) {
                    var.setValue(offsetX + i * deltaX);
                    yVar.setValue(offsetY - j * deltaY);
                    data[i][j] = func.calculate().floatValue();
                    data1.setPixel(i, j, VOID);
                }
            }
            for (int i = 0; i < MAP_SIZE; ++i) {
                for (int j = 0; j < yMAP_SIZE; ++j) {
                    nsign = data[i][j] > 0;
                    if ((i < MAP_SIZE - 1 && (data[i + 1][j] < 0) == nsign) && Math.abs(data[i + 1][j] - data[i][j]) < sensitivity) {
                        data1.setPixel(i, j, color);
                        data1.setPixel(i + 1, j, color);
                    }
                    if ((j < yMAP_SIZE - 1 && (data[i][j + 1] < 0 == nsign)) && Math.abs(data[i][j + 1] - data[i][j]) < sensitivity) {
                        data1.setPixel(i, j + 1, color);
                        data1.setPixel(i, j, color);
                    }
                    if ((i > 0 && (data[i - 1][j] < 0) == nsign) && Math.abs(data[i - 1][j] - data[i][j]) < sensitivity) {
                        data1.setPixel(i - 1, j, color);
                        data1.setPixel(i, j, color);
                    }
                    if ((j > 0 && (data[i][j - 1] < 0 == nsign)) && Math.abs(data[i][j - 1] - data[i][j]) < sensitivity) {
                        data1.setPixel(i, j - 1, color);
                        data1.setPixel(i, j, color);
                    }
                    if (data[i][j] == 0)
                        data1.setPixel(i, j, color);
                }
            }
        }
        this.offsetY = offsetY;
        this.scaleY = scaleY;
        this.offsetX = offsetX;
        this.scaleX = scaleX;
    }

    public Bitmap getData1() {
        for (int i = 0; i < data1.getWidth(); ++i) {
            for (int j = 0; j < data1.getHeight(); ++j) {
                if (data1.getPixel(i, j) >> 24 != 0)
                    data1.setPixel(i, j, data1.getPixel(i, j) | 0xff000000);
            }
        }
        return data1;
    }
    public void updateAfterSave(){
        int val = 0xffffffff;
        switch (type){
            case EQUALITY:
                val = 0xffffffff;
                break;
            case INEQUALITY:
                val = 0x82ffffff;
                break;
            case SPECTRUM:
                val = 0xb6ffffff;
                break;
        }
        for (int i = 0; i < data1.getWidth(); ++i) {
            for (int j = 0; j < data1.getHeight(); ++j) {
                if (data1.getPixel(i, j) >> 24 != 0) {
                    data1.setPixel(i, j, data1.getPixel(i, j) & val);
                }
            }
        }
    }
    @Override
    public void paint(Canvas g, Paint p) {
        matrix.setScale((float)(cSX / scaleX * GRAPH_WIDTH / data1.getWidth()), (float)(cSY / scaleY * HEIGHT / data1.getHeight()));
        matrix.postTranslate((float) ((offsetX - cOX) * cSX), (float) ((cOY - offsetY) * cSY));
        g.drawBitmap(data1, matrix, p);
    }

    public void updateY(FuncVariable<Double> yVar) {
        this.yVar = yVar;
        if (func.getName().equals("=")) {
            type = EQUALITY;
            BinaryActor<Double> actor = (BinaryActor<Double>) func;
            actor.setFunc((a, b) -> a.calculate() - b.calculate());
        } else if (func.getName().equals("<") || func.getName().equals(">") || func.getName().equals("0.0")) {
            type = INEQUALITY;
            setC();
        } else {
            type = SPECTRUM;
        }
    }

    public void setC() {
        if (type == INEQUALITY) {
            c = Color.argb(0x82, Color.red(color), Color.green(color), Color.blue(color));
        }
        if(MainModel.dark_theme)
            c = c ^ 0x00ffffff;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
        needResize = true;
    }

    public void setViewType(int type) {
        if (viewType == type)
            return;
        viewType = type;
        needResize = true;
    }

    @Override
    public void free() {
        super.free();
        data1 = null;
        data = null;
        yVar = null;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    @Override
    public void setMAP_SIZE(int map_size) {
        needResize = true;
        MAP_SIZE = map_size;
        float dw = (float) GRAPH_WIDTH / MAP_SIZE;
        yMAP_SIZE = (int) (HEIGHT / dw);
        data = new float[MAP_SIZE][yMAP_SIZE];
        data1 = Bitmap.createBitmap(MAP_SIZE, yMAP_SIZE, Bitmap.Config.ARGB_8888);
    }
}
