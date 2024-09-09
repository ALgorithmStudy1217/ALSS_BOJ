package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

// BOJ 17143 낚시왕

/*

RxC 격자판에서 상어 낚시
칸에는 상어가 최대 한 마리, 상어는 크기와 속도를 가지고 있다.
낚시왕은 처음에 1번 열의 한 칸 왼쪽에 있다.
낚시왕이 가장 오른쪽 열의 오른쪽 칸에 이동하면 이동을 멈춘다.
1초 동안 순서대로
1. 낚시왕이 오른쪽으로 한 칸 이동한다.
2. 낚시왕이 있는 열에 있는 상어 중에서 땅과 제일 가까운 상어를 잡느다.
상어를 잡으면 격자판에서 잡은 상어가 사라진다.
3. 상어가 이동한다.
상어는 입력으로 주어진 속도로 이동하고, 속도의 단위는 칸/초
상어가 이동하려고 하는 칸이 격자판의 경계를 넘는 경우에는 방향을 반대로 바꿔서 속력을 유지한 채로 이동
이동을 마친 후에 한 칸에 상어가 두 마리 이상 있을 수 있다. 이때는 크기가 가장 큰 상어가 나머지 상어를 모두 잡아먹는다.
낚시왕이 잡은 상어 크기의 합을 구해보자.
 
 R,C 2~100 M 0 ~ RxC
 M개의 줄에 상어의 정보 r c s d z 상어의 위치, 속력, 이동 방향, 크기  1234 상하우좌
 
1. 낚시왕의 위치를 오른쪽으로 한칸 움직인다.
2. 낚시왕이 있는 열에 있는 상어 중 제일 가까운 상어를 잡는다.
	2-1. 상어 리스트에 있는 상어들 중에서
	2-2. 상어가 낚시왕과 같은 열에 있다면
	2-3. 가장 가까운 행에 있는 상어를 찾는
	2-3. 상어를 격자판에서 지운다
3. 상어가 이동한다.
	3-1. 현재 격자판을 복사한다.
	3-2. 상어 리스트를 돌면서 상어를 이동시킨다.
	3-3. 도착한 위치에 이미 상어가 존재한다면
	3-4. 사이즈가 작은 상어를 리스트에서 삭제한다.
	3-5. 완성된 격자판을 원본에 덮어 쓴다.
4. 낚시왕이 맨 오른쪽 열 오른쪽칸으로 갈 때까지 반복 

 */

public class BOJ17143 {
	static BufferedReader br;
	static StringTokenizer st;

	static final int UP = 1;
	static final int DOWN = 2;
	static final int RIGHT = 3;
	static final int LEFT = 4;

	static final int ROW_DELTA[] = { 0, -1, 1, 0, 0 };
	static final int COL_DELTA[] = { 0, 0, 0, 1, -1 };
	static final int[] CHANGE_DIR = { 0, 2, 1, 4, 3 };

	static class Shark {
		int row;
		int col;
		int speed;
		int dir;
		int size;

		public Shark(int row, int col, int speed, int dir, int size) {
			super();
			this.row = row;
			this.col = col;
			this.speed = speed;
			this.dir = dir;
			this.size = size;
		}
	}

	static int rowSize;
	static int colSize;
	static int sharksNum;

	static int fishingKingCol;
	static int caughtSharkSize;

	static Shark[] sharkList;
	static int[][] grid;
	static int[][] moveGrid;

	public static void init() throws IOException {
		st = new StringTokenizer(br.readLine().trim());
		rowSize = Integer.parseInt(st.nextToken());
		colSize = Integer.parseInt(st.nextToken());
		sharksNum = Integer.parseInt(st.nextToken());

		grid = new int[rowSize + 1][colSize + 1];
		moveGrid = new int[rowSize + 1][colSize + 1];

		sharkList = new Shark[sharksNum];
		int row = 0, col = 0, speed = 0, dir = 0, size = 0;
		for (int sharkIndex = 0; sharkIndex < sharksNum; ++sharkIndex) {
			st = new StringTokenizer(br.readLine().trim());
			row = Integer.parseInt(st.nextToken());
			col = Integer.parseInt(st.nextToken());
			speed = Integer.parseInt(st.nextToken());
			dir = Integer.parseInt(st.nextToken());
			size = Integer.parseInt(st.nextToken());
			sharkList[sharkIndex] = new Shark(row, col, speed, dir, size);
			grid[row][col] = sharkIndex;
		}

		fishingKingCol = 0;
		caughtSharkSize = 0;
	}

	public static void fishNearShark() {

		int nearSharkIndex = -1;
		int nearSharkRow = Integer.MAX_VALUE;
		// 2-1. 상어 리스트에 있는 상어들 중에서
		for (int sharkIndex = 0; sharkIndex < sharkList.length; ++sharkIndex) {
			Shark shark = sharkList[sharkIndex];
			if (shark == null) {
				continue;
			}
			// 2-2. 상어가 낚시왕과 같은 열에 있다면
			if (shark.col == fishingKingCol) {
				// 2-3. 가장 가까운 행에 있는 상어를 찾는
				if (nearSharkRow > shark.row) {
					nearSharkRow = shark.row;
					nearSharkIndex = sharkIndex;
				}
			}
		}
		// 2-3. 상어를 격자판에서 지운다
		if (nearSharkIndex != -1) {
			caughtSharkSize += sharkList[nearSharkIndex].size;
			sharkList[nearSharkIndex] = null;
		}
	}

	public static boolean isInEdge(Shark shark) {
		switch (shark.dir) {
		case UP:
			return shark.row == 1;
		case DOWN:
			return shark.row == rowSize;
		case RIGHT:
			return shark.col == colSize;
		default:
			return shark.col == 1;
		}
	}

	public static void sharksMove() {

		// 3-1. 격자판을 -1로 초기화한다
		for (int row = 1; row <= rowSize; ++row) {
			Arrays.fill(grid[row], -1);
		}
		// 3-2. 상어 리스트를 돌면서 상어를 이동시킨다.
		for (int sharkIndex = 0; sharkIndex < sharksNum; ++sharkIndex) {
			Shark shark = sharkList[sharkIndex];
			if (shark == null) {
				continue;
			}
			int speed = 0;
			if (shark.dir < RIGHT) {
				speed = shark.speed % (rowSize * 2 - 2);
			} else {
				speed = shark.speed % (colSize * 2 - 2);	
			}
			for (int moveCnt = 0; moveCnt < speed; ++moveCnt) {
				if (isInEdge(shark)) {
					shark.dir = CHANGE_DIR[shark.dir];
				}
				shark.row += ROW_DELTA[shark.dir];
				shark.col += COL_DELTA[shark.dir];
			}

			// 3-3. 도착한 위치에 이미 상어가 존재한다면
			if (grid[shark.row][shark.col] != -1) {
				if (sharkList[grid[shark.row][shark.col]].size > shark.size) {
					// 3-4. 사이즈가 작은 상어를 리스트에서 삭제한다.
					sharkList[sharkIndex] = null;
					shark = null;
				} else {
					sharkList[grid[shark.row][shark.col]] = null;
					grid[shark.row][shark.col] = sharkIndex;
				}
			} else {
				grid[shark.row][shark.col] = sharkIndex;
			}
		}

	}

	public static void fish() {
		// 4. 낚시왕이 맨 오른쪽 열 오른쪽칸으로 갈 때까지 반복
		while (++fishingKingCol <= colSize) {
			// 1. 낚시왕의 위치를 오른쪽으로 한칸 움직인다.

			// 2. 낚시왕이 있는 열에 있는 상어 중 제일 가까운 상어를 잡는다.
			fishNearShark();

			// 3. 상어가 이동한다.
			sharksMove();
		}
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));

		init();
		fish();
		System.out.println(caughtSharkSize);
	}
}
