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

                // TODO: Obviously this can be much more efficient, just doing quick and dirty until the language settles down
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
			switch (cqlLexer.tokenNames[tokenType])
			{
				case "<INVALID>" : return TokenType.Unknown;
				case "'except'" : return TokenType.Keyword;
				case "'u'" : return TokenType.Literal;
				case "'*'" : return TokenType.Operator;
				case "'combine'" : return TokenType.Keyword;
				case "'before'" : return TokenType.Keyword;
				case "'between'" : return TokenType.Keyword;
				case "'to'" : return TokenType.Keyword;
				case "'by'" : return TokenType.Keyword;
				case "'}'" : return TokenType.Text;
				case "'millisecond'" : return TokenType.Keyword;
				case "'after'" : return TokenType.Keyword;
				case "')'" : return TokenType.Text;
				case "'expand'" : return TokenType.Keyword;
				case "'parameter'" : return TokenType.Keyword;
				case "'interval'" : return TokenType.Keyword;
				case "'hours'" : return TokenType.Keyword;
				case "'meets'" : return TokenType.Keyword;
				case "'mod'" : return TokenType.Keyword;
				case "'when'" : return TokenType.Keyword;
				case "'foreach'" : return TokenType.Keyword;
				case "'context'" : return TokenType.Keyword;
				case "'in'" : return TokenType.Keyword;
				case "'second'" : return TokenType.Keyword;
				case "','" : return TokenType.Delimiter;
				case "'-'" : return TokenType.Operator;
				case "'if'" : return TokenType.Keyword;
				case "'coalesce'" : return TokenType.Keyword;
				case "'as'" : return TokenType.Keyword;
				case "'sort'" : return TokenType.Keyword;
				case "'let'" : return TokenType.Keyword;
				case "'else'" : return TokenType.Keyword;
				case "'years'" : return TokenType.Keyword;
				case "'concurrent with'" : return TokenType.Keyword;
				case "'contains'" : return TokenType.Keyword;
				case "'.'" : return TokenType.Operator;
				case "'+'" : return TokenType.Operator;
				case "'define'" : return TokenType.Keyword;
				case "'minutes'" : return TokenType.Keyword;
				case "'weeks'" : return TokenType.Keyword;
				case "'minute'" : return TokenType.Keyword;
				case "'>'" : return TokenType.Operator;
				case "'exists'" : return TokenType.Keyword;
				case "'time'" : return TokenType.Keyword;
				case "'then'" : return TokenType.Keyword;
				case "'intersect'" : return TokenType.Keyword;
				case "'where'" : return TokenType.Keyword;
				case "'/'" : return TokenType.Operator;
				case "'day'" : return TokenType.Keyword;
				case "'like'" : return TokenType.Keyword;
				case "'valueset'" : return TokenType.Keyword;
				case "'retrieve'" : return TokenType.Keyword;
				case "'or'" : return TokenType.Keyword;
				case "'['" : return TokenType.Text;
				case "'<'" : return TokenType.Operator;
				case "'months'" : return TokenType.Keyword;
				case "'properly'" : return TokenType.Keyword;
				case "'<='" : return TokenType.Operator;
				case "'convert'" : return TokenType.Keyword;
				case "'unknown'" : return TokenType.Keyword;
				case "'overlaps'" : return TokenType.Keyword;
				case "'case'" : return TokenType.Keyword;
				case "'no'" : return TokenType.Keyword;
				case "'xor'" : return TokenType.Keyword;
				case "'union'" : return TokenType.Keyword;
				case "'seconds'" : return TokenType.Keyword;
				case "'during'" : return TokenType.Keyword;
				case "'duration'" : return TokenType.Keyword;
				case "'='" : return TokenType.Operator;
				case "'desc'" : return TokenType.Keyword;
				case "'within'" : return TokenType.Keyword;
				case "'milliseconds'" : return TokenType.Keyword;
				case "'div'" : return TokenType.Keyword;
				case "'days'" : return TokenType.Keyword;
				case "'ends'" : return TokenType.Keyword;
				case "'version'" : return TokenType.Keyword;
				case "'using'" : return TokenType.Keyword;
				case "']'" : return TokenType.Text;
				case "'includes'" : return TokenType.Keyword;
				case "'default'" : return TokenType.Keyword;
				case "'of'" : return TokenType.Keyword;
				case "'from'" : return TokenType.Keyword;
				case "'('" : return TokenType.Text;
				case "':'" : return TokenType.Text;
				case "'not'" : return TokenType.Keyword;
				case "'month'" : return TokenType.Keyword;
				case "'tuple'" : return TokenType.Keyword;
				case "'{'" : return TokenType.Text;
				case "'and'" : return TokenType.Keyword;
				case "'asc'" : return TokenType.Keyword;
				case "'date'" : return TokenType.Keyword;
				case "'^'" : return TokenType.Operator;
				case "'is'" : return TokenType.Keyword;
				case "'starts'" : return TokenType.Keyword;
				case "'list'" : return TokenType.Keyword;
				case "'<>'" : return TokenType.Operator;
				case "'function'" : return TokenType.Keyword;
				case "'return'" : return TokenType.Keyword;
				case "'start'" : return TokenType.Keyword;
				case "'with'" : return TokenType.Keyword;
				case "'include'" : return TokenType.Keyword;
				case "'started by'" : return TokenType.Keyword;
				case "'year'" : return TokenType.Keyword;
				case "'ended by'" : return TokenType.Keyword;
				case "'>='" : return TokenType.Operator;
				case "'end'" : return TokenType.Keyword;
				case "'hour'" : return TokenType.Keyword;
				case "IDENTIFIER" : return TokenType.Identifier;
				case "'null'" : return TokenType.Literal;
				case "BOOLEAN" : return TokenType.Literal;
				case "QUANTITY" : return TokenType.Literal;
				case "STRING" : return TokenType.Literal;
				case "WS" : return TokenType.WhiteSpace;
				case "NEWLINE" : return TokenType.WhiteSpace;
				case "COMMENT" : return TokenType.Comment;
				case "LINE_COMMENT" : return TokenType.Comment;
				default : return TokenType.Unknown;
			}
		}

		private TokenColor GetTokenColor(int tokenType)
		{
			switch (cqlLexer.tokenNames[tokenType])
			{
				case "<INVALID>" : return TokenColor.Text;
				case "'except'" : return TokenColor.Keyword;
				case "'u'" : return TokenColor.Number;
				case "'*'" : return TokenColor.Text;
				case "'combine'" : return TokenColor.Keyword;
				case "'before'" : return TokenColor.Keyword;
				case "'between'" : return TokenColor.Keyword;
				case "'to'" : return TokenColor.Keyword;
				case "'by'" : return TokenColor.Keyword;
				case "'}'" : return TokenColor.Text;
				case "'millisecond'" : return TokenColor.Keyword;
				case "'after'" : return TokenColor.Keyword;
				case "')'" : return TokenColor.Text;
				case "'expand'" : return TokenColor.Keyword;
				case "'parameter'" : return TokenColor.Keyword;
				case "'interval'" : return TokenColor.Keyword;
				case "'hours'" : return TokenColor.Keyword;
				case "'meets'" : return TokenColor.Keyword;
				case "'mod'" : return TokenColor.Keyword;
				case "'when'" : return TokenColor.Keyword;
				case "'foreach'" : return TokenColor.Keyword;
				case "'context'" : return TokenColor.Keyword;
				case "'in'" : return TokenColor.Keyword;
				case "'second'" : return TokenColor.Keyword;
				case "','" : return TokenColor.Text;
				case "'-'" : return TokenColor.Text;
				case "'if'" : return TokenColor.Keyword;
				case "'coalesce'" : return TokenColor.Keyword;
				case "'as'" : return TokenColor.Keyword;
				case "'sort'" : return TokenColor.Keyword;
				case "'let'" : return TokenColor.Keyword;
				case "'else'" : return TokenColor.Keyword;
				case "'years'" : return TokenColor.Keyword;
				case "'concurrent with'" : return TokenColor.Keyword;
				case "'contains'" : return TokenColor.Keyword;
				case "'.'" : return TokenColor.Text;
				case "'+'" : return TokenColor.Text;
				case "'define'" : return TokenColor.Keyword;
				case "'minutes'" : return TokenColor.Keyword;
				case "'weeks'" : return TokenColor.Keyword;
				case "'minute'" : return TokenColor.Keyword;
				case "'>'" : return TokenColor.Text;
				case "'exists'" : return TokenColor.Keyword;
				case "'time'" : return TokenColor.Keyword;
				case "'then'" : return TokenColor.Keyword;
				case "'intersect'" : return TokenColor.Keyword;
				case "'where'" : return TokenColor.Keyword;
				case "'/'" : return TokenColor.Text;
				case "'day'" : return TokenColor.Keyword;
				case "'like'" : return TokenColor.Keyword;
				case "'valueset'" : return TokenColor.Keyword;
				case "'retrieve'" : return TokenColor.Keyword;
				case "'or'" : return TokenColor.Keyword;
				case "'['" : return TokenColor.Text;
				case "'<'" : return TokenColor.Text;
				case "'months'" : return TokenColor.Keyword;
				case "'properly'" : return TokenColor.Keyword;
				case "'<='" : return TokenColor.Text;
				case "'convert'" : return TokenColor.Keyword;
				case "'unknown'" : return TokenColor.Keyword;
				case "'overlaps'" : return TokenColor.Keyword;
				case "'case'" : return TokenColor.Keyword;
				case "'no'" : return TokenColor.Keyword;
				case "'xor'" : return TokenColor.Keyword;
				case "'union'" : return TokenColor.Keyword;
				case "'seconds'" : return TokenColor.Keyword;
				case "'during'" : return TokenColor.Keyword;
				case "'duration'" : return TokenColor.Keyword;
				case "'='" : return TokenColor.Text;
				case "'desc'" : return TokenColor.Keyword;
				case "'within'" : return TokenColor.Keyword;
				case "'milliseconds'" : return TokenColor.Keyword;
				case "'div'" : return TokenColor.Keyword;
				case "'days'" : return TokenColor.Keyword;
				case "'ends'" : return TokenColor.Keyword;
				case "'version'" : return TokenColor.Keyword;
				case "'using'" : return TokenColor.Keyword;
				case "']'" : return TokenColor.Text;
				case "'includes'" : return TokenColor.Keyword;
				case "'default'" : return TokenColor.Keyword;
				case "'of'" : return TokenColor.Keyword;
				case "'from'" : return TokenColor.Keyword;
				case "'('" : return TokenColor.Text;
				case "':'" : return TokenColor.Text;
				case "'not'" : return TokenColor.Keyword;
				case "'month'" : return TokenColor.Keyword;
				case "'tuple'" : return TokenColor.Keyword;
				case "'{'" : return TokenColor.Text;
				case "'and'" : return TokenColor.Keyword;
				case "'asc'" : return TokenColor.Keyword;
				case "'date'" : return TokenColor.Keyword;
				case "'^'" : return TokenColor.Text;
				case "'is'" : return TokenColor.Keyword;
				case "'starts'" : return TokenColor.Keyword;
				case "'list'" : return TokenColor.Keyword;
				case "'<>'" : return TokenColor.Text;
				case "'function'" : return TokenColor.Keyword;
				case "'return'" : return TokenColor.Keyword;
				case "'start'" : return TokenColor.Keyword;
				case "'with'" : return TokenColor.Keyword;
				case "'include'" : return TokenColor.Keyword;
				case "'started by'" : return TokenColor.Keyword;
				case "'year'" : return TokenColor.Keyword;
				case "'ended by'" : return TokenColor.Keyword;
				case "'>='" : return TokenColor.Text;
				case "'end'" : return TokenColor.Keyword;
				case "'hour'" : return TokenColor.Keyword;
				case "IDENTIFIER" : return TokenColor.Identifier;
				case "'null'" : return TokenColor.Number;
				case "BOOLEAN" : return TokenColor.Number;
				case "QUANTITY" : return TokenColor.Number;
				case "STRING" : return TokenColor.String;
				case "WS" : return TokenColor.Text;
				case "NEWLINE" : return TokenColor.Text;
				case "COMMENT" : return TokenColor.Comment;
				case "LINE_COMMENT" : return TokenColor.Comment;
				default : return TokenColor.Text;
			}
		}

		private TokenTriggers GetTokenTriggers(int tokenType)
		{
			switch (cqlLexer.tokenNames[tokenType])
			{
				case "<INVALID>" : return TokenTriggers.None;
				case "'except'" : return TokenTriggers.None;
				case "'u'" : return TokenTriggers.None;
				case "'*'" : return TokenTriggers.None;
				case "'combine'" : return TokenTriggers.None;
				case "'before'" : return TokenTriggers.None;
				case "'between'" : return TokenTriggers.None;
				case "'to'" : return TokenTriggers.None;
				case "'by'" : return TokenTriggers.None;
				case "'}'" : return TokenTriggers.None;
				case "'millisecond'" : return TokenTriggers.None;
				case "'after'" : return TokenTriggers.None;
				case "')'" : return TokenTriggers.None;
				case "'expand'" : return TokenTriggers.None;
				case "'parameter'" : return TokenTriggers.None;
				case "'interval'" : return TokenTriggers.None;
				case "'hours'" : return TokenTriggers.None;
				case "'meets'" : return TokenTriggers.None;
				case "'mod'" : return TokenTriggers.None;
				case "'when'" : return TokenTriggers.None;
				case "'foreach'" : return TokenTriggers.None;
				case "'context'" : return TokenTriggers.None;
				case "'in'" : return TokenTriggers.None;
				case "'second'" : return TokenTriggers.None;
				case "','" : return TokenTriggers.None;
				case "'-'" : return TokenTriggers.None;
				case "'if'" : return TokenTriggers.None;
				case "'coalesce'" : return TokenTriggers.None;
				case "'as'" : return TokenTriggers.None;
				case "'sort'" : return TokenTriggers.None;
				case "'let'" : return TokenTriggers.None;
				case "'else'" : return TokenTriggers.None;
				case "'years'" : return TokenTriggers.None;
				case "'concurrent with'" : return TokenTriggers.None;
				case "'contains'" : return TokenTriggers.None;
				case "'.'" : return TokenTriggers.None;
				case "'+'" : return TokenTriggers.None;
				case "'define'" : return TokenTriggers.None;
				case "'minutes'" : return TokenTriggers.None;
				case "'weeks'" : return TokenTriggers.None;
				case "'minute'" : return TokenTriggers.None;
				case "'>'" : return TokenTriggers.None;
				case "'exists'" : return TokenTriggers.None;
				case "'time'" : return TokenTriggers.None;
				case "'then'" : return TokenTriggers.None;
				case "'intersect'" : return TokenTriggers.None;
				case "'where'" : return TokenTriggers.None;
				case "'/'" : return TokenTriggers.None;
				case "'day'" : return TokenTriggers.None;
				case "'like'" : return TokenTriggers.None;
				case "'valueset'" : return TokenTriggers.None;
				case "'retrieve'" : return TokenTriggers.None;
				case "'or'" : return TokenTriggers.None;
				case "'['" : return TokenTriggers.None;
				case "'<'" : return TokenTriggers.None;
				case "'months'" : return TokenTriggers.None;
				case "'properly'" : return TokenTriggers.None;
				case "'<='" : return TokenTriggers.None;
				case "'convert'" : return TokenTriggers.None;
				case "'unknown'" : return TokenTriggers.None;
				case "'overlaps'" : return TokenTriggers.None;
				case "'case'" : return TokenTriggers.None;
				case "'no'" : return TokenTriggers.None;
				case "'xor'" : return TokenTriggers.None;
				case "'union'" : return TokenTriggers.None;
				case "'seconds'" : return TokenTriggers.None;
				case "'during'" : return TokenTriggers.None;
				case "'duration'" : return TokenTriggers.None;
				case "'='" : return TokenTriggers.None;
				case "'desc'" : return TokenTriggers.None;
				case "'within'" : return TokenTriggers.None;
				case "'milliseconds'" : return TokenTriggers.None;
				case "'div'" : return TokenTriggers.None;
				case "'days'" : return TokenTriggers.None;
				case "'ends'" : return TokenTriggers.None;
				case "'version'" : return TokenTriggers.None;
				case "'using'" : return TokenTriggers.None;
				case "']'" : return TokenTriggers.None;
				case "'includes'" : return TokenTriggers.None;
				case "'default'" : return TokenTriggers.None;
				case "'of'" : return TokenTriggers.None;
				case "'from'" : return TokenTriggers.None;
				case "'('" : return TokenTriggers.None;
				case "':'" : return TokenTriggers.None;
				case "'not'" : return TokenTriggers.None;
				case "'month'" : return TokenTriggers.None;
				case "'tuple'" : return TokenTriggers.None;
				case "'{'" : return TokenTriggers.None;
				case "'and'" : return TokenTriggers.None;
				case "'asc'" : return TokenTriggers.None;
				case "'date'" : return TokenTriggers.None;
				case "'^'" : return TokenTriggers.None;
				case "'is'" : return TokenTriggers.None;
				case "'starts'" : return TokenTriggers.None;
				case "'list'" : return TokenTriggers.None;
				case "'<>'" : return TokenTriggers.None;
				case "'None'" : return TokenTriggers.None;
				case "'return'" : return TokenTriggers.None;
				case "'start'" : return TokenTriggers.None;
				case "'with'" : return TokenTriggers.None;
				case "'include'" : return TokenTriggers.None;
				case "'started by'" : return TokenTriggers.None;
				case "'year'" : return TokenTriggers.None;
				case "'ended by'" : return TokenTriggers.None;
				case "'>='" : return TokenTriggers.None;
				case "'end'" : return TokenTriggers.None;
				case "'hour'" : return TokenTriggers.None;
				case "IDENTIFIER" : return TokenTriggers.None;
				case "'null'" : return TokenTriggers.None;
				case "BOOLEAN" : return TokenTriggers.None;
				case "QUANTITY" : return TokenTriggers.None;
				case "STRING" : return TokenTriggers.None;
				case "WS" : return TokenTriggers.None;
				case "NEWLINE" : return TokenTriggers.None;
				case "COMMENT" : return TokenTriggers.None;
				case "LINE_COMMENT" : return TokenTriggers.None;
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
