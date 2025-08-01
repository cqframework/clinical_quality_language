library BundleHelpers

using FHIR version '3.0.1'

define TestBundle:
  Bundle {
    id: FHIR.id { value: 'x' }
  }

define TestBundleEntry:
  TestBundle.entry

define function Bundle(Resources List<Resource>):
  Bundle {
    entry:
      Resources R
        return FHIR.Bundle.EntryComponent {
          resource: R
        }
  }
