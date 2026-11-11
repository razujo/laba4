package ru.kostikov.lab4;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Книга из списка избранного посетителя.
// Lombok @Data генерирует геттеры, сеттеры, equals/hashCode и toString,
// поэтому одинаковые книги разных посетителей считаются равными (нужно для distinct()).
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String name;
    private String author;
    private int publishingYear;
    private String isbn;
    private String publisher;
}
