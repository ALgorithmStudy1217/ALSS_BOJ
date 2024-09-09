package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

// BOJ 1600 말이 되고픈 원숭이

/*

동물원에서 막 탈출한 원숭이 한 마리가 세상구경 말이 되기를 간절히 원했다.
그래서 그는 말의 움직임을 유사히 살펴보고 그대로 따라하기로
말은 격자판에서 체스의 나이트와 같은 이동방식
말은 장애물을 뛰어넘을 수 있다.
근데 원숭이는 능력이 부족해서 총 K번만 말같이 움직이고 그 외에는 그냥 인접한 칸으로만 움직일 수 있다.
대각선 방향은 인접한 칸에 포함되지 않는다.
격자판의 맨 왼쪽 위에서 시작해서 맨 오른쪽 아래까지 가야한다.
인접한 네 방향으로, 말의 움직임 모두 한 번의 동작으로 친다.
원숭이가 최소한의 동작으로 시작지점에서 도착지점까지 갈 수 있는 방법을 알아내는 프로그램

1. 첫째 줄에 정수 K가 주어진다. (0~30)
2. 둘째 줄에 격자판의 가로길이 W, 세로길이 H가 주어진다. (1~200)
3. H줄에 걸쳐 W개의 숫자가 주어지는데 0은 아무것도 없는 평지, 1은 장애물을 뜻한다.
4. 원숭이가 이동하는 칸까지 도달한 동작 횟수를 INF로 초기화 한다.
5. 우선순위 큐에 원숭이를 넣어놓고
6. 큐가 빌 때까지
	6-1. 큐에서 원숭이를 꺼낸다.
	6-2. 원숭이의 위치가 도착지점이면 종료
	6-3. 원숭이의 점프 횟수가 남아 있으면
	6-4. 점프 위치에 이동할 수 있으면 최소거리를 갱신해주고 큐에 넣는다.
	6-5. 원숭이의 인접한 위치에 이동할 수 있으면 최소거리를 갱신해주고 큐에 넣는다.

 */

public class BOJ1600 {
	static BufferedReader br;
	static StringTokenizer st;

	static int jumpCount;
	static int rowSize;
	static int colSize;

	static int[][] map;

	static class Monkey implements Comparable<Monkey> {
		int row;
		int col;
		int jumpCount;
		int moveCount;

		public Monkey(int row, int col, int jumpCount, int moveCount) {
			this.row = row;
			this.col = col;
			this.jumpCount = jumpCount;
			this.moveCount = moveCount;
		}

		@Override
		public int compareTo(Monkey other) {
			if (this.moveCount > other.moveCount) {
				return 1;
			} else if (this.moveCount < other.moveCount) {
				return -1;
			} else {
				return Integer.compare(this.jumpCount, other.jumpCount);
			}
		}
	}

	public static void init() throws IOException {
		// 1. 첫째 줄에 정수 K가 주어진다. (0~30)
		jumpCount = Integer.parseInt(br.readLine().trim());

		// 2. 둘째 줄에 격자판의 가로길이 W, 세로길이 H가 주어진다. (1~200)
		st = new StringTokenizer(br.readLine().trim());
		colSize = Integer.parseInt(st.nextToken());
		rowSize = Integer.parseInt(st.nextToken());

		// 3. H줄에 걸쳐 W개의 숫자가 주어지는데 0은 아무것도 없는 평지, 1은 장애물을 뜻한다.
		map = new int[rowSize][colSize];
		for (int row = 0; row < rowSize; ++row) {
			st = new StringTokenizer(br.readLine().trim());
			for (int col = 0; col < colSize; ++col) {
				map[row][col] = Integer.parseInt(st.nextToken());
			}
		}
	}

	static final int INF = Integer.MAX_VALUE;
	static final int startRow = 0;
	static final int startCol = 0;
	static int endRow;
	static int endCol;

	static final int[] ROW_DELTA = { -1, 1, 0, 0 };
	static final int[] COL_DELTA = { 0, 0, -1, 1 };

	static final int[] HORSE_ROW_DELTA = { -1, -2, -2, -1, 1, 2, 2, 1 };
	static final int[] HORSE_COL_DELTA = { -2, -1, 1, 2, -2, -1, 1, 2 };

	public static void move() {
		endRow = rowSize - 1;
		endCol = colSize - 1;
		// 4. 원숭이가 이동하는 칸까지 도달한 동작 횟수를 INF로 초기화 한다.
		int minMoveCount[][][] = new int[rowSize][colSize][jumpCount+1];
		for (int row = 0; row < rowSize; ++row) {
			for (int col = 0; col < colSize; ++col) {
				Arrays.fill(minMoveCount[row][col], INF);
			}
		}
		minMoveCount[startRow][startCol][0] = 0;

		// 5. 우선순위 큐에 원숭이를 넣어놓고
		PriorityQueue<Monkey> pQueue = new PriorityQueue<>();
		pQueue.offer(new Monkey(startRow, startCol, 0, minMoveCount[startRow][startCol][0]));

		// 6. 큐가 빌 때까지
		while (!pQueue.isEmpty()) {
			// 6-1. 큐에서 원숭이를 꺼낸다.
			Monkey monkey = pQueue.poll();

			// 6-2. 원숭이의 위치가 도착지점이면 종료
			if (monkey.row == endRow && monkey.col == endCol) {
				break;
			}
			// 6-3. 원숭이의 점프 횟수가 남아 있으면
			if (monkey.jumpCount < jumpCount) {
				for (int delta = 0; delta < HORSE_COL_DELTA.length; ++delta) {
					int nextRow = monkey.row + HORSE_ROW_DELTA[delta];
					int nextCol = monkey.col + HORSE_COL_DELTA[delta];
					// 6-4. 점프 위치에 이동할 수 있으면 최소거리를 갱신해주고 큐에 넣는다.
					if (!canMove(nextRow, nextCol)) {
						continue;
					}

					if (minMoveCount[nextRow][nextCol][monkey.jumpCount + 1] > monkey.moveCount + 1) {
						minMoveCount[nextRow][nextCol][monkey.jumpCount + 1] = monkey.moveCount + 1;
						pQueue.offer(
								new Monkey(nextRow, nextCol, monkey.jumpCount + 1, minMoveCount[nextRow][nextCol][monkey.jumpCount + 1]));
					}
				}
			}
			// 6-5. 원숭이의 인접한 위치에 이동할 수 있으면 최소거리를 갱신해주고 큐에 넣는다.
			for (int delta = 0; delta < ROW_DELTA.length; ++delta) {
				int nextRow = monkey.row + ROW_DELTA[delta];
				int nextCol = monkey.col + COL_DELTA[delta];

				if (!canMove(nextRow, nextCol)) {
					continue;
				}

				if (minMoveCount[nextRow][nextCol][monkey.jumpCount] > monkey.moveCount + 1) {
					minMoveCount[nextRow][nextCol][monkey.jumpCount] = monkey.moveCount + 1;
					pQueue.offer(new Monkey(nextRow, nextCol, monkey.jumpCount, minMoveCount[nextRow][nextCol][monkey.jumpCount]));
				}
			}

		}
		int minCount = INF;
		for (int jump = 0; jump <= jumpCount; ++jump) {
			minCount = Math.min(minCount, minMoveCount[endRow][endCol][jump]);
		}
		System.out.println(minCount != INF ? minCount : -1);
	}

	public static final int OBSTACLE = 1;

	public static boolean canMove(int row, int col) {
		if (row >= 0 && row < rowSize && col >= 0 && col < colSize) {
			if (map[row][col] == OBSTACLE) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));

		init();
		move();
	}
}
