package student;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiConfig {
	@Autowired private FileService fileService;
	public final static String innerWebapp = "src/main/webapp/";
	
	@Value("${application.home:/tmp}")
	private String applicationHome;
	public String getApplicationHome() {
		return applicationHome;
	}

	@Value("${file.common_content:fcc.json}")
	public  String fileCommonContent;
	
	@Value("${folder.db:/tmp}")
	public  String folderDb;

	Map contentFileTypes = null;

	public final static SimpleDateFormat yyyyMMddHHmmssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public void setNullContentFileTypes(){
		contentFileTypes = null;
	}
	public Map<String, Object> getContentFileTypes(){
		if(contentFileTypes == null){
			contentFileTypes = 
					fileService.readJsonFromFileName(fileCommonContent);
			contentFileTypes.remove("title");
			Map map = (Map)contentFileTypes.get("pages");
			for (String pageName : (Set<String>) map.keySet()) {
				Map pageContent = (Map) map.get(pageName);
				pageContent.remove("title");
				pageContent.remove("html");
			}
		}
		return contentFileTypes;
	}

	public String applicationViewPath() {
		return getApplicationHome() + innerWebapp + folderPublicFiles;
	}

	@Value("${folder.public.files:/view}") 
	public  String folderPublicFiles;


}
