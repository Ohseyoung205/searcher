package com.saltlux.khnp.searcher;

import com.saltlux.khnp.searcher.broker.model.STTResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

public class STTRequestTest {

    public static void main(String[] args) throws Exception {
        File wav = new File("C:\\Users\\jhjeon\\Downloads\\test.wav");
//        byte[] out = Base64.decodeBase64(FileUtils.readFileToByteArray(wav));
        String out = Base64.encodeBase64String(FileUtils.readFileToByteArray(wav));
        System.out.println(out);

        /*String uri = "http://192.168.219.2:8081/SGSAS/API/send_real";
        String accessToken = null;
        byte[] base64 = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("accessToken", accessToken);

        LinkedMultiValueMap<String, Object> multipart = new LinkedMultiValueMap<String, Object>();
        multipart.add("upload_file", base64);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity =
                new HttpEntity<LinkedMultiValueMap<String, Object>>(multipart, headers);

        ResponseEntity<STTResponse> response = new RestTemplate().exchange(
                uri, HttpMethod.POST, requestEntity, STTResponse.class);
        System.out.println(response.getBody().toString());*/
    }
}
