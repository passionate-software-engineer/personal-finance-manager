// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/lib/config.ts

const {SpecReporter} = require('jasmine-spec-reporter');
const retry = require('protractor-retry').retry;

exports.config = {
  allScriptsTimeout: 20000,
  specs: [
    './src/**/*.e2e-spec.ts'
  ],
  capabilities: {
    'browserName': 'chrome',

//    // allows different specs to run in parallel.
//    // If this is set to be true, specs will be sharded by file
//    // (i.e. all files to be run by this set of capabilities will run in parallel).
//    // Default is false.
//    shardTestFiles: true,
//
//    // Maximum number of browser instances that can run in parallel for this
//    // set of capabilities. This is only needed if shardTestFiles is true.
//    // Default is 1.
//    maxInstances: 2,

    chromeOptions: {
      args: ['--headless', '--disable-gpu', '--start-maximized', '--no-sandbox']
    }
  },

  directConnect: true,
  baseUrl: 'http://localhost:4200/',
  framework: 'jasmine',
  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 40000,
    print: function () {
    }
  },



  onPrepare() {
    retry.onPrepare();
    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.e2e.json')
    });
    jasmine.getEnv().addReporter(
      new SpecReporter({spec: {displayStacktrace: true}}));

//    browser.driver.setNetworkConditions({
//        offline: false,
//        latency: 0, // Additional latency (ms).
//        download_throughput: 500 * 1024, // Maximal aggregated download throughput.
//        upload_throughput: 500 * 1024 // Maximal aggregated upload throughput.
//    }, "No throttling");
    // GPRS, Regular 2G, Good 2G, Regular 3G, Good 3G, Regular 4G, DSL, WiFi or No throttling to disable.
    // When preset is specified values passed in first argument are not respected.
  },

  onCleanUp : function(results) {
    retry.onCleanUp(results);
  },

  afterLaunch : function() {
    return retry.afterLaunch(3);
  }
};


