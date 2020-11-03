import { EntityState } from '@ngrx/entity/src';

import Event from '../models/event.model';

export default interface EventStoreState extends EntityState<Event> {}
