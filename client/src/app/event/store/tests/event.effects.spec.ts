import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { of, ReplaySubject } from 'rxjs';

import EventsGetResponse from '../../models/events-get-response.model';
import EventService from '../../services/event.service';
import * as EventActions from '../event.actions';
import EventEffects from '../event.effects';

const mockEvents: EventsGetResponse = {
  content: [
    {
      id: 6,
      summary: 'summary',
      motive: 'motive',
      date: new Date(),
      isPartOfMultipleIncidents: false,
      isSuccessful: true,
      isSuicidal: false,
      target: {
        id: 3,
        target: 'target',
        countryOfOrigin: { id: 1, name: 'country' },
      },
      city: {
        id: 4,
        name: 'city',
        latitude: 20,
        longitude: 10,
        province: {
          id: 2,
          name: 'province',
          country: { id: 1, name: 'country' },
        },
      },
      victim: {
        id: 5,
        totalNumberOfFatalities: 11,
        numberOfPerpetratorFatalities: 3,
        totalNumberOfInjured: 14,
        numberOfPerpetratorInjured: 4,
        valueOfPropertyDamage: 2000,
      },
    },
    {
      id: 12,
      summary: 'summary 2',
      motive: 'motive 2',
      date: new Date(),
      isPartOfMultipleIncidents: true,
      isSuccessful: false,
      isSuicidal: true,
      target: {
        id: 9,
        target: 'target 2',
        countryOfOrigin: { id: 7, name: 'country 2' },
      },
      city: {
        id: 10,
        name: 'city 2',
        latitude: 10,
        longitude: 20,
        province: {
          id: 8,
          name: 'province 2',
          country: { id: 7, name: 'country 2' },
        },
      },
      victim: {
        id: 11,
        totalNumberOfFatalities: 10,
        numberOfPerpetratorFatalities: 2,
        totalNumberOfInjured: 11,
        numberOfPerpetratorInjured: 6,
        valueOfPropertyDamage: 7000,
      },
    },
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
