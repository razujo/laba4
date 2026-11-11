package ru.kostikov.lab4;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws IOException {
        // читаем файл и с помощью Gson превращаем JSON в массив объектов Visitor
        FileReader reader = new FileReader("src/main/resources/books.json");
        Visitor[] array = new Gson().fromJson(reader, Visitor[].class);
        reader.close();
        List<Visitor> visitors = Arrays.asList(array);

        task1(visitors);
        task2(visitors);
        task3(visitors);
        task4(visitors);
        task5(visitors);
        task6(visitors);
    }

    // Задание 1. Список посетителей и их количество
    static void task1(List<Visitor> visitors) {
        System.out.println("===== Задание 1 =====");
        visitors.stream()
                .forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));
        System.out.println("Всего посетителей: " + visitors.stream().count());
        System.out.println();
    }

    // Задание 2. Книги без повторений и их количество
    static void task2(List<Visitor> visitors) {
        System.out.println("===== Задание 2 =====");
        List<Book> books = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())   // все книги всех посетителей в один поток
                .distinct()                                     // убираем повторы
                .toList();
        books.forEach(b -> System.out.println(b.getName() + " - " + b.getAuthor()));
        System.out.println("Уникальных книг: " + books.size());
        System.out.println();
    }

    // Задание 3. Книги, отсортированные по году издания
    static void task3(List<Visitor> visitors) {
        System.out.println("===== Задание 3 =====");
        visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .sorted(Comparator.comparing(Book::getPublishingYear).thenComparing(Book::getName))
                .forEach(b -> System.out.println(b.getPublishingYear() + ": " + b.getName() + " (" + b.getAuthor() + ")"));
        System.out.println();
    }

    // Задание 4. Есть ли у кого-то книга Jane Austen
    static void task4(List<Visitor> visitors) {
        System.out.println("===== Задание 4 =====");
        boolean found = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> b.getAuthor().equals("Jane Austen"));
        if (found) {
            System.out.println("Книга автора Jane Austen есть в избранном: да");
        } else {
            System.out.println("Книга автора Jane Austen есть в избранном: нет");
        }

        // дополнительно: у кого именно
        List<String> names = visitors.stream()
                .filter(v -> v.getFavoriteBooks().stream().anyMatch(b -> b.getAuthor().equals("Jane Austen")))
                .map(v -> v.getName() + " " + v.getSurname())
                .toList();
        System.out.println("У посетителей: " + String.join(", ", names));
        System.out.println();
    }

    // Задание 5. Максимальное число книг в избранном
    static void task5(List<Visitor> visitors) {
        System.out.println("===== Задание 5 =====");
        int max = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .getAsInt();
        System.out.println("Максимальное число книг в избранном: " + max);
        System.out.println();
    }

    // Задание 6. SMS подписчикам
    static void task6(List<Visitor> visitors) {
        System.out.println("===== Задание 6 =====");
        double average = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .getAsDouble();
        System.out.println("Среднее количество книг в избранном: " + average);

        // для каждой категории отбираем подписчиков и делаем им sms
        printCategory(visitors, average, "you are a bookworm");
        printCategory(visitors, average, "read more");
        printCategory(visitors, average, "fine");
    }

    static void printCategory(List<Visitor> visitors, double average, String message) {
        List<Sms> smsList = visitors.stream()
                .filter(v -> v.isSubscribed())
                .filter(v -> getMessage(v.getFavoriteBooks().size(), average).equals(message))
                .map(v -> new Sms(v.getPhone(), message))
                .toList();

        System.out.println();
        System.out.println("Категория \"" + message + "\" (" + smsList.size() + "):");
        if (smsList.isEmpty()) {
            System.out.println("   нет посетителей");
        }
        for (Sms sms : smsList) {
            System.out.println("   " + sms);
        }
    }

    // какое сообщение получит человек с таким количеством книг
    static String getMessage(int count, double average) {
        if (count > average) {
            return "you are a bookworm";
        } else if (count < average) {
            return "read more";
        } else {
            return "fine";
        }
    }
}
