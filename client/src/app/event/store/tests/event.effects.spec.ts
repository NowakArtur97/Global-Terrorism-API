import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';
import City from 'src/app/city/models/city.model';

import Victim from '../../../victim/models/victim.model';
import Event from '../../models/event.model';
import EventsGetResponse from '../../models/events-get-response.model';
import EventService from '../../services/event.service';
import * as EventActions from '../event.actions';
import EventEffects from '../event.effects';

const mockEvents: EventsGetResponse = {
  content: [
    new Event(
      3,
      'summary',
      'motive',
      new Date(),
      true,
      true,
      true,
      new City(1, 'city', 10, 30),
      new Victim(2, 10, 1, 12, 2, 1000)
    ),
    new Event(
      6,
      'summary 2',
      'motive 2',
      new Date(),
      false,
      false,
      false,
      new City(4, 'city 2', 20, 10),
      new Victim(5, 11, 3, 14, 4, 2000)
    ),
  ],
};

describe('EventEffects', () => {
  let eventEffects: EventEffects;
  let actions$: ReplaySubject<any>;
  let eventService: EventService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [
        EventEffects,
        provideMockActions(() => actions$),
        {
          provide: EventService,
          useValue: jasmine.createSpyObj('eventService', ['getAll']),
        },
      ],
    })
  );

  beforeEach(() => {
    eventEffects = TestBed.inject(EventEffects);
    eventService = TestBed.inject(EventService);
  });

  describe('fetchEvents$', () => {
    beforeEach(() => {
      actions$ = new ReplaySubject(1);
      actions$.next(EventActions.fetchEvents);
      (eventService.getAll as jasmine.Spy).and.returnValue(of(mockEvents));
    });

    it('should return a setEvents action', () => {
      eventEffects.fetchEvents$.subscribe((resultAction) => {
        expect(resultAction).toEqual(
          EventActions.setEvents({ events: mockEvents.content })
        );
        expect(eventService.getAll).toHaveBeenCalled();
      });
    });
  });
});
