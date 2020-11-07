package com.example.puyo_base_simulator.ui.home;


import com.example.puyo_base_simulator.R;
import java.util.ArrayList;
import java.util.List;

public class FieldPreview {
    public int id;
    public int iconRes;
    public String content;

    public FieldPreview(int id, int iconRes, String content){
        super();
        this.id = id;
        this.iconRes = iconRes;
        this.content = content;
    }

    public static List<FieldPreview> generateFieldPreviewList(){
        List<FieldPreview> fieldPreviews = new ArrayList<>();
        //debug
        fieldPreviews.add(new FieldPreview(0, R.drawable.pr, "hoge"));
        fieldPreviews.add(new FieldPreview(1, R.drawable.pb, "fuga"));

        return fieldPreviews;
    }
}
