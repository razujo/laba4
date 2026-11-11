package ru.kostikov.lab4;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Лабораторная работа №4. Разбор books.json с помощью Gson и обработка данных через Stream API.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        List<Visitor> visitors = readVisitors();

        task1(visitors);
        task2(visitors);
        task3(visitors);
        task4(visitors);
        task5(visitors);
        task6(visitors);
    }

    private static List<Visitor> readVisitors() throws IOException {
        try (InputStream in = Main.class.getResourceAsStream("/books.json")) {
            if (in == null) {
                throw new IOException("Файл books.json не найден в ресурсах");
            }
            Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Visitor>>() { }.getType();
            return new Gson().fromJson(reader, listType);
        }
    }

    /** Задание 1. Список посетителей и их количество. */
    private static void task1(List<Visitor> visitors) {
        System.out.println("===== Задание 1 =====");
        visitors.stream()
                .map(v -> v.getName() + " " + v.getSurname())
                .forEach(System.out::println);
        System.out.println("Всего посетителей: " + visitors.stream().count());
        System.out.println();
    }

    /** Задание 2. Список и количество книг в избранном без повторений. */
    private static void task2(List<Visitor> visitors) {
        System.out.println("===== Задание 2 =====");
        List<Book> uniqueBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .toList();
        uniqueBooks.forEach(b -> System.out.println(b.getName() + " - " + b.getAuthor()));
        System.out.println("Уникальных книг: " + uniqueBooks.size());
        System.out.println();
    }

    /** Задание 3. Книги, отсортированные по году издания. */
    private static void task3(List<Visitor> visitors) {
        System.out.println("===== Задание 3 =====");
        visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .sorted(Comparator.comparingInt(Book::getPublishingYear)
                        .thenComparing(Book::getName))
                .forEach(b -> System.out.println(b.getPublishingYear() + ": "
                        + b.getName() + " (" + b.getAuthor() + ")"));
        System.out.println();
    }

    /** Задание 4. Есть ли у кого-то в избранном книга Jane Austen. */
    private static void task4(List<Visitor> visitors) {
        System.out.println("===== Задание 4 =====");
        String author = "Jane Austen";
        boolean found = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> author.equals(b.getAuthor()));
        System.out.println("Книга автора " + author + " есть в избранном: " + (found ? "да" : "нет"));

        List<String> fans = visitors.stream()
                .filter(v -> v.getFavoriteBooks().stream().anyMatch(b -> author.equals(b.getAuthor())))
                .map(v -> v.getName() + " " + v.getSurname())
                .toList();
        System.out.println("У посетителей: " + String.join(", ", fans));
        System.out.println();
    }

    /** Задание 5. Максимальное число книг в избранном у одного посетителя. */
    private static void task5(List<Visitor> visitors) {
        System.out.println("===== Задание 5 =====");
        int max = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимальное число книг в избранном: " + max);
        System.out.println();
    }

    /** Задание 6. SMS подписчикам в зависимости от числа книг относительно среднего. */
    private static void task6(List<Visitor> visitors) {
        System.out.println("===== Задание 6 =====");
        double average = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0);
        System.out.println("Среднее количество книг в избранном: " + average);

        Map<String, List<Sms>> groups = visitors.stream()
                .filter(Visitor::isSubscribed)
                .collect(Collectors.groupingBy(
                        v -> messageFor(v.getFavoriteBooks().size(), average),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                v -> new Sms(v.getPhone(), messageFor(v.getFavoriteBooks().size(), average)),
                                Collectors.toList())));

        // Выводим все три категории, даже если какая-то из них пустая
        Stream.of("you are a bookworm", "read more", "fine").forEach(message -> {
            List<Sms> smsList = groups.getOrDefault(message, List.of());
            System.out.println("\nКатегория \"" + message + "\" (" + smsList.size() + "):");
            if (smsList.isEmpty()) {
                System.out.println("   нет посетителей");
            }
            smsList.forEach(sms -> System.out.println("   " + sms));
        });
    }

    private static String messageFor(int booksCount, double average) {
        int cmp = Double.compare(booksCount, average);
        if (cmp > 0) {
            return "you are a bookworm";
        } else if (cmp < 0) {
            return "read more";
        }
        return "fine";
    }
}
