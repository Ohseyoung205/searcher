package com.saltlux.khnp.searcher.broker.service;

import com.saltlux.khnp.searcher.broker.model.STTAccessTokenResponse;
import com.saltlux.khnp.searcher.broker.model.STTResponse;
import com.saltlux.khnp.searcher.broker.model.TTSRequest;
import com.saltlux.khnp.searcher.broker.model.TTSResponse;
import com.saltlux.khnp.searcher.common.CommonResponseVo;
import com.saltlux.khnp.searcher.common.libttsapi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BrokerService {

    @Value("${stt.access.uri}")
    private String accessUri;

    @Value("${stt.access.userid}")
    private String userId;

    @Value("${stt.access.userpass}")
    private String userPassword;

    @Value("${stt.sgstt.uri}")
    private String sttUri;

    @Value("${tts.saltlux.uri}")
    private String ttsUri;

    @Value("${tts.voiceware.host}")
    private String ttsHost;

    @Value("${tts.voiceware.port}")
    private Integer ttsPort;

    private String accessToken;

    @Async
    @Scheduled(initialDelay = 0, fixedRate = 60 * 1000 * 12)
    public void sttAccessToken(){
        log.info("Try stt access token issuing");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json", Charset.forName("UTF-8")));

        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(accessUri)
                .queryParam("uid", userId)
                .queryParam("upwd", userPassword)
                .build(false);

        ResponseEntity<STTAccessTokenResponse> response = new RestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<String>(headers),
                STTAccessTokenResponse.class);
        STTAccessTokenResponse result = response.getBody();
        if(!result.isResult())
            throw new RuntimeException(result.getErrMsg());
        accessToken = result.getAccessToken();
        log.info("success issuing stt access token : {}", result.getAccessToken());
    }

    public TTSResponse textToSpeech(TTSRequest request) throws Exception {
        if("saltlux".equalsIgnoreCase(request.getVoiceType())){
            return textToSpeechBySaltlux(request);
        }else{
            return textToSpeechByVoiceware(request);
        }
    }

    public TTSResponse textToSpeechBySaltlux(TTSRequest request){
        final String text = request.getMessage().replaceAll("<([^>]+)>", "");
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(ttsUri)
                .queryParam("text", text)
                .queryParam("voice", request.getVoiceType())
                .queryParam("cache", "false")
                .build(false);

        ResponseEntity<CommonResponseVo> response = new RestTemplate().exchange(
                builder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(null, null),
                CommonResponseVo.class);
        CommonResponseVo result = response.getBody();
        return new TTSResponse(result.getResult().toString());
    }

    public TTSResponse textToSpeechByVoiceware(TTSRequest request) throws Exception {
        final String text = request.getMessage().replaceAll("<([^>]+)>", "");
        final int voiceType = Integer.parseInt(request.getVoiceType());

        libttsapi ttsapi = new libttsapi();
        int result = ttsapi.ttsRequestBuffer(ttsHost, ttsPort, text, voiceType, libttsapi.FORMAT_WAV, libttsapi.TRUE, libttsapi.TRUE);
        if(result != libttsapi.TTS_RESULT_SUCCESS)
            throw new RuntimeException("TTS ERROR CODE : " + result);

        String base64Audio = Base64.encodeBase64String(ttsapi.szVoiceData); //넘어온 binary data => String 로 변경
        return new TTSResponse(base64Audio);
    }

    public String speechToText(String base64Audio) {
        byte[] base64 = Base64.decodeBase64(base64Audio.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> multipart = new LinkedMultiValueMap<>();
        multipart.add("upload_file", new MultipartInputStreamFileResource(new ByteArrayInputStream(base64)));

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity(multipart, headers);
        ResponseEntity<STTResponse> response = new RestTemplate().exchange(sttUri, HttpMethod.POST, requestEntity, STTResponse.class);
        STTResponse result = response.getBody();
        if(!result.isResult())
            throw new RuntimeException(result.getErrMsg());

        return result.getSttList()
                .stream()
                .peek(stt -> log.debug("stt_result : {}", stt.toString()))
                .map(stt -> stt.getSttResult())
                .collect(Collectors.joining());
    }

    static class MultipartInputStreamFileResource extends InputStreamResource {

        private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream) {
            super(inputStream);
            this.filename = sdf.format(new Date()) + ".wav";
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }
    }
}
