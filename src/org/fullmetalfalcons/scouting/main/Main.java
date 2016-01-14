package org.fullmetalfalcons.scouting.main;

import com.dd.plist.NSDictionary;
import org.fullmetalfalcons.scouting.elements.Element;
import org.fullmetalfalcons.scouting.equations.Equation;
import org.fullmetalfalcons.scouting.exceptions.ElementParseException;
import org.fullmetalfalcons.scouting.fileio.Reader;
import org.fullmetalfalcons.scouting.fileio.Writer;
import org.fullmetalfalcons.scouting.teams.Team;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class
 *
 * Created by Dan on 1/11/2016.
 */
public class Main {

    private static final ArrayList<Element> ELEMENTS = new ArrayList<>();
    private static final ArrayList<Team> TEAMS = new ArrayList<>();
    private static final ArrayList<Equation> EQUATIONS = new ArrayList<>();
    //Console spam
    private static final boolean DEBUG = false;

    public static void main(String args[]){
        try {
            //Program needs to be told where to look for the plist files
            if(args.length==0){
                sendError("You have not provided a location for plists");
                System.exit(-1);
            }

            log("Program Starting");
            log("Starting to load configuration");

            //Populates ELEMENTS ArrayList from configuration file
            Reader.loadConfig();

            log(ELEMENTS.size() + " elements loaded");

            log("Starting to load plists");

            //Populates TEAMS ArrayList from plist files, passes location of plists
            Reader.loadPlists(args[0]);

            log(TEAMS.size() + " teams loaded");

            log("Starting to write file");

            //Writes data to Excel spreadsheet
            Writer.write();

            //Asks user if they would like to open the Excel workbook
            exitDialogue();

            log("Exiting program");

            System.exit(0);
        } catch(Exception e){
            e.printStackTrace();
            sendError("Unknown error occurred: " + e.toString());
            System.exit(-1);
        }



    }

    private static void exitDialogue() throws IOException {
        int result = JOptionPane.showConfirmDialog(null, "Results have been saved to \"results.xlsx.\" Would you like to open the workbook now?",
                "Done!", JOptionPane.YES_NO_OPTION);
        if (result==JOptionPane.YES_OPTION){
            try {
                Desktop.getDesktop().open(new File(Writer.FILENAME));
            }catch (IllegalArgumentException e){
                sendError("Congratulations! You managed damage/lose the results file in the time since it was made!");
            }
        }
    }

    /**
     * Creates an Element from a line provided from the config file
     *
     * @param line The line from the config file
     */
    public static void addElement(String line){

        try {
            //If something is wrong with "line" the new element will thrown an ElementParseException
            Element e = new Element(line);
            debug("Element of type " + e.getType().toString() + " created");
            ELEMENTS.add(e);
        } catch (ElementParseException e) {
            sendError("Config error: " + e.getMessage());
        }
    }

    /**
     * Creates a Team Object which essentially just holds an NSDictionary
     *
     * @param dictionary Holds the key/value pairs for the team
     */
    public static void addTeam(NSDictionary dictionary){
        Team t = new Team(dictionary);
        debug("Team " + t.getValue(Team.NUMBER_KEY) + " loaded");
        TEAMS.add(t);
    }

    public static void addEquation(String line){
        Equation e = new Equation(line);
        debug("Equation " + line + " added");
        EQUATIONS.add(e);

    }

    public static ArrayList<Element> getElements(){
        return ELEMENTS;
    }

    public static ArrayList<Team> getTeams() {
        return TEAMS;
    }

    /**
     * Sends an error message to the user with a JOptionPane
     *
     * @param message error message to send
     */
    public static void sendError(String message){
        try {
            JOptionPane.showMessageDialog(null, message,
                    "You done messed up", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "An error occurred while displaying an error",
                    "Yo Dawg!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void debug(String message){
        if (DEBUG){
            System.out.println(message);
        }
    }

    public static void log(String message){
        System.out.println(message);
    }

    public static ArrayList<Equation> getEquations() {
        return EQUATIONS;
    }
}
