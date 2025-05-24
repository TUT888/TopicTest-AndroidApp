package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class StudentTask implements Serializable {
    private String id;
    private String studentID;
    private String title;
    private String description;
    private boolean finish;
    private int score;
//    private int totalQuestion;
    private List<StudentTaskQuestion> studentTaskQuestions;

    public StudentTask(String studentID, String title, String description) {
        this.studentID = studentID;
        this.title = title;
        this.description = description;
        this.finish = false;
        this.score = 0;
//        this.totalQuestion = 0;
        this.studentTaskQuestions = new ArrayList<StudentTaskQuestion>();
    }

    public StudentTask(String id, String studentID, String title, String description, boolean finish, int score, List<StudentTaskQuestion> studentTaskQuestions) {
        this.id = id;
        this.studentID = studentID;
        this.title = title;
        this.description = description;
        this.finish = finish;
        this.score = score;
        this.studentTaskQuestions = studentTaskQuestions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

//    public int getTotalQuestion() {
//        return totalQuestion;
//    }
//
//    public void setTotalQuestion(int totalQuestion) {
//        this.totalQuestion = totalQuestion;
//    }

    public List<StudentTaskQuestion> getStudentTaskQuestions() {
        return studentTaskQuestions;
    }

    public void setStudentTaskQuestions(List<StudentTaskQuestion> studentTaskQuestions) {
        this.studentTaskQuestions = studentTaskQuestions;
    }

    public void addQuestion(StudentTaskQuestion question) {
        this.studentTaskQuestions.add(question);
    }
}
