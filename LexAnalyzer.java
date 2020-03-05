/**
 * 
This class is a lexical analyzer for the tokens defined by the grammar:

⟨letter⟩ → a | b | ... | z | A | B | ... | Z
⟨digit⟩ → 0 | 1 | ... | 9
⟨id⟩ → ⟨letter⟩ {⟨letter⟩ | ⟨digit⟩}
⟨int⟩ → [+|−] {⟨digit⟩}+
⟨float⟩ → [+|−] ( {⟨digit⟩}+ "." {⟨digit⟩}  |  "." {⟨digit⟩}+ )
⟨floatE⟩ → (⟨int⟩ | ⟨float⟩) (e|E) [+|−] {⟨digit⟩}+
⟨floatF⟩ → (⟨int⟩ | ⟨float⟩ | ⟨floatE⟩) ("f" | "F")
⟨add⟩ → +
⟨sub⟩ → −
⟨mul⟩ → *
⟨div⟩ → /
⟨lt⟩ → <
⟨le⟩ → "<="
⟨gt⟩ → >
⟨ge⟩ → ">="
⟨eq⟩ → =
⟨LParen⟩ → (
⟨RParen⟩ → )
⟨LBrace⟩ → {
⟨RBrace⟩ → }

This class implements a DFA that will accept the above tokens.

The DFA states are represented by the Enum type "State".
The DFA has the following 10 final states represented by enum-type literals:

state     token accepted

Id        identifiers
Int       integers
Float     floats without exponentiation part
FloatE    floats with exponentiation part
FloatF    auxiliary category of floats with exponentiation character part
Add       +
Sub       -
Mul       *
Div       /
LParen    (
RParen    )
LBrace    {}
RBrace    }

state 	  token extended

Lt,       <
Le,       <=     
Gt,       > 
Ge,       >= 
Eq,       =

The DFA also uses the following 4 non-final states:

state      	string recognized

Start      	the empty string
Dot        	float parts ending with "."
E          	float parts ending with E or e
EPlusMinus 	float parts ending with + or - in exponentiation part
**/

/**
 * 
The function "driver" operates the DFA. 

The function "nextState" returns the next state given the current state and the input character.
**/

public abstract class LexAnalyzer extends IO
{
	public enum State 
       	{ 
	  // non-final states     ordinal number - this are states that won't be return.

		Start,             // 0
		Dot,               // 1
		E,                 // 2
		EPlusMinus,        // 3

	  // final states		 This states will be return if found by the method nextState

		Id,                // 4
		Int,               // 5
		Float,             // 6
		FloatE,            // 7
		FloatF,            // 8
		Add,               // 9
		Sub,               // 10
		Mul,               // 11
		Div,               // 12

		LParen,            // 13
		RParen,            // 14
		LBrace,            // 15
		RBrace,            // 16

		Lt,                // 17
		Le,                // 18
		Gt,            	   // 19
		Ge,            	   // 20
		Eq,            	   // 21

	  //These keywords are not used as identifiers but are part of identifiers.
		Keyword_if,		   // 22
		Keyword_then,	   // 23
		Keyword_else,	   // 24
		Keyword_or,	   	   // 25
		Keyword_and,	   // 26
		Keyword_not,	   // 27
		Keyword_pair,	   // 28
		Keyword_first,	   // 29
		Keyword_second,	   // 30
		Keyword_nil,	   // 31
		
		UNDEF;			   // 22

		private boolean isFinal()
		{
			return ( this.compareTo(State.Id) >= 0 );  
		}	
	}

	/**
	 * 
	// By enumerating the non-final states first and then the final states,
	// test for a final state can be done by testing if the state's ordinal number
	// is greater than or equal to that of Id.

	// The following variables of "IO" class are used:

	//   static int a; the current input character
	//   static char c; used to convert the variable "a" to the char type whenever necessary
	 * 
	 *  */ 

	public static String t; // holds an extracted token after being concatenated on the driver() method
	public static State state; // the current state of the FA

	private static int driver()

	/**
	// This is the driver of the FA. 
	// If a valid token is found, assigns it to "t" and returns 1.
	// If an invalid token is found, assigns it to "t" and returns 0.
	// If end-of-stream is reached without finding any non-whitespace character, returns -1.
	 */
	{
		State nextSt; // the next state of the FA

		t = "";
		state = State.Start;

		if ( Character.isWhitespace((char) a) )
			a = getChar(); // get the next non-whitespace character
		if ( a == -1 ) // end-of-stream is reached
			return -1;

		while ( a != -1 ) // do the body if "a" is not end-of-stream meaning that file has not ended.
		{
			c = (char) a;
			nextSt = nextState( state, c );
			if ( nextSt == State.UNDEF ) // The FA will halt due to nextSt being a nontoken, therefore setting it self to return last token found.
			{
				if ( state.isFinal() ){ //if final tokens not found and state is undefined we check if the state is final and check if its keyword to return this keyword state.
					if( t.equals("if") )
						state = State.Keyword_if;
					if( t.equals("then") )
						state = State.Keyword_then;
					if( t.equals("else") )
						state = State.Keyword_else;
					if( t.equals("or") )
						state = State.Keyword_or;
					if( t.equals("and") )
						state = State.Keyword_and;
					if( t.equals("not") )
						state = State.Keyword_not;
					if( t.equals("pair") )
						state = State.Keyword_pair;
					if( t.equals("first") )
						state = State.Keyword_first;
					if( t.equals("second") )
						state = State.Keyword_second;
					if( t.equals("nil") )
						state = State.Keyword_nil;
					return 1; // valid token extracted and return.
				}
				else // "c" is an unexpected character
				{
					t = t+c;
					a = getNextChar();
					return 0; // invalid token found
				}
			}
			
			else // The FA will go on to read characters till it finds token.
			{
				state = nextSt;
				t = t+c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if ( state.isFinal() )
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	public static void getToken()

	// Extract the next token using the driver of the FA.
	// If an invalid token is found, issue an error message.

	{
		int i = driver();
		if ( i == 0 )
			displayln(t + " : Lexical Error, invalid token");
	}

	private static State nextState(State s, char c)

	// Returns the next state of the FA given the current state and input char;
	// if the next state is undefined, UNDEF is returned and will check for keywords

	{
		switch( state )
		{
	//string always uses this Start case to set and find token or its beginnings.
		case Start:
			if ( Character.isLetter(c) )
				return State.Id;
			else if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '+' )
				return State.Add;
			else if ( c == '-' )
				return State.Sub;
			else if ( c == '*' )
				return State.Mul;
			else if ( c == '/' )
				return State.Div;
			else if ( c == '(' )
				return State.LParen;
			else if ( c == ')' )
				return State.RParen;
			else if ( c == '<' )
				return State.Lt;
			else if ( c == '>' )
				return State.Gt;
			else if ( c == '=' )
				return State.Eq;
			else if ( c == '{' )
				return State.LBrace;
			else if ( c == '}' )
				return State.RBrace;
			else if ( c == '.' )
				return State.Dot;
			else
				return State.UNDEF;
	//Now cases below will be used to implement the logic on State Diagram.
		case Id:			
			if ( Character.isLetterOrDigit(c) )
				return State.Id;
			else
				return State.UNDEF;
		case Int:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.' )
				return State.Float;
			else if ( c == 'e' || c == 'E' )
				return State.E;
			else if ( c == 'f' || c == 'F' )
				return State.FloatF;
			else
				return State.UNDEF;
		case Dot:
			if ( Character.isDigit(c) )
				return State.Float;
			else
				return State.UNDEF;
		case Float:
			if ( Character.isDigit(c) )
				return State.Float;
			else if ( c == 'e' || c == 'E' )
				return State.E;
			else if ( c == 'f' || c == 'F' )
				return State.FloatF;
			else
				return State.UNDEF;
		case E:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else if ( c == '+' || c == '-' )
				return State.EPlusMinus;
			else
				return State.UNDEF;
		case EPlusMinus:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else
				return State.UNDEF;
		case FloatE:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else if ( c == 'f' || c == 'F' )
				return State.FloatF;
			else
				return State.UNDEF;
		case Lt:
			if ( c == '=' )
				return State.Le;
			else
				return State.UNDEF;
		case Gt:
			if ( c == '=' )
				return State.Ge;
			else
				return State.UNDEF;
		case Add:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.' )
				return State.Dot;
			else if ( c == '+' )
				return State.Dot;
			else
				return State.UNDEF;
		case Sub:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.' )
				return State.Dot;
			else if ( c == '-' )
				return State.Sub;
			else
				return State.UNDEF;
		case Mul:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '*' )
				return State.Mul;
			else
				return State.UNDEF;
		case FloatF:
			if ( c == 'f' || c=='F' )
				return State.FloatF;
			else
				return State.UNDEF;
		default:
			return State.UNDEF;
		}
	} // end nextState

	public static void main(String argv[])

	{		
		//setIO( "lexTest/t7.txt", "lexTest/to7.txt" );
		setIO( argv[0], argv[1] );
		
		int i;

		while ( a != -1 ) // while "a" is not end-of-stream
		{
			i = driver(); // extract the next token
			if ( i == 1 )
				displayln( t+"   : "+state.toString() );
			else if ( i == 0 )
				displayln( t+" : Lexical Error, invalid token"); //if no token we return message shown on file.
		} 

		closeIO(); //we write to file and close connection to it.
	}
} 

