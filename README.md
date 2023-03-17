# Quizzer 👨‍🏫
### Description ✏️:
Quizzer is a Java based application with SQL (through MySQL) for a database. It is a note review application I created to help me review my progamming notes and as a review for some of the skills I learned while teaching myself both Java and SQL (with of course help from BroCode). The reason I ended up on a note review application is because I often have issues with remebering to review my notes and don't really like Quizlet. There are a few obscure things about this note review application that I should talk about: you cannot delete notes or note categories and the UI is strictly in the command line. I did this because for me I don't see the point in deleting previous notes since I have a skip button for skipping old notes I already know (and I don't see the issue with a little more review) and there are double checks in each of the insertions into the database so I don't think I'll make a mistake in adding to my notes. Additionally although I could've used JavaSwing to aid the UI for my program I wanted to focus more on Java and SQL review and save a JavaSwing project for a future idea. However if I make an updated version of this project (as I believe I will in the future, although it may be in a different language) I would definately improve these two aspects because mistakes happen and a good UI makes the note review experience more enjoyable. Some challenges I faced when building this project were working with a SQL database in a Java Program becuase I had no previous knowledge of the Java JDBC and how to use it (thank you to luv2code on YouTube for some help). Additionally, the structure of the program: debating when to create certain functions versus simply writing the code within other already created functions often made me consider what was the best way to proceed. In addition, the structure of MySQL database and when to create views/stored procedures as well as properties of the columns often made me think.

#### Outline.txt 📋
For reference Outline.txt is a text file I used for both pre-planning and anaylsis of my program during the process, so feel free to check it out!

### How To Run the Program 🏃:
1. Build the Java Classes in whichever code-editor you like.
2. Build the SQL database in MySQL (following my reccomendations in Outline.txt)
3. Change the dbUrl, user, and pass values corresponding to your corresponding databaseUrl, username, and password for MySQL
4. Either build and run the project using the green arrow or in the commandline using Javac _.java (where _ is the name of your java classes) for both of your java classes and Java Quizzer
### ❗ Final Note ❗
If anyone uses this or sees errors in the code or ways to make improvements please let me know! I'm always looking for ways to improve.
