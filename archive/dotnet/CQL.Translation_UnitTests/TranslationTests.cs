using System;
using CQL.Translation;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace CQL.Translation_UnitTests
{
	[TestClass]
	public class TranslationTests
	{
		[TestMethod]
		public void TestTranslator()
		{
			var translator = new Translator();

			var library = translator.TranslateLibrary("let X = distinct A");

			Assert.IsNotNull(library.statements);
		}
	}
}
