package com.example.parking.Service;
import com.example.parking.Dto.QuestionStatsDTO;
import com.example.parking.Entities.Participant;
import com.example.parking.Entities.Question;
import com.example.parking.Reposetories.ParticipantRepository;
import com.example.parking.Reposetories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ParticipantRepository repository;

    private Map<Integer, QuestionStatsDTO> statsMap = new HashMap<>();
    private int currentGlobalQuestionIndex = 0;
    private int questionVersion = 0;
    private boolean gameActive = false;

    private int questionTimeSeconds = 20; // זמן לכל שאלה




    // =====================
    // START GAME
    // =====================
    public void startGame() {
        gameActive = true;
        currentGlobalQuestionIndex = -1;
        questionVersion = 1;
    }

    public boolean hasActiveQuestion() {
        return currentGlobalQuestionIndex >= 0;
    }
    // =====================
    // NEXT QUESTION (ONLY ONE!)
    // =====================
    public void nextQuestion() {
        if (!gameActive) return;

        List<Question> questions = questionRepository.findAll();

        if (currentGlobalQuestionIndex + 1 >= questions.size()) {
            gameActive = false;
            return;
        }

        currentGlobalQuestionIndex++;
        questionVersion++;
    }

    // =====================
    // GETTERS
    // =====================
    public int getCurrentGlobalQuestionIndex() {
        return currentGlobalQuestionIndex;
    }

    public int getQuestionVersion() {
        return questionVersion;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    // =====================
    // ANSWER PROCESSING
    // =====================
    public int processAnswer(String phone, String answer, boolean correct) {

        // 🔥 חישוב נקודות:
        // אם התשובה נכונה (correct == true) → מוסיפים 10 נקודות
        // אחרת → 0 נקודות
        int pointsToAdd = correct ? 10 : 0;


        // 🔥 שליפת המשתמש לפי מספר טלפון
        // אם לא קיים → יצירת משתמש חדש עם ניקוד התחלתי 0
        Participant p = repository.findById(phone)
                .orElse(new Participant(1, 0, phone));

        // 🔥 עדכון מספר טלפון (למקרה של משתמש חדש)
        p.setPhoneNumber(phone);

        // 🔥 הוספת הנקודות שחושבו למשתמש
        p.setScore(p.getScore() + pointsToAdd);

        // 🔥 שמירת אינדקס השאלה הנוכחית
        // לצורך מעקב אחרי התקדמות השחקן
        p.setCurrentQuestionIndex(currentGlobalQuestionIndex);

        // 🔥 שמירת הנתונים המעודכנים לדאטהבייס
        repository.save(p);

        int qIndex = currentGlobalQuestionIndex;

        statsMap.putIfAbsent(qIndex, new QuestionStatsDTO());

        QuestionStatsDTO stats = statsMap.get(qIndex);

        switch (answer) {
            case "1" -> stats.setAnswer1(stats.getAnswer1() + 1);
            case "2" -> stats.setAnswer2(stats.getAnswer2() + 1);
            case "3" -> stats.setAnswer3(stats.getAnswer3() + 1);
            case "4" -> stats.setAnswer4(stats.getAnswer4() + 1);
        }
        // 🔥 החזרת הניקוד המעודכן של המשתמש
        return p.getScore();
    }

    public Question getCurrentQuestion() {
        if (currentGlobalQuestionIndex < 0) return null;
        return questionRepository.findById((long) (currentGlobalQuestionIndex + 1))
                .orElse(null);
    }

    public void resetGame() {
        gameActive = false;
        currentGlobalQuestionIndex = 0;
        // questionStartTime = 0;
    }
}