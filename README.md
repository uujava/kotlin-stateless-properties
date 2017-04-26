# kotlin-stateless-properties
Proof of concept for stateless properties in Kotlin

Proposed optimization:
- Delegate to statically initialized fields if delegate object doesn't have state associated with an object instance.

Description:
Current delegation implementation always declare and initialize delegate object.
This takes time and memory in some cases.
In some cases it's preferrable to initialize delegate object once put it to static field and use it in getters and setters.

Naive approach implemented as following:
 - Mark property class with static annotation kotlin.properties.StatelessProperty
 - compiler analyze delegate class for

Consider an example in src/example/Foo.kt

Current kotlinc implementation generates a bytecode as below:
```
> javap -p build\kotlin-classes\main\example\Foo.class
Compiled from "Foo.kt"
public final class example.Foo {
  private int value;
  private final example.DoubleProperty x$delegate;
  private final example.DoubleProperty y$delegate;
  private final example.DelegatedDoubleProperty z$delegate;
  static final kotlin.reflect.KProperty[] $$delegatedProperties;
  public static final example.Foo$Companion Companion;
  public final int getValue();
  public final void setValue(int);
  public final int getX();
  public final int getY();
  public final int getZ();
  public final void setZ(int);
  public java.lang.String toString();
  public example.Foo();
  static {};
}
```
and when running gives following output:
```
> gradle execute
:compileKotlin UP-TO-DATE
:compileJava UP-TO-DATE
:copyMainKotlinClasses UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:execute
created: example.DoubleProperty@66a29884
created: example.DoubleProperty@4769b07b
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='_FACTORY_INSTANCE')
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='z')
foo value:1 x:2 y:2 z:2
created: example.DoubleProperty@cc34f4d
created: example.DoubleProperty@17a7cec2
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='_FACTORY_INSTANCE')
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='z')
foo value:2 x:4 y:4 z:4
created: example.DoubleProperty@65b3120a
created: example.DoubleProperty@6f539caf
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='_FACTORY_INSTANCE')
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='z')
foo value:3 x:6 y:6 z:6
foo value:6 x:12 y:12 z:12

BUILD SUCCESSFUL

Total time: 11.521 secs
```

Kotlinc created from the branch gives:
```
> javap -p build\kotlin-classes\main\example\Foo.class
Compiled from "Foo.kt"
public final class example.Foo {
  private int value;
  private static final example.DoubleProperty x$delegate;
  private static final example.DoubleProperty y$delegate;
  private static final example.DelegatedDoubleProperty z$delegate;
  static final kotlin.reflect.KProperty[] $$delegatedProperties;
  public static final example.Foo$Companion Companion;
  public final int getValue();
  public final void setValue(int);
  public final int getX();
  public final int getY();
  public final int getZ();
  public final void setZ(int);
  public java.lang.String toString();
  public example.Foo();
  static {};
}
```
and output
```
> gradle execute
:compileKotlin UP-TO-DATE
:compileJava UP-TO-DATE
:copyMainKotlinClasses UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:execute
created: example.DoubleProperty@65b3120a
created: example.DoubleProperty@6f539caf
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='_FACTORY_INSTANCE')
created: DelegatedDoubleProperty(name='null')
delegate created: DelegatedDoubleProperty(name='z')
foo value:1 x:2 y:2 z:2
foo value:2 x:4 y:4 z:4
foo value:3 x:6 y:6 z:6
foo value:6 x:12 y:12 z:12

BUILD SUCCESSFUL

Total time: 11.828 secs
```

Usage

Build Kotlin with new POC feature implementation:
- clone somethere Kotlin fork https://github.com/uujava/kotlin.git
- checkout feature_stateless_properties branch
```
git checkout feature_stateless_properties
```
- build and install Kotlin artifacts in local repo as described in Kotlin github readme
```
ant -f update_dependencies.xml
ant -f build.xml
cd libraries
./gradlew build install
mvn install
```
- after you have all above done without errors, go to kotlin-statelss-properties project folder do:
```
./gradlew clean build
./gradlew execute
```
