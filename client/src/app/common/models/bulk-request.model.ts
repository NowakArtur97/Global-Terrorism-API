import BulkRequestMethod from './bulk-request-method.model';

export default interface BulkRequest {
  operations: BulkRequestMethod[];
}
