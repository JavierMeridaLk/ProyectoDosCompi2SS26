// Generated from Zetariano.g4 by ANTLR 4.13.2
 package com.mycompany; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ZetarianoParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PUBLIC=1, PRIVATE=2, PROTECTED=3, STATIC=4, CLASS=5, EXTENDS=6, VOID=7, 
		INT=8, DOUBLE=9, CHAR=10, BOOLEAN=11, STRING=12, NEW=13, THIS=14, SUPER=15, 
		NULL=16, TRUE=17, FALSE=18, IF=19, ELSE=20, SWITCH=21, CASE=22, DEFAULT=23, 
		FOR=24, WHILE=25, DO=26, BREAK=27, CONTINUE=28, RETURN=29, PRINTLN=30, 
		PRINT=31, READLN=32, OVERRIDE=33, MAS_IGUAL=34, MENOS_IGUAL=35, POR_IGUAL=36, 
		ENTRE_IGUAL=37, MODULO_IGUAL=38, IGUAL_QUE=39, DIFERENTE_DE=40, MENOR_O_IGUAL_QUE=41, 
		MAYOR_O_IGUAL_QUE=42, Y_LOGICO=43, O_LOGICO=44, INCREMENTO=45, DECREMENTO=46, 
		NO_LOGICO=47, MENOR_QUE=48, MAYOR_QUE=49, SUMA=50, RESTA=51, MULTIPLICACION=52, 
		DIVISION=53, MODULO=54, ASIGNACION=55, INTERROGACION=56, DOS_PUNTOS=57, 
		PUNTO_Y_COMA=58, COMA=59, PUNTO=60, CORCHETE_IZQUIERDO=61, CORCHETE_DERECHO=62, 
		LLAVE_IZQUIERDA=63, LLAVE_DERECHA=64, PARENTESIS_IZQUIERDO=65, PARENTESIS_DERECHO=66, 
		DECIMAL=67, ENTERO=68, TEXTO=69, CARACTER=70, IDENTIFICADOR=71, COMENTARIO_DE_BLOQUE=72, 
		COMENTARIO_DE_LINEA=73, ESPACIOS=74, COMENTARIO_SIN_CERRAR=75, TEXTO_SIN_CERRAR=76, 
		CARACTER_INVALIDO=77, ERROR_LEXICO=78;
	public static final int
		RULE_compilacion = 0, RULE_declaracionClase = 1, RULE_modificador = 2, 
		RULE_miembro = 3, RULE_declaracionAtributo = 4, RULE_declaracionConstructor = 5, 
		RULE_cuerpoConstructor = 6, RULE_llamadaSuper = 7, RULE_declaracionMetodo = 8, 
		RULE_tipoRetorno = 9, RULE_parametros = 10, RULE_parametro = 11, RULE_tipo = 12, 
		RULE_tipoBase = 13, RULE_declaradorVariable = 14, RULE_inicializadorVariable = 15, 
		RULE_inicializadorArreglo = 16, RULE_bloque = 17, RULE_sentencia = 18, 
		RULE_declaracionLocal = 19, RULE_seccionSwitch = 20, RULE_etiquetaSwitch = 21, 
		RULE_inicioFor = 22, RULE_listaExpresiones = 23, RULE_expresion = 24, 
		RULE_primario = 25, RULE_argumentos = 26, RULE_literal = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilacion", "declaracionClase", "modificador", "miembro", "declaracionAtributo", 
			"declaracionConstructor", "cuerpoConstructor", "llamadaSuper", "declaracionMetodo", 
			"tipoRetorno", "parametros", "parametro", "tipo", "tipoBase", "declaradorVariable", 
			"inicializadorVariable", "inicializadorArreglo", "bloque", "sentencia", 
			"declaracionLocal", "seccionSwitch", "etiquetaSwitch", "inicioFor", "listaExpresiones", 
			"expresion", "primario", "argumentos", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'public'", "'private'", "'protected'", "'static'", "'class'", 
			"'extends'", "'void'", "'int'", "'double'", "'char'", "'boolean'", "'String'", 
			"'new'", "'this'", "'super'", "'null'", "'true'", "'false'", "'if'", 
			"'else'", "'switch'", "'case'", "'default'", "'for'", "'while'", "'do'", 
			"'break'", "'continue'", "'return'", "'println'", "'print'", "'readln'", 
			"'@Override'", "'+='", "'-='", "'*='", "'/='", "'%='", "'=='", "'!='", 
			"'<='", "'>='", "'&&'", "'||'", "'++'", "'--'", "'!'", "'<'", "'>'", 
			"'+'", "'-'", "'*'", "'/'", "'%'", "'='", "'?'", "':'", "';'", "','", 
			"'.'", "'['", "']'", "'{'", "'}'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PUBLIC", "PRIVATE", "PROTECTED", "STATIC", "CLASS", "EXTENDS", 
			"VOID", "INT", "DOUBLE", "CHAR", "BOOLEAN", "STRING", "NEW", "THIS", 
			"SUPER", "NULL", "TRUE", "FALSE", "IF", "ELSE", "SWITCH", "CASE", "DEFAULT", 
			"FOR", "WHILE", "DO", "BREAK", "CONTINUE", "RETURN", "PRINTLN", "PRINT", 
			"READLN", "OVERRIDE", "MAS_IGUAL", "MENOS_IGUAL", "POR_IGUAL", "ENTRE_IGUAL", 
			"MODULO_IGUAL", "IGUAL_QUE", "DIFERENTE_DE", "MENOR_O_IGUAL_QUE", "MAYOR_O_IGUAL_QUE", 
			"Y_LOGICO", "O_LOGICO", "INCREMENTO", "DECREMENTO", "NO_LOGICO", "MENOR_QUE", 
			"MAYOR_QUE", "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "MODULO", 
			"ASIGNACION", "INTERROGACION", "DOS_PUNTOS", "PUNTO_Y_COMA", "COMA", 
			"PUNTO", "CORCHETE_IZQUIERDO", "CORCHETE_DERECHO", "LLAVE_IZQUIERDA", 
			"LLAVE_DERECHA", "PARENTESIS_IZQUIERDO", "PARENTESIS_DERECHO", "DECIMAL", 
			"ENTERO", "TEXTO", "CARACTER", "IDENTIFICADOR", "COMENTARIO_DE_BLOQUE", 
			"COMENTARIO_DE_LINEA", "ESPACIOS", "COMENTARIO_SIN_CERRAR", "TEXTO_SIN_CERRAR", 
			"CARACTER_INVALIDO", "ERROR_LEXICO"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Zetariano.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ZetarianoParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CompilacionContext extends ParserRuleContext {
		public DeclaracionClaseContext declaracionClase() {
			return getRuleContext(DeclaracionClaseContext.class,0);
		}
		public TerminalNode EOF() { return getToken(ZetarianoParser.EOF, 0); }
		public CompilacionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilacion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterCompilacion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitCompilacion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitCompilacion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompilacionContext compilacion() throws RecognitionException {
		CompilacionContext _localctx = new CompilacionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilacion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			declaracionClase();
			setState(57);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracionClaseContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(ZetarianoParser.CLASS, 0); }
		public List<TerminalNode> IDENTIFICADOR() { return getTokens(ZetarianoParser.IDENTIFICADOR); }
		public TerminalNode IDENTIFICADOR(int i) {
			return getToken(ZetarianoParser.IDENTIFICADOR, i);
		}
		public TerminalNode LLAVE_IZQUIERDA() { return getToken(ZetarianoParser.LLAVE_IZQUIERDA, 0); }
		public TerminalNode LLAVE_DERECHA() { return getToken(ZetarianoParser.LLAVE_DERECHA, 0); }
		public List<ModificadorContext> modificador() {
			return getRuleContexts(ModificadorContext.class);
		}
		public ModificadorContext modificador(int i) {
			return getRuleContext(ModificadorContext.class,i);
		}
		public TerminalNode EXTENDS() { return getToken(ZetarianoParser.EXTENDS, 0); }
		public List<MiembroContext> miembro() {
			return getRuleContexts(MiembroContext.class);
		}
		public MiembroContext miembro(int i) {
			return getRuleContext(MiembroContext.class,i);
		}
		public DeclaracionClaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracionClase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaracionClase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaracionClase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaracionClase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaracionClaseContext declaracionClase() throws RecognitionException {
		DeclaracionClaseContext _localctx = new DeclaracionClaseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_declaracionClase);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 30L) != 0)) {
				{
				{
				setState(59);
				modificador();
				}
				}
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65);
			match(CLASS);
			setState(66);
			match(IDENTIFICADOR);
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EXTENDS) {
				{
				setState(67);
				match(EXTENDS);
				setState(68);
				match(IDENTIFICADOR);
				}
			}

			setState(71);
			match(LLAVE_IZQUIERDA);
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 8589942686L) != 0) || _la==IDENTIFICADOR) {
				{
				{
				setState(72);
				miembro();
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(78);
			match(LLAVE_DERECHA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ModificadorContext extends ParserRuleContext {
		public TerminalNode PUBLIC() { return getToken(ZetarianoParser.PUBLIC, 0); }
		public TerminalNode PRIVATE() { return getToken(ZetarianoParser.PRIVATE, 0); }
		public TerminalNode PROTECTED() { return getToken(ZetarianoParser.PROTECTED, 0); }
		public TerminalNode STATIC() { return getToken(ZetarianoParser.STATIC, 0); }
		public ModificadorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modificador; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterModificador(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitModificador(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitModificador(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModificadorContext modificador() throws RecognitionException {
		ModificadorContext _localctx = new ModificadorContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_modificador);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 30L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MiembroContext extends ParserRuleContext {
		public DeclaracionAtributoContext declaracionAtributo() {
			return getRuleContext(DeclaracionAtributoContext.class,0);
		}
		public DeclaracionConstructorContext declaracionConstructor() {
			return getRuleContext(DeclaracionConstructorContext.class,0);
		}
		public DeclaracionMetodoContext declaracionMetodo() {
			return getRuleContext(DeclaracionMetodoContext.class,0);
		}
		public MiembroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_miembro; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterMiembro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitMiembro(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitMiembro(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MiembroContext miembro() throws RecognitionException {
		MiembroContext _localctx = new MiembroContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_miembro);
		try {
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(82);
				declaracionAtributo();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(83);
				declaracionConstructor();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(84);
				declaracionMetodo();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracionAtributoContext extends ParserRuleContext {
		public TipoContext tipo() {
			return getRuleContext(TipoContext.class,0);
		}
		public List<DeclaradorVariableContext> declaradorVariable() {
			return getRuleContexts(DeclaradorVariableContext.class);
		}
		public DeclaradorVariableContext declaradorVariable(int i) {
			return getRuleContext(DeclaradorVariableContext.class,i);
		}
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public List<ModificadorContext> modificador() {
			return getRuleContexts(ModificadorContext.class);
		}
		public ModificadorContext modificador(int i) {
			return getRuleContext(ModificadorContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public DeclaracionAtributoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracionAtributo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaracionAtributo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaracionAtributo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaracionAtributo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaracionAtributoContext declaracionAtributo() throws RecognitionException {
		DeclaracionAtributoContext _localctx = new DeclaracionAtributoContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declaracionAtributo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 30L) != 0)) {
				{
				{
				setState(87);
				modificador();
				}
				}
				setState(92);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(93);
			tipo();
			setState(94);
			declaradorVariable();
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMA) {
				{
				{
				setState(95);
				match(COMA);
				setState(96);
				declaradorVariable();
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102);
			match(PUNTO_Y_COMA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracionConstructorContext extends ParserRuleContext {
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public CuerpoConstructorContext cuerpoConstructor() {
			return getRuleContext(CuerpoConstructorContext.class,0);
		}
		public List<ModificadorContext> modificador() {
			return getRuleContexts(ModificadorContext.class);
		}
		public ModificadorContext modificador(int i) {
			return getRuleContext(ModificadorContext.class,i);
		}
		public ParametrosContext parametros() {
			return getRuleContext(ParametrosContext.class,0);
		}
		public DeclaracionConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracionConstructor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaracionConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaracionConstructor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaracionConstructor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaracionConstructorContext declaracionConstructor() throws RecognitionException {
		DeclaracionConstructorContext _localctx = new DeclaracionConstructorContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_declaracionConstructor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 30L) != 0)) {
				{
				{
				setState(104);
				modificador();
				}
				}
				setState(109);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(110);
			match(IDENTIFICADOR);
			setState(111);
			match(PARENTESIS_IZQUIERDO);
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -9223372036854775777L) != 0)) {
				{
				setState(112);
				parametros();
				}
			}

			setState(115);
			match(PARENTESIS_DERECHO);
			setState(116);
			cuerpoConstructor();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CuerpoConstructorContext extends ParserRuleContext {
		public TerminalNode LLAVE_IZQUIERDA() { return getToken(ZetarianoParser.LLAVE_IZQUIERDA, 0); }
		public TerminalNode LLAVE_DERECHA() { return getToken(ZetarianoParser.LLAVE_DERECHA, 0); }
		public LlamadaSuperContext llamadaSuper() {
			return getRuleContext(LlamadaSuperContext.class,0);
		}
		public List<SentenciaContext> sentencia() {
			return getRuleContexts(SentenciaContext.class);
		}
		public SentenciaContext sentencia(int i) {
			return getRuleContext(SentenciaContext.class,i);
		}
		public CuerpoConstructorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cuerpoConstructor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterCuerpoConstructor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitCuerpoConstructor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitCuerpoConstructor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CuerpoConstructorContext cuerpoConstructor() throws RecognitionException {
		CuerpoConstructorContext _localctx = new CuerpoConstructorContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_cuerpoConstructor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(LLAVE_IZQUIERDA);
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(119);
				llamadaSuper();
				}
				break;
			}
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -395176711056052225L) != 0)) {
				{
				{
				setState(122);
				sentencia();
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(128);
			match(LLAVE_DERECHA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LlamadaSuperContext extends ParserRuleContext {
		public TerminalNode SUPER() { return getToken(ZetarianoParser.SUPER, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public ArgumentosContext argumentos() {
			return getRuleContext(ArgumentosContext.class,0);
		}
		public LlamadaSuperContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_llamadaSuper; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterLlamadaSuper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitLlamadaSuper(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitLlamadaSuper(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LlamadaSuperContext llamadaSuper() throws RecognitionException {
		LlamadaSuperContext _localctx = new LlamadaSuperContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_llamadaSuper);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(SUPER);
			setState(131);
			match(PARENTESIS_IZQUIERDO);
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
				{
				setState(132);
				argumentos();
				}
			}

			setState(135);
			match(PARENTESIS_DERECHO);
			setState(136);
			match(PUNTO_Y_COMA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracionMetodoContext extends ParserRuleContext {
		public TipoRetornoContext tipoRetorno() {
			return getRuleContext(TipoRetornoContext.class,0);
		}
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public BloqueContext bloque() {
			return getRuleContext(BloqueContext.class,0);
		}
		public TerminalNode OVERRIDE() { return getToken(ZetarianoParser.OVERRIDE, 0); }
		public List<ModificadorContext> modificador() {
			return getRuleContexts(ModificadorContext.class);
		}
		public ModificadorContext modificador(int i) {
			return getRuleContext(ModificadorContext.class,i);
		}
		public ParametrosContext parametros() {
			return getRuleContext(ParametrosContext.class,0);
		}
		public DeclaracionMetodoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracionMetodo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaracionMetodo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaracionMetodo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaracionMetodo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaracionMetodoContext declaracionMetodo() throws RecognitionException {
		DeclaracionMetodoContext _localctx = new DeclaracionMetodoContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_declaracionMetodo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OVERRIDE) {
				{
				setState(138);
				match(OVERRIDE);
				}
			}

			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 30L) != 0)) {
				{
				{
				setState(141);
				modificador();
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(147);
			tipoRetorno();
			setState(148);
			match(IDENTIFICADOR);
			setState(149);
			match(PARENTESIS_IZQUIERDO);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -9223372036854775777L) != 0)) {
				{
				setState(150);
				parametros();
				}
			}

			setState(153);
			match(PARENTESIS_DERECHO);
			setState(154);
			bloque();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TipoRetornoContext extends ParserRuleContext {
		public TipoContext tipo() {
			return getRuleContext(TipoContext.class,0);
		}
		public TerminalNode VOID() { return getToken(ZetarianoParser.VOID, 0); }
		public TipoRetornoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipoRetorno; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterTipoRetorno(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitTipoRetorno(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitTipoRetorno(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TipoRetornoContext tipoRetorno() throws RecognitionException {
		TipoRetornoContext _localctx = new TipoRetornoContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_tipoRetorno);
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT:
			case DOUBLE:
			case CHAR:
			case BOOLEAN:
			case STRING:
			case IDENTIFICADOR:
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				tipo();
				}
				break;
			case VOID:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
				match(VOID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParametrosContext extends ParserRuleContext {
		public List<ParametroContext> parametro() {
			return getRuleContexts(ParametroContext.class);
		}
		public ParametroContext parametro(int i) {
			return getRuleContext(ParametroContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public ParametrosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametros; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterParametros(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitParametros(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitParametros(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametrosContext parametros() throws RecognitionException {
		ParametrosContext _localctx = new ParametrosContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_parametros);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			parametro();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMA) {
				{
				{
				setState(161);
				match(COMA);
				setState(162);
				parametro();
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParametroContext extends ParserRuleContext {
		public TipoContext tipo() {
			return getRuleContext(TipoContext.class,0);
		}
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public ParametroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parametro; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterParametro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitParametro(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitParametro(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParametroContext parametro() throws RecognitionException {
		ParametroContext _localctx = new ParametroContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_parametro);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			tipo();
			setState(169);
			match(IDENTIFICADOR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TipoContext extends ParserRuleContext {
		public TipoBaseContext tipoBase() {
			return getRuleContext(TipoBaseContext.class,0);
		}
		public List<TerminalNode> CORCHETE_IZQUIERDO() { return getTokens(ZetarianoParser.CORCHETE_IZQUIERDO); }
		public TerminalNode CORCHETE_IZQUIERDO(int i) {
			return getToken(ZetarianoParser.CORCHETE_IZQUIERDO, i);
		}
		public List<TerminalNode> CORCHETE_DERECHO() { return getTokens(ZetarianoParser.CORCHETE_DERECHO); }
		public TerminalNode CORCHETE_DERECHO(int i) {
			return getToken(ZetarianoParser.CORCHETE_DERECHO, i);
		}
		public TipoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterTipo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitTipo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitTipo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TipoContext tipo() throws RecognitionException {
		TipoContext _localctx = new TipoContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_tipo);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			tipoBase();
			setState(176);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==CORCHETE_IZQUIERDO) {
				{
				{
				setState(172);
				match(CORCHETE_IZQUIERDO);
				setState(173);
				match(CORCHETE_DERECHO);
				}
				}
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TipoBaseContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(ZetarianoParser.INT, 0); }
		public TerminalNode DOUBLE() { return getToken(ZetarianoParser.DOUBLE, 0); }
		public TerminalNode CHAR() { return getToken(ZetarianoParser.CHAR, 0); }
		public TerminalNode BOOLEAN() { return getToken(ZetarianoParser.BOOLEAN, 0); }
		public TerminalNode STRING() { return getToken(ZetarianoParser.STRING, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TipoBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tipoBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterTipoBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitTipoBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitTipoBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TipoBaseContext tipoBase() throws RecognitionException {
		TipoBaseContext _localctx = new TipoBaseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_tipoBase);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			_la = _input.LA(1);
			if ( !(((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -9223372036854775777L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaradorVariableContext extends ParserRuleContext {
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode ASIGNACION() { return getToken(ZetarianoParser.ASIGNACION, 0); }
		public InicializadorVariableContext inicializadorVariable() {
			return getRuleContext(InicializadorVariableContext.class,0);
		}
		public DeclaradorVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaradorVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaradorVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaradorVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaradorVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaradorVariableContext declaradorVariable() throws RecognitionException {
		DeclaradorVariableContext _localctx = new DeclaradorVariableContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_declaradorVariable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			match(IDENTIFICADOR);
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASIGNACION) {
				{
				setState(182);
				match(ASIGNACION);
				setState(183);
				inicializadorVariable();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InicializadorVariableContext extends ParserRuleContext {
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public InicializadorArregloContext inicializadorArreglo() {
			return getRuleContext(InicializadorArregloContext.class,0);
		}
		public InicializadorVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inicializadorVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterInicializadorVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitInicializadorVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitInicializadorVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InicializadorVariableContext inicializadorVariable() throws RecognitionException {
		InicializadorVariableContext _localctx = new InicializadorVariableContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_inicializadorVariable);
		try {
			setState(188);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NEW:
			case THIS:
			case SUPER:
			case NULL:
			case TRUE:
			case FALSE:
			case READLN:
			case INCREMENTO:
			case DECREMENTO:
			case NO_LOGICO:
			case SUMA:
			case RESTA:
			case PARENTESIS_IZQUIERDO:
			case DECIMAL:
			case ENTERO:
			case TEXTO:
			case CARACTER:
			case IDENTIFICADOR:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				expresion(0);
				}
				break;
			case LLAVE_IZQUIERDA:
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				inicializadorArreglo();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InicializadorArregloContext extends ParserRuleContext {
		public TerminalNode LLAVE_IZQUIERDA() { return getToken(ZetarianoParser.LLAVE_IZQUIERDA, 0); }
		public TerminalNode LLAVE_DERECHA() { return getToken(ZetarianoParser.LLAVE_DERECHA, 0); }
		public List<InicializadorVariableContext> inicializadorVariable() {
			return getRuleContexts(InicializadorVariableContext.class);
		}
		public InicializadorVariableContext inicializadorVariable(int i) {
			return getRuleContext(InicializadorVariableContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public InicializadorArregloContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inicializadorArreglo; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterInicializadorArreglo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitInicializadorArreglo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitInicializadorArreglo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InicializadorArregloContext inicializadorArreglo() throws RecognitionException {
		InicializadorArregloContext _localctx = new InicializadorArregloContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_inicializadorArreglo);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(LLAVE_IZQUIERDA);
			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 564076295710310463L) != 0)) {
				{
				setState(191);
				inicializadorVariable();
				setState(196);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(192);
						match(COMA);
						setState(193);
						inicializadorVariable();
						}
						} 
					}
					setState(198);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				}
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMA) {
					{
					setState(199);
					match(COMA);
					}
				}

				}
			}

			setState(204);
			match(LLAVE_DERECHA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BloqueContext extends ParserRuleContext {
		public TerminalNode LLAVE_IZQUIERDA() { return getToken(ZetarianoParser.LLAVE_IZQUIERDA, 0); }
		public TerminalNode LLAVE_DERECHA() { return getToken(ZetarianoParser.LLAVE_DERECHA, 0); }
		public List<SentenciaContext> sentencia() {
			return getRuleContexts(SentenciaContext.class);
		}
		public SentenciaContext sentencia(int i) {
			return getRuleContext(SentenciaContext.class,i);
		}
		public BloqueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bloque; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterBloque(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitBloque(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitBloque(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BloqueContext bloque() throws RecognitionException {
		BloqueContext _localctx = new BloqueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_bloque);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(LLAVE_IZQUIERDA);
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -395176711056052225L) != 0)) {
				{
				{
				setState(207);
				sentencia();
				}
				}
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(213);
			match(LLAVE_DERECHA);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaContext extends ParserRuleContext {
		public SentenciaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sentencia; }
	 
		public SentenciaContext() { }
		public void copyFrom(SentenciaContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaDeclaracionContext extends SentenciaContext {
		public DeclaracionLocalContext declaracionLocal() {
			return getRuleContext(DeclaracionLocalContext.class,0);
		}
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaDeclaracionContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaDeclaracion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaDeclaracion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaDeclaracion(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaReturnContext extends SentenciaContext {
		public TerminalNode RETURN() { return getToken(ZetarianoParser.RETURN, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public SentenciaReturnContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaReturn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaReturn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaReturn(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaIfContext extends SentenciaContext {
		public TerminalNode IF() { return getToken(ZetarianoParser.IF, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public List<SentenciaContext> sentencia() {
			return getRuleContexts(SentenciaContext.class);
		}
		public SentenciaContext sentencia(int i) {
			return getRuleContext(SentenciaContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(ZetarianoParser.ELSE, 0); }
		public SentenciaIfContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaIf(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaPrintContext extends SentenciaContext {
		public TerminalNode PRINT() { return getToken(ZetarianoParser.PRINT, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaPrintContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaPrint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaPrint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaPrint(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaExpresionContext extends SentenciaContext {
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaExpresionContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaExpresion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaExpresion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaExpresion(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaVaciaContext extends SentenciaContext {
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaVaciaContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaVacia(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaVacia(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaVacia(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaPrintlnContext extends SentenciaContext {
		public TerminalNode PRINTLN() { return getToken(ZetarianoParser.PRINTLN, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public SentenciaPrintlnContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaPrintln(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaPrintln(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaPrintln(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaBloqueContext extends SentenciaContext {
		public BloqueContext bloque() {
			return getRuleContext(BloqueContext.class,0);
		}
		public SentenciaBloqueContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaBloque(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaBloque(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaBloque(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaForContext extends SentenciaContext {
		public TerminalNode FOR() { return getToken(ZetarianoParser.FOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public List<TerminalNode> PUNTO_Y_COMA() { return getTokens(ZetarianoParser.PUNTO_Y_COMA); }
		public TerminalNode PUNTO_Y_COMA(int i) {
			return getToken(ZetarianoParser.PUNTO_Y_COMA, i);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public SentenciaContext sentencia() {
			return getRuleContext(SentenciaContext.class,0);
		}
		public InicioForContext inicioFor() {
			return getRuleContext(InicioForContext.class,0);
		}
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public ListaExpresionesContext listaExpresiones() {
			return getRuleContext(ListaExpresionesContext.class,0);
		}
		public SentenciaForContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaFor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaBreakContext extends SentenciaContext {
		public TerminalNode BREAK() { return getToken(ZetarianoParser.BREAK, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaBreakContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaBreak(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaBreak(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaBreak(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaContinueContext extends SentenciaContext {
		public TerminalNode CONTINUE() { return getToken(ZetarianoParser.CONTINUE, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaContinueContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaContinue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaContinue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaContinue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaWhileContext extends SentenciaContext {
		public TerminalNode WHILE() { return getToken(ZetarianoParser.WHILE, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public SentenciaContext sentencia() {
			return getRuleContext(SentenciaContext.class,0);
		}
		public SentenciaWhileContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaWhile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaWhile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaDoWhileContext extends SentenciaContext {
		public TerminalNode DO() { return getToken(ZetarianoParser.DO, 0); }
		public SentenciaContext sentencia() {
			return getRuleContext(SentenciaContext.class,0);
		}
		public TerminalNode WHILE() { return getToken(ZetarianoParser.WHILE, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public TerminalNode PUNTO_Y_COMA() { return getToken(ZetarianoParser.PUNTO_Y_COMA, 0); }
		public SentenciaDoWhileContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaDoWhile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaDoWhile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaDoWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SentenciaSwitchContext extends SentenciaContext {
		public TerminalNode SWITCH() { return getToken(ZetarianoParser.SWITCH, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public TerminalNode LLAVE_IZQUIERDA() { return getToken(ZetarianoParser.LLAVE_IZQUIERDA, 0); }
		public TerminalNode LLAVE_DERECHA() { return getToken(ZetarianoParser.LLAVE_DERECHA, 0); }
		public List<SeccionSwitchContext> seccionSwitch() {
			return getRuleContexts(SeccionSwitchContext.class);
		}
		public SeccionSwitchContext seccionSwitch(int i) {
			return getRuleContext(SeccionSwitchContext.class,i);
		}
		public SentenciaSwitchContext(SentenciaContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSentenciaSwitch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSentenciaSwitch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSentenciaSwitch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SentenciaContext sentencia() throws RecognitionException {
		SentenciaContext _localctx = new SentenciaContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_sentencia);
		int _la;
		try {
			setState(296);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				_localctx = new SentenciaBloqueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(215);
				bloque();
				}
				break;
			case 2:
				_localctx = new SentenciaDeclaracionContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(216);
				declaracionLocal();
				setState(217);
				match(PUNTO_Y_COMA);
				}
				break;
			case 3:
				_localctx = new SentenciaIfContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(219);
				match(IF);
				setState(220);
				match(PARENTESIS_IZQUIERDO);
				setState(221);
				expresion(0);
				setState(222);
				match(PARENTESIS_DERECHO);
				setState(223);
				sentencia();
				setState(226);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
				case 1:
					{
					setState(224);
					match(ELSE);
					setState(225);
					sentencia();
					}
					break;
				}
				}
				break;
			case 4:
				_localctx = new SentenciaSwitchContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(228);
				match(SWITCH);
				setState(229);
				match(PARENTESIS_IZQUIERDO);
				setState(230);
				expresion(0);
				setState(231);
				match(PARENTESIS_DERECHO);
				setState(232);
				match(LLAVE_IZQUIERDA);
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==CASE || _la==DEFAULT) {
					{
					{
					setState(233);
					seccionSwitch();
					}
					}
					setState(238);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(239);
				match(LLAVE_DERECHA);
				}
				break;
			case 5:
				_localctx = new SentenciaForContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(241);
				match(FOR);
				setState(242);
				match(PARENTESIS_IZQUIERDO);
				setState(244);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -432331407998580737L) != 0)) {
					{
					setState(243);
					inicioFor();
					}
				}

				setState(246);
				match(PUNTO_Y_COMA);
				setState(248);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(247);
					expresion(0);
					}
				}

				setState(250);
				match(PUNTO_Y_COMA);
				setState(252);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(251);
					listaExpresiones();
					}
				}

				setState(254);
				match(PARENTESIS_DERECHO);
				setState(255);
				sentencia();
				}
				break;
			case 6:
				_localctx = new SentenciaWhileContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(256);
				match(WHILE);
				setState(257);
				match(PARENTESIS_IZQUIERDO);
				setState(258);
				expresion(0);
				setState(259);
				match(PARENTESIS_DERECHO);
				setState(260);
				sentencia();
				}
				break;
			case 7:
				_localctx = new SentenciaDoWhileContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(262);
				match(DO);
				setState(263);
				sentencia();
				setState(264);
				match(WHILE);
				setState(265);
				match(PARENTESIS_IZQUIERDO);
				setState(266);
				expresion(0);
				setState(267);
				match(PARENTESIS_DERECHO);
				setState(268);
				match(PUNTO_Y_COMA);
				}
				break;
			case 8:
				_localctx = new SentenciaBreakContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(270);
				match(BREAK);
				setState(271);
				match(PUNTO_Y_COMA);
				}
				break;
			case 9:
				_localctx = new SentenciaContinueContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(272);
				match(CONTINUE);
				setState(273);
				match(PUNTO_Y_COMA);
				}
				break;
			case 10:
				_localctx = new SentenciaReturnContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(274);
				match(RETURN);
				setState(276);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(275);
					expresion(0);
					}
				}

				setState(278);
				match(PUNTO_Y_COMA);
				}
				break;
			case 11:
				_localctx = new SentenciaPrintlnContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(279);
				match(PRINTLN);
				setState(280);
				match(PARENTESIS_IZQUIERDO);
				setState(282);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(281);
					expresion(0);
					}
				}

				setState(284);
				match(PARENTESIS_DERECHO);
				setState(285);
				match(PUNTO_Y_COMA);
				}
				break;
			case 12:
				_localctx = new SentenciaPrintContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(286);
				match(PRINT);
				setState(287);
				match(PARENTESIS_IZQUIERDO);
				setState(288);
				expresion(0);
				setState(289);
				match(PARENTESIS_DERECHO);
				setState(290);
				match(PUNTO_Y_COMA);
				}
				break;
			case 13:
				_localctx = new SentenciaExpresionContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(292);
				expresion(0);
				setState(293);
				match(PUNTO_Y_COMA);
				}
				break;
			case 14:
				_localctx = new SentenciaVaciaContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(295);
				match(PUNTO_Y_COMA);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclaracionLocalContext extends ParserRuleContext {
		public TipoContext tipo() {
			return getRuleContext(TipoContext.class,0);
		}
		public List<DeclaradorVariableContext> declaradorVariable() {
			return getRuleContexts(DeclaradorVariableContext.class);
		}
		public DeclaradorVariableContext declaradorVariable(int i) {
			return getRuleContext(DeclaradorVariableContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public DeclaracionLocalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaracionLocal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterDeclaracionLocal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitDeclaracionLocal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitDeclaracionLocal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclaracionLocalContext declaracionLocal() throws RecognitionException {
		DeclaracionLocalContext _localctx = new DeclaracionLocalContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_declaracionLocal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			tipo();
			setState(299);
			declaradorVariable();
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMA) {
				{
				{
				setState(300);
				match(COMA);
				setState(301);
				declaradorVariable();
				}
				}
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SeccionSwitchContext extends ParserRuleContext {
		public List<EtiquetaSwitchContext> etiquetaSwitch() {
			return getRuleContexts(EtiquetaSwitchContext.class);
		}
		public EtiquetaSwitchContext etiquetaSwitch(int i) {
			return getRuleContext(EtiquetaSwitchContext.class,i);
		}
		public List<SentenciaContext> sentencia() {
			return getRuleContexts(SentenciaContext.class);
		}
		public SentenciaContext sentencia(int i) {
			return getRuleContext(SentenciaContext.class,i);
		}
		public SeccionSwitchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_seccionSwitch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterSeccionSwitch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitSeccionSwitch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitSeccionSwitch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SeccionSwitchContext seccionSwitch() throws RecognitionException {
		SeccionSwitchContext _localctx = new SeccionSwitchContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_seccionSwitch);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(308); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(307);
					etiquetaSwitch();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(310); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 8)) & ~0x3f) == 0 && ((1L << (_la - 8)) & -395176711056052225L) != 0)) {
				{
				{
				setState(312);
				sentencia();
				}
				}
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EtiquetaSwitchContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(ZetarianoParser.CASE, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode DOS_PUNTOS() { return getToken(ZetarianoParser.DOS_PUNTOS, 0); }
		public TerminalNode DEFAULT() { return getToken(ZetarianoParser.DEFAULT, 0); }
		public EtiquetaSwitchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_etiquetaSwitch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterEtiquetaSwitch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitEtiquetaSwitch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitEtiquetaSwitch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EtiquetaSwitchContext etiquetaSwitch() throws RecognitionException {
		EtiquetaSwitchContext _localctx = new EtiquetaSwitchContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_etiquetaSwitch);
		try {
			setState(324);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(318);
				match(CASE);
				setState(319);
				expresion(0);
				setState(320);
				match(DOS_PUNTOS);
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				match(DEFAULT);
				setState(323);
				match(DOS_PUNTOS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InicioForContext extends ParserRuleContext {
		public DeclaracionLocalContext declaracionLocal() {
			return getRuleContext(DeclaracionLocalContext.class,0);
		}
		public ListaExpresionesContext listaExpresiones() {
			return getRuleContext(ListaExpresionesContext.class,0);
		}
		public InicioForContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inicioFor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterInicioFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitInicioFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitInicioFor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InicioForContext inicioFor() throws RecognitionException {
		InicioForContext _localctx = new InicioForContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_inicioFor);
		try {
			setState(328);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(326);
				declaracionLocal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(327);
				listaExpresiones();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ListaExpresionesContext extends ParserRuleContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public ListaExpresionesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listaExpresiones; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterListaExpresiones(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitListaExpresiones(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitListaExpresiones(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListaExpresionesContext listaExpresiones() throws RecognitionException {
		ListaExpresionesContext _localctx = new ListaExpresionesContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_listaExpresiones);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(330);
			expresion(0);
			setState(335);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMA) {
				{
				{
				setState(331);
				match(COMA);
				setState(332);
				expresion(0);
				}
				}
				setState(337);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionContext extends ParserRuleContext {
		public ExpresionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expresion; }
	 
		public ExpresionContext() { }
		public void copyFrom(ExpresionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionMultiplicativaContext extends ExpresionContext {
		public Token operador;
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode MULTIPLICACION() { return getToken(ZetarianoParser.MULTIPLICACION, 0); }
		public TerminalNode DIVISION() { return getToken(ZetarianoParser.DIVISION, 0); }
		public TerminalNode MODULO() { return getToken(ZetarianoParser.MODULO, 0); }
		public ExpresionMultiplicativaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionMultiplicativa(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionMultiplicativa(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionMultiplicativa(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionYContext extends ExpresionContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode Y_LOGICO() { return getToken(ZetarianoParser.Y_LOGICO, 0); }
		public ExpresionYContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionY(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionY(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionY(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionPrimariaContext extends ExpresionContext {
		public PrimarioContext primario() {
			return getRuleContext(PrimarioContext.class,0);
		}
		public ExpresionPrimariaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionPrimaria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionPrimaria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionPrimaria(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionPostfijaContext extends ExpresionContext {
		public Token operador;
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode INCREMENTO() { return getToken(ZetarianoParser.INCREMENTO, 0); }
		public TerminalNode DECREMENTO() { return getToken(ZetarianoParser.DECREMENTO, 0); }
		public ExpresionPostfijaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionPostfija(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionPostfija(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionPostfija(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionAtributoContext extends ExpresionContext {
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PUNTO() { return getToken(ZetarianoParser.PUNTO, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public ExpresionAtributoContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionAtributo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionAtributo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionAtributo(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionIndiceContext extends ExpresionContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode CORCHETE_IZQUIERDO() { return getToken(ZetarianoParser.CORCHETE_IZQUIERDO, 0); }
		public TerminalNode CORCHETE_DERECHO() { return getToken(ZetarianoParser.CORCHETE_DERECHO, 0); }
		public ExpresionIndiceContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionIndice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionIndice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionIndice(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionUnariaContext extends ExpresionContext {
		public Token operador;
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode INCREMENTO() { return getToken(ZetarianoParser.INCREMENTO, 0); }
		public TerminalNode DECREMENTO() { return getToken(ZetarianoParser.DECREMENTO, 0); }
		public TerminalNode SUMA() { return getToken(ZetarianoParser.SUMA, 0); }
		public TerminalNode RESTA() { return getToken(ZetarianoParser.RESTA, 0); }
		public TerminalNode NO_LOGICO() { return getToken(ZetarianoParser.NO_LOGICO, 0); }
		public ExpresionUnariaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionUnaria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionUnaria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionUnaria(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionIgualdadContext extends ExpresionContext {
		public Token operador;
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode IGUAL_QUE() { return getToken(ZetarianoParser.IGUAL_QUE, 0); }
		public TerminalNode DIFERENTE_DE() { return getToken(ZetarianoParser.DIFERENTE_DE, 0); }
		public ExpresionIgualdadContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionIgualdad(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionIgualdad(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionIgualdad(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionRelacionalContext extends ExpresionContext {
		public Token operador;
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode MENOR_QUE() { return getToken(ZetarianoParser.MENOR_QUE, 0); }
		public TerminalNode MAYOR_QUE() { return getToken(ZetarianoParser.MAYOR_QUE, 0); }
		public TerminalNode MENOR_O_IGUAL_QUE() { return getToken(ZetarianoParser.MENOR_O_IGUAL_QUE, 0); }
		public TerminalNode MAYOR_O_IGUAL_QUE() { return getToken(ZetarianoParser.MAYOR_O_IGUAL_QUE, 0); }
		public ExpresionRelacionalContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionRelacional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionRelacional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionRelacional(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionTernariaContext extends ExpresionContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode INTERROGACION() { return getToken(ZetarianoParser.INTERROGACION, 0); }
		public TerminalNode DOS_PUNTOS() { return getToken(ZetarianoParser.DOS_PUNTOS, 0); }
		public ExpresionTernariaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionTernaria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionTernaria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionTernaria(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionLlamadaMetodoContext extends ExpresionContext {
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PUNTO() { return getToken(ZetarianoParser.PUNTO, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public ArgumentosContext argumentos() {
			return getRuleContext(ArgumentosContext.class,0);
		}
		public ExpresionLlamadaMetodoContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionLlamadaMetodo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionLlamadaMetodo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionLlamadaMetodo(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionAsignacionContext extends ExpresionContext {
		public Token operador;
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode ASIGNACION() { return getToken(ZetarianoParser.ASIGNACION, 0); }
		public TerminalNode MAS_IGUAL() { return getToken(ZetarianoParser.MAS_IGUAL, 0); }
		public TerminalNode MENOS_IGUAL() { return getToken(ZetarianoParser.MENOS_IGUAL, 0); }
		public TerminalNode POR_IGUAL() { return getToken(ZetarianoParser.POR_IGUAL, 0); }
		public TerminalNode ENTRE_IGUAL() { return getToken(ZetarianoParser.ENTRE_IGUAL, 0); }
		public TerminalNode MODULO_IGUAL() { return getToken(ZetarianoParser.MODULO_IGUAL, 0); }
		public ExpresionAsignacionContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionAsignacion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionAsignacion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionAsignacion(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionOContext extends ExpresionContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode O_LOGICO() { return getToken(ZetarianoParser.O_LOGICO, 0); }
		public ExpresionOContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionO(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionO(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionO(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpresionAditivaContext extends ExpresionContext {
		public Token operador;
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public TerminalNode SUMA() { return getToken(ZetarianoParser.SUMA, 0); }
		public TerminalNode RESTA() { return getToken(ZetarianoParser.RESTA, 0); }
		public ExpresionAditivaContext(ExpresionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterExpresionAditiva(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitExpresionAditiva(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitExpresionAditiva(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpresionContext expresion() throws RecognitionException {
		return expresion(0);
	}

	private ExpresionContext expresion(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpresionContext _localctx = new ExpresionContext(_ctx, _parentState);
		ExpresionContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_expresion, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NEW:
			case THIS:
			case SUPER:
			case NULL:
			case TRUE:
			case FALSE:
			case READLN:
			case PARENTESIS_IZQUIERDO:
			case DECIMAL:
			case ENTERO:
			case TEXTO:
			case CARACTER:
			case IDENTIFICADOR:
				{
				_localctx = new ExpresionPrimariaContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(339);
				primario();
				}
				break;
			case INCREMENTO:
			case DECREMENTO:
			case NO_LOGICO:
			case SUMA:
			case RESTA:
				{
				_localctx = new ExpresionUnariaContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(340);
				((ExpresionUnariaContext)_localctx).operador = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 3623990325149696L) != 0)) ) {
					((ExpresionUnariaContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(341);
				expresion(9);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(391);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(389);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
					case 1:
						{
						_localctx = new ExpresionMultiplicativaContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(344);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(345);
						((ExpresionMultiplicativaContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 31525197391593472L) != 0)) ) {
							((ExpresionMultiplicativaContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(346);
						expresion(9);
						}
						break;
					case 2:
						{
						_localctx = new ExpresionAditivaContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(347);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(348);
						((ExpresionAditivaContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SUMA || _la==RESTA) ) {
							((ExpresionAditivaContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(349);
						expresion(8);
						}
						break;
					case 3:
						{
						_localctx = new ExpresionRelacionalContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(350);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(351);
						((ExpresionRelacionalContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 851021999898624L) != 0)) ) {
							((ExpresionRelacionalContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(352);
						expresion(7);
						}
						break;
					case 4:
						{
						_localctx = new ExpresionIgualdadContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(353);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(354);
						((ExpresionIgualdadContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==IGUAL_QUE || _la==DIFERENTE_DE) ) {
							((ExpresionIgualdadContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(355);
						expresion(6);
						}
						break;
					case 5:
						{
						_localctx = new ExpresionYContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(356);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(357);
						match(Y_LOGICO);
						setState(358);
						expresion(5);
						}
						break;
					case 6:
						{
						_localctx = new ExpresionOContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(359);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(360);
						match(O_LOGICO);
						setState(361);
						expresion(4);
						}
						break;
					case 7:
						{
						_localctx = new ExpresionTernariaContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(362);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(363);
						match(INTERROGACION);
						setState(364);
						expresion(0);
						setState(365);
						match(DOS_PUNTOS);
						setState(366);
						expresion(2);
						}
						break;
					case 8:
						{
						_localctx = new ExpresionAsignacionContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(368);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(369);
						((ExpresionAsignacionContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 36029329594908672L) != 0)) ) {
							((ExpresionAsignacionContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(370);
						expresion(1);
						}
						break;
					case 9:
						{
						_localctx = new ExpresionLlamadaMetodoContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(371);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(372);
						match(PUNTO);
						setState(373);
						match(IDENTIFICADOR);
						setState(374);
						match(PARENTESIS_IZQUIERDO);
						setState(376);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
							{
							setState(375);
							argumentos();
							}
						}

						setState(378);
						match(PARENTESIS_DERECHO);
						}
						break;
					case 10:
						{
						_localctx = new ExpresionAtributoContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(379);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(380);
						match(PUNTO);
						setState(381);
						match(IDENTIFICADOR);
						}
						break;
					case 11:
						{
						_localctx = new ExpresionIndiceContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(382);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(383);
						match(CORCHETE_IZQUIERDO);
						setState(384);
						expresion(0);
						setState(385);
						match(CORCHETE_DERECHO);
						}
						break;
					case 12:
						{
						_localctx = new ExpresionPostfijaContext(new ExpresionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expresion);
						setState(387);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(388);
						((ExpresionPostfijaContext)_localctx).operador = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==INCREMENTO || _la==DECREMENTO) ) {
							((ExpresionPostfijaContext)_localctx).operador = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					}
					} 
				}
				setState(393);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioContext extends ParserRuleContext {
		public PrimarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primario; }
	 
		public PrimarioContext() { }
		public void copyFrom(PrimarioContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioThisContext extends PrimarioContext {
		public TerminalNode THIS() { return getToken(ZetarianoParser.THIS, 0); }
		public PrimarioThisContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioThis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioThis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioThis(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioReadlnContext extends PrimarioContext {
		public TerminalNode READLN() { return getToken(ZetarianoParser.READLN, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public PrimarioReadlnContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioReadln(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioReadln(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioReadln(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioNuevoObjetoContext extends PrimarioContext {
		public TerminalNode NEW() { return getToken(ZetarianoParser.NEW, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public ArgumentosContext argumentos() {
			return getRuleContext(ArgumentosContext.class,0);
		}
		public PrimarioNuevoObjetoContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioNuevoObjeto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioNuevoObjeto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioNuevoObjeto(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioNuevoArregloContext extends PrimarioContext {
		public TerminalNode NEW() { return getToken(ZetarianoParser.NEW, 0); }
		public TipoBaseContext tipoBase() {
			return getRuleContext(TipoBaseContext.class,0);
		}
		public List<TerminalNode> CORCHETE_IZQUIERDO() { return getTokens(ZetarianoParser.CORCHETE_IZQUIERDO); }
		public TerminalNode CORCHETE_IZQUIERDO(int i) {
			return getToken(ZetarianoParser.CORCHETE_IZQUIERDO, i);
		}
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public List<TerminalNode> CORCHETE_DERECHO() { return getTokens(ZetarianoParser.CORCHETE_DERECHO); }
		public TerminalNode CORCHETE_DERECHO(int i) {
			return getToken(ZetarianoParser.CORCHETE_DERECHO, i);
		}
		public PrimarioNuevoArregloContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioNuevoArreglo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioNuevoArreglo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioNuevoArreglo(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioSuperAtributoContext extends PrimarioContext {
		public TerminalNode SUPER() { return getToken(ZetarianoParser.SUPER, 0); }
		public TerminalNode PUNTO() { return getToken(ZetarianoParser.PUNTO, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public PrimarioSuperAtributoContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioSuperAtributo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioSuperAtributo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioSuperAtributo(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioLlamadaContext extends PrimarioContext {
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public ArgumentosContext argumentos() {
			return getRuleContext(ArgumentosContext.class,0);
		}
		public PrimarioLlamadaContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioLlamada(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioLlamada(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioLlamada(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioIdentificadorContext extends PrimarioContext {
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public PrimarioIdentificadorContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioIdentificador(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioIdentificador(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioIdentificador(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioParentesisContext extends PrimarioContext {
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public ExpresionContext expresion() {
			return getRuleContext(ExpresionContext.class,0);
		}
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public PrimarioParentesisContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioParentesis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioParentesis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioParentesis(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioLiteralContext extends PrimarioContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public PrimarioLiteralContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioNuevoArregloInicializadoContext extends PrimarioContext {
		public TerminalNode NEW() { return getToken(ZetarianoParser.NEW, 0); }
		public TipoBaseContext tipoBase() {
			return getRuleContext(TipoBaseContext.class,0);
		}
		public InicializadorArregloContext inicializadorArreglo() {
			return getRuleContext(InicializadorArregloContext.class,0);
		}
		public List<TerminalNode> CORCHETE_IZQUIERDO() { return getTokens(ZetarianoParser.CORCHETE_IZQUIERDO); }
		public TerminalNode CORCHETE_IZQUIERDO(int i) {
			return getToken(ZetarianoParser.CORCHETE_IZQUIERDO, i);
		}
		public List<TerminalNode> CORCHETE_DERECHO() { return getTokens(ZetarianoParser.CORCHETE_DERECHO); }
		public TerminalNode CORCHETE_DERECHO(int i) {
			return getToken(ZetarianoParser.CORCHETE_DERECHO, i);
		}
		public PrimarioNuevoArregloInicializadoContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioNuevoArregloInicializado(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioNuevoArregloInicializado(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioNuevoArregloInicializado(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrimarioSuperMetodoContext extends PrimarioContext {
		public TerminalNode SUPER() { return getToken(ZetarianoParser.SUPER, 0); }
		public TerminalNode PUNTO() { return getToken(ZetarianoParser.PUNTO, 0); }
		public TerminalNode IDENTIFICADOR() { return getToken(ZetarianoParser.IDENTIFICADOR, 0); }
		public TerminalNode PARENTESIS_IZQUIERDO() { return getToken(ZetarianoParser.PARENTESIS_IZQUIERDO, 0); }
		public TerminalNode PARENTESIS_DERECHO() { return getToken(ZetarianoParser.PARENTESIS_DERECHO, 0); }
		public ArgumentosContext argumentos() {
			return getRuleContext(ArgumentosContext.class,0);
		}
		public PrimarioSuperMetodoContext(PrimarioContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterPrimarioSuperMetodo(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitPrimarioSuperMetodo(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitPrimarioSuperMetodo(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimarioContext primario() throws RecognitionException {
		PrimarioContext _localctx = new PrimarioContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_primario);
		int _la;
		try {
			int _alt;
			setState(455);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				_localctx = new PrimarioParentesisContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(394);
				match(PARENTESIS_IZQUIERDO);
				setState(395);
				expresion(0);
				setState(396);
				match(PARENTESIS_DERECHO);
				}
				break;
			case 2:
				_localctx = new PrimarioLiteralContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(398);
				literal();
				}
				break;
			case 3:
				_localctx = new PrimarioThisContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(399);
				match(THIS);
				}
				break;
			case 4:
				_localctx = new PrimarioSuperMetodoContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(400);
				match(SUPER);
				setState(401);
				match(PUNTO);
				setState(402);
				match(IDENTIFICADOR);
				setState(403);
				match(PARENTESIS_IZQUIERDO);
				setState(405);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(404);
					argumentos();
					}
				}

				setState(407);
				match(PARENTESIS_DERECHO);
				}
				break;
			case 5:
				_localctx = new PrimarioSuperAtributoContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(408);
				match(SUPER);
				setState(409);
				match(PUNTO);
				setState(410);
				match(IDENTIFICADOR);
				}
				break;
			case 6:
				_localctx = new PrimarioLlamadaContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(411);
				match(IDENTIFICADOR);
				setState(412);
				match(PARENTESIS_IZQUIERDO);
				setState(414);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(413);
					argumentos();
					}
				}

				setState(416);
				match(PARENTESIS_DERECHO);
				}
				break;
			case 7:
				_localctx = new PrimarioIdentificadorContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(417);
				match(IDENTIFICADOR);
				}
				break;
			case 8:
				_localctx = new PrimarioNuevoObjetoContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(418);
				match(NEW);
				setState(419);
				match(IDENTIFICADOR);
				setState(420);
				match(PARENTESIS_IZQUIERDO);
				setState(422);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 13)) & ~0x3f) == 0 && ((1L << (_la - 13)) & 562950395803467839L) != 0)) {
					{
					setState(421);
					argumentos();
					}
				}

				setState(424);
				match(PARENTESIS_DERECHO);
				}
				break;
			case 9:
				_localctx = new PrimarioNuevoArregloContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(425);
				match(NEW);
				setState(426);
				tipoBase();
				setState(431); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(427);
						match(CORCHETE_IZQUIERDO);
						setState(428);
						expresion(0);
						setState(429);
						match(CORCHETE_DERECHO);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(433); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				setState(439);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(435);
						match(CORCHETE_IZQUIERDO);
						setState(436);
						match(CORCHETE_DERECHO);
						}
						} 
					}
					setState(441);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,45,_ctx);
				}
				}
				break;
			case 10:
				_localctx = new PrimarioNuevoArregloInicializadoContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(442);
				match(NEW);
				setState(443);
				tipoBase();
				setState(446); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(444);
					match(CORCHETE_IZQUIERDO);
					setState(445);
					match(CORCHETE_DERECHO);
					}
					}
					setState(448); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==CORCHETE_IZQUIERDO );
				setState(450);
				inicializadorArreglo();
				}
				break;
			case 11:
				_localctx = new PrimarioReadlnContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(452);
				match(READLN);
				setState(453);
				match(PARENTESIS_IZQUIERDO);
				setState(454);
				match(PARENTESIS_DERECHO);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentosContext extends ParserRuleContext {
		public List<ExpresionContext> expresion() {
			return getRuleContexts(ExpresionContext.class);
		}
		public ExpresionContext expresion(int i) {
			return getRuleContext(ExpresionContext.class,i);
		}
		public List<TerminalNode> COMA() { return getTokens(ZetarianoParser.COMA); }
		public TerminalNode COMA(int i) {
			return getToken(ZetarianoParser.COMA, i);
		}
		public ArgumentosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterArgumentos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitArgumentos(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitArgumentos(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentosContext argumentos() throws RecognitionException {
		ArgumentosContext _localctx = new ArgumentosContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_argumentos);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(457);
			expresion(0);
			setState(462);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMA) {
				{
				{
				setState(458);
				match(COMA);
				setState(459);
				expresion(0);
				}
				}
				setState(464);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode ENTERO() { return getToken(ZetarianoParser.ENTERO, 0); }
		public TerminalNode DECIMAL() { return getToken(ZetarianoParser.DECIMAL, 0); }
		public TerminalNode TEXTO() { return getToken(ZetarianoParser.TEXTO, 0); }
		public TerminalNode CARACTER() { return getToken(ZetarianoParser.CARACTER, 0); }
		public TerminalNode TRUE() { return getToken(ZetarianoParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(ZetarianoParser.FALSE, 0); }
		public TerminalNode NULL() { return getToken(ZetarianoParser.NULL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ZetarianoListener ) ((ZetarianoListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ZetarianoVisitor ) return ((ZetarianoVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(465);
			_la = _input.LA(1);
			if ( !(((((_la - 16)) & ~0x3f) == 0 && ((1L << (_la - 16)) & 33776997205278727L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 24:
			return expresion_sempred((ExpresionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expresion_sempred(ExpresionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		case 4:
			return precpred(_ctx, 4);
		case 5:
			return precpred(_ctx, 3);
		case 6:
			return precpred(_ctx, 2);
		case 7:
			return precpred(_ctx, 1);
		case 8:
			return precpred(_ctx, 13);
		case 9:
			return precpred(_ctx, 12);
		case 10:
			return precpred(_ctx, 11);
		case 11:
			return precpred(_ctx, 10);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001N\u01d4\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0005\u0001=\b\u0001"+
		"\n\u0001\f\u0001@\t\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0003\u0001F\b\u0001\u0001\u0001\u0001\u0001\u0005\u0001J\b\u0001\n\u0001"+
		"\f\u0001M\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0003\u0003V\b\u0003\u0001\u0004\u0005"+
		"\u0004Y\b\u0004\n\u0004\f\u0004\\\t\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0005\u0004b\b\u0004\n\u0004\f\u0004e\t\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0005\u0005j\b\u0005\n\u0005\f\u0005m\t"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005r\b\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006y\b"+
		"\u0006\u0001\u0006\u0005\u0006|\b\u0006\n\u0006\f\u0006\u007f\t\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007"+
		"\u0086\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0003\b\u008c"+
		"\b\b\u0001\b\u0005\b\u008f\b\b\n\b\f\b\u0092\t\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0003\b\u0098\b\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0003"+
		"\t\u009f\b\t\u0001\n\u0001\n\u0001\n\u0005\n\u00a4\b\n\n\n\f\n\u00a7\t"+
		"\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0005\f"+
		"\u00af\b\f\n\f\f\f\u00b2\t\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0003\u000e\u00b9\b\u000e\u0001\u000f\u0001\u000f\u0003\u000f\u00bd"+
		"\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010\u00c3"+
		"\b\u0010\n\u0010\f\u0010\u00c6\t\u0010\u0001\u0010\u0003\u0010\u00c9\b"+
		"\u0010\u0003\u0010\u00cb\b\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001"+
		"\u0011\u0005\u0011\u00d1\b\u0011\n\u0011\f\u0011\u00d4\t\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0003\u0012\u00e3\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0005\u0012\u00eb\b\u0012\n\u0012\f\u0012\u00ee"+
		"\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003"+
		"\u0012\u00f5\b\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00f9\b\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u00fd\b\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u0115\b\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0003\u0012\u011b\b\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u0129\b\u0012"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u012f\b\u0013"+
		"\n\u0013\f\u0013\u0132\t\u0013\u0001\u0014\u0004\u0014\u0135\b\u0014\u000b"+
		"\u0014\f\u0014\u0136\u0001\u0014\u0005\u0014\u013a\b\u0014\n\u0014\f\u0014"+
		"\u013d\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0003\u0015\u0145\b\u0015\u0001\u0016\u0001\u0016\u0003\u0016"+
		"\u0149\b\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0005\u0017\u014e\b"+
		"\u0017\n\u0017\f\u0017\u0151\t\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0003\u0018\u0157\b\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0003\u0018"+
		"\u0179\b\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0005\u0018\u0186\b\u0018\n\u0018\f\u0018\u0189\t\u0018\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u0196\b\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0003\u0019\u019f\b\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0003\u0019\u01a7\b\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0004"+
		"\u0019\u01b0\b\u0019\u000b\u0019\f\u0019\u01b1\u0001\u0019\u0001\u0019"+
		"\u0005\u0019\u01b6\b\u0019\n\u0019\f\u0019\u01b9\t\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0004\u0019\u01bf\b\u0019\u000b\u0019\f"+
		"\u0019\u01c0\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019"+
		"\u0003\u0019\u01c8\b\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0005\u001a"+
		"\u01cd\b\u001a\n\u001a\f\u001a\u01d0\t\u001a\u0001\u001b\u0001\u001b\u0001"+
		"\u001b\u0000\u00010\u001c\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012"+
		"\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.0246\u0000\n\u0001\u0000"+
		"\u0001\u0004\u0002\u0000\b\fGG\u0002\u0000-/23\u0001\u000046\u0001\u0000"+
		"23\u0002\u0000)*01\u0001\u0000\'(\u0002\u0000\"&77\u0001\u0000-.\u0002"+
		"\u0000\u0010\u0012CF\u0208\u00008\u0001\u0000\u0000\u0000\u0002>\u0001"+
		"\u0000\u0000\u0000\u0004P\u0001\u0000\u0000\u0000\u0006U\u0001\u0000\u0000"+
		"\u0000\bZ\u0001\u0000\u0000\u0000\nk\u0001\u0000\u0000\u0000\fv\u0001"+
		"\u0000\u0000\u0000\u000e\u0082\u0001\u0000\u0000\u0000\u0010\u008b\u0001"+
		"\u0000\u0000\u0000\u0012\u009e\u0001\u0000\u0000\u0000\u0014\u00a0\u0001"+
		"\u0000\u0000\u0000\u0016\u00a8\u0001\u0000\u0000\u0000\u0018\u00ab\u0001"+
		"\u0000\u0000\u0000\u001a\u00b3\u0001\u0000\u0000\u0000\u001c\u00b5\u0001"+
		"\u0000\u0000\u0000\u001e\u00bc\u0001\u0000\u0000\u0000 \u00be\u0001\u0000"+
		"\u0000\u0000\"\u00ce\u0001\u0000\u0000\u0000$\u0128\u0001\u0000\u0000"+
		"\u0000&\u012a\u0001\u0000\u0000\u0000(\u0134\u0001\u0000\u0000\u0000*"+
		"\u0144\u0001\u0000\u0000\u0000,\u0148\u0001\u0000\u0000\u0000.\u014a\u0001"+
		"\u0000\u0000\u00000\u0156\u0001\u0000\u0000\u00002\u01c7\u0001\u0000\u0000"+
		"\u00004\u01c9\u0001\u0000\u0000\u00006\u01d1\u0001\u0000\u0000\u00008"+
		"9\u0003\u0002\u0001\u00009:\u0005\u0000\u0000\u0001:\u0001\u0001\u0000"+
		"\u0000\u0000;=\u0003\u0004\u0002\u0000<;\u0001\u0000\u0000\u0000=@\u0001"+
		"\u0000\u0000\u0000><\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000"+
		"?A\u0001\u0000\u0000\u0000@>\u0001\u0000\u0000\u0000AB\u0005\u0005\u0000"+
		"\u0000BE\u0005G\u0000\u0000CD\u0005\u0006\u0000\u0000DF\u0005G\u0000\u0000"+
		"EC\u0001\u0000\u0000\u0000EF\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000"+
		"\u0000GK\u0005?\u0000\u0000HJ\u0003\u0006\u0003\u0000IH\u0001\u0000\u0000"+
		"\u0000JM\u0001\u0000\u0000\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000"+
		"\u0000\u0000LN\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000NO\u0005"+
		"@\u0000\u0000O\u0003\u0001\u0000\u0000\u0000PQ\u0007\u0000\u0000\u0000"+
		"Q\u0005\u0001\u0000\u0000\u0000RV\u0003\b\u0004\u0000SV\u0003\n\u0005"+
		"\u0000TV\u0003\u0010\b\u0000UR\u0001\u0000\u0000\u0000US\u0001\u0000\u0000"+
		"\u0000UT\u0001\u0000\u0000\u0000V\u0007\u0001\u0000\u0000\u0000WY\u0003"+
		"\u0004\u0002\u0000XW\u0001\u0000\u0000\u0000Y\\\u0001\u0000\u0000\u0000"+
		"ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000[]\u0001\u0000\u0000"+
		"\u0000\\Z\u0001\u0000\u0000\u0000]^\u0003\u0018\f\u0000^c\u0003\u001c"+
		"\u000e\u0000_`\u0005;\u0000\u0000`b\u0003\u001c\u000e\u0000a_\u0001\u0000"+
		"\u0000\u0000be\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cd\u0001"+
		"\u0000\u0000\u0000df\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000\u0000"+
		"fg\u0005:\u0000\u0000g\t\u0001\u0000\u0000\u0000hj\u0003\u0004\u0002\u0000"+
		"ih\u0001\u0000\u0000\u0000jm\u0001\u0000\u0000\u0000ki\u0001\u0000\u0000"+
		"\u0000kl\u0001\u0000\u0000\u0000ln\u0001\u0000\u0000\u0000mk\u0001\u0000"+
		"\u0000\u0000no\u0005G\u0000\u0000oq\u0005A\u0000\u0000pr\u0003\u0014\n"+
		"\u0000qp\u0001\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rs\u0001\u0000"+
		"\u0000\u0000st\u0005B\u0000\u0000tu\u0003\f\u0006\u0000u\u000b\u0001\u0000"+
		"\u0000\u0000vx\u0005?\u0000\u0000wy\u0003\u000e\u0007\u0000xw\u0001\u0000"+
		"\u0000\u0000xy\u0001\u0000\u0000\u0000y}\u0001\u0000\u0000\u0000z|\u0003"+
		"$\u0012\u0000{z\u0001\u0000\u0000\u0000|\u007f\u0001\u0000\u0000\u0000"+
		"}{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~\u0080\u0001\u0000"+
		"\u0000\u0000\u007f}\u0001\u0000\u0000\u0000\u0080\u0081\u0005@\u0000\u0000"+
		"\u0081\r\u0001\u0000\u0000\u0000\u0082\u0083\u0005\u000f\u0000\u0000\u0083"+
		"\u0085\u0005A\u0000\u0000\u0084\u0086\u00034\u001a\u0000\u0085\u0084\u0001"+
		"\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086\u0087\u0001"+
		"\u0000\u0000\u0000\u0087\u0088\u0005B\u0000\u0000\u0088\u0089\u0005:\u0000"+
		"\u0000\u0089\u000f\u0001\u0000\u0000\u0000\u008a\u008c\u0005!\u0000\u0000"+
		"\u008b\u008a\u0001\u0000\u0000\u0000\u008b\u008c\u0001\u0000\u0000\u0000"+
		"\u008c\u0090\u0001\u0000\u0000\u0000\u008d\u008f\u0003\u0004\u0002\u0000"+
		"\u008e\u008d\u0001\u0000\u0000\u0000\u008f\u0092\u0001\u0000\u0000\u0000"+
		"\u0090\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000\u0000"+
		"\u0091\u0093\u0001\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000"+
		"\u0093\u0094\u0003\u0012\t\u0000\u0094\u0095\u0005G\u0000\u0000\u0095"+
		"\u0097\u0005A\u0000\u0000\u0096\u0098\u0003\u0014\n\u0000\u0097\u0096"+
		"\u0001\u0000\u0000\u0000\u0097\u0098\u0001\u0000\u0000\u0000\u0098\u0099"+
		"\u0001\u0000\u0000\u0000\u0099\u009a\u0005B\u0000\u0000\u009a\u009b\u0003"+
		"\"\u0011\u0000\u009b\u0011\u0001\u0000\u0000\u0000\u009c\u009f\u0003\u0018"+
		"\f\u0000\u009d\u009f\u0005\u0007\u0000\u0000\u009e\u009c\u0001\u0000\u0000"+
		"\u0000\u009e\u009d\u0001\u0000\u0000\u0000\u009f\u0013\u0001\u0000\u0000"+
		"\u0000\u00a0\u00a5\u0003\u0016\u000b\u0000\u00a1\u00a2\u0005;\u0000\u0000"+
		"\u00a2\u00a4\u0003\u0016\u000b\u0000\u00a3\u00a1\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a7\u0001\u0000\u0000\u0000\u00a5\u00a3\u0001\u0000\u0000\u0000"+
		"\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u0015\u0001\u0000\u0000\u0000"+
		"\u00a7\u00a5\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003\u0018\f\u0000\u00a9"+
		"\u00aa\u0005G\u0000\u0000\u00aa\u0017\u0001\u0000\u0000\u0000\u00ab\u00b0"+
		"\u0003\u001a\r\u0000\u00ac\u00ad\u0005=\u0000\u0000\u00ad\u00af\u0005"+
		">\u0000\u0000\u00ae\u00ac\u0001\u0000\u0000\u0000\u00af\u00b2\u0001\u0000"+
		"\u0000\u0000\u00b0\u00ae\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000"+
		"\u0000\u0000\u00b1\u0019\u0001\u0000\u0000\u0000\u00b2\u00b0\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b4\u0007\u0001\u0000\u0000\u00b4\u001b\u0001\u0000"+
		"\u0000\u0000\u00b5\u00b8\u0005G\u0000\u0000\u00b6\u00b7\u00057\u0000\u0000"+
		"\u00b7\u00b9\u0003\u001e\u000f\u0000\u00b8\u00b6\u0001\u0000\u0000\u0000"+
		"\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u001d\u0001\u0000\u0000\u0000"+
		"\u00ba\u00bd\u00030\u0018\u0000\u00bb\u00bd\u0003 \u0010\u0000\u00bc\u00ba"+
		"\u0001\u0000\u0000\u0000\u00bc\u00bb\u0001\u0000\u0000\u0000\u00bd\u001f"+
		"\u0001\u0000\u0000\u0000\u00be\u00ca\u0005?\u0000\u0000\u00bf\u00c4\u0003"+
		"\u001e\u000f\u0000\u00c0\u00c1\u0005;\u0000\u0000\u00c1\u00c3\u0003\u001e"+
		"\u000f\u0000\u00c2\u00c0\u0001\u0000\u0000\u0000\u00c3\u00c6\u0001\u0000"+
		"\u0000\u0000\u00c4\u00c2\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000"+
		"\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6\u00c4\u0001\u0000"+
		"\u0000\u0000\u00c7\u00c9\u0005;\u0000\u0000\u00c8\u00c7\u0001\u0000\u0000"+
		"\u0000\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00cb\u0001\u0000\u0000"+
		"\u0000\u00ca\u00bf\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000"+
		"\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000\u00cc\u00cd\u0005@\u0000\u0000"+
		"\u00cd!\u0001\u0000\u0000\u0000\u00ce\u00d2\u0005?\u0000\u0000\u00cf\u00d1"+
		"\u0003$\u0012\u0000\u00d0\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d4\u0001"+
		"\u0000\u0000\u0000\u00d2\u00d0\u0001\u0000\u0000\u0000\u00d2\u00d3\u0001"+
		"\u0000\u0000\u0000\u00d3\u00d5\u0001\u0000\u0000\u0000\u00d4\u00d2\u0001"+
		"\u0000\u0000\u0000\u00d5\u00d6\u0005@\u0000\u0000\u00d6#\u0001\u0000\u0000"+
		"\u0000\u00d7\u0129\u0003\"\u0011\u0000\u00d8\u00d9\u0003&\u0013\u0000"+
		"\u00d9\u00da\u0005:\u0000\u0000\u00da\u0129\u0001\u0000\u0000\u0000\u00db"+
		"\u00dc\u0005\u0013\u0000\u0000\u00dc\u00dd\u0005A\u0000\u0000\u00dd\u00de"+
		"\u00030\u0018\u0000\u00de\u00df\u0005B\u0000\u0000\u00df\u00e2\u0003$"+
		"\u0012\u0000\u00e0\u00e1\u0005\u0014\u0000\u0000\u00e1\u00e3\u0003$\u0012"+
		"\u0000\u00e2\u00e0\u0001\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000"+
		"\u0000\u00e3\u0129\u0001\u0000\u0000\u0000\u00e4\u00e5\u0005\u0015\u0000"+
		"\u0000\u00e5\u00e6\u0005A\u0000\u0000\u00e6\u00e7\u00030\u0018\u0000\u00e7"+
		"\u00e8\u0005B\u0000\u0000\u00e8\u00ec\u0005?\u0000\u0000\u00e9\u00eb\u0003"+
		"(\u0014\u0000\u00ea\u00e9\u0001\u0000\u0000\u0000\u00eb\u00ee\u0001\u0000"+
		"\u0000\u0000\u00ec\u00ea\u0001\u0000\u0000\u0000\u00ec\u00ed\u0001\u0000"+
		"\u0000\u0000\u00ed\u00ef\u0001\u0000\u0000\u0000\u00ee\u00ec\u0001\u0000"+
		"\u0000\u0000\u00ef\u00f0\u0005@\u0000\u0000\u00f0\u0129\u0001\u0000\u0000"+
		"\u0000\u00f1\u00f2\u0005\u0018\u0000\u0000\u00f2\u00f4\u0005A\u0000\u0000"+
		"\u00f3\u00f5\u0003,\u0016\u0000\u00f4\u00f3\u0001\u0000\u0000\u0000\u00f4"+
		"\u00f5\u0001\u0000\u0000\u0000\u00f5\u00f6\u0001\u0000\u0000\u0000\u00f6"+
		"\u00f8\u0005:\u0000\u0000\u00f7\u00f9\u00030\u0018\u0000\u00f8\u00f7\u0001"+
		"\u0000\u0000\u0000\u00f8\u00f9\u0001\u0000\u0000\u0000\u00f9\u00fa\u0001"+
		"\u0000\u0000\u0000\u00fa\u00fc\u0005:\u0000\u0000\u00fb\u00fd\u0003.\u0017"+
		"\u0000\u00fc\u00fb\u0001\u0000\u0000\u0000\u00fc\u00fd\u0001\u0000\u0000"+
		"\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u00ff\u0005B\u0000\u0000"+
		"\u00ff\u0129\u0003$\u0012\u0000\u0100\u0101\u0005\u0019\u0000\u0000\u0101"+
		"\u0102\u0005A\u0000\u0000\u0102\u0103\u00030\u0018\u0000\u0103\u0104\u0005"+
		"B\u0000\u0000\u0104\u0105\u0003$\u0012\u0000\u0105\u0129\u0001\u0000\u0000"+
		"\u0000\u0106\u0107\u0005\u001a\u0000\u0000\u0107\u0108\u0003$\u0012\u0000"+
		"\u0108\u0109\u0005\u0019\u0000\u0000\u0109\u010a\u0005A\u0000\u0000\u010a"+
		"\u010b\u00030\u0018\u0000\u010b\u010c\u0005B\u0000\u0000\u010c\u010d\u0005"+
		":\u0000\u0000\u010d\u0129\u0001\u0000\u0000\u0000\u010e\u010f\u0005\u001b"+
		"\u0000\u0000\u010f\u0129\u0005:\u0000\u0000\u0110\u0111\u0005\u001c\u0000"+
		"\u0000\u0111\u0129\u0005:\u0000\u0000\u0112\u0114\u0005\u001d\u0000\u0000"+
		"\u0113\u0115\u00030\u0018\u0000\u0114\u0113\u0001\u0000\u0000\u0000\u0114"+
		"\u0115\u0001\u0000\u0000\u0000\u0115\u0116\u0001\u0000\u0000\u0000\u0116"+
		"\u0129\u0005:\u0000\u0000\u0117\u0118\u0005\u001e\u0000\u0000\u0118\u011a"+
		"\u0005A\u0000\u0000\u0119\u011b\u00030\u0018\u0000\u011a\u0119\u0001\u0000"+
		"\u0000\u0000\u011a\u011b\u0001\u0000\u0000\u0000\u011b\u011c\u0001\u0000"+
		"\u0000\u0000\u011c\u011d\u0005B\u0000\u0000\u011d\u0129\u0005:\u0000\u0000"+
		"\u011e\u011f\u0005\u001f\u0000\u0000\u011f\u0120\u0005A\u0000\u0000\u0120"+
		"\u0121\u00030\u0018\u0000\u0121\u0122\u0005B\u0000\u0000\u0122\u0123\u0005"+
		":\u0000\u0000\u0123\u0129\u0001\u0000\u0000\u0000\u0124\u0125\u00030\u0018"+
		"\u0000\u0125\u0126\u0005:\u0000\u0000\u0126\u0129\u0001\u0000\u0000\u0000"+
		"\u0127\u0129\u0005:\u0000\u0000\u0128\u00d7\u0001\u0000\u0000\u0000\u0128"+
		"\u00d8\u0001\u0000\u0000\u0000\u0128\u00db\u0001\u0000\u0000\u0000\u0128"+
		"\u00e4\u0001\u0000\u0000\u0000\u0128\u00f1\u0001\u0000\u0000\u0000\u0128"+
		"\u0100\u0001\u0000\u0000\u0000\u0128\u0106\u0001\u0000\u0000\u0000\u0128"+
		"\u010e\u0001\u0000\u0000\u0000\u0128\u0110\u0001\u0000\u0000\u0000\u0128"+
		"\u0112\u0001\u0000\u0000\u0000\u0128\u0117\u0001\u0000\u0000\u0000\u0128"+
		"\u011e\u0001\u0000\u0000\u0000\u0128\u0124\u0001\u0000\u0000\u0000\u0128"+
		"\u0127\u0001\u0000\u0000\u0000\u0129%\u0001\u0000\u0000\u0000\u012a\u012b"+
		"\u0003\u0018\f\u0000\u012b\u0130\u0003\u001c\u000e\u0000\u012c\u012d\u0005"+
		";\u0000\u0000\u012d\u012f\u0003\u001c\u000e\u0000\u012e\u012c\u0001\u0000"+
		"\u0000\u0000\u012f\u0132\u0001\u0000\u0000\u0000\u0130\u012e\u0001\u0000"+
		"\u0000\u0000\u0130\u0131\u0001\u0000\u0000\u0000\u0131\'\u0001\u0000\u0000"+
		"\u0000\u0132\u0130\u0001\u0000\u0000\u0000\u0133\u0135\u0003*\u0015\u0000"+
		"\u0134\u0133\u0001\u0000\u0000\u0000\u0135\u0136\u0001\u0000\u0000\u0000"+
		"\u0136\u0134\u0001\u0000\u0000\u0000\u0136\u0137\u0001\u0000\u0000\u0000"+
		"\u0137\u013b\u0001\u0000\u0000\u0000\u0138\u013a\u0003$\u0012\u0000\u0139"+
		"\u0138\u0001\u0000\u0000\u0000\u013a\u013d\u0001\u0000\u0000\u0000\u013b"+
		"\u0139\u0001\u0000\u0000\u0000\u013b\u013c\u0001\u0000\u0000\u0000\u013c"+
		")\u0001\u0000\u0000\u0000\u013d\u013b\u0001\u0000\u0000\u0000\u013e\u013f"+
		"\u0005\u0016\u0000\u0000\u013f\u0140\u00030\u0018\u0000\u0140\u0141\u0005"+
		"9\u0000\u0000\u0141\u0145\u0001\u0000\u0000\u0000\u0142\u0143\u0005\u0017"+
		"\u0000\u0000\u0143\u0145\u00059\u0000\u0000\u0144\u013e\u0001\u0000\u0000"+
		"\u0000\u0144\u0142\u0001\u0000\u0000\u0000\u0145+\u0001\u0000\u0000\u0000"+
		"\u0146\u0149\u0003&\u0013\u0000\u0147\u0149\u0003.\u0017\u0000\u0148\u0146"+
		"\u0001\u0000\u0000\u0000\u0148\u0147\u0001\u0000\u0000\u0000\u0149-\u0001"+
		"\u0000\u0000\u0000\u014a\u014f\u00030\u0018\u0000\u014b\u014c\u0005;\u0000"+
		"\u0000\u014c\u014e\u00030\u0018\u0000\u014d\u014b\u0001\u0000\u0000\u0000"+
		"\u014e\u0151\u0001\u0000\u0000\u0000\u014f\u014d\u0001\u0000\u0000\u0000"+
		"\u014f\u0150\u0001\u0000\u0000\u0000\u0150/\u0001\u0000\u0000\u0000\u0151"+
		"\u014f\u0001\u0000\u0000\u0000\u0152\u0153\u0006\u0018\uffff\uffff\u0000"+
		"\u0153\u0157\u00032\u0019\u0000\u0154\u0155\u0007\u0002\u0000\u0000\u0155"+
		"\u0157\u00030\u0018\t\u0156\u0152\u0001\u0000\u0000\u0000\u0156\u0154"+
		"\u0001\u0000\u0000\u0000\u0157\u0187\u0001\u0000\u0000\u0000\u0158\u0159"+
		"\n\b\u0000\u0000\u0159\u015a\u0007\u0003\u0000\u0000\u015a\u0186\u0003"+
		"0\u0018\t\u015b\u015c\n\u0007\u0000\u0000\u015c\u015d\u0007\u0004\u0000"+
		"\u0000\u015d\u0186\u00030\u0018\b\u015e\u015f\n\u0006\u0000\u0000\u015f"+
		"\u0160\u0007\u0005\u0000\u0000\u0160\u0186\u00030\u0018\u0007\u0161\u0162"+
		"\n\u0005\u0000\u0000\u0162\u0163\u0007\u0006\u0000\u0000\u0163\u0186\u0003"+
		"0\u0018\u0006\u0164\u0165\n\u0004\u0000\u0000\u0165\u0166\u0005+\u0000"+
		"\u0000\u0166\u0186\u00030\u0018\u0005\u0167\u0168\n\u0003\u0000\u0000"+
		"\u0168\u0169\u0005,\u0000\u0000\u0169\u0186\u00030\u0018\u0004\u016a\u016b"+
		"\n\u0002\u0000\u0000\u016b\u016c\u00058\u0000\u0000\u016c\u016d\u0003"+
		"0\u0018\u0000\u016d\u016e\u00059\u0000\u0000\u016e\u016f\u00030\u0018"+
		"\u0002\u016f\u0186\u0001\u0000\u0000\u0000\u0170\u0171\n\u0001\u0000\u0000"+
		"\u0171\u0172\u0007\u0007\u0000\u0000\u0172\u0186\u00030\u0018\u0001\u0173"+
		"\u0174\n\r\u0000\u0000\u0174\u0175\u0005<\u0000\u0000\u0175\u0176\u0005"+
		"G\u0000\u0000\u0176\u0178\u0005A\u0000\u0000\u0177\u0179\u00034\u001a"+
		"\u0000\u0178\u0177\u0001\u0000\u0000\u0000\u0178\u0179\u0001\u0000\u0000"+
		"\u0000\u0179\u017a\u0001\u0000\u0000\u0000\u017a\u0186\u0005B\u0000\u0000"+
		"\u017b\u017c\n\f\u0000\u0000\u017c\u017d\u0005<\u0000\u0000\u017d\u0186"+
		"\u0005G\u0000\u0000\u017e\u017f\n\u000b\u0000\u0000\u017f\u0180\u0005"+
		"=\u0000\u0000\u0180\u0181\u00030\u0018\u0000\u0181\u0182\u0005>\u0000"+
		"\u0000\u0182\u0186\u0001\u0000\u0000\u0000\u0183\u0184\n\n\u0000\u0000"+
		"\u0184\u0186\u0007\b\u0000\u0000\u0185\u0158\u0001\u0000\u0000\u0000\u0185"+
		"\u015b\u0001\u0000\u0000\u0000\u0185\u015e\u0001\u0000\u0000\u0000\u0185"+
		"\u0161\u0001\u0000\u0000\u0000\u0185\u0164\u0001\u0000\u0000\u0000\u0185"+
		"\u0167\u0001\u0000\u0000\u0000\u0185\u016a\u0001\u0000\u0000\u0000\u0185"+
		"\u0170\u0001\u0000\u0000\u0000\u0185\u0173\u0001\u0000\u0000\u0000\u0185"+
		"\u017b\u0001\u0000\u0000\u0000\u0185\u017e\u0001\u0000\u0000\u0000\u0185"+
		"\u0183\u0001\u0000\u0000\u0000\u0186\u0189\u0001\u0000\u0000\u0000\u0187"+
		"\u0185\u0001\u0000\u0000\u0000\u0187\u0188\u0001\u0000\u0000\u0000\u0188"+
		"1\u0001\u0000\u0000\u0000\u0189\u0187\u0001\u0000\u0000\u0000\u018a\u018b"+
		"\u0005A\u0000\u0000\u018b\u018c\u00030\u0018\u0000\u018c\u018d\u0005B"+
		"\u0000\u0000\u018d\u01c8\u0001\u0000\u0000\u0000\u018e\u01c8\u00036\u001b"+
		"\u0000\u018f\u01c8\u0005\u000e\u0000\u0000\u0190\u0191\u0005\u000f\u0000"+
		"\u0000\u0191\u0192\u0005<\u0000\u0000\u0192\u0193\u0005G\u0000\u0000\u0193"+
		"\u0195\u0005A\u0000\u0000\u0194\u0196\u00034\u001a\u0000\u0195\u0194\u0001"+
		"\u0000\u0000\u0000\u0195\u0196\u0001\u0000\u0000\u0000\u0196\u0197\u0001"+
		"\u0000\u0000\u0000\u0197\u01c8\u0005B\u0000\u0000\u0198\u0199\u0005\u000f"+
		"\u0000\u0000\u0199\u019a\u0005<\u0000\u0000\u019a\u01c8\u0005G\u0000\u0000"+
		"\u019b\u019c\u0005G\u0000\u0000\u019c\u019e\u0005A\u0000\u0000\u019d\u019f"+
		"\u00034\u001a\u0000\u019e\u019d\u0001\u0000\u0000\u0000\u019e\u019f\u0001"+
		"\u0000\u0000\u0000\u019f\u01a0\u0001\u0000\u0000\u0000\u01a0\u01c8\u0005"+
		"B\u0000\u0000\u01a1\u01c8\u0005G\u0000\u0000\u01a2\u01a3\u0005\r\u0000"+
		"\u0000\u01a3\u01a4\u0005G\u0000\u0000\u01a4\u01a6\u0005A\u0000\u0000\u01a5"+
		"\u01a7\u00034\u001a\u0000\u01a6\u01a5\u0001\u0000\u0000\u0000\u01a6\u01a7"+
		"\u0001\u0000\u0000\u0000\u01a7\u01a8\u0001\u0000\u0000\u0000\u01a8\u01c8"+
		"\u0005B\u0000\u0000\u01a9\u01aa\u0005\r\u0000\u0000\u01aa\u01af\u0003"+
		"\u001a\r\u0000\u01ab\u01ac\u0005=\u0000\u0000\u01ac\u01ad\u00030\u0018"+
		"\u0000\u01ad\u01ae\u0005>\u0000\u0000\u01ae\u01b0\u0001\u0000\u0000\u0000"+
		"\u01af\u01ab\u0001\u0000\u0000\u0000\u01b0\u01b1\u0001\u0000\u0000\u0000"+
		"\u01b1\u01af\u0001\u0000\u0000\u0000\u01b1\u01b2\u0001\u0000\u0000\u0000"+
		"\u01b2\u01b7\u0001\u0000\u0000\u0000\u01b3\u01b4\u0005=\u0000\u0000\u01b4"+
		"\u01b6\u0005>\u0000\u0000\u01b5\u01b3\u0001\u0000\u0000\u0000\u01b6\u01b9"+
		"\u0001\u0000\u0000\u0000\u01b7\u01b5\u0001\u0000\u0000\u0000\u01b7\u01b8"+
		"\u0001\u0000\u0000\u0000\u01b8\u01c8\u0001\u0000\u0000\u0000\u01b9\u01b7"+
		"\u0001\u0000\u0000\u0000\u01ba\u01bb\u0005\r\u0000\u0000\u01bb\u01be\u0003"+
		"\u001a\r\u0000\u01bc\u01bd\u0005=\u0000\u0000\u01bd\u01bf\u0005>\u0000"+
		"\u0000\u01be\u01bc\u0001\u0000\u0000\u0000\u01bf\u01c0\u0001\u0000\u0000"+
		"\u0000\u01c0\u01be\u0001\u0000\u0000\u0000\u01c0\u01c1\u0001\u0000\u0000"+
		"\u0000\u01c1\u01c2\u0001\u0000\u0000\u0000\u01c2\u01c3\u0003 \u0010\u0000"+
		"\u01c3\u01c8\u0001\u0000\u0000\u0000\u01c4\u01c5\u0005 \u0000\u0000\u01c5"+
		"\u01c6\u0005A\u0000\u0000\u01c6\u01c8\u0005B\u0000\u0000\u01c7\u018a\u0001"+
		"\u0000\u0000\u0000\u01c7\u018e\u0001\u0000\u0000\u0000\u01c7\u018f\u0001"+
		"\u0000\u0000\u0000\u01c7\u0190\u0001\u0000\u0000\u0000\u01c7\u0198\u0001"+
		"\u0000\u0000\u0000\u01c7\u019b\u0001\u0000\u0000\u0000\u01c7\u01a1\u0001"+
		"\u0000\u0000\u0000\u01c7\u01a2\u0001\u0000\u0000\u0000\u01c7\u01a9\u0001"+
		"\u0000\u0000\u0000\u01c7\u01ba\u0001\u0000\u0000\u0000\u01c7\u01c4\u0001"+
		"\u0000\u0000\u0000\u01c83\u0001\u0000\u0000\u0000\u01c9\u01ce\u00030\u0018"+
		"\u0000\u01ca\u01cb\u0005;\u0000\u0000\u01cb\u01cd\u00030\u0018\u0000\u01cc"+
		"\u01ca\u0001\u0000\u0000\u0000\u01cd\u01d0\u0001\u0000\u0000\u0000\u01ce"+
		"\u01cc\u0001\u0000\u0000\u0000\u01ce\u01cf\u0001\u0000\u0000\u0000\u01cf"+
		"5\u0001\u0000\u0000\u0000\u01d0\u01ce\u0001\u0000\u0000\u0000\u01d1\u01d2"+
		"\u0007\t\u0000\u0000\u01d27\u0001\u0000\u0000\u00001>EKUZckqx}\u0085\u008b"+
		"\u0090\u0097\u009e\u00a5\u00b0\u00b8\u00bc\u00c4\u00c8\u00ca\u00d2\u00e2"+
		"\u00ec\u00f4\u00f8\u00fc\u0114\u011a\u0128\u0130\u0136\u013b\u0144\u0148"+
		"\u014f\u0156\u0178\u0185\u0187\u0195\u019e\u01a6\u01b1\u01b7\u01c0\u01c7"+
		"\u01ce";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}