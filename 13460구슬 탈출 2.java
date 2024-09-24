import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.StringTokenizer;

/**
 * 
 */

public class Main {
	static char[] board;
	static Check[] redCheckBoard;
	
	// row+col의 값으로 생각
	static int[] MOVE = {-10, 10, -1, 1};
	
	
	static class Check {
		boolean[] check;
		
		Check() {
			this.check = new boolean[100];
		}
	}
	
	
	public static int move(int bead, int way) {
		
		while (board[bead] == '.') {
			bead += MOVE[way];
		}
		
		if (board[bead] == 'O') {
			return 100;
		}
		
		return bead-MOVE[way];
	}
	
	
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine().trim());
		
		int boardRow = Integer.parseInt(st.nextToken());
		int boardCol = Integer.parseInt(st.nextToken());
		
		board = new char[100];
		redCheckBoard = new Check[100];
		
		int Red = 0, Blue = 0;
		
		for (int row=0; row < boardRow; row++) {
			String nextLine = br.readLine().trim();
			
			int rowNum = row*10;
			
			for (int col=0; col < boardCol; col++) {
				board[rowNum+col] = nextLine.charAt(col);
				redCheckBoard[rowNum+col] = new Check();
				
				if (board[rowNum+col] == 'R') {
					board[rowNum+col] = '.';
					Red = rowNum+col;
				}
				
				else if (board[rowNum+col] == 'B') {
					board[rowNum+col] = '.';
					Blue = rowNum+col;
				}
			}
		}
		
		
		ArrayDeque<int[]> bfs = new ArrayDeque<int[]>();
		bfs.add(new int[]{Red, Blue, 0});
		
		int answer = -1;
		while (!bfs.isEmpty()) {
			int[] status = bfs.poll();
			
			int R = status[0];
			int B = status[1];
			int count = status[2];
			
			count += 1;
			
			if (count > 10) {
				break;
			}
			
			for (int way=0; way < 4; way++) {
				
				int nextRed = move(R, way);
				int nextBlue = move(B, way);
				
				if (nextBlue == 100) {
					continue;
				}
				
				// Blue는 들어가지 않고 Red만 들어갔다면 종료
				if (nextRed == 100) {
					answer = count;
					break;
				}
				
				if (nextBlue == nextRed) {
					if (R*MOVE[way] > B*MOVE[way]) {
						nextBlue -= MOVE[way];
					}
					else {
						nextRed -= MOVE[way];
					}
				}
				
				if (redCheckBoard[nextRed].check[nextBlue]) {
					continue;
				}
				
				else {
					redCheckBoard[nextRed].check[nextBlue] = true;
					
					bfs.add(new int[]{nextRed, nextBlue, count});
				}
			}
			
			if (answer != -1) break;
		}
		
		System.out.println(answer);
	}
}
