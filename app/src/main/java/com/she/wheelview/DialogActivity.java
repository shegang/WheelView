package com.she.wheelview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.she.wheelview.loopview.LoopView;
import com.she.wheelview.loopview.OnItemSelectedListener;

import java.util.ArrayList;

/**
 * Created by sg on 2017/3/25.
 */

public class DialogActivity extends Activity {

    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view, null);
        dialogBuilder.setView(dialogView);

        LoopView loopView = (LoopView) dialogView.findViewById(R.id.loopView);

        loopView.setCenterTextColor(getResources().getColor(R.color.design_default_color_primary));
        loopView.setTextSize((int) (Resources.getSystem().getDisplayMetrics().density * 14));
        loopView.setTextSizeCenter((int) (Resources.getSystem().getDisplayMetrics().density * 16));

        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 15; i++) {
            list.add("item " + i);
        }
        // 滚动监听
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (toast == null) {
                    toast = Toast.makeText(DialogActivity.this, "item " + index, Toast.LENGTH_SHORT);
                }
                toast.setText("item " + index);
                toast.show();
            }
        });
        // 设置原始数据
        loopView.setItems(list);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
