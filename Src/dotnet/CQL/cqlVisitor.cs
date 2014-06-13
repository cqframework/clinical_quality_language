using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CQL
{
	public class cqlVisitor : cqlBaseVisitor<string>
	{
		public override string VisitStatement(cqlParser.StatementContext context)
		{
			return base.VisitStatement(context);
		}

		public override string VisitExistenceModifier(cqlParser.ExistenceModifierContext context)
		{
			return base.VisitExistenceModifier(context);
		}

        public override string VisitValueset(cqlParser.ValuesetContext context)
        {
            return base.VisitValueset(context);
        }
	}
}
