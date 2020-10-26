/**
 * A generic event handler.
 * This interface is used heavily throughout Vert.x as a handler for all types of asynchronous occurrences.
 */
export interface Handler<T> {

  /**
   * Something has happened, so handle it.
   *
   * @param arg0 something
   */
  handle(arg0: T) : void;
}

/**
 * Encapsulates the result of an asynchronous operation.
 *
 * Many operations in Vert.x APIs provide results back by passing an instance of this in a Handler.
 *
 * The result can either have failed or succeeded.
 *
 * If it failed then the cause of the failure is available with cause().
 *
 * If it succeeded then the actual result is available with result()
 */
export interface AsyncResult<T> {

  /**
   * Did it succeed?
   */
  succeeded() : boolean;

  /**
   * Did it fail?
   */
  failed() : boolean;

  /**
   * A Throwable describing failure.
   */
  cause() : Throwable | null;

  /**
   * The result of the operation.
   */
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
