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

    public static List<FieldPreview> generateFieldPreviewList(){
        List<FieldPreview> fieldPreviews = new ArrayList<>();
        //debug
        fieldPreviews.add(new FieldPreview(0, 124, "rrr   bbb   yyy"));
        fieldPreviews.add(new FieldPreview(1, 124, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(2, 124, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(3, 124, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(4, 124, "rbbybbyyy"));

        return fieldPreviews;
    }
}
