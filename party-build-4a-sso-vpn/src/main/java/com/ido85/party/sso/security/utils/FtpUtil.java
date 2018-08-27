package com.ido85.party.sso.security.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
//import org.apache.tomcat.util.http.fileupload.FileUploadException;


/**
 * ftp工具类
 * 
 */
public class FtpUtil {
	
	/**
	 * 存储一个流对象,然后直接关闭ftpClient
	 * 
	 * @author wangzq 2012-9-5
	 * @param remote
	 * @param in
	 * @return
	 */
	public int store(String remote, InputStream in, FTPClient ftp) {
		try {
			if (ftp != null) {
				if (!StringUtils.startsWith(remote, "/"))
					remote = "/" + remote;
				String filename =  remote;
				String name = FilenameUtils.getName(filename);
				String path = FilenameUtils.getFullPath(filename);

				if (!ftp.changeWorkingDirectory(path)) {
					String[] ps = StringUtils.split(path, "/");
					String p = "/";
					ftp.changeWorkingDirectory(p);
					for (String s : ps) {
						p += s + "/";
						if (!ftp.changeWorkingDirectory(p)) {
							ftp.makeDirectory(s);
							ftp.changeWorkingDirectory(p);
						}
					}
				}
				ftp.storeFile(name, in);
				ftp.logout();
				ftp.disconnect();
			}
			in.close();
			return 0;
		} catch (SocketException e) {
			return 3;
		} catch (IOException e) {
			return 4;
		}
	}

	/**
	 * 一次保存多个文件
	 * 
	 * @author wangzq 2012-9-6
	 * @param remotes
	 * @param ins
	 * @return
	 */
	public int store(String[] remotes, InputStream[] ins, FTPClient ftp)
			throws SocketException, IOException {
		try {
			if (ftp != null) {
				for (int i = 0; i < remotes.length; i++) {
					if (!StringUtils.startsWith(remotes[i],
							"/"))
						remotes[i] = "/" + remotes[i];
					String filename =   remotes[i];
					String name = FilenameUtils.getName(filename);
					String path = FilenameUtils.getFullPath(filename);

					if (!ftp.changeWorkingDirectory(path)) {
						String[] ps = StringUtils.split(path, "/");
						String p = "/";
						ftp.changeWorkingDirectory(p);
						for (String s : ps) {
							p += s + "/";
							if (!ftp.changeWorkingDirectory(p)) {
								ftp.makeDirectory(s);
								ftp.changeWorkingDirectory(p);
							}
						}
					}
					ftp.storeFile(name, ins[i]);
					ins[i].close();
				}

				ftp.logout();
				ftp.disconnect();
			}

			return 0;
		} catch (SocketException e) {
			throw new SocketException("ftp连接异常");
			// return 3;
		} catch (IOException e) {
			throw new SocketException("io连接异常");
			// return 4;
		}
	}

	/**
	 * 判断一组文件是否在ftp服务器上存在
	 * 
	 * @author wangzq Oct 24, 2012
	 * @param remotes
	 *            文件的逻辑路径集合
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public List<String> getNotExitFile(String[] remotes, FTPClient ftp)
			throws SocketException, IOException {
		List<String> notExitRemotes = new ArrayList<String>();
		try {
			if (ftp != null) {
				for (int i = 0; i < remotes.length; i++) {
					if (ftp.listFiles(remotes[i]).length != 1) {
						notExitRemotes.add(remotes[i]);
					}
				}
				ftp.logout();
				ftp.disconnect();
			}
			return notExitRemotes;
		} catch (SocketException e) {
			throw new SocketException("ftp连接异常");
		} catch (IOException e) {
			throw new SocketException("io连接异常");
		}
	}

	/**
	 * 判断某个文件是否存在
	 * 
	 * @author wangzq Oct 24, 2012
	 * @param ftp
	 * @param remote
	 * @return
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public boolean isExitFile(FTPClient ftp, String remote) throws IOException {
		try {
			if (ftp != null) {
				if (ftp.listFiles(remote).length != 1) {
					return false;
				}
			}
			return true;
		} catch (IOException e) {
			throw new SocketException("io连接异常");
		}
	}

	public int delFile(String[] remotes, FTPClient ftp) throws SocketException, IOException {
		try {
			if (ftp != null) {
				for (int i = 0; i < remotes.length; i++) {
					if (!StringUtils.startsWith(remotes[i],
							"/"))
						remotes[i] = "/" + remotes[i];
					System.out.println("remotes[i]==" + remotes[i]);
					String filename = remotes[i];
					String name = FilenameUtils.getName(filename);
					String path = FilenameUtils.getFullPath(filename);

					if (!ftp.changeWorkingDirectory(path)) {
						String[] ps = StringUtils.split(path, "/");
						String p = "/";
						ftp.changeWorkingDirectory(p);
						for (String s : ps) {
							p += s + "/";
							if (!ftp.changeWorkingDirectory(p)) {
								ftp.makeDirectory(s);
								ftp.changeWorkingDirectory(p);
							}
						}
					}
					ftp.deleteFile(name);
				}
				ftp.logout();
				ftp.disconnect();
			}
			return 0;
		} catch (SocketException e) {
			throw new SocketException("ftp连接异常");
			// return 3;
		} catch (IOException e) {
			throw new SocketException("io连接异常");
			// return 4;
		}
	}

	/**
	 * 上传一个
	 * 
	 * @author wangxd 2013-10-20
	 * @param remote
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int upload(FTPClient ftp, String remote, InputStream in)
			throws SocketException, IOException {
		if (ftp == null)
			return 5;
		if (ftp != null) {
			if (!StringUtils.startsWith(remote, "/"))
				remote = "/" + remote;
			String filename =  remote;
			String name = FilenameUtils.getName(filename);
			String path = FilenameUtils.getFullPath(filename);
			if (!ftp.changeWorkingDirectory(path)) {
				String[] ps = StringUtils.split(path, "/");
				String p = "/";
				ftp.changeWorkingDirectory(p);
				for (String s : ps) {
					p += s + "/";
					if (!ftp.changeWorkingDirectory(p)) {
						ftp.makeDirectory(s);
						ftp.changeWorkingDirectory(p);
					}
				}
			}
			ftp.storeFile(name, in);
		}
		in.close();
		return 0;

	}

	/**
	 * 获取Ftp客户端连接实体对象
	 * 
	 * @author wangzq 2012-9-5
	 * @return
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPClient getClient(String ip,int port, String account, String password) throws SocketException, IOException {
		FTPClient ftp = new FTPClient();
		ftp.setControlEncoding("UTF-8");
		ftp.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out)));
		ftp.setDefaultPort(port);
		ftp.connect(ip);
//		ftp.connect(Constants.ip);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return null;
		}
//		if (!ftp.login(Constants.ftpusername, Constants.ftppassword)) {
		if (!ftp.login(account, password)) {
			ftp.logout();
			ftp.disconnect();
			return null;
		}
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
//		ftp.enterLocalActiveMode();
		ftp.enterLocalPassiveMode();
		return ftp;
	}

	public void closeClient(FTPClient fTPClient) {
		try {
			if (fTPClient != null) {
				fTPClient.logout();
				fTPClient.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	
	
	/**
	 * ftp下载的例子
	 * 
	 * @author wangxd 2014-04-11
	 */
	private static void download() {
		FTPClient ftpclient = new FTPClient();
		FileOutputStream fos = null;
		try {
			ftpclient.connect("10.72.67.241");
			ftpclient.login("ftpusers", "ftpusers");
			File file = new File("G:" + File.separator + "22.txt");
			ftpclient.changeWorkingDirectory("/");
			ftpclient.setBufferSize(1024);
			ftpclient.setControlEncoding("UTF-8");
			ftpclient.setFileType(ftpclient.BINARY_FILE_TYPE);
			fos = new FileOutputStream(file);
			ftpclient.retrieveFile("/aa/22.txt", fos);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int download(FTPClient ftp, String remotefile, String localfile)
			throws SocketException, IOException {
		if (ftp == null) {
			return 5;
		} else {
			File file = new File(localfile);
			FileOutputStream fos = new FileOutputStream(file);
			if (!ftp.changeWorkingDirectory("")) {
				ftp.setBufferSize(1024);
				ftp.setControlEncoding("UTF-8");
				ftp.setFileType(ftp.BINARY_FILE_TYPE);
				ftp.retrieveFile(remotefile, fos);
			}
		}
		return 0;
	}

	  /** 
     * 在服务器上创建一个文件夹 
     * 
     * @param dir 
     *            文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>... 
     */  
    public static boolean makeDirectory(FTPClient ftp,String dir) {  
        boolean flag = true;  
        try {  
            // System.out.println("dir=======" dir);  
            flag = ftp.makeDirectory(dir);  
            if (flag) {  
                System.out.println("make Directory " +dir +" succeed");  
  
            } else {  
  
                System.out.println("make Directory " +dir+ " false");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return flag;  
    } 
	
	/*public static void main(String[] args) {
		InputStream in;
		try {
			in = new FileInputStream(new File("D:/", "搜索引擎对比情况20160930152101.xlsx"));
			FtpUtil ftputil = new FtpUtil();
			FTPClient ftp;
			ftp = ftputil.getClient();
			FtpUtil.upload(ftp, "/alidata1/seo/123.xlsx", in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * @author wangzq 2012-9-5
	 * @param args
	 */
	 /*public static void main_(String[] args) {
	 // System.out.println("开始执行ftp导入。。。");
	 FtpUtil ftpUtil=new FtpUtil();
	 String[] remotes=new
	 String[]{"1"+File.separator+"test"+File.separator+"a1.txt","1"+File.separator+"test"+File.separator+"a2.txt"};
	 try {
	 ftpUtil.delFile(remotes);
	 } catch (SocketException e) {
	 e.printStackTrace();
	 } catch (IOException e) {
	 }
	
	 }*/
	/**
	 * 上传一个
	 * 
	 * @author wangzq 2012-9-5
	 * @param remote
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public int uploadCourseWare(FTPClient ftp, String remote, InputStream in)
			throws SocketException, IOException {
		if (ftp == null)
			return 5;
		if (ftp != null) {
			if (!StringUtils.startsWith(remote, "/"))
				remote = "/" + remote;
			System.out.println("pathSeparator==" + "/");
			String filename = remote;
			String name = FilenameUtils.getName(filename);
			String path = FilenameUtils.getFullPath(filename);
			System.out.println("filename==" + filename);
			System.out.println("name==" + name);
			System.out.println("path==" + path);
			if (!ftp.changeWorkingDirectory(path)) {
				String[] ps = StringUtils.split(path, "/");
				String p = "/";
				ftp.changeWorkingDirectory(p);
				for (String s : ps) {
					p += s + "/";
					if (!ftp.changeWorkingDirectory(p)) {
						ftp.makeDirectory(s);
						ftp.changeWorkingDirectory(p);
					}
				}
			}
			ftp.storeFile(name, in);
		}
		in.close();
		return 0;

	}

	

}
