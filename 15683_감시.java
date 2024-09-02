import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * CCTV를 효율적으로 위치할 수 있는 방법 구하기
 * 
 * ## 제약 사항
 * 
 * 벽(6)을 만나면 감시가 더이상 불가능하다.
 * 
 * CCTV(1-5)를 만나면 계속 가능하다.
 * 
 * 안전구역의 최소치를 구하라 (CCTV 배치의 최대값)
 * 
 * 최대 8*8 의 보드이기에 long형의 비트마스킹을 활용할 수 있다.
 * 
 * 
 * ## 구현 방식
 * 
 * #1 모든 CCTV의 좌표를 구하고, 종류별로 나누어 관리한다
 * 
 * #2 각 CCTV의 종류 마다 범위를 확인하는 모든 경우를 확인한다
 * 
 * #3 탐색이 끝났을 때 답과 비교하여 갱신한다
 */

public class Main {
    
    static char[][] board;
    
    static int boardRow;
    static int boardCol;
    
    // 상, 우, 하, 좌 의 순서
    static final int[] DELTA_X = new int[] {0, 1, 0, -1};
    static final int[] DELTA_Y = new int[] {-1, 0, 1, 0};
    
    static ArrayList<CCTV> CCTVArray;
    static int CCTVArrayNum;
    
    static int maxRange;
    
    // #1 모든 CCTV의 좌표를 구하고, 종류별로 나누어 관리한다
    static class CCTV{
    	int row;
    	int col;
    	
    	// CCTV의 종류
    	char CCTVshape;
    	
		public CCTV(int row, int col, char CCTVshape) {
			this.row = row;
			this.col = col;
			this.CCTVshape = CCTVshape;
		}
		
    }
    
    
    // 매개변수로 CCTV 순서와 long형 비트마스킹을 받는다.
    static void search(int cctvIndex, long check) {
    	
    	// 모든 CCTV를 탐색했을 때 종료
    	if (cctvIndex == CCTVArrayNum) {
    		maxRange = Math.max(maxRange, Long.bitCount(check));
    		return;
    	}
    	
    	CCTV cctv = CCTVArray.get(cctvIndex);
    	
    	if (cctv.CCTVshape == '1') {
    		for (int way=0; way < 4; way++) {
    			search(cctvIndex+1, move(cctv.row, cctv.col, way, check));
    		}
    	}
    	
    	// 0, 2 (상, 하) | 1, 3 (우, 좌)
    	else if (cctv.CCTVshape == '2') {
    		for (int way=0; way < 2; way++) {
    			search(cctvIndex+1, move(cctv.row, cctv.col, way, check) | move(cctv.row, cctv.col, way+2, check));
    		}
    	}
    	
    	// 시계 방향으로 구성된 DELTA 배열의 바로 옆 방향
    	else if (cctv.CCTVshape == '3') {
    		for (int way=0; way < 4; way++) {
    			search(cctvIndex+1, move(cctv.row, cctv.col, way, check) | move(cctv.row, cctv.col, (way+1)%4, check));
    		}
    	}
    	
    	// 0, 2 (상, 하) + 우 or 좌 | 1, 3 (우, 좌) + 상 or 하
    	else if(cctv.CCTVshape == '4') {
    		for (int way=0; way < 2; way++) {
    			search(cctvIndex+1, move(cctv.row, cctv.col, way, check) | move(cctv.row, cctv.col, way+2, check) | move(cctv.row, cctv.col, way+1, check));
    			search(cctvIndex+1, move(cctv.row, cctv.col, way, check) | move(cctv.row, cctv.col, way+2, check) | move(cctv.row, cctv.col, (way+3)%4, check));
    		}
    	}
    	
    	// 4방향 모두 확인
    	else {
    		search(cctvIndex+1, move(cctv.row, cctv.col, 0, check) | move(cctv.row, cctv.col, 1, check) | move(cctv.row, cctv.col, 2, check) | move(cctv.row, cctv.col, 3, check));
    	}
    	
    }
    
    
    // CCTV의 정보와 확인할 방향, 체크한 누적 시야를 받는다
    static long move(int row, int col, int way, long check) {
    	
    	while (row >= 0 && col >= 0 && row < boardRow && col < boardCol) {
    		
    		// 벽이면 탐색 중지
    		if (board[row][col] == '6') {
    			return check;
    		}
    		
    		// 한 행의 최대치인 8 * row + col으로 저장
    		check |= 1L << (8*row)+col;
    		
    		row += DELTA_Y[way];
    		col += DELTA_X[way];
    	}
    	
    	return check;
    }
    
    
    public static void main(String[] args) throws Exception{
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        
        boardRow = Integer.parseInt(st.nextToken());
        boardCol = Integer.parseInt(st.nextToken());
        
        board = new char[boardRow][boardCol];
        
        CCTVArray = new ArrayList<CCTV>();
        
        // 마지막 정답 도출을 위한 값. (boardRow*boardCol) - bitCount - sixCount
        int sixCount = 0;
        
        for (int row=0; row < boardRow; row++) {
            st = new StringTokenizer(br.readLine().trim());
            
            for (int col=0; col < boardCol; col++) {
                board[row][col] = st.nextToken().charAt(0);
                
                // 벽은 마지막 사각지대에 포함되지 않는다
                if (board[row][col] == '6') {
                    sixCount += 1;
                }
                
                // #1 모든 CCTV의 좌표를 구하고, 종류별로 나누어 관리한다
                else if (board[row][col] != '0') {
                    CCTVArray.add(new CCTV(row, col, board[row][col]));
                }
            }
        }
        
        CCTVArrayNum = CCTVArray.size();
        maxRange = 0;
        
        search(0, 0L);
        
        System.out.println((boardRow*boardCol) - maxRange - sixCount);
    }
    
}
