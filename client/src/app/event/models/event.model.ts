import City from 'src/app/city/models/city.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../victim/models/victim.model';

export default interface Event {
  readonly id: number;
  readonly summary: string;
  readonly motive: string;
  readonly date: Date;
  readonly isPartOfMultipleIncidents: boolean;
  readonly isSuccessful: boolean;
  readonly isSuicidal: boolean;
  readonly target: Target;
  readonly city: City;
  readonly victim: Victim;
}
