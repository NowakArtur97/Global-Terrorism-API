import ProvinceDTO from 'src/app/province/models/province.dto';

export default interface CityDTO {
  readonly name: string;
  readonly latitude: number;
  readonly longitude: number;
  readonly province: ProvinceDTO;
}
