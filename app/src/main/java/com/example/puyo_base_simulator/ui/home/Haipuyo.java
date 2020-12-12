package com.example.puyo_base_simulator.ui.home;

import com.example.puyo_base_simulator.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Haipuyo {
    List<String> content = new ArrayList<>();
    List<String> sortedContent = new ArrayList<>();
    // Singleton instance.
    private static final Haipuyo INSTANCE = new Haipuyo();
    private Haipuyo() {}
    public static Haipuyo getInstance() {
        return INSTANCE;
    }

    public void load(BufferedReader haipuyoBr, BufferedReader sortedBr) {
        try {
            for (int i = 0; i < 65536; i++) {
                content.add(haipuyoBr.readLine());
                sortedContent.add(sortedBr.readLine());
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
    public List<Integer> searchSeedWithPattern(String str) {
        int len = str.length();
        if (BuildConfig.DEBUG && len % 2 != 0) {
            throw new AssertionError("Assertion failed");
        }
        String sortedStr = pairwiseSort(str);
        List<Integer> ret = new ArrayList<>();
        for (int i = 0; i < 65536; i++) {
            if (sortedContent.get(i).startsWith(sortedStr)) {
                ret.add(i);
            }
        }
        return ret;
    }
    private class CharOrder {
        public int index;
        public char chara;
        public CharOrder(int index, char chara) {
            this.index = index;
            this.chara = chara;
        }
    }
    private class CharaOrderComparator implements Comparator<CharOrder> {
        @Override
        public int compare(CharOrder c1, CharOrder c2) {
            //noinspection ComparatorMethodParameterNotUsed
            return c1.index < c2.index ? -1 : 1;
        }
    }
    private String pairwiseSort(String str) {
        String newStr = "";
        for (int i = 0; i < str.length()/2; i++) {
            String substring = str.substring(i*2, i*2+2);
            char[]chars =substring.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);
            newStr = newStr.concat(sorted);
        }
        return newStr;
    }
}
