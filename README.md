reflect
=======

Easy-to-use reflection library for java 8

Introduction
------------

Suppose you have this class:

```
class ClassA {
    private String myString;

    [...]

    public String getMyString() {
        return myString;
    }
}
```

Normal java:
```
ClassA obj = new ClassA();
try {
    Field f = ClassA.class.getDeclaredField("myString");
    f.setAccessible(true);

    System.out.println(f.get(obj));
    f.set(obj, "test");
} catch (ReflectiveOperationException e) {
    // handle
}
```

With this library:
```
ClassA obj = new ClassA();
Fields fields = Fields.of(obj).name("myString");
System.out.println(fields.get());
fields.set("test");
```


### Base interfaces

There are three "accessor" interfaces in reflect, `Fields`, `Methods` and `Constructors`:

```
FieldValue v = Fields.of(myObj).get();
ReturnValue v = Methods.of(myObj).invoke("arg0", "arg1");
MyClass o = Constructors.of(MyClass.class).invoke("arg0", "arg1");
```

### Selecting members

All three of these interfaces share (mostly) the same "selectors" that help you narrow down which members to reflect.

- `.name("myField")` selects members by name. This does not work on constructors.
- `.modifier(java.lang.reflect.Modifier.TRANSIENT)` selects members by modifier. `.withoutModifier` does the same, just that it excludes members. Note that different modifiers can be OR-ed together (for example `VOLATILE | TRANSIENT`), this will be handled the same way two subsequent calls would.
- `.mode(SelectionMode.#)` specifies how to handle cases where not exactly one result was found:
    + `FIRST` will use the first match and ignore any following matches. If no match was found, it will fail with a `NoSuchElementException`.
    + `ONLY` works the same way just that it will also throw an `IllegalStateException` if there was *more* than one match. **This is the default.**
    + `ALL` will attempt to use all members that are found:
        * ***Field retrieval* does not support `ALL`.**
        * *Field modification* will assign the value to all fields.
        * *Method invocation* will invoke all methods and return one of the return values.
        * *Constructor invocation* will call all constructors and return one of the created objects.
    `.all()`, `.first()` and `.only()` are shortcuts for their specific modes.
- `.match(Predicate<Method/Field/Constructor>)` will select all members that match the given predicate.

### Using members

- *Fields* can be retrieved with `.get()` or assigned with `.set(value)`. Additionally, they can be chained with `.fields()` and `.methods()` which essentially work as `Fields.of(fields.get())`; they wrap the field value in another reflector. `.each(Consumer<T>)` will invoke the given consumer for each matched field value. `.eachField(ReflectiveConsumer<Field>)` will invoke the given consumer (ReflectiveConsumers are just Consumers that may throw a ReflectiveOperationException) on each Field object.
- *Methods* can be invoked with `.invoke(arg0, arg1...)`. They, too, can be chained and walked over with `.each` and `.eachMethod` like fields.
- *Constructors* work the same way as methods.


