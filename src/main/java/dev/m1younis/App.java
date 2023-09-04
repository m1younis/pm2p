
package dev.m1younis;

import dev.m1younis.controller.DatabaseController;
import dev.m1younis.view.MainView;
import java.io.File;
import java.io.IOException;

/**
 * The project's entry point, responsible for launching the application.
 */
public class App {
    public static void main(String[] args) {
        // Local database file is created and configured given it doesn't already exist
        try {
            if (new File(DatabaseController.DATABASE_PATH).createNewFile()) {
                DatabaseController.init();
                System.out.printf(
                    "`%s` created and initialised\n",
                    DatabaseController.DATABASE_PATH
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new MainView();
    }
}
