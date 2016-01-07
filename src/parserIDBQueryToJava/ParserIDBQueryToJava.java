/* ParserIDBQueryToJava.java */
/* Generated By:JavaCC: Do not edit this line. ParserIDBQueryToJava.java */
package parserIDBQueryToJava;
import java.util.ArrayList;
import bottomUp.Relation;
import bottomUp.Query;
import bottomUp.Condition;
import bottomUp.Pair;

public class ParserIDBQueryToJava implements ParserIDBQueryToJavaConstants {

  final public ArrayList < Query > start() throws ParseException {ArrayList < Query > querys = new ArrayList < Query > ();
  Pair p = null;
  Relation leftRelation = null;
  Query q = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case not:
    case kindValue:{
      leftRelation = getRelation();
      jj_consume_token(13);
      p = getRelationList();
q = new Query(leftRelation, p.relations);
      if (!p.conditions.isEmpty()) q.setConditions(p.conditions);
      querys.add(q);
      jj_consume_token(14);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case not:
        case kindValue:{
          ;
          break;
          }
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        leftRelation = getRelation();
        jj_consume_token(13);
        p = getRelationList();
q = new Query(leftRelation, p.relations);
          if (!p.conditions.isEmpty()) q.setConditions(p.conditions);
          querys.add(q);
        jj_consume_token(14);
      }
      break;
      }
    default:
      jj_la1[1] = jj_gen;
      ;
    }
    jj_consume_token(0);
{if ("" != null) return querys;}
    throw new Error("Missing return statement in function");
  }

  final public Pair getRelationList() throws ParseException {ArrayList < Relation > values = new ArrayList < Relation > ();
  ArrayList < Condition > conditions = new ArrayList < Condition > ();
  Relation relation = null;
  Condition condition = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case variable:
    case string:
    case number:
    case not:
    case kindValue:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case not:
      case kindValue:{
        relation = getRelation();
values.add(relation);
        break;
        }
      case variable:
      case string:
      case number:{
        condition = getCondition();
conditions.add(condition);
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case 15:{
          ;
          break;
          }
        default:
          jj_la1[3] = jj_gen;
          break label_2;
        }
        jj_consume_token(15);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case not:
        case kindValue:{
          relation = getRelation();
values.add(relation);
          break;
          }
        case variable:
        case string:
        case number:{
          condition = getCondition();
conditions.add(condition);
          break;
          }
        default:
          jj_la1[4] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
      break;
      }
    default:
      jj_la1[5] = jj_gen;
      ;
    }
{if ("" != null) return new Pair(values, conditions);}
    throw new Error("Missing return statement in function");
  }

  final public Relation getRelation() throws ParseException {Token kind = null;
  String value = null;
  ArrayList < String > values = new ArrayList < String > ();
  Relation relation = null;
  boolean isNot = false;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case not:{
      jj_consume_token(not);
isNot = true;
      break;
      }
    default:
      jj_la1[6] = jj_gen;
      ;
    }
    kind = jj_consume_token(kindValue);
    jj_consume_token(16);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case variable:
    case string:
    case number:{
      value = getValue();
values.add(value);
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case 15:{
          ;
          break;
          }
        default:
          jj_la1[7] = jj_gen;
          break label_3;
        }
        jj_consume_token(15);
        value = getValue();
values.add(value);
      }
      break;
      }
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    jj_consume_token(17);
relation = new Relation(kind.toString(), values.size(), values);
    if (isNot) relation.setNot(true);
    {if ("" != null) return relation;}
    throw new Error("Missing return statement in function");
  }

  final public Condition getCondition() throws ParseException {String right = null;
  String left = null;
  Token operator = null;
    left = getValue();
    operator = jj_consume_token(operators);
    right = getValue();
Condition con = new Condition(left, right, operator.toString());
    {if ("" != null) return con;}
    throw new Error("Missing return statement in function");
  }

  final public String getValue() throws ParseException {Token valueOfToken = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case variable:{
      valueOfToken = jj_consume_token(variable);
      break;
      }
    case number:{
      valueOfToken = jj_consume_token(number);
      break;
      }
    case string:{
      valueOfToken = jj_consume_token(string);
      break;
      }
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return valueOfToken.toString();}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ParserIDBQueryToJavaTokenManager token_source;
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
      jj_la1_0 = new int[] {0xc00,0xc00,0xd60,0x8000,0xd60,0xd60,0x400,0x8000,0x160,0x160,};
   }

  /** Constructor with InputStream. */
  public ParserIDBQueryToJava(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ParserIDBQueryToJava(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserIDBQueryToJavaTokenManager(jj_input_stream);
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
  public ParserIDBQueryToJava(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserIDBQueryToJavaTokenManager(jj_input_stream);
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
  public ParserIDBQueryToJava(ParserIDBQueryToJavaTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ParserIDBQueryToJavaTokenManager tm) {
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

  private int jj_ntk_f() {
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
    boolean[] la1tokens = new boolean[18];
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
    for (int i = 0; i < 18; i++) {
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