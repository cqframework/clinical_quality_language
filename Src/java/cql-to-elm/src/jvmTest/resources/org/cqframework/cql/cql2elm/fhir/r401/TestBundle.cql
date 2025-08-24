library BundleHelpers

using FHIR version '4.0.1'

define function Bundle(Resources List<Resource>):
  Bundle {
    entry:
      Resources R
        return FHIR.Bundle.Entry {
          resource: R
        }
  }
