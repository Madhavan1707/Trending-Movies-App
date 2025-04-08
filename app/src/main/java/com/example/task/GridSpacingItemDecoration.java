package com.example.task;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;

    public GridSpacingItemDecoration(int spacingPx) {
        this.spacing = spacingPx;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.top = spacing;
        outRect.bottom = spacing;
    }
}      
