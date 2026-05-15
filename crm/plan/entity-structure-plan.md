# Entity Structure Plan

## Decision

Use **composition**, not inheritance.

```text
Trainer has one User
Trainee has one User
```

Reason: `trainer.id` and `trainee.id` must be independent from `user.id`, while both profiles reference `users` through `user_id`.

Users are registered first. Trainer/trainee profiles are created later and linked to an existing user.

The current `Entity` interface is temporary and should not drive the final entity design.

## Java Model

### `User`

Stores common account data:

- `id`
- `firstName`
- `lastName`
- `username`
- `password`
- `isActive`

```text
public class User {
  private Long id;
  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private boolean isActive;
}
```

### `Trainer`

Stores trainer-specific data and references one `User`.

```text
public class Trainer {
  private Long id;
  private User user;
  private Training training;
  private TrainingType specialization;
}
```

### `Trainee`

Stores trainee-specific data and references one `User`.

```text
public class Trainee {
  private Long id;
  private User user;
  private LocalDate dateOfBirth;
  private String address;
}
```

Common user fields should be accessed through the composed user:

```text
trainer.getUser().getUsername();
trainee.getUser().getFirstName();
```

## Database Model

### `users`

Stores only common account fields.

```text
id PK
first_name NOT NULL
last_name NOT NULL
username NOT NULL UNIQUE
password NOT NULL
is_active NOT NULL
```

### `trainers`

Stores trainer profile fields.

```text
id PK
user_id FK -> users.id NOT NULL UNIQUE
specialization_id FK -> training_types.id
```

`trainers.id` is independent from `users.id`.

### `trainees`

Stores trainee profile fields.

```text
id PK
user_id FK -> users.id NOT NULL UNIQUE
date_of_birth
address
```

`trainees.id` is independent from `users.id`.

### `training_types`

```text
id PK
name NOT NULL
```

### `trainings`

```text
id PK
name NOT NULL
training_type_id FK -> training_types.id
trainee_id FK -> trainees.id
trainer_id FK -> trainers.id
date NOT NULL
duration_in_minutes NOT NULL
```

## Relationship Summary

```text
User 1 -> 0..1 Trainer
User 1 -> 0..1 Trainee
TrainingType 1 -> many Trainer
TrainingType 1 -> many Training
Trainee 1 -> many Training
Trainer 1 -> many Training
```

## JPA Mapping

Do **not** use joined inheritance.

Use `@OneToOne` composition instead.

```text
@Entity
@Table(name = "trainers")
public class Trainer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
```

```text
@Entity
@Table(name = "trainees")
public class Trainee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
```

Do not use `cascade = CascadeType.ALL` for `user`, because the user is registered first and profiles reference an existing user.

## Repository Notes

Username belongs to `User`, so username search should go through `user.username`.

Spring Data JPA method names:

```text
Optional<Trainer> findByUserUsername(String username);
Optional<Trainee> findByUserUsername(String username);
```


## Implementation Order

1. Make `User` concrete, not abstract.
2. Remove dependency on the temporary `Entity` interface from the final plan.
3. Change `Trainer extends User` to `Trainer` with `private User user`.
4. Change `Trainee extends User` to `Trainee` with `private User user`.
5. Register users first.
6. Create trainer/trainee profiles by linking an existing user.
7. Update mappers/builders to use nested `User` objects.
8. Update repositories to search by `user.username`.
9. Add JPA `@OneToOne` mappings without cascade to `user` when persistence is introduced.
10. Update tests for composition and user-first profile creation.
