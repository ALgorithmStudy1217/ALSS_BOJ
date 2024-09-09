package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;

// BOJ 19237 어른 상어

/*
상어가 사는 공간에 다른 상어들만 남아있다.
상어에는 1 이상 M 이하의 자연수 번호가 붙어 있고, 모든 번호는 서로 다르다.
1의 번호를 가진 어른 상어는 가장 강력
NxN 크기의 격자 중 M개의 칸에 상어가 한 마리씩 들어 있다.
맨 처음에는 모든 상어가 자신의 위치에 자신의 냄새를 뿌린다.
그 후 1초마다 모든 상어가 동시에 상하좌우로 인접한 칸 중 하나로 이동하고, 자신의 냄새를 그 칸에 뿌린다.
냄새는 K번 이동하고 나면 사라진다.
각 상어가 이동 방향을 결정할 때는, 먼저 인접한 칸 중 아무 냄새가 없는 칸의 방향으로 잡는다. 그런 칸이 없으면 자신의 냄새가 있는 칸의 방향으로 잡는다.
이때 가능한 칸이 여러 개일 수 있는데, 그 경우에는 특정한 우선순위를 따른다. 우선순위는 상어마다 다를 수 있고, 같은 상어라도 현재 상어가 보고 있느 방향에 따라 또 다를 수 있다.
상어가 맨 처음에 보고 있는 방향은 입력으로 주어지고, 그 후에는 방금 이동한 방향이 보고 있는 방향이 된다.
모든 상어가 이동한 후 한 칸에 여러 마리의 상어가 남아 있으면, 가장 작은 번호를 가진 상어를 제외하고 모두 격자 밖으로 쫓겨난다.



1. N, M, K 입력 (2~20 2~N^2, 1~1000)
2. N개의 줄에 걸쳐 격자의 모습이 주어진다. 0은 빈칸, 0이 아닌 수 x는 x번 상어가 들어있는 칸
3. 그 다음줄에는 각 상어의 방향이 차례대로 주어진다. 1234 위아래왼쪽오른쪽
4. 그 다음 둘부터 각 상어의 방향 우선순위가 상어 당 4줄씩 차레대로 주어진다.
5. 1번 상어만 남을 때까지
6. 모든 상어가 자신의 위치에 자신의 냄새를 뿌린다.
7. 모든 상어가 동시에 상하좌우로 인접한 칸 중 하나로 이동한다.
 	7-1. 인전합 칸 중 아무 냄새가 없는 칸의 방향으로 잡는다.
 	7-2. 그런 칸이 없으면 자신의 냄새가 있는 칸의 방향으로 잡는다.
 	7-3. 이때 가능한 칸이 여러 개이면 우선순위를 따른다.
 8. 모든 상어가 이동한 후 한 칸에 여러 마리의 상어가 남아 있으면, 가장 작은 번호를 가진 상어를 제외하고 모두 쫓겨난다.
 */

public class BOJ19237 {
	static BufferedReader br;
	static StringTokenizer st;

	static final int UP = 1;
	static final int DOWN = 2;
	static final int LEFT = 3;
	static final int RIGHT = 4;
	static final int[] ROW_DELTA = { 0, -1, 1, 0, 0 };
	static final int[] COL_DELTA = { 0, 0, 0, -1, 1 };

	static int size;
	static int sharkNum;
	static int restTime;

	static int[][] sharkMap;
	static int[][] moveMap;
	static int[][] scentMap;

	static class Shark {
		int row;
		int col;
		int num;
		int dir;
		int[][] priority;

		public Shark(int row, int col, int num) {
			this.row = row;
			this.col = col;
			this.num = num;

			priority = new int[5][4];
		}
	}

	static Shark[] sharks;

	public static boolean canMove(int row, int col) {
		return (row >= 0 && row < size && col >= 0 && col < size);
	}

	public static void init() throws IOException {
		// 1. N, M, K 입력 (2~20 2~N^2, 1~1000)
		st = new StringTokenizer(br.readLine().trim());
		size = Integer.parseInt(st.nextToken());
		sharkNum = Integer.parseInt(st.nextToken());
		restTime = Integer.parseInt(st.nextToken());

		// 2. N개의 줄에 걸쳐 격자의 모습이 주어진다. 0은 빈칸, 0이 아닌 수 x는 x번 상어가 들어있는 칸
		sharks = new Shark[sharkNum + 1];
		sharkMap = new int[size][size];
		scentMap = new int[size][size];
		moveMap = new int[size][size];
		for (int row = 0; row < size; ++row) {
			st = new StringTokenizer(br.readLine().trim());
			for (int col = 0; col < size; ++col) {
				sharkMap[row][col] = Integer.parseInt(st.nextToken());
				if (sharkMap[row][col] != 0) {
					sharks[sharkMap[row][col]] = new Shark(row, col, sharkMap[row][col]);
				}
			}
		}

		// 3. 그 다음줄에는 각 상어의 방향이 차례대로 주어진다. 1234 위아래왼쪽오른쪽
		st = new StringTokenizer(br.readLine().trim());
		for (int shark = 1; shark <= sharkNum; ++shark) {
			sharks[shark].dir = Integer.parseInt(st.nextToken());
		}

		// 4. 그 다음 둘부터 각 상어의 방향 우선순위가 상어 당 4줄씩 차레대로 주어진다.
		for (int shark = 1; shark <= sharkNum; ++shark) {
			for (int dir = UP; dir <= RIGHT; ++dir) {
				st = new StringTokenizer(br.readLine().trim());
				for (int priority = 0; priority < 4; ++priority) {
					int priorityDir = Integer.parseInt(st.nextToken());
					sharks[shark].priority[dir][priority] = priorityDir;
				}
			}
		}

	}

	public static boolean onlyOne() {
		for (int shark = 2; shark <= sharkNum; shark++) {
			if (sharks[shark] != null) {
				return false;
			}
		}
		return true;
	}

	public static void spray() {
		for (int shark = 1; shark <= sharkNum; ++shark) {
			if (sharks[shark] == null) {
				continue;
			}

			Shark curShark = sharks[shark];

			sharkMap[curShark.row][curShark.col] = shark;
			scentMap[curShark.row][curShark.col] = restTime;
		}
	}

	public static void sharksMove() {

		for (int shark = 1; shark <= sharkNum; ++shark) {
			if (sharks[shark] == null) {
				continue;
			}

			Shark curShark = sharks[shark];

			int dir = curShark.dir;
			boolean nearEmpty = false;
			// 7-1. 인전합 칸 중 아무 냄새가 없는 칸의 방향으로 잡는다.
			for (int priority = 0; priority < 4; ++priority) {
				// 7-3. 이때 가능한 칸이 여러 개이면 우선순위를 따른다.
				int nextRow = curShark.row + ROW_DELTA[curShark.priority[dir][priority]];
				int nextCol = curShark.col + COL_DELTA[curShark.priority[dir][priority]];

				if (!canMove(nextRow, nextCol)) {
					continue;
				}
				if (sharkMap[nextRow][nextCol] == 0) {
					// 8. 모든 상어가 이동한 후 한 칸에 여러 마리의 상어가 남아 있으면, 가장 작은 번호를 가진 상어를 제외하고 모두 쫓겨난다.
					if (moveMap[nextRow][nextCol] != 0) {
						// 상어 번호순으로 움직였기 때문에 늦게 들어온 애가 번호가 더 큼
						sharks[shark] = null;
						nearEmpty = true;
						break;
					}
					moveMap[nextRow][nextCol] = shark;
					curShark.row = nextRow;
					curShark.col = nextCol;
					curShark.dir = curShark.priority[dir][priority];
					nearEmpty = true;
					break;
				}
			}

			// 7-2. 그런 칸이 없으면 자신의 냄새가 있는 칸의 방향으로 잡는다.
			if (!nearEmpty) {
				for (int priority = 0; priority < 4; ++priority) {
					// 7-3. 이때 가능한 칸이 여러 개이면 우선순위를 따른다.
					int nextRow = curShark.row + ROW_DELTA[curShark.priority[dir][priority]];
					int nextCol = curShark.col + COL_DELTA[curShark.priority[dir][priority]];

					if (!canMove(nextRow, nextCol)) {
						continue;
					}
					if (sharkMap[nextRow][nextCol] == shark) {
						moveMap[nextRow][nextCol] = shark;
						curShark.row = nextRow;
						curShark.col = nextCol;
						curShark.dir = curShark.priority[dir][priority];
						break;
					}
				}
			}
		}
	}

	public static void downRestTime() {
		for (int row = 0; row < size; ++row) {
			for (int col = 0; col < size; ++col) {
				if (sharkMap[row][col] != 0) {
					scentMap[row][col]--;
					if (scentMap[row][col] == 0) {
						sharkMap[row][col] = 0;
					}
				}
			}
		}
	}

	public static int move() {
		int elapsedTime = 0;
		// 5. 1번 상어만 남을 때까지
		while (!onlyOne() && elapsedTime <= 1000) {
			// 6. 모든 상어가 자신의 위치에 자신의 냄새를 뿌린다.
			downRestTime();
			spray();

			// 7. 모든 상어가 동시에 상하좌우로 인접한 칸 중 하나로 이동한다.
			elapsedTime++;
			for (int row = 0; row < size; ++row) {
				moveMap[row] = sharkMap[row].clone();
			}
			sharksMove();
			for (int row = 0; row < size; ++row) {
				sharkMap[row] = moveMap[row].clone();
			}
//			for (int row = 0; row < size; ++row) {
//				System.out.println(Arrays.toString(sharkMap[row]));
//			}
//			System.out.println(elapsedTime);
		}
		return elapsedTime;
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));
		init();
		int elapsedTime = move();
		System.out.println(elapsedTime <= 1000 ? elapsedTime : -1);
	}
}
