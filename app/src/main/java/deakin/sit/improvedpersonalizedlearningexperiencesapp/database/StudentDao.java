package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM student")
    List<Student> getAll();

    @Query("SELECT * FROM student WHERE id = :id")
    Student getStudentByID(int id);

    @Query("SELECT * FROM student WHERE username = :username")
    Student getStudentByUsername(String username);

    @Query("SELECT * FROM student WHERE username = :username AND password = :password")
    Student login(String username, String password);

    @Insert
    void register(Student student);
}
