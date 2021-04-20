package com.saltlux.khnp.searcher;

import ai.saltlux.nlu.conf.ConfigManager;
import ai.saltlux.nlu.conf.NluConfig;
import ai.saltlux.nlu.semantic.SemanticAnalyzer;

import java.io.File;

public class NLUTest {
    public static void main(String[] args) throws Exception {
        String confPath = "D:\\workspace\\2021_khnp_searcher\\host_nh_bank.cf";
        NluConfig nluconfig = ConfigManager.getinstance().loadConfig("nh_bank", new File(confPath), NluConfig.class);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(nluconfig);
        analyzer.analyseQuestion("우리 아이오이이는 정말 이뻐요");
    }
}
