import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;

import java.util.Arrays;

public class test {

    public static int add(int a, int b) {
        return a + b;
    }

    public void instanceMethod() {
        System.out.println("인스턴스 메서드 호출");
    }

    public static void staticMethod() {
        System.out.println("정적 메서드 호출");
    }

    public static void main(String[] args) {
        // 인스턴스 메서드 호출
        test myObject = new test();
        myObject.instanceMethod();

        // 정적 메서드 호출
        test.staticMethod();

        int number = 10;
        if (number > 5) {
            System.out.println("Number is greater than 5");
        }

        for (int i = 0; i < 5; i++) {
            System.out.println(i);
        }

        int result = add(100, 3);
        System.out.println(result);

        int i = 1;
        while (i <= 5) {
            System.out.println(i);
            i++;
        }

        Map<String, Integer> map = new HashMap<>();
        System.out.println(map);
        if (map.isEmpty()) {
            System.out.println("map is empty");
        }
        else {
            System.out.println("map is not empty");
        }
        map.put("apple", 10);
        map.put("orange", 20);
        
        if (map.isEmpty()) {
            System.out.println("map is empty");
        }
        else {
            System.out.println("map is not empty");
        }
        int value = map.get("apple"); // 10
        System.out.println(map);
        map.remove("orange");
        System.out.println(map);

        ArrayList<String> myList = new ArrayList<String>();
        myList.add("Hello");
        myList.add("World");

        System.out.println(myList);
        
        int[] myArray = {1, 2, 3};
        String[] myStringArray = {"Hello", "World"};

        System.out.println(Arrays.toString(myArray));
        System.out.println(Arrays.toString(myStringArray));

        Arrays.stream(myArray).forEach(System.out::print);

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 짝수를 필터링하고, 각 숫자를 제곱하여 리스트로 수집
        List<Integer> squaredEvenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0) // 짝수만 선택
            .map(n -> n * n)         // 각 숫자를 제곱
            .collect(Collectors.toList()); // 결과를 리스트로 수집

        System.out.println(squaredEvenNumbers); // [4, 16, 36, 64, 100]

        
        Scanner scanner = new Scanner(System.in); // Scanner 객체 생성
        System.out.println("사각형의 가로 길이를 입력해주세요.");
        int width = scanner.nextInt(); // 가로 길이 입력
        System.out.println("사각형의 세로 길이를 입력해주세요.");
        int height = scanner.nextInt(); // 세로 길이 입력
        
        
        int area = width * height; // 사각형의 넓이 계산
        System.out.println("사각형의 넓이는 %d 입니다.".formatted(area));


        System.out.println("메시지를 입력해주세요.");
        //String msg;
        String msg = scanner.nextLine();
        System.out.println(msg);

    }
}
