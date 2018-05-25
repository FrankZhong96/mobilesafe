package com.frank.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

	/**
	 * 流转换成字符串
	 * @param in  流对象
	 * @return   流转换的字符串   返回null代表异常
	 */
	public static String streamToString(InputStream in) {
		//在读取的过程中，将读取的内容储存缓存中，然后一次性转换成字符串
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//读取操作，读到为止（循环）
		byte[] buffer = new byte[1024];
		//记录读取内容的临时变量
		int temp = -1;
		try {
			while((temp = in.read(buffer)) != -1) {
				bos.write(buffer, 0, temp);
			}
			//返回读取的数据
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {		
			try {
				//先关闭读再关闭写
				in.close();
				bos.close();		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
