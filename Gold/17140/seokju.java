package BOJ;

import java.util.Arrays;
import java.util.Scanner;

// BOJ 17140 이차원 배열과 연산

/*
 * 크기가 3x3 배열 A 배열의 인덱스는 1부터 시작, 1초가 지날 때마다 배열에 연산이 적용
 * R 연산 : 배열 A의 모든 행에 대해서 정렬을 수행한다. 행의 개수 >= 열의 개수인 경우에 적용된다.
 * C 연산 : 배열 A의 모든 열에 대해서 정렬을 수행한다. 행의 개수 < 열의 개수인 경우에 적용된다.
 * 한 행 또는 열에 있는 수를 정렬하려면, 각각의 수가 몇 번 나왔는지 알아야 한다. 그 다음, 수의 등장 횟수가 커지는 순으로,
 * 그러한 것이 여러가지면 수가 커지는 순으로 정렬한다. 그 다음에는 배열 A에 정렬된 결과를 다시 넣어야 한다.
 * 정렬된 결과를 배열에 넣을 때는, 수와 등장 횟수를 모두 넣으며, 순서는 수가 먼저이다.
 * 배열에 다시 넣으면 행 또는 열의 크기가 달라질 수 있다.
 * 행 또는 열의 크기가 100을 넘어가는 경우에는 처음 100개를 제외한 나머지는 버린다.
 * 배열 A에 들어있는 수와 r,c,k가 주어졌을 때, A[r][c]에 들어있는 값이 k가 되기 위한 최소 시간을 구해보자.
 * 
 * 1. 시간이 100초 지날때까지
 *  1-1. 값이 k가 되었는 지 확인
 *  1-2. 행 >= 열이면 R연산
 *  1-3. 열 > 행이면 C연산
 */

public class BOJ17140 {

	static int checkRow;
	static int checkCol;
	static int checkNum;

	static int[][] arr;

	static int curRowLength = 3;
	static int curColLength = 3;

	static int elapsedTime;

	static class Count implements Comparable<Count> {
		int num;
		int count;

		public Count(int num, int count) {
			this.num = num;
			this.count = count;
		}

		@Override
		public int compareTo(Count other) {
			if (count > other.count) {
				return 1;
			} else if (count < other.count) {
				return -1;
			} else {
				return this.num - other.num;
			}
		}
	}

	public static void operatorR() {
		int colLength = 0;
		int maxColLength = 0;
		for (int row = 1; row <= curRowLength; ++row) {
			colLength = 0;
			Count[] count = new Count[101];
			for (int num = 0; num < 101; ++num) {
				count[num] = new Count(num, 0);
			}
			for (int col = 1; col <= curColLength; ++col) {
				count[arr[row][col]].count++;
			}
			Arrays.sort(count);
			int cnt = 1;
			for (int index = 0; index < count.length; ++index) {
				if (count[index].num == 0) {
					continue;
				}
				if (count[index].count == 0) {
					continue;
				}
				arr[row][cnt * 2 - 1] = count[index].num;
				arr[row][cnt * 2] = count[index].count;
				colLength = cnt * 2;
				cnt++;
				if (colLength == 100) {
					break;
				}
			}
			for(int index = colLength + 1; index <= 100; index++) {
				arr[row][index] = 0;
			}
			maxColLength = Math.max(maxColLength, colLength);
		}
		curColLength = maxColLength;
	}

	public static void operatorC() {
		int rowLength = 0;
		int maxRowLength = 0;
		for (int col = 1; col <= curColLength; ++col) {
			rowLength = 0;
			Count[] count = new Count[101];
			for (int num = 0; num < 101; ++num) {
				count[num] = new Count(num, 0);
			}
			for (int row = 1; row <= curRowLength; ++row) {
				count[arr[row][col]].count++;
			}
			Arrays.sort(count);
			int cnt = 1;
			for (int index = 0; index < count.length; ++index) {
				if (count[index].num == 0) {
					continue;
				}
				if (count[index].count == 0) {
					continue;
				}
				arr[cnt * 2 - 1][col] = count[index].num;
				arr[cnt * 2][col] = count[index].count;
				rowLength = cnt * 2;
				cnt++;
				if (rowLength == 100) {
					break;
				}
			}
			for(int index = rowLength + 1; index <= 100; index++) {
				arr[index][col] = 0;
			}
			maxRowLength = Math.max(maxRowLength, rowLength);
		}
		curRowLength = maxRowLength;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		checkRow = sc.nextInt();
		checkCol = sc.nextInt();
		checkNum = sc.nextInt();

		arr = new int[101][101];

		for (int row = 1; row <= curRowLength; ++row) {
			for (int col = 1; col <= curColLength; ++col) {
				arr[row][col] = sc.nextInt();
			}
		}
		// 1. 시간이 100초 지날때까지
		while (elapsedTime <= 100) {
			// 1-1. 값이 k가 되었는 지 확인
			if (checkRow <= curRowLength && checkCol <= curColLength) {
				if (arr[checkRow][checkCol] == checkNum) {
					break;
				}
			}

			if (curRowLength >= curColLength) {
				// 1-2. 행 >= 열이면 R연산
				operatorR();
			} else {
				// 1-3. 열 > 행이면 C연산
				operatorC();
			}
			elapsedTime++;
//			for (int row = 1; row <= curRowLength; ++row) {
//				for (int col = 1; col <= curColLength; ++col) {
//					System.out.print(arr[row][col] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println(elapsedTime);
		}
		System.out.println(elapsedTime > 100 ? -1 : elapsedTime);
	}

}
