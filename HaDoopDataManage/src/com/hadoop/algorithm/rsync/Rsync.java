package com.hadoop.algorithm.rsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hadoop.algorithmImpl.AlgorithmImpl;

/**
 * Rsync算法
 * @author MCG
 *
 */

public class Rsync {
	/**
	 * 对文件分块，计算每块的弱校验和和强校验和
	 * @param fileName 文件名
	 * @return 以弱校验和为KEY， 强校验和组成的链表 为Value的数据结构
	 */
	public static Map<Integer, List<Chunk>> calcCheckNum(String fileName){
		Map<Integer, List<Chunk>> checkSum = new HashMap<Integer, List<Chunk>>();
		File file = new File(fileName);
		
		// 文件不存在直接返回null
		if(!file.exists())
			return null;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Constant.CHUNK_SIZE];
			int read = 0;	
			int index = 0;
			while((read = fis.read(buffer)) != -1){
				Chunk chunk = new Chunk(); // 设置Chunk的编号
				chunk.setIndex(index++);
				chunk.setSize(read);       // 设置chunk的size
				int weakCheckSum = calcWeakCheckSum(buffer,read);
				chunk.setWeakCheckSum(weakCheckSum);
				String strongCheckSum = calcStrongCheckSum(buffer,read);
				chunk.setStrongCheckSum(strongCheckSum);
				
				// 如果checkSum包含此弱校验和，取出来然后把新的chunk加进去，如果没有的话新建chunk链表，然后put进去
				if(checkSum.containsKey(weakCheckSum)){
					List<Chunk> chunks = checkSum.get(weakCheckSum);
					chunks.add(chunk);
				}else{
					List<Chunk> chunks = new ArrayList<Chunk>();
					chunks.add(chunk);
					checkSum.put(weakCheckSum, chunks);
				}
			}
			      	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
			try {
				if(fis != null)
					fis.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
			
		return checkSum;
	}
	
	// get weakCheckSum
	private static int calcWeakCheckSum(byte[] datas, int length){
		byte[] temp = new byte[length];
		for(int i=0; i<length; i++){
			temp[i] = datas[i];
		}
		AlgorithmImpl algorithmImpl = new AlgorithmImpl();
		return algorithmImpl.Adler32(temp);
	}

	// get strongCheckSum
	private static String calcStrongCheckSum(byte[] datas, int length){
		byte[] temp = new byte[length];
		for(int i=0; i<length; i++){
			temp[i] = datas[i];
		}
		AlgorithmImpl algorithmImpl = new AlgorithmImpl();
		return algorithmImpl.MD5(temp);	
	}
}
