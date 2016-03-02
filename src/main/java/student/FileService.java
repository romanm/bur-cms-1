package student;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Component("fileService")
public class FileService {
	private static final Logger logger = LoggerFactory.getLogger(FileService.class);
	
	@Autowired private PropertiConfig propertiConfig;
	public Map<String, Object> readJsonFromFileName(String fileName) {
		String fileLongName = propertiConfig.folderDb + fileName;
//		fileLongName = fileLongName.trim();
		File file = new File(fileLongName);
		logger.debug(file.toString());
		return readJsonFromFullFileName(file);
	}

	ObjectMapper mapper = new ObjectMapper();

	private Map<String, Object> readJsonFromFullFileName(File file) {
		Map<String, Object> readJsonFileToJavaObject = null;
		try {
			readJsonFileToJavaObject = mapper.readValue(file, Map.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return readJsonFileToJavaObject;
	}

	public void saveJsonToFile(Map<String, Object> javaObjectToJson, String fileName) {
		File file = new File(propertiConfig.folderDb + fileName);
		System.out.println(file);
		ObjectWriter writerWithDefaultPrettyPrinter = mapper.writerWithDefaultPrettyPrinter();
		System.out.println(23);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			writerWithDefaultPrettyPrinter.writeValue(fileOutputStream, javaObjectToJson);
		} catch (IOException e) {
			e.printStackTrace();
		}
		propertiConfig.setNullContentFileTypes();
	}

	public void backup(String fileName) {
		DateTime today = new DateTime();
		String timestampStr = propertiConfig.yyyyMMddHHmmssDateFormat.format(today.toDate());
		try {
			Files.copy(new File(propertiConfig.folderDb + fileName).toPath()
			, new File(propertiConfig.folderDb + "backup/" + fileName +"."+ timestampStr).toPath()
			, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void htmlToResponce(String siteName, String siteType, HttpServletResponse response) {
		Map<String, Object> contentFileTypes = propertiConfig.getContentFileTypes();
		logger.debug(contentFileTypes.toString());
		Map pages = (Map)contentFileTypes.get("pages");
		Map pageContent = (Map) pages.get(siteName);
		logger.debug(""+pageContent);
		String siteFileName ;
		if(pageContent == null){
			siteFileName = siteName;
		}else{
			String fileTypeName = (String) pageContent.get("fileType");
			logger.debug(fileTypeName);
			Map fileTypes = (Map)contentFileTypes.get("fileTypes");
			Map fileType = (Map)fileTypes.get(fileTypeName);
			siteFileName = (String) fileType.get(siteType);
		}
		logger.debug(siteFileName);
		response.setContentType("text/html; charset=UTF-8");
		try {
			String fileName = propertiConfig.getApplicationHome()+propertiConfig.innerWebapp+"/html/"
					+ siteFileName
					+ ".html";
			logger.debug(fileName);
			File file = new File(fileName);
			logger.debug(file.toString());
			Files.copy(file.toPath(), response.getOutputStream());
		} catch (IOException e) {
			InputStream stream = new ByteArrayInputStream(siteName.getBytes(StandardCharsets.UTF_8));
			try {
				IOUtils.copy(stream, response.getOutputStream());
			} catch (IOException e1) {}
			e.printStackTrace();
		}
	}
}
