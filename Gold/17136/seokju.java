package BOJ;

import java.util.Scanner;

// BOJ 17136 색종이 붙이기

/*
 * 1x1 2x2 3x3 4x4 5x5 5종류의 색종이 5개씩
 * 색종이를 크기가 10x10인 종이 위에 붙이려고 한다. 각각의 칸에는 0 또는 1이 적혀 있다.
 * 1이 적힌 칸은 모두 색종이로 덮여져야 한다. 색종이를 붙일 때는 종이의 경계 밖으로 나가서는 안되고, 겹쳐도 안 된다.
 * 또 칸의 경계와 일치하게 붙여야 한다. 0이 적힌 칸에는 색종이가 있으면 안된다.
 * 종이가 주어졌을 때, 1이 적힌 모든 칸을 붙이는데 필요한 색종이의 최소 개수를 구해보자
 * 
 * 1. 10x10 색종이 정보 입력
 * 2. 좌상단부터 탐색한다
 * 3. 1을 만난다면
 *  3-1. 1x1 ~ 5x5를 붙이는 경우를 모두 탐색한다.
 *  3-2. 가능한 모든 경우를 붙여보고 다음 색종이 붙일 곳을 탐색해본다.
 *  3-3. 실패했다면 떼준다..
 */

public class BOJ17136 {
	static Scanner sc;

	static final int SIZE = 10;
	static int[][] coloredPaper;

	static final int CAN_ATTACH = 1;
	static final int NOT_ATTACH = 0;

	static int[] usedPaper;

	static int minUsedPaper;

	public static void init() {
		coloredPaper = new int[SIZE][SIZE];

		for (int row = 0; row < SIZE; ++row) {
			for (int col = 0; col < SIZE; ++col) {
				coloredPaper[row][col] = sc.nextInt();
			}
		}

		usedPaper = new int[6];
		minUsedPaper = -1;
	}

	public static void attachPaper(int row, int col) {
		if (col == SIZE) {
			col = 0;
			row++;
		}
		// 모두 돌았으면 계산
		if (row == SIZE) {
			int paper = 0;
			for (int index = 1; index <= 5; ++index) {
				paper += usedPaper[index];
			}
			if (minUsedPaper == -1) {
				minUsedPaper = paper;
			} else {
				minUsedPaper = Math.min(minUsedPaper, paper);
			}
			return;
		}

		for (int index = 1; index <= 5; ++index) {
			if (usedPaper[index] > 5) {
				return;
			}
		}

		// 3. 1을 만난다면
		if (coloredPaper[row][col] == NOT_ATTACH) {
			attachPaper(row, col + 1);
			return;
		}

		// 3-1. 1x1 ~ 5x5를 붙이는 경우를 모두 탐색한다.
		for (int size = 5; size >= 1; --size) {
			if (canAttach(row, col, size)) {
				fillPaper(row, col, size);
				usedPaper[size]++;
				// 3-2. 가능한 모든 경우를 붙여보고 다음 색종이 붙일 곳을 탐색해본다.
				attachPaper(row, col + size);
				// 3-3. 실패했다면 떼준다..
				usedPaper[size]--;
				resetPaper(row, col, size);
			}
		}

	}

	public static void fillPaper(int startRow, int startCol, int size) {
		for (int row = startRow; row < startRow + size; ++row) {
			for (int col = startCol; col < startCol + size; ++col) {
				coloredPaper[row][col] = NOT_ATTACH;
			}
		}
	}

	public static void resetPaper(int startRow, int startCol, int size) {
		for (int row = startRow; row < startRow + size; ++row) {
			for (int col = startCol; col < startCol + size; ++col) {
				coloredPaper[row][col] = CAN_ATTACH;
			}
		}
	}

	public static boolean canAttach(int startRow, int startCol, int size) {
		if (startRow + size - 1 >= SIZE || startCol + size - 1 >= SIZE) {
			return false;
		}
		for (int row = startRow; row < startRow + size; ++row) {
			for (int col = startCol; col < startCol + size; ++col) {
				if (coloredPaper[row][col] != CAN_ATTACH) {
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
		sc = new Scanner(System.in);
		init();
		// 2. 좌상단부터 탐색한다
		attachPaper(0, 0);
		System.out.println(minUsedPaper);
	}
}
