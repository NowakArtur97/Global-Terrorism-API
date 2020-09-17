import City from './city.model';

export default interface CitiesGetResponse {
  content: {
    cities: City[];
  };
}
