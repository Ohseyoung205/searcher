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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IndexHtmChange {
	
	@Value("${htm.file.path}")
    private String htmFilePath;
	
	public boolean indexHtmChange(String domain, String htmFileName) throws Exception{
		boolean btn = false;
		String[] fileNm = htmFileName.split(";");
		try {
			for(int i=0;i<fileNm.length; i++) {
				HashMap<String, String> htmlMap = new LinkedHashMap<>();
				String readLine = null ;
				int lineNum = 0;
				String oriFilePath = htmFilePath+File.separator+domain+File.separator+fileNm[i];

				File file = new File(oriFilePath);
				BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
				
				while((readLine =  bufReader.readLine()) != null ){
					lineNum++;
					readLine = readLine.replaceAll("<IMG src=\".\\\\", "<IMG src=\"/resources/"+domain+"/");
					htmlMap.put(lineNum+"", readLine+"\r\n");
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
