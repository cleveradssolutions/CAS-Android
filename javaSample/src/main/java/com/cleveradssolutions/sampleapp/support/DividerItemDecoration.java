package com.cleveradssolutions.sampleapp.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cleveradssolutions.sampleapp.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public DividerItemDecoration(Context context) {
        divider = ContextCompat.getDrawable(context, R.drawable.divider);
        if (divider == null) {
            throw new NullPointerException("Divider drawable not found");
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int lastIndex = parent.getChildCount() - 1;
        if (lastIndex > 0) {
            for (int i = 0; i < lastIndex; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

}
