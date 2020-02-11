export interface Handler<T> {
  handle(arg0: T) : void;
}

export interface AsyncResult<T> {
  succeeded() : boolean;
  failed() : boolean;
  cause() : Error | null;
  result() : T | null;
}
