package deakin.sit.improvedpersonalizedlearningexperiencesapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Student.class, StudentInterest.class, StudentTask.class, StudentTaskQuestion.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract StudentInterestDao studentInterestDao();
    public abstract StudentTaskDao studentTaskDao();
    public abstract StudentTaskQuestionDao studentTaskQuestionDao();

    private static AppDatabase instance;
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app-database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}

