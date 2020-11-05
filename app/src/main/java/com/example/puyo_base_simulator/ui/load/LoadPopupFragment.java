package com.example.puyo_base_simulator.ui.load;
import com.example.puyo_base_simulator.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

public class LoadPopupFragment extends Fragment implements OnClickListener{

    PopupWindow popupWindow;
    Handler mHandler = new Handler();
    View view;

    //@Override
    //public void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    //setContentView(R.layout.main);

    //    // LayoutInflaterインスタンスを取得
    //    LayoutInflater inflater = (LayoutInflater)getSystemService(
    //            Context.LAYOUT_INFLATER_SERVICE);

    //    // ポップアップ用のViewをpopupxmlから読み込む
    //    View popupView = (View)inflater.inflate(R.layout.popup_load, null);

    //    // レイアウトパラメータをセット
    //    popupView.setLayoutParams(new ViewGroup.LayoutParams(
    //            ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

    //    // PopupWindowを紐づけるViewのインスタンスを取得
    //    view = findViewById(R.id.button);

    //    // viewに紐づけたPopupWindowインスタンスを生成
    //    popupWindow = new PopupWindow(view);

    //    // ポップアップ用のViewをpopupWindowにセットする
    //    popupWindow.setContentView(popupView);

    //    // サイズ(幅)を設定
    //    popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

    //    // サイズ(高さ)を設定
    //    popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

    //    //// 切替ボタンにリスナーを設定
    //    //Button btn = (Button) findViewById(R.id.button);
    //    //btn.setOnClickListener(this);

    //}

    public void onClick(View v){

        // 切替ボタン押下時にポップアップウィンドウの表示、非表示を切り替える
        if(popupWindow.isShowing()){

            popupWindow.dismiss();
        }else{
            popupWindow.showAsDropDown(view, 0, 0);
        }
    }
}