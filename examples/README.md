# Examples

This repository contains several examples that illustrate the typical usage of the *Diapper* framework.
Applications can be built in two main styles, either as a *monolith* or in *split* style:

- **Monolithic Style:**
Sometimes, all that is needed is an application skeleton for dependency injection and argument parsing.
In these cases, one would create a single, [monolithic](monolithic) project.
The example is very limited and only shows how to start a simple "hello world" app, for which the name can be provided via CLI argument.

- **Split Style:**
More complex applications can be [split](split) into multiple smaller building blocks.
This can be useful for improving decoupling and reusability.
In the example, the `main` project is the entry point that declares all dependencies and starts the `Runner`.
The other projects declare business logic and data structures.

In addition, we provide another example to illustrate proper logging pracices:

- **Logging:**
The application uses SLF4J internally and binds an SLF4J simple logger and a *JUL* (`java.util.logging`) bridge to also include logging with the internal Java API.


**Please note:** Packages of *Diapper* are released in the [COPS Lab Packages Repository](https://github.com/cops-lab/packages) and can be added to your project as a regular Maven dependency. Refer to the GitHub documentation to understand [how to add that repository as a package registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) to a Maven `pom.xml` file.

## Running the examples

To run the examples, build and package *the whole repository*, not just the examples.

    $ cd diapper
    $ mvn package

Then you can try to start the individual examples.
Start with the logging project.
Even though the projects are self-contained and executable, you will find that simply starting the .jar file will crash.


    $ cd examples/logging/target
    $ java -jar logging-0.0.1-SNAPSHOT.jar
    ...
    Insufficient startup arguments:
    -> A requested argument is null (no 'Runnable' defined)
    
    The *subset* of related arguments that might get requested at runtime:
      Options:
        --logLevel
          Desired log level.
          Default: INFO
          Possible Values: [ALL, DEBUG, INFO, WARN, ERROR, OFF]
        --run
          Fully-qualified class name of the 'Runnable' to be started.

*Diapper* gives you an error messages that explains the problem: there is no runnable defined.
From the list of arguments, you see that that the `--run` parameter is missing.
Once you provide a runnable, the application can be started (implicitly using INFO log level).

    $ cd examples/logging/target
    $ java -jar logging-0.0.1-SNAPSHOT.jar --run example.LogSomething
    ...
    2023-08-07 14:53:53.991 [main] INFO example.LogSomething - SLF-info
    2023-08-07 14:53:53.992 [main] ERROR example.LogSomething - SLF-error
    2023-08-07 14:53:53.992 [main] WARN example.LogSomething - SLF-warn
    2023-08-07 14:53:53.992 [main] INFO example.LogSomething - JUL-config
    2023-08-07 14:53:53.992 [main] WARN example.LogSomething - JUL-warning
    2023-08-07 14:53:53.992 [main] INFO example.LogSomething - JUL-info
    2023-08-07 14:53:53.992 [main] ERROR example.LogSomething - JUL-severe

You can use the `--logLevel` argument with any of the listed values to make the log less (or more) verbose.
For examples, by only whowing the `WARN` level or higher.

    $ java -jar logging-0.0.1-SNAPSHOT.jar --run example.LogSomething --logLevel WARN
    2023-08-07 14:58:18.293 [main] ERROR example.LogSomething - SLF-error
    2023-08-07 14:58:18.293 [main] WARN example.LogSomething - SLF-warn
    2023-08-07 14:58:18.294 [main] WARN example.LogSomething - JUL-warning
    2023-08-07 14:58:18.294 [main] ERROR example.LogSomething - JUL-severe

The two functional examples, `monolith` and `split`, can now be run in the same way.
Without providing further arguments, another error will pop up.


    $ cd examples/monolith/target
    $ java -jar monolith-0.0.1-SNAPSHOT.jar --run example.HelloWorld
    ...
    Insufficient startup arguments:
    -> A requested argument is null (Name must be set)
    
    The *subset* of related arguments that might get requested at runtime:
      Options:
        --name
          Name to be greeted.

You need to provide a `--name` for the example.

    $ java -jar monolith-0.0.1-SNAPSHOT.jar --run example.HelloWorld --name "John Doe"
    Hello John Doe!

Please note that the example already draws argument definitions from multiple sources.
While `--run` is defined in the *Diapper* internal `RunnerArgs`, the `--name` argument has been defined in the monolith example's `Args` class.

The last example, `split`, is even more complex.
It has a code base with two components that each offer a possible runnable: `c1.Main` and `c2.Main`, which execute trivial statements.
Split projects require a main project that is used for packaging a runnable .jar file, in the example, it is called `split-main`.
These main projects have dependencies to all split components, in the example, these are `split-component1` and `split-component2`.

The `split-component1` defines two arguments (`--name` and `--num`) in its *argument object* `Args1`.
The runnable `c1.Main` illustrates how to access these arguments for output or how to use injected components.

    $ cd examples/split/split-main/target/
    $ java -jar split-main-0.0.1-SNAPSHOT.jar --run example.c1.Main1 --name somename --num 2
    You can directly access arguments like --name: somename
    Or injected instances, like SOMENAME or 22!

The `split-component2` goes one step further and illustrates how to inject information or how to access data/utils from other components.
It introduces its own `Args2`, and uses the `NumberRepeater` that is defined and configured in `split-component1`.

Please note how the following execution now mixes arguments from both components.

    $ java -jar split-main-0.0.1-SNAPSHOT.jar --run example.c2.Main2 --foo bar --num 3
    Value of --foo is 'bar'
    The repeated number is 333.


## Understanding the Examples

You should study the source code now to see how to use *Diapper* in practice.
The code is of all example projects is very short and extensively commented to elaborate on many details.
All files in the repository should be explored, including (and maybe starting from) the Maven `pom.xml` files.
It is recommended to start with exploring the `monolith` style first and then follow-up with the `split` style.
The logging example is only provided as a minimal example to show how work with logging backends.

Make sure that you read the [README](../) of the main project and that you understand the underlying concepts of *Diapper*.
As a brief reminder, all implementations of `Runnable` that a *Diapper* application can find on the class path can be started by providing their fully-qualified name in the `--run` CLI argument.
Any declared constructor parameter in such a class needs to be provided through one of the available `@InjectorConfig` implementations that are detected through *class path scanning*.

When going through the examples, please note:

- The examples avoid redundancy and collect all recurring configuration options in a *parent pom* for all examples.
You can avoid this parent by moving the `properties`, `repositories`, and `build` sections to the `root` pom of each example.
- All examples point to the current development version of *Diapper*.
In your application, however, you must refer to the [latest stable release](https://github.com/orgs/cops-lab/packages?tab=packages&q=diapper) or you won't be able to find the dependency.


### Monolith

The monolith represents the most basic form to get started with *Diapper* when only a single point of execution exists for an application.
Several basic steps are required to make use of the framework.

First, it is required to setup the central `pom.xml` file:

- Make sure the project is usign Java 11+ through the `maven-compiler-plugin`
- Configure the `maven-jar-plugin` and the `maven-dependency-setup`: the `package` goal will then create an executable .jar file and assemble its dependencies in a `lib` folder.
- Add the COPS repository, so the *Diapper* dependencies can be found
- Include dependencies to the *Diapper* `runner` and a logging backend (e.g., `slf4j-simple`).
- *Please note:* As stated before, some of these options have been moved to the parent pom for maintainability of the examples.

Then add the actual source code for the application

- Create the entry class `Main`, that has the main method and kicks off the `Runner`
- Create `Args` to model CLI arguments, in this case, the basic `--name` argument

Create the `Config` class for the dependency injector

- Add the `@InjectorConfig` annotation and extend `InjectorConfigBase`
- When an application requires CLI arguments, the argument object(s) must be defined in the constructor of the `InjectorConfig`.
These arguments wil be parsed and injected on start.
- The `InjectorConfig` is a Google Guice module and can be used as such, i.e., override the `configure` method or add *provider* methods (`@Provides`).
The example binds the `Args` object, so it can be injected.
- It is highly recommended to use `@Provides` annotations to allow for a lazy initialization.
- Instead of making arguments *required* in `Args`, we recommend adding an `AssertArgs` check before use.
This allows for a *lazy crash*, i.e., does not fail when the argument is actually not required for an initialization of objects.

Finally, create the runnable `HelloWorld` class that has been started in the previous section.
In the example, please note the `@Inject` and the arguments object `Args`.



### Split

Also the *split style* example is very basic and many of the steps from the `monolith` apply as well.
However, due to its decomposed nature, the code base is a bit more involved.
Similar to the *monolith*, a main project is required with a dependency to 1) `runner` and 2) an SLF4J logging backend. The individual *split* components only need to depend on the *Diapper* API though.
Obviously, each *split* component can also depend on other application specific dependencies, or dependencies on other components.

First main difference is the project structure.

- Within the `root` project, create three submodules: `split-component1` and `split-component2` for the logic and `split-main` as the aggregator project.
- Setup the `/pom.xml` file
    - Include `split-component1`, `split-component2`, and `split-main` as submodules
    - Make sure the project is usign Java 11+ through the `maven-compiler-plugin`
    - Configure the `maven-jar-plugin` and the `maven-dependency-setup`: the `package` goal will then create an executable .jar file and assemble its dependencies in a `lib` folder.
    - Add the COPS repository, so the *Diapper* dependencies can be found
    - *Please note:* As stated before, some of these options have been moved to the parent pom for maintainability of the examples.
- Setup the `split-main/pom.xml` file of the aggregator project
    - Refer to the `root` project as parent to inherit its configuration
    - Include dependencies to the *Diapper* `runner` and a logging backend (e.g., `slf4j-simple`)
    - Add a dependency to *all* split components (i.e., `split-component1`, `split-component1`), to include them in the packaging.
    - Enable the `maven-jar-plugin` and `maven-dependency-setup` build plugins
- Create the entry class `Main` in the aggregator project (`split-main`), that has the main method and kicks off the `Runner`

Now, the required setup for all components in the submodules is similar to the monolith example.
For example, for the `split-component2`

- Setup the `split-component2/pom.xml` file
    - Refer to the `root` project as parent to inherit its configuration
    - Add a dependency to the *Diapper* `api` and to other poject-specific libraries.
- Create `Args2` to model CLI arguments, in this case, the `--foo` argument
- Create the *injector config* `Config2`, refer to `Args2` in the constructors, and bind it for injection.

Finally, create the runnable `Main2` class that has been started in the previous section.
Using the `@Inject` annotation on the constructor allows to request injection, for example, `Args2`.

*Please note:* *Diapper* will always load *all* injector configs of *all* components, which explains why `split-component2` can also request `NumberRepeater` (which is defined in `split-component1` and configured in `Config1`).
The effect of loading all injector configs is that parsing `Config1` also causes `Args1` to be executed.
The lazy setup of `NumberRepeater` contains an `AssertArgs` check for `--num`, so  this argument *must* be provided, while the `--name` parameter of `Args1` is nowhere used and therefore optional.


### Logging

The general setup of the *logging* example follow the *monolith* example, with some notable differences.

- The `pom.xml` depends not only on the `slf4j-simple` logger implementation, but also uses the `jul-to-slf4j` bridge, which allows to log the `java.util.logging` logs via SLF4J.
- A `Main` class is required to kick off the *Diapper* `Runner`.
In contrast to the previous examples, an implementation of `ILogSettings` are provided in the construction of the `Runner`.
- Configuring the logging is highly dependent on the backend that is being used and no general guideline can be given.
- The example provides the `ExampleLogSettings`, which configure the SLF4J simple logger via system properties.
The used SUL bridge needs to be added to the JUL `LogManager`.
- The `LogLevel` enumeration is defined by *Diapper*.
The `ILogSettings` implementation needs to map it to a level that makes sense for the concrete logging backend that is used in the application.

The `LogSomething` class is the `Runnable` that can be started with *Diapper*.
It illustrates two canonical ways to log with SLF4J and JUL: The loggers are instantiated statically at the top of the file and the `run` method contains log statements on various levels.
The output of these logging statements respect the logging level that is provided to *Diapper* via the `--logLevel` argument, as illustrated in the previosu section.

