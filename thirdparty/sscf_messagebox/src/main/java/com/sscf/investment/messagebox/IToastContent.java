package com.sscf.investment.messagebox;

import android.content.Context;
import android.view.View;

public interface IToastContent {

    View createContentView(Context context);

    void updateContent(CharSequence message, int imgRes);
}
