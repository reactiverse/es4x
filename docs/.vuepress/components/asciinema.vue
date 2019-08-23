<template>
  <div ref="player"></div>
</template>
<script lang="js">
function fixEscapeCodes(text) {
  if (text) {
    var f = function(match, p1, offset, string) {
      return String.fromCodePoint(parseInt(p1, 16));
    };

    return text.
    replace(/\\u([a-z0-9]{4})/gi, f).
    replace(/\\x([a-z0-9]{2})/gi, f).
    replace(/\\e/g, "\x1b");
  } else {
    return text;
  }
}
export default {
  props: {
    src: {
      type: String,
      required: true
    },
    cols: {
      type: String,
      default: "0"
    },
    rows: {
      type: String,
      default: "0"
    },
    autoplay: {
      type: String,
      default: null
    },
    preload: {
      type: String,
      default: null
    },
    loop: {
      type: String,
      default: null
    },
    startAt: {
      type: String,
      default: null
    },
    speed: {
      type: String,
      default: null
    },
    idleTimeLimit: {
      type: String,
      default: null
    },
    poster: {
      type: String,
      default: null
    },
    fontSize: {
      type: String,
      default: null
    },
    theme: {
      type: String,
      default: null
    },
    title: {
      type: String,
      default: null
    },
    author: {
      type: String,
      default: null
    },
    authorURL: {
      type: String,
      default: null
    },
    authorImgURL: {
      type: String,
      default: null
    }
  },
  mounted() {
    let options = this.merge(
      this.attribute(this.cols, "width", 0, parseInt),
      this.attribute(this.rows, "height", 0, parseInt),
      this.attribute(this.autoplay, "autoPlay", true, Boolean),
      this.attribute(this.preload, "preload", true, Boolean),
      this.attribute(this.loop, "loop", true, Boolean),
      this.attribute(this.startAt, "startAt", 0, parseInt),
      this.attribute(this.speed, "speed", 1, parseInt),
      this.attribute(this.idleTimeLimit, "idleTimeLimit", null, parseFloat),
      this.attribute(this.poster, "poster", null, fixEscapeCodes),
      this.attribute(this.fontSize, "fontSize"),
      this.attribute(this.theme, "theme"),
      this.attribute(this.title, "title"),
      this.attribute(this.author, "author"),
      this.attribute(this.authorURL, "authorURL"),
      this.attribute(this.authorImgURL, "authorImgURL"),
      {
        onCanPlay:() => {
          console.log("onCanPlay");
        },
        onPlay:() => {
          console.log("onPlay");
        },
        onPause:() => {
          console.log("onPause");
        }
      }
    );
    asciinema.player.js.CreatePlayer(this.$refs["player"], this.src, options);
  },
  methods: {
    attribute(value, optName, defaultValue, coerceFn) {
      let obj = {};
      if (value !== null) {
        if (value === '' && defaultValue !== undefined) {
          value = defaultValue;
        } else if (coerceFn) {
          value = coerceFn(value);
        }
        obj[optName] = value;
      }
      return obj;
    },
    merge() {
      let merged = {};
      for (let i=0; i<arguments.length; i++) {
        let obj = arguments[i];
        for (let attrname in obj) {
          // noinspection JSUnfilteredForInLoop
          merged[attrname] = obj[attrname];
        }
      }
      return merged;
    }
  }
}
</script>
<style>
  .theme-default-content pre {
    overflow: hidden;
  }
</style>
