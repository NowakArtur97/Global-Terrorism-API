import Event from '../models/event.model';

export default interface EventStoreState {
  events: Event[];
}
