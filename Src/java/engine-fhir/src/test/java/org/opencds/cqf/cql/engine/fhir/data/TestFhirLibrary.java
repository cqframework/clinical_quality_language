package org.opencds.cqf.cql.engine.fhir.data;

public class TestFhirLibrary {

    // @Test
    /* TODO: These tests haven't been run for a while. Why not just deleting them? I am not sure what their purpose is.
      public void testCBP() throws IOException, JAXBException {
          File xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("library-cbp.elm.xml").getFile(), "UTF-8"));
          Library library = CqlLibraryReader.read(xmlFile);

          Context context = new Context(library);

          FhirContext fhirContext = FhirContext.forCached(FhirVersionEnum.DSTU3);

    Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
    RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(fhirContext), fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
    CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://fhir3.healthintersections.com.au/open/");
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://wildfhir.aegis.net/fhir");
          context.registerDataProvider("http://hl7.org/fhir", provider);

          var value = context.resolveExpressionRef("BP: Systolic").evaluate(context);
          assertThat(value, instanceOf(Iterable.class));
          for (Object element : (Iterable<?>)result) {
              assertThat(element, instanceOf(Observation.class));
              Observation observation = (Observation)element;
              assertThat(observation.getCode().getCoding().get(0).getCode(), is("8480-6"));
          }

          result = context.resolveExpressionRef("BP: Diastolic").evaluate(context);
          assertThat(value, instanceOf(Iterable.class));
          for (Object element : (Iterable<?>)result) {
              assertThat(element, instanceOf(Observation.class));
              Observation observation = (Observation)element;
              assertThat(observation.getCode().getCoding().get(0).getCode(), is("8462-4"));
          }
      }

      // TODO: Fix this, it depends on the Convert...
      //@Test
      public void testCMS9v4_CQM() throws IOException, JAXBException {
          File xmlFile = new File(URLDecoder.decode(TestFhirLibrary.class.getResource("CMS9v4_CQM.xml").getFile(), "UTF-8"));
          Library library = CqlLibraryReader.read(xmlFile);

          Context context = new Context(library);

          FhirContext fhirContext = FhirContext.forCached(FhirVersionEnum.DSTU3);

    Dstu3FhirModelResolver modelResolver = new Dstu3FhirModelResolver();
    RestFhirRetrieveProvider retrieveProvider = new RestFhirRetrieveProvider(new SearchParameterResolver(fhirContext),  fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu3"));
    CompositeDataProvider provider = new CompositeDataProvider(modelResolver, retrieveProvider);
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://fhirtest.uhn.ca/baseDstu3");
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://fhir3.healthintersections.com.au/open/");
          //BaseFhirDataProvider provider = new FhirDataProviderStu3().setEndpoint("http://wildfhir.aegis.net/fhir");
          context.registerDataProvider("http://hl7.org/fhir", provider);

          var value = context.resolveExpressionRef("Breastfeeding Intention Assessment").evaluate(context);
          assertThat(value, instanceOf(Iterable.class));
          for (Object element : (Iterable<?>)result) {
              assertThat(element, instanceOf(RiskAssessment.class));
          }
      }
      */
}
