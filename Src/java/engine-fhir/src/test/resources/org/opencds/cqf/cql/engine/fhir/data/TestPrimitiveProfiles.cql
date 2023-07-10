library TestPrimitiveProfiles

using FHIR version '4.0.1'

// Types that exhibit this behavior are:
// url: uri
// canonical: uri
// uuid: uri
// oid: uri
// positiveInt: integer
// unsignedInt: integer
// code: string
// markdown: string
// id: string
// Age: Quantity
// Distance: Quantity
// Duration: Quantity
// Count: Quantity
// MoneyQuantity: Quantity
// SimpleQuantity: Quantity

define UrlUri: FHIR.uri { value: 'http://example.org' }
define CastToUrl: UrlUri as FHIR.url

define CanonicalUri: FHIR.uri { value: 'http://example.org/StructureDefinition/profile' }
define CastToCanonical: CanonicalUri as FHIR.canonical

define UuidUri: FHIR.uri { value: 'urn:uuid:d27ceea4-e506-42a4-8111-f01c003a11c4' }
define CastToUuid: UuidUri as FHIR.uuid

define OidUri: FHIR.uri { value: 'urn:oid:2.16.840.1.113883.3.464.1004.1116' }
define CastToOid: OidUri as FHIR.oid

define PositiveInt: FHIR.integer { value: 12 }
define CastToPositiveInt: PositiveInt as FHIR.positiveInt

define UnsignedInt: FHIR.integer { value: 12 }
define CastToUnsignedInt: UnsignedInt as FHIR.unsignedInt

define CodeString: FHIR.string { value: '12345' }
define CastToCode: CodeString as FHIR.code

define MarkdownString: FHIR.string { value: '# Markdown is [good](http://example.org)' }
define CastToMarkdown: MarkdownString as FHIR.markdown

define IdString: FHIR.string { value: 'fhir-string' }
define CastToId: IdString as FHIR.id

define SimpleQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1.0 }, code: FHIR.code { value: 'mg' } }
define CastToSimpleQuantity: SimpleQuantity as FHIR.SimpleQuantity

define AgeQuantity: FHIR.Quantity { value: FHIR.decimal { value: 10.0 }, code: FHIR.code { value: 'a' } }
define CastToAge: AgeQuantity as FHIR.Age

define DistanceQuantity: FHIR.Quantity { value: FHIR.decimal { value: 1200.0 }, code: FHIR.code { value: 'km' } }
define CastToDistance: DistanceQuantity as FHIR.Distance

define DurationQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12.0 }, code: FHIR.code { value: 'a' } }
define CastToDuration: DurationQuantity as FHIR.Duration

define CountQuantity: FHIR.Quantity { value: FHIR.decimal { value: 100 }, code: FHIR.code { value: '1' } }
define CastToCount: CountQuantity as FHIR.Count

define MoneyQuantity: FHIR.Quantity { value: FHIR.decimal { value: 12000.00 }, code: FHIR.code { value: '$' } }
define CastToMoney: MoneyQuantity as FHIR.MoneyQuantity