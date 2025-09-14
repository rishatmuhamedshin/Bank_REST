Для запуска использовать команду:
#### **docker-compose up --build**

# Система управления банковскими картами

ТЗ: Управление Банковскими картами

## Аутентификация

Для получения токена используйте эндпоинт `POST /api/auth/signin`.

## Эндпоинты

### Аутентификация

#### Регистрация пользователя

- **POST** `/api/auth/signup`
- **Request Body:**
```json
{
  "username": "Rishat432",
  "email": "Rishat@example.com",
  "password": "mySuperPuperParol12314%$"
}
```


#### Авторизация пользователя

- **POST** `/api/auth/signin`

- **Request Body:**
```json
{
  "username": "ADMIN ADMINOV",
  "password": "adminAdminov123$"
}
```

### Управление пользователями (только ADMIN)

#### Получить список пользователей

- **POST** `/api/users`
- **Request Body:**

#### Обновить данные пользователя
- **PUT** `/api/users/{userId}/update`
- **Request Body:**
```json
{
  "username": "Rishat432",
  "email": "Rishat@example.com",
  "password": "mySuperPuperParol12314%$"
}
```

#### Назначить роль ADMIN пользователю
- **PUT** `/api/users/{userId}/newAdmin`

#### Удалить пользователя
- **PATCH** `/api/users/{userId}/delete`


### Управление картами

#### Получить все карты (только ADMIN)

- **GET** `/api/card`
- 
#### Создать карту (только ADMIN)

- **POST** `/api/card`
- **Request Body:**
```json
{
  "username": "John432",
  "cardNumber": 4111111111111111,
  "cardStatus": "ACTIVE",
  "balance": 1000.5,
  "expiryDate": "2026-12-31"
}

```

#### Получить свои карты (только USER)
- **POST** `/api/card/my`
- **Request Body:**
```json
{
  "searchQuery": "4111111111111111",
  "page": 0,
  "size": 10,
  "sortBy": "id",
  "sortDirection": "asc"
}
```
#### Блокировка карты
- **POST** `/api/card/{cardId}/block`
- **Request Body:**
```json
{
  "reason": "Карта утеряна. Требуется блокировка."
}
```

#### Активировать карту (только ADMIN)
- **PATCH** `/api/card/{cardId}/activate`

#### Получить баланс карты (только USER)
- **GET** `/api/card/{cardId}/balance`

#### Удалить карту (только ADMIN)
- **DELETE** `/api/card/{cardId}`

### Управление переводами
#### Получить свои карты (только USER)
- **POST** `/api/transfers/to-my-card`
- **Request Body:**
```json
{
  "senderCardId": 1,
  "recipientCardId": 2,
  "transferAmount": 100.5
}

```
