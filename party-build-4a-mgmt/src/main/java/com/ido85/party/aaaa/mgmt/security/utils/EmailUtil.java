package com.ido85.party.aaaa.mgmt.security.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Fire
 * @Create Date: 2017/9/9 17:05.
 * @version: 1.0
 * @Description: 发送邮件,支持多收件人/抄送/附件
 */
public class EmailUtil {

	public final static String senderNick = "灯塔统计日报";   // 发件人昵称
	public final static String TEMPLATE_PATH = "mailtemplate";   // FTL模板位置

	private static Properties props; // 系统属性
	private static Session session; // 邮件会话对象
	private static MimeMessage mimeMsg; // MIME邮件对象
	private static Multipart mp;   // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

	private static EmailUtil instance = null;
	private static Map<String, String> hostMap = new HashMap<>();
	static {
		// 126
		hostMap.put("smtp.126", "smtp.126.com");
		// qq
		hostMap.put("smtp.qq", "smtp.qq.com");

		// 163
		hostMap.put("smtp.163", "smtp.163.com");

		// sina
		hostMap.put("smtp.sina", "smtp.sina.com.cn");

		// tom
		hostMap.put("smtp.tom", "smtp.tom.com");

		// 263
		hostMap.put("smtp.263", "smtp.263.net");

		// yahoo
		hostMap.put("smtp.yahoo", "smtp.mail.yahoo.com");

		// hotmail
		hostMap.put("smtp.hotmail", "smtp.live.com");

		// gmail
		hostMap.put("smtp.gmail", "smtp.gmail.com");
		//腾讯企业邮箱
		hostMap.put("smtp.netconcepts", "smtp.exmail.qq.com");

		hostMap.put("smtp.port.gmail", "465");

		hostMap.put("smtp.85ido", "smtp.exmail.qq.com");
	}

	public EmailUtil() {
//		props = System.getProperties();
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.transport.protocol", "smtp");
//		props.put("mail.smtp.host", "smtp.exmail.qq.com");
//		props.put("mail.smtp.port", "25");
//		props.put("username", username);
//		props.put("password", password);
//		// 建立会话
//		session = Session.getDefaultInstance(props);
//		session.setDebug(false);
	}

	public static void getProperties(String from, String pwd) throws Exception {
		props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", getHost(from));
		props.put("mail.smtp.port", getSmtpPort(from));
		props.put("username", from);
		props.put("password", pwd);
		// 建立会话
		session = Session.getDefaultInstance(props);
		session.setDebug(false);
	}

	public static EmailUtil getInstance() {
		if (instance == null) {
			instance = new EmailUtil();
		}
		return instance;
	}

	public static String getHost(String email) throws Exception {
		Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
		Matcher matcher = pattern.matcher(email);
		String key = "unSupportEmail";
		if (matcher.find()) {
			key = "smtp." + matcher.group(1);
		}
		if (hostMap.containsKey(key)) {
			return hostMap.get(key);
		} else {
			throw new Exception("unSupportEmail");
		}
	}

	public static int getSmtpPort(String email) throws Exception {
		Pattern pattern = Pattern.compile("\\w+@(\\w+)(\\.\\w+){1,2}");
		Matcher matcher = pattern.matcher(email);
		String key = "unSupportEmail";
		if (matcher.find()) {
			key = "smtp.port." + matcher.group(1);
		}
		if (hostMap.containsKey(key)) {
			return Integer.parseInt(hostMap.get(key));
		} else {
			return 25;
		}
	}

	/**
	 * 发送邮件
	 * @param from 发件人
	 * @param to 收件人
	 * @param copyto 抄送
	 * @param subject 主题
	 * @param content 内容
	 * @param fileList 附件列表
	 * @return
	 */
	public static boolean sendMail(String from, String pwd, String[] to, String[] copyto, String subject, String content, String[] fileList) throws Exception {
		boolean success = true;
		try {
			getProperties(from, pwd);
			mimeMsg = new MimeMessage(session);
			mp = new MimeMultipart();

			// 自定义发件人昵称
			String nick = "";
			try {
				nick = MimeUtility.encodeText(senderNick);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 设置发件人
			mimeMsg.setFrom(new InternetAddress(from, nick));
			// 设置收件人
			if (to != null && to.length > 0) {
				String toListStr = getMailList(to);
				mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toListStr));
			}
			// 设置抄送人
			if (copyto != null && copyto.length > 0) {
				String ccListStr = getMailList(copyto);
				mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccListStr));
			}
			// 设置主题
			mimeMsg.setSubject(subject);
			// 设置正文
			BodyPart bp = new MimeBodyPart();
			bp.setContent(content, "text/html;charset=utf-8");
			mp.addBodyPart(bp);
			// 设置附件
			if (null != fileList && fileList.length > 0) {
				for (int i = 0; i < fileList.length; i++) {
					bp = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(fileList[i]);
					bp.setDataHandler(new DataHandler(fds));
					bp.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
					mp.addBodyPart(bp);
				}
			}
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			// 发送邮件
			if (props.get("mail.smtp.auth").equals("true")) {
				Transport transport = session.getTransport("smtp");
				transport.connect((String)props.get("mail.smtp.host"), (String)props.get("username"), (String)props.get("password"));
				transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
				transport.close();
			} else {
				Transport.send(mimeMsg);
			}
			System.out.println("邮件发送成功");
		} catch (MessagingException e) {
			e.printStackTrace();
			success = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	/**
	 * 发送邮件
	 * @param from 发件人
	 * @param to 收件人, 多个Email以英文逗号分隔
	 * @param cc 抄送, 多个Email以英文逗号分隔
	 * @param subject 主题
	 * @param content 内容
	 * @param fileList 附件列表
	 * @return
	 */
	public static boolean sendMail(String from, String pwd, String to, String cc, String subject, String content, String[] fileList) throws Exception {
		boolean success = true;
		Template template = null;
		try {
			getProperties(from, pwd);
			mimeMsg = new MimeMessage(session);
			mp = new MimeMultipart();

			// 自定义发件人昵称
			String nick = "";
			try {
				nick = MimeUtility.encodeText(senderNick);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 设置发件人
			mimeMsg.setFrom(new InternetAddress(from, nick));
			// 设置收件人
			if (to != null && to.length() > 0) {
				mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			}
			// 设置抄送人
			if (cc != null && cc.length() > 0) {
				mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			}
			// 设置主题
			mimeMsg.setSubject(subject);
			// 设置正文
			BodyPart bp = new MimeBodyPart();
			bp.setContent(content, "text/html;charset=utf-8");
			mp.addBodyPart(bp);
			// 设置附件
			if (fileList != null && fileList.length > 0) {
				for (int i = 0; i < fileList.length; i++) {
					bp = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(fileList[i]);
					bp.setDataHandler(new DataHandler(fds));
					bp.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
					mp.addBodyPart(bp);
				}
			}
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			// 发送邮件
			if (props.get("mail.smtp.auth").equals("true")) {
				Transport transport = session.getTransport("smtp");
				transport.connect((String)props.get("mail.smtp.host"), (String)props.get("username"), (String)props.get("password"));
				transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
				transport.close();
			} else {
				Transport.send(mimeMsg);
			}
//			System.out.println("邮件发送成功");
		} catch (MessagingException e) {
			e.printStackTrace();
			success = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	private static String getFilePath() {
		String path = getAppPath(EmailUtil.class);
		path = path + File.separator + TEMPLATE_PATH + File.separator;
		path = path.replace("\\", "/");
		return path;
	}

	private static String getFileName(String path) {
		path = path.replace("\\", "/");
		return path.substring(path.lastIndexOf("/") + 1);
	}

	public static String getAppPath(Class<?> cls) {
		// 检查用户传入的参数是否为空
		if (cls == null) {
			throw new IllegalArgumentException("参数不能为空！");
		}
		ClassLoader loader = cls.getClassLoader();
		// 获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 获得传入参数所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			// 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new IllegalArgumentException("不要传送系统类！");
			// 在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (packName.indexOf(".") < 0)
				path = packName + "/";
			else {// 否则按照包名的组成部分，将包名转换为路径
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 从URL对象中获取路径信息
		String realPath = url.getPath();
		// 去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1) {
			realPath = realPath.substring(pos + 5);
		}
		// 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!")) {
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		}
		/*------------------------------------------------------------
		 ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
		  中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
		  的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
		  中文及空格路径
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}

	/**
	 * 发送邮件
	 * @param from 发件人
	 * @param to 收件人, 多个Email以英文逗号分隔
	 * @param cc 抄送, 多个Email以英文逗号分隔
	 * @param subject 主题
	 * @param fileList 附件列表
	 * @param templatePath 模板地址
	 * @param map 模板内容
	 * @return
	 * @throws Exception
	 */
	public static boolean sendFtlMail(String from, String pwd, String to, String cc, String subject, String[] fileList, String templatePath, Map<String, Object> map) throws Exception {
		boolean success = true;
		Template template = null;
		try {
			getProperties(from, pwd);
			mimeMsg = new MimeMessage(session);
			mp = new MimeMultipart();

			// 自定义发件人昵称
			String nick = "";
			try {
				nick = MimeUtility.encodeText(senderNick);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 设置发件人
			mimeMsg.setFrom(new InternetAddress(from, nick));
			// 设置收件人
			if (to != null && to.length() > 0) {
				mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			}
			// 设置抄送人
			if (cc != null && cc.length() > 0) {
				mimeMsg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			}
			// 设置主题
			mimeMsg.setSubject(subject);
			// 设置正文
			// 获取模板
			Configuration freeMarkerConfig = null;
			freeMarkerConfig = new Configuration();
			freeMarkerConfig.setDirectoryForTemplateLoading(new File(
					getFilePath()));
			template = freeMarkerConfig.getTemplate(getFileName(templatePath),
					new Locale("Zh_cn"), "UTF-8");
			// 模板内容转换为string
			String htmlText = FreeMarkerTemplateUtils
					.processTemplateIntoString(template, map);

			BodyPart bp = new MimeBodyPart();
			bp.setContent(htmlText, "text/html;charset=utf-8");
			mp.addBodyPart(bp);
			// 设置附件
			if (null != fileList && fileList.length > 0) {
				for (int i = 0; i < fileList.length; i++) {
					bp = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(fileList[i]);
					bp.setDataHandler(new DataHandler(fds));
					bp.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
					mp.addBodyPart(bp);
				}
			}
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			// 发送邮件
			if (props.get("mail.smtp.auth").equals("true")) {
				Transport transport = session.getTransport("smtp");
				transport.connect((String)props.get("mail.smtp.host"), (String)props.get("username"), (String)props.get("password"));
				transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());
				transport.close();
			} else {
				Transport.send(mimeMsg);
			}
			System.out.println("邮件发送成功");
		} catch (MessagingException e) {
			e.printStackTrace();
			success = false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	public static String getMailList(String[] mailArray) {
		StringBuffer toList = new StringBuffer();
		int length = mailArray.length;
		if (mailArray != null && length < 2) {
			toList.append(mailArray[0]);
		} else {
			for (int i = 0; i < length; i++) {
				toList.append(mailArray[i]);
				if (i != (length - 1)) {
					toList.append(",");
				}

			}
		}
		return toList.toString();
	}

//	public static void main(String[] args) throws Exception {
//		String from = username;
//		String to = "liangzheng.li@85ido.com,1204533142@qq.com";
//		String copyto = "fire_in_java2013@163.com,yang.sun@85ido.com";
//		String subject = "测试标题";
//		String content = "这是邮件内容，仅仅是测试，不需要回复.";
//		String[] fileList = new String[2];
//		fileList[0] = "d:/temp/考试导入模板_干部.xls";
//		fileList[1] = "d:/temp/InterWebClient.class";
////		EmailUtil.getInstance().sendMail(from, to, copyto, subject, content, null);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("subject", "测试 2 标题");
//		map.put("content", "测试 内容");
//		String templatePath = "mailtemplate/test.ftl";
//		sendFtlMail(from, to, copyto, subject, null, templatePath, map);
//	}
}