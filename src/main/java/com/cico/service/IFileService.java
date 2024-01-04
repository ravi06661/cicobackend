package com.cico.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface IFileService {

	public String uploadFileInFolder(MultipartFile file,String path);
	
	 public InputStream getImages(String fileName,String destination);
	 
	 public InputStream getAttachment(String attachment);
	 
	 public void deleteImagesInFolder(List<String> images,String path);

	public Resource loadFileAsResource(String filename, String destination);	 

}