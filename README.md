# Domic — Reactive Virtual DOM for Android

Domic is an abstraction for Android UI layer that mirrors real Android [DOM][dom], but ***reactively***.

It allows you:

- Unify interactions with Android UI layer across codebase
- Unit test UI-related code with in-memory implementation of Virtual DOM
- Efficiently render complex state objects (MVI/Redux)
- Enforce async-only interaction with UI layer across codebase
- Reuse existing Android DOM: Views, Widgets, Layouts

>Domic — [DOM][dom] like.

## Table of contents

- [Motivation](#motivation)
- [State of the Project](#state-of-the-project)
- [Examples](#examples)
    - [Bind Document Object](#bind-document-object)
    - [Observe State](#observe-state)
    - [Change State](#change-state)
    - [Testing](#testing)
- [Key Take-Aways](#key-take-aways)
    - [Reusing Existing Android DOM](#reusing-existing-android-dom)
    - [Threading](#threading)
    - [Diffing](#diffing)
    - [Observing Shared State](#observing-shared-state)
    - [Testing](#testing-1)
    - [Type Safety](#type-safety)
- [Implementation Details](#key-implementation-details)
    - [Library Structure](#library-structure)
        - [api](#api)
        - [android](#android)
        - [test](#android)
    - [100% Reactive](#100-reactive)
        - [Observing DOM](#observing-dom)
        - [Changing DOM](#observing-dom)
    - [Diffing](#diffing)
    - [Threading](#threading)
    - [Rendering](#rendering)
    - [Laziness](#laziness)
    - [State Consistency](#state-consistency)
    - [Animations](#animations)
    - [Custom Document Objects](#custom-document-objects)
    - [Performance](#performance)
    - [Multiplatform](#multiplatform)
- [Integrating with Existing Projects](#integrating-with-existing-projects)
    - [MVP](#integrating-with-mvp)
    - [MVVM](#integrating-with-mvvm)
    - [MVI](#integrating-with-mvi)
    - [Redux](#integrating-with-redux)
- [Alternatives](#alternatives)
- [Terminology](#terminology)
- [Credits](#credits)

## Motivation

Scaling Android app codebase and development is complicated. Part of that complication comes from constant need in interaction between app's business logic and UI.

In recent years, lots of progress has been made by community in adopting, inventing and redesigning patterns that other platforms use to solve similar problems: MVP, MVVM, MVI, Redux, etc.

With help of reactive libraries like [RxJava][rxjava] combining streams of data and expressing complicated logic became easier. 

It naturally shifted application code into a form of reactive cycle where app state is combined with user input and new state is produced for rendering. It was always a cycle, but it's more explicit now.

However we've found that existing approaches have problems with scaling for such reactive rendering cycles and we think there is something that can enhance them to fix those problems.

We think that Domic, a *Reactive Virtual DOM*, can be that enhancement layer.

>It is important to say that there are existing projects that overlap with Domic's functionality but with different trade-offs. Please refer to [Alternatives](#alternatives) section for details.

## State of the Project

At the moment Domic is an experimental project.

It means that Lyft is not using it in production *yet*. We do however think that this is a perspective direction that will help to shape, scale and move Android and client-side development further in general.

The project doesn't have a public release yet because *we want to gather some feedback from community* to make sure we didn't make major design errors as scope and internal complexity of the project are quite high.

We're planning to start shipping `0.1.0` version soon though, please *stay tuned!*

Right now we expect community members interested in this project to clone it and play with its source code and [`sample-app`][sample-app], submit issues and pull requests to shape the project!

## Examples

### Bind Real Document Object

#### Binding Signature

```kotlin
val nameOfDocumentObject: VirtualType = BindingType()
```

#### Practical Example

```kotlin
val search: EditText = AndroidEditText(v.findViewById(R.id.search))
```

### Observe State

#### Property Signature

```kotlin
search.observe.textChanges: Observable
```

#### Practical Example

```kotlin
search
    .observe
    .textChanges
    .debounce(300, MILLISECONDS, timeScheduler)
    .switchMap { searchService.search(it) }
```

### Change State

#### Function Signature

```kotlin
search.change.enabled(Observable): Disposable
```

#### Practical Example

```kotlin
searchState
    .map { 
        when (it) {
            is InProgress -> false
            is Finished -> true
        } 
    }
    .startWith(true)
    .subscribe(search.change::enabled)
    // `(Observable) -> Disposable` is an extension function Domic provides.
```

### Testing

#### Binding Signature

```kotlin
val nameOfDocumentObject: VirtualType = BindingType()
```

#### Practical Example

```kotlin
val search: EditText = TestEditText()
```

#### Simulating State Change

```kotlin
search.simulate.text("search term")
```

#### Asserting State

```kotlin
assertThat(search.check.enabled).isEqualTo(false)
```

## Key Take-Aways

### Reusing Existing Android DOM

Domic does *not* require you to rewrite layouts or adopt a new framework to build UI. 

Domic binds to existing Android Framework DOM (UI components: Views, Widgets) so you can continue to use all the tooling and libraries you're used to: Views, Widgets, Layouts, Support Library, Layout Preview, IDE, build system.

### Threading

Domic takes care of threading. 

You can observe state on non-main thread(s), you can change state from non-main thread(s). 

This allows to minimize workload on main thread thus giving it more time for rendering and handling user input.

*Tip*:

>You can run `Presenter`/`ViewModel`/`Model`/`Reductor` on non-main thread(s) and only use main thread for minimal amount of required interactions with real Android [DOM][dom].

### Diffing

Domic takes care of computing diff between previous state and new one thus only rendering what is different.

*Tip*:

>You can drop complex state objects on Domic (like Redux or MVI are designed to work) and let it figure what needs to be rendered.

### Observing Shared State

Domic makes sure you can observe state of same property with multiple `Observer`s. 

Typical example would be observing clicks on Android `View`: Android `View` can only have one click listener at a time, subsequent `Observer` effectively detaches previous one from the `View`. 

Domic however `share()`s the `Observable` allowing multiple `Observer`s observe same state.

### Testing

Domic is an abstraction.

Bindings to real Android [DOM][dom] is just one implementation of that abstraction.

Domic has separate in-memory implementation of the Virtual DOM that let's you unit test your `Presenter`/`ViewModel`/`Model`/`Reductor`.

Domic provides synchronous testing API to ease *simulating* and *checking* state changes.

*Tip:*

>You can take Domic further and ***rendered whole app in memory*** thus be able to run functional and integration tests in memory on JVM*!*

### Type Safety

Domic is type safe.

Observing state and changing state encapsulated into *separate types* withing each Document Object. ie `Button.Observe` and `Button.Change`.

This allows one to have *read-only* or *write-only* reference to a Document Object, thus pushing type safety even further.

*Tip:*

>You can take Domic further and maybe even create a new MV-design with clear separation of code that `Observe`s and `Change`s UI?

## Implementation Details

TODO

## Integrating with Existing Projects

TODO

## Alternatives

## Terminology

### DOM

DOM stands for [Document Object Model][dom]. Term itself comes from early days of web development (1998), however it's abstract enough and pretty much reflects how Android UI system works: Layouts, Views, Widgets, XML-based markdown language, memory model.

Domic however as of now doesn't track child-parent relationship between Document Objects, instead Domic binds to real DOM objects thus leaving layouting to the real DOM.

### Document Object

Since Domic is a Reactive Virtual DOM for Android, we use Document Object as a name for Widget/View/Component type or instance.

It is however valid to call them Widget, View or Component, but Document Object is preferred, specifically for reasons of possible multiplatform support and for sake of easier discussions with developers working on other platforms.

### Diffing

Diffing is a process of computing difference between previous known state and new state. Every property that Domic allows to change for a given Document Object is going through comparison with its previous state, thus eliminating updates of real DOM if they're not required.

### Rendering

Rendering is a process of reflecting in-memory state of Virtual DOM on the real DOM.

## Credits

As many other things in the tech world: programming languages, libraries, cluster management systems, build tools, etc, Domic is *not an invention*, but rather a compilation of ideas and experience.

Domic was influenced by:

- [React.js][reactjs]
- [Litho][litho]
- [Silverlight & WPF XAML + Data Binding][silverlightwpf]

Special credit ❤️ goes to [Juno][juno] Android team, specifically Igor Korotenko for development efforts in similar direction.

[dom]: https://en.wikipedia.org/wiki/Document_Object_Model
[sample-app]: sample-app
[rxjava]: https://github.com/ReactiveX/RxJava
[reactjs]: https://reactjs.org
[litho]: https://fblitho.com
[silverlightwpf]: https://msdn.microsoft.com/en-us/library/ff921107(v=pandp.20).aspx
[juno]: https://gojuno.com
