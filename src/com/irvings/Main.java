package com.irvings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {

    static Map<String, String> map = new HashMap<>();
    static Map<String, String> temp;
    static Map<String, String> top;

    static Deque<Map<String, String>> transactions = new ArrayDeque<>();
    static Map<String, LinkedList<Integer>> counts = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Enter command ...");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            String next;
            while ((next = bufferedReader.readLine()) != null) {
                try {
                    if (!next.isEmpty()) {
                        Object result = runCommand(next.split("\\s+"));
                        System.out.println(result);
                    }
                } catch(Exception e) {
                    System.out.println("Invalid syntax, please try again");
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static Object runCommand(String... args) {
        switch(args[0].toUpperCase(Locale.ROOT)) {
            case "SET":
                set(args[1], args[2]);
                break;
            case "GET":
                return get(args[1]);
            case "DELETE":
                delete(args[1]);
                break;
            case "COUNT":
                return count(args[1]);
            case "BEGIN":
                begin();
                break;
            case "ROLLBACK":
                return rollback();
            case "COMMIT":
                return commit();
            case "END":
                System.exit(0);
            default:
                return "Invalid syntax, please try again";
        }
        return "";
    }

    public static void set(String key, String value) {
        map.put(key, value);
        pushCount(value);
    }

    public static String get(String name) {
        return map.getOrDefault(name, null);
    }

    public static void delete(String name) {
        String value = map.remove(name);
        popCount(value);
    }

    public static long count(String value) {
        return map.values().stream().filter(e -> e.equals(value)).count();
    }

    public static void begin() {
        top = new HashMap<>(map);
        transactions.push(top);
        temp = map;
        map = top;
    }

    public static Object rollback() {
        try {
            transactions.pop();
            if (transactions.peek() != null) {
                map = top = transactions.peek();
            } else {
                map = temp;
            }
        } catch(NoSuchElementException e) {
            return "TRANSACTION NOT FOUND";
        }
        return "";
    }

    public static Object commit() {
        try {
            map = transactions.pop();
            temp = null;
            transactions.clear();
        } catch(NoSuchElementException e) {
            return "TRANSACTION NOT FOUND";
        }
        return "";
    }

    public static void pushCount(String value) {
        LinkedList<Integer> _counts;
        if(counts.get(value) != null) {
            _counts = counts.get(value);
            _counts.push(1);
        } else {
            _counts = new LinkedList<>();
            _counts.push(1);
            counts.put(value, _counts);
        }
    }

    public static void popCount(String value) {
        LinkedList<Integer> _counts;
        if(counts.get(value) != null) {
            _counts = counts.get(value);
            _counts.pop();
        }
    }
}
