package com.kostya.ratingview;

import android.content.Context;

public final class Utils {

    private Utils() {
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
