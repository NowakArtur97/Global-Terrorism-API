import ProvinceDTO from 'src/app/province/models/province.dto';

export default interface CityDTO {
  id?: number;
  readonly name: string;
  readonly latitude: number;
  readonly longitude: number;
  readonly province: ProvinceDTO;
}
