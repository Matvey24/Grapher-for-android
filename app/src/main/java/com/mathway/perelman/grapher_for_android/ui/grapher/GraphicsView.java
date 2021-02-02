package com.mathway.perelman.grapher_for_android.ui.grapher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.model.MainModel;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.grapher.graphics.Graphic;

import java.util.ArrayList;

public class GraphicsView extends View {
    private final Paint paint;
    private final CoordinateSystem cs;
    private final ModelUpdater updater;
    private final ScaleGestureDetector sgd;
    private final GestureDetector gd;
    private final int background;
    private final ArrayList<Graphic> graphics;
    public boolean painting;
    public MainActivity activity;
    public float mouseX, mouseY;
    public boolean isMousePressed;
    public boolean sizeUpdated;
    public boolean view_movable = true;

    public GraphicsView(Context context) {
        super(context);
        this.updater = MainModel.getInstance().updater;
        updater.setGraphicsView(this);
        graphics = updater.graphics;
        cs = updater.getCoordinateSystem();
        paint = new Paint();
        activity = updater.getMain();
        paint.setStyle(Paint.Style.FILL);
        sgd = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mouseX = detector.getFocusX();
                mouseY = detector.getFocusY();
                if (!view_movable)
                    return false;
                if (updater.resize_multiple) {
                    if(detector.getCurrentSpanY() < 50 ||
                            detector.getPreviousSpanY() < 50 ||
                            detector.getCurrentSpanX() < 50 ||
                            detector.getPreviousSpanX() < 50)
                        return true;
                    double x = detector.getCurrentSpanX() / detector.getPreviousSpanX();
                    double y = detector.getCurrentSpanY() / detector.getPreviousSpanY();
                    updater.rescale(x, y, mouseX, mouseY);
                } else {
                    updater.rescale(detector.getScaleFactor(), mouseX, mouseY);
                }
                return true;
            }
        });
        gd = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mouseX = e2.getX();
                mouseY = e2.getY();
                if (!view_movable)
                    return false;
                updater.translate(-distanceX, -distanceY);
                return true;
            }

        });
        background = getResources().getColor(R.color.background);
        cs.setColors(getResources().getColor(R.color.extra_line), getResources().getColor(R.color.main_color));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mouseX = event.getX();
            mouseY = event.getY();
            isMousePressed = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isMousePressed = false;
            updater.runResize();
        }
        sgd.onTouchEvent(event);
        gd.onTouchEvent(event);
        return true;
    }

    public boolean myrepaint() {
        if (!painting) {
            painting = true;
            postInvalidateOnAnimation();
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        if (!sizeUpdated) {
            sizeUpdated = true;
            updater.setBounds(width, height);
        } else if (width > ModelUpdater.GRAPH_WIDTH || height > ModelUpdater.HEIGHT) {
            updater.setSize(width, height);
        }
        if (updater.dangerState)
            return;
        paint.setColor(background);
        canvas.drawPaint(paint);
        paint.setTextSize(30);
        if (updater.draw_coordinates)
            cs.draw(canvas, paint);
        for (int i = 0; i < graphics.size(); ++i)
            graphics.get(i).paint(canvas, paint);
        painting = false;
    }
}
