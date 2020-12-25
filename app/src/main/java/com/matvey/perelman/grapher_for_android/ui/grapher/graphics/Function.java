package com.matvey.perelman.grapher_for_android.ui.grapher.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.matvey.perelman.grapher_for_android.model.BoolAsk;
import com.matvey.perelman.grapher_for_android.model.GraphType;

import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.GRAPH_WIDTH;
import static com.matvey.perelman.grapher_for_android.controller.ModelUpdater.HEIGHT;

public class Function extends Graphic {
    public boolean abscissa = true;
    public Function(){
        map = new double[MAP_SIZE];
        type = GraphType.FUNCTION;
    }
    public Function(int map_size, boolean feelsTime){
        super(map_size, feelsTime);

        type = GraphType.FUNCTION;
    }
    @Override
    public void resize(double offsetX, double offsetY, double scaleX, double scaleY) {
        if (abscissa) {
            this.offsetY = offsetY;
            this.scaleY = scaleY;
            this.graph_height = HEIGHT;
            if (needResize || offsetX != this.offsetX || this.scaleX != scaleX || this.graph_width != GRAPH_WIDTH) {
                needResize = false;
                this.offsetX = offsetX;
                this.scaleX = scaleX;
                this.graph_width = GRAPH_WIDTH;
                for (int i = 0; i < MAP_SIZE; ++i) {
                    var.setValue(offsetX + (double) i * GRAPH_WIDTH / (MAP_SIZE - 1) / scaleX);
                    map[i] = func.calculate();
                }
            }
        } else {
            this.offsetX = offsetX;
            this.scaleX = scaleX;
            this.graph_width = GRAPH_WIDTH;
            if (needResize || offsetY != this.offsetY || this.scaleY != scaleY || this.graph_height != HEIGHT) {
                needResize = false;
                this.offsetY = offsetY;
                this.scaleY = scaleY;
                this.graph_height = HEIGHT;
                for (int i = 0; i < MAP_SIZE; ++i) {
                    var.setValue(offsetY - (double) i * HEIGHT / (MAP_SIZE - 1) / scaleY);
                    map[i] = func.calculate();
                }
            }
        }
    }

    @Override
    public void paint(Canvas g, Paint p) {
        p.setColor(color);
        if (abscissa) {
            for (int i = 0; i < MAP_SIZE - 1; ++i) {
                if (Double.isNaN(map[i]) || Double.isNaN(map[i + 1]) || Math.abs(map[i] - map[i + 1]) * scaleY > MAX_DELTA)
                    continue;
                double y1 = ((offsetY - map[i]) * scaleY);
                double y2 = ((offsetY - map[i + 1]) * scaleY);
                if (y1 < 0 && y2 < 0 || y1 > HEIGHT && y2 > HEIGHT)
                    continue;
                g.drawLine(i * GRAPH_WIDTH / (MAP_SIZE-1f), (float)y1,
                        (i + 1) * GRAPH_WIDTH / (MAP_SIZE-1f), (float)y2, p);
            }
        } else {
            for (int i = 0; i < MAP_SIZE - 1; ++i) {
                if (Double.isNaN(map[i]) || Double.isNaN(map[i + 1]) || Math.abs(map[i] - map[i + 1]) * scaleX > MAX_DELTA)
                    continue;
                int x1 = (int) ((-offsetX + map[i]) * scaleX);
                int x2 = (int) ((-offsetX + map[i + 1]) * scaleX);
                if (x1 < 0 || x2 < 0 || x1 > GRAPH_WIDTH && x2 > GRAPH_WIDTH)
                    continue;
                g.drawLine(x1, i * HEIGHT / (MAP_SIZE-1f),
                        x2, (i + 1) * HEIGHT / (MAP_SIZE-1f), p);
            }
        }
    }
}
