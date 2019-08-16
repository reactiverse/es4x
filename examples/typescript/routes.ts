import {RoutingContext} from "@vertx/web";

export function home(ctx:RoutingContext) {
  ctx.response()
    .end('Hello from Vert.x Web!');
}
