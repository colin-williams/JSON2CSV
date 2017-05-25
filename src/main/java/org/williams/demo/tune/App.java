package org.williams.demo.tune;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.LinkedHashSet;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonReader;


/**
 * Tune Exercise -  A program which reads a JSON formatted array of objects and outputs a CSV file
 */
public class App {
  //alternatively we could use strings instead.
  static final char[] trueCharArray = {'t', 'r', 'u', 'e'};
  static final char[] falseCharArray = {'f', 'a', 'l', 's', 'e'};

  public static void main(String[] args) {
    validate(args);
    App app = new App();
    JsonStructure jsonStructure = app.readJSON(args[0]);
    JsonArray jsonArray = app.readJSONArray(jsonStructure);
    LinkedHashSet<String> keySet = app.getTotalKeyset(jsonArray);
    app.writeToFile(jsonArray, keySet, args[1]);
  }

  /**
   * Validate arguments and attempt to check file creation before processing
   *
   * @param args
   */

  public static void validate(String[] args) {
    if (args.length != 2) {
      usage();
      System.exit(3);
    }
    File outputFile = new File(args[1]);
    try {
      outputFile.createNewFile();
    } catch (IOException ioe) {
      System.err.println("Cannot write file" + args[1] + ", exiting");
      System.exit(4);
    }
  }

  public static void usage() {
    System.out.printf("%n A program which reads a JSON formatted array of objects and outputs a CSV file %n%n%n " +
            "Usage:" +
            "App array.json output.csv%n%n");
  }

  /**
   * Reads a JsonStructure and returns a JsonArray
   *
   * @param structure - the structure we wish to cast to JSONArray
   * @return JsonArray
   */
  public JsonArray readJSONArray(JsonStructure structure) {
    JsonValue.ValueType value = structure.getValueType();
    if (value.equals(JsonValue.ValueType.ARRAY)) return (JsonArray) structure;
    else throw new IllegalArgumentException("The provided file is of type:" + value + " and not JsonArray");
  }

  /**
   * Reads a file and returns a JsonStructure...
   *
   * @param filename - a string which represents the filepath.
   * @return JsonStructure
   */

  public JsonStructure readJSON(String filename) {
    // Don't like setting null!
    JsonReader jsonReader = null;
    JsonStructure jsonStructure = null;
    try {
      jsonReader = Json.createReader(new FileReader(filename));
      jsonStructure = jsonReader.read();
      jsonReader.close();
      return jsonStructure;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(255);
    } finally {
      if (jsonReader != null) jsonReader.close();
    }
    return jsonStructure;
  }

  /**
   * Retrieves keys from a JsonArray of JavaScript objects and returns the KeySet for all the objects combined
   *
   * @param jsonArray
   * @return LinkedHashSet<String>
   */

  public LinkedHashSet<String> getTotalKeyset(JsonArray jsonArray) {
    LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>();
    for (JsonValue jsonValue : jsonArray) {
      // We are told that the array is of javascript objects and valid JSON, so this check isn't necessary
      if (!jsonValue.getValueType().equals(JsonValue.ValueType.OBJECT)) {
        throw new IllegalArgumentException("The JSON array has an element of non object type.");
      } else {
        linkedHashSet.addAll(((JsonObject) jsonValue).keySet());
      }
    }
    return linkedHashSet;
  }

  public void writeToFile(JsonArray jsonArray, LinkedHashSet<String> keySet, String filename) {
    try {
      FileWriter fileWriter = new FileWriter(filename);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

      int size = keySet.size();
      int counter = 0;
      for (String key : keySet) {
        counter++;
        bufferedWriter.append(key);
        if (counter < size) {
          bufferedWriter.append(',');
        }
      }
      bufferedWriter.newLine();
      for (JsonValue jsonValue : jsonArray) {
        this.writeObject((JsonObject) jsonValue, bufferedWriter, keySet);
      }
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(253);
    }
  }

  /**
   * Passes the string representation of a JavaScript object to a BufferedWriter
   *
   * @param jsonObject     the object we wish to write
   * @param bufferedWriter the writer which will write the jsonObject to file
   * @param totalKeyset    the set of all keys from all the objects in the array
   */
  public void writeObject(JsonObject jsonObject, BufferedWriter bufferedWriter, LinkedHashSet<String> totalKeyset)
          throws IOException {

    int size = totalKeyset.size();
    int counter = 0;
    for (String key : totalKeyset) {
      counter++;
      if (jsonObject.keySet().contains(key)) {
        JsonValue value = jsonObject.get(key);
        switch (value.getValueType()) {
          case STRING:
            //removing quotes from the string, to get output like the example. CSV format is not well defined.
            String quotesRemoved = value.toString();
            bufferedWriter.write(quotesRemoved.substring(1, quotesRemoved.length() - 1));
            break;
          case NUMBER:
            bufferedWriter.write(value.toString());
            break;
          case TRUE:
            bufferedWriter.write(trueCharArray);
            break;
          case FALSE:
            bufferedWriter.write(falseCharArray);
            break;
          // We could handle arrays and null types, but the spec tells us otherwise. Should I implement?
          default:
            throw new UnsupportedOperationException("A provided value JSON value type" + value.getValueType() +
                    "for value: " + value.toString() + "was not defined in the spec.");
        }
      }
      if (counter < size) {
        bufferedWriter.append(',');
      }
    }
    bufferedWriter.newLine();
  }
}
