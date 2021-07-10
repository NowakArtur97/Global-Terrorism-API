import * as fromAuth from '../auth/store/auth.reducer';
import * as fromEvent from '../event/store/event.reducer';

export default interface AppStoreState {
  auth: fromAuth.AuthStoreState;
  event: fromEvent.EventStoreState;
}
