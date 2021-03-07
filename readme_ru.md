Библиотека предназначена для создания единого диалекта связи между Spring и Angular на стороне Spring приложения.

Обычно, связь между Spring и Angular происходит при помощи DTO объектов, копии которых создаются для каждой стороны отдельно. 
Это значит, что если мы захотим модифицировать или добавить DTO, нам придется делать это дважды, на двух сторонах и на двух разных языках программирования.
Библиотека же предоставляет возможность иметь лишь один набор этих DTO на стороне Spring приложения.
Они будут автоматически преобразованы в TypeScript при сборке и опубликованы в репозитории.
Angular приложение должно лишь синхронизировать библиотеку из репозитория.

Пример java объекта:
```java
@J2SLibrary(name = "my-awesome-user-library")
@J2SModel
public class User {
    private String firstname;
    private String lastname;
    private int age;
    private Role[] roles;
}

@J2SModel
public class Role {
    private String name;
    private int id;
}
```

Тот же самый объект, сгенерированный J2S на TypeScript:
```typescript
import { Role } from "./Role";

export class User {
    firstname: string;
    lastname: string;
    age: number;
    roles: Role[]
}

export class Role {
    name: string;
    id: number;
}
```