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
 * Rsync�㷨
 * @author MCG
 *
 */

public class Rsync {
	/**
	 * ����patch���ļ����ڷ�����������һ���µ��ļ�
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
	 * �����ļ�����������
	 * @param checkSums ���������ļ����
	 * @param fileName  �ļ���
	 * @param start ��ʼλ��
	 * @param end   ����λ��
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
		//���checkSums����Ϊ�� ֱ������patch����ȥ
		if(checkSums.isEmpty()){
			PatchPartData  patchPartData = new PatchPartData();
			patchPartData.setDatas(readFile(fileName));
			patch.addPatchPart(patchPartData);
			return patch;
		}

		ArrayList<Byte> diffDatas = new ArrayList<Byte>();// put unconsistent data
		while(start < length){
			byte[] buffer = readChunk(fileName, start);		
			PatchPart patchPart = matchCheckSums(checkSums, buffer);// �Ƿ�ƥ����
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
