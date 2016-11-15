package ru.ayaz;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String nickname;
    private Set<String> ignoredUsers;


    public User() {
        this.ignoredUsers = new HashSet<String>();
    }

    public void setNickname(String nickname) throws InvalidNicknameException {
        validateNickName(nickname);
        this.nickname = nickname;
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


    public String getNickname() {
        return nickname;
    }

    public boolean isIgnored(String nickname) {
        return ignoredUsers.contains(nickname);
    }

    public void ignoreUser(String blackNickname) throws InvalidUserCommandException {
        if (isItMe(blackNickname)) {
            throw new InvalidUserCommandException("#ignore", blackNickname);
        } else {
             if(!ignoredUsers.add(blackNickname)) {
                 throw new InvalidUserCommandException("#ignore", blackNickname);
             }
        }
    }

    public boolean isItMe(String nickname) {
        return this.nickname.equals(nickname);
    }

}
