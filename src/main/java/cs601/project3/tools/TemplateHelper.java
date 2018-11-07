package cs601.project3.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Helper class for dynamically generating html code with data given
 * @author yangzun
 *
 */
public class TemplateHelper {

	/**
	 * generate a well formed table on html page with structured data
	 * @param data
	 * @return
	 */
	public static String generateTable(List<String[]> data) {
		//if <table> tag has existed
		boolean openedTable = false;
		StringBuffer sb = new StringBuffer();
		for (String[] lineData : data) {
			if(lineData.length == 1) {
				//if length is 1, lineData shoule be put into a <h> tag
				if(openedTable) {
					sb.append("</table>\n");
					openedTable = false;
				}
				sb.append("<h2>"+lineData[0]+"</h2><br/>");
				sb.append("<table>\n");
				openedTable = true;
				continue;
			}
			if(!openedTable) {
				sb.append("<table>\n");
				openedTable = true;
			}
			sb.append("<tr>\n");
			for (String colunmData : lineData) {
				colunmData = StringEscapeUtils.escapeHtml4(StringEscapeUtils.unescapeHtml4(colunmData));
				sb.append("<td>"+colunmData+"</td>\n");
			}
			sb.append("</tr>\n");
		}
		if(openedTable) {
			sb.append("</table><br/>\n");
		}
		return sb.toString();
	}
	
	/**
	 * generate prev and next page button with pageInfo data given
	 * when button clicked, jsPost method in javascript on html will be called
	 * @param pageInfo
	 * @param searchTerms
	 * @return
	 */
	public static String generatePrevNextButton(String[] pageInfo, String searchTerms) {
		if(pageInfo.length != 3) return "";
		StringBuffer sb = new StringBuffer();
		int page = 1;
		try {
			page = Integer.valueOf(pageInfo[1]);
		}catch(NumberFormatException nfe) {
		}
		if(pageInfo[0] != null) {
			sb.append("<button type=\"button\" onclick=\"jsPost('"+searchTerms+"','"+ (page -1) +"');\">prevPage</button>\n");
		}
		sb.append("<span>current_Page:"+pageInfo[1]+"</span>\n");
		if(pageInfo[2] != null) {
			sb.append("<button type=\"button\" onclick=\"jsPost('"+searchTerms+"','"+(page+1)+"');\">nextPage</button>\n");
		}
		return sb.toString();
	}
	
	/**
	 * generate parameters for fill into the template page for review search and asin find
	 * @param foundresultsdata
	 * @param terms
	 * @return
	 */
	public static List<String> genrateParamsForSearchTemplate(List<String[]> foundresultsdata, String terms){
		String[] pageInfo = foundresultsdata.remove(0);
		List<String> params = new ArrayList<>();
		params.add(terms);
		String buttons = generatePrevNextButton(pageInfo, terms);
		params.add(buttons);
		params.add(generateTable(foundresultsdata));
		return params;
	}
	
}
