import { createFeatureSelector, createSelector } from '@ngrx/store';

import AuthStoreState from '../auth/store/auth.state';
import CityStoreState from '../city/store/city.state';
import * as fromEvents from '../event/store/event.reducer';
import EventStoreState from '../event/store/event.state';

export default interface AppStoreState {
  auth: AuthStoreState;
  city: CityStoreState;
  event: EventStoreState;
}

export const selectEventState = createFeatureSelector<EventStoreState>('event');

// export const {
//   selectAll: selectAllEvents,
// } = fromEvents.eventAdapter.getSelectors(selectEventState);
export const selectAllEvents = createSelector(
  selectEventState,
  fromEvents.selectAllEvents
);
