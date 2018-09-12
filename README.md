# DHIS2 FHIR Adapter Prototype for Demo
The adapter prototype handles for a demo the data as described by https://github.com/jamesagnew/aehin2018-demo-data. 

__The source code is still under development and must still be extended to support all features that are required for the demo!__
## Mappings to DHIS2
FHIR structures may contain multiple values for one field (e.g. multiple names or addresses). This will not be displayed by the following sections. The resulting mapping configuration reduces multiple values to a single value.
### FHIR Organization
The organization resource is only included in the transaction bundle to include a reference to the national identifier of the organization (must be the organization code). Since the organization will not include the parent organization (FHIR Organization.partOf) it cannot be used to create or update an organization unit on DHIS2. This would also require that the complete hierarchy up to the root organization is included and updated in the transaction bundle (due to required references of national identifiers). The prototype adapter will therefore ignore the update request of the organization and return a status of 200.
### FHIR Patient
| FHIR Structure           | DHIS2 Tracked Entity Name | Tracked Entity Attribute Name | Required | Unique |
|--------------------------|---------------------------|-------------------------------|----------|--------|
| Patient.name.family      | Person                    | First name                    | No       | No     |
| Patient.name.given       | Person                    | Last name                     | No       | No     |
| Patient.birthDate        | Person                    | Birth date                    | No       | No     |
| Patient.identifier.value | Person                    | National identifier           | Yes      | Yes    |
| Patient.gender           | Person                    | Gender                        | No       | No     |
| Patient.address.line     | Person                    | Address line                  | No       | No     |
| Patient.address.city     | Person                    | City                          | No       | No     |
| Patient.address.state    | Person                    | State of country              | No       | No     |
| Patient.address.country  | Person                    | Country                       | No       | No     |

For mapping the following mapping script is used. Data type conversions are made by the transformation engine that is included in the adapter.

    output.organizationUnitId = organizationUtils.getOrganizationUnitId( input.managingOrganization, 'http://example.ph/organizations' );
    output.setValueByName( 'National identifier', identifierUtils.getIdentifier( input, 'http://example.ph/national-patient-id' ) );
    output.setValueByName( 'Last name', humanNameUtils.getPrimaryName( input.name ).family );
    output.setValueByName( 'First name', humanNameUtils.getSingleGiven( humanNameUtils.getPrimaryName( input.name ) ) );
    output.setValueByName( 'Birth date', input.birthDate );
    output.setValueByName( 'Gender', input.gender );
    output.setValueByName( 'Address line', addressUtils.getSingleLine( addressUtils.getPrimaryAddress( input.address ) ) );
    output.setValueByName( 'City', addressUtils.getPrimaryAddress( input.address ).city );
    output.setValueByName( 'State of country', addressUtils.getPrimaryAddress( input.address ).state );
    output.setValueByName( 'Country', addressUtils.getPrimaryAddress( input.address ).country );
    output.coordinates = geoUtils.getLocation( addressUtils.getPrimaryAddress( input.address ) );
    true;

### FHIR Immunization
_The mapping needs to be defined when the DHIS2 program and program stages are available._
## Implementation Notes
- The prototype may only contain technical and functional implementations that are absolutely required for the demo.
- The prototype may contain a read-only metadata database that contains the mappings.
- The prototype may not be optimized (e.g. may not use caching).
- The prototype may not be unit tested.
- The prototype may not be able to handle multiple FHIR versions at the same time.
