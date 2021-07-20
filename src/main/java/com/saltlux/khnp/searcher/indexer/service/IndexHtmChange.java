package com.saltlux.khnp.searcher.indexer.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndexHtmChange {
	
	@Value("${htm.file.path}")
    private String htmFilePath;
	
	@Value("${pattern1.tags0}")
	private String tags0;
	
	@Value("${pattern.img}")
	private String ptnImg;
	
	@Value("${pattern.number}")
	private String ptnNum;
	
	public boolean indexHtmChange(String domain, String htmFileName) throws Exception{
		boolean btn = false;
		String[] fileNm = htmFileName.split(";");
		Pattern tags = Pattern.compile(tags0);
		String pattern0 = "══(.*?)══";
		String pattern1 = "__(.*?)__";
		String pattern2 = "──(.*?)──";
		String pattern3 = "󰡈󰡈󰡈(.*?)󰡈󰡈󰡈";
		String pattern4 = "\\\\?󰠧󰠧(.*?)?󰠧󰠧";
		String pattern6 = "\\\\?󰠏󰠏(.*?)?󰠏󰠏";
		String pattern7 = "󰠏󰠏󰠏󰠏󰠏󰠏󰠏󰠏(.*?)";
		String pattern8 = "^[0-9]+";
		Matcher m;
		Pattern ptns = Pattern.compile(ptnImg);
		System.out.println("ptnNum ::"+ptnNum);
		try {
			for(int i=0;i<fileNm.length; i++) {
				HashMap<String, String> htmlMap = new LinkedHashMap<>();
				String readLine = null ;
				int lineNum = 0;
				String oriFilePath = htmFilePath+File.separator+domain+File.separator+fileNm[i];
			
				File file = new File(oriFilePath);
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				String tagsHtm = "";
				while((readLine =  bufReader.readLine()) != null ){
					lineNum++;
					
					readLine = readLine.replaceAll("font-family:(.*?);", "");
					if(readLine.contains("font-size:36.0pt;") || readLine.contains("font-size:12.0pt;")) {
						if(!"font-weight:bold;".contains(readLine)) {
							readLine = readLine.replaceAll("font-size:36.0pt;", "font-size:36.0pt;font-family:견고딕;font-weight:bold;");
							readLine = readLine.replaceAll("font-size:12.0pt;", "font-size:12.0pt;font-family:견고딕;font-weight:bold;");
						}
					}
				
					readLine = readLine.replaceAll("<SPAN STYLE=''>", "<SPAN STYLE='font-family:견고딕;font-weight:bold;'>");
					String[] imagePath = domain.split("/");
					String imagePath1 = "";
					for(int k=1;k<imagePath.length;k++) {
						if(k == 1) {
							imagePath1="/"+imagePath[k];
						}else {
							imagePath1=imagePath1+"/"+imagePath[k];
						}
					}
				
					m = ptns.matcher(readLine);
					while (m.find()) {
						String img1 = m.group();
						img1 = img1.replace("\\", "\\\\");
						String[] sImg = img1.split("\\\\");
						readLine = readLine.replaceAll(img1, "<IMG src=\".\\\\"+sImg[sImg.length-1]);
					}
				
					readLine = readLine.replaceAll("<IMG src=\".\\\\", "<IMG src=\""+imagePath1+"/");
					tagsHtm = tags.matcher(readLine).replaceAll("").replaceAll("&nbsp;", "");
					
					if(tagsHtm.matches(pattern0)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern1)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern2)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern3)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern4)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern6)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}else if(tagsHtm.matches(pattern7)) {
						readLine = readLine.replace(tagsHtm, "<hr>");
					}
	
					htmlMap.put(lineNum+"", readLine+"\r\n");
				}
				
				int chkNum = 0;
				int chkTwo = 0;
				String tmpStr = "";
				for( Map.Entry<String, String> hElem : htmlMap.entrySet() ){
					if(hElem.getValue().contains("<TABLE border=\"1\" cellspacing=\"0\" cellpadding=\"0\" style='border-collapse:collapse;border:none;'>")) {
						chkNum = Integer.parseInt(hElem.getKey());
					}
					if(hElem.getValue().contains("</TABLE>")) {
						chkTwo = Integer.parseInt(hElem.getKey());
						
						boolean chkBln = true;
						for(int l =chkNum; l < chkTwo; l++) {
							tmpStr = tags.matcher(htmlMap.get(String.valueOf(l))).replaceAll("").replaceAll("&nbsp;", "");
							if(!tmpStr.trim().matches(ptnNum) && !"".equals(tmpStr.trim())) {
								chkBln = false;
								break;
							}
						}
						if(chkBln) {
							for(int j = chkNum; j <=chkTwo; j++) {
								if(chkTwo == j) {
									htmlMap.put(j+"", htmlMap.get(String.valueOf(j)).replaceAll("</TABLE>", ""));
								}else {
									htmlMap.put(j+"", "");
								}
							}
						}
					}
					
				}
				
				bufReader.close();
				
				File copyFile = new File(oriFilePath);
				FileOutputStream fos = new FileOutputStream(copyFile); //복사할파일
				for( Map.Entry<String, String> elem : htmlMap.entrySet() ){
					fos.write(elem.getValue().getBytes());
			    }
				fos.close();
				
				Path filePath = Paths.get(oriFilePath);
				//############### 파일 소유자 변경 ###############
				UserPrincipal hostUid = filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("saltlux");
				Files.setOwner(filePath, hostUid);
				//############### 파일 그룹 변경 ################
				GroupPrincipal group =filePath.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByGroupName("saltlux");
				Files.getFileAttributeView(filePath, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);
				btn = true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("error");
		}
		
		
		return btn;
	}

}
