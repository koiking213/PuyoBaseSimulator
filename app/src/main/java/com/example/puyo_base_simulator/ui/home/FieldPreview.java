package com.example.puyo_base_simulator.ui.home;


import com.example.puyo_base_simulator.R;
import java.util.ArrayList;
import java.util.List;

public class FieldPreview {
    public int seed;
    public String content;

    public FieldPreview(int seed, String content){
        super();
        this.seed = seed;
        this.content = content;
    }

    public static List<FieldPreview> generateFieldPreviewList(){
        List<FieldPreview> fieldPreviews = new ArrayList<>();
        //debug
        fieldPreviews.add(new FieldPreview(0, "rrr   bbb   yyy"));
        fieldPreviews.add(new FieldPreview(1, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(2, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(3, "rbb   yyy"));
        fieldPreviews.add(new FieldPreview(4, "rbb   yyy"));

        return fieldPreviews;
    }
}
