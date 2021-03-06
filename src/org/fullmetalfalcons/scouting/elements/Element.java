package org.fullmetalfalcons.scouting.elements;

import org.fullmetalfalcons.scouting.exceptions.ElementParseException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class holds information on each individual element including its type, descriptions, arguments, and keys
 * Holds data from the config file
 *
 * For more information about why data is parsed in a certain way, see the config file.
 *
 * Created by Dan on 1/11/2016.
 */
public class Element {

    private ElementType type;
    private String[] descriptions;
    private String[] keys;
    private String[] arguments;
    private String[] values;

    //The below elements are used to capture the data between arrows <Like this>
    private final String argumentRegex = "<(.*?)>";
    private final Pattern argumentPattern = Pattern.compile(argumentRegex);


    /**
     * Constructor for the Element Class, requires a line from the config file
     *
     * @param line A line read from the config file
     * @throws ElementParseException If there is a problem with the line/ it does not conform to conventions
     */
    public Element(String line) throws ElementParseException {
        parseString(line);

    }

    /**
     * Breaks the line apart and assigns its parts to different variables.
     *
     * @param line Line to be parsed
     * @throws ElementParseException If the line to be parsed is malformed
     */
    private void parseString(String line) throws ElementParseException {
        //Arguments in the config file should be separated by ";;"
        String[] splitLine = line.split(";;");


        Matcher argumentMatcher = argumentPattern.matcher(splitLine[0]);
        //Checks to see if there is any information <like this> in the first portion of the line
        if (argumentMatcher.find()){
            //Group 0 includes <>, Group 1 just has the information inside <>
            arguments = argumentMatcher.group(1).split(",");
            for (int i = 0; i<arguments.length;i++){
                arguments[i]=arguments[i].trim();
            }
        }

        //Retrieves the ElementType based on the portion of the first section not in <> i.e. LABEL
        type = ElementType.getElement(splitLine[0].replaceAll(argumentRegex,"").trim());
        if (type==null){
            throw new ElementParseException("Element Type not recognized: " + splitLine[0].replaceAll(argumentRegex,""));
        }


        //Labels don't have keys, so they need to be parsed differently
        if (type!=ElementType.LABEL && type!=ElementType.SPACE){
            //Get Descriptions
            String descriptions = splitLine[1];
            String[] descriptionList = descriptions.split(",");
            for (int i = 0; i<descriptionList.length;i++){
                descriptionList[i]=descriptionList[i].trim();
            }
            this.descriptions = descriptionList;

            //Get keys
            String keys = splitLine[2];
            String[] keyList = keys.split(",");
            for (int i = 0; i<keyList.length;i++){
                keyList[i]=keyList[i].trim();
            }
            this.keys = keyList;

            //Cannot get any information from SPACE
        } else if (type==ElementType.LABEL){
            descriptions = new String[1];
            descriptions[0] = splitLine[1].trim();
        }



    }

    /**
     *Simple getter
     *
     * @return Type of Element
     */
    public ElementType getType() {
        return type;
    }

    /**
     *
     * @return Array containing arguments
     */
    public String[] getArguments(){
        return arguments;
    }

    /**
     * Simple getter
     *
     * @return Array containing descriptions
     */
    public String[] getDescriptions() {
        return descriptions;
    }

    /**
     * Simple getter
     *
     * @return Array containing keys
     */
    public String[] getKeys() {
        return keys;
    }
    
    public String[] getColumnValues(){
        if (values!=null){
            return values;
        }
        ArrayList<String> values = new ArrayList<>();
        switch(type){
            case SEGMENTED_CONTROL:
                for (String s: arguments){
                    values.add(keys[0]+"_"+s.trim());
                }
                break;
            case TEXTFIELD:
                if (arguments[0].equalsIgnoreCase("number") || arguments[0].equalsIgnoreCase("decimal")){
                    values.add(keys[0].trim());
                }
                break;
            case STEPPER:
                values.add(keys[0].trim());
                break;
            case LABEL:
                break;
            case SWITCH:
                for (String key: keys){
                    values.add(key.trim()+"_yes");
                    values.add(key.trim()+"_no");
                }
                break;
            case SPACE:
                break;
            case SLIDER:
                values.add(keys[0]);
                break;
        }

        for (int i = 0; i< values.size();i++){
            values.set(i, values.get(i)
                    .replace("\\","_")
                    .replace("/","_")
                    .replace(" ","_")
                    .toLowerCase()
                    .trim());
        }

        this.values = values.toArray(new String[values.size()]);
        
        return this.values;
    }
}
