package BOJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringTokenizer;

// BOJ 3665 최종 순위

/*

n개의 1번~n번 팀
올해 최종 순위 발표 x 작년 순위 존재
대신 상대적인 순위가 바뀐 팀만 발표

1. 총 테스트 케이스 입력
2. 팀의 수 n 입력 (2~500)
3. n개의 정수를 포함하고 있는 한 줄  1등이 가장 성적이 좋은 팀
4. 상대적인 등수가 바뀐 쌍의 수 m 0~25000
5. 두 정수 a,b를 포함하는 m줄 같은 쌍이 여러 번 발표되는 경우는 없다.

1. N개의 팀의 인접행렬을 구한다. 유향 그래프로 바로 앞 순위 팀이 뒷 순위 팀을 가리킨다.
2. 상대적인 등수가 바뀐 쌍을 받으면 해당 행 렬을 바꿔준다.
3. 진입 차수 배열을 만들어준다.
4. 진입 차수가 0인 팀들을 큐에 모두 넣는다.
5. 큐가 빌 때까지
	5-1. 큐에서 팀을 꺼내어 해당 팀에 인접한 팀들의 진입차수를 1 제거한다
	5-2. 제거 후 진입차수가 0이 된 팀들을 큐에 넣는다.
6. 진입차수가 0이 아닌 팀이 있다면 IMPOSSIBLE
 */

public class BOJ3665 {
	static BufferedReader br;
	static StringTokenizer st;
	static StringBuilder sb;

	static int teamNum;
	static int[] rank;
	static int pairNum;

	static int[][] adjTeamMatrix;
	static int[] indegreeArray;

	public static void init() throws IOException {
		// 2. 팀의 수 n 입력 (2~500)
		teamNum = Integer.parseInt(br.readLine().trim());
		rank = new int[teamNum];

		// 3. n개의 정수를 포함하고 있는 한 줄 1등이 가장 성적이 좋은 팀
		st = new StringTokenizer(br.readLine().trim());
		for (int index = 0; index < teamNum; ++index) {
			rank[index] = Integer.parseInt(st.nextToken());
		}

		// 1. N개의 팀의 인접행렬을 구한다. 유향 그래프로 바로 앞 순위 팀이 뒷 순위 팀을 가리킨다.
		adjTeamMatrix = new int[teamNum + 1][teamNum + 1];
		for (int winTeamIndex = 0; winTeamIndex < teamNum - 1; ++winTeamIndex) {
			for (int loseTeamIndex = winTeamIndex + 1; loseTeamIndex < teamNum; ++loseTeamIndex) {
				adjTeamMatrix[rank[winTeamIndex]][rank[loseTeamIndex]] = 1;
			}
		}

		// 4. 상대적인 등수가 바뀐 쌍의 수 m 0~25000
		pairNum = Integer.parseInt(br.readLine().trim());

		// 5. 두 정수 a,b를 포함하는 m줄 같은 쌍이 여러 번 발표되는 경우는 없다.
		for (int pair = 0; pair < pairNum; ++pair) {
			st = new StringTokenizer(br.readLine().trim());
			int team1 = Integer.parseInt(st.nextToken());
			int team2 = Integer.parseInt(st.nextToken());
			// 2. 상대적인 등수가 바뀐 쌍을 받으면 해당 행 렬을 바꿔준다.
			if (adjTeamMatrix[team1][team2] == 1) {
				adjTeamMatrix[team1][team2] = 0;
				adjTeamMatrix[team2][team1] = 1;
			} else {
				adjTeamMatrix[team1][team2] = 1;
				adjTeamMatrix[team2][team1] = 0;
			}
		}

		// 3. 진입 차수 배열을 만들어준다.
		indegreeArray = new int[teamNum + 1];
		for (int team1 = 1; team1 <= teamNum; ++team1) {
			for (int team2 = 1; team2 <= teamNum; ++team2) {
				if (adjTeamMatrix[team1][team2] == 1) {
					indegreeArray[team2]++;
				}
			}
		}
	}

	public static void rankSort() {
		// 4. 진입 차수가 0인 팀들을 큐에 모두 넣는다.
		Queue<Integer> queue = new ArrayDeque<>();
		for (int team = 1; team <= teamNum; ++team) {
			if (indegreeArray[team] == 0) {
				indegreeArray[team] = -1;
				queue.offer(team);
			}
		}

		// 5. 큐가 빌 때까지
		while (!queue.isEmpty()) {
			// 5-1. 큐에서 팀을 꺼내어 해당 팀에 인접한 팀들의 진입차수를 1 제거한다
			int curTeam = queue.poll();
			sb.append(curTeam).append(" ");
			
			for (int team = 1; team <= teamNum; ++team) {
				if (adjTeamMatrix[curTeam][team] == 1) {
					adjTeamMatrix[curTeam][team] = 0;
					indegreeArray[team]--;
				}
			}
			
			// 5-2. 제거 후 진입차수가 0이 된 팀들을 큐에 넣는다.
			for (int team = 1; team <= teamNum; ++team) {
				if (indegreeArray[team] == 0) {
					indegreeArray[team] = -1;
					queue.offer(team);
				}
			}
		}
		
		// 6. 진입차수가 0이 아닌 팀이 있다면 IMPOSSIBLE
		for (int team = 1; team <= teamNum; ++team) {
			if (indegreeArray[team] != -1) {
				System.out.println("IMPOSSIBLE");
				return;
			}
		}
		System.out.println(sb);
	}

	public static void main(String[] args) throws IOException {
		br = new BufferedReader(new InputStreamReader(System.in));
		
		// 1. 총 테스트 케이스 입력
		int testCase = Integer.parseInt(br.readLine().trim());

		for (int tc = 1; tc <= testCase; ++tc) {
			sb = new StringBuilder();
			init();
			rankSort();
		}
	}
}
