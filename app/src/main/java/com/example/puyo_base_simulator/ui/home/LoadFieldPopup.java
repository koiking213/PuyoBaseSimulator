package com.example.puyo_base_simulator.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.example.puyo_base_simulator.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;

public class LoadFieldPopup extends PopupWindow {
    private Context context;
    private RecyclerView recyclerView;
    private LoadFieldAdapter loadFieldAdapter;

    public LoadFieldPopup(Context context){
        super(context);
        this.context = context;
        setupView();
    }

    public void setFieldSelectedListener(LoadFieldAdapter.FieldSelectedListener fieldSelectedListener) {
        loadFieldAdapter.setFieldSelectedListener(fieldSelectedListener);
    }

    private void setupView() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.popup_load, null);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        loadFieldAdapter = new LoadFieldAdapter(FieldPreview.generateFieldPreviewList());
        recyclerView.setAdapter(loadFieldAdapter);

        setContentView(view);
    }
}
