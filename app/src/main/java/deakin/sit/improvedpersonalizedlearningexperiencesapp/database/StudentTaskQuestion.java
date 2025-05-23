package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class StudentTaskQuestion {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "taskID")
    private int taskID;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "choices")
    private String[] choices;

    @ColumnInfo(name = "correctAnswer")
    private int correctAnswer;

    @ColumnInfo(name = "selectedAnswer")
    private int selectedAnswer;

    public StudentTaskQuestion(int taskID, String title, String description, String[] choices, int correctAnswer) {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.selectedAnswer = -1;
    }

    public boolean isCorrest() {
        return correctAnswer == selectedAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
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
