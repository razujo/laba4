package ru.kostikov.lab4;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Посетитель библиотеки. Имена полей совпадают с ключами в books.json,
// поэтому Gson заполняет их автоматически.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visitor {
    private String name;
    private String surname;
    private String phone;
    private boolean subscribed;
    private List<Book> favoriteBooks;
}
