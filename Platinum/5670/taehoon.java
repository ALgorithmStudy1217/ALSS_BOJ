import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/*
 * 첫 글자를 입력할 시 사전에 저장된 단어들을 기반으로
 * 자동으로 뒷 글자를 추천
 * 예를 들어, hello, hell이 저장되어있는 경우
 * h 입력 시, ell까지 자동으로 입력 hell -> 총 2번
 * 그 뒤, hello를 찾기 위해서는 o를 직접 입력 -> 총 3번
 * 저장된 각 단어를 입력하기 위해 버튼을 눌러야하는 횟수의 평균 (소수점 둘째자리에서 반올림)
 * 
 * 1. 입력받은 문자열을 각 문자열을 Node를 통해 이어주고 끝을 표시
 * 	1-1. 글자들은 set을 통해 저장
 * 2. set에 저장된 글자를 탐색
 * 	2-1. 첫 글자 카운트
 * 	2-2. 자식노드가 여러 개 존재하는 경우 또는 자식노드 중 끝이 존재하는 경우 카운트
 * 3. 모든 카운트의 평균 출력 
 * */

public class Main {

	static Scanner sc = new Scanner(System.in);
	static int wordCnt;
	static Node rootNode;

	static Set<String> word;

	static class Node {
		Map<Character, Node> childNode = new HashMap<Character, Node>();
		boolean endPoint;
	}

	// 1. 입력받은 문자열을 각 문자열을 Node를 통해 이어주고 끝을 표시
	static void input() {
		// root노드 생성
		rootNode = new Node();
		word = new HashSet<>();

		// 1-1. 글자들은 set을 통해 저장
		for (int cnt = 0; cnt < wordCnt; cnt++) {
			String inputWord = sc.next();

			Node node = rootNode;

			word.add(inputWord);

			// 문자열 각 단어마다 가져와서 자식 노드 중에 있는지 체크 후, 존재하지 않는다면 자식노드 생성
			for (int idx = 0; idx < inputWord.length(); idx++) {
				node = node.childNode.computeIfAbsent(inputWord.charAt(idx), key -> new Node());
			}

			// 마지막 단어에 끝을 표시
			node.endPoint = true;
		}
	}

	// 2. set에 저장된 글자들을 통해 단어 탐색
	static double search() {
		double cnt = 0;
		for (String curWord : word) {
			// 2-1. 첫 글자 카운트
			cnt++;

			Node curNode = rootNode.childNode.get(curWord.charAt(0));

			for (int idx = 1; idx < curWord.length(); idx++) {

				// 2-2. 자식노드가 여러 개 존재하는 경우 또는 자식노드 중 끝이 존재하는 경우 카운트
				// 자식노드가 여러 개 존재하는 경우 다른 단어들이 존재하므로 추천이 현재 노드에서 멈추게 됨!
				// 자식 노드 중 끝이 존재하는 경우 추천이 현재 노드에서 멈추게 됨!
				if (curNode.childNode.size() > 1) {
					cnt++;
				} else if (curNode.endPoint) {
					cnt++;
				}
				curNode = curNode.childNode.get(curWord.charAt(idx));
			}
		}
		return cnt;
	}

	public static void main(String[] args) {
		while (sc.hasNext()) {
			wordCnt = sc.nextInt();
			input();
			double total = search();
			// 3. 모든 카운트의 평균 출력
			String result = String.format("%.2f", total / word.size());
			System.out.println(result);
		}
	}
}
