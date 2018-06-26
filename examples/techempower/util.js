const ThreadLocalRandom = Java.type('java.util.concurrent.ThreadLocalRandom');

module.exports = {
    randomWorld: () => {
        return 1 + ThreadLocalRandom.current().nextInt(10000)
    },

    /**
   * Returns the value of the "queries" getRequest parameter, which is an integer
   * bound between 1 and 500 with a default value of 1.
   *
   * @param request the current HTTP request
   * @return the value of the "queries" parameter
   */
    getQueries: (request) => {
        let param = request.getParam("queries");

        if (param == null) {
            return 1;
        }
        try {
            let parsedValue = parseInt(param, 10);
            return Math.min(500, Math.max(1, parsedValue));
        } catch (e) {
            return 1;
        }
    }

}