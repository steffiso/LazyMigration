/* Generated By:JavaCC: Do not edit this line. ParserForPut.java */
package parserPutToDatalog;
import java.io.IOException;
import database.Database;
import java.util.ArrayList;
import java.util.InputMismatchException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import database.Schema;

public class ParserForPut implements ParserForPutConstants {
  private static int schemaVersion = 0;

  private static ArrayList < String > attributes = null;

  private static int counter = 0;

  private static int length = 0;

  private static int ts = 0;

  private static boolean hasTS = false;

  private static Database db;

  private static void getSchema(String kind, int number) throws InputMismatchException, JsonParseException, JsonMappingException, IOException
  {
    Schema schema = null;
    if (number == 0)
    {
      schema = db.getLatestSchema(kind);
    }
    else schema = db.getSchema(kind, number);
    if (schema != null)
    {
      attributes = schema.getAttributes();
      length = attributes.size();
      schemaVersion = schema.getSchemaversion();
    }
  }

  private static int getLastTS() throws JsonParseException, JsonMappingException, IOException
  {
    ts = db.getLastTimestamp();
    return ts;
  }

  final public String start(Database db) throws ParseException, InputMismatchException, JsonParseException, JsonMappingException, IOException {
  String value = null;
  counter = 0;
  this.db = db;
  hasTS = false;
  schemaVersion = 0;
  attributes = null;
  length = 0;
  ts = 0;
    value = getJSONString();
    jj_consume_token(0);
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public String startJSON(Database db) throws ParseException, InputMismatchException, JsonParseException, JsonMappingException, IOException {
  String value = null;
  counter = 0;
  this.db = db;
    value = getJSON();
    jj_consume_token(0);
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public String getJSONString() throws ParseException, InputMismatchException, JsonParseException, JsonMappingException, IOException {
  Token kind = null;
  Token schemaToken = null;
  String value = null;
  boolean testOverflow = false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case put:
      jj_consume_token(put);
    testOverflow = true;
      break;
    default:
      jj_la1[0] = jj_gen;
      ;
    }
    kind = jj_consume_token(kindValue);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case number:
      schemaToken = jj_consume_token(number);
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    if (schemaToken != null && testOverflow==false)
    {
      schemaVersion = Integer.parseInt(schemaToken.toString());
      getSchema(kind.toString(), schemaVersion);
    }
    else if (schemaToken != null && testOverflow==true)
    {
       {if (true) throw new InputMismatchException("no numbers for value of kind allowed");}
    }
    else getSchema(kind.toString(), 0);
    if (attributes == null)
    {
      {if (true) throw new InputMismatchException("no info for schema of " + kind.toString() + " found");}
    }
    jj_consume_token(16);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case nullValue:
    case string:
    case number:
      value = listOfValues("");
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
      jj_consume_token(18);
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    if (value == null) {if (true) throw new InputMismatchException("no attributes for " + kind.toString());}
    if (hasTS == false || (testOverflow && (counter > (length - 1)))) ts = getLastTS() + 1;
    String jsonString = "{\u005c"kind\u005c":\u005c"" + kind + "\u005c",\u005cn" + "\u005c"schemaversion\u005c":" + schemaVersion + ",\u005cn" + "\u005c"attributes\u005c":{" + value + "},\u005cn\u005c"ts\u005c":" + Integer.toString(ts) + "}";
    {if (true) return jsonString;}
    throw new Error("Missing return statement in function");
  }

  final public String getJSON() throws ParseException, InputMismatchException, JsonParseException, JsonMappingException, IOException {
  Token kind = null;
  Token schemaToken = null;
  String value = null;
    kind = jj_consume_token(kindValue);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case number:
      schemaToken = jj_consume_token(number);
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    if (schemaToken != null)
    {
      schemaVersion = Integer.parseInt(schemaToken.toString());
      getSchema(kind.toString(), schemaVersion);
    }
    else getSchema(kind.toString(), 0);
    if (attributes == null)
    {
      {if (true) throw new InputMismatchException("no info for schema of " + kind.toString() + " found");}
    }
    jj_consume_token(16);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case nullValue:
    case string:
    case number:
      value = listOfValues("");
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 18:
      jj_consume_token(18);
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    String jsonString = kind + "{" + value + ", \u005c"ts\u005c":" + Integer.toString(ts) + "}";
    {if (true) return jsonString;}
    throw new Error("Missing return statement in function");
  }

  final public String listOfValues(String value) throws ParseException {
  Token valueOfToken = null;
  String valueOfOtherToken = null;
  String valueOne = "";
  boolean isTS = false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case string:
      valueOfToken = jj_consume_token(string);
      break;
    case number:
      valueOfToken = jj_consume_token(number);
      break;
    case nullValue:
      valueOfToken = jj_consume_token(nullValue);
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    String name = null;
    if (counter < length)
    {
      if (valueOfToken.kind == string)
      {
        name = valueOfToken.toString();
        name = name.substring(1, name.length() - 1);
        name = "\u005c"" + name + "\u005c"";
      }
      else if (valueOfToken.kind == nullValue)
      {
        name = null;
      }
      else name = valueOfToken.toString();
      String attributename = attributes.get(counter);
      valueOne = "\u005c"" + attributename.substring(1, attributename.length()) + "\u005c":" + name;
      counter++;
    }
    else if (counter == length)
    {
      if (!(valueOfToken.kind == string))
      {
        isTS = true;
        hasTS = true;
        ts = Integer.parseInt(valueOfToken.toString());
      }
      counter++;
    }
    else
    {
      counter++;
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 19:
        ;
        break;
      default:
        jj_la1[8] = jj_gen;
        break label_1;
      }
      jj_consume_token(19);
      valueOfOtherToken = listOfValues(value);
    }
    if (isTS) {if (true) return null;}
    String nullValues = "";
    if (valueOfOtherToken != null && !valueOfOtherToken.equals(""))
    {
      if (counter < length)
      {
        for (int i = counter; i < length; i++)
        {
          String attributename = attributes.get(i);
          name = null;
          nullValues = nullValues + "\u005c"" + attributename.substring(1, attributename.length()) + "\u005c":" + name + ",";
        }
        nullValues = nullValues.substring(0, nullValues.length() - 1);
      }
      if (nullValues.equals(""))
      {
        {if (true) return value + valueOne + ", " + valueOfOtherToken;}
      }
      else {if (true) return value + valueOne + ", " + valueOfOtherToken + "," + nullValues;}
    }
    else
    {
      if (counter < length)
      {
        for (int i = counter; i < length; i++)
        {
          String attributename = attributes.get(i);
          name = null;
          nullValues = nullValues + "\u005c"" + attributename.substring(1, attributename.length()) + "\u005c":" + name + ",";
        }
        nullValues = nullValues.substring(0, nullValues.length() - 1);
      }
      if (nullValues.equals(""))
      {
        {if (true) return value + valueOne;}
      }
      else {if (true) return value + valueOne + "," + nullValues;}
    }
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ParserForPutTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[9];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x40,0x1000,0x1c00,0x40000,0x1000,0x1c00,0x40000,0x1c00,0x80000,};
   }

  /** Constructor with InputStream. */
  public ParserForPut(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ParserForPut(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserForPutTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ParserForPut(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserForPutTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ParserForPut(ParserForPutTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserForPutTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 9; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[20];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 9; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 20; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
