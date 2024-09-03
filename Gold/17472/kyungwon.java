import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// 시험장 풀이
// 1. 지도의 크기를 입력받는다
// 2. 지도의 정보를 입력받는다
// 3. bfs를 돌며 섬을 다 찾는다(모든 좌표 저장하기)
// 4. dfs로 섬의 좌표에서 다른 섬이 나올때까지 진행한다
// 4-1. 자기 자신일때 return; 영역 밖으로 나가면 return;
// 4-2. 다음칸이 바다(map[x][y] == 0) 일때 다리를 확장시킨다
// 4-3. 다른 섬을 만나면 다리 길이를 비교한다
//        자기 자신을 제외한 모든 섬의 좌표값을 비교하여 어떤 섬과 연결되는 것인지 확인한다
// 4-4. 가장 짧은 다리 값을 업데이트한다
// 5. 다리가 이어지면 하나의 섬으로 만든다
// 5-1. island에서 이어진 2개의 섬을 삭제하고 새로운 섬을 추가한다 -> island에는 이어져있지 않은 n개의 섬만 남긴다

public class Main {

    static BufferedReader bf;
    static StringTokenizer st;
    static StringBuilder sb;
    static int map[][];
    static int ROW;
    static int COL;
    static int totalBridge;
    static int minBridge;
    static class Pair{
        int x;
        int y;
        public Pair(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }
    }
    static int[] dx = {0, 1, 0, -1};
    static int[] dy = {1, 0, -1, 0};
    static List<List<Pair>> island;
    static int tempIndex;


    public static void dfs(int x, int y, int length, int dir, int islandNum) {
        // 4. dfs로 섬의 좌표에서 다른 섬이 나올때까지 진행한다
        // 4-1. 자기 자신일때 return; 영역 밖으로 나가면 return;
        // 4-2. 다음칸이 바다(map[x][y] == 0 일때 다리를 확장시킨다
        // 4-3. 다른 섬을 만나면 다리 길이를 비교한다
        //         자기 자신을 제외한 모든 섬의 좌표값을 비교한다
        // 4-4. 가장 짧은 다리 값을 업데이트한다

        //map 바깥 영역
        if(x < 0 || x >= ROW || y<0 || y>= COL) return;

        //섬을 만나면
        if(map[x][y] == 1) {

            if(length ==1) return;
            // 자기 자신이라면
            for(int idx = 0; idx < island.get(islandNum).size(); idx++) {
                if(x == island.get(islandNum).get(idx).x &&y == island.get(islandNum).get(idx).y)
                    return;
            }
            // 다른 섬이라면
            if(minBridge > length) {

                //어떤 섬과 만났는지 확인해야함
                for(int idx = 0; idx < island.size(); idx++) {
                    boolean isConnect = false;
                    if(idx != islandNum) {
                        for(int curIdx = 0; curIdx < island.get(idx).size(); curIdx++) {
                            int curX = island.get(idx).get(curIdx).x;
                            int curY = island.get(idx).get(curIdx).y;

                            if (x == curX && y == curY) {
                                tempIndex = idx;
                                isConnect = true;
                            }
                        }
                        if(isConnect)break;
                    }
                }
                minBridge = length;
            }
            return;
        }

        if(map[x][y] == 0) {
            //방향대로 가기 1: 오른쪽, 2: 아래 3: 왼쪽, 4: 위
            if(dir == 1) {
                dfs(x, y+1, length+1, 1, islandNum);

            } else if(dir == 2) {
                dfs(x+1, y, length+1, 2, islandNum);
            } else if(dir ==3) {
                dfs(x, y-1, length+1, 3, islandNum);

            } else {
                dfs(x-1, y, length+1, 4, islandNum);
            }
        }

    }

    public static void connect() {
        // 4. dfs로 섬의 좌표에서 다른 섬이 나올때까지 진행한다
        for(int idx = 0; idx < island.size(); idx++) {
            minBridge = 987654321;
            tempIndex = 0;
            for(int curIdx = 0; curIdx < island.get(idx).size(); curIdx++) {
                int x = island.get(idx).get(curIdx).x;
                int y = island.get(idx).get(curIdx).y;
                //하나의 섬에서 이을 수 있는 가장 짧은 다리 길이
                dfs(x, y+1, 0, 1, idx);
                dfs(x+1, y, 0, 2, idx);
                dfs(x, y-1, 0, 3, idx);
                dfs(x-1, y, 0, 4, idx);

            }

            // 5. 다리가 이어지면 하나의 섬으로 만든다
            if(minBridge < 98765431) {
                // 5-1. island에서 이어진 2개의 섬을 삭제하고 새로운 섬을 추가한다 -> island에는 이어져있지 않은 n개의 섬만 남긴다
                totalBridge += minBridge;

                List<Pair> newIsland = new ArrayList<>();
                newIsland.addAll(island.get(idx));
                newIsland.addAll(island.get(tempIndex));
                island.remove(idx);

                if(idx < tempIndex) {
                    island.remove(tempIndex-1);
                } else {island.remove(tempIndex);}
                island.add(newIsland);
                idx = 0;
            } else return;
        }
    }

    public static void bfs() {
        ArrayDeque<Pair> que = new ArrayDeque<>();
        int visited[][] = new int[ROW][COL];
        int index = 0;

        for(int row = 0; row<ROW; row++) {
            for(int col = 0; col<COL; col++) {
                if(visited[row][col] == 0 && map[row][col] == 1) {
                    List<Pair> curIsland = new ArrayList<>();
                    visited[row][col]= 1;
                    que.push(new Pair(row, col));

                    while(!que.isEmpty()) {
                        Pair cur = que.removeFirst();
                        curIsland.add(cur);

                        for(int dir = 0; dir < 4; dir++) {
                            int nx = cur.x + dx[dir];
                            int ny = cur.y + dy[dir];

                            if(nx < 0 || nx >= ROW || ny<0 || ny>= COL) continue;
                            if(visited[nx][ny] == 1 || map[nx][ny] == 0) continue;

                            Pair next = new Pair(nx, ny);
                            que.push(next);
                            visited[nx][ny] = 1;

                        }
                    }
                    island.add(index, curIsland);
                    index++;
                }
            }
        }

    }

    public static void main(String args[]) throws NumberFormatException, IOException {
        bf = new BufferedReader(new InputStreamReader(System.in));

//        int Test = Integer.parseInt(bf.readLine().trim());
//
//        for(int tcase = 1; tcase <=Test; tcase++) {

            //테스트케이스마다 초기화
            totalBridge = 0;
            minBridge = 987654321;
            island = new ArrayList<>();


            // 1. 지도의 크기를 입력받는다
            st = new StringTokenizer(bf.readLine().trim());
            ROW = Integer.parseInt(st.nextToken());
            COL = Integer.parseInt(st.nextToken());
            // 2. 지도의 정보를 입력받는다
            map = new int[ROW][COL];
            for(int row = 0; row<ROW; row++) {
                st = new StringTokenizer(bf.readLine().trim());
                for(int col = 0; col<COL; col++) {
                    map[row][col] = Integer.parseInt(st.nextToken());
                }
            }

            // 3. bfs를 돌며 섬을 다 찾는다(모든 좌표 저장하기)
            bfs();

            connect();
            if(island.size() == 1) System.out.println(totalBridge);
            else System.out.println("-1");
//        }
    }
}
