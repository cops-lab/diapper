# Diapper

This repository contains *Diapper*, a lightweight *all-you-need-to-get-started* framework for creating modern applications that embrace the usage of dependency injection.

Breaking down complex applications into loosely coupled components is key for modern software architectures, as systems become more maintainable and testable. We created *Diapper* to make it easy to follow this principle. It provides a scaffold that trivializes two basic tasks required from essentially every application:

1) We embed dependency injection (DI) as a first-class citizen that can be used to compose even complex architectures.
Notably, the injector config can be broken down into multiple definitions.

2) *Diapper* supports parsing of CLI arguments.
Complex configurations can be split into multiple argument definitions that are used on demand.

*Diapper* is not just another DI framework or a lightweight parser for CLI arguments.
Instead of re-inventing the wheel, the application is build on top of existing, battle-proven technology.
This includes [Dependency Injection via Guice](https://github.com/google/guice) and the parsing of [CLI Arguments via JCommander](https://jcommander.org/).
The contribution of *Diapper* is a tight integration of both in a useful framework that supports a *lazy-initialization* of all components, which allows to reduce the application startup to a minimal set of *needed* components and allows to raise configuration errors only when they are relevant.
With minimal integration effort, *Diapper* makes it trivial to rapidly create applications without sacrificing on best practices.
Supported application types range from UI application to webservices and the resulting applications are trivial to containerize.

Why not just use [Spring Boot](https://spring.io)?
We love Spring and, in fact, many concepts of *Diapper* have been inspired by Spring, like the annotation-driven configuration.
However, while being a versatile tool that goes far beyond what *Diapper* can do, we feel that Spring claims a very *holistic* role in your application that makes it harder to integrate and learn.
We see *Diapper* as a lightweight alternative. *Diapper* is based on a handful of basic concepts and it gets along very well with other libraries and frameworks, which makes it easy to migrate existing applications.

The *Diapper* framework has the following key features:

- It is boring. It reuses established technology to keep the learning curve shallow.
- Get started after learning [five simple concepts](#usage) and add one direct dependency (<20 transitive).
- Embracing dependency injection and lazy initialization makes it straight-forward to follow best-practices and create maintainable, testable applications.
- Component auto-discovery via *class path scanning* allows to build extensible applications with loosely coupled components.
- Running and debugging CLI applications is possible directly from the IDE.
- Clear separation of modules on all levels (i.e., arguments, injector config, dependencies).
- Extensible, annotation-driven application design.
- Easy migration and containerization of applications.



**Please note:** Packages of *Diapper* are released in the [COPS Lab Packages Repository](https://github.com/cops-lab/packages) and can be added to your project as a regular Maven dependency. Refer to the GitHub documentation to understand [how to add that repository as a package registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry) to a Maven `pom.xml` file.



## Usage

It is very easy to get started with *Diapper*. Understanding the following, simple concepts is all that is required for following our [usage examples](examples/), which illistrate how to use *Diapper* in different setups.

### Runner

The `Runner` class is the central entrypoint to the *Diapper* framework. It will set up the dependency injector, scan the class path for declared *Diapper* components, parse command-line arguments, and start the application. You can use it anywhere in your code.

     public static void main(String[] args) {
        new Runner("your.package", "other", "...").run(args);
    }

A `Runner` is initialized with one or two parameters:

1. One or more package names, in which *Diapper* will search for your components.
2. An optional instance of `ILogSettings` can be provided to control the logging behavior of the application.

**Note:**
*Only* final applications should use concrete logging backends to defer a technology lock-in to the last possible point.
As such, *Diapper* does not include any concrete backend and is internally logging with the *SLF4J* API.
A *Diapper* application then needs to define an *SLF4J*-aware dependency, like *Logback*, *Log4J*, or simply the *SLF4J* `SimpleLogger` (`org.slf4j:slf4j-simple`).

The logging setup presents a chicken/egg problem, as the `--logLevel` is parsed by the `Runner` class, which does not know which logging backend is used.
Users can handle the mapping of *SLF4J* log levels to a concrete backend in a custom `ILogSetting` implementation.
The [examples](examples/) contain the `logging` project that illustrates how to setup logging with `java.util.logging` and *SLF4J* in co-existence.


### Runnable Components

A *Diapper* application can provide multiple `Runnable` implementations. One of these implementations can be started by referring to its fully-qualified class name (i.e., including package) through the `--run` CLI argument:

    java -jar release.jar --run your.package.YourRunnable

Optionally, is is possible to control the logging verbosity of the application through the `--logLevel` argument, but the interpretation depends on the concrete logging setup of the application.

A *Diapper* application provides the required infrastructure to use *dependency injection* directly in the `Runnable` without further setup.
*Diapper* uses [Google Guice](https://github.com/google/guice) to instantiate the `Runnable`, so `javax.inject.Inject` annotations can be added to fields or constructors to request references to other components.
We have provided several [examples](examples/) to illustrate this concept in action, especially the `split` example can illustrate a more complex setup.


### Configuration of Dependency Injection

The annnotation `@InjectorConfig` is used as a marker for modules that contribute bindings to the dependency injection.
The annotation is automatically located through class-path scanning, loaded, and registered with Guice's `Injector` object.
All detected classes must implement the `IInjectorConfig` interface (or the abstract convenience class `InjectorConfigBase`).
Please note that this interface is a simple renaming of Guice's `Module` that avoids a naming conflict with Jackson's `Module` when both are used within the same file.

The [examples](examples/) illustrate best-practices for `IInjectorConfig`.
It is recommended to use the Guice annotations `@Provides` and to add `@Singleton` for cases, in which bindings should only be initialized once.
For advanced usage, all [Guice configuration options](https://github.com/google/guice/wiki) are fully supported.  


### CLI Arguments

*Diapper* makes it is easy to define custom CLI arguments in *Argument Objects* and uses the *JCommander* library for the parsing.
Please refer to the [JCommander Guide](https://jcommander.org/) to find an extended documentation.

In a nutshell, two steps are required for using custom CLI arguments in *Diapper*.

1. Define the *argument object* as a class with one field per option and use `@Parameter` to mark fields that should be parsed.
The annotation options should be limited to `names`, `description`, and `arity`.
Default values can be provided by initializing the field.
2. *Argument objects* are detected by *Diapper* when they appear as constructor parameters in `@InjectorConfig` implementations.
*Diapper* will use *JCommander* to instantiate and parse these *argument objects*, to make them usable in the application, they should be bound as a `@Singleton` in the injector.

Required CLI arguments can be broken into as many *argument objects* as necessary and can even be distributed across projects.
*Diapper* will automatically warn about conflicting options (e.g., using the same argument name twice).

### Modularization

The *Diapper* `Runner` can run any specified `Runnable` in the class path, there are no boundaries between the different components/projects.
In the simplest case, a *Diapper* application is a monolithic Maven project.
However, *Diapper* allows to break applications into several smaller modules.
The decoupling of application components is encouraged and facilitated on three distinct levels:

*Dependencies:*
All components should be defined in separate Maven projects to decouple their logic.
While components *can* depend on each other, components should only depend on the minimal set of dependencies that is necessary for their functionality.
There is one exception: Any application built with *Diapper* needs a central *main* project.
This project can, but does not have to contain business logic.
It typically just *depends on all components* and configures the *Maven packaging*.
For more details, please refer to the various [examples](examples/) and check out the differences in the `pom.xml` of the `monolith` and `split` projects. 
 
*Injector Config:*
Configs can be split into multiple classes, which are *lazy evaluated* when `@Provides` annotations are used.
On application start, only those provider methods will get executed that are (transitively) required to instantiate the started runnable.
This speeds up starting time and allows to perform a lazy argument parsing.
As a result, it won't be necessary anymore to provide all *potentially* required CLI arguments (e.g., DB credentials, URLs, ...), but only those few that are *actually needed* by the required bindings. 
It also means that argument validation can be lazy as well, so errors are only raised when they are relevant.

*CLI arguments:*
As introduced before, it is very easy to define CLI arguments, by referring to an *argument object* in the `@InjectorConfig` constructor.
The class-path scanning will detect and initialize all `@InjectorConfig` definitions, so arguments of complex applications can be split into as many *argument objects* as necessary without loosing convenience.
While the full set of *JCommander* options is supported, no argument should be marked as *required*, as this means that it must be provided at all times.
Instead, it is preferable to put validations (e.g., `AssertArgs`) in the corresponding provider methods to *lazily* validate arguments only when they are actually needed.

