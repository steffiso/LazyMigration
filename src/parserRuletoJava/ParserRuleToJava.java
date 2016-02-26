/* Generated By:JavaCC: Do not edit this line. ParserRuleToJava.java */
package parserRuletoJava;
import java.util.ArrayList;
import datalog.Predicate;
import datalog.Rule;
import datalog.Condition;
import datalog.RuleBody;
import database.Database;
import database.Schema;
//import java.util.SortedMap;
//import java.util.TreeMap;
import java.util.LinkedHashMap;

public class ParserRuleToJava implements ParserRuleToJavaConstants {
  public ArrayList < String > getSchema(String kind, int schemaNumber)
  {
    Database db = new Database();
    Schema currentSchema = db.getSchema(kind, schemaNumber);
    if (currentSchema != null)
        return currentSchema.getAttributes();
    else
        return null;
  }

  public String getAttributeName(String kind, int schemaNumber, int pos)
  {
    Database db = new Database();
    Schema currentSchema = db.getSchema(kind, schemaNumber);
    if (currentSchema != null)
    {
        ArrayList<String > attributes = currentSchema.getAttributes();
        String value = attributes.get(pos);
        return value;
   }
    else
        return null;

  }

  final public ArrayList < Rule > start() throws ParseException {
  ArrayList < Rule > querys = new ArrayList < Rule > ();
  RuleBody p = null;
  Predicate leftRelation = null;
  Rule q = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case not:
    case kindValue:
    case 16:
      leftRelation = getRelation();
      jj_consume_token(13);
      p = getRelationList();
      q = new Rule(leftRelation, p);
      //if (!p.conditions.isEmpty()) q.setConditions(p.conditions);
      querys.add(q);
      jj_consume_token(14);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case not:
        case kindValue:
        case 16:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        leftRelation = getRelation();
        jj_consume_token(13);
        p = getRelationList();
          q = new Rule(leftRelation, p);
          //if (!p.conditions.isEmpty()) q.setConditions(p.conditions);
          querys.add(q);
        jj_consume_token(14);
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    jj_consume_token(0);
    {if (true) return querys;}
    throw new Error("Missing return statement in function");
  }

  final public RuleBody getRelationList() throws ParseException {
  ArrayList < Predicate > values = new ArrayList < Predicate > ();
  ArrayList < Condition > conditions = new ArrayList < Condition > ();
  Predicate predicate = null;
  Condition condition = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case variable:
    case string:
    case number:
    case not:
    case kindValue:
    case 16:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case not:
      case kindValue:
      case 16:
        predicate = getRelation();
          values.add(predicate);
        break;
      case variable:
      case string:
      case number:
        condition = getCondition();
          conditions.add(condition);
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_2;
        }
        jj_consume_token(15);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case not:
        case kindValue:
        case 16:
          predicate = getRelation();
            values.add(predicate);
          break;
        case variable:
        case string:
        case number:
          condition = getCondition();
            conditions.add(condition);
          break;
        default:
          jj_la1[4] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    {if (true) return new RuleBody(values, conditions);}
    throw new Error("Missing return statement in function");
  }

  final public Predicate getRelation() throws ParseException {
  Token kind = null;
  Token schemaToken = null;
  String value = null;
  String attribute = null;
  ArrayList < String > scheme = null;
  //SortedMap <String, String > values = null;
  LinkedHashMap < String, String > values = null;
  Predicate predicate = null;
  ArrayList < String > schema = null;
  boolean isNot = false;
  boolean schemaExists = true;
  boolean isHead = false;
    //values = new TreeMap <String, String > ();
    values = new LinkedHashMap < String, String > ();
    scheme = new ArrayList < String > ();
    int pos = 0;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 16:
      jj_consume_token(16);
    isHead = true;
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case not:
      jj_consume_token(not);
    isNot = true;
      break;
    default:
      jj_la1[7] = jj_gen;
      ;
    }
    kind = jj_consume_token(kindValue);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case number:
      schemaToken = jj_consume_token(number);
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    schema = getSchema(kind.toString(), Integer.parseInt(schemaToken.toString()));
    if (schema == null)
    {
      schema = new ArrayList < String > ();
      schemaExists = false;
    }
    jj_consume_token(17);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case variable:
    case string:
    case number:
      value = getValue();
      scheme.add(value);
//      if (value.startsWith("?"))
//      {
//        if (!schemaExists)
//        {
//          schema.add(value);
//        }
//        attribute = value;
//        value = "";
//      }
//      else attribute = "";
//      //attribute = getAttributeName(kind.toString(), Integer.parseInt(schemaToken.toString()), pos);
//      {
//        values.put(attribute, value);
//      }
//      pos++;

      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 15:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_3;
        }
        jj_consume_token(15);
        value = getValue();
          scheme.add(value);
//          if (value.startsWith("?"))
//          {
//            if (!schemaExists)
//            {
//              schema.add(value);
//            }
//            attribute = value;
//            value = "";
//          }
//          else attribute = "";
//          //attribute = getAttributeName(kind.toString(), Integer.parseInt(schemaToken.toString()), pos);
//          {
//            values.put(attribute, value);
//          }
//          pos++;

      }
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    jj_consume_token(18);
    predicate = new Predicate(kind.toString() + schemaToken.toString(), scheme.size(), scheme);
    if (isNot) predicate.setNot(true);
    predicate.setHead(isHead);
    {if (true) return predicate;}
    throw new Error("Missing return statement in function");
  }

  final public Condition getCondition() throws ParseException {
  String right = null;
  String left = null;
  Token operator = null;
    left = getValue();
    operator = jj_consume_token(operators);
    right = getValue();
    Condition con = new Condition(left, right, operator.toString());
    {if (true) return con;}
    throw new Error("Missing return statement in function");
  }

  final public String getValue() throws ParseException {
  Token valueOfToken = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case variable:
      valueOfToken = jj_consume_token(variable);
      break;
    case number:
      valueOfToken = jj_consume_token(number);
      break;
    case string:
      valueOfToken = jj_consume_token(string);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return valueOfToken.toString();}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ParserRuleToJavaTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[12];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x10c00,0x10c00,0x10d60,0x8000,0x10d60,0x10d60,0x10000,0x400,0x100,0x8000,0x160,0x160,};
   }

  /** Constructor with InputStream. */
  public ParserRuleToJava(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ParserRuleToJava(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserRuleToJavaTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ParserRuleToJava(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserRuleToJavaTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ParserRuleToJava(ParserRuleToJavaTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserRuleToJavaTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 12; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 12; i++) {
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
