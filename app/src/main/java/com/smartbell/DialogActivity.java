package com.smartbell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class DialogActivity extends Activity {

    int level = 0;
    private final String[] multiChoiceItems = {"Level 1", "Level 2", "Level 3", "Level 4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent it = getIntent();
        level = it.getIntExtra("level", 1);

        showDialog();
    }

    private void showDialog() {

        AlertDialog dialog = new MyAlertDialog.Builder(DialogActivity.this).setTitle("RadioButton").setSingleChoiceItems(multiChoiceItems, level - 1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                level = which + 1;
                // saveLevelValue(level);
                // mViewManager.refreshView();
                dialog.dismiss();
            }
        }).show();


    }

    class MyAlertDialog extends AlertDialog {

        protected MyAlertDialog(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void dismiss() {
            // TODO Auto-generated method stub
//			super.dismiss();
            DialogActivity.this.finish();
        }

    }

}
