import City from 'src/app/city/models/city.model';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';
import Target from 'src/app/target/models/target.model';
import Victim from 'src/app/victim/models/victim.model';

import EventDTO from '../models/event.dto';
import Event from '../models/event.model';

export default class EventMapper {
  static mapToModel(event: EventDTO): Event {
    const { target, city, victim } = event;
    const province = city.province;
    const country = target.countryOfOrigin;
    const countryModel = new Country(country.id, country.name);
    const provinceModel = new Province(
      province.id,
      province.name,
      countryModel
    );
    const cityModel = new City(
      city.id,
      city.name,
      city.latitude,
      city.longitude,
      provinceModel
    );
    const victimModel = new Victim(
      victim.id,
      victim.totalNumberOfFatalities,
      victim.numberOfPerpetratorFatalities,
      victim.totalNumberOfInjured,
      victim.numberOfPerpetratorInjured,
      victim.valueOfPropertyDamage
    );
    const targetModel = new Target(target.id, target.target, countryModel);
    return new Event(
      event.id,
      event.summary,
      event.motive,
      event.date,
      event.isPartOfMultipleIncidents,
      event.isSuccessful,
      event.isSuicidal,
      targetModel,
      cityModel,
      victimModel
    );
  }
}
