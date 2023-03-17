import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Quizzer {
    public static void main(String[] args) throws SQLException {
        // Decided to use a Database class to better organize my code
        // db_url, user and pass are not actual values, you will need to put your own values in based on your database
        // I have information in outline if you want to make one similar to mine
        Database db = new Database(db_url, user, pass);

        db.setConnection();
        System.out.println("Welcome to Quizzer!");

        // StandardCharsets.UTF_8 is an encoding system for string unicode translation. it just ensures all computers
        // parse the string the same way
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        handle_menu(scanner, db, 0);
    }

    // function to handle a menu
    public static void handle_menu(Scanner scanner, Database db, int type) throws SQLException {
        int selection;

        // adding a label allows me to break out of the entire selection handle_menu process if a user enters quit
        // inside a nested function -> basically allows you to only exit the second layer of menus
        outerloop:
        while (true) {
            selection = start_menu(scanner, db, type);

            switch (selection) {
                case 0:
                    add_db(scanner, db, type);
                    break;
                case 1:
                    select_db(scanner, db, type);
                    break;
                default:
                    if (type == 0) {
                        System.out.println("Closing quizzer...");

                        // resets reviewed value
                        db.modify_table("UPDATE note_categories SET reviewed = 0");
                        db.closeConnection();

                        // 0 indicates successful termination, 1 is unsuccessful, -1 is same as 1 but with exception
                        System.exit(0);
                    } else {
                        break outerloop;
                    }
            }
        }
    }

    // Organize code for the user menu interface
    // type = 0 means it is the start menu prior to selecting a category, type = 1 means it is the start menu after
    // selecting a category -> meant to save code instead of needing another while loop
    public static int start_menu(Scanner scanner, Database db, int type) throws SQLException {
        ResultSet rs;
        int selection;
        String menu_description, menu_selection;

        if (type == 0) {
            menu_description = "Would you like to add a notes category, select a notes category or quit?";
            menu_selection = "Please enter add, select or quit";
        } else {
            menu_description = "Would you like to add a note card, review a notes category or quit to the main menu?";
            menu_selection = "Please enter add, review or quit";
        }

        // ensures the system only reminds you once
        rs = db.select_statement("SELECT COUNT(category_name) as count FROM note_categories WHERE reviewed = 0");
        rs.next();
        int count = rs.getInt("count");

        if (count != 0) {
            rs = db.select_statement("SELECT COUNT(category_name) AS length FROM note_categories WHERE review_date " +
                    "<= CURRENT_DATE()");
            rs.next();
            int length = rs.getInt("length");

            if (length != 0) {
                rs = db.select_statement("SELECT category_name FROM note_categories WHERE review_date " +
                        "<= CURRENT_DATE()");
                System.out.print("You need to review: ");
                int i = 1;

                while (rs.next()) {
                    String category_name = rs.getString("category_name");

                    if (i != length) {
                        System.out.print(category_name + ", ");
                    } else {
                        System.out.println(category_name);
                        break;
                    }

                    i++;
                }
            }

            db.modify_table("UPDATE note_categories SET reviewed = 1");
        }

        System.out.println(menu_description);

        // ensures the user must enter one of: add, select or quit
        while (true) {
            System.out.println(menu_selection);
            String response = scanner.nextLine().trim();

            if (response.equalsIgnoreCase("add")) {
                selection = 0;

                if (type == 1) {
                    check_category(scanner, db, 0);
                }

                break;
            } else if (response.equalsIgnoreCase("select") || response.equalsIgnoreCase("review")) {
                selection = 1;

                if (type == 1) {
                    check_category(scanner, db, 1);
                }

                break;
            } else if (response.equalsIgnoreCase("quit")) {
                selection = 2;
                break;
            } else {
                System.out.println("You have not entered a valid selection, please try again.");
            }
        }
        return selection;
    }

    // organizes the adding of a category to the quizzer database
    public static void add_db(Scanner scanner, Database db, int type) throws SQLException {
        String add_description, add_check, category_name, note_description, note_answer, double_check;
        int default_value;

        if (type == 1) {
            // checks to make sure quit wasn't clicked in check_category
            ResultSet rs = db.select_statement("SELECT DEFAULT(category_id) as default_value FROM note_cards");

            if (rs.next()) {
                default_value = rs.getInt("default_value");
            } else {
                default_value = 0;
            }


            if (default_value == -1) {
                return;
            }
        }

        // gives a value other than "" to the string variables so the code will run regardless of type
        category_name = note_description = note_answer = "tmp";

        if (type == 0) {
            add_description = "Enter the name of the note category you would like to add or enter quit to go back" +
                    " to the home menu";
            add_check = "Invalid category name";
        } else {
            add_description = "Enter the note description, followed by the note answer on the next line or quit" +
                    " to go back to the select menu";
            add_check = "Invalid answer or description";
        }

        // ensures user doesn't enter a blank string as a category name
        // outerloop is a label to the loop and allows it to be specifically broken out of even if nested (line 100)
        outerloop:
        while (true) {
            System.out.println(add_description);

            if (type == 0) {
                category_name = scanner.nextLine().trim();
            } else {
                note_description = scanner.nextLine().trim();

                // allows checking of note_description in real time
                if (note_description.equalsIgnoreCase("quit")) {
                    break;
                }

                note_answer = scanner.nextLine().trim();
            }

            ResultSet rs = db.select_statement("SELECT * FROM note_categories WHERE category_name LIKE \"" +
                    category_name + "\"");

            if (category_name.equals("") || note_description.equals("") || note_answer.equals("")) {
                System.out.println(add_check);
            } else if (category_name.equalsIgnoreCase("quit") || note_answer.equalsIgnoreCase("quit")) {
                break;
            } else if (rs.next()) {
                System.out.println("There is already a category named " + category_name);
            } else {
                if (type == 0) {
                    // capitalizes the first letter of handle_menu(scanner, db, 1)category_name (formats it)
                    // .substring gets the substring from index _ to _ (not inclusive)
                    category_name = category_name.substring(0, 1).toUpperCase() + category_name.substring(1);

                    double_check = "Is " + category_name + " the correct category name? Yes or no?";
                } else {
                    double_check = "Is the note description:\n" + note_description + "\nand the note answer:\n"
                            + note_answer + "\ncorrect?";
                }

                while (true) {
                    // double checks if category_name is correct
                    System.out.println(double_check);
                    String check = scanner.nextLine();

                    if (check.equalsIgnoreCase("yes")) {
                        if (type == 0) {
                            String[] cat_name = {category_name};
                            db.stored_procedure("{CALL add_category(?)}", cat_name);
                            System.out.println("Category name added!");
                        } else {
                            String[] note_details = {note_description, note_answer};
                            db.stored_procedure("CALL add_note(?, ?)", note_details);
                            System.out.println("Note card added!");
                        }

                        break outerloop;
                    } else if (check.equalsIgnoreCase("no")) {
                        break;
                    } else {
                        System.out.println("Please enter yes or no.");
                    }
                }
            }
        }
    }

    // organizes the select_category option
    public static void select_db(Scanner scanner, Database db, int type) throws SQLException {
        ResultSet rs;

        if (type == 0) {
            rs = db.select_statement("SELECT COUNT(category_name) AS length FROM category_names");
            rs.next();
            int number_of_categories = rs.getInt("length");

            if (number_of_categories == 0) {
                System.out.println("You have no note categories");
            } else {
                rs = db.select_statement("SELECT * FROM category_names");
                System.out.println("Your note categories are:");

                // helps format the category names with commas on one line
                int i = 1;

                while (rs.next()) {
                    if (i == number_of_categories) {
                        System.out.println(rs.getString("category_name"));
                    } else {
                        System.out.print(rs.getString("category_name") + ", ");
                    }

                    i++;
                }
            }

            handle_menu(scanner, db, 1);
        } else {
            rs = db.select_statement("SELECT category_name FROM note_categories WHERE selected = 1");

            if (rs.next()) {
                String note_category = rs.getString("category_name");

                // resets all values to false, ensuring all questions are currently incorrect
                db.modify_table("UPDATE note_cards_categories SET correct = 0 WHERE category_name LIKE" +
                        "\"" + note_category + "\"");

                outerloop:
                while (true) {
                    rs = db.select_statement("SELECT note_description, note_answer, note_id FROM " +
                            "note_cards_categories WHERE category_name LIKE \"" + note_category + "\" AND" +
                            " correct = 0");

                    while (rs.next()) {
                        String question = rs.getString("note_description");
                        String answer = rs.getString("note_answer");
                        int note_id = rs.getInt("note_id");

                        System.out.println("The question is: " + question);
                        System.out.println("Type your answer below or type skip to skip:");
                        String user_response = scanner.nextLine();

                        if (user_response.equalsIgnoreCase(answer) || user_response.equalsIgnoreCase("skip")) {
                            if (user_response.equalsIgnoreCase(answer)) {
                                System.out.println("Correct!");
                            } else {
                                System.out.println("Skipped!");
                            }

                            db.modify_table("UPDATE note_cards SET correct = 1 WHERE note_id = " + note_id);

                        } else {
                            System.out.println("Incorrect! The correct answer was " + answer);

                            while (true) {
                                // checks for computer errors -> allows user to mark answer as correct
                                System.out.println("Did you actually get this answer correct?\nEnter yes or no.");
                                user_response = scanner.nextLine();

                                if (user_response.equalsIgnoreCase("yes")) {
                                    db.modify_table("UPDATE note_cards SET correct = 1 WHERE note_id = " + note_id);
                                    break;
                                } else if (user_response.equalsIgnoreCase("no")) {
                                    break;
                                } else {
                                    System.out.println("Invalid response");
                                }

                            }
                        }

                    }

                    rs = db.select_statement("SELECT * FROM note_cards_categories WHERE correct = 0 AND " +
                            "category_name LIKE \"" + note_category + "\"");

                    if (!rs.next()) {
                        System.out.println("You got every review question correct!");

                        while (true) {
                            // Updates the review date
                            System.out.println("Would you like to set a review date?\nEnter yes or no.");
                            String user_response = scanner.nextLine();

                            if (user_response.equalsIgnoreCase("no")) {
                                break;
                            } else if (user_response.equalsIgnoreCase("yes")) {

                                while (true) {
                                    System.out.println("How many days from now would you like to review?");
                                    user_response = scanner.nextLine();

                                    try {
                                        int days_to_review = Integer.parseInt(user_response);
                                        db.modify_table("UPDATE note_categories SET review_date = DATE_ADD(" +
                                                "CURRENT_DATE(),INTERVAL " + days_to_review + " DAY) WHERE" +
                                                " category_name LIKE \"" + note_category + "\"");
                                        System.out.println("Review date updated!");
                                        break;
                                    } catch (NumberFormatException e) {
                                        System.out.println("You didn't enter an integer value (enter in numeric form)");
                                    }
                                }

                                break;

                            } else {
                                System.out.println("Please enter yes or no.");
                            }
                        }


                        // ensures no note categories are selected and all note_cards are reset
                        db.modify_table("UPDATE note_categories SET selected = 0");
                        break;
                    }

                    // allows user to repeat until all questions have been solved correctly
                    while (true) {
                        System.out.println("Continue reviewing incorrect note cards?\nEnter yes or no.");
                        String user_response = scanner.nextLine();

                        if (user_response.equalsIgnoreCase("no")) {
                            db.modify_table("UPDATE note_categories SET selected = 0");
                            break outerloop;
                        } else if (!user_response.equalsIgnoreCase("yes")) {
                            System.out.println("Please enter yes or no.");
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    // organizes the checking of categories in the database
    public static void check_category(Scanner scanner, Database db, int id) throws SQLException {
        String choice, query, response;

        if (id == 0) {
            choice = "What note category would you like to add to? (type quit to go back)";
        } else {
            choice = "What note category would you like to review? (type quit to go back)";
        }

        while (true) {
            System.out.println(choice);
            String note_category = scanner.nextLine();

            if (note_category.equalsIgnoreCase("quit")) {
                // allows add_db to quit out if a quit was selected
                db.modify_table("ALTER TABLE note_cards ALTER category_id SET DEFAULT -1");
                break;
            }

            if (id == 0) {
                query = "SELECT category_name FROM note_categories WHERE category_name LIKE \"" + note_category + "\"";
                response = "There is no note category with that name";
            } else {
                query = "SELECT * FROM note_cards_categories WHERE category_name LIKE \"" + note_category + "\"";
                response = "Either there is no note category with that name or the category is empty";
            }

            ResultSet rs = db.select_statement(query);

            if (!rs.next()) {
                System.out.println(response);
            } else {
                if (id == 0) {
                    // updates default value of note_category so whenever you add a note card the selected
                    // note category is already the default of the table (no need to store the value)
                    rs = db.select_statement("SELECT category_id FROM note_categories WHERE category_name " +
                            "LIKE \"" + note_category + "\"");
                    rs.next();
                    int category_id = rs.getInt("category_id");

                    db.modify_table("ALTER TABLE note_cards ALTER category_id SET DEFAULT " + category_id);
                } else {
                    db.modify_table("UPDATE note_categories SET selected = 1 WHERE category_name =  \"" +
                            note_category + "\"");
                }

                break;
            }
        }
    }
}
