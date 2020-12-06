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
    List<String> sortedContent;
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
            sortedContent = generateSortedContent(content);
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
    private List<String> generateSortedContent(List<String> content) {
        List<String> ret = new ArrayList<>();
        for (String orig : content) {
            String str = orig;
            List<CharOrder> order = new ArrayList<>();
            // rgbyp -> abcde
            order.add(new CharOrder(str.indexOf('r'), 'r'));
            order.add(new CharOrder(str.indexOf('g'), 'g'));
            order.add(new CharOrder(str.indexOf('b'), 'b'));
            order.add(new CharOrder(str.indexOf('y'), 'y'));
            order.add(new CharOrder(str.indexOf('p'), 'p'));
            order.sort(new CharaOrderComparator());
            // blueとabcdのbがかぶっているので最後にbを置換する
            // 4色なので、無い色に対してindexOfが-1を返している
            str = str.replace(order.get(1).chara, 'a');
            str = str.replace(order.get(3).chara, 'c');
            str = str.replace(order.get(4).chara, 'd');
            str = str.replace(order.get(2).chara, 'b');
            // sort each pair
            ret.add(pairwiseSort(str));
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
