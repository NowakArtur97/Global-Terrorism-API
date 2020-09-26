// import { ComponentFixture, TestBed } from '@angular/core/testing';
// import { Store, StoreModule } from '@ngrx/store';

// import AppStoreState from '../store/app.store.state';
// import { MapComponent } from './map.component';

// describe('MapComponent', () => {
//   let component: MapComponent;
//   let fixture: ComponentFixture<MapComponent>;
//   let store: Store<AppStoreState>;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [StoreModule.forRoot({})],
//       declarations: [MapComponent],
//       providers: [Store],
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(MapComponent);
//     component = fixture.componentInstance;

//     store = TestBed.inject(Store);
//     spyOn(store, 'dispatch');

//     fixture.detectChanges();
//   });

//   describe('when fetch cars button is clickedcomponent is initialized', () => {
//     it('should dispatch FetchCars action', () => {
//       expect(store.dispatch).toHaveBeenCalledWith(new FetchCars()); // czy dispatch zawołany z dobrą akcją
//     });
//   });
// });
