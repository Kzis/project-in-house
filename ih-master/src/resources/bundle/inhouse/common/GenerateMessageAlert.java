package resources.bundle.inhouse.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.database.CCTConnectionUtil;
import util.file.FileManagerUtil;

public class GenerateMessageAlert {

	public static void main(String[] args) {

		try {
			GenerateMessageAlert gen = new GenerateMessageAlert();
			gen.initParameterXML();
			gen.genMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String convert(String str) {

		StringBuffer ostr = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);

			// Does the char need to be converted to unicode?
			if ((ch >= 0x0020) && (ch <= 0x007e)) {
				ostr.append(ch); // No.
			} else {// Yes.
				ostr.append("\\u"); // standard unicode format.

				// Get hex value of the char.
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);

				for (int j = 0; j < 4 - hex.length(); j++)
					// Prepend zeros because unicode requires 4 digits
					ostr.append("0");

				ostr.append(hex.toLowerCase()); // standard unicode format.
			}
		}

		return (new String(ostr));
	}

	public void initParameterXML() throws Exception {
		/*ParameterManager parameterManager = new ParameterManager();
		parameterManager.loadBg(ParameterManager.XML_PATH);*/
	}

	public void genMessage() throws Exception {
		String pathDefault = "src/resources/bundle/inhouse/common/MessageAlert.properties";
		String pathEN = "src/resources/bundle/inhouse/common/MessageAlert_en.properties";
		String pathTH = "src/resources/bundle/inhouse/common/MessageAlert_th.properties";

		Connection conn = null;
		try {
			conn = getConnection();

			String sql = "SELECT MESSAGE_CODE, MESSAGE_DESC_EN, MESSAGE_DESC_TH FROM inhouse_project.con_message ORDER BY MESSAGE_CODE";

//			for (String key : ParameterConfig.getParameter().getDatabase()[0].getSchemasMap().keySet()) {
//				sql = sql.replace(key, ParameterConfig.getParameter().getDatabase()[0].getSchemasMap().get(key));
//			}

			System.out.println(sql);

			List<String> messageEn = new ArrayList<String>();
			List<String> messageTh = new ArrayList<String>();

			Statement stmt = null;
			ResultSet rst = null;
			try {
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					messageEn.add(convert(rst.getString("MESSAGE_CODE") + "=" + (rst.getString("MESSAGE_DESC_EN") != null ? rst.getString("MESSAGE_DESC_EN") : "")));
					messageTh.add(convert(rst.getString("MESSAGE_CODE") + "=" + (rst.getString("MESSAGE_DESC_TH") != null ? rst.getString("MESSAGE_DESC_TH") : "")));

				}

				// Gen file
				FileManagerUtil.writeLines(new java.io.File(pathEN), messageEn, false);
				FileManagerUtil.writeLines(new java.io.File(pathTH), messageTh, false);
				FileManagerUtil.copy(pathTH, pathDefault);
			} catch (Exception ex) {
				// ex.printStackTrace();
			} finally {
				CCTConnectionUtil.closeAll(rst, stmt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
	}

	public Connection getConnection() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		
		
		//return DriverManager.getConnection("jdbc:mysql://10.100.70.68:3306/inhouse_project?useUnicode=true&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNullL", "appdev2", "AppDev2017");
		return DriverManager.getConnection("jdbc:mysql://10.100.70.68:3306/inhouse_project?useUnicode=true&useSSL=false&serverTimezone=UTC", "appdev2", "AppDev2017");
	}

	public void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {

		}
	}
}
