## What is Superb JDBC?

**Superb JDBC** is an object-oriented wrapper of JDBC API that simplifies work with relational databases.

**Key Features:**
- Convenient use through clear DBMS abstractions (Queries, Transactions, etc.).
- Close attention to the reliable (encapsulation of closing Connections, ResultSets, etc.).
- Close attention to the performance (encapsulation of PreparedStatements, etc.).
- SQL and nothing more.
- Zero dependencies.
- MIT License.

## Quick Start

1. Add the dependency into your `pom.xml`
```xml
<dependency>
   <groupId>com.nmalygin</groupId>
   <artifactId>superb-jdbc</artifactId>
   <version>0.0.3</version>
</dependency>
```

2. Create a rdbms object
```java
Dbms rdbms = new RealDbms(/* your datasource */);
```

3. Use the rdbms object
```java
List<String> names = rdbms
        .query("SELECT name FROM names")
        .execute(new StringListHandler("name"));
```




