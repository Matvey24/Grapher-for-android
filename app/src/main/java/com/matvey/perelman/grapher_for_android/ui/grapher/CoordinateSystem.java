package com.matvey.perelman.grapher_for_android.ui.grapher;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.math.BigDecimal;

import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.HEIGHT;
import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.GRAPH_WIDTH;

public class CoordinateSystem {
    private int MIN_DELTA;
    private int MAX_DELTA;
    private int EXTRA_LINE_COLOR;
    private int MAIN_COLOR;
    private double offsetX;
    private double offsetY;
    private double scaleX;
    private double scaleY;
    private double deltaX = 1;
    private double deltaY = 1;
    private int deltaXpow = 0;
    private int deltaYpow = 0;
    private int maxDeltaX;
    private int maxDeltaY;
    public int MAX_LINES;

    public CoordinateSystem() {
        MIN_DELTA = 160;
        MAX_DELTA = MIN_DELTA * 5 / 2;
        maxDeltaY = MAX_DELTA * 4 / 5;
        maxDeltaX = MAX_DELTA * 4 / 5;
        setMAX_LINES();
    }
    public void setColors(int extra_color, int main_color){
        EXTRA_LINE_COLOR = extra_color;
        MAIN_COLOR = main_color;
    }
    public void resize(double offsetX, double offsetY, double scaleX, double scaleY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        if (deltaX <= 0.5001 && deltaX >= 0.4999) {
            deltaX = 0.5;
        }
        if (deltaY <= 0.5001 && deltaY >= 0.4999) {
            deltaY = 0.5;
        }
        setMAX_LINES();
        resizeNet();
    }

    public void setMIN_DELTA(int min_delta) {
        MIN_DELTA = min_delta;
        MAX_DELTA = MIN_DELTA * 5 / 2;
        maxDeltaY = MAX_DELTA * 4 / 5;
        maxDeltaX = MAX_DELTA * 4 / 5;
        setMAX_LINES();
        resizeNet();
    }

    private void setMAX_LINES() {
        MAX_LINES = HEIGHT / MIN_DELTA + GRAPH_WIDTH / MIN_DELTA + 6;
    }

    public int getMinDelta() {
        return MIN_DELTA;
    }

    private void resizeNet() {
        boolean redo = true;
        while (redo) {
            redo = false;
            if (deltaX * scaleX > maxDeltaX) {
                if ((deltaXpow - 2) % 3 == 0) {
                    deltaX *= 0.4;
                } else {
                    deltaX *= 0.5;
                }
                --deltaXpow;
                if ((deltaXpow - 2) % 3 == 0) {
                    maxDeltaX = MAX_DELTA;
                } else {
                    maxDeltaX = MAX_DELTA * 4 / 5;
                }
                redo = true;
            } else if (deltaX * scaleX < MIN_DELTA) {
                if ((deltaXpow - 1) % 3 == 0) {
                    deltaX *= 2.5;
                } else {
                    deltaX *= 2;
                }
                ++deltaXpow;
                if ((deltaXpow - 2) % 3 == 0) {
                    maxDeltaX = MAX_DELTA;
                } else {
                    maxDeltaX = MAX_DELTA * 4 / 5;
                }
                redo = true;
            }
        }
        redo = true;
        while (redo) {
            redo = false;
            if (deltaY * scaleY > maxDeltaY) {
                if ((deltaYpow - 2) % 3 == 0) {
                    deltaY *= 0.4;
                } else {
                    deltaY *= 0.5;
                }
                --deltaYpow;
                if ((deltaYpow - 2) % 3 == 0) {
                    maxDeltaY = MAX_DELTA;
                } else {
                    maxDeltaY = MAX_DELTA * 4 / 5;
                }
                redo = true;
            } else if (deltaY * scaleY < MIN_DELTA) {
                if ((deltaYpow - 1) % 3 == 0) {
                    deltaY *= 2.5;
                } else {
                    deltaY *= 2;
                }
                ++deltaYpow;
                if ((deltaYpow - 2) % 3 == 0) {
                    maxDeltaY = MAX_DELTA;
                } else {
                    maxDeltaY = MAX_DELTA * 4 / 5;
                }
                redo = true;
            }
        }
    }

    public void draw(Canvas gr, Paint p) {
        p.setColor(MAIN_COLOR);
        boolean drawLineX = false;
        int lineX;
        if (offsetX * scaleX > -GRAPH_WIDTH + 70) {
            lineX = 0;
            if (offsetX < 0) {
                drawLineX = true;
                lineX = (int) (-offsetX * scaleX);
            }
        } else {
            lineX = GRAPH_WIDTH - 70;
        }
        for (int i = 0, m = (int) (HEIGHT / scaleY / deltaY) + 1; i < m; ++i) {
            int y = (int) ((mod(offsetY, deltaY) + (m - i - 1) * deltaY) * scaleY);
            gr.drawText(dts(ceil(offsetY + (i - m) * deltaY, deltaY)), lineX + 5, y + 30, p);
            p.setColor(EXTRA_LINE_COLOR);
            gr.drawLine(0, y, GRAPH_WIDTH, y, p);
            p.setColor(MAIN_COLOR);
        }

        int lineY;
        boolean drawLineY = false;
        if (offsetY * scaleY > 0) {
            lineY = HEIGHT - 30;
            if (offsetY * scaleY < HEIGHT - 30) {
                lineY = (int) (offsetY * scaleY);
                drawLineY = true;
            }
        } else {
            lineY = 0;
        }
        for (int i = 0, m = (int) (GRAPH_WIDTH / scaleX / deltaX) + 1; i < m; ++i) {
            int x = (int) ((-mod(offsetX, deltaX) + (i + 1) * deltaX) * scaleX);
            gr.drawText(dts(ceil(offsetX + i * deltaX, deltaX)), x + 5, lineY + 30, p);
            p.setColor(EXTRA_LINE_COLOR);
            gr.drawLine(x, 0, x, HEIGHT, p);
            p.setColor(MAIN_COLOR);
        }
        if (drawLineY) {
            gr.drawLine(0, lineY, GRAPH_WIDTH, lineY, p);
            gr.drawText("x", GRAPH_WIDTH - 25, (int) (offsetY * scaleY - 10), p);
        }
        if (drawLineX) {
            gr.drawLine(lineX, 0, lineX, HEIGHT, p);
            gr.drawText("y", (int) (-offsetX * scaleX - 10), 30, p);
        }
    }

    private static double ceil(double a, double m) {
        return m * Math.ceil(a / m);
    }

    public static String dts(double d) {
        if (d % 1 == 0) {
            return String.valueOf(BigDecimal.valueOf(d).toBigInteger());
        }
        String val = BigDecimal.valueOf(d)
                .toPlainString();
        return BigDecimal.valueOf(Double.parseDouble(val.substring(0, Math.min(val.length(), 16))))
                .stripTrailingZeros()
                .toPlainString();

    }

    public static double mod(double a, double b) {
        double c = a % b;
        if (c <= 0)
            c += b;
        return c;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }
}
