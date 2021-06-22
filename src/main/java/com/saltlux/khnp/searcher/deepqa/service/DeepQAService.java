package com.saltlux.khnp.searcher.deepqa.service;

import com.saltlux.khnp.searcher.deepqa.analyzer.TMSAnalyzer;
import com.saltlux.khnp.searcher.deepqa.model.DeepQAHtmlWrapper;
import com.saltlux.khnp.searcher.deepqa.model.DeepQARequest;
import com.saltlux.khnp.searcher.deepqa.model.DeepQAResult;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeepQAService {

    @Value("${deepqa.uri}")
    private String deepQAUri;

    @Autowired
    TMSAnalyzer analyzer;

    @Value("${deepqa.wrapper.tag}")
    private String[] tag;

    @PostConstruct
    // ssl security Exception 방지
    private static void disableSslVerification() {
        try
        {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public DeepQAResult deepQA(DeepQARequest request){
        DeepQAHtmlWrapper wrapper = new DeepQAHtmlWrapper(Jsoup.parse(request.getContext()), analyzer);

        HashMap<String, String> parameters = new HashMap<>();
        String questionParam = analyzer.getPOS(request.getQuestion());
        String contextParam = wrapper.joinMorphTexts();
        parameters.put("question", questionParam);
        parameters.put("context", contextParam);

        ResponseEntity<DeepQAResult> res = new RestTemplate().postForEntity(deepQAUri, parameters, DeepQAResult.class);
        if(!res.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException(res.getStatusCode().getReasonPhrase());
        }

        DeepQAResult result = res.getBody();

        List<Integer> keyList = wrapper.matches(result.getStart(), result.getEnd());
        String context = wrapper.getTexts().entrySet().stream()
                .map(e -> {
                    // body 제거
                    if("<body>".equalsIgnoreCase(e.getValue()) || "</body>".equalsIgnoreCase(e.getValue()))
                        return "";
                    if(keyList.contains(e.getKey()))
                        return e.setValue(String.format("%s%s%s", tag[0], e.getValue(), tag[1]));
                    return e.getValue();
                }).collect(Collectors.joining());

        result.setContext(context);
        return result;
    }
}
