package com.ido85.party.sso.security.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	/*** 
	 * 获取指定目录下的所有的文件（不包括文件夹），采用了递归 
	 *  
	 * @param obj 
	 * @return 
	 */  
	public static ArrayList<File> getListFiles(Object obj) {  
		File directory = null;  
		if (obj instanceof File) {  
			directory = (File) obj;  
		} else {  
			directory = new File(StringUtils.toString(obj));
		}  
		ArrayList<File> files = new ArrayList<File>();  
		if (directory.isFile()) {  
			files.add(directory);  
			return files;  
		} else if (directory.isDirectory()) {
			if(!directory.exists()){//文件不存在
				return null;
			}
			File[] fileArr = directory.listFiles();  
			for (int i = 0; i < fileArr.length; i++) {  
				File fileOne = fileArr[i];  
				files.addAll(getListFiles(fileOne));  
			}  
		}  
		return files;  
	}  

	/*** 
	 *  
	 * @param path 
	 * @param prefixStr 
	 *            :前缀名 
	 * @return 
	 */  
	public static File[] getFilesByPathPrefix(File path, final String prefixStr) {  
		File[] fileArr = path.listFiles(new FilenameFilter() {  
			@Override  
			public boolean accept(File dir, String name) {  
				// System.out.println("prefixStr:"+prefixStr);  
				if ((StringUtils.isBlank(prefixStr) || (dir.isDirectory() && name  
						.startsWith(prefixStr)))) {  
					return true;  
				} else {  
					return false;  
				}  
			}  
		});  
		return fileArr;  

	}  
	/*** 
	 * 过滤前缀（只是获取指定目录下的文件，没有递归）
	 *  
	 * @param pathStr 
	 * @param prefixStr 
	 * @return 
	 */  
	public static File[] getFilesByPathAndPrefix(String pathStr,  
			final String prefixStr) {  
		File path = new File(pathStr);
		if(!path.exists()){//文件不存在
			return null;
		}
		return getFilesByPathPrefix(path, prefixStr);  
	}  

	/*** 
	 *  
	 * @param path 
	 * @param prefixStr 
	 *            :后缀名 
	 * @return 
	 */  
	public static File[] getFilesByPathAndSuffix(File path,  
			final String sufixStr) {  
		File[] fileArr = path.listFiles(new FilenameFilter() {  
			@Override  
			public boolean accept(File dir, String name) {  
				// System.out.println("prefixStr:"+prefixStr);  
				if ((StringUtils.isBlank(sufixStr) || (dir.isDirectory() && name  
						.endsWith(sufixStr)))) {  
					return true;  
				} else {  
					return false;  
				}  
			}  
		});  
		return fileArr;  

	}  

	/*** 
	 * 过滤后缀名（只是获取指定目录下的文件，没有递归)
	 *  
	 * @param pathStr 
	 * @param sufixStr 
	 * @return 
	 */  
	public static File[] getFilesByPathAndSuffix(String pathStr,  
			final String sufixStr) {  
		File file = new File(pathStr);
		if(!file.exists()){//文件不存在
			return null;
		}
		return getFilesByPathAndSuffix(file, sufixStr);  
	}  

	/**
	 * 读取一个文件夹下所有文件及子文件夹下的所有文件
	 * @param filePath
	 * @return
	 */
	public static List<File> readAllFile(String filePath) {
		List<File> list = new ArrayList<File>();  
		File f = new File(filePath);
		if(!f.exists()){//文件不存在
			return null;
		}
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
		for (File file : files) {  
			if(file.isDirectory()) {  
				//如何当前路劲是文件夹，则循环读取这个文件夹下的所有文件  
				readAllFile(file.getAbsolutePath());  
			} else {  
				list.add(file);  
			}  
		}
		return list;
	}  

	/**
	 * 读取一个文件夹下的所有文件夹和文件  
	 * @param filePath
	 * @return
	 */
	public static List<File> readFile(String filePath) {  
		List<File> list = new ArrayList<File>();  
		File f = new File(filePath);
		if(!f.exists()){//文件不存在
			return list;
		}
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
		for (File file : files) {  
			list.add(file);  
		}  
		return list;
	}
	/**
	 * 读取一个文件夹下的所有文件夹
	 * @param filePath
	 * @return
	 */
	public static List<File> readAllDic(String filePath) {  
		List<File> list = new ArrayList<File>();  
		File f = new File(filePath);
		if(!f.exists()){//文件不存在
			return list;
		}
		File[] files = f.listFiles(); // 得到f文件夹下面的所有文件。  
		for (File file : files) {  
			if(file.isDirectory()) {  
				list.add(file);  
			} 
		}  
		return list;
	}

	/**
     * 删除空目录
     * @param dir 将要删除的目录路径
     */
	public static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
           //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
/*public static void main(String[] args) {
	FileUtil.deleteDir(new File("d:\\竞品对比情况20161010113538.xlsx"));
}*/

}
