package com.elior.findmyloss.OthersPck;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;

    public ItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = mSpace;
    }

}
