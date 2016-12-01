package ru.ayaz;

import ru.ayaz.ru.ayaz.exceptions.InvalidNicknameException;
import ru.ayaz.ru.ayaz.exceptions.InvalidUserCommandException;

import java.util.HashSet;
import java.util.Set;

public class User {

    private String nickname;
    private Set<String> ignoredUsers;


    User(String nickname) throws InvalidNicknameException {
        validateNickName(nickname);
        this.nickname = nickname;
        this.ignoredUsers = new HashSet<>();
    }

    private void validateNickName(String nickname) throws InvalidNicknameException {
        if (nickname == null) {
            throw new InvalidNicknameException(nickname);
        }

        if (isSpacesOnly(nickname)) {
            throw new InvalidNicknameException(nickname);
        }

        if (isFirstCharDigit(nickname)) {
            throw new InvalidNicknameException(nickname);
        }
    }

    private boolean isFirstCharDigit(String nickname) {
        return Character.isDigit(nickname.charAt(0));
    }

    private boolean isSpacesOnly(String nickname) {
        return nickname.replaceAll("\\s+", "").equals("");
    }


    String getNickname() {
        return nickname;
    }

    boolean isIgnored(String blackNickname) {
        return ignoredUsers.contains(blackNickname);
    }

    void ignoreUser(String blackNickname) throws InvalidUserCommandException {
        if (isItMe(blackNickname)) {
            throw new InvalidUserCommandException("#ignore", blackNickname);
        } else {
             if(!ignoredUsers.add(blackNickname)) {
                 throw new InvalidUserCommandException("#ignore", blackNickname);
             }
        }
    }

    boolean isItMe(String blackNickName) {
        return nickname.equals(blackNickName);
    }
}
