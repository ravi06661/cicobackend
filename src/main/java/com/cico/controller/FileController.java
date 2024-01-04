package com.cico.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cico.service.IFileService;
import com.cico.service.impl.FileServiceImpl;

@RestController
@RequestMapping("/file")
@CrossOrigin("*")
public class FileController {

	@Autowired
	private IFileService fileService;

//	@RequestMapping(value = "/getImageApi/{destination}/{fileName}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
//	public void getImage(@PathVariable("fileName") String fileName,@PathVariable("destination") String destination ,HttpServletResponse response) throws IOException {
//         System.err.println(destination+"   "+fileName);
//		InputStream data = fileService.getImages(fileName,destination);
//		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//		StreamUtils.copy(data, response.getOutputStream());
//
//	}

	@GetMapping("/download/{destination}/{filename}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename,
			@PathVariable("destination") String destination) {
		// Load your file using appropriate service
		Resource file = fileService.loadFileAsResource(filename, destination);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	public FileController(FileServiceImpl mediaService) {
		this.fileService = mediaService;
	} 

	@RequestMapping(value = "/getImageApi/{destination}/{fileName}", method = RequestMethod.GET, produces = MediaType.ALL_VALUE)
	public void getImage(@PathVariable("fileName") String fileName, @PathVariable("destination") String destination,
			HttpServletResponse response) throws IOException {
		InputStream data = fileService.getImages(fileName, destination);

		// Determine content type based on file extension
		String contentType = determineContentType(fileName);
		response.setContentType(contentType);

		StreamUtils.copy(data, response.getOutputStream());
	}

	private String determineContentType(String fileName) {
	    // Determine content type based on file extension
	    String extension = FilenameUtils.getExtension(fileName);

	    switch (extension.toLowerCase()) {
	        case "jpg":
	        case "jpeg":
	        case "png":   
	            return MediaType.IMAGE_JPEG_VALUE;
	        case "mp4":
	            return "video/mp4";
	        case "mp3":
	            return "audio/mpeg";
	        case "aac":
	            return "audio/aac";
	        case "wav":
	            return "audio/wav";
	        case "amr":
	            return "audio/amr";
	        case "pcm":
	            return "audio/wav";
	        case "m4a":
	            return "audio/mp4";
	        // Additional audio formats
	        case "ogg":
	            return "audio/ogg";
	        case "flac":
	            return "audio/flac";
	        case "3gp":
	            return "audio/3gp";     
	        // Add more audio formats as needed
	        default:
	            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
	    }
	}

}
