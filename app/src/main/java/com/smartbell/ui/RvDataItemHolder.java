package com.smartbell.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.smartbell.R;

/**
 * Created by Curry on 18/3/17.
 */

public class RvDataItemHolder extends RecyclerView.ViewHolder {

    private TextView contentTv;

    public RvDataItemHolder(View itemView) {
        super(itemView);
        contentTv = (TextView) itemView.findViewById(R.id.content);
    }

    public TextView getContentTv() {
        return contentTv;
    }
}
