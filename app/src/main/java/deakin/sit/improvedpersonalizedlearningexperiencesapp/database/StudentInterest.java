package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

public class StudentInterest {
    private int id;
    private int studentID;
    private String name;
    private boolean isSelected;

    public StudentInterest(int studentID, String name) {
        this.name = name;
        this.studentID = studentID;
    }

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
