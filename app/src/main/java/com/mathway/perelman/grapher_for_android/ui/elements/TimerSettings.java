package com.mathway.perelman.grapher_for_android.ui.elements;

import android.app.Dialog;
import android.widget.Button;
import android.widget.ToggleButton;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.controller.DNEditor;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.controller.MyTimer;
import com.mathway.perelman.grapher_for_android.model.FullModel;

public class TimerSettings {
    private final ModelUpdater updater;

    private final MainActivity activity;
    private final Dialog timer_dialog;

    private final Button btn_open_timer;
    private final ToggleButton btn_start_timer;
    private final ToggleButton btn_change_mod;
    private final DNEditor duration_fps;
    private final DNEditor dimension;
    private final MyTimer timer;

    private double duration = 20;
    private double fps;
    private double start = 0;
    private double end = 6.2832;
    private boolean mod = false;
    private Runnable timer_run;
    private double timeBefore;
    private double time;
    private double value;
    private boolean fTimeDirection = true;

    public TimerSettings(MainActivity activity, ModelUpdater updater) {
        this.activity = activity;
        this.updater = updater;
        btn_open_timer = activity.findViewById(R.id.btn_timer);
        btn_open_timer.setOnClickListener((view) -> show_dialog());
        timer_dialog = new Dialog(activity);
        timer_dialog.setContentView(R.layout.timer_settings);

        btn_start_timer = timer_dialog.findViewById(R.id.timer_btn_start);
        btn_change_mod = timer_dialog.findViewById(R.id.timer_btn_boomerang);

        duration_fps = new DNEditor(timer_dialog.findViewById(R.id.timer_dur_fps));
        dimension = new DNEditor(timer_dialog.findViewById(R.id.timer_dimension));

        btn_start_timer.setOnClickListener((view) -> saveState(btn_start_timer.isChecked()));
        btn_change_mod.setOnClickListener((view) -> mod = btn_change_mod.isChecked());

        btn_open_timer.setOnLongClickListener((v) -> {
            saveState(!btn_start_timer.isChecked());
            return true;
        });
        duration_fps.setListener(this::update_text);
        dimension.setListener(this::update_text);
        timer = new MyTimer(() -> timer_run.run());
        timer_run = this::timer_iteration;
        setFPS(60);
        value = this.start;
    }

    public void saveState(boolean state) {
        if (state) {
            timeBefore = System.currentTimeMillis();
            timer.start();
        } else {
            timer.stop();
        }
        activity.runOnUiThread(()-> {
            btn_start_timer.setChecked(state);
            btn_open_timer.setText(getTimerText(state));
            activity.setMenuTimer(state);
        });
    }

    public boolean getDirection() {
        return fTimeDirection;
    }

    private String getTimerText(boolean selected) {
        return activity.getResources().getString(R.string.nav_timer) + (selected ? " I" : " 0");
    }

    private void update_text() {
        boolean good = true;
        try {
            parseDurationFPS(duration_fps.getText());
        } catch (RuntimeException e) {
            duration_fps.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            good = false;
        }
        try {
            parseDimension(dimension.getText());
        } catch (RuntimeException e) {
            dimension.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            good = false;
        }
        if (good) {
            activity.hideKeyboard(timer_dialog.getCurrentFocus());
        }
        updater.timerResize();
    }

    private void parseDurationFPS(String text) {
        duration_fps.parseString(text);

        double dur = duration_fps.getA();
        if (dur < 0)
            throw new RuntimeException(dur + " < 0");
        this.duration = dur;
        if (dur == 0) {
            timer_run = this::timer_real_time;
        } else {
            timer_run = this::timer_iteration;
        }
        double fp = duration_fps.getB();
        if (fp <= 0) {
            throw new RuntimeException(fp + " <= 0");
        } else if (fp > 1000) {
            throw new RuntimeException(fp + " > 1000");
        }
        setFPS(fp);
    }

    private void parseDimension(String text) {
        dimension.parseString(text);
        start = dimension.getA();
        value = start;
        time = 0;
        end = dimension.getB();
        if (start == end) {
            throw new RuntimeException("start = end");
        }
    }

    private void setFPS(double fps) {
        this.fps = fps;
        timer.setDelay((int) (1000 / fps));
    }

    private void timer_iteration() {
        long t = System.currentTimeMillis();
        double delta = (t - timeBefore) / 1000d;
        timeBefore = t;
        double len = end - start;
        value = time / duration * len + start;
        updater.setTime(value);
        if (fTimeDirection)
            time += delta;
        else
            time -= delta;
        if (time > duration) {
            if (mod) {
                time = duration;
                fTimeDirection = false;
            } else {
                time -= duration;
            }
        } else if (time < 0) {
            time = 0;
            fTimeDirection = true;
        }
        updater.timerResize();
    }

    private void timer_real_time() {
        long t = System.currentTimeMillis();
        double delta = (t - timeBefore) / 1000d;
        timeBefore = t;
        time += delta;
        value = time;
        updater.setTime(value);
        updater.timerResize();
    }

    public void stopTimer() {
        saveState(false);
    }

    private void show_dialog() {
        duration_fps.set(duration, fps);
        dimension.set(start, end);

        timer_dialog.show();
    }

    public double getTime() {
        return value;
    }

    public void setPosition(float time, boolean started, boolean direction) {
        if (time >= start && time <= end) {
            this.value = time;
            this.time = value - start / (end - start) * duration;
            this.timeBefore = time;
        }
        fTimeDirection = direction;
        saveState(started);
    }

    public boolean isStarted() {
        return btn_start_timer.isChecked();
    }

    public void makeModel(FullModel m) {
        m.timer_info = DNEditor.makeString(duration, fps) + "\n" + DNEditor.makeString(start, end) + "\n" + mod;
    }

    public void fromModel(FullModel m) {
        stopTimer();
        String[] arr = m.timer_info.split("\n");
        if (arr.length > 0)
            try {
                parseDurationFPS(arr[0]);
            } catch (RuntimeException ignored) {
            }
        if (arr.length > 1)
            try {
                parseDimension(arr[1]);
            } catch (RuntimeException ignored) {
            }
        if (arr.length > 2)
            try {
                mod = Boolean.parseBoolean(arr[2]);
                btn_change_mod.setChecked(mod);
            } catch (RuntimeException ignored) {
            }
    }

    public void dispose() {
        timer.dispose();
    }
}
