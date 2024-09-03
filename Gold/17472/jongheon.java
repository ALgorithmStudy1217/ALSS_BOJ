/*
= BOJ 17472. 다리 만들기 2

1. 지도의 가로, 세로 크기 입력
2. 지도의 정보 입력
3. 변수 초기화
4. 미개척 섬 찾기
	4-1. 미개척 섬 개척
5. 건설할 수 있는 다리 찾기
	5-1. 다리 만들기
6. 건설 가능한 다리 중 (섬의 개수 - 1)개 뽑아서 최소 건설 비용 구하기
7. 모든 섬을 연결할 수 없는 경우 -1 출력
 */

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
	static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	static StringBuilder output = new StringBuilder();
	static StringTokenizer st;
	static final int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; //상하좌우 배열
	static final int MIN_BRIDGE_LENGTH = 2;
	static final int OCEAN = 0, LAND = -1;
	static int testCase;
	static int mapRow, mapCol;
	static int[][] map;
	static int islandSize; //섬의 개수
	static List<Position> islandPositions; //섬인 좌표들의 리스트
	static int[][] tempBridges; //건설할 수 있는 다리 배열 [시작 섬][끝 섬] = 다리 길이
	static List<Bridge> bridges; //건설 가능한 다리만 tempBridges에서 뽑아낸 리스트
	static Bridge[] selectBridge; //다리 건설할 때 선택한 다리 배열
	static int minBridgeCost; //최소 건설 비용
	
	public static class Position {
		int row;
		int col;
		
		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
	
	public static class Bridge {
		int start;
		int end;
		int cost;
		
		public Bridge(int start, int end, int cost) {
			this.start = start;
			this.end = end;
			this.cost = cost;
		}
	}
	
	public static void init() throws IOException {
		//1. 지도의 크기 입력
		st = new StringTokenizer(input.readLine().trim());
		mapRow = Integer.parseInt(st.nextToken());
		mapCol = Integer.parseInt(st.nextToken());
		
		//2. 지도의 정보 입력
		map = new int[mapRow][mapCol];
		for (int row = 0; row < mapRow; row++) {
			st = new StringTokenizer(input.readLine().trim());
			for (int col = 0; col < mapCol; col++) {
				map[row][col] = Integer.parseInt(st.nextToken());
				
				if (map[row][col] != OCEAN) {
					map[row][col] = LAND;
				}
			}
		}
		
		//3. 변수 초기화
		islandSize = 0;
		islandPositions = new ArrayList<>();
		minBridgeCost = Integer.MAX_VALUE;
	}
	
	//4. 미개척 섬 찾기
	public static void findIsland() {
		Deque<Position> queue = new ArrayDeque<>();
		boolean[][] visit = new boolean[mapRow][mapCol];
		
		queue.add(new Position(0, 0));
		
		while (!queue.isEmpty()) {
			Position cur = queue.poll();
			
			//방문처리
			if (visit[cur.row][cur.col])
				continue;
			visit[cur.row][cur.col] = true;
			
			//미개척 섬을 찾았다면 개척하기
			if (map[cur.row][cur.col] == LAND) {
				islandSize++;
				setIsland(cur);
			}
			
			//주변 방문하기
			for (int[] delta : deltas) {
				int nr = cur.row + delta[0];
				int nc = cur.col + delta[1];
				
				if (canGo(nr, nc))
					queue.add(new Position(nr, nc));
			}
		}
	}
	
	//4-1. 미개척 섬 개척
	public static void setIsland(Position startPos) {
		Deque<Position> queue = new ArrayDeque<>();
		queue.add(startPos);
		boolean[][] visit = new boolean[mapRow][mapCol];
		
		while (!queue.isEmpty()) {
			Position cur = queue.poll();

			//방문처리
			if (visit[cur.row][cur.col])
				continue;
			visit[cur.row][cur.col] = true;
			
			//바다라면 탐색 종료
			if (map[cur.row][cur.col] == OCEAN)
				continue;
			
			//미개척 섬이라면 개척하기
			if (map[cur.row][cur.col] == LAND) {
				map[cur.row][cur.col] = islandSize;
				islandPositions.add(cur);
			}
			
			//주변 방문하기
			for (int[] delta : deltas) {
				int nr = cur.row + delta[0];
				int nc = cur.col + delta[1];
				
				if (canGo(nr, nc))
					queue.add(new Position(nr, nc));
			}
		}
	}
	
	//5. 건설할 수 있는 다리 찾기
	public static void findBridge(int positionIdx) {
		//모든 좌표를 탐색했다면, 탐색 종료
		if (positionIdx == islandPositions.size()) {
			return;
		}
		
		Position cur = islandPositions.get(positionIdx);
		int islandIdx = map[cur.row][cur.col];
		
		//사방 다리 건설 시도
		for (int[] delta : deltas) {
			int[] bridgeInfo = makeBridge(cur, islandIdx, delta);
			
			if (bridgeInfo[0] != OCEAN && bridgeInfo[1] >= MIN_BRIDGE_LENGTH) {
				int minIslandIdx = Math.min(islandIdx, bridgeInfo[0]);
				int maxIslandIdx = Math.max(islandIdx, bridgeInfo[0]);
				tempBridges[minIslandIdx][maxIslandIdx] = Math.min(tempBridges[minIslandIdx][maxIslandIdx], bridgeInfo[1]);
			}
			
		}
		
		//다음 좌표 탐색
		findBridge(positionIdx + 1);
	}
	
	//5-1. 다리 만들기
	public static int[] makeBridge(Position cur, int islandIdx, int[] delta) {
		int[] bridgeInfo = new int[2]; //연결된 섬의 번호, 다리의 길이
		
		int nr = cur.row + delta[0];
		int nc = cur.col + delta[1];
		
		while (canGo(nr, nc)) {
			if (map[nr][nc] == islandIdx) {
				break;
			}
			
			if (map[nr][nc] != OCEAN) {
				bridgeInfo[0] = map[nr][nc];
				break;
			}
			
			bridgeInfo[1]++;
			nr += delta[0];
			nc += delta[1];
		}
		
		return bridgeInfo;
	}
	
	//6. 건설 가능한 다리 중 (섬의 개수 - 1)개 뽑아서 최소 건설 비용 구하기
	public static void chooseBridge(int selectIdx, int visit, int bridgeCost) {
		//선택한 다리가 (섬의 개수 - 1)일 경우 탐색 종료
		if (selectIdx == islandSize - 1) {
			//해당 섬이 모두 연결된다면, 최소 건설 비용 업데이트
			if (isAllConnected())
				minBridgeCost = Math.min(bridgeCost, minBridgeCost);
			return;
		}
		
		for (int idx = 0; idx < bridges.size(); idx++) {
			if ((visit & 1 << idx) != 0)
				continue;
			
			selectBridge[selectIdx] = bridges.get(idx);
			chooseBridge(selectIdx + 1, visit | 1 << idx, bridgeCost + bridges.get(idx).cost);
		}
	}
	
	public static boolean isAllConnected() {
		int connectedBit = 0b1;
		
		//첫번째 다리 건설
		connectedBit |= 1 << selectBridge[0].start;
		connectedBit |= 1 << selectBridge[0].end;
		
		//나머지 다리 건설
		for (int idx = 1; idx < selectBridge.length; idx++) {
			if ((connectedBit & 1 << selectBridge[idx].start) == 0 && (connectedBit & 1 << selectBridge[idx].end) == 0)
				return false;
			
			if ((connectedBit & 1 << selectBridge[idx].start) == 0) {
				connectedBit |= 1 << selectBridge[idx].start;
			}

			if ((connectedBit & 1 << selectBridge[idx].end) == 0) {
				connectedBit |= 1 << selectBridge[idx].end;
			}
		}
		
		//모든 섬이 연결되지 않았다면 false 반환
		if (connectedBit != (1 << (islandSize + 1)) - 1)
			return false;
		
		return true;
	}
	
	public static void main(String[] args) throws IOException {
		init();
		
		//4. 미개척 섬 찾기
		findIsland();
		
		//5. 건설할 수 있는 다리 찾기
		tempBridges = new int[islandSize + 1][islandSize + 1];
		for (int[] bridge : tempBridges) {
			Arrays.fill(bridge, Integer.MAX_VALUE);
		}
		findBridge(0);
		bridges = new ArrayList<>();
		for (int row = 0; row <islandSize + 1; row++) {
			for (int col = 0; col <islandSize + 1; col++) {
				if (tempBridges[row][col] != Integer.MAX_VALUE)
					bridges.add(new Bridge(row, col, tempBridges[row][col]));
			}
		}
		
		//6. 건설 가능한 다리 중 섬의 개수 - 1 개 뽑아서 최소 건설 비용 구하기
		selectBridge = new Bridge[islandSize - 1];
		chooseBridge(0, 0, 0);
		
		//7. 모든 섬을 연결할 수 없는 경우 -1 출력
		if (minBridgeCost == Integer.MAX_VALUE)
			minBridgeCost = -1;
		
		System.out.println(minBridgeCost);
	}
	
	public static boolean canGo(int row, int col) {
		return row >= 0 && row < mapRow && col >= 0 && col < mapCol;
	}
}
