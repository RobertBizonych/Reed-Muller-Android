package com.uzhnu.reedmuller.api;


import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import com.uzhnu.reedmuller.R;

public class TransparentProgressDialog extends Dialog {

    public static TransparentProgressDialog show(Context context, CharSequence title,
                                        CharSequence message) {
        TransparentProgressDialog dialog = new TransparentProgressDialog(context);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ProgressBar bar = new ProgressBar(context);
        dialog.addContentView(bar, params);

        return dialog;
    }

    public TransparentProgressDialog(Context context) {
        super(context, R.style.NewDialog);
    }
}