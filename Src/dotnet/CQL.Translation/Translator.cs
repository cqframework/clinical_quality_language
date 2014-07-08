using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Antlr4.Runtime;
using Antlr4.Runtime.Tree;
using CQL.ELM.Model;

namespace CQL.Translation
{
    public class Translator
    {
        public Library TranslateLibrary(string source)
        {
			var inputStream = new AntlrInputStream(source);
			var lexer = new cqlLexer(inputStream);
            var tokenStream = new CommonTokenStream(lexer);
            var parser = new cqlParser(tokenStream);
            var parseTree = parser.logic();
            var visitor = new cqlTranslationVisitor();
            return visitor.Visit(parseTree) as Library;
        }
    }
}
