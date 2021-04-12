import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoardUtils {
		//takes a string representation of a binary representation of a bitboard
		//and returns the long bitboard representation
		public static long stringToBitBoard(String binary) {
			if(binary.charAt(0)=='0'||binary.length()<64) {
				return Long.parseLong(binary, 2);
			}
			else {
					return Long.parseLong("1"+binary.substring(2), 2)*2;
			}
		}
			
		//displays rays for testing purposes
		public static void displayRays(String binary, List<List<Integer>> rays) {
			long bin = stringToBitBoard(binary);
			long bintemp = bin;
			for( List<Integer> list : rays) {
				for(int i : list) {
					if(i>0) {
					bin = bin|bintemp<<i;
					}
					else {
						bin = bin|bintemp>>>(i*-1);
					}
				}
			}
			//bin = bin^Long.parseLong(binary,2); /*comment this line to show original place*/
			
			printBoard(bin);
		}
			
		public static void printBoard(long binary) {
			String back2bin = Long.toBinaryString(binary);
			int binfill0 = 64 - back2bin.length();
			for(int i = 0; i<binfill0; i++) {
				back2bin = "0"+back2bin;
			}
			for(int i = 0; i<64; i++) {
				System.out.print(back2bin.substring(0, 1)+", ");
				if((i+1)%8==0) {System.out.println();}
				back2bin = back2bin.substring(1);
			}
		}
		
		public static void printBoard(String back2bin) {
			int binfill0 = 64 - back2bin.length();
			for(int i = 0; i<binfill0; i++) {
				back2bin = "0"+back2bin;
			}
			for(int i = 0; i<64; i++) {
				System.out.print(back2bin.substring(0, 1)+", ");
				if((i+1)%8==0) {System.out.println();}
				back2bin = back2bin.substring(1);
			}
		}
		
		public static void printRaySet(char piece, Map<Character, Map<Long, List<List<Integer>>>> rays) {
			int count = 0;
			Set<Long> positionSet = rays.get(piece).keySet();
			for(long bin: positionSet) {
				displayRays(Long.toBinaryString(bin), rays.get(piece).get(bin));
				//System.out.println(Long.toBinaryString(bin));
				System.out.println();
				count++;
			}
			System.out.println("Number of boards:" + count);
		}
}
