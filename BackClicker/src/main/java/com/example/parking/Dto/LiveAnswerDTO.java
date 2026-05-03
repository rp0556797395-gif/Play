package com.example.parking.Dto;

public class LiveAnswerDTO {
    private String phone;
    private String answer;
    private boolean correct;

    // getters + setters


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getPhone() {
        return phone;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isCorrect() {
        return correct;
    }
}