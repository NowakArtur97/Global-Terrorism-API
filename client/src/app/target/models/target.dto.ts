import CountryDTO from 'src/app/country/models/country.dto';

export default interface TargetDTO {
  target: string;
  countryOfOrigin: CountryDTO;
}
