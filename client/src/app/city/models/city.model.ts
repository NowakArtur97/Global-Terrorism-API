import Province from 'src/app/province/models/province.model';

export default interface City {
  readonly id: number;
  readonly name: string;
  readonly latitude: number;
  readonly longitude: number;
  readonly province: Province;
}
