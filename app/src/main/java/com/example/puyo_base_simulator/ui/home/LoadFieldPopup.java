package com.example.puyo_base_simulator.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private LoadFieldAdapter.FieldSelectedListener listener;
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
        listener = fieldSelectedListener;
        loadFieldAdapter.setFieldSelectedListener(fieldSelectedListener);
    }

    private void setupView() {
        final @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.popup_load, null);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, GridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, GridLayoutManager.HORIZONTAL));

        loadFieldAdapter = new LoadFieldAdapter(new ArrayList<FieldPreview>());
        recyclerView.setAdapter(loadFieldAdapter);

        Button searchBySeedButton = view.findViewById(R.id.searchBySeedButton);
        searchBySeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.seedEditTextNumberDecimal);
                try {
                    int seed = Integer.parseInt(editText.getText().toString());
                    if (!(0 <= seed && seed <= 65535)) {
                        throw new NumberFormatException();
                    }
                    List<Base> bases = mDB.baseDao().findByHash(seed);
                    List<FieldPreview> fieldPreviews = new ArrayList<>();
                    for (Base base : bases) {
                        fieldPreviews.add(new FieldPreview(base.getId(), seed, base.getField()));
                    }
                    loadFieldAdapter = new LoadFieldAdapter(fieldPreviews);
                    loadFieldAdapter.setFieldSelectedListener(listener);
                    recyclerView.setAdapter(loadFieldAdapter);
                } catch (NumberFormatException e) {
                    editText.setError("should enter 0-65535.");
                }
            }
        });

        Button showAllButton = view.findViewById(R.id.showAllButton);
        showAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Base> bases = mDB.baseDao().getAll();
                List<FieldPreview> fieldPreviews = new ArrayList<>();
                for (Base base : bases) {
                    fieldPreviews.add(new FieldPreview(base.getId(), base.getHash(), base.getField()));
                }
                loadFieldAdapter = new LoadFieldAdapter(fieldPreviews);
                loadFieldAdapter.setFieldSelectedListener(listener);
                recyclerView.setAdapter(loadFieldAdapter);
            }
        });
        setContentView(view);
    }
}