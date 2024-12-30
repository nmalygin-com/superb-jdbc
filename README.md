## What is Superb JDBC?

**Superb JDBC** is an object-oriented wrapper of JDBC API that simplifies work with relational databases.

**Principles:**
- Convenient use through clear DBMS abstractions (Queries, Transactions, etc.)
- Close attention to the reliable
- Close attention to the performance
- SQL and nothing more
- Zero dependencies
- MIT License

## Quick Start

1. Add the dependency into your `pom.xml`
```xml
<dependency>
   <groupId>com.nmalygin</groupId>
   <artifactId>superb-jdbc</artifactId>
   <version>0.0.4</version>
</dependency>
```

2. Create a rdbms object
```java
Rdbms rdbms = new RealRdbms(dataSource);
```

3. Use the rdbms object
```java
rdbms.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();
```

## Abstractions

### Rdbms

Interface `Rdbms` combines interfaces: `Queries`, `Changes`, `Batches`, `Transactions` (factories of objects `Query`, 
`Change`, `Batch` and `Transaction`).

This separation of interfaces allows us to not violate the _Liskov's Substitution Principle_ in places where we 
want to provide partial functionality.

Note that extracting interfaces also allows us to use doubles (Fake, Stub, etc.) to write tests.

For real interaction with the RDBMS, the  `RealRdbms` class is used, an example of creating an object of the corresponding class:
```java
Rdbms rdbms = new RealRdbms(dataSource);
```

### Query

Interface `Query` is a request to which RDBMS returns a result set, which must then be processed in some way.

#### Simple example

```java
List<String> titles = queries
        .query("SELECT title FROM books")
        .executeWith(new ColumnToListRsh<>(new StringColumn("title")));
```

#### Using parameters

```java
List<String> titles = queries
        .query("SELECT title FROM books WHERE title LIKE ?",
                new StringArgument("Clean%"))
        .executeWith(new ColumnToListRsh<>(new StringColumn("title")));
```

Implementations of the `Argument` interface are used to define arguments. `Superb-jdbc` has several commonly used
implementations of the corresponding interface, but you can always implement your own implementation. 
This approach makes `superb-jdbc` open to extension (_Open–closed principle_).

It is also worth paying attention to the fact that the arguments are specified directly in the place where the
parameterized query is defined, which reduces the likelihood of making a mistake (unlike `jdbc`, where we first
define the query and then set the parameter values).

#### Building a query

```java
Query titlesQuery = queries.query("SELECT title FROM books");
titlesQuery.append(" WHERE title LIKE ?", new StringArgument("Clean%"));
titlesQuery.append(" LIMIT ?", new IntArgument(10));
List<String> titles = titlesQuery
        .executeWith(new ColumnToListRsh<>(new StringColumn("title")));
```

This approach can be useful, for example, when we have a web form and, depending on the fields filled in it, we want to
be able to change the structure of our query (include or exclude some parts of the query).

### Change

The `Change` interface is a request to change the database (_DDL or DML_).

#### Simple example

```java
changes
        .change("INSERT INTO books(title) VALUES ('Clean Code')")
        .apply();
```

#### Using parameters

```java
changes
        .change("INSERT INTO books(title) VALUES (?)", new StringArgument("Clean Code"))
        .apply();
```

### Batch

The `Batch` interface is designed to efficiently insert a batch of data.

#### Simple example

```java
try (Batch batch = batches.batch("INSERT INTO books(title) VALUES (?)")) {
    batch.put(new StringArgument("Clean Code"));
    batch.put(new StringArgument("Code Complete"));
    batch.put(new StringArgument("Effective Java"));
    batch.apply();
}
```

### Transaction

The `Transaction` interface is a transaction within which you can combine several queries to the RDBMS.

#### Simple example

```java
try (Transaction transaction = transactions.transaction()) {
    transaction.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();
    transaction.change("INSERT INTO books(title) VALUES ('Code Complete')").apply();
    transaction.change("INSERT INTO books(title) VALUES ('Effective Java')").apply();
    transaction.commit();
}
```

#### Setting the transaction isolation level

```java
try (Transaction transaction = transactions.transaction(1)) {
    // some code
}
```

#### Using savepoints
```java
try (Transaction transaction = transactions.transaction()) {
    transaction.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();
    transaction.setSavepoint("MySavepoint");
    transaction.change("INSERT INTO books(title) VALUES ('Code Complete')").apply();
    transaction.rollbackTo("MySavepoint");
    transaction.change("INSERT INTO books(title) VALUES ('Effective Java')").apply();
    transaction.commit();
}
```