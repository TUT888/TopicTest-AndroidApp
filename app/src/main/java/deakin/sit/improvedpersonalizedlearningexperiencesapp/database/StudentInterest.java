package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class StudentInterest {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "studentID")
    private int studentID;

    @ColumnInfo(name = "name")
    private String name;

    private boolean isSelected;

    public StudentInterest(int studentID, String name) {
        this.name = name;
        this.studentID = studentID;
    }

    @Ignore
    public StudentInterest(String name) {
        this.name = name;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
