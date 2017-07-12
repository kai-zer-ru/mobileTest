package pro.myburse.android.myburse.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;

import pro.myburse.android.myburse.R;

/**
 * Created by alexey on 10.07.17.
 */

public class Utils {

    public static void showErrorMessage(Context context, String msg){
        AlertDialog ad = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.stat_notify_error)
                .setTitle("Ошибка")
                .setMessage(msg)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
}
