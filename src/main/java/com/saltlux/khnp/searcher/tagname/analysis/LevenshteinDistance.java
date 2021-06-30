package com.saltlux.khnp.searcher.tagname.analysis;

import com.saltlux.khnp.searcher.tagname.model.TagnameVo;

public class LevenshteinDistance implements DistanceMeasure {

    private int LIMIT;
    public LevenshteinDistance(){}
    public LevenshteinDistance(int limit){
        LIMIT = limit;
    }

    public double distance(String s1, String s2, int limit) {
        if (s1 == null) {
            throw new NullPointerException("s1 must not be null");
        } else if (s2 == null) {
            throw new NullPointerException("s2 must not be null");
        } else if (s1.equals(s2)) {
            return 0.0D;
        } else if (s1.length() == 0) {
            return (double)s2.length();
        } else if (s2.length() == 0) {
            return (double)s1.length();
        } else {
            int[] v0 = new int[s2.length() + 1];
            int[] v1 = new int[s2.length() + 1];

            int i;
            for(i = 0; i < v0.length; v0[i] = i++) {
            }

            for(i = 0; i < s1.length(); ++i) {
                v1[0] = i + 1;
                int minv1 = v1[0];

                for(int j = 0; j < s2.length(); ++j) {
                    int cost = 1;
                    if (s1.charAt(i) == s2.charAt(j)) {
                        cost = 0;
                    }

                    v1[j + 1] = Math.min(v1[j] + 1, Math.min(v0[j + 1] + 1, v0[j] + cost));
                    minv1 = Math.min(minv1, v1[j + 1]);
                }

                if (minv1 >= limit) {
                    return (double)limit;
                }

                int[] vtemp = v0;
                v0 = v1;
                v1 = vtemp;
            }

            return (double)v0[s2.length()];
        }
    }

    @Override
    public double distance(Object a, Object b) {
        if(a instanceof TagnameVo && b instanceof TagnameVo)
            return distance(((TagnameVo) a).getDescription(), ((TagnameVo) b).getDescription(), LIMIT);
        return Double.MAX_VALUE;
    }
}
