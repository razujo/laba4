package ru.kostikov.lab4;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Задание 6. SMS-сообщение: номер телефона и текст.
 */
@Data
@AllArgsConstructor
public class Sms {
    private String phone;
    private String message;

    @Override
    public String toString() {
        return phone + ": " + message;
    }
}
