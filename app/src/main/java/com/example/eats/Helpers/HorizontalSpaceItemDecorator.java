package com.example.eats.Helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Used to add horizontal spacing between items in recycler view
 * Code adopted from: https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
 */
public class HorizontalSpaceItemDecorator extends RecyclerView.ItemDecoration {
    private final int horizontalSpaceHeight;

    public HorizontalSpaceItemDecorator(int horizontalSpaceHeight) {
        this.horizontalSpaceHeight = horizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = horizontalSpaceHeight;
    }
}
