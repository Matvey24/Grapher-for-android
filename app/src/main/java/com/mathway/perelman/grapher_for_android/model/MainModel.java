package com.mathway.perelman.grapher_for_android.model;

import com.mathway.perelman.grapher_for_android.MainActivity;
import com.mathway.perelman.grapher_for_android.R;
import com.mathway.perelman.grapher_for_android.calculator2.calculator.CalcLanguage;
import com.mathway.perelman.grapher_for_android.controller.ModelUpdater;
import com.mathway.perelman.grapher_for_android.ui.helper.HelperFragment;

public class MainModel {
    private static MainModel mm;
    public static boolean dark_theme;
    public static void createInstance(MainActivity activity){
        if(mm == null)
            mm = new MainModel(activity);
        else{
            mm.updater.setMain(activity);
        }
    }
    public static MainModel getInstance(){
        return mm;
    }

    public static final int GRAPHICS = 1;
    public static final int HELPER = 2;
    public HelperFragment helperFragment;
    public int openedWindow;
    private MainModel(MainActivity activity){
        updater = new ModelUpdater(activity);
        CalcLanguage.CALCULATOR_ERRORS = new String[]{
                activity.getString(R.string.calculator_errors_0),
                activity.getString(R.string.calculator_errors_1),
                activity.getString(R.string.calculator_errors_2),
                activity.getString(R.string.calculator_errors_3),
                activity.getString(R.string.calculator_errors_4),
                activity.getString(R.string.calculator_errors_5),
                activity.getString(R.string.calculator_errors_6),
        };
        CalcLanguage.PARSER_ERRORS = new String[]{
                activity.getString(R.string.parser_errors_0),
                activity.getString(R.string.parser_errors_1),
                activity.getString(R.string.parser_errors_2),
                activity.getString(R.string.parser_errors_3),
                activity.getString(R.string.parser_errors_4),
                activity.getString(R.string.parser_errors_5),
                activity.getString(R.string.parser_errors_6),
        };
    }
    public static String[][][] fullArray;
    public static boolean log_loaded;
    public static int selected_array;

    public final ModelUpdater updater;

}
