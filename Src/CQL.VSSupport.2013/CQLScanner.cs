using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Antlr4.Runtime;
using CQL;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

namespace ONC.CQL_VSSupport_2013
{
    internal class CQLScanner : IScanner
    {
        private IVsTextBuffer m_buffer;
        string m_source;
		IList<IToken> m_tokens;
		int m_tokenIndex;

        public CQLScanner(IVsTextBuffer buffer)
        {
            m_buffer = buffer;
        }

		public IToken GetToken(int tokenIndex)
		{
			if (m_tokens != null && tokenIndex >= 0 && tokenIndex < m_tokens.Count)
			{
				return m_tokens[tokenIndex];
			}
			return null;
		}

        bool IScanner.ScanTokenAndProvideInfoAboutIt(TokenInfo tokenInfo, ref int state)
        {
			if (m_tokens != null && m_tokenIndex >= 0 && m_tokenIndex < m_tokens.Count)
			{
				var token = m_tokens[m_tokenIndex];

				tokenInfo.Type = GetTokenType(token.Type);
				tokenInfo.Color = GetTokenColor(token.Type);
				tokenInfo.Trigger = GetTokenTriggers(token.Type);
				tokenInfo.StartIndex = token.StartIndex;
				tokenInfo.EndIndex = token.StopIndex;
				tokenInfo.Token = state;

				m_tokenIndex++;

				return m_tokenIndex <= m_tokens.Count;
			}

            tokenInfo.Type = TokenType.Unknown;
            tokenInfo.Color = TokenColor.Text;
            return false;
        }

		private TokenType GetTokenType(int tokenType)
		{
			switch (tokenType)
			{
				case 0 : return TokenType.Unknown; // "<INVALID>",
				case 1 : return TokenType.Keyword; // "'except'", 
				case 2 : return TokenType.Keyword; // "'like'", 
				case 3 : return TokenType.Keyword; // "'concept'", 
				case 4 : return TokenType.Keyword; // "'retrieve'", 
				case 5 : return TokenType.Literal; // "'u'", 
				case 6 : return TokenType.Keyword; // "'combine'", 
				case 7 : return TokenType.Operator; // "'['", 
				case 8 : return TokenType.Operator; // "'*'", 
				case 9 : return TokenType.Keyword; // "'or'", 
				case 10 : return TokenType.Operator; // "'<'", 
				case 11 : return TokenType.Keyword; // "'months'", 
				case 12 : return TokenType.Keyword; // "'convert'", 
				case 13 : return TokenType.Operator; // "'<='", 
				case 14 : return TokenType.Keyword; // "'properly'", 
				case 15 : return TokenType.Keyword; // "'before'", 
				case 16 : return TokenType.Keyword; // "'to'", 
				case 17 : return TokenType.Keyword; // "'unknown'", 
				case 18 : return TokenType.Operator; // "'}'", 
				case 19 : return TokenType.Keyword; // "'after'", 
				case 20 : return TokenType.Keyword; // "'overlaps'", 
				case 21 : return TokenType.Keyword; // "'case'", 
				case 22 : return TokenType.Keyword; // "'no'", 
				case 23 : return TokenType.Keyword; // "'union'", 
				case 24 : return TokenType.Operator; // "')'", 
				case 25 : return TokenType.Keyword; // "'parameter'", 
				case 26 : return TokenType.Keyword; // "'seconds'", 
				case 27 : return TokenType.Keyword; // "'interval'", 
				case 28 : return TokenType.Keyword; // "'during'", 
				case 29 : return TokenType.Keyword; // "'hours'", 
				case 30 : return TokenType.Operator; // "'='", 
				case 31 : return TokenType.Keyword; // "'within'", 
				case 32 : return TokenType.Keyword; // "'div'", 
				case 33 : return TokenType.Keyword; // "'meets'", 
				case 34 : return TokenType.Keyword; // "'days'", 
				case 35 : return TokenType.Keyword; // "'ends'", 
				case 36 : return TokenType.Keyword; // "'mod'", 
				case 37 : return TokenType.Keyword; // "'version'", 
				case 38 : return TokenType.Keyword; // "'when'", 
				case 39 : return TokenType.Keyword; // "'context'", 
				case 40 : return TokenType.Operator; // "']'", 
				case 41 : return TokenType.Keyword; // "'using'", 
				case 42 : return TokenType.Keyword; // "'default'", 
				case 43 : return TokenType.Keyword; // "'in'", 
				case 44 : return TokenType.Keyword; // "'includes'", 
				case 45 : return TokenType.Keyword; // "'of'", 
				case 46 : return TokenType.Delimiter; // "','", 
				case 47 : return TokenType.Operator; // "'-'", 
				case 48 : return TokenType.Keyword; // "'not'", 
				case 49 : return TokenType.Operator; // "':'", 
				case 50 : return TokenType.Operator; // "'('", 
				case 51 : return TokenType.Keyword; // "'if'", 
				case 52 : return TokenType.Keyword; // "'coalesce'", 
				case 53 : return TokenType.Keyword; // "'tuple'", 
				case 54 : return TokenType.Keyword; // "'as'", 
				case 55 : return TokenType.Operator; // "'{'", 
				case 56 : return TokenType.Keyword; // "'and'", 
				case 57 : return TokenType.Keyword; // "'let'", 
				case 58 : return TokenType.Keyword; // "'else'", 
				case 59 : return TokenType.Keyword; // "'years'", 
				case 60 : return TokenType.Keyword; // "'concurrent with'", 
				case 61 : return TokenType.Operator; // "'^'", 
				case 62 : return TokenType.Keyword; // "'is'", 
				case 63 : return TokenType.Keyword; // "'starts'", 
				case 64 : return TokenType.Operator; // "'.'", 
				case 65 : return TokenType.Operator; // "'+'", 
				case 66 : return TokenType.Keyword; // "'define'", 
				case 67 : return TokenType.Keyword; // "'list'", 
				case 68 : return TokenType.Keyword; // "'minutes'", 
				case 69 : return TokenType.Operator; // "'<>'", 
				case 70 : return TokenType.Keyword; // "'operator'", 
				case 71 : return TokenType.Keyword; // "'return'", 
				case 72 : return TokenType.Keyword; // "'start'", 
				case 73 : return TokenType.Keyword; // "'weeks'", 
				case 74 : return TokenType.Keyword; // "'with'", 
				case 75 : return TokenType.Keyword; // "'include'", 
				case 76 : return TokenType.Keyword; // "'exists'", 
				case 77 : return TokenType.Operator; // "'>'", 
				case 78 : return TokenType.Keyword; // "'started by'", 
				case 79 : return TokenType.Keyword; // "'intersect'", 
				case 80 : return TokenType.Keyword; // "'then'", 
				case 81 : return TokenType.Keyword; // "'where'", 
				case 82 : return TokenType.Operator; // "'/'", 
				case 83 : return TokenType.Keyword; // "'ended by'", 
				case 84 : return TokenType.Operator; // "'>='", 
				case 85 : return TokenType.Keyword; // "'end'", 
				case 86 : return TokenType.Identifier; // "IDENTIFIER", 
				case 87 : return TokenType.Literal; // "'null'", 
				case 88 : return TokenType.Literal; // "BOOLEAN", 
				case 89 : return TokenType.Literal; // "QUANTITY", 
				case 90 : return TokenType.Literal; // "STRING", 
				case 91 : return TokenType.WhiteSpace; // "WS", 
				case 92 : return TokenType.WhiteSpace; // "NEWLINE", 
				case 93 : return TokenType.Comment; // "COMMENT", 
				case 94 : return TokenType.LineComment; // "LINE_COMMENT"
				default : return TokenType.Unknown;
			}
		}

		private TokenColor GetTokenColor(int tokenType)
		{
			switch (tokenType)
			{
				case 0 : return TokenColor.Text; // "<INVALID>",
				case 1 : return TokenColor.Keyword; // "'except'", 
				case 2 : return TokenColor.Keyword; // "'like'", 
				case 3 : return TokenColor.Keyword; // "'concept'", 
				case 4 : return TokenColor.Keyword; // "'retrieve'", 
				case 5 : return TokenColor.Number; // "'u'", 
				case 6 : return TokenColor.Keyword; // "'combine'", 
				case 7 : return TokenColor.Text; // "'['", 
				case 8 : return TokenColor.Text; // "'*'", 
				case 9 : return TokenColor.Keyword; // "'or'", 
				case 10 : return TokenColor.Text; // "'<'", 
				case 11 : return TokenColor.Keyword; // "'months'", 
				case 12 : return TokenColor.Keyword; // "'convert'", 
				case 13 : return TokenColor.Text; // "'<='", 
				case 14 : return TokenColor.Keyword; // "'properly'", 
				case 15 : return TokenColor.Keyword; // "'before'", 
				case 16 : return TokenColor.Keyword; // "'to'", 
				case 17 : return TokenColor.Keyword; // "'unknown'", 
				case 18 : return TokenColor.Text; // "'}'", 
				case 19 : return TokenColor.Keyword; // "'after'", 
				case 20 : return TokenColor.Keyword; // "'overlaps'", 
				case 21 : return TokenColor.Keyword; // "'case'", 
				case 22 : return TokenColor.Keyword; // "'no'", 
				case 23 : return TokenColor.Keyword; // "'union'", 
				case 24 : return TokenColor.Text; // "')'", 
				case 25 : return TokenColor.Keyword; // "'parameter'", 
				case 26 : return TokenColor.Keyword; // "'seconds'", 
				case 27 : return TokenColor.Keyword; // "'interval'", 
				case 28 : return TokenColor.Keyword; // "'during'", 
				case 29 : return TokenColor.Keyword; // "'hours'", 
				case 30 : return TokenColor.Text; // "'='", 
				case 31 : return TokenColor.Keyword; // "'within'", 
				case 32 : return TokenColor.Keyword; // "'div'", 
				case 33 : return TokenColor.Keyword; // "'meets'", 
				case 34 : return TokenColor.Keyword; // "'days'", 
				case 35 : return TokenColor.Keyword; // "'ends'", 
				case 36 : return TokenColor.Keyword; // "'mod'", 
				case 37 : return TokenColor.Keyword; // "'version'", 
				case 38 : return TokenColor.Keyword; // "'when'", 
				case 39 : return TokenColor.Keyword; // "'context'", 
				case 40 : return TokenColor.Text; // "']'", 
				case 41 : return TokenColor.Keyword; // "'using'", 
				case 42 : return TokenColor.Keyword; // "'default'", 
				case 43 : return TokenColor.Keyword; // "'in'", 
				case 44 : return TokenColor.Keyword; // "'includes'", 
				case 45 : return TokenColor.Keyword; // "'of'", 
				case 46 : return TokenColor.Text; // "','", 
				case 47 : return TokenColor.Text; // "'-'", 
				case 48 : return TokenColor.Keyword; // "'not'", 
				case 49 : return TokenColor.Text; // "':'", 
				case 50 : return TokenColor.Text; // "'('", 
				case 51 : return TokenColor.Keyword; // "'if'", 
				case 52 : return TokenColor.Keyword; // "'coalesce'", 
				case 53 : return TokenColor.Keyword; // "'tuple'", 
				case 54 : return TokenColor.Keyword; // "'as'", 
				case 55 : return TokenColor.Text; // "'{'", 
				case 56 : return TokenColor.Keyword; // "'and'", 
				case 57 : return TokenColor.Keyword; // "'let'", 
				case 58 : return TokenColor.Keyword; // "'else'", 
				case 59 : return TokenColor.Keyword; // "'years'", 
				case 60 : return TokenColor.Keyword; // "'concurrent with'", 
				case 61 : return TokenColor.Text; // "'^'", 
				case 62 : return TokenColor.Keyword; // "'is'", 
				case 63 : return TokenColor.Keyword; // "'starts'", 
				case 64 : return TokenColor.Text; // "'.'", 
				case 65 : return TokenColor.Text; // "'+'", 
				case 66 : return TokenColor.Keyword; // "'define'", 
				case 67 : return TokenColor.Keyword; // "'list'", 
				case 68 : return TokenColor.Keyword; // "'minutes'", 
				case 69 : return TokenColor.Text; // "'<>'", 
				case 70 : return TokenColor.Keyword; // "'operator'", 
				case 71 : return TokenColor.Keyword; // "'return'", 
				case 72 : return TokenColor.Keyword; // "'start'", 
				case 73 : return TokenColor.Keyword; // "'weeks'", 
				case 74 : return TokenColor.Keyword; // "'with'", 
				case 75 : return TokenColor.Keyword; // "'include'", 
				case 76 : return TokenColor.Keyword; // "'exists'", 
				case 77 : return TokenColor.Text; // "'>'", 
				case 78 : return TokenColor.Keyword; // "'started by'", 
				case 79 : return TokenColor.Keyword; // "'intersect'", 
				case 80 : return TokenColor.Keyword; // "'then'", 
				case 81 : return TokenColor.Keyword; // "'where'", 
				case 82 : return TokenColor.Text; // "'/'", 
				case 83 : return TokenColor.Keyword; // "'ended by'", 
				case 84 : return TokenColor.Text; // "'>='", 
				case 85 : return TokenColor.Keyword; // "'end'", 
				case 86 : return TokenColor.Identifier; // "IDENTIFIER", 
				case 87 : return TokenColor.Number; // "'null'", 
				case 88 : return TokenColor.Number; // "BOOLEAN", 
				case 89 : return TokenColor.Number; // "QUANTITY", 
				case 90 : return TokenColor.String; // "STRING", 
				case 91 : return TokenColor.Text; // "WS", 
				case 92 : return TokenColor.Text; // "NEWLINE", 
				case 93 : return TokenColor.Comment; // "COMMENT", 
				case 94 : return TokenColor.Comment; // "LINE_COMMENT"
				default : return TokenColor.Text;
			}
		}

		private TokenTriggers GetTokenTriggers(int tokenType)
		{
			switch (tokenType)
			{
				case 0 : return TokenTriggers.None; // "<INVALID>",
				case 1 : return TokenTriggers.None; // "'except'", 
				case 2 : return TokenTriggers.None; // "'like'", 
				case 3 : return TokenTriggers.None; // "'concept'", 
				case 4 : return TokenTriggers.None; // "'retrieve'", 
				case 5 : return TokenTriggers.None; // "'u'", 
				case 6 : return TokenTriggers.None; // "'combine'", 
				case 7 : return TokenTriggers.None; // "'['", 
				case 8 : return TokenTriggers.None; // "'*'", 
				case 9 : return TokenTriggers.None; // "'or'", 
				case 10 : return TokenTriggers.None; // "'<'", 
				case 11 : return TokenTriggers.None; // "'months'", 
				case 12 : return TokenTriggers.None; // "'convert'", 
				case 13 : return TokenTriggers.None; // "'<='", 
				case 14 : return TokenTriggers.None; // "'properly'", 
				case 15 : return TokenTriggers.None; // "'before'", 
				case 16 : return TokenTriggers.None; // "'to'", 
				case 17 : return TokenTriggers.None; // "'unknown'", 
				case 18 : return TokenTriggers.None; // "'}'", 
				case 19 : return TokenTriggers.None; // "'after'", 
				case 20 : return TokenTriggers.None; // "'overlaps'", 
				case 21 : return TokenTriggers.None; // "'case'", 
				case 22 : return TokenTriggers.None; // "'no'", 
				case 23 : return TokenTriggers.None; // "'union'", 
				case 24 : return TokenTriggers.None; // "')'", 
				case 25 : return TokenTriggers.None; // "'parameter'", 
				case 26 : return TokenTriggers.None; // "'seconds'", 
				case 27 : return TokenTriggers.None; // "'interval'", 
				case 28 : return TokenTriggers.None; // "'during'", 
				case 29 : return TokenTriggers.None; // "'hours'", 
				case 30 : return TokenTriggers.None; // "'='", 
				case 31 : return TokenTriggers.None; // "'within'", 
				case 32 : return TokenTriggers.None; // "'div'", 
				case 33 : return TokenTriggers.None; // "'meets'", 
				case 34 : return TokenTriggers.None; // "'days'", 
				case 35 : return TokenTriggers.None; // "'ends'", 
				case 36 : return TokenTriggers.None; // "'mod'", 
				case 37 : return TokenTriggers.None; // "'version'", 
				case 38 : return TokenTriggers.None; // "'when'", 
				case 39 : return TokenTriggers.None; // "'context'", 
				case 40 : return TokenTriggers.None; // "']'", 
				case 41 : return TokenTriggers.None; // "'using'", 
				case 42 : return TokenTriggers.None; // "'default'", 
				case 43 : return TokenTriggers.None; // "'in'", 
				case 44 : return TokenTriggers.None; // "'includes'", 
				case 45 : return TokenTriggers.None; // "'of'", 
				case 46 : return TokenTriggers.None; // "','", 
				case 47 : return TokenTriggers.None; // "'-'", 
				case 48 : return TokenTriggers.None; // "'not'", 
				case 49 : return TokenTriggers.None; // "':'", 
				case 50 : return TokenTriggers.None; // "'('", 
				case 51 : return TokenTriggers.None; // "'if'", 
				case 52 : return TokenTriggers.None; // "'coalesce'", 
				case 53 : return TokenTriggers.None; // "'tuple'", 
				case 54 : return TokenTriggers.None; // "'as'", 
				case 55 : return TokenTriggers.None; // "'{'", 
				case 56 : return TokenTriggers.None; // "'and'", 
				case 57 : return TokenTriggers.None; // "'let'", 
				case 58 : return TokenTriggers.None; // "'else'", 
				case 59 : return TokenTriggers.None; // "'years'", 
				case 60 : return TokenTriggers.None; // "'concurrent with'", 
				case 61 : return TokenTriggers.None; // "'^'", 
				case 62 : return TokenTriggers.None; // "'is'", 
				case 63 : return TokenTriggers.None; // "'starts'", 
				case 64 : return TokenTriggers.None; // "'.'", 
				case 65 : return TokenTriggers.None; // "'+'", 
				case 66 : return TokenTriggers.None; // "'define'", 
				case 67 : return TokenTriggers.None; // "'list'", 
				case 68 : return TokenTriggers.None; // "'minutes'", 
				case 69 : return TokenTriggers.None; // "'<>'", 
				case 70 : return TokenTriggers.None; // "'operator'", 
				case 71 : return TokenTriggers.None; // "'return'", 
				case 72 : return TokenTriggers.None; // "'start'", 
				case 73 : return TokenTriggers.None; // "'weeks'", 
				case 74 : return TokenTriggers.None; // "'with'", 
				case 75 : return TokenTriggers.None; // "'include'", 
				case 76 : return TokenTriggers.None; // "'exists'", 
				case 77 : return TokenTriggers.None; // "'>'", 
				case 78 : return TokenTriggers.None; // "'started by'", 
				case 79 : return TokenTriggers.None; // "'intersect'", 
				case 80 : return TokenTriggers.None; // "'then'", 
				case 81 : return TokenTriggers.None; // "'where'", 
				case 82 : return TokenTriggers.None; // "'/'", 
				case 83 : return TokenTriggers.None; // "'ended by'", 
				case 84 : return TokenTriggers.None; // "'>='", 
				case 85 : return TokenTriggers.None; // "'end'", 
				case 86 : return TokenTriggers.None; // "IDENTIFIER", 
				case 87 : return TokenTriggers.None; // "'null'", 
				case 88 : return TokenTriggers.None; // "BOOLEAN", 
				case 89 : return TokenTriggers.None; // "QUANTITY", 
				case 90 : return TokenTriggers.None; // "STRING", 
				case 91 : return TokenTriggers.None; // "WS", 
				case 92 : return TokenTriggers.None; // "NEWLINE", 
				case 93 : return TokenTriggers.None; // "COMMENT", 
				case 94 : return TokenTriggers.None; // "LINE_COMMENT"
				default : return TokenTriggers.None;
			}
		}

        void IScanner.SetSource(string source, int offset)
        {
            m_source = source.Substring(offset);
			var inputStream = new AntlrInputStream(m_source);
			var lexer = new cqlLexer(inputStream);
			m_tokens = lexer.GetAllTokens();
			m_tokenIndex = 0;
        }
    }
}
