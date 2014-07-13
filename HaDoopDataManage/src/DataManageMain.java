import com.hadoop.algorithmImpl.AlgorithmImpl;

public class DataManageMain {
	public static void main	(String [] args) {
		
		byte dataByte[] = new byte[]{'W','i','k','i','p','e','d','i','a'};
		AlgorithmImpl algrithmImpl = new AlgorithmImpl(); 
		System.out.println(algrithmImpl.Adler32(dataByte));
		
		System.out.println(algrithmImpl.nextAdler32(algrithmImpl.Adler32(dataByte),dataByte[0],dataByte[3],9));
		
		System.out.println(algrithmImpl.MD5("".getBytes()));
		
		
	}


}
