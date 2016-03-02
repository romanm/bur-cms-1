package student;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CmsCommonRest {
	private static final Logger logger = LoggerFactory.getLogger(CmsCommonRest.class);
	@Autowired private FileService fileService;
	@Autowired private PropertiConfig propertiConfig;

	@RequestMapping(value = "/v/{siteName}", method = RequestMethod.GET)
	public void restForViewHtml(@PathVariable String siteName, HttpServletResponse response) {
		logger.debug(siteName);
		fileService.htmlToResponce(siteName,"view", response);
	}

	@RequestMapping(value = "/v/readContent", method = RequestMethod.GET)
	public  @ResponseBody Map<String, Object> readBugTinyWiki() {
		logger.debug("------------------------");
		Map<String, Object> readJsonFromFile = 
				fileService.readJsonFromFileName(propertiConfig.fileCommonContent);
		logger.debug(""+readJsonFromFile);
		return readJsonFromFile;
	}

	@RequestMapping(value = "/saveCommonContent", method = RequestMethod.POST)
	public  @ResponseBody Map<String, Object> saveCommonContent(@RequestBody Map<String, Object> commonContentJavaObject) {
		logger.debug("/saveCommonContent");
		fileService.saveJsonToFile(commonContentJavaObject,propertiConfig.fileCommonContent);
		logger.debug("2");
		fileService.backup(propertiConfig.fileCommonContent);
		logger.debug("3");
		return commonContentJavaObject;
	}
	@RequestMapping(value = "/read_user", method = RequestMethod.GET)
	public  @ResponseBody Principal getRoleTypes(Principal userPrincipal) {
		return userPrincipal;
	}

}
