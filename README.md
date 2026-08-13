# SendMoney

An Android technical-assessment app demonstrating a simple "send money" wallet
flow — login, viewing a wallet balance, sending money, and viewing transaction
history — built with Kotlin, Jetpack Compose, and Clean Architecture / MVVM.

## Overview

SendMoney is a single-module Android app that simulates a minimal wallet
experience:

1. A user logs in with a hardcoded demo credential.
2. The session is persisted locally (DataStore) so the user stays logged in
   across app restarts.
3. From the Home screen, the user can view their wallet balance, send money,
   or view transaction history.
4. Sending money validates the amount, calls a network API to simulate the
   transfer, records the transaction locally, and deducts the amount from the
   in-memory wallet balance.

The project is scoped as an architecture/engineering exercise, not a
production payments app — see [Known limitations](#known-limitations) for
what is intentionally out of scope.

## Features

- **Login** with a hardcoded username/password check (`LoginUseCase`) and a
  persisted session (DataStore), so the app resumes on the Home screen after
  a restart if already logged in.
- **Home screen** showing the current wallet balance with a show/hide toggle,
  and navigation to Send Money / Transactions.
- **Send Money** with client-side and use-case-level validation (amount must
  be positive and not exceed the current balance), a loading state, and a
  success/error result shown in a bottom sheet.
- **Transaction history** listing transactions created during the current
  app session, most recent first.
- **Logout** (available from the top bar on Home, Send Money, and
  Transactions) that clears the persisted session and returns to Login.

## Architecture

The app follows **Clean Architecture** with three layers, combined with
**MVVM** on the presentation side:

- **`domain`** — pure Kotlin, no Android/framework dependencies. Contains
  `model` (e.g. `Transaction`), `repository` interfaces
  (`WalletRepository`, `TransactionRepository`, `SessionRepository`), and
  `usecase` classes that encapsulate business rules (`SendMoneyUseCase`,
  `LoginUseCase`, `LogoutUseCase`, `GetTransactionsUseCase`,
  `GetWalletBalanceUseCase`).
- **`data`** — implements the domain repository interfaces
  (`WalletRepositoryImpl`, `TransactionRepositoryImpl`,
  `SessionRepositoryImpl`), and owns the actual data sources: Retrofit API
  (`TransactionApi`), DataStore (`SessionLocalDataSource`), and an in-memory
  data source (`TransactionLocalDataSource`), plus DTOs and mappers.
- **`presentation`** — Jetpack Compose UI + `ViewModel`s (`LoginViewModel`,
  `HomeViewModel`, `SendMoneyViewModel`, `TransactionsViewModel`,
  `AppViewModel`). Each screen follows the same MVVM pattern:
  - a `MutableStateFlow<XxxUiState>` exposed as a `StateFlow` for
    render-driving state,
  - a `Channel<XxxEffect>` exposed as a `Flow` for one-off events (e.g.
    navigation), consumed via `LaunchedEffect` in the screen's `*Route`
    composable.

Dependencies point inward (`presentation` → `domain` ← `data`); the domain
layer defines the repository contracts, and `data` implementations are
wired in via Hilt (`di/RepositoryModule.kt`, `di/NetworkModule.kt`) so
ViewModels and use cases only ever depend on interfaces.

`AppViewModel` observes `SessionRepository.isLoggedIn` to decide the
navigation start destination (`AppRoute.Home` vs `AppRoute.Login`) at process
start, showing a loading indicator until the first DataStore read completes.

## Tech stack

- **Kotlin** 2.2.10
- **Jetpack Compose** (BOM `2026.02.01`), Material 3
- **Navigation Compose** (type-safe routes via `@Serializable` `AppRoute`)
- **Hilt** 2.60.1 for dependency injection
- **Retrofit** 3.0.0 + **OkHttp** 5.4.0 for networking
- **kotlinx.serialization** (JSON) as the Retrofit converter
- **DataStore Preferences** 1.2.1 for session persistence
- **Kotlin Coroutines / Flow** 1.11.0
- **JUnit4**, **MockK**, **kotlinx-coroutines-test** for unit testing
- Gradle / AGP 9.2.1, `minSdk` 24, `targetSdk` 36, `compileSdk` 37.1

## Project / package structure

```
com.jantiojo.sendmoney
├── SendMoneyApplication.kt        # @HiltAndroidApp
├── di/
│   ├── NetworkModule.kt           # OkHttp, Retrofit, TransactionApi
│   └── RepositoryModule.kt        # binds domain repository interfaces to impls
├── domain/
│   ├── model/Transaction.kt
│   ├── repository/                # WalletRepository, TransactionRepository, SessionRepository
│   └── usecase/                   # LoginUseCase, LogoutUseCase, SendMoneyUseCase,
│                                   # GetTransactionsUseCase, GetWalletBalanceUseCase
├── data/
│   ├── local/                     # SessionLocalDataSource (DataStore), TransactionLocalDataSource (in-memory)
│   ├── remote/
│   │   ├── api/TransactionApi.kt  # Retrofit interface
│   │   └── dto/                   # SendMoneyRequestDto, TransactionDto
│   ├── mapper/TransactionMapper.kt
│   └── repository/                # WalletRepositoryImpl, TransactionRepositoryImpl, SessionRepositoryImpl
└── presentation/
    ├── MainActivity.kt, SendMoneyApp.kt, AppViewModel.kt, AppUiState.kt
    ├── navigation/                # AppNavigation.kt, AppRoute.kt
    ├── components/                # AppButton, AppTopBar (shared Compose components)
    ├── ui/theme/                  # Material 3 theme
    ├── login/                     # LoginScreen(Route), LoginViewModel, LoginUiState, LoginEffect
    ├── home/                      # HomeScreen(Route), HomeViewModel, HomeUiState, HomeEffect
    ├── sendmoney/                 # SendMoneyScreen(Route), SendMoneyViewModel, SendMoneyUiState,
    │                              # SendMoneyEffect, SendMoneyResult
    └── transactions/              # TransactionsScreen(Route), TransactionsViewModel, TransactionsUiState,
                                    # TransactionsEffect, TransactionUiModel, TransactionUiMapper
```

## Login / session flow

- `LoginUseCase` validates that username/password are non-blank and checks
  them against a **hardcoded** credential pair (`"test"` / `"123456"`). There
  is no real authentication backend.
- On success, it calls `SessionRepository.saveSession(username)`, which
  writes `is_logged_in = true` and `username` into a DataStore Preferences
  file (`session_preferences`).
- `LoginViewModel` drives `LoginUiState` (username, password, visibility
  toggle, loading, error message) and emits a one-off `LoginEffect
  .NavigateToHome` on success.
- `AppViewModel` separately observes `SessionRepository.isLoggedIn` at app
  start to pick the initial destination, so a previously logged-in user
  lands directly on Home after relaunching the app.
- `LogoutUseCase` calls `SessionRepository.logout()`, which clears the
  DataStore preferences, and is exposed from Home, Send Money, and
  Transactions screens.

## Wallet balance

- `WalletRepositoryImpl` holds the balance as an in-memory
  `MutableStateFlow<BigDecimal>` seeded at **₱500.00**, exposed both as a
  suspend `getBalance()` snapshot and as a hot `balance: Flow<BigDecimal>`.
- `GetWalletBalanceUseCase` simply exposes `WalletRepository.balance` for the
  Home and Send Money screens to collect.
- The repository is Hilt-`@Singleton`-scoped with no backing storage, so the
  balance **resets to ₱500.00 whenever the app process restarts** — this is
  an intentional design decision (see [Design decisions](#design-decisions-and-tradeoffs)).

## Send Money flow

`SendMoneyViewModel.submit()` → `SendMoneyUseCase.invoke(amount)`:

1. The amount is parsed from the text field (`toBigDecimalOrNull()`); a
   non-numeric value is a no-op.
2. `SendMoneyUseCase` reads the current balance via
   `WalletRepository.getBalance()`.
3. It validates, via `require(...)`, that `amount > 0` and
   `amount <= balance`; a failed check throws `IllegalArgumentException`,
   caught by `runCatching` and surfaced as `Result.failure`.
4. On valid input, `TransactionRepository.sendMoney(amount)` is called,
   which POSTs to the remote API, maps the response to a domain
   `Transaction`, and saves it to the in-memory local data source.
5. `WalletRepository.updateBalance(balance - amount)` is called to deduct
   the amount.
6. `SendMoneyViewModel` maps the `Result` to `SendMoneyUiState.result`:
   `SendMoneyResult.Success(amount, remainingBalance)` or
   `SendMoneyResult.Error(message)`, shown to the user in a `ModalBottomSheet`.

The full call chain, including both the success and failure paths, is
diagrammed in [docs/sequence-diagram.md](docs/sequence-diagram.md).

## Transaction History

- `TransactionsViewModel` loads transactions on `init` via
  `GetTransactionsUseCase`, which delegates to
  `TransactionRepository.getTransactions()`.
- `TransactionRepositoryImpl.getTransactions()` returns transactions from
  `TransactionLocalDataSource` **only** — it does not call the remote API.
  This is a deliberate choice: JSONPlaceholder's `GET /posts` response has no
  `amount` field, so it cannot be mapped back into a `Transaction` (see the
  comment in `TransactionRepositoryImpl.kt`).
- As a result, transaction history reflects **only transactions sent during
  the current app process** and is empty on a fresh launch.
- Each `Transaction` is mapped to a `TransactionUiModel` for display
  (`TransactionUiMapper.toUiModel()`), formatting `createdAt` with
  `SimpleDateFormat("MMM dd, yyyy • hh:mm a")`.

## Network / API implementation

- **Retrofit** + **OkHttp**, with a `kotlinx.serialization` JSON converter
  (`Json { ignoreUnknownKeys = true; explicitNulls = false }`), configured in
  `di/NetworkModule.kt`.
- Base URL: `https://jsonplaceholder.typicode.com/` — a free fake REST API
  with no real persistence.
- `TransactionApi`:
  - `POST posts` — used by `TransactionRepositoryImpl.sendMoney()` to
    simulate submitting a money transfer. JSONPlaceholder accepts the
    request and echoes back a fake `id`; it does not actually persist or
    process anything.
  - `GET posts` — declared on the interface but **not called** by the
    repository, for the reason described above.
- `HttpLoggingInterceptor` (body-level logging) is attached only in debug
  builds (`BuildConfig.DEBUG`).
- `createdAt` on a sent transaction is set client-side
  (`System.currentTimeMillis()`) at mapping time, since the API does not
  return a timestamp.

## Local / in-memory storage

- `TransactionLocalDataSource` is a Hilt-injected class holding a plain
  `mutableListOf<Transaction>` in memory (new transactions inserted at index
  0, most recent first). There is no database — the list, and everything in
  it, is lost when the app process is killed.

## DataStore usage

- `SessionLocalDataSource` uses **Preferences DataStore**
  (`context.sessionDataStore`, file name `session_preferences`) as the
  **only persisted state** in the app, storing:
  - `is_logged_in: Boolean`
  - `username: String`
- This is what allows the login session (and only the login session) to
  survive an app restart, while wallet balance and transaction history do
  not.

## Testing

Unit tests live under `app/src/test/java/com/jantiojo/sendmoney/`, using
**JUnit4**, **MockK**, and **kotlinx-coroutines-test** (plus a shared
`MainDispatcherRule` test util for `ViewModel` coroutine testing):

| Class under test | Tests |
|---|---|
| `LoginUseCase` | 4 |
| `SendMoneyUseCase` | 7 |
| `GetTransactionsUseCase` | 2 |
| `LogoutUseCase` | 1 |
| `LoginViewModel` | 5 |
| `SendMoneyViewModel` | 11 |
| `TransactionsViewModel` | 5 |

Coverage focuses on the Send Money business rules (valid/zero/negative
amount, amount greater than balance, successful transaction, repository/API
failure, correct remaining balance), login success/failure, transaction
loading success/failure, and logout side effects.

Run all unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

There are no Compose UI tests beyond the default generated
`ExampleInstrumentedTest` boilerplate under `androidTest/`.

## Class and sequence diagram links

- [docs/sequence-diagram.md](docs/sequence-diagram.md) — Mermaid sequence
  diagram of the Send Money flow (amount entry through success/error
  display), covering both the success and failure paths.
- [docs/sequence-diagram.png](docs/sequence-diagram.png) — rendered image of
  the same diagram.

No class diagram is included at this time.

## How to build and run

**Requirements:** Android Studio (current stable), JDK 11+, Android SDK with
`compileSdk` 37.1 / `targetSdk` 36 installed.

**Android Studio:**

1. Open the project root in Android Studio and let Gradle sync.
2. Run the `app` configuration on an emulator or device running API 24+.

**Command line:**

```bash
./gradlew assembleDebug     # build a debug APK
./gradlew installDebug      # install on a connected device/emulator
./gradlew :app:testDebugUnitTest   # run unit tests
```

**Demo login:** username `test`, password `123456` (hardcoded in
`LoginUseCase`).

## Design decisions and tradeoffs

- **Clean Architecture + MVVM**: domain logic (validation, transaction
  rules) is isolated in `usecase` classes independent of Android, and
  repository interfaces in `domain` decouple ViewModels from the concrete
  data sources (DataStore, Retrofit, in-memory list) — this is what makes
  the unit tests in this repo possible without instrumentation.
- **DataStore for session/login persistence**: the only thing that needs to
  survive a process restart in this exercise is "is the user logged in,"
  which is a couple of key-value pairs — DataStore Preferences is a
  lightweight fit, with no need for a schema/database.
- **Wallet balance starts at ₱500 and lives in memory**: the balance is
  seeded once per process in `WalletRepositoryImpl` and is not written to
  any persistent store. It is intentionally reset on process restart,
  since persisting a financial balance was not required for this exercise
  and would otherwise need a real, consistent source of truth (e.g. a
  backend), which JSONPlaceholder cannot provide.
- **Transaction history is in-memory, per session**: `TransactionLocalDataSource`
  is a simple in-memory list rather than a persisted store, consistent with
  the wallet balance decision above.
- **Room was intentionally not introduced**: adding a local database would
  imply building durable, reconciled financial records (migrations, schema,
  balance/ledger consistency) — that scope is outside this exercise, which
  focuses on the Clean Architecture/MVVM flow and network integration rather
  than persistence engineering.
- **JSONPlaceholder as the backend**: it's a free fake REST API, useful for
  exercising a real Retrofit/OkHttp/kotlinx.serialization network stack
  (including error handling) without standing up a backend. Its
  limitations directly shaped two implementation choices: `POST /posts` is
  repurposed to simulate "sending money" (it accepts arbitrary JSON and
  always returns a fake success response, so it cannot reject invalid
  transfers server-side), and `GET /posts` is left unused for transaction
  history because its response schema has no `amount` field.

## Known limitations

- **No persistent transaction or balance storage** — both reset when the
  app process is killed. This is by design for this exercise, not an
  oversight (see above).
- **Login is mock/hardcoded** — a single fixed username/password checked
  client-side, no password hashing, no real auth backend or token, no
  registration flow.
- **JSONPlaceholder does not model a wallet or transactions** — the "Send
  Money" API call does not perform any real transfer, cannot fail based on
  business rules server-side, and the transaction `id`/`amount` returned by
  the API are not reliable domain data (amount is not even used from the
  response; the client-submitted amount is used instead).
- **No offline support, retries, or request caching.**
- **No Compose UI/instrumentation test coverage** — only unit tests for
  use cases and ViewModels.
- **Single hardcoded currency (₱ / PHP)**, no localization or multi-currency
  support.
- **No multi-user support** — one implicit "current user" per app install.
