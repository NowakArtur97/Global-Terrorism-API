export default interface Victim {
  readonly id: number;
  readonly totalNumberOfFatalities: number;
  readonly numberOfPerpetratorsFatalities: number;
  readonly totalNumberOfInjured: number;
  readonly numberOfPerpetratorsInjured: number;
  readonly valueOfPropertyDamage: number;
}
