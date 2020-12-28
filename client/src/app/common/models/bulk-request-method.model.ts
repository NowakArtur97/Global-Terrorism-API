export default interface BulkRequestMethod {
  method: string;
  url: string;
  headers?: { Authentication: string };
}
