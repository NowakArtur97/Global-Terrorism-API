import CountryDTO from 'src/app/country/models/country.dto';

export default interface TargetDTO {
  readonly id?: number;
  readonly target: string;
  readonly countryOfOrigin: CountryDTO;
}
