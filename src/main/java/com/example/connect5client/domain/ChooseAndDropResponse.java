package com.example.connect5client.domain;

public class ChooseAndDropResponse {
    String message;
    Boolean isGameOver;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getGameOver() {
        return isGameOver;
    }

    public void setGameOver(Boolean gameOver) {
        isGameOver = gameOver;
    }

}
