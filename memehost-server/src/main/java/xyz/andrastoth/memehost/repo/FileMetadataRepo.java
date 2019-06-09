package xyz.andrastoth.memehost.repo;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class FileMetadataRepo {

    private static final String DB_FILE_PATH = System.getProperty("db-file-path", "~/memehost");

    public FileMetadataRepo() {
        initDb();
    }

    private void initDb() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:h2:" + DB_FILE_PATH, "sa", "");

            connection
                    .createStatement()
                    .execute("CREATE TABLE IF NOT EXISTS TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));");

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
