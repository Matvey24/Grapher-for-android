package com.matvey.perelman.grapher_for_android.ui.elements.graphic_settings;

import android.Manifest;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.material.textfield.TextInputEditText;
import com.matvey.perelman.grapher_for_android.MainActivity;
import com.matvey.perelman.grapher_for_android.R;
import com.matvey.perelman.grapher_for_android.controller.DNEditor;
import com.matvey.perelman.grapher_for_android.controller.ModelUpdater;
import com.matvey.perelman.grapher_for_android.ui.elements.elements_list.TextElement;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Graphic;
import com.matvey.perelman.grapher_for_android.ui.grapher.graphics.Implicit;

import java.io.File;
import java.io.FileOutputStream;

public class ImplicitSettings extends DefaultSettings {
    private Implicit implicit;
    private TextInputEditText et_sensitivity;
    private RadioButton btn_infrared_imager;
    private RadioButton btn_rainbow;

    public ImplicitSettings(MainActivity activity, ModelUpdater updater) {
        super(activity, updater);
    }

    @Override
    protected boolean onScan() {
        String text = DNEditor.getText(et_sensitivity);
        try{
            implicit.setSensitivity(Double.parseDouble(text));
            return true;
        }catch (RuntimeException e){
            et_sensitivity.setError(activity.getString(R.string.simple_error) + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public Graphic getGraphic() {
        return implicit;
    }

    @Override
    public void startSettings(Graphic g, TextElement e) {
        implicit = (Implicit) g;
        if (getDialog() == null) {
            Dialog dialog = new Dialog(activity);
            dialog.setContentView(R.layout.implicit_settings);
            et_sensitivity = dialog.findViewById(R.id.et_implicit_sensitivity);
            Button btn_save_picture = dialog.findViewById(R.id.btn_implicit_save_picture);
            btn_infrared_imager = dialog.findViewById(R.id.btn_infrared_imager);
            btn_rainbow = dialog.findViewById(R.id.btn_rainbow);

            btn_infrared_imager.setOnClickListener((view) -> {
                implicit.setViewType(Implicit.INFRARED_IMAGER);
                updater.runResize();
            });
            btn_rainbow.setOnClickListener((view) -> {
                implicit.setViewType(Implicit.RAY_SPECTRUM);
                updater.runResize();
            });

            et_sensitivity.setImeOptions(EditorInfo.IME_ACTION_DONE);
            et_sensitivity.setOnEditorActionListener((a, b, c) -> {
                fullScan();
                return true;
            });

            btn_save_picture.setOnClickListener((view)->{
                if(Build.VERSION.SDK_INT >= 23){
                    activity.requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, MainActivity.REQUEST_SAVE_PICTURE);
                }else{
                    save_picture();
                }
            });
            onCreateDialog(dialog);
        }
        et_sensitivity.setText(String.valueOf(implicit.getSensitivity()));
        switch (implicit.viewType){
            case Implicit.INFRARED_IMAGER:
                btn_infrared_imager.setChecked(true);
                break;
            case Implicit.RAY_SPECTRUM:
                btn_rainbow.setChecked(true);
                break;
        }
        show();
    }
    public void save_picture(){
        activity.runInBackground(()-> {
            try {
                Bitmap bitmap = implicit.getData1();
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "img.jpg");
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                activity.setState(activity.getString(R.string.saved) + " img.jpg");
            } catch (Exception ex) {
                activity.setState(ex.toString());
            }
            implicit.updateAfterSave();
        });
    }
}
