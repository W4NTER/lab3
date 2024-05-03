package urfu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
//        String message = "Штирлиц – Вы Герой!!";
        String key;
        String message;
        String encryptedMessage;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите режим работы: ");
        System.out.println("1 - генерация одного ключа, " +
                "2 - дешифровка, " +
                "3 - генерация 10 равнозначных ключей " +
                "4 - дешефровка из \"однозначного\" ключа");
        int command = scanner.nextInt();
        switch (command) {
            case 1 -> {
                System.out.println("Введите сообщение:");
                scanner.nextLine();
                message = scanner.nextLine();
                key = getStartedKey(message);
                System.out.println("key - " + key);
                System.out.println("encrypted message - " + encryptedMessage(message, key));
            }
            case 2 -> {
                System.out.println("Ведите зашифрованное сообщение ");
                scanner.nextLine();
                encryptedMessage = scanner.nextLine();
                System.out.println("Введите ключ ");
                key = scanner.nextLine();
                System.out.println("Сообщение - " + decryptedMessage(encryptedMessage, key));
            }
            case 3 -> {
                System.out.println("Введите сообщение:");
                scanner.nextLine();
                message = scanner.nextLine();
                key = generateKey(message);
                saveKeysAtFile(generateKeys(key));
                System.out.println("зашифрованное сообщение - " + encryptedMessage(message, key));
                System.out.println("А ключи вы найдете в отдельном файле)");
            }
            case 4 -> {
                System.out.println("Ведите зашифрованное сообщение ");
                scanner.nextLine();
                encryptedMessage = scanner.nextLine();
                System.out.println("Введите ключ ");
                key = scanner.nextLine();
                System.out.println("Сообщение - " + decryptedMessage(encryptedMessage, getStartedKey(key)));
            }
        }
    }

    private static void saveKeysAtFile(String[] keys) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"))) {
            for (String key : keys) {
                bufferedWriter.write(key + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateKey(String message) {
        Random random = new Random();
        String[] res = new String[message.length()];
        for (int i = 0; i < message.length(); i++) {
            res[i] = Integer.toHexString(random.nextInt(0, 256));
        }
        return String.join(" ", res);
    }

    private static String decryptedMessage(String message, String key) {
        StringBuilder res = new StringBuilder();
        Character[] messageArr = getArrFromHex(message);
        Character[] keyArr = getArrFromHex(key);
        Arrays.stream(decrypt(messageArr, keyArr)).forEach(res::append);
        return res.toString();
    }

    private static String[] decrypt(Character[] messageArr, Character[] keyArr) {
        String[] res = new String[messageArr.length];
        for (int i = 0; i < messageArr.length; i++) {
            res[i] = String.valueOf( (char) (messageArr[i] ^ keyArr[i]));
        }
        return res;
    }

    private static String encryptedMessage(String message, String key) {
        Character[] messageArr = getArrFromMessage(message);
        Character[] keyArr = getArrFromHex(key);
        return String.join(" ", encrypt(messageArr, keyArr));
    }

    private static String[] encrypt(Character[] messageArr, Character[] keyArr) {
        String[] resArr = new String[messageArr.length];
        for (int i = 0; i < messageArr.length; i++) {
            resArr[i] = Integer.toHexString(messageArr[i] ^ keyArr[i]);
        }
        return resArr;
    }

    private static Character[] getArrFromHex(String key) {
        return Arrays.stream(key.split(" "))
                .map(a -> (char) Integer.parseInt(a, 16)).toArray(Character[]::new);
    }

    private static Character[] getArrFromMessage(String message) {
        return Arrays.stream(message.split(""))
                .map(a -> a.charAt(0)).toArray(Character[]::new);
    }

    //Вот такая простенькая формула получилась - xor(A, ikey) + A (а - случайно созданный ключ,
    // ikey - рандомно сгенерированный ключ, который используется для кодирования сообщения (гамма))
    private static String[] generateKeys(String key) {
        String[] res = new String[10];
        String[] startedKey = key.split(" ");
        int lengthOfKey = startedKey.length;
        String[] newKey = new String[lengthOfKey];
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            String[] currRes = Arrays.stream(newKey).map(a ->
                    Integer.toHexString(random.nextInt( 0, 256))).toArray(String[]::new);
            newKey = xor(startedKey, currRes);
            String[] resArr = Stream.of(newKey, currRes).flatMap(Stream::of).toArray(String[]::new);
            res[i] = String.join(" ", resArr);
        }
        return res;
    }

    private static String getStartedKey(String key) {
        String[] keyArr = key.split(" ");
        String[] newKey = new String[keyArr.length / 2];
        int j = keyArr.length / 2;
        for (int i = 0; i < keyArr.length / 2; i++) {
            newKey[i] = Integer.toHexString(Integer.parseInt(keyArr[i], 16)
                    ^ Integer.parseInt(keyArr[j], 16));
            j++;
        }
        return String.join(" ", newKey);
    }

    private static String[] xor(String[] arr1, String[] arr2) {
        String[] res = new String[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            res[i] = Integer.toHexString(
                     Integer.parseInt(arr1[i], 16) ^ Integer.parseInt(arr2[i], 16));
        }
        return res;
    }
}