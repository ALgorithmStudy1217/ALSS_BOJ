package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

//BOJ 19236 청소년 상어

/*
* 4x4 크기의 공간 크기가 1x1인 정사각형 칸으로 나누어져 있다.
* 공간의 각 칸은 (x,y)와 같이 표현 x는 행의 번호 y는 열의 번호
* 한 칸에는 물고기가 한 마리 존재
* 각 물고기는 번호와 방향을 가지고 있다.
* 번호는 1보다 크거나 같고, 16보다 작거나 같은 자연수이며, 두 물고기가 같은 번호를 갖는 경우는 없다.
* 방향은 8가지 방향(상하좌우, 대각선)중 하나이다.
* 상어는 0,0에 있는 물고기를 먹고 0,0에 들어각 ㅔ된다. 상어의 방향은 (0,0)에 있던 물고기의 방향과 같다. 이후 물고기가 이동
* 물고기는 번호가 작은 물고기부터 순서대로 이동한다. 물고기는 한 칸을 이동할 수 있고 이동할 수 있는 칸은 빈 칸과 다른 물고기가 있는 칸,
* 이동할 수 없는 칸은 상어가 있거나, 공간의 경계를 넘는 칸이다. 각 물고기는 방향이 이동할 수 있는 칸을 향할 때까지 방향을 45도 반시계 회전
* 만약ㄱ, 이동할 수 있는 칸이 없으면 이동을 하지 않는다. 그 외의 경우에는 그 칸으로 이동한다.
* 물고기가 다른 물고기가 있는 칸으로 이동할 때는 서로의 위치를 바꾸는 방식으로 이동한다.
* 물고기의 이동이 모두 끝나면 상어가 이동한다. 상어는 방향에 있는 칸으로 이동할 수 있는데, 한 번에 여러 개의 칸을 이동할 수 있다.
* 이동하는 중에 지나가는 칸에 있는 물고기는 먹지 않는다. 물고기가 없는 칸으로는 이동할 수 없다.
* 상어가 이동할 수 있는 칸이 없으면 공간에서 벗어나 집으로 간다.
* 상어가 이동한 후에는 다시 물고기가 이동하며, 이후 이 과정이 계속해서 반복된다.
* 상어가 먹을 수 있는 물고기 번호의 최댓값을 구해보자.
* 
* 1.물고기의 정보 입력 물고기의 번호, 물고기의 번호
* 
*/

public class BOJ19236 {

	static final int UP = 0;
	static final int UP_LEFT = 1;
	static final int LEFT = 2;
	static final int LEFT_DOWN = 3;
	static final int DOWN = 4;
	static final int DOWN_RIGHT = 5;
	static final int RIGHT = 6;
	static final int RIHGT_UP = 7;

	static final int[] ROW_DELTA = { -1, -1, 0, 1, 1, 1, 0, -1 };
	static final int[] COL_DELTA = { 0, -1, -1, -1, 0, 1, 1, 1 };

	static class Fish {
		int row;
		int col;
		int dir;

		public Fish(int row, int col, int dir) {
			this.row = row;
			this.col = col;
			this.dir = dir;
		}
	}

	static class Shark extends Fish {
		int size;

		public Shark(int row, int col, int dir, int size) {
			super(row, col, dir);
			this.size = size;
		}
	}

	static final int FISH = 16;
	static final int SIZE = 4;
	
	static int maxSize;
	
	public static boolean isThereShark(Fish fish, Shark shark) {
		return (fish.row == shark.row && fish.col == shark.col);
	}
	
	public static boolean isEmpty(int[][] map, Fish fish) {
		return map[fish.row][fish.col] == 0; 
	}
	
	public static boolean canMove(Fish fish) {
		return canMove(fish.row, fish.col);
	}
	
	public static boolean canMove(int row, int col) {
		if (row < 0 || row >= SIZE || col < 0 || col >= SIZE ) {
			return false;
		}
		return true;
	}
	
	public static void eat(int[][] map, Fish[] fishes, Shark shark) {
		int num = map[shark.row][shark.col]; 
		shark.size += num;
		shark.dir = fishes[num].dir;
		fishes[num] = null;
		map[shark.row][shark.col]=0; 
	}
	
	public static void rotate(Fish fish) {
		fish.dir = (fish.dir + 1) % 8;
	}
	
	public static void fishMove(int[][] map, Fish[] fishes, Shark shark) {
		
		for (int fish = 1; fish <= FISH; ++fish) {
			if (fishes[fish] == null ) {
				continue;
			}
			
			Fish currentFish = fishes[fish];
			int row = currentFish.row;
			int col = currentFish.col;
			
			for (int cnt = 0; cnt < 8; ++cnt) {
				currentFish.row = row + ROW_DELTA[currentFish.dir];
				currentFish.col = col + COL_DELTA[currentFish.dir];
				
				if (!canMove(currentFish)) {
					rotate(currentFish);
					continue;
				}
				
				if (isThereShark(currentFish, shark)) {
					rotate(currentFish);
					continue;
				}
				
				if (isEmpty(map, currentFish)) {
					map[row][col] = 0;
					map[currentFish.row][currentFish.col] = fish; 
					break;
				}
				
				int otherFish = map[currentFish.row][currentFish.col];
				fishes[otherFish].row = row;
				fishes[otherFish].col = col;
				map[row][col] = otherFish;
				map[currentFish.row][currentFish.col] = fish;
				break;
			}
		}		
	}
	
	public static void move(int[][] map, Fish[] fishes, Shark shark) {
		if (!canMove(shark)) {
			maxSize = Math.max(maxSize, shark.size);
			return;
		}
		if (isEmpty(map,shark)) {
			maxSize = Math.max(maxSize, shark.size);
			return;
		}
		
		int[][] copyMap = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; ++row) {
			copyMap[row] = map[row].clone();
		}
		Fish[] copyFishes = new Fish[FISH + 1];
		for (int fish = 1; fish<= FISH; ++fish) {
			if (fishes[fish] == null) {
				continue;
			}
			copyFishes[fish] = new Fish(fishes[fish].row, fishes[fish].col, fishes[fish].dir); 
		}
		Shark copyShark = new Shark(shark.row, shark.col, shark.dir, shark.size);
		
		// 상어가 물고기를 먹는다.
		eat(copyMap, copyFishes, copyShark);
		
		// 물고기들이 이동한다.
		fishMove(copyMap, copyFishes, copyShark);
		
		// 상어가 이동한다.
		int sharkRow = copyShark.row;
		int sharkCol = copyShark.col;
		for (int cnt = 1; cnt < SIZE; ++cnt) {
			copyShark.row = sharkRow + ROW_DELTA[copyShark.dir] * cnt;
			copyShark.col = sharkCol + COL_DELTA[copyShark.dir] * cnt;
			move(copyMap, copyFishes, copyShark);
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		// 1.물고기의 정보 입력 물고기의 번호, 이동 방향
		Fish[] fishes = new Fish[FISH + 1];
		int[][] map = new int[SIZE][SIZE];
		for (int row = 0; row < SIZE; row++) {
			StringTokenizer st = new StringTokenizer(br.readLine().trim());
			for (int col = 0; col < SIZE; ++col) {
				int num = Integer.parseInt(st.nextToken());
				int dir = Integer.parseInt(st.nextToken()) - 1;
				map[row][col] = num;
				fishes[num] = new Fish(row, col, dir);
			}
		}

		Shark shark = new Shark(0,0,0,0);
		move(map, fishes, shark);
		System.out.println(maxSize);
	}
}