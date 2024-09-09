package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

// BOJ 16236 아기 상어

/*
 * NxN 크기의 공간에 물고기 M마리와 아기 상어 1마리
 * 아기 상어와 물고기는 모두 크기를 가지고 있고 자연수, 처음 아기 상어의 크기 2, 1초에 상하좌우로 인접한 한 칸씩 이동한다.
 * 자신의 크기보다 큰 물고기가 있는 칸은 지나갈 수 없고, 나머지 칸은 모두 지나갈 수 있다. 자신의 크기보다 작은 물고기만 먹을 수 있다.
 * 크기가 같은 물고기는 먹을 수 없지만 지나갈 수 있다.
 * 더 이상 먹을 수 있는 물고기가 공간에 없다면 아기 상어는 엄마 상어에게 도움을 요청
 * 먹을 수 있는 물고기가 1마리라면, 그 물고기를 먹으러 간다.
 * 1마리보다 많다면, 거리가 가장 가까운 물고기를 먹으러 간다.
 *  - 거리는 아기 상어가 있는 칸에서 물고기가 있는 칸으로 이동할 때, 지나야하는 칸의 개수의 최솟값이다.
 *  - 거리가 가까운 물고기가 많다면, 가장 위에 있는 물고기, 그러한 물고기가 여러마리라면, 가장 왼쪽에 있는 물고기를 먹는다.
 *  아기 상어의 이동은 1초
 *  아기 상어는 자신의 크기와 같은 수의 물고기를 먹을 때 마다 크기가 1 증가.
 *  아기 상어가 몇 초 동안 엄마 상어에게 도움을 요청하지 않고 물고기를 잡아 먹을 수 있는지 구하는 프로그램
 *  
 *  크기 2~20 0 빈칸 123456 물고기 크기 9 아기상어 위치
 *  
 *  1. 공간의 크기를 입력 받는다.
 *  2. 공간 정보를 입력받으면서 
 *  3. 물고기들의 정보를 저장해둔다. 
 *  4. 먹을 수 있는 물고기를 찾는다
 *   4-1. 한마리라면 그 물고기를 먹으러 간다.
 *   4-2. 여러마리라면 가장 가까운 물고기를 고른다.
 *   4-3. 가장 가까운 물고기가 많다면 가장 위, 가장 왼쪽에 있는 물고기를 먹는다.
 *  5. 먹을 수 있는 물고기가 없을 때 까지 반복
 */

public class BOJ16236 {
	static BufferedReader br;
	static StringTokenizer st;

	static int size;
	static int[][] space;

	static class Fish {
		int row;
		int col;
		int size;

		public Fish(int row, int col, int size) {
			this.row = row;
			this.col = col;
			this.size = size;
		}
	}

	static class Shark {
		int row;
		int col;
		int size;
		int count;

		public Shark(int row, int col, int size) {
			this.row = row;
			this.col = col;
			this.size = size;
			this.count = 0;
		}

		public void eatFish(Fish fish) {
			count++;
			if (size == count) {
				size++;
				count = 0;
			}
			this.row = fish.row;
			this.col = fish.col;
			fish.size = 0;
			space[this.row][this.col] = 0;
		}
	}

	static class Position {
		int row;
		int col;
		int length;

		public Position(int row, int col, int length) {
			this.row = row;
			this.col = col;
			this.length = length;
		}
	}

	static Queue<Position> queue;

	static List<Fish> fishes;
	static Shark babyShark;
	static int moveSecond;

	public static void init() throws IOException {
		targetFish = null;
		moveSecond = 0;

		size = Integer.parseInt(br.readLine().trim());

		space = new int[size][size];
		fishes = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < size; ++rowIndex) {
			st = new StringTokenizer(br.readLine().trim());
			for (int colIndex = 0; colIndex < size; ++colIndex) {
				space[rowIndex][colIndex] = Integer.parseInt(st.nextToken());
				if (space[rowIndex][colIndex] == 9) {
					space[rowIndex][colIndex] = 0;
					babyShark = new Shark(rowIndex, colIndex, 2);
				} else if (space[rowIndex][colIndex] != 0) {
					fishes.add(new Fish(rowIndex, colIndex, space[rowIndex][colIndex]));
				}
			}
		}
	}

	static final int[] SHARK_DELTA_ROW = { 0, 0, 1, -1 };
	static final int[] SHARK_DELTA_COL = { 1, -1, 0, 0 };
	static boolean[][] visited;

	static class TargetFish {
		Fish fish;
		int length;

		public TargetFish(Fish fish, int length) {
			this.fish = fish;
			this.length = length;
		}
	}

	static List<TargetFish> targetFishes;
	static TargetFish targetFish;
	static int minDist;
	static int fishDist;

	public static void findEatableFish() {
		for (int fishIndex = 0; fishIndex < fishes.size(); ++fishIndex) {
			if (fishes.get(fishIndex).size == 0) {
				continue;
			}
			// 큰 물고기는 못잡아 먹는다
			if (fishes.get(fishIndex).size >= babyShark.size) {
				continue;
			}
			fishDist = 400;
			if (canEat(fishes.get(fishIndex))) {
				minDist = Math.min(minDist, fishDist);
				targetFishes.add(new TargetFish(fishes.get(fishIndex), fishDist));
			}
		}
		if (targetFishes.size() == 0) {
			// 먹을 수 있는 물고기가 없음
			targetFish = null;
		} else if (targetFishes.size() == 1) {
			// 한마리라면 그 물고기가 타겟
			targetFish = targetFishes.get(0);
		} else {
			int minFishDist = targetFishes.get(0).length;
			targetFish = targetFishes.get(0);
			for (int fishIndex = 1; fishIndex < targetFishes.size(); ++fishIndex) {
				if (minFishDist > targetFishes.get(fishIndex).length) {
					minFishDist = targetFishes.get(fishIndex).length;
					targetFish = targetFishes.get(fishIndex);
				} else if (minFishDist == targetFishes.get(fishIndex).length) {
					if (targetFishes.get(fishIndex).fish.row < targetFish.fish.row) {
						targetFish = targetFishes.get(fishIndex);
					} else if (targetFishes.get(fishIndex).fish.row == targetFish.fish.row) {
						if (targetFishes.get(fishIndex).fish.col < targetFish.fish.col) {
							targetFish = targetFishes.get(fishIndex);
						}
					}
				}
			}
		}
	}

	public static boolean canEat(Fish fish) {
		boolean canEat = false;
		visited = new boolean[size][size];

		queue = new ArrayDeque<>();
		queue.add(new Position(babyShark.row, babyShark.col, 0));
		visited[babyShark.row][babyShark.col] = true;

		while (!queue.isEmpty()) {
			Position position = queue.poll();
			int row = position.row;
			int col = position.col;
			int length = position.length;
			if (row == fish.row && col == fish.col) {
				canEat = true;
				fishDist = length;
				queue.clear();
				break;
			}

			for (int delta = 0; delta < SHARK_DELTA_ROW.length; ++delta) {
				int nextRow = row + SHARK_DELTA_ROW[delta];
				int nextCol = col + SHARK_DELTA_COL[delta];

				// 범위 체크
				if (nextRow < 0 || nextRow >= size || nextCol < 0 || nextCol >= size) {
					continue;
				}
				// 갈 수 있는 지 확인
				if (visited[nextRow][nextCol]) {
					continue;
				}
				if (space[nextRow][nextCol] > babyShark.size) {
					continue;
				}
				visited[nextRow][nextCol] = true;
				queue.add(new Position(nextRow, nextCol, length + 1));
			}
		}
		return canEat;
	}

	public static void move() {
		while (true) {
			targetFishes = new ArrayList<>();
			findEatableFish();
			if (targetFish == null) {
				return;
			} else {
				babyShark.eatFish(targetFish.fish);
				moveSecond += targetFish.length;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));

		init();
		move();
		System.out.println(moveSecond);
	}
}
