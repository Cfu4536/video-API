package com.example.oldfaces.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.oldfaces.R;
import com.example.oldfaces.points.URL;

public class UrlChangeDialog {
    private Context context;
    private String title;

    public interface OnSubmitListener {
        void onSubmit(URL uptatedata);
    }

    public UrlChangeDialog(Context context, String title) {
        this.context = context;
        this.title = title;
    }

    public void showDialog(URL data, OnSubmitListener I) {
        URL temp = new URL("", "");
        if (data != null) {
            temp.setName(data.getName());
            temp.setUrl((data.getUrl()));

        }
        View v = LayoutInflater.from(context).inflate(R.layout.url_change_layout, null, false);
        EditText editName = v.findViewById(R.id.editTextChangeName);
        EditText editPhone = v.findViewById(R.id.editTextChangeUrl);

        editName.setText(temp.getName());
        editPhone.setText(temp.getUrl());

        AlertDialog.Builder bl = new AlertDialog.Builder(context);
        bl.setTitle(title).setView(v).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                URL updata = new URL(editPhone.getText().toString(), editName.getText().toString());
                if (I != null) {
                    I.onSubmit(updata);
                }
            }
        }).show();
    }
}
