import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * 구현 문제
 * 
 * s는 속력, d는 이동 방향, z는 크기이다. d가 1인 경우는 위, 2인 경우는 아래, 3인 경우는 오른쪽, 4인 경우는 왼쪽을 의미
 * 
 * 범위를 양수로 넘어가면 "초과한 만큼 빼준다"
 * 음수로 넘어가면 절대값으로 변경한다
 * 
 * 낚시꾼이 이동 후 잡는 순서가 가장 처음
 * 이후 상어 이동
 */

public class Main {
	static int boardRow;
	static int boardCol;
	static int sharkNum;
	
	static int rowMove;
	static int colMove;
	
	static Shark[] sharkArray;
	static boolean[] isDead;
	static int[] checkBoard;
	
	// 1인 경우는 위, 2인 경우는 아래, 3인 경우는 오른쪽, 4인 경우는 왼쪽을 의미
	static final int[] DELTA_X = {0, 0, 0, 1, -1};
	static final int[] DELTA_Y = {0, -1, 1, 0, 0};
	
	static int answer;
	
	
	static class Shark{
		int row, col, speed, direction, size, update;
		
		
		public Shark(int row, int col, int speed, int direction, int size) {
			this.row = row;
			this.col = col;
			this.speed = speed;
			this.direction = direction;
			this.size = size;
			this.update = 0;
		}
		
		
		public int location() {
			return row*1000+col;
		}
	}
	
	
	static void catchShark(int startIndex) {
		// 땅과 얼마나 가까운지에 대한 row값
		int closeGround = Integer.MAX_VALUE;
		int sharkSize = 0;
		int sharkIndex = 0;
		
		if (startIndex > boardCol) {
			return;
		}
		
		for (int index=1; index <= sharkNum; index++) {
			if (!isDead[index] && sharkArray[index].col == startIndex && sharkArray[index].row < closeGround) {
				sharkSize = sharkArray[index].size;
				sharkIndex = index;
				closeGround = sharkArray[index].row;
			}
		}
		
		answer += sharkSize;
		isDead[sharkIndex] = true;
		
		moveShark(startIndex);
		catchShark(startIndex+1);
	}
	
	
	static void moveShark(int update) {
		
		for (int index=1; index <= sharkNum; index++) {
			// 상어가 죽지 않았으면 이동
			if (!isDead[index]) {
				
				// 새로운 상어로 update
				sharkArray[index].update = update;
				
				// 만약 현 위치에 어떤 상어도 덮어 씌워지지 않았다면 초기화 후 이동
				int before = sharkArray[index].location();
				checkBoard[before] = checkBoard[before] == index? 0: checkBoard[before];
				
				if (sharkArray[index].direction == 1 || sharkArray[index].direction == 2) {
					int move = sharkArray[index].speed % rowMove;
					
					while (move != 0) {
						sharkArray[index].row += DELTA_Y[sharkArray[index].direction];
						sharkArray[index].col += DELTA_X[sharkArray[index].direction];
						move -= 1;
						
						if (sharkArray[index].row < 1) {
							sharkArray[index].direction = 2;
							sharkArray[index].row = 2;
						}
						else if (sharkArray[index].row > boardRow) {
							sharkArray[index].direction = 1;
							sharkArray[index].row = boardRow-1;
						}
					}
				}
				
				else if(sharkArray[index].direction == 3 || sharkArray[index].direction == 4) {
					int move = sharkArray[index].speed % colMove;
					
					while (move != 0) {
						sharkArray[index].row += DELTA_Y[sharkArray[index].direction];
						sharkArray[index].col += DELTA_X[sharkArray[index].direction];
						move -= 1;
						
						if (sharkArray[index].col < 1) {
							sharkArray[index].direction = 3;
							sharkArray[index].col = 2;
						}
						else if (sharkArray[index].col > boardCol) {
							sharkArray[index].direction = 4;
							sharkArray[index].col = boardCol-1;
						}
					}
				}
				
				
				int checkBoardIndex = sharkArray[index].location();
				
				// 마주친 두 상어의 update가 같으면 대소비교 후 한 마리의 상어가 먹힌다.
				// update가 다르거나 비어있다면(0) 덮어쓴다.
				if (sharkArray[checkBoard[checkBoardIndex]].update == sharkArray[index].update &&
						sharkArray[checkBoard[checkBoardIndex]].size < sharkArray[index].size) {
					
					isDead[checkBoard[checkBoardIndex]] = true;
					checkBoard[checkBoardIndex] = index;
				}
				else if (sharkArray[checkBoard[checkBoardIndex]].update == sharkArray[index].update &&
						sharkArray[checkBoard[checkBoardIndex]].size > sharkArray[index].size) {
					isDead[index] = true;
				}
				else {
					checkBoard[checkBoardIndex] = index;
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine().trim());
		
		boardRow = Integer.parseInt(st.nextToken());
		boardCol = Integer.parseInt(st.nextToken());
		sharkNum = Integer.parseInt(st.nextToken());
		
		rowMove = (boardRow*2)-2;
		colMove = (boardCol*2)-2;
		
		sharkArray = new Shark[sharkNum+50];
		sharkArray[0] = new Shark(0, 0, 0, 0, 0);
		isDead = new boolean[sharkNum+50];
		
		// 상어의 좌표 == row*100+col 상어의 size는 1부터 시작하므로 0은 상어가 없음을 의미
		checkBoard = new int[boardRow*1000+boardCol+50];
		
		for (int index=1; index <= sharkNum; index++) {
			st = new StringTokenizer(br.readLine().trim());
			sharkArray[index] = new Shark(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
			
			int checkBoardIndex = sharkArray[index].location();
			
//			 같은 곳에 존재 시 잡아먹히는 것을 구현
//			 checkBoard[좌표] == 각 상어의 index
			if (checkBoard[checkBoardIndex] == 0) {
				checkBoard[checkBoardIndex] = index;
			}
			else if (sharkArray[checkBoard[checkBoardIndex]].size < sharkArray[index].size) {
				isDead[checkBoard[checkBoardIndex]] = true;
				checkBoard[checkBoardIndex] = index;
			}
			else {
				isDead[index] = true;
			}
		}
		
		answer = 0;
		
		catchShark(1);
		System.out.println(answer);
	}
}
