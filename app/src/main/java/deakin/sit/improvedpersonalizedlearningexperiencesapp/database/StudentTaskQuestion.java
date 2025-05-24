package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class StudentTaskQuestion implements Serializable {
    private String title;
    private String description;
    private String[] choices;
    private int correctAnswer;
    private int selectedAnswer;

    public StudentTaskQuestion(String title, String description, String[] choices, int correctAnswer) {
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = -1;
    }

    public StudentTaskQuestion(String title, String description, String[] choices, int correctAnswer, int selectedAnswer) {
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isCorrest() {
        return correctAnswer == selectedAnswer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getChoices() {
        return choices;
    }

    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}
