package a4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The main program of the A4 assignment. It reads an academic genealogy in CSV format and supports
 * various commands querying the genealogy tree.
 */
public class Main {
    /**
     * Name of the file to read commands from, or an empty string to signify reading from console.
     */
    public static String inputFile = "";
    /**
     * Name of the file to read the genealogy from
     */
    public static String csvFileName = "professors.csv";
    /**
     * The academic genealogy tree
     */
    public static PhDTree professorTree;

    /**
     * Effect: Print a usage message to standard error.
     */
    public static void usage() {
        System.err.println("Usage: a4.Main [--help] [-i <input script>] [filename.csv]");
    }

    public static void main(String[] args) {
        if (!processProgramArguments(args)) {
            usage();
            System.exit(1);
        }
        try {
            professorTree = csvToTree(csvFileName);
            processCommands();
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Effect: Parse the program arguments given in {@code args} and set global parameters like
     * {@code inputFile} and {@code csvFileName} appropriately.
     * Returns true iff all arguments were parsed and the flag "--help" was not encountered.
     */
    private static boolean processProgramArguments(String[] args) {
        int i;
        for (i = 0; i < args.length; i++) {
            if (args[i].equals("-i")) {
                if (i + 1 < args.length) {
                    inputFile = args[i + 1];
                    i++;
                } else {
                    return false;
                }
            } else if (args[i].equals("--help")) {
                return false;
            } else {
                break;
            }
        }
        if (i < args.length) {
            csvFileName = args[i];
            i++;
        }
        return (i == args.length);
    }

    /**
     * Returns a PhDTree representation of the CSV file named by filename. Throws an appropriate
     * IOException if there was an error reading the file.
     */
    public static PhDTree csvToTree(String filename)
            throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] header = reader.readLine().split(","); // skip header line
            if (!Arrays.equals(header,
                    new String[]{"advisee","year","advisor"})) {
                throw new IOException("Invalid file format");
            }

            // TODO 1
            String line = reader.readLine();
            String[] words = line.split(",");
            String[] name = words[0].split(" ");
            Professor root = new Professor(name[0], name[1]);
            PhDTree myTree = new PhDTree(root, Integer.parseInt(words[1]));
            line = reader.readLine();
            while(line != null){
                words = line.split(",");
                name = words[0].split(" ");
                String[] aName = words[2].split(" ");
                Professor newProf = new Professor(name[0], name[1]);
                Professor advisor = new Professor(aName[0], aName[1]);
                myTree.insert(advisor, newProf, Integer.parseInt(words[1]));
                line = reader.readLine();
            }
            return myTree;
        }
    }

    /**
     * Effect: Reads commands from a Scanner and executes them. Returns when the "exit" command is
     * read.
     */
    public static void processCommands() {
        Scanner sc;
        if (inputFile.isEmpty()) {
            sc = new Scanner(System.in);
        } else {
            try {
                sc = new Scanner(new File(inputFile));
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
                return;
            }
        }
        while (true) {
            if (inputFile.isEmpty()) {
                System.out.print("Please enter a command: ");
            }
            try {
                String input = sc.nextLine().trim();
                String[] cmd = input.split(" +");
                switch (cmd[0].toLowerCase()) {
                    case "help":
                        doHelp();
                        break;
                    case "contains":
                        doContains(cmd);
                        break;
                    case "size":
                        doSize(cmd);
                        break;
                    case "advisor":
                        doAdvisor(cmd);
                        break;
                    case "ancestor":
                        doAncestor(cmd);
                        break;
                    case "find":
                        doFind(cmd);
                        break;
                    case "lineage":
                        doLineage(cmd);
                        break;
                    case "print":
                        doPrint(cmd);
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.println(
                                "This is not a valid command. For help, enter the command \"help\"");
                }
            } catch (NoSuchElementException exc) {
                // no more lines on input
                return;
            }
        }
    }

    /**
     * Effect: Print a message about the command being invalid.
     */
    private static void invalidCommand(String cmd) {
        System.out.println("Invalid " + cmd + " command. " +
                "Enter the command \"help\" for information about that command.");
    }

    /** Effect: perform the help command */
    public static void doHelp() {
        System.out.println("\nhelp\n" +
                "contains <firstName> <lastName> : whether this professor is in the PhD tree\n"
                +
                "size <firstName> <lastName> : the number of nodes in the tree with the given professor at the root\n"
                +
                "advisor <firstName> <lastName> : the direct advisor of the given professor\n" +
                "ancestor <firstName1> <lastName1> <firstName2> <lastName2> : the common ancestor between the two given professors\n"
                +
                "find <firstName> <lastName> : the PhDTree with the given professor at the root\n"
                +
                "lineage <firstName> <lastName> : the sequence of professors that are related and come before this professor\n"
                +
                "print : prints out to the console the entire tree in your .csv file\n" +
                "exit : exit the program\n");
    }

    /**
     * Effect: perform the "contains" command, where cmd contains the words in the command.
     */
    public static void doContains(String[] cmd) {
        // TODO 2
        if (cmd.length !=  3){
            StringBuilder s = new StringBuilder();
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p = new Professor(cmd[1], cmd[2]);
        if (professorTree.contains(p)){
            System.out.println("This professor is contained in the PhD tree.");
        }
        else{
            System.out.println("This professor is not contained in the PhD tree.");
        }
    }

    /**
     * Effect: perform the "size" command, where cmd contains the words in the command.
     */
    public static void doSize(String[] cmd) {
        // TODO 3
        if (cmd.length !=  3 && cmd.length != 1){
            StringBuilder s = new StringBuilder();
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p;
        if (cmd.length == 1){
            p = professorTree.prof();
        }
        else {
            p = new Professor(cmd[1], cmd[2]);
        }
        try {
            PhDTree myTree = professorTree.findTree(p);
            int n = myTree.size();
            System.out.println("The number of nodes in this tree is: " + n + ".");
        }catch(NotFound exc){
            System.out.println("This person does not exist in the tree.");
        }

    }

    /**
     * Effect: perform the "advisor" command, where cmd contains the words in the command.
     */
    public static void doAdvisor(String[] cmd) {
        // TODO 4
        if (cmd.length !=  3){
            StringBuilder s = new StringBuilder();
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p = new Professor(cmd[1], cmd[2]);
        if (!professorTree.contains(p)){
            System.out.println("This professor does not exist in the tree.");
        }
        else{
            try {
                Professor advisor = professorTree.findAdvisor(p);
                System.out.println("The advisor of this advisee is: " + advisor + ".");
            }catch(NotFound exc){
                System.out.println("This person does not have an advisor.");
            }
        }
    }

    /**
     * Effect: perform the "ancestor" command, where cmd contains the words in the command.
     */
    public static void doAncestor(String[] cmd) {
        // TODO 5
        if (cmd.length !=  5){
            StringBuilder s = new StringBuilder();
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p1 = new Professor(cmd[1], cmd[2]);
        Professor p2 = new Professor(cmd[3], cmd[4]);
        try{
            Professor ancestor = professorTree.commonAncestor(p1, p2);
            System.out.println("The common ancestor of these scholars is: " + ancestor + ".");
        }catch(NotFound exc){
            System.out.println("These scholars do not have a common ancestor.");
        }
    }

    /**
     * Effect: perform the "find" command, where cmd contains the words in the command.
     */
    public static void doFind(String[] cmd) {
        // TODO 6
        if (cmd.length !=  3){
            StringBuilder s = new StringBuilder();
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p = new Professor(cmd[1], cmd[2]);
        try{
            PhDTree myTree = professorTree.findTree(p);
            System.out.println("The PhDTree with " + p + " at the root is " + myTree + ".");
        }catch(NotFound exc){
            System.out.println("This person does not exist in the tree.");
        }
    }

    /**
     * Effect: perform the "print" command, where cmd contains the words in the command.
     */
    public static void doPrint(String[] cmd) {
        // TODO 7
        if (cmd.length !=  1){
            invalidCommand(cmd[0]);
            return;
        }
        System.out.println(professorTree.toStringVerbose());
    }

    /**
     * Effect: perform the "lineage" command, where cmd contains the words in the command.
     */
    public static void doLineage(String[] cmd) {
        // TODO 8
        if (cmd.length !=  3){
            StringBuilder s = new StringBuilder(" ");
            for (String m:cmd){
                s.append(m).append(" ");
            }
            invalidCommand(s.toString());
            return;
        }
        Professor p = new Professor(cmd[1], cmd[2]);
        try{
            List<Professor> myList = professorTree.findAcademicLineage(p);
            System.out.print("The lineage is: ");
            System.out.print(myList.get(0));
            for (int i = 1; i < myList.size(); i++){
                System.out.print("--");
                System.out.print(myList.get(i));
            }
            System.out.println(".");
        }catch(NotFound exc){
            System.out.println("This person does not exist in the tree.");
        }
    }
}