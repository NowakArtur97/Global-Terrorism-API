import CountryDTO from 'src/app/country/models/country.dto';

export default interface ProvinceDTO {
  readonly id?: number;
  readonly name: string;
  readonly country: CountryDTO;
}
