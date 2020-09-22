export interface Handler<T> {
  handle(arg0: T) : void;
}

export interface AsyncResult<T> {
  succeeded() : boolean;
  failed() : boolean;
  cause() : Throwable | null;
  result() : T | null;
}

export class JsonObject extends Object {
  constructor();
  constructor(from : string | JsonObject | { [key: string]: any });

  toBuffer() : Buffer;
}

export class JsonArray extends Array<any> {
  constructor();
  constructor(from: string | JsonArray | Array<any>);

  toBuffer() : Buffer;
}
