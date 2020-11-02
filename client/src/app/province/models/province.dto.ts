import CountryDTO from 'src/app/country/models/country.dto';

export default interface ProvinceDTO {
  name: string;
  country: CountryDTO;
}
