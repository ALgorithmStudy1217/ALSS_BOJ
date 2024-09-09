package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

// BOJ 16236 아기상어

/*
NxN 크기의 공간에 물고기 M마리와 아기 상어 1마리가 있다.
한 칸에는 물고기가 최대 1마리 존재한다.
아기 상어와 물고기는 모두 크기를 가지고 있고, 이 크기는 자연수이다.
가장 처음에 아기 상어의 크기는 2이고, 아기 상어는 1초에 상하좌우로 인접한 한 칸씩 이동한다.
아기 상어는 자신의 크기보다 큰 물고기가 있는 칸은 지나갈 수 없고 나머지 칸은 모두 지나갈 수 있다.
아기 상어는 자신의 크기보다 작은 물고기만 먹을 수 있다. 따라서, 크기가 같은 물고기는 먹을 수 없지만, 지나갈 수 있다.
이동할지 결정하는 방법
	- 더 이상 먹을 수 있는 물고기가 없다면 도움 요청
	- 1마리라면 그 물고기를 먹으러 간다
	- 1마리보다 많다면 거리가 가장 가까운 물고기를 먹으러 간다.
		- 거리는 지나야하는 칸의 개수의 최솟값
		- 거리가 가까운 물고기가 많다면, 가장 위에 있는 물고기, 그러한 물고기가 여러마리라면, 가장 왼쪽에 있는 물고기
아기 상어의 이동은 1초 걸리고, 물고기를 먹는데 걸리는 시간은 없다고 가정
아기 상어는 자신의 크기와 같은 수의 물고기를 먹을 때 마다 크기가 1증가
공간의 상태가 주어졌을 때, 아기 상어가 몇 초 동안 엄마 상어에게 도움을 요청하지 않고 물고기를 잡아먹을 수 있는지 구하는 프로그램

1. 첫째 줄에 공간의 크기 N 2~20
2. 둘째 줄부터 공간의 상태. 0 빈칸 123456 물고기의 크기 9 아기 상어의 위치
3. 아기상어가 먹이를 너비 우선으로 탐색한다.
	3-1. 공간 밖이면 못움직인다.
	3-2. 이미 지나간 공간이면 지나가지 않는다.
	3-3. 아기상어보다 큰 물고기가 있다면 못지나간다.
	3-4. 먹이를 찾으면 현재까지 탐색한 깊이가 이동시간이다.
4. 탐색을 해도 먹이가 없으면 종료
 */

public class BOJ16236_아기상어 {
	static BufferedReader br;
	static StringTokenizer st;

	static int size;
	static int[][] map;

	static class Shark {
		int row;
		int col;
		int size;
		int count;

		public Shark(int row, int col, int size, int count) {
			this.row = row;
			this.col = col;
			this.size = size;
			this.count = count;
		}

		public void eat() {
			count++;
			if (count == size) {
				size++;
				count = 0;
			}
		}
	}

	static final int SHARK = 9;
	static Shark babyShark;

	static int elapsedTime;

	public static void init() throws IOException {
		// 1. 첫째 줄에 공간의 크기 N 2~20
		size = Integer.parseInt(br.readLine().trim());

		// 2. 둘째 줄부터 공간의 상태. 0 빈칸 123456 물고기의 크기 9 아기 상어의 위치
		map = new int[size][size];
		for (int row = 0; row < size; ++row) {
			st = new StringTokenizer(br.readLine().trim());
			for (int col = 0; col < size; ++col) {
				map[row][col] = Integer.parseInt(st.nextToken());
				if (map[row][col] == SHARK) {
					babyShark = new Shark(row, col, 2, 0);
				}
			}
		}
		elapsedTime = 0;
	}

	static boolean[][] visited;
	static final int[] ROW_DELTA = { -1, 0, 0, 1 };
	static final int[] COL_DELTA = { 0, -1, 1, 0 };

	public static boolean canMove(Shark shark) {
		return shark.row >= 0 && shark.row < size && shark.col >= 0 && shark.col < size;
	}

	public static boolean isVisited(Shark shark) {
		return visited[shark.row][shark.col];
	}

	public static boolean isThereBiggerFish(Shark shark) {
		return map[shark.row][shark.col] != SHARK && map[shark.row][shark.col] > shark.size;
	}

	public static boolean isThereEatableFish(Shark shark) {
		return map[shark.row][shark.col] != SHARK && map[shark.row][shark.col] != 0 && map[shark.row][shark.col] < shark.size;
	}

	public static boolean findEatableFish() {
		visited = new boolean[size][size];
		Queue<Shark> queue = new ArrayDeque<>();
		queue.offer(babyShark);
		visited[babyShark.row][babyShark.col] = true;

		// 3. 아기상어가 먹이를 너비 우선으로 탐색한다.
		int distance = 0;
		int fishRow = -1;
		int fishCol = -1;
		while (!queue.isEmpty()) {
			int queueSize = queue.size();
			distance++;

			while (--queueSize >= 0) {
				Shark curShark = queue.poll();

				for (int delta = 0; delta < ROW_DELTA.length; ++delta) {
					Shark nextShark = new Shark(curShark.row + ROW_DELTA[delta], curShark.col + COL_DELTA[delta],
							curShark.size, curShark.count);

					// 3-1. 공간 밖이면 못움직인다.
					if (!canMove(nextShark)) {
						continue;
					}
					// 3-2. 이미 지나간 공간이면 지나가지 않는다.
					if (isVisited(nextShark)) {
						continue;
					}
					// 3-3. 아기상어보다 큰 물고기가 있다면 못지나간다.
					if (isThereBiggerFish(nextShark)) {
						continue;
					}
					// 3-4. 먹이를 찾으면 현재까지 탐색한 깊이가 이동시간이다.
					if (isThereEatableFish(nextShark)) {
						// babyShark = nextShark;
						// babyShark.eat();
						// map[babyShark.row][babyShark.col] = 0;
						// elapsedTime += distance;
						if (fishRow == -1) {
							fishRow = nextShark.row;
							fishCol = nextShark.col;
						} else {
							if (nextShark.row < fishRow) {
								fishRow = nextShark.row;
								fishCol = nextShark.col;
							} else if (nextShark.row == fishRow) {
								if (nextShark.col < fishCol) {
									fishRow = nextShark.row;
									fishCol = nextShark.col;
								}
							}
						}
						continue;
					}
					// 없다면 계속 탐색
					queue.offer(nextShark);
					visited[nextShark.row][nextShark.col] = true;
				}
			}
			// 3-4. 먹이를 찾으면 현재까지 탐색한 깊이가 이동시간이다.
			if (fishRow != -1) {
				babyShark = new Shark(fishRow, fishCol, babyShark.size, babyShark.count);
				babyShark.eat();
				map[fishRow][fishCol] = 0;
				elapsedTime += distance;
				return true;
			}
		}
		return false;
	}

	public static void move() {
		while (true) {
			if (!findEatableFish()) {
				// 4. 탐색을 해도 먹이가 없으면 종료
				System.out.println(elapsedTime);
				return;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));

		init();
		move();
	}
}
