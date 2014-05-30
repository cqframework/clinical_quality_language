using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.VisualStudio.Package;
using Microsoft.VisualStudio.TextManager.Interop;

namespace ONC.CQL_VSSupport_2013
{
	public class CQLLanguageService : LanguageService
	{
		public override Source CreateSource(IVsTextLines buffer)
		{
			return base.CreateSource(buffer);
		}

		public override string GetFormatFilterList()
		{
			return "CQL files (*.cql)|*.cql";
		}

        private LanguagePreferences m_preferences;

        public override LanguagePreferences GetLanguagePreferences()
        {
            if (m_preferences == null)
            {
                m_preferences = new LanguagePreferences(this.Site, typeof(CQLLanguageService).GUID, this.Name);
                m_preferences.Init();
            }
            return m_preferences;
        }

        private CQLScanner m_scanner;

        public override IScanner GetScanner(IVsTextLines buffer)
        {
            if (m_scanner == null)
            {
                m_scanner = new CQLScanner(buffer);
            }
            return m_scanner;
        }

		public override string Name
		{
			get { return "Clinical Quality Language (CQL)"; }
		}

        public override AuthoringScope ParseSource(ParseRequest req)
        {
			if (req == null)
			{
				return null;
			}

			var scope = new CQLAuthoringScope();
			req.Scope = scope;

			if (req.Reason == ParseReason.Check)
			{
			}

			if (m_scanner != null && req.Reason == ParseReason.MemberSelect || req.Reason == ParseReason.MemberSelectAndHighlightBraces && req.TokenInfo != null)
			{
				var token = m_scanner.GetToken(req.TokenInfo.Token);

				if (token != null)
				{
					if (token.Type == 4) // [
					{
						scope.AddDeclaration(new CQLDeclaration("Encounter", 0, "An encounter with the patient"));
						scope.AddDeclaration(new CQLDeclaration("Procedure", 0, "A procedure"));
						scope.AddDeclaration(new CQLDeclaration("Medication", 0, "A medication"));
					}

					if (token.Type == 3) // ,
					{
						scope.AddDeclaration(new CQLDeclaration("Performed", 0, "An action performed"));
						scope.AddDeclaration(new CQLDeclaration("Proposed", 0, "An action performed"));
						scope.AddDeclaration(new CQLDeclaration("Ordered", 0, "An action performed"));
					}

					if (token.Type == 5) // :
					{
						scope.AddDeclaration(new CQLDeclaration("\"Inpatient\"", 0, "Inpatient encounter"));
						scope.AddDeclaration(new CQLDeclaration("\"Outpatient\"", 0, "Outpatient encounter"));
						scope.AddDeclaration(new CQLDeclaration("\"Face-to-face Interaction\"", 0, "Face-to-face interaction"));
					}
				}
			}

			return scope;
        }

		public override void OnIdle(bool periodic)
		{
			base.OnIdle(periodic);
		}
	}
}
