import CountryDTO from 'src/app/country/models/country.dto';

export default interface ProvinceDTO {
  id?: number;
  readonly name: string;
  readonly country: CountryDTO;
}
