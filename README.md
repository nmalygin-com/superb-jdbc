## What is Superb JDBC?

**Superb JDBC** is an object-oriented wrapper of JDBC API that simplifies work with relational databases.

**Features:**
- Convenient use through clear DBMS abstractions (Queries, Transactions, etc.).
- Reliable work with DBMS (encapsulation of closing Connections, ResultSets, etc.).
- Close attention to the performance (encapsulation of PreparedStatements, etc.).
- SQL and nothing more.
- Zero dependencies.
- MIT License.

## Usage

### Quick Start

1. Add the dependency into your `pom.xml`
```xml
<dependency>
   <groupId>com.nmalygin</groupId>
   <artifactId>superb-jdbc</artifactId>
   <version>0.0.3</version>
</dependency>
```

2. Create an object
```java
Dbms dbms = new RealDbms(/* your datasource */);
```

3. Execute queries
```java
List<String> names = dbms
        .query("SELECT name FROM names")
        .execute(new StringListHandler("name"));
```

