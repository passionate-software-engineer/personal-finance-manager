// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/lib/config.ts

const {SpecReporter} = require('jasmine-spec-reporter');

exports.config = {
  allScriptsTimeout: 20000,
  specs: [
   './src/**/*.e2e-spec.ts'

  ],
  capabilities: {
    'browserName': 'chrome',

    chromeOptions: {
      args: ['--start-maximized', '--no-sandbox']
    }
  },
  directConnect: true,
  baseUrl: 'https://pfm.passionatesoftwareengineer.com/',
  framework: 'jasmine',
  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 40000,
    print: function () {
    }
  },
  onPrepare() {
    require('ts-node').register({
      project: require('path').join(__dirname, './tsconfig.e2e.json')
    });
    jasmine.getEnv().addReporter(
      new SpecReporter({spec: {displayStacktrace: true}}));
  }
};


