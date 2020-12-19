package com.example.puyo_base_simulator.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puyo_base_simulator.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

public class HomeFragment extends Fragment implements HomeContract.View {
    ImageView[][] fieldView;
    ImageView[][] currentPuyoView;
    ImageView[][] nextPuyoView;
    GridLayout currentPuyoLayout;
    GridLayout nextPuyoLayout;
    private HomePresenter mPresenter;
    private Activity mActivity;
    private View mRoot;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRoot = root;
        Activity activity = getActivity();
        assert activity != null;
        mActivity = activity;

        // current puyo area
        currentPuyoView = new ImageView[3][7];
        currentPuyoLayout = root.findViewById(R.id.currentPuyoLayout);
        for(int i=0;i<3;i++){
            for(int j=0;j<7;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                view.setImageResource(R.drawable.blank);
                currentPuyoLayout.addView(view);
                currentPuyoView[i][j] = view;
            }
        }

        // next puyo area
        nextPuyoView = new ImageView[4][2];
        nextPuyoLayout = root.findViewById(R.id.nextPuyoLayout);
        ImageView[][] views = new ImageView[4][2];
        for(int i=0;i<4;i++){
            for(int j=0;j<2;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                view.setImageResource(R.drawable.blank);
                nextPuyoLayout.addView(view);
                views[i][j] = view;
            }
        }
        nextPuyoView[0][0] = views[0][0];
        nextPuyoView[0][1] = views[1][0];
        nextPuyoView[1][0] = views[2][1];
        nextPuyoView[1][1] = views[3][1];

        // main field
        final GridLayout fieldLayout = root.findViewById(R.id.fieldLayout);
        fieldView = new ImageView[14][8];
        for(int i=0;i<14;i++){
            for(int j=0;j<8;j++){
                ImageView view = new ImageView(getActivity());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                view.setLayoutParams(params);
                fieldLayout.addView(view);
                fieldView[13-i][j] = view;
            }
        }

        // wall
        for (int i=0; i<14; i++) {
            fieldView[i][0].setImageResource(R.drawable.wall);
            fieldView[i][7].setImageResource(R.drawable.wall);
        }
        for (int j=1; j<7; j++) {
            fieldView[0][j].setImageResource(R.drawable.wall);
        }

        ((TextView)root.findViewById(R.id.pointTextView)).setText("0点");
        mPresenter = new HomePresenter(this, requireActivity().getAssets(), mActivity);

        // ボタン群
        root.findViewById(R.id.buttonUndo).setOnClickListener(v -> mPresenter.undo());
        root.findViewById(R.id.buttonRedo).setOnClickListener(v -> mPresenter.redo());
        root.findViewById(R.id.buttonSave).setOnClickListener(v -> mPresenter.save());
        root.findViewById(R.id.buttonLoad).setOnClickListener(v -> {
            final LoadFieldPopup loadFieldPopup = new LoadFieldPopup(mActivity);
            loadFieldPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
            loadFieldPopup.setOutsideTouchable(true);
            loadFieldPopup.setFocusable(true);
            loadFieldPopup.showAsDropDown(mRoot.findViewById(R.id.buttonLoad));
            loadFieldPopup.setFieldSelectedListener((position, fieldPreview) -> {
                mPresenter.load(fieldPreview);
                loadFieldPopup.dismiss();
            });
        });
        root.findViewById(R.id.buttonLeft).setOnClickListener(v -> mPresenter.moveLeft());
        root.findViewById(R.id.buttonRight).setOnClickListener(v -> mPresenter.moveRight());
        root.findViewById(R.id.buttonDown).setOnClickListener(v -> mPresenter.dropDown());
        root.findViewById(R.id.buttonA).setOnClickListener(v -> mPresenter.rotateLeft());
        root.findViewById(R.id.buttonB).setOnClickListener(v -> mPresenter.rotateRight());
        root.findViewById(R.id.buttonSetSeed).setOnClickListener(v -> mPresenter.setSeed());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public int getSpecifiedSeed() {
        EditText editText = mRoot.findViewById(R.id.editTextSeed);
        try {
            int seed = Integer.parseInt(editText.getText().toString());
            if (0 <= seed && seed <= 65535) {
                return seed;
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            editText.setError("should enter 0-65535.");
            throw e;
        }
    }

    public void setSeedText(int seed) {
        TextView view = mRoot.findViewById(R.id.textViewSeed);
        view.setText(String.format(Locale.JAPAN, "seed: %d", seed));
    }

    public void drawField(final Field field) {
        for (int i=1; i<14; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field.field[i][j];
                fieldView[i][j].setImageResource(getPuyoImage(puyo.color));
            }
        }
    }

    public void drawDisappearField(Field field) {
        for (int i=1; i<14; i++) {
            for (int j=1; j<7; j++) {
                Puyo puyo = field.field[i][j];
                if (field.isDisappear(puyo)) {
                    fieldView[i][j].setImageResource(R.drawable.disappear);
                } else {
                    fieldView[i][j].setImageResource(getPuyoImage(puyo.color));
                }
            }
        }
    }

    public void drawPoint(final String text) {
        mActivity.runOnUiThread(() -> ((TextView)mRoot.findViewById(R.id.pointTextView)).setText(text));
    }

    int getPuyoImage(PuyoColor color) {
        switch (color) {
            case RED:
                return R.drawable.pr;
            case BLUE:
                return R.drawable.pb;
            case YELLOW:
                return R.drawable.py;
            case GREEN:
                return R.drawable.pg;
            case PURPLE:
                return R.drawable.pp;
            case EMPTY:
                return R.drawable.blank;
            default:
                return -1;
        }
    }

    int getDotImage(PuyoColor color) {
        switch (color) {
            case RED:
                return R.drawable.dotr;
            case BLUE:
                return R.drawable.dotb;
            case YELLOW:
                return R.drawable.doty;
            case GREEN:
                return R.drawable.dotg;
            case PURPLE:
                return R.drawable.dotp;
            default:
                return -1;
        }
    }

    public void update(Field field, TsumoInfo tsumoInfo) {
        drawField(field);
        drawTsumo(tsumoInfo, field);
    }

    // リストで渡された順に下から積み上げる
    void drawDot(int column, List<PuyoColor> colors, Field field) {
        int row = field.heights[column] + 1;
        for (PuyoColor color : colors) {
            if (row <= 13) {
                fieldView[row++][column].setImageResource(getDotImage(color));
            }
        }
    }

    public void drawTsumo(TsumoInfo tsumoInfo, Field field) {
        // draw current
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 7; j++) {
                currentPuyoView[i][j].setImageResource(R.drawable.blank);
            }
        }
        int jikuColor = getPuyoImage(tsumoInfo.currentColor[0]);
        int nonJikuColor = getPuyoImage(tsumoInfo.currentColor[1]);
        // draw jiku-puyo
        currentPuyoView[tsumoInfo.currentMainPos[0]][tsumoInfo.currentMainPos[1]].setImageResource(jikuColor);

        // draw not-jiku-puyo
        currentPuyoView[tsumoInfo.currentSubPos[0]][tsumoInfo.currentSubPos[1]].setImageResource(nonJikuColor);

        // draw next and next next
        for (int i=0; i<2; i++) {
            for (int j=0; j<2; j++) {
                nextPuyoView[i][j].setImageResource(getPuyoImage(tsumoInfo.nextColor[i][j]));
            }
        }

        // draw dot
        PuyoColor[] currentColor = new PuyoColor[2];
        currentColor[0] = tsumoInfo.currentColor[0];
        currentColor[1] = tsumoInfo.currentColor[1];
        Rotation currentCursorRotate = tsumoInfo.currentCursorRotate;
        switch (currentCursorRotate) {
            case DEGREE0:
                drawDot(tsumoInfo.currentMainPos[1], Arrays.asList(currentColor[0], currentColor[1]), field);
                break;
            case DEGREE90:
            case DEGREE270:
                drawDot(tsumoInfo.currentMainPos[1], Collections.singletonList(currentColor[0]), field);
                drawDot(tsumoInfo.currentSubPos[1], Collections.singletonList(currentColor[1]), field);
                break;
            case DEGREE180:
                drawDot(tsumoInfo.currentMainPos[1], Arrays.asList(currentColor[1], currentColor[0]), field);
                break;
        }
    }

    public void disableUndoButton() {
        mRoot.findViewById(R.id.buttonUndo).setEnabled(false);
    }

    public void enableUndoButton() {
        mRoot.findViewById(R.id.buttonUndo).setEnabled(true);
    }

    public void disableRedoButton() {
        mRoot.findViewById(R.id.buttonRedo).setEnabled(false);
    }

    public void enableRedoButton() {
        mRoot.findViewById(R.id.buttonRedo).setEnabled(true);
    }

    public void disableAllButtons() {
        mRoot.findViewById(R.id.buttonLeft).setEnabled(false);
        mRoot.findViewById(R.id.buttonRight).setEnabled(false);
        mRoot.findViewById(R.id.buttonDown).setEnabled(false);
        mRoot.findViewById(R.id.buttonA).setEnabled(false);
        mRoot.findViewById(R.id.buttonB).setEnabled(false);
        mRoot.findViewById(R.id.buttonUndo).setEnabled(false);
        mRoot.findViewById(R.id.buttonRedo).setEnabled(false);
    }

    public void enableAllButtons() {
        mRoot.findViewById(R.id.buttonLeft).setEnabled(true);
        mRoot.findViewById(R.id.buttonRight).setEnabled(true);
        mRoot.findViewById(R.id.buttonDown).setEnabled(true);
        mRoot.findViewById(R.id.buttonA).setEnabled(true);
        mRoot.findViewById(R.id.buttonB).setEnabled(true);
        mRoot.findViewById(R.id.buttonUndo).setEnabled(true);
        mRoot.findViewById(R.id.buttonRedo).setEnabled(true);
    }

    public void eraseCurrentPuyo() {
        for(int i=0;i<3;i++){
            for(int j=0;j<7;j++){
                currentPuyoView[i][j].setImageResource(R.drawable.blank);
            }
        }
    }
}