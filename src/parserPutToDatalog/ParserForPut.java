/* Generated By:JavaCC: Do not edit this line. ParserForPut.java */
package parserPutToDatalog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import database.Database;
import java.util.ArrayList;
import database.Schema;

public class ParserForPut implements ParserForPutConstants {
  private static int schemaVersion = 0;

  private static int lastTS = 0;

  private static ArrayList < String > attributes = null;

  private static int zaehler = 0;

  private static int laenge = 0;

  private static void getSchema(String kind, int number)
  {
    Schema schema = null;
    Database db = new Database();
    if (number == 0)
    {
      schema = db.getLatestSchema(kind);
    }
    else schema = db.getSchema(kind, number);
    attributes = schema.getAttributes();
    laenge = attributes.size();
    lastTS = db.getLastTimestamp();
    schemaVersion = schema.getSchemaversion();
  }

  final public String start() throws ParseException {
  String value = null;
  zaehler = 0;
    value = getJSONString();
    jj_consume_token(0);
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public String start2() throws ParseException {
  String value = null;
  zaehler = 0;
    value = getJSON();
    jj_consume_token(0);
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public String getJSONString() throws ParseException {
  Token kind = null;
  Token schemaToken = null;
  String value = null;
  int ts;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case put:
      jj_consume_token(put);
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
    if (schemaToken != null)
    {
      schemaVersion = Integer.parseInt(schemaToken.toString());
      getSchema(kind.toString(), schemaVersion);
    }
    else
    getSchema(kind.toString(), 0);
    jj_consume_token(15);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case string:
    case number:
      value = listOfValues("");
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(16);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 17:
      jj_consume_token(17);
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    ts = lastTS + 1;
    String jsonString = "{\u005c"kind\u005c":\u005c"" + kind + "\u005c",\u005cn" + "\u005c"schemaversion\u005c":" + schemaVersion + ",\u005cn" + "\u005c"attributes\u005c":{" + value + "},\u005cn\u005c"ts\u005c":" + Integer.toString(ts) + "}";
    {if (true) return jsonString;}
    throw new Error("Missing return statement in function");
  }

  final public String getJSON() throws ParseException {
  Token kind = null;
  Token schemaToken = null;
  String value = null;
  int ts;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case put:
      jj_consume_token(put);
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    kind = jj_consume_token(kindValue);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case number:
      schemaToken = jj_consume_token(number);
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    if (schemaToken != null)
    {
        schemaVersion = Integer.parseInt(schemaToken.toString());
        getSchema(kind.toString(), schemaVersion);
    }
    else
        getSchema(kind.toString(), 0);
    jj_consume_token(15);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case string:
    case number:
      value = listOfValues("");
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    jj_consume_token(16);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 17:
      jj_consume_token(17);
      break;
    default:
      jj_la1[7] = jj_gen;
      ;
    }
    ts = lastTS + 1;
    String jsonString = kind +"{"+ value +", ts\u005c":" + Integer.toString(ts) + "}";
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
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    String name = null;
    if (valueOfToken.kind == string)
    {
      name = valueOfToken.toString();
      name = name.substring(1, name.length() - 1);
      name = "\u005c"" + name + "\u005c"";
    }
    else name = "\u005c"" + valueOfToken.toString() + "\u005c"";
    if (zaehler < laenge)
    {
      String attributename = attributes.get(zaehler);
      valueOne = "\u005c"" + attributename.substring(1, attributename.length()) + "\u005c":" + name;
      zaehler++;
    }
    else if (zaehler == laenge)
    {
      isTS = true;
      lastTS=Integer.parseInt(valueOfToken.toString());
      zaehler++;
    }
    else
    {
      //Exception werfen, da mehr Attribute eingegeben wurden, als in Schema abgespeichert
      zaehler++;
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 18:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_1;
      }
      jj_consume_token(18);
      valueOfOtherToken = listOfValues(value);
    }
    if (isTS) {if (true) return null;}
    if (valueOfOtherToken != null) {if (true) return value + valueOne + ", " + valueOfOtherToken;}
    else {if (true) return value + valueOne;}
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
  final private int[] jj_la1 = new int[10];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x40,0x800,0xc00,0x20000,0x40,0x800,0xc00,0x20000,0xc00,0x40000,};
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
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ParserForPut(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserForPutTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ParserForPut(ParserForPutTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserForPutTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
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
    boolean[] la1tokens = new boolean[19];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 10; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 19; i++) {
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
