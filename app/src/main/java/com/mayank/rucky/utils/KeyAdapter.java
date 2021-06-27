package com.mayank.rucky.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mayank.rucky.R;
import com.mayank.rucky.models.KeyModel;

import java.util.ArrayList;

public class KeyAdapter extends ArrayAdapter<KeyModel> {

    ArrayList<KeyModel> keyModelList;
    Context context;

    public KeyAdapter(ArrayList<KeyModel> keyModelList, Context context) {
        super(context, -1, keyModelList);
        this.keyModelList = keyModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return keyModelList.size();
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
        KeyModel keyViewModel = keyModelList.get(pos);
        if(convertedView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertedView = layoutInflater.inflate(R.layout.key_list_item, parent, false);
        }
        TextView keyIcon = convertedView.findViewById(R.id.key_icon);
        keyIcon.setText(String.valueOf(keyViewModel.getKey()));

        TextView keyName = convertedView.findViewById(R.id.key_name);
        keyName.setText(keyViewModel.getKeyName());

        TextView keycode = convertedView.findViewById(R.id.key_code);
        keycode.setText(String.format("0x%02X",keyViewModel.getKeycode()));

        TextView modcode = convertedView.findViewById(R.id.mod_code);
        modcode.setText(String.format("0x%02X",keyViewModel.getModifier()));
        return convertedView;
    }

}
