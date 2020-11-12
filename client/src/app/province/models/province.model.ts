import Country from 'src/app/country/models/country.model';

export default interface Province {
  readonly id: number;
  readonly name: string;
  readonly country: Country;
}
