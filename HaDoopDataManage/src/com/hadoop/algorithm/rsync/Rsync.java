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
 * Rsync�㷨
 * @author MCG
 *
 */

public class Rsync {
	/**
	 * ���ļ��ֿ飬����ÿ�����У��ͺ�ǿУ���
	 * @param fileName �ļ���
	 * @return ����У���ΪKEY�� ǿУ�����ɵ����� ΪValue�����ݽṹ
	 */
	public static Map<Integer, List<Chunk>> calcCheckNum(String fileName){
		Map<Integer, List<Chunk>> checkSum = new HashMap<Integer, List<Chunk>>();
		File file = new File(fileName);
		
		// �ļ�������ֱ�ӷ���null
		if(!file.exists())
			return null;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Constant.CHUNK_SIZE];
			int read = 0;	
			int index = 0;
			while((read = fis.read(buffer)) != -1){
				Chunk chunk = new Chunk(); // ����Chunk�ı��
				chunk.setIndex(index++);
				chunk.setSize(read);       // ����chunk��size
				int weakCheckSum = calcWeakCheckSum(buffer,read);
				chunk.setWeakCheckSum(weakCheckSum);
				String strongCheckSum = calcStrongCheckSum(buffer,read);
				chunk.setStrongCheckSum(strongCheckSum);
				
				// ���checkSum��������У��ͣ�ȡ����Ȼ����µ�chunk�ӽ�ȥ�����û�еĻ��½�chunk����Ȼ��put��ȥ
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
