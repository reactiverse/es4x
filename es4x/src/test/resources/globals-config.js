if (config === null || config === undefined) {
  throw new Error('"config" is missing!');
}

if (config.foo !== 'bar') {
  throw new Error('Config data is incorrect!');
}
