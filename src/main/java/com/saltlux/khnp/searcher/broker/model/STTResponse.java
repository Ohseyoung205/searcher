package com.saltlux.khnp.searcher.broker.model;


import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class STTResponse {

    /*callId	2	String	STT call ID
    sttSeq	2	int	STT key
    seq	2	int	음성 인식 순서
    fileId	2	String	파일 키	파일 고유키
    fileName	2	String	파일 명
    sttResult	2	String	음성인식 결과(Text)
    channel	2	int	파일 채널	스테레오 인식 시 사용
    frames	2	double	음성인식 구간 시작 시간 (초단위)
    frameEp	2	double	음성인식 구간 종료 시간 (초단위)
    regDate	2	String	파일 등록일
    regTime	2	String	파일 등록시간
    duration	2	String	파일 총 길이(시:분:초)*/

    private boolean result;

    private String errMsg;

    private List<STTResult> sttList;

    @Getter
    @ToString
    public static class STTResult{

        private String callId;

        private int sttSeq;

        private int seq;

        private String fileId;

        private String fileName;

        private String sttResult;

        private int channel;

        private double frames;

        private double frameEp;

        private String regDate;

        private String regTime;

        private String duration;
    }
}
