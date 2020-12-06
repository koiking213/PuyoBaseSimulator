package com.example.puyo_base_simulator.ui.home;


import com.example.puyo_base_simulator.R;
import java.util.ArrayList;
import java.util.List;

public class FieldPreview {
    public int id;
    public int seed;
    public String content;

    public FieldPreview(int id, int seed, String content){
        super();
        this.id = id;
        this.seed = seed;
        this.content = content;
    }
}
