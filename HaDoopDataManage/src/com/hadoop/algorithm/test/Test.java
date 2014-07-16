package com.hadoop.algorithm.test;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.hadoop.algorithm.rsync.Chunk;
import com.hadoop.algorithm.rsync.Patch;
import com.hadoop.algorithm.rsync.Rsync;

/**
 * 测试Rsync算法
 * @author MCG
 *
 */
public class Test {
	public static void main(String [] args) throws Exception {
		long start = System.currentTimeMillis();
		// 1、calculate checkSums
		Map<Integer, List<Chunk>> checkSums = Rsync.calcCheckNum("E:\\remote\\123.txt");
		// 2、get patch
		File file1 = new File("E:\\local\\456.txt");
		Patch patch = Rsync.createPatch(checkSums, "E:\\local\\456.txt", 0, file1.length());
		// 3、生成新的文件
		Rsync.createNewFile(patch, "E:\\remote\\123.txt");
		long end = System.currentTimeMillis();
		System.out.print("运行时间: " + (end-start));		
	}
}
 