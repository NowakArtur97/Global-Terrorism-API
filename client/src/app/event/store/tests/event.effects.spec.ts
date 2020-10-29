import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';
import City from 'src/app/city/models/city.model';
import Country from 'src/app/country/models/country.model';
import Province from 'src/app/province/models/province.model';
import Target from 'src/app/target/models/target.model';

import Victim from '../../../victim/models/victim.model';
import Event from '../../models/event.model';
import EventsGetResponse from '../../models/events-get-response.model';
import EventService from '../../services/event.service';
import * as EventActions from '../event.actions';
import EventEffects from '../event.effects';

const mockEvents: EventsGetResponse = {
  content: [
    new Event(
      6,
      'summary',
      'motive',
      new Date(),
      false,
      false,
      false,
      new Target(3, 'target', new Country(1, 'country')),
      new City(
        4,
        'city',
        20,
        10,
        new Province(2, 'province', new Country(1, 'country'))
      ),
      new Victim(5, 11, 3, 14, 4, 2000)
    ),
    new Event(
      12,
      'summary 2',
      'motive 2',
      new Date(),
      false,
      false,
      false,
      new Target(9, 'target 2', new Country(1, 'country 2')),
      new City(
        10,
        'city 2',
        20,
        10,
        new Province(8, 'province 2', new Country(7, 'country 2'))
      ),
      new Victim(11, 21, 13, 11, 1, 2200)
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
