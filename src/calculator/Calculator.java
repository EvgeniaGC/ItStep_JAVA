package calculator;

//Задача: Очень поможет вам вспомнить основы написанный калькулятор. При реализации используйте принципы ООП, соблюдайте пакетную структуру,
//        если вам про нее не рассказывали, то минимально почитайте, там не сложно.
//        Правила по которым должен работать калькулятор:
//        Я могу ввести в консоль следующие данные - цифры, буквы, знаки для математических операций.
//        Bad request: 3+6*17/t или 3+-17 / 12 + & в данном случае должна выдаваться ошибка о некорректном вводе данных.
//        Happy request: 3+6 / 18 - 34, 1*(5+7) - 2.5
//        Допускаются пробелы между символами(от них можно легко избавится в коде)
//        Реализовать умножение, деление, сложение, вычитание. Круто будет, если кто-то реализует модули и иные математические операции

import java.util.ArrayList;
import java.util.Scanner;

public class Calculator {

    public static void main(String[] args) {
//        String str = input().replaceAll(" ", "");
        String str = "(-2.1)*2-24/6-120/((1+2.35)*2+4)+20+2*(1+1.5)";
        if (!checkSymbols(str) || !checkOPBrackets(str)) {
            System.err.println("Incorrect input ! Check your expression. You can enter: digits from 0 to 9, ., +, -, /, *, (, ), space.");
            System.err.println("Also check if you close all brackets.");
        }
        StringBuilder sb = new StringBuilder(str);
        double rez = calculateExp(sb);
        System.out.println(sb + " = " + rez);
    }

    public static String input() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please, enter your expression:");
        return sc.nextLine();
    }

    public static boolean checkSymbols(String str) {
        boolean isRight = true;
        char[] symbolsArr = str.toCharArray();
        for (char k : symbolsArr) {
            if (k < 32 || (k > 32 && k < 40) || k == 44 || k > 57) { // 32 40-43 45-57
                isRight = false;
                break;
            }
        }
        return isRight;
    }

    public static boolean checkOPBrackets(String str) {
        boolean isRight = true;
        char[] symbolsArr = str.toCharArray();
        int opened = 0;
        int closed = 0;
        for (char k : symbolsArr) {
            if (k == 40) opened++;
            if (k == 41) closed++;
        }
        if (opened != closed) isRight = false;
        return isRight;
    }

    public static boolean isDigit(int i) {
        return (i > 47 && i < 58) || i == 46;
    }

    public static double[] buildNumber(StringBuilder sb, int i) {
        double[] numProp = new double[2]; // [0]-num, [1]-quantity of chars
        double num = 0;
        double q = 0;
        boolean afterPoint = false;
        for (int a = 1; i < sb.length(); i++, q++) {
            int k = sb.charAt(i);
            if (isDigit(k)) {
                if (k == 46)
                    afterPoint = true;
                else {
                    if (!afterPoint) num = num * 10 + (k - 48);
                    else num = num + (k - 48) / Math.pow(10, a++);
                }
            } else {
                break;
            }
        }
        numProp[0] = num;
        numProp[1] = q;
        return numProp;
    }

    public static double calculateExp(StringBuilder sb) {
        ArrayList<Double> digits = new ArrayList<>();
        ArrayList<Character> operations = new ArrayList<>();

        while (checkIsThereBrackets(sb)) {
            // TODO:
            String[] rez = findExpInBr(sb);
            StringBuilder newSb = new StringBuilder();
            newSb.append(sb.substring(0, Integer.parseInt(rez[1]))).append(calculateExp(new StringBuilder(rez[0])))
                    .append(sb.substring(Integer.parseInt(rez[2]) + 1, sb.length()));
            sb = newSb;
        }
        boolean isFirstMinus = (sb.charAt(0) == 45);
        for (int i = 0; i < sb.length(); i++) {
            if (isFirstMinus) {
                digits.add(-1.0);
                operations.add('*');
                isFirstMinus = false;
            } else {
                if (isDigit(sb.charAt(i))) {
                    double[] rez = buildNumber(sb, i);
                    digits.add(rez[0]);
                    i += rez[1] - 1;
                } else {
                    operations.add(sb.charAt(i));
                }
            }
        }
        while (operations.size() > 0) {
            boolean isLast = false;
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i) == 42 || operations.get(i) == 47) {
                    double rez = sign(digits.get(i), operations.get(i), digits.get(i + 1));
                    digits.set(i, rez);
                    operations.remove(i);
                    digits.remove(i + 1);
                    break;
                }
                if (i == operations.size() - 1)
                    isLast = true;
            }
            if (isLast)
                break;
        }
        while (operations.size() > 0) {
            for (int i = 0; i < operations.size(); i++) {
                if (operations.get(i) == 43 || operations.get(i) == 45) {
                    double rez = sign(digits.get(i), operations.get(i), digits.get(i + 1));
                    digits.set(i, rez);
                    operations.remove(i);
                    digits.remove(i + 1);
                    break;
                }
            }
        }
        return digits.get(0);
    }

    public static double sign(double a, char ch, double b) {
        double exp;
        switch (ch) {
            case 42:
                exp = a * b;
                break;
            case 47:
                exp = a / b;
                break;
            case 43:
                exp = a + b;
                break;
            case 45:
                exp = a - b;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ch);
        }
        return exp;
    }

    public static String[] findExpInBr(StringBuilder sb) {
        String[] rez = new String[3];
        String str;
        String firstInd = "";
        String lastInd = "";

        int opened = 0;
        int closed = 0;

        boolean findFirst = false;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == 40) {
                while (!findFirst) {
                    firstInd = i + "";
                    findFirst = true;
                }
                opened++;
            }
            if (sb.charAt(i) == 41) {
                lastInd = i + "";
                closed++;
            }
            if (opened == closed && opened != 0)
                break;
        }
        str = sb.substring(Integer.parseInt(firstInd) + 1, Integer.parseInt(lastInd));
        rez[0] = str;
        rez[1] = firstInd;
        rez[2] = lastInd;
        return rez;
    }

    public static boolean checkIsThereBrackets(StringBuilder sb) {
        boolean isThere = false;
        for (int i = 0; i < sb.length(); i++)
            if (sb.charAt(i) == 40)
                isThere = true;
        return isThere;
    }
}