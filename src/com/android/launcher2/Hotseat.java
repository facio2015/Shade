/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.launcher.R;

public class Hotseat extends FrameLayout {
    static final String TAG = "Hotseat";

    private Launcher mLauncher;
    private CellLayout mContent;

    private int mCellCountX;
    private int mCellCountY;
    private boolean mIsLandscape;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Hotseat, defStyle, 0);
        mCellCountX = a.getInt(R.styleable.Hotseat_cellCountX, -1);
        mCellCountY = a.getInt(R.styleable.Hotseat_cellCountY, -1);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
    }

    CellLayout getLayout() {
        return mContent;
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return mIsLandscape ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return mIsLandscape ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return mIsLandscape ? (mContent.getCountY() - (rank + 1)) : 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCellCountX < 0) mCellCountX = LauncherModel.getCellCountX();
        if (mCellCountY < 0) mCellCountY = LauncherModel.getCellCountY();
        mContent = (CellLayout) findViewById(R.id.layout);
        mContent.setGridSize(mCellCountX, mCellCountY);

        resetLayout();
    }

    void resetLayout() {
        mContent.removeAllViewsInLayout();

        // Add the Apps button
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        BubbleTextView allAppsButton = (BubbleTextView)
                inflater.inflate(R.layout.application, mContent, false);
        allAppsButton.setCompoundDrawablesWithIntrinsicBounds(null,
                context.getResources().getDrawable(R.drawable.apps_hotseat_button), null, null);
        // button.setText(context.getString(R.string.all_apps_button_label));
        allAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                mLauncher.showAllApps(true);
            }
        });

        // Note: We do this to ensure that the hotseat is always laid out in the orientation of
        // the hotseat in order regardless of which orientation they were added
        int x = getCellXFromOrder(0);
        int y = getCellYFromOrder(0);
        mContent.addViewToCellLayout(allAppsButton, -1, 0, new CellLayout.LayoutParams(x,y,1,1),
                true);
    }
}
