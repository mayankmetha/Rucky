package com.mayank.rucky.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mayank.rucky.R;
import com.mayank.rucky.models.HidModel;

import java.util.ArrayList;

public class HidAdapter extends ArrayAdapter<HidModel> {

    ArrayList<HidModel> hidModelList;
    Context context;

    public HidAdapter(ArrayList<HidModel> hidModelList, Context context) {
        super(context, -1, hidModelList);
        this.hidModelList = hidModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return hidModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @NonNull
    @Override
    public View getView(int pos, View convertedView, @NonNull ViewGroup parent) {
        HidModel hidViewModel = hidModelList.get(pos);
        if(convertedView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertedView = layoutInflater.inflate(R.layout.hid_list_item, parent, false);
        }
        TextView name = convertedView.findViewById(R.id.hid_name);
        name.setText(hidViewModel.getHidModelName());
        TextView rev = convertedView.findViewById(R.id.hid_version);
        rev.setText(context.getResources().getString(R.string.hid_version, hidViewModel.getHidModelRevision()));
        ImageView icon = convertedView.findViewById(R.id.hid_icon);
        switch (hidViewModel.getHidModelState()) {
            case Constants.HID_DOWNLOAD:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hid_download));
                break;
            case Constants.HID_UPDATE:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hid_update));
                break;
            case Constants.HID_OFFLINE:
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hid_offline));
                break;
        }
        return convertedView;
    }

}


