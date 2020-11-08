package com.example.puyo_base_simulator.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import com.example.puyo_base_simulator.R;
import com.example.puyo_base_simulator.data.AppDatabase;
import com.example.puyo_base_simulator.data.Base;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class LoadFieldPopup extends PopupWindow {
    private Context context;
    private RecyclerView recyclerView;
    private LoadFieldAdapter loadFieldAdapter;
    // should be initialized in Presenter?
    AppDatabase mDB;

    public LoadFieldPopup(Context context){
        super(context);
        this.context = context;
        mDB = Room.databaseBuilder(context,
                AppDatabase.class, "database-name")
                .allowMainThreadQueries()
                .build();
        setupView();
    }

    public void setFieldSelectedListener(LoadFieldAdapter.FieldSelectedListener fieldSelectedListener) {
        loadFieldAdapter.setFieldSelectedListener(fieldSelectedListener);
    }

    private void setupView() {
        final @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.popup_load, null);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        loadFieldAdapter = new LoadFieldAdapter(FieldPreview.generateFieldPreviewList());
        recyclerView.setAdapter(loadFieldAdapter);

        Button searchBySeedButton = view.findViewById(R.id.searchBySeedButton);
        searchBySeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.seedEditTextNumberDecimal);
                int seed = Integer.parseInt(editText.getText().toString());
                List<Base> bases = mDB.baseDao().findByHash(seed);
                List<FieldPreview> fieldPreviews = new ArrayList<>();
                for (Base base : bases) {
                    fieldPreviews.add(new FieldPreview(seed, base.getField()));
                }
                recyclerView.setAdapter(new LoadFieldAdapter(fieldPreviews));
            }
        });
        setContentView(view);
    }
}
