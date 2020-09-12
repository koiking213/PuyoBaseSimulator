package com.example.puyo_base_simulator.ui.home;

public class HomePresenter implements HomeContract.Presenter {
    HomeFragment mView;
    HomePresenter (HomeFragment view) {
        mView = view;
    }
    public void rotateLeft() {};

    public void rotateRight() {};

    public void moveLeft() {};

    public void moveRight() {};

    public void dropDown() {};

    public void undo() {};

    public void redo() {};
}
