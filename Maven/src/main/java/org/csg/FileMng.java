package org.csg;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileMng {
	  public static void copyDir(File source, File target)
	  {
	    if (source.isDirectory()){
	      if (!target.exists()) {
	        target.mkdirs();
	      }
	      if(!source.getName().equals("playerdata") && !source.getName().equals("stats")){
	    	  for (String el : source.list()){
	  	        if (!el.equals("uid.dat") && !el.equals("session.lock")) {
	  	          copyDir(new File(source, el), new File(target, el));
	  	        }
	  	      }
	      }
	      
	    }else{
	      try{
	        if (!target.getParentFile().exists())
	        {
	          new File(target.getParentFile().getAbsolutePath()).mkdirs();
	          target.createNewFile();
	        }
	        else if (!target.exists())
	        {
	          target.createNewFile();
	        }
	        InputStream in = new FileInputStream(source);
	        Object out = new FileOutputStream(target);
	        
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	          ((OutputStream)out).write(buf, 0, len);
	        }
	        in.close();
	        ((OutputStream)out).close();
	      }
	      catch (Exception exception)
	      {
	      }
	    }
	  }
	  
	  public static boolean deleteDir(File dir)
	  {
	    if (dir.isDirectory()) {
	      for (File f : dir.listFiles()) {
	        if (!deleteDir(f)) {
	          return false;
	        }
	      }
	    }
	    return dir.delete();
	  }
	  
	  static String basePath;
	  public static void unZip(File srcFile, String destDirPath){
		  if (!srcFile.exists()) {
			  throw new RuntimeException(srcFile.getPath() + "");
		  }
		  if(!tryUnZip(srcFile,destDirPath,StandardCharsets.UTF_8)){
			  tryUnZip(srcFile,destDirPath,StandardCharsets.ISO_8859_1);
		  }

	  }

	  private static boolean tryUnZip(File srcFile,String destDirPath,Charset code){
		  ZipFile zipFile = null;
		  boolean success = true;
		  try {
			  zipFile = new ZipFile(srcFile, code);
			  Enumeration<?> entries = zipFile.entries();
			  while (entries.hasMoreElements()) {
				  ZipEntry entry = (ZipEntry) entries.nextElement();
				  if (entry.isDirectory()) {
					  String dirPath = destDirPath + "/" + entry.getName();
					  File dir = new File(dirPath);
					  dir.mkdirs();
				  } else {
					  File targetFile = new File(destDirPath + "/" + entry.getName());

					  if(!targetFile.getParentFile().exists()){
						  targetFile.getParentFile().mkdirs();
					  }
					  targetFile.createNewFile();

					  InputStream is = zipFile.getInputStream(entry);
					  FileOutputStream fos = new FileOutputStream(targetFile);
					  int len;
					  byte[] buf = new byte[1024];
					  while ((len = is.read(buf)) != -1) {
						  fos.write(buf, 0, len);
					  }

					  fos.close();
					  is.close();
				  }
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
			  success = false;
		  }
		  if(zipFile != null){
			  try {
				  zipFile.close();
			  } catch (IOException e) {
				  e.printStackTrace();
			  }
		  }
		  if(success){
			  Data.ConsoleInfo("获取资源"+srcFile.getName()+"成功！");
		  }
		  return success;

	  }


	/* 替换文件中的字符串，并覆盖原文件
	 * @param filePath
	 * @param oldstr
	 * @param newStr
	 * @throws IOException
	 */
	public static void autoReplaceStr(String filePath, String oldstr, String newStr) throws IOException {
		File file = new File(filePath);
		Long fileLength = file.length();
		byte[] fileContext = new byte[fileLength.intValue()];
		FileInputStream in = null;
		PrintWriter out = null;
		in = new FileInputStream(filePath);
		in.read(fileContext);
// 避免出现中文乱码
		String str = new String(fileContext, "utf-8");//字节转换成字符
		str = str.replace(oldstr, newStr);
		out = new PrintWriter(filePath, "utf-8");//写入文件时的charset
		out.write(str);
		out.flush();
		out.close();
		in.close();
	}
}
