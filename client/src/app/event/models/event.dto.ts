import CityDTO from 'src/app/city/models/city.dto';
import TargetDTO from 'src/app/target/models/target.dto';
import VictimDTO from 'src/app/victim/models/victim.dto';

export default interface EventDTO {
  id?: number;
  readonly summary: string;
  readonly motive: string;
  readonly date: Date;
  readonly isPartOfMultipleIncidents: boolean;
  readonly isSuccessful: boolean;
  readonly isSuicidal: boolean;
  readonly target: TargetDTO;
  readonly city: CityDTO;
  readonly victim: VictimDTO;
}
