package com.example.puyo_base_simulator.ui.home;

import java.util.Stack;

public class StackWithButton<T> extends Stack<T> {
    ButtonUpdateFunction enableFun;
    ButtonUpdateFunction disableFun;

    StackWithButton(ButtonUpdateFunction enableFun, ButtonUpdateFunction disableFun) {
        super();
        this.enableFun = enableFun;
        this.disableFun = disableFun;
        disableFun.func();
    }

    @Override
    public T push(T elm) {
        enableFun.func();
        return super.push(elm);
    }

    @Override
    public T pop() {
        T elm =  super.pop();
        if (super.isEmpty()) disableFun.func();
        return elm;
    }

    @Override
    public void clear() {
        super.clear();
        disableFun.func();
    }
}
