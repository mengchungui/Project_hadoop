package com.hadoop.algorithm.rsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.hadoop.algorithmImpl.AlgorithmImpl;

/**
 * Rsync算法
 * @author MCG
 *
 */

public class Rsync {
	/**
	 * 根据patch和文件名在服务器上生成一个新的文件
	 * @param patch
	 * @param fileName
	 * @throws FileNotFoundException 
	 */
	public static void createNewFile(Patch patch, String fileName) throws Exception {
		File file = new File(fileName);

		RandomAccessFile src = new RandomAccessFile(fileName, "r");
		RandomAccessFile dst = new RandomAccessFile("temp_" + file.getName(), "rw");// the file after complete patch 
		for(PatchPart part:patch.getPatchParts()){
			if(part instanceof PatchPartData){
				PatchPartData patchPartData = (PatchPartData)part;
				dst.write(patchPartData.getDatas());
			}else{
				PatchPartChunk patchPartChunk = (PatchPartChunk)part;
				src.seek(patchPartChunk.getIndex()*Constant.CHUNK_SIZE);
				byte[] buffer = new byte[patchPartChunk.getSize()];
				src.read(buffer);
				dst.write(buffer);
			}
		}
		if(file.exists()){
			file.delete();
		}
		File tempFile = new File("temp_" + file.getName());
		FileUtils.copyFile(tempFile, file);
	
		if(src != null)
			src.close();
		if(dst != null)
			dst.close();
	}
	
	
	/**
	 * 根据文件，创建补丁
	 * @param checkSums 服务器传的检验和
	 * @param fileName  文件名
	 * @param start 开始位置
	 * @param end   结束位置
	 * @return
	 * @throws IOException 
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static Patch createPatch(Map<Integer, List<Chunk>> checkSums, String fileName, long start, long length) throws Exception {
		Patch patch = new Patch();
		
		if( start >= length ){
			return patch;
		}
		//如果checkSums里面为空 直接生成patch传回去
		if(checkSums.isEmpty()){
			PatchPartData  patchPartData = new PatchPartData();
			patchPartData.setDatas(readFile(fileName));
			patch.addPatchPart(patchPartData);
			return patch;
		}

		ArrayList<Byte> diffDatas = new ArrayList<Byte>();// put unconsistent data
		while(start < length){
			byte[] buffer = readChunk(fileName, start);		
			PatchPart patchPart = matchCheckSums(checkSums, buffer);// 是否匹配上
			// is matched
			if(patchPart != null){
				if(diffDatas.size() > 0){
					PatchPartData patchPartData = new PatchPartData();
					byte[] temp = new byte[diffDatas.size()];
					for(int i=0; i<diffDatas.size(); i++){
						temp[i] = diffDatas.get(i);
					}
					patchPartData.setDatas(temp);
					patch.addPatchPart(patchPartData);// add unconsistent patch
				}
				diffDatas.clear();// clear arraylist diffData
				patch.addPatchPart(patchPart);    // add consistent patch
				start = start + buffer.length;
				if(start >= length){
					return patch;
				}
				continue;
			}else{// is not matched
				start = start + 1; // move right one byte
				if(start >= length){
					PatchPartData patchPartData = new PatchPartData();
					byte[] temp = new byte[diffDatas.size() + buffer.length];
					// put unconsistnt data to temp 
					for(int i=0; i<diffDatas.size(); i++){
						temp[i] = diffDatas.get(i);					
					}
					System.arraycopy(buffer, 0, temp, diffDatas.size(), buffer.length);
					patchPartData.setDatas(temp);
					patch.addPatchPart(patchPartData);
					return patch;
				}
				diffDatas.add(buffer[0]);
				continue;
			}		
		}	
		return patch;
	}
	
	//output matched patchPart
	public static PatchPart matchCheckSums(Map<Integer, List<Chunk>> checkSums, byte[] buffer){
		
		int weakCheckSum = calcWeakCheckSum(buffer);
		if(checkSums.containsKey(weakCheckSum)){
			List<Chunk> strongCheckSums = checkSums.get(weakCheckSum);
			String strongCheckSum = calcStrongCheckSum(buffer);
			for(Chunk chunk:strongCheckSums){
				if(strongCheckSum.equals(chunk.getStrongCheckSum())){
					PatchPartChunk patchPartChunk = new PatchPartChunk();
					patchPartChunk.setIndex(chunk.getIndex());
					patchPartChunk.setSize(buffer.length);
					return patchPartChunk;
				}
			}
		}
		return null;
	}

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
	
	private static byte[] readFile(String fileName) throws Exception{
		File file = new File(fileName);
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(fileName);
		fis.read(buffer);
		fis.close();
		return buffer;
	}
	
	private static byte[] readChunk(String filename, long start) throws Exception {
		RandomAccessFile raf = new RandomAccessFile(new File(filename), "rw");
		raf.seek(start);
		byte[] temp = new byte[Constant.CHUNK_SIZE];
		int read = raf.read(temp, 0, Constant.CHUNK_SIZE);
		byte[] buffer = new byte[read];
		System.arraycopy(temp, 0, buffer, 0, read);
		if(raf != null)
			raf.close();
		
		return buffer;
		
	}
	
	// get weakCheckSum
	private static int calcWeakCheckSum(byte[] datas){
		
		AlgorithmImpl algorithmImpl = new AlgorithmImpl();
		return algorithmImpl.Adler32(datas);
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
	
	// get strongCheckSum
	private static String calcStrongCheckSum(byte[] datas){
	
		AlgorithmImpl algorithmImpl = new AlgorithmImpl();
		return algorithmImpl.MD5(datas);	
	}
}
