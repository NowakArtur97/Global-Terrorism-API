import AuthStoreState from '../auth/store/auth.store.state';
import CityStoreState from '../cities/store/city.store.state';
import EventStoreState from '../events/store/event.store.state';

export default interface AppStoreState {
  auth: AuthStoreState;
  city: CityStoreState;
  event: EventStoreState;
}
