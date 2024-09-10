import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

/**
 * BOJ 1600
 *
 * 3차원 방문 배열에 0~canMoveCount까지 체크
 * Queue에 canMoveCount를 담아서 계속 갱신
 */
public class Main { 

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static StringBuilder sb = new StringBuilder();

    static int canMoveCount;

    static int rowSize, colSize;
    static int[][] board;

    static boolean[][][] status;

    static final int[] DELTA_ROW = {-1, 0, 1,0};
    static final int[] DELTA_COLUMN = {0,1,0,-1};
    static final int[] DELTA_DIAG_ROW = {-1,-1,-1,1,1,1,1,-1};
    static final int[] DELTA_DIAG_COLUMN = {-1,1,1,1,1,-1,-1,-1};

    static int result;

    public static void main(String[] args) throws IOException {

        init();

        solve(new Point(1, 1, 0, 0));

        if(result == Integer.MAX_VALUE) result = -1;

        sb.append(result);

        System.out.println(sb);

    }

    static void init() throws IOException{
        canMoveCount = Integer.parseInt(br.readLine());
        result = Integer.MAX_VALUE;

        st = new StringTokenizer(br.readLine().trim(), " ");

        colSize = Integer.parseInt(st.nextToken());
        rowSize = Integer.parseInt(st.nextToken());

        board = new int[rowSize+1][colSize+1];
        status = new boolean[rowSize+1][colSize+1][canMoveCount+1];

        for(int row=1; row <= rowSize; row++) {
            st = new StringTokenizer(br.readLine().trim(), " ");
            for(int col=1; col <= colSize; col++) {
                board[row][col] = Integer.parseInt(st.nextToken());
            }
        }
    }

    private static void solve(Point point) throws IOException {
        Queue<Point> queue = new LinkedList<>();

        queue.add(point);
        status[point.row][point.col][0] = true;

        while(!queue.isEmpty()){
            Point cur = queue.poll();

            if(cur.row == rowSize && cur.col == colSize) {
                result = Math.min(result, cur.depth);
                break;
            }

            for(int delta=0; delta < 4; delta++){
                int dRow = cur.row;
                int dCol = cur.col;

                dRow += DELTA_ROW[delta];
                dCol += DELTA_COLUMN[delta];

                if(!checkPoint(dRow, dCol)) continue;

                // 이미 순수 BFS를 탐색한 경우 무시
                if(status[dRow][dCol][cur.horseMoveCount]) continue;
                
                status[dRow][dCol][cur.horseMoveCount] = true;
                queue.add(new Point(dRow, dCol, cur.depth+1, cur.horseMoveCount));
            }

            // 이미 말의 움직임을 모두 사용했을 경우 무시
            if(cur.horseMoveCount == canMoveCount) continue;
            
            for(int delta=0; delta < 8; delta++){
                int dRow = cur.row;
                int dCol = cur.col;

                dRow += DELTA_ROW[delta/2] + DELTA_DIAG_ROW[delta];
                dCol += DELTA_COLUMN[delta/2] + DELTA_DIAG_COLUMN[delta];

                if(!checkPoint(dRow, dCol)) continue;

                if(status[dRow][dCol][cur.horseMoveCount+1]) continue;

                status[dRow][dCol][cur.horseMoveCount+1] = true;

                queue.add(new Point(dRow, dCol, cur.depth+1, cur.horseMoveCount+1));
            }
        }
    }

    static boolean checkPoint(int row, int col) {
        if(row < 1 || row > rowSize || col < 1 || col > colSize) return false;
        if(board[row][col] == 1) return false;
        return true;
    }

    static class Point{
        int row;
        int col;
        int depth;
        int horseMoveCount;

        public Point(int row, int col, int depth, int horseMoveCount){
            this.row = row;
            this.col = col;
            this.depth = depth;
            this.horseMoveCount = horseMoveCount;
        }
    }

}
