package com.saltlux.khnp.searcher.search.service;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.saltlux.khnp.searcher.common.libttsapi;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Service
public class BrokerService {
	
	@Value("${broker.talkbotId}")		//톡봇아이디
	public String talkbotId;
	@Value("${broker.ip}")				// 아이피
	public String ip; 
	@Value("${broker.port}") 			// 톡봇 포트
	public String port; 
	@Value("${broker.batch.port}") 		// 음성 batch, token 포트
	public String bPort; 
	@Value("${broker.stt.port}") 		//음성결과 포트
	public int sPort; 
	
	
	public ArrayList<JSONObject> userConverstationList = new ArrayList<JSONObject>(); 			// converstation을 관리하는 arrayList
	//public HashMap<String, String> returnSttMsg = new HashMap<String, String>();				// 음성인식에서 넘어온 결과값을 담는 HashMap
	
	public String stt_uuid = ""; 																// STT음석인식으로 넘어오면 대화세션 UUID를 받는 변수
	public String stt_create_day = null; 														// 토큰발급일자
	public String accessToken = null;    														// 발급받은 token 값
	public int tmpCnt = 0;

	static final RequestConfig config = RequestConfig.custom()
			.setConnectTimeout(10*1000)
			.setConnectionRequestTimeout(10*1000)
			.setSocketTimeout(10*1000)
			.build();
	
	public Map<String, String> khnpBroker(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<>();
		
		String callId = "";
		String chid = request.getParameter("chid");
		String sysid = request.getParameter("sysid");
		String uuid = request.getParameter("uuid");
		String userid = request.getParameter("userid");
		String user_msg = request.getParameter("user_msg");
		String type = request.getParameter("type");
		
		String param = "{\"chid\":\""+chid+"\",\"sysid\":\""+sysid+"\",\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"user_msg\":\""+user_msg+"\",\"user_stt_msg\":\""+user_msg+"\",\"type\":\""+type+"\"}";
		
		if(chid == null || "".equals(chid)) {
			param = jsonString(request);
		}
		
		JSONObject json = new JSONObject();
		json = (JSONObject) JSONValue.parse(param);
		
		System.out.println("json ::"+json);
		
		if(json == null || json.get("chid") == null || "".equals(json.get("chid")) 
				|| json.get("uuid") == null || "".equals(json.get("uuid"))) {					// 채널명과 대화세션 UUID가 존재하지 않으면
			map = returnRslt("", "", "", "", "", "", "-1", "입력값 오류"); 
		}else {
			String tmpType = json.get("type").toString();
			
			if("stt_khnp".equals(json.get("chid").toString())) { 								// 넘어온 채널명이 음성인식이면
				String sttMsg = "";		
				boolean sttToken = false;
				stt_uuid = json.get("uuid").toString();
				Date today = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
				String tmpDay = sdf.format(today);
				if(json.get("user_stt_msg") == null || "".equals(json.get("user_stt_msg"))) { 	// 사용자 음성메세지가 넘어오지 않으면
					map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-2", "음성메시지가 존재하지 않음");
					return map;
				}
				String user_stt_msg = json.get("user_stt_msg").toString();
				if("1".equals(tmpType) || "2".equals(tmpType)) {
					if(stt_create_day == null) { 													// token발급 일자가 없으면
						sttToken = accessToken(); 													// accessToken 발급
						if(sttToken) {
							stt_create_day = tmpDay; 												// token발급 일자를 저장
							sttMsg =  sendBatch(user_stt_msg);									// 음성인식 전송
						}
					} else {
						if(Integer.parseInt(tmpDay) > Integer.parseInt(stt_create_day)) {			// token의 유효시간이 지나면
							sttToken = accessToken();
							if(sttToken) {
								stt_create_day = tmpDay;
								sttMsg =  sendBatch(user_stt_msg);
							}
						} else {
							sttMsg =  sendBatch(user_stt_msg); 								
						}
					}
				}else {
					String conversationId= isUserConverstationId(stt_uuid);
					int chkInt = conversationChk(request, conversationId);
					if(chkInt==1) {
						map = on_cnvs(request, json, conversationId, stt_uuid);
					}else if(chkInt == 0) {
						conversationId = start_cnvs(request, stt_uuid);
						if(conversationId == null) {
							map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-5", "톡봇API접속 오류");
						}else {
							map = on_cnvs(request, json, conversationId, stt_uuid);
						}
					} else {
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-5", "톡봇API접속 오류");
					}
				}
				
				System.out.println("sttMsg ::"+sttMsg);
				if(!"".equals(sttMsg)) { 															
					map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", sttMsg, "0", "");
				} else {
					if("1".equals(tmpType) || "2".equals(tmpType)) {
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-4","음성 인식 전송 오류");
					}else {
						String sttReturn = ttsReturn01(map.get("text_answer").toString());
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), map.get("text_answer").toString(), sttReturn, "0", "");
					}
				}
			}else {																				// text로 넘어오면
				if("2".equals(tmpType)) {
					String sttReturn = ttsReturn01(json.get("user_msg").toString());
					map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), json.get("user_msg").toString(), sttReturn, "0", "");
				}else {
					callId = json.get("uuid").toString();
					String conversationId = isUserConverstationId(callId);
					int chkInt = conversationChk(request, conversationId);
					
					if(chkInt == 1) {
						map = on_cnvs(request, json, conversationId, callId);
					} else if(chkInt == 0) {
						conversationId = start_cnvs(request, callId);
						if(conversationId == null) {
							map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-5", "톡봇API접속 오류");
						}else {
							map = on_cnvs(request, json, conversationId, callId);
						}
					} else {
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), json.get("uuid").toString(), json.get("userid").toString(), "", "", "-5", "톡봇API접속 오류");
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 대화모델 on_cnvs 요청
	 * @param request
	 * @param json
	 * @param conversationId
	 * @param callId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> on_cnvs(HttpServletRequest request, JSONObject json, String conversationId, String callId) throws Exception{
		Map<String, String> map = new HashMap<>();
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			HttpPost req = new HttpPost("http://"+ip+":"+port+"/api/v1/chat/"+talkbotId+"/"+conversationId+"/1");
			StringEntity params = new StringEntity(json.get("user_msg").toString(), "UTF-8");
			req.addHeader("content-type", "application/json");
			req.setEntity(params);
			
			CloseableHttpResponse res = httpClient.execute(req);
			HttpEntity entity = res.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			JsonObject responseJson = new JsonObject(responseString);
			JsonArray jsonMsg = responseJson.getJsonArray("replies");
			
			if(jsonMsg.size() > 0) { 	//정상
				if(!"TEXT".equals(jsonMsg.getJsonObject(0).getString("type"))) {
					JSONObject msgJson = (JSONObject) JSONValue.parse(jsonMsg.getJsonObject(0).getString("message"));
					if(msgJson.get("title") == null) {	//title 존재하지 않으면
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), callId, json.get("userid").toString(), "", "", "-6", "톡봇API 응답 메시지 오류");
					}else {
						map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), callId, json.get("userid").toString(), jsonMsg.getJsonObject(0).getString("message"), "", "0", "");
					}
				}else {
					map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), callId, json.get("userid").toString(), jsonMsg.getJsonObject(0).getString("message"), "", "0", "");
				}
				
			}else {
				map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), callId, json.get("userid").toString(), "", "", "-6", "톡봇API 응답 메시지 오류");
			}
			
		}catch(Exception e) {
			map = returnRslt(json.get("chid").toString(), json.get("sysid").toString(), callId, json.get("userid").toString(), "", "", "-5", "톡봇API접속 오류");
			System.out.println("on_cnvs 접속오류");
		}finally {
			httpClient.close();
		}
		sessionList(callId, conversationId);
		
		return map;
	}
	
	/**
	 * 대화모델 start_cnvs 요청
	 * @param request
	 * @param callId
	 * @return
	 * @throws Exception
	 */
	public String start_cnvs(HttpServletRequest request, String callId) throws Exception{
		String returnConversationid = null;
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			HttpGet req = new HttpGet("http://"+ip+":"+port+"/api/v1/chat/"+talkbotId+"/startConversation");
			req.addHeader("content-type", "application/json");
			CloseableHttpResponse res = httpClient.execute(req);
			HttpEntity entity = res.getEntity();
			String responseMsg = EntityUtils.toString(entity,"UTF-8");
			JsonObject responseJson = new JsonObject(responseMsg);
			JsonArray jsonMsg = responseJson.getJsonArray("replies");
			if(jsonMsg.size() > 0) {  //정상 출력
				returnConversationid = responseJson.getString("conversationId");
				sessionList(callId, returnConversationid);
			}
			
		}catch(Exception e) {
			System.out.println("start_cnvs 접속오류");
		}finally {
			httpClient.close();
		}
		
		return returnConversationid;
	}
	
	/**
	 * 대화모델 stop_cnvs 요청
	 * @param c_id
	 * @throws Exception
	 */
	public void stop_cnvs(String c_id) throws Exception{
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			HttpGet request = new HttpGet("http://"+ip+":"+port+"/api/v1/chat/"+talkbotId+"/stopConversation/"+c_id);
            request.addHeader("content-type", "text/plain");
            httpClient.execute(request);
		}catch(Exception e) {
			System.out.println("stop_cnvs 접속오류");
		}finally {
			httpClient.close();
		}
	}
	
	/**
	 * 컨버스테이션ID가 존재하는지 체크
	 * @param request
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public int conversationChk(HttpServletRequest request, String conversationId) throws Exception{
		int result = 0;

		if(conversationId != null) {
			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			try {
				HttpGet req = new HttpGet("http://"+ip+":"+port+"/api/v1/chat/"+talkbotId+"/getConversationUtterances/"+conversationId);
				req.addHeader("content-type", "application/json");
				CloseableHttpResponse res = httpClient.execute(req);
	            HttpEntity entity = res.getEntity();
	            String responseString = EntityUtils.toString(entity, "UTF-8");
	            if(responseString == null || "".equals(responseString)) {
	            	return 0;
	            }
	            JsonObject responseJson = new JsonObject(responseString);
	            JsonArray responseMsg = responseJson.getJsonArray("transcript");
	            
	            if(responseMsg.size() > 0) {
	        	   result = 1;
	            }
			}catch(Exception e) {
				result = 2;
				System.out.println("getConversationUtterances 접속오류");
			}finally {
				httpClient.close();
			}
		}
		return result;
	}
	
	/**
	 * 대화세션을 저장한다.
	 * @param callid
	 * @param conversationId
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void sessionList(String callid, String conversationId) throws Exception{
		JSONObject obj = new JSONObject();
		long sTime = System.currentTimeMillis();
		if(callid != null && !"".equals(callid)) {
			obj.put("uuid", callid);
			obj.put("lifeTime", (sTime+5*60*1000)); //대화세션 유지시간을 5분으로 설정
			obj.put("conversationId", conversationId);
		}
		
		boolean chk = true;
		if(!userConverstationList.isEmpty()) {
			for(int i=0;i<userConverstationList.size();i++) { //저장된 대화세션 목록을 불러온다.
				if(callid.equals(userConverstationList.get(i).get("uuid").toString())) {
					conversationId = userConverstationList.get(i).get("conversationId").toString();
					userConverstationList.set(i, obj);
					chk = false;
				}
			}
		}
		
		if(chk) {
			userConverstationList.add(obj);
		}
	}
	
	/**
	 * callId의 converstationId가 존재유무
	 * @param callId
	 * @return
	 * @throws Exception
	 */
	public String isUserConverstationId(String callId) throws Exception{
		String returnConverstationId = null;
		long sTime = System.currentTimeMillis();
		if(!userConverstationList.isEmpty()) {
			for(int i=0;i<userConverstationList.size();i++) {
				long ltime = Long.valueOf(userConverstationList.get(i).get("lifeTime").toString());
				if(sTime > ltime) { //일정시간이 지난 대화세션을 삭제한다.
					String c_id = userConverstationList.get(i).get("conversationId").toString();
					stop_cnvs(c_id); 
					userConverstationList.remove(i);
					i--;
				}else if(callId.equals(userConverstationList.get(i).get("uuid").toString())) {
					returnConverstationId = userConverstationList.get(i).get("conversationId").toString();
				}
			}
		}
		return returnConverstationId;
	}
	
	/**
	 * 음성인식에 값을 전달
	 * @param stt_msg
	 * @return
	 * @throws Exception
	 */
	public String sendBatch(String stt_msg) throws Exception{
		System.out.println("######### sendBatch #########");
		String tmp_msg = "";
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
			String tmpNm = sdf.format(today)+".wav";
			
			byte[] b64dec = Base64.decodeBase64(stt_msg.getBytes());
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody("upload_file", b64dec, ContentType.DEFAULT_BINARY, tmpNm);
			HttpEntity entity = builder.build();
			
			HttpPost req = new HttpPost("http://"+ip+":"+bPort+"/SGSAS/API/send_real");
			req.addHeader("accessToken", accessToken);
			req.setEntity(entity);
			
			CloseableHttpResponse res = httpClient.execute(req);
			HttpEntity resEntity = res.getEntity();
			String responseMsg = EntityUtils.toString(resEntity,"UTF-8");
			JsonObject responseJson = new JsonObject(responseMsg);
			System.out.println("responseJson send ::"+responseJson);

			JSONObject sttJson = (JSONObject) JSONValue.parse(responseMsg);
			
			boolean returnResult = (boolean) sttJson.get("result");
			if(returnResult) {
				JSONArray arrJson = (JSONArray) sttJson.get("sttList");

				for(int i=0; i< arrJson.size();i++) {
					JSONObject obj = (JSONObject) arrJson.get(i);
					tmp_msg = tmp_msg +" "+obj.get("sttResult").toString(); //음석인식 STT 결과의 메시지를 합친다.
				}
			}
			
		}catch(Exception e) {
			System.out.println("send_batch 접속오류");
		}finally {
			httpClient.close();
		}
		return tmp_msg;
	}
	
	/**
	 * access token값을 발급 받음
	 * @throws Exception
	 */
	public boolean accessToken() throws Exception{
		boolean resultToken = false;
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		try {
			HttpGet req = new HttpGet("http://"+ip+":"+bPort+"/SGSAS/API/get_token?uid=admin&upwd=rhksflwk!23");
			CloseableHttpResponse res = httpClient.execute(req);
			HttpEntity entity = res.getEntity();
			String responseMsg = EntityUtils.toString(entity,"UTF-8");
			JsonObject responseJson = new JsonObject(responseMsg);
			if(responseJson.getBoolean("result")) {
				resultToken = true;
				accessToken = responseJson.getString("accessToken");
			}
		}catch(Exception e) {
			System.out.println("token 접속오류");
		}finally {
			httpClient.close();
		}
		return resultToken;
	}
	
	public Map<String, String> returnRslt(String chid, String sysid, String uuid, String userid, String text_answer
			, String stt_answer, String status, String error_msg) throws Exception{
		Map<String, String> map = new HashMap<>();
		map.put("chid", chid);
		map.put("sysid", sysid);
		map.put("uuid", uuid);
		map.put("userid", userid);
		map.put("text_answer", text_answer);
		map.put("stt_answer", stt_answer);
		map.put("status", status);
		map.put("error_msg", error_msg);
		
		return map;
	}
	
	/**
	 * 넘어온 parameter 값을 String으로 변경
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String jsonString(HttpServletRequest request) throws Exception{
		request.setCharacterEncoding("UTF-8");
		StringBuffer data = new StringBuffer();
		BufferedReader in = null;
		try {
			in = request.getReader();
			String inputLine = null;
			while((inputLine = in.readLine()) != null) {
				data.append(inputLine);
			}
		}catch(Exception e) {
			System.out.println("error");
		}finally {
			in.close();
		}
		return data.toString();
	}
	
	/**
	 * 음성인식 결과값 binary로 받음
	 * @param text
	 * @throws Exception
	 */
	public void ttsReturn(String text) throws Exception{
		text = text.replaceAll("<([^>]+)>", "");
		int nReturn = 0;
		libttsapi ttsapi = new libttsapi();
		try {
            nReturn = ttsapi.ttsRequestBuffer(ip, sPort, text, 10, libttsapi.FORMAT_WAV, libttsapi.TRUE, libttsapi.TRUE);
		}catch(Exception e) {
			nReturn = -9;
		}
		
		if (nReturn == libttsapi.TTS_RESULT_SUCCESS) {
            byte[] szVoiceData = ttsapi.szVoiceData;
            String returnVoice = Base64.encodeBase64String(szVoiceData); //넘어온 binary data => String 로 변경
        } else {
            System.out.println("TTS Failed (" + nReturn + ")!!!");
        }
		
	}
	
	public String ttsReturn01(String text) throws Exception{
		String returnVoice = "";
		text = text.replaceAll("<([^>]+)>", "");
		int nReturn = 0;
		libttsapi ttsapi = new libttsapi();
		try {
            nReturn = ttsapi.ttsRequestBuffer(ip, sPort, text, 10, libttsapi.FORMAT_WAV, libttsapi.TRUE, libttsapi.TRUE);
		}catch(Exception e) {
			nReturn = -9;
		}
		
		if (nReturn == libttsapi.TTS_RESULT_SUCCESS) {
            byte[] szVoiceData = ttsapi.szVoiceData;
            returnVoice = Base64.encodeBase64String(szVoiceData); //넘어온 binary data => String 로 변경
        } else {
            System.out.println("TTS Failed (" + nReturn + ")!!!");
        }
		
		return returnVoice;
		
	}

}
