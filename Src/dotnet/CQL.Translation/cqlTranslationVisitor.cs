using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using Antlr4.Runtime.Tree;
using CQL.ELM.Model;

namespace CQL.Translation
{
    public class cqlTranslationVisitor : cqlBaseVisitor<Element>
    {
		#region ParseTreeVisitorMethods

        public override Element Visit(IParseTree tree)
        {
            return base.Visit(tree);
        }

        public override Element VisitChildren(IRuleNode node)
        {
            return base.VisitChildren(node);
        }

        protected override bool ShouldVisitNextChild(IRuleNode node, Element currentResult)
        {
            return base.ShouldVisitNextChild(node, currentResult);
        }

        protected override Element DefaultResult()
        {
            return new Null();
        }

		protected override Element AggregateResult(Element aggregate, Element nextResult)
		{
			return base.AggregateResult(aggregate, nextResult);
		}

		public override Element VisitErrorNode(IErrorNode node)
		{
			return base.VisitErrorNode(node);
		}

		public override Element VisitTerminal(ITerminalNode node)
		{
			return base.VisitTerminal(node);
		}

		#endregion

		#region CQLHelperMethods

		private string GetClassName(string existenceModifier, string topic, string modality)
		{
			return String.Format("{0}{1}{2}", topic, modality ?? String.Empty, existenceModifier);
		}
		
		#endregion

		#region CQLGrammarMethods

		public override Element VisitRetrieve(cqlParser.RetrieveContext context)
		{
			var existenceModifier = context.existenceModifier();
			var topic = context.topic();
			var modality = context.modality();
			var className = 
				GetClassName
				(
					existenceModifier == null 
						? "Occurrence" 
						: (existenceModifier.GetText() == "no" ? "NonOccurrence" : "UnknownOccurrence"), 
					topic.GetText(), 
					modality == null ? null : modality.GetText()
				);

			var valueset = context.valueset();
			var valuesetPathIdentifier = context.valuesetPathIdentifier();
			var during = context.expression();
			var duringPathIdentifier = context.duringPathIdentifier();

			var request = new ClinicalRequest();

			request.cardinality = RequestCardinality.Multiple;
			//request.dataType = className; // TODO: xml qualifier for data model?
			if (valueset != null)
			{
				request.codes = (Expression)Visit(valueset);
				if (valuesetPathIdentifier != null)
				{
					request.codeProperty = valuesetPathIdentifier.GetText();
				}
			}

			if (during != null)
			{
				request.dateRange = (Expression)Visit(during);
				if (duringPathIdentifier != null)
				{
					request.dateProperty = duringPathIdentifier.GetText();
				}
			}

			return request;
		}

		public override Element VisitQualifiedIdentifier(cqlParser.QualifiedIdentifierContext context)
		{
			var qualifier = context.qualifier();
			var identifier = context.IDENTIFIER().GetText();

			return new ExpressionRef { libraryName = qualifier == null ? null : qualifier.GetText(), name = identifier };
		}

		public override Element VisitQuerySource(cqlParser.QuerySourceContext context)
		{
			var retrieve = context.retrieve();
			if (retrieve != null)
			{
				return VisitRetrieve(retrieve);
			}

			var qualifiedIdentifier = context.qualifiedIdentifier();
			if (qualifiedIdentifier != null)
			{
				return VisitQualifiedIdentifier(qualifiedIdentifier);
			}

			var expression = context.expression();
			if (expression != null)
			{
				return Visit(expression);
			}

			throw new NotSupportedException("Unknown query source category.");
		}

		public override Element VisitQuery(cqlParser.QueryContext context)
		{
			var aliasedSource = context.aliasedQuerySource();
			var querySource = (Expression)Visit(aliasedSource.querySource());
			var alias = aliasedSource.alias().GetText();
			var result = (Expression)new Filter { source = querySource, scope = alias };
			var condition = new And { operand = new List<Expression>() };

			foreach (var queryInclusionClause in context.queryInclusionClause())
			{
				// NOTE: This only works with "with" clauses right now. If we add "combine" it will need to be dealt with here.
				var withSource = (Expression)VisitQuerySource(queryInclusionClause.aliasedQuerySource().querySource());
				var withAlias = queryInclusionClause.aliasedQuerySource().alias().GetText();
				var withCondition = (Expression)Visit(queryInclusionClause.expression());
				var withExpression = new IsNotEmpty { operand = new Filter { source = withSource, scope = withAlias, condition = withCondition } };
				condition.operand.Add(withExpression);
			}

			var whereClause = context.whereClause();
			if (whereClause != null)
			{
				condition.operand.Add((Expression)Visit(whereClause.expression()));
			}

			var returnClause = context.returnClause();
			if (returnClause != null)
			{
				result = new ForEach { source = result, scope = alias, element = (Expression)Visit(returnClause.expression()) };
			}

			var sortClause = context.sortClause();
			if (sortClause != null)
			{
				var sortDirection = sortClause.sortDirection();
				if (sortDirection != null)
				{
					if (sortDirection.GetText() != "asc")
					{
						// TODO: CQL-LM must be modified to support directional sort
						throw new NotSupportedException();
					}

					result = new Sort { source = result };
				}
				else
				{
					if (sortClause.sortByItem().Count != 1)
					{
						// TODO: CQL-LM must be modified to support multi-column sort
						throw new NotSupportedException();
					}

					if (sortClause.sortByItem(0).sortDirection() != null && sortClause.sortByItem(0).sortDirection().GetText() != "asc")
					{
						// TODO: CQL-LM must be modified to support directional sort
						throw new NotSupportedException();
					}

					return new Sort { source = result, orderBy = sortClause.sortByItem(0).qualifiedIdentifier().GetText() };
				}
			}

			return result;
		}

		public override Element VisitAdditionExpressionTerm(cqlParser.AdditionExpressionTermContext context)
		{
			return new Add { operand = new List<Expression> { (Expression)Visit(context.expressionTerm(0)), (Expression)Visit(context.expressionTerm(1)) } };
		}

		public override Element VisitAggregateExpressionTerm(cqlParser.AggregateExpressionTermContext context)
		{
			switch (context.GetChild(0).GetText())
			{
				case "distinct" : return new Distinct { source = (Expression)Visit(context.expression()) };
				break;

				case "collapse" : return new Collapse { operand = (Expression)Visit(context.expression()) };
				break;

				case "expand" : return new Expand { operand = (Expression)Visit(context.expression()) };
				break;
			}

			throw new NotSupportedException(String.Format("Unrecognized operator for aggregateExpressionTerm: {0}", context.GetChild(0).GetText()));
		}

		public override Element VisitLogic(cqlParser.LogicContext context)
		{
			var library = new Library();

			foreach (var usingDefinition in context.usingDefinition())
			{
				throw new NotImplementedException();
			}

			foreach (var includeDefinition in context.includeDefinition())
			{
				throw new NotImplementedException();
			}

			foreach (var parameterDefinition in context.parameterDefinition())
			{
				library.parameters.Add(new ParameterDef { name = parameterDefinition.IDENTIFIER().GetText(), parameterType = new XmlQualifiedName(parameterDefinition.typeSpecifier().GetText()), @default = (Expression)Visit(parameterDefinition.expression()) });
			}

			foreach (var valuesetDefinition in context.valuesetDefinition())
			{
				library.valueSets.Add(new ValueSetDef { name = valuesetDefinition.VALUESET().GetText(), valueSet = (Expression)Visit(valuesetDefinition.expression()) });
			}

			string currentContext = Contexts.PATIENT;
			foreach (var statement in context.statement())
			{
				var contextDefinition = statement.contextDefinition();
				if (contextDefinition != null)
				{
					var newContext = contextDefinition.IDENTIFIER().GetText();
					if (newContext == Contexts.PATIENT || newContext == Contexts.POPULATION)
					{
						currentContext = newContext;
					}
					else
					{
						throw new InvalidOperationException(String.Format("Unknown context {0}.", newContext));
					}
				}

				var letStatement = statement.letStatement();
				if (letStatement != null)
				{
					library.statements.Add(new ExpressionDef { name = letStatement.IDENTIFIER().GetText(), context = currentContext, expression = (Expression)Visit(letStatement.expression()) });
				}

				var functionDefinition = statement.functionDefinition();
				if (functionDefinition != null)
				{
					var functionDef = new FunctionDef { name = functionDefinition.IDENTIFIER().GetText(), context = currentContext, expression = (Expression)Visit(functionDefinition.functionBody()) };
					foreach (var parameter in functionDefinition.operandDefinition())
					{
						// TODO: More complete type resolution here, although ELM will have to be expanded to support this as well....
						functionDef.parameter.Add(new ParameterDef { name = parameter.IDENTIFIER().GetText(), parameterType = new XmlQualifiedName(parameter.typeSpecifier().GetText()) });
					}

					library.statements.Add(functionDef);
				}

				var retrieveDefinition = statement.retrieveDefinition();
				if (retrieveDefinition != null)
				{
					throw new NotImplementedException();
				}
			}

			return library;
		}

		#endregion
    }
}
