# financial instrument

The financial instrument application provides services for managing different instruments price.


## Services

The following services are provided by the financial instrument application:

- `RunnerMain` - Service that provides the ability to manage financial instrument.

## For Developers/Tester

### Building

For building the code, you will need:

- JDK 8
- Maven 3.5.4 or higher

To build, simply execute:

```
$ git clone https://github.com/lalitverma186/financial-instrument.git
$ cd financial-instrument
$ mvn clean install
```

### Working with Eclipse/STS

To import the projects, follow these steps:

- Open the `Git` perspective
- Click on `Clone a Git Repository and add the clone to this view`
- Enter the SSH URI from Github (in the right git repo, select `clone` and SSH), user/password and click on the `Next` button
- Select the branch and click on `Next`
- Enter your workspace location and click finish
- Navigate to your Java EE perspective and import the projects as `Existing Maven Projects` 

### CI tools

- Jenkins   : http://XXXXXXXXXXXXX.com/jenkins
- SonarQube : http://XXXXXXXXXXXXX.com:9000/