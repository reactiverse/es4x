// rollup.config.mjs
import resolve from '@rollup/plugin-node-resolve';

export default {
  input: 'src/index.mjs',
  output: {
    sourcemap: true,
    file: 'bundle.js',
    format: 'cjs'
  },
  plugins: [
    resolve({
      // pass custom options to the resolve plugin
      moduleDirectories: ['node_modules']
    })
  ],
  // indicate which modules should be treated as external
  external: ['lodash']
};
