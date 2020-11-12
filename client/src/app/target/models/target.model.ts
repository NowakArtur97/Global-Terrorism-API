import Country from 'src/app/country/models/country.model';

export default interface Target {
  readonly id: number;
  readonly target: string;
  readonly countryOfOrigin: Country;
}
