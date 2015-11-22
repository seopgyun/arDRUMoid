package com.ardrumoid;

import com.ardrumoid.util.BackPressCloseHandler;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class MenuActivity extends Activity {

    private static final String LOG_TAG = "MenuActivity";
    private final String TAG = MenuActivity.class.getSimpleName();

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void onStartBtnClick(View v) {
        Intent intent = new Intent(this, MusicSelectActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
    }

    public void onOptionBtnClick(View v) {
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
    }

    public void onExitBtnClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_message)
                .setPositiveButton("나가기", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MenuActivity.this.finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }
}
