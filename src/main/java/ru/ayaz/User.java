package ru.ayaz;

import java.util.Set;
import java.util.TreeSet;

public class User {
    private String nickname;
    private Set<String> ignoredUsersSet;


    public User() {
        this.ignoredUsersSet = new TreeSet<String>();
    }

    public void setNickname(String nickname) throws invalidNicknameException {
        validateNickName(nickname);
        this.nickname = nickname;
    }

    private void validateNickName(String nickname) throws invalidNicknameException {
        if (nickname == null) {
            throw new invalidNicknameException(nickname);
        }

        if (isSpacesOnly(nickname)) {
            throw new invalidNicknameException(nickname);
        }

        if (isFirstCharDigit(nickname)) {
            throw new invalidNicknameException(nickname);
        }
    }

    private boolean isFirstCharDigit(String nickname) {
        return Character.isDigit(nickname.charAt(0));
    }

    private boolean isSpacesOnly(String nickname) {
        return nickname.replaceAll("\\s+", "").equals("");
    }


    public String getNickname() {
        return nickname;
    }

    public boolean isInIgnoredSet(String nickname) {
        return ignoredUsersSet.contains(nickname);
    }

    public boolean ignoreUser(String blackNickname) {
        if (isItMe(blackNickname)) {
            return false;
        } else {
            return ignoredUsersSet.add(blackNickname);
        }
    }

    public boolean isItMe(String nickname) {
        return this.nickname.equals(nickname);
    }

}
