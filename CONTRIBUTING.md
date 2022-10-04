## Getting started
ClickHouse-JDBC client is an open-source project, and we welcome any contributions from the community. Please share your ideas, contribute to the codebase, and help us maintain up-to-date documentation.

### Set up environment
You have installed:
- JDK 8 or JDK 11+
- To build a multi-release jar use JDK 11+ with `~/.m2/toolchains.xml`
    ```xml
    <?xml version="1.0" encoding="UTF8"?>
    <toolchains>
        <toolchain>
            <type>jdk</type>
            <provides>
                <version>11</version>
            </provides>
            <configuration>
                <jdkHome>/usr/lib/jvm/java-11-openjdk</jdkHome>
            </configuration>
        </toolchain>
        <toolchain>
            <type>jdk</type>
            <provides>
                <version>17</version>
            </provides>
            <configuration>
                <jdkHome>/usr/lib/jvm/java-17-openjdk</jdkHome>
            </configuration>
        </toolchain>
    </toolchains>
    ```

### Create a fork of the repository and clone it
```bash
git clone https://github.com/[YOUR_USERNAME]/clickhouse-jdbc
cd clickhouse-jdbc
```

### Build modules
- JDK 8 Use `mvn -DskipITs clean verify` to compile and generate packages. 
- JDK 11+ Use create a multi-release jar (see [JEP-238](https://openjdk.java.net/jeps/238)) please verify that you added `~/.m2/toolchains.xml` and run `mvn -Drelease -DskipITs clean verify`

## Testing
By default, [docker](https://docs.docker.com/engine/install/) is required to run integration test. Docker image(defaults to `clickhouse/clickhouse-server`) will be pulled from Internet, and containers will be created automatically by [testcontainers](https://www.testcontainers.org/) before testing. To test against specific version of ClickHouse, you can pass parameter like `-DclickhouseVersion=22.3` to Maven.

In the case you don't want to use docker and/or prefer to test against an existing server, please follow instructions below:

- make sure the server can be accessed using default account(user `default` and no password), which has both DDL and DML privileges
- add below two configuration files to the existing server and expose all defaults ports for external access
    - [ports.xml](../../blob/master/clickhouse-client/src/test/resources/containers/clickhouse-server/config.d/ports.xml) - enable all ports
    - and [users.xml](../../blob/master/clickhouse-client/src/test/resources/containers/clickhouse-server/users.d/users.xml) - accounts used for integration test
      Note: you may need to change root element from `clickhouse` to `yandex` when testing old version of ClickHouse.
- make sure ClickHouse binary(usually `/usr/bin/clickhouse`) is available in PATH, as it's required to test `clickhouse-cli-client`
- put `test.properties` under either `~/.clickhouse` or `src/test/resources` of your project, with content like below:
  ```properties
  clickhouseServer=x.x.x.x
  # below properties are only useful for test containers
  #clickhouseVersion=latest
  #clickhouseTimezone=UTC
  #clickhouseImage=clickhouse/clickhouse-server
  #additionalPackages=
  ```

### Tooling
We use [TestNG](http://org.testng/doc) as testing framework and for running ClickHouse Local instance [testcontainers](https://www.testcontainers.org/modules/databases/clickhouse/).

### Running unit tests

Does not require a running ClickHouse server.
Running the maven commands above will trigger the test. 

### Benchmark

To benchmark JDBC drivers:

```bash
cd clickhouse-benchmark
mvn -Drelease clean package
# single thread mode
java -DdbHost=localhost -jar target/benchmarks.jar -t 1 \
    -p client=clickhouse-jdbc -p connection=reuse \
    -p statement=prepared Query.selectInt8
```

It's time-consuming to run all benchmarks against all drivers using different parameters for comparison. If you just need some numbers to understand performance, please refer to [this](https://github.com/ClickHouse/clickhouse-jdbc/issues/768)(still have plenty of room to improve according to ranking at [here](https://github.com/go-faster/ch-bench)).

