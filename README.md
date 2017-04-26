# Lockdown

[![Travis CI](https://img.shields.io/travis/Corona-IDE/lockdown.svg?branch=master)](https://travis-ci.org/Corona-IDE/lockdown) [![Code Coverage](https://img.shields.io/codecov/c/github/Corona-IDE/lockdown.svg)](https://codecov.io/github/Corona-IDE/lockdown) [![Black Duck Security Risk](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master/badge-risk.svg)](https://copilot.blackducksoftware.com/github/groups/Corona-IDE/locations/lockdown/public/results/branches/master) [![BCH compliance](https://bettercodehub.com/edge/badge/Corona-IDE/lockdown)](https://bettercodehub.com/)

When you simply can't avoid storing basic credentials

Lockdown is intended for those cases where responsible developers have handed off user authentication to some other secure system - only to discover that system itself has credentials their application needs in order to use it! It is intended to make it easy to store those pesky strings with reasonable, basic security methods, instead of clear text.
