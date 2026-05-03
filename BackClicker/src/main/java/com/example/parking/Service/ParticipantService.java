package com.example.parking.Service;


import com.example.parking.Entities.Participant;
import com.example.parking.Entities.Question;
import com.example.parking.Reposetories.ParticipantRepository;
import com.example.parking.Reposetories.QuestionRepository;
import com.example.parking.Service.QuizService;
import com.example.parking.ParkingApplication; // כדי לגשת לרשימת השאלות
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired

    private QuestionRepository questionRepository;
    @Autowired

    private QuizService quizService;

    public String processPhoneInput(String phoneNumber, String inputData) {

        // 1. יצירת משתתף אם לא קיים
        Participant participant = participantRepository.findById(phoneNumber)
                .orElseGet(() -> {
                    Participant p = new Participant();
                    p.setPhoneNumber(phoneNumber);
                    p.setCurrentQuestionIndex(0); // חשוב
                    p.setScore(0);
                    return p;
                });

        // 2. שאלה נוכחית מהמערכת (הגלובלית!)
        int currentIndex = quizService.getCurrentGlobalQuestionIndex();

        List<Question> questions = questionRepository.findAll();

        // 3. בדיקה אם נגמרו השאלות
        if (currentIndex >= questions.size()) {
            return "read=t-סיימת את כל השאלות! הניקוד שלך הוא "
                    + participant.getScore() + ".&&";
        }

        // 4. שליפת שאלה נוכחית
        Question currentQuestion = questions.get(currentIndex);

        // 5. בדיקת תשובה
        boolean correct = currentQuestion.getCorrectAnswer().equals(inputData);

        if (correct) {
            participant.setScore(participant.getScore() + 10);
        }

        // 6. שמירה
        participantRepository.save(participant);

        // 7. תשובה לימות המשיח
        return correct
                ? "read=t-תשובה נכונה!&&"
                : "read=t-תשובה שגויה!&&";
    }



}