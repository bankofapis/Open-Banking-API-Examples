# Contributing

Check out ways to contribute to NatWest Open Banking SDK:

## Feature requests

If you have an idea for a new feature, please check our [discussions](REPLACE ME: LINK TO DISCUSSIONS PAGE) to see if
there are already similar ideas or feature requests under discussion. If there are none, please [start](REPLACE ME: LINK
TO CREATE NEW FEATURE) your feature request as a new discussion topic. Add the title `FR: My feature request` and add a
description of the feature with example use case(s).

## Changes to existing features

Help our community by raising your merge requests and issues.

Setup:

```bash
# Clone the repo:
git clone [REPLACE ME: CLONING LINK]
cd ob-code-examples

# Create a branch for your changes
git checkout -b <issue or feature ticket identifier>-<description of issue or feature>
```

Make sure everything works as expected by running the tests in Intellij:

* Navigate to src/test/java
* Right click on package com.bankofapis
* Click on 'Run Tests in 'com.bankofapis''

Or run tests using Maven:

```bash
mvn clean test
```

Create a Pull Request:

To raise a PR on github please take the following steps:

- At [REPLACE ME: LINK TO GIT REPO] click on fork (at the right top)

```bash
# add fork to your remotes
git remote add fork git@github.com:<your-user>/<your-branch-name>.git

# push new branch to your fork
git push -u fork <issue or feature ticket identifier>-<description of issue or feature>
```

- Go to your fork and create a Pull Request.

Some things that will increase the chance that your merge request is accepted:

- Write tests.
- Write a good commit message.