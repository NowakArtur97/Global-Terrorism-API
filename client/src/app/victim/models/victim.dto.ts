export default interface VictimDTO {
  readonly id?: number;
  readonly totalNumberOfFatalities: number;
  readonly numberOfPerpetratorFatalities: number;
  readonly totalNumberOfInjured: number;
  readonly numberOfPerpetratorInjured: number;
  readonly valueOfPropertyDamage: number;
}
