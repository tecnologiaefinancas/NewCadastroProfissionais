package com.tecnologiaefinancas.newcadastroprofissionais.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.tecnologiaefinancas.newcadastroprofissionais.R;

public class Dialogs {

    public static void alert(Context context, int idText){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.warn);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setMessage(idText);


        builder.setNeutralButton(R.string.ok, null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void actionConfirmation(Context context,
                                          String message,
                                          DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.confirmation);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(message);

        builder.setPositiveButton(R.string.yes, listener);
        builder.setNegativeButton(R.string.no, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
