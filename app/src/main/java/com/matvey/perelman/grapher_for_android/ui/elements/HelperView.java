package com.matvey.perelman.grapher_for_android.ui.elements;

import android.app.Dialog;
import android.widget.Button;

import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.model.MainModel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class HelperView {
    private Dialog dialog;
    private final MainActivity activity;

    public HelperView(MainActivity activity){
        this.activity = activity;
        Button btn_open_dialog = activity.findViewById(R.id.btn_help);
        btn_open_dialog.setOnClickListener((view)->open_dialog());
    }
    private void open_dialog(){
        if(dialog == null){
            dialog = new Dialog(activity);
            dialog.setContentView(R.layout.helper_view);

            Button btn_using_help = dialog.findViewById(R.id.btn_using_help);
            btn_using_help.setOnClickListener((view)-> openHelperFragment(0));

            Button btn_calculator_help = dialog.findViewById(R.id.btn_calculator_help);
            btn_calculator_help.setOnClickListener((view)-> openHelperFragment(1));

            Button btn_extra_info = dialog.findViewById(R.id.btn_extra_info);
            btn_extra_info.setOnClickListener((view)-> openHelperFragment(2));

            Button btn_version_log = dialog.findViewById(R.id.btn_version_log);
            btn_version_log.setOnClickListener((view)-> openHelperFragment(3));
        }
        activity.stopTimer();
        dialog.show();
    }
    private void openHelperFragment(int type){
        MainModel.selected_array = type;
        dialog.dismiss();
        activity.stopTimer();
        if(MainModel.fullArray != null){
            if(type == 3 && !MainModel.log_loaded)
                activity.runInBackground(activity::loadLog);
            else
                activity.open_helper();
            return;
        }
        activity.runInBackground(()-> {
            String[] array = activity.getResources().getStringArray(R.array.helpers);
            List<String> list = Arrays.asList(array);
            Iterator<String> it = list.iterator();
            String[][][] HELPERS = new String[4][][];
            String[] arr;
            array:
            {
                for (int i = 0; i < 3; ++i) {
                    arr = it.next().split(" ");
                    if (!arr[0].equals("help")) {
                        break array;
                    }
                    int n = Integer.parseInt(arr[1]);
                    HELPERS[i] = new String[n][2];
                    for (int j = 0; j < n; ++j) {
                        HELPERS[i][j][0] = it.next();
                        HELPERS[i][j][1] = it.next();
                    }
                }
            }
            MainModel.fullArray = HELPERS;
            if (type == 3) {
                activity.loadLog();
            } else {
                activity.open_helper();
            }
        });
    }
}
