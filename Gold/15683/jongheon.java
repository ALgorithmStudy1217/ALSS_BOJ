/*
= BOJ 15683. 감시

= 특이사항
CCTV는 CCTV를 통과할 수 있고, 벽은 통과하지 못한다.

= 로직
1. 초기 세팅
	1-1. 변수 초기화
	1-2. 사무실의 행과 열 크기 입력
	1-3. 사무실의 행 크기만큼 사무실의 정보 입력
		1-3-1. 빈 칸의 개수 보관
		1-3-2. CCTV 좌표 보관
2. CCTV의 방향을 순열로 결정
	2-1. 모든 CCTV의 방향을 선택했다면 사각 지대 탐색
	2-2. 다음 CCTV 방향 선택
3. CCTV 방향에 맞춰 사각 지대 탐색
4. 출력
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output = new StringBuilder();
	static StringTokenizer st;
	static final int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; //상우하좌 배열
	static final int MAX_CCTV_SIZE = 8, DELTA_SIZE = 4, ROTATE_SIZE = 4;
	static final int EMPTY = 0, WALL = 6;
	static int rowSize, colSize;
	static int[][] office;
	static Position[] cctvs;
	static int[] cctvDeltas;
	static int cctvSize; 
	static int blindSpotCount;
	static int version;
	static int minBlindSpot;
	
	public static class Position {
		int row;
		int col;
		CCTV cctv;
		
		public Position(int row, int col, CCTV cctv) {
			this.row = row;
			this.col = col;
			this.cctv = cctv;
		}
	}
	
	public static enum CCTV {
		FIRST(1, 0b0010, 4),
		SECOND(2, 0b1010, 2),
		THIRD(3, 0b0011, 4),
		FOURTH(4, 0b1011, 4),
		FIFTH(5, 0b1111, 1);
		
		final int number;
		final int deltaIdx;
		final int rotateSize;
		
		CCTV(int number, int deltaIdx, int rotateSize) {
			this.number = number;
			this.deltaIdx = deltaIdx;
			this.rotateSize = rotateSize;
		}
		
		public int getDeltaIdxWithRotate(int rotateIdx) {
			int temp = deltaIdx;
			
			for (int idx = 0; idx < rotateIdx; idx++) {
				temp = (temp >> 1) | ((temp & 1) << (ROTATE_SIZE - 1));
			}
			
			return temp;
		}

		public static CCTV of(int number) {
			for (CCTV cctv : values()) {
				if (cctv.number == number)
					return cctv;
			}
			
			return null;
		}
	}
	
	public static void selectCCTVDirection(int cctvIdx) {
		//2-1. 모든 CCTV의 방향을 선택했다면 사각 지대 탐색
		if (cctvIdx == cctvSize) {
			//3. CCTV 방향에 맞춰 사각 지대 탐색
			version--;
			minBlindSpot = Math.min(findBlindSpot(), minBlindSpot);
			
			return;
		}

		//2-2. 다음 CCTV 방향 선택
		CCTV cctv = cctvs[cctvIdx].cctv;
		for (int rotateIdx = 0; rotateIdx < cctv.rotateSize; rotateIdx++) {
			cctvDeltas[cctvIdx] = cctv.getDeltaIdxWithRotate(rotateIdx);
			selectCCTVDirection(cctvIdx + 1);
		}
	}
	
	//3. CCTV 방향에 맞춰 사각 지대 탐색
	public static int findBlindSpot() {
		int blindSpot = blindSpotCount;
		
		for (int cctvIdx = 0; cctvIdx < cctvSize; cctvIdx++) {
			Position cctv = cctvs[cctvIdx];
			for (int deltaIdx = 0; deltaIdx < DELTA_SIZE; deltaIdx++) {
				if ((cctvDeltas[cctvIdx] & 1 << deltaIdx) == 0)
					continue;
				
				int nr = cctv.row + deltas[deltaIdx][0];
				int nc = cctv.col + deltas[deltaIdx][1];
				while (canGo(nr, nc)) {
					if (office[nr][nc] <= EMPTY && office[nr][nc] != version) {
						office[nr][nc] = version;
						blindSpot--;
					}
					
					nr += deltas[deltaIdx][0];
					nc += deltas[deltaIdx][1];
				}
			}
		}
		
		return blindSpot;
	}
	
	public static boolean canGo(int row, int col) {
		return row >= 0 && row < rowSize && col >= 0 && col < colSize && office[row][col] != WALL;
	}
	
	public static void main(String[] args) throws IOException {
		//1. 초기 세팅
		initTestCase();
		
		//2. CCTV의 방향을 순열로 결정
		selectCCTVDirection(0);
		
		//4. 출력
		System.out.println(minBlindSpot);
	}
	
	public static void initTestCase() throws IOException {
		//1-1. 변수 초기화
		cctvs = new Position[MAX_CCTV_SIZE];
		cctvDeltas = new int[MAX_CCTV_SIZE];
		cctvSize = 0;
		blindSpotCount = 0;
		version = 0;
		minBlindSpot = Integer.MAX_VALUE;
		
		//1-2. 사람의 수와 친구 관계의 수 입력
		st = new StringTokenizer(input.readLine());
		rowSize = Integer.parseInt(st.nextToken());
		colSize = Integer.parseInt(st.nextToken());
		
		//1-3. 친구 관계의 수만큼 친구 관계 정보 입력
		office = new int[rowSize][colSize];
		for (int row = 0; row < rowSize; row++) {
			st = new StringTokenizer(input.readLine());
			for (int col = 0; col < colSize; col++) {
				office[row][col] = Integer.parseInt(st.nextToken());
				
				//1-3-1. 빈 칸의 개수 보관
				if (office[row][col] == EMPTY)
					blindSpotCount++;
				
				//1-3-2. CCTV 좌표 보관
				if (office[row][col] != EMPTY && office[row][col] != WALL)
					cctvs[cctvSize++] = new Position(row, col, CCTV.of(office[row][col]));
			}
		}
	}
}
