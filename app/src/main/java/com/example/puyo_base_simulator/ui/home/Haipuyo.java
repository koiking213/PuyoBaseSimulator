package com.example.puyo_base_simulator.ui.home;

import com.example.puyo_base_simulator.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Haipuyo {
    List<String> content = new ArrayList<>();
    // Singleton instance.
    private static final Haipuyo INSTANCE = new Haipuyo();
    private Haipuyo() {}
    public static Haipuyo getInstance() {
        return INSTANCE;
    }

    public void load(BufferedReader br) {
        try {
            for (int i = 0; i < 65536; i++) {
                content.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String get(int seed) {
        if (BuildConfig.DEBUG && !(0 <= seed && seed <= 65535)) {
            throw new AssertionError("Assertion failed");
        }
        return content.get(seed);
    }
}
