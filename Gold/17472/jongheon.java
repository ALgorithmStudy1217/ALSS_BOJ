/*
= BOJ 17472. 다리 만들기 2

= 특이 사항
크루스칼

= 로직
1. 초기 세팅
    1-1. 지도의 가로, 세로 크기 입력
    1-2. 지도의 정보 입력
    1-3. 변수 초기화
2. 미개척 섬 찾기
    2-1. 미개척 섬 개척
3. 건설할 수 있는 다리 찾기
    3-1. 다리 만들기
4. 모든 섬을 연결하는 다리 건설
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static final int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; //상하좌우 배열
    static final int MAX_ISLAND_SIZE = 6, MIN_BRIDGE_LENGTH = 2;
    static final int OCEAN = 0, LAND = -1;
    static int mapRow, mapCol;
    static int[][] map;

    //union-find 알고리즘을 위한 변수
    public static int[] parents;
    public static int[] rank;

    static boolean[][] visited;
    static int islandSize;
    static List<Point> islandPositions; //섬인 좌표들의 리스트
    static PriorityQueue<Bridge> bridges; //건설 가능한 다리를 보관하는 우선순위 큐
    static int minBridgeCost; //최소 건설 비용

    //2. 미개척 섬 찾기
    public static void findIsland() {
        for (int row = 0; row < mapRow; row++) {
            for (int col = 0; col < mapCol; col++) {
                if (visited[row][col])
                    continue;

                if (map[row][col] == OCEAN)
                    continue;

                //2-1. 미개척 섬 개척
                islandSize++;
                setIsland(new Point(row, col));
            }
        }
    }

    //2-1. 미개척 섬 개척
    public static void setIsland(Point startPos) {
        Deque<Point> queue = new ArrayDeque<>();

        visited[startPos.row][startPos.col] = true;
        queue.add(startPos);

        while (!queue.isEmpty()) {
            Point cur = queue.poll();

            //미개척 섬 개척하기
            map[cur.row][cur.col] = islandSize;
            islandPositions.add(cur);

            //주변 방문하기
            for (int[] delta : deltas) {
                int nr = cur.row + delta[0];
                int nc = cur.col + delta[1];

                if (!canGo(nr, nc))
                    continue;

                if (visited[nr][nc])
                    continue;

                if (map[nr][nc] == OCEAN)
                    continue;

                visited[nr][nc] = true;
                queue.add(new Point(nr, nc));
            }
        }
    }

    //3. 건설할 수 있는 다리 찾기
    public static void findBridge(int positionIdx) {
        //모든 좌표를 탐색했다면, 탐색 종료
        if (positionIdx == islandPositions.size()) {
            return;
        }

        Point cur = islandPositions.get(positionIdx);
        int islandIdx = map[cur.row][cur.col];

        //사방 다리 건설 시도
        for (int[] delta : deltas) {
            //3-1. 다리 만들기
            makeBridge(cur, islandIdx, delta);
        }

        //다음 좌표 탐색
        findBridge(positionIdx + 1);
    }

    //3-1. 다리 만들기
    public static void makeBridge(Point cur, int islandIdx, int[] delta) {
        int nr = cur.row + delta[0];
        int nc = cur.col + delta[1];

        for (int bridgeLength = 0; canGo(nr, nc); nr += delta[0], nc += delta[1], bridgeLength++) {
            if (map[nr][nc] == islandIdx)
                return;

            if (map[nr][nc] != OCEAN) {
                if (bridgeLength >= MIN_BRIDGE_LENGTH)
                    bridges.add(new Bridge(islandIdx, map[nr][nc], bridgeLength));
                return;
            }
        }
    }

    //4. 모든 섬을 연결하는 다리 건설
    public static void chooseBridge(int selectIdx, int visit, int bridgeCost) {
        int cnt = 0;

        while (!bridges.isEmpty()) {
            Bridge cur = bridges.poll();

            if (union(cur.start, cur.end)) {
                minBridgeCost += cur.cost;

                if (++cnt == islandSize - 1)
                    break;
            }
        }

        if (cnt != islandSize - 1)
            minBridgeCost = -1;
    }

    public static void main(String[] args) throws IOException {
        //1. 초기 세팅
        init();

        //2. 미개척 섬 찾기
        findIsland();

        //3. 건설할 수 있는 다리 찾기
        findBridge(0);

        //4. 모든 섬을 연결하는 다리 건설
        chooseBridge(0, 0, 0);

        System.out.println(minBridgeCost);
    }

    public static class Point {
        int row, col;

        public Point(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (!(o instanceof Point))
                return false;

            Point other = (Point) o;

            return row == other.row && col == other.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    public static class Bridge implements Comparable<Bridge> {
        int start;
        int end;
        int cost;

        public Bridge(int start, int end, int cost) {
            this.start = start;
            this.end = end;
            this.cost = cost;
        }

        @Override
        public int compareTo(Bridge o) {
            return Integer.compare(this.cost, o.cost);
        }
    }

    public static boolean canGo(int row, int col) {
        return row >= 0 && row < mapRow && col >= 0 && col < mapCol;
    }

    public static boolean union(int island1, int island2) {
        int parent1 = find(island1);
        int parent2 = find(island2);

        if (Objects.equals(parent1, parent2))
            return false;

        if (rank[parent1] > rank[parent2]) {
            parents[parent2] = parent1;
            return true;
        }

        if (rank[parent1] == rank[parent2]) {
            rank[parent2]++;
        }

        parents[parent1] = parent2;
        return true;
    }

    public static int find(int island) {
        if (parents[island] == 0)
            return island;

        return parents[island] = find(parents[island]);
    }

    public static void init() throws IOException {
        //1-1. 지도의 크기 입력
        st = new StringTokenizer(input.readLine().trim());
        mapRow = Integer.parseInt(st.nextToken());
        mapCol = Integer.parseInt(st.nextToken());

        //1-2. 지도의 정보 입력
        map = new int[mapRow][mapCol];
        for (int row = 0; row < mapRow; row++) {
            st = new StringTokenizer(input.readLine().trim());
            for (int col = 0; col < mapCol; col++) {
                map[row][col] = Integer.parseInt(st.nextToken());

                if (map[row][col] != OCEAN)
                    map[row][col] = LAND;
            }
        }

        //1-3. 변수 초기화
        parents = new int[MAX_ISLAND_SIZE + 1];
        rank = new int[MAX_ISLAND_SIZE + 1];

        visited = new boolean[mapRow][mapCol];
        islandPositions = new ArrayList<>();
        bridges = new PriorityQueue<>();
    }
}
