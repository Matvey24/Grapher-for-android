package com.matvey.perelman.grapher_for_android.ui.elements;

import android.app.Dialog;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.material.textfield.TextInputEditText;
import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.controller.ModelUpdater;
import com.matvey.perelman.grapher_for_android.controller.MyTimer;
import com.matvey.perelman.grapher_for_android.model.FullModel;

public class TimerSettings {
    private final ModelUpdater updater;

    private final MainActivity activity;
    private final Dialog timer_dialog;

    private final Button btn_open_timer;
    private final ToggleButton btn_start_timer;
    private final ToggleButton btn_change_mod;
    private final TextInputEditText et_duration_fps;
    private final TextInputEditText et_dimension;
    private final MyTimer timer;

    private double duration = 20;
    private double fps;
    private double start = 0;
    private double end = 6.2832;
    private boolean mod = false;

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
        et_duration_fps = timer_dialog.findViewById(R.id.timer_dur_fps);
        et_dimension = timer_dialog.findViewById(R.id.timer_dimension);
        et_duration_fps.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_dimension.setImeOptions(EditorInfo.IME_ACTION_DONE);
        btn_start_timer.setOnClickListener((view) -> btnStart());
        btn_change_mod.setOnClickListener((view) -> mod = btn_change_mod.isChecked());

        btn_open_timer.setOnLongClickListener((v)->{
            btn_start_timer.setChecked(!btn_start_timer.isChecked());
            btnStart();
            return true;
        });
        et_duration_fps.setOnEditorActionListener((v, actionId, event) -> {
            update_text();
            return true;
        });
        et_dimension.setOnEditorActionListener((v, actionId, event) -> {
            update_text();
            return true;
        });
        timer = new MyTimer(this::timer_iteration);
        setFPS(60);
        value = this.start;
    }
    private void btnStart() {
        boolean checked = btn_start_timer.isChecked();
        if (checked) {
            timeBefore = System.currentTimeMillis();
            timer.start();
        } else {
            timer.stop();
        }
        btn_open_timer.setText(getTimerText(checked));
    }

    private String getTimerText(boolean selected) {
        return activity.getResources().getString(R.string.nav_timer) + (selected ? " I" : " O");
    }

    private void update_text() {
        boolean good = true;
        try {
            parseDurationFPS(MainActivity.getFromEditText(et_duration_fps));
        } catch (RuntimeException e) {
            et_duration_fps.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            good = false;
        }
        try {
            parseDimension(MainActivity.getFromEditText(et_dimension));
        } catch (RuntimeException e) {
            et_dimension.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            good = false;
        }
        if (good) {
            activity.hideKeyboard(timer_dialog.getCurrentFocus());
        }
    }

    private void parseDurationFPS(String text) {
        int idx = text.indexOf(":");
        String duration = text.substring(0, idx).replaceAll("[ \t\r]", "");
        double dur = Double.parseDouble(duration);
        if(dur < 0){
            throw new RuntimeException(dur + " < 0");
        }
        this.duration = dur;
        String fps = text.substring(idx + 1).replaceAll("[ \t\r]", "");
        double fp = Double.parseDouble(fps);
        if(fp <= 0){
            throw new RuntimeException(fp + " <= 0");
        }else if(fp > 3000){
            throw new RuntimeException(fp + " > 3000");
        }
        setFPS(fp);
    }

    private void parseDimension(String text) {
        int idx = text.indexOf(":");
        String start = text.substring(0, idx).replaceAll("[ \t\r]", "");
        this.start = Double.parseDouble(start);
        String end = text.substring(idx + 1).replaceAll("[ \t\r]", "");
        this.end = Double.parseDouble(end);
        time = 0;
        value = this.start;
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

    private String getDuration_FPS_String() {
        return duration + " : " + fps;
    }

    private String getDimensionString() {
        return start + " : " + end;
    }

    public void stopTimer() {
        btn_start_timer.setChecked(false);
        btnStart();
    }

    private void show_dialog() {
        et_duration_fps.setText(getDuration_FPS_String());

        et_dimension.setText(getDimensionString());

        timer_dialog.show();
    }

    public double getTime() {
        return value;
    }

    public void makeModel(FullModel m) {
        m.timer_info = getDuration_FPS_String() + "\n" + getDimensionString() + "\n" + mod;
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
            }catch (RuntimeException ignored){}
    }
}
