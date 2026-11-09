# Game Collector

[![CI](https://github.com/Miketb12/WCCI-capstone-final-project/actions/workflows/ci.yml/badge.svg)](https://github.com/Miketb12/WCCI-capstone-final-project/actions/workflows/ci.yml)
[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/y0epCdja)

Game Collector is a full-stack web app for tracking video games. Browse new releases and top-rated games, search the RAWG catalog of video games, and keep two personal lists: games you own and games you want.

Game data comes from the [RAWG Video Games Database](https://rawg.io/apidocs). This was our capstone project at We Can Code IT.

## Features

**Discover games**
- Home page with a game trailer and three carousels: new releases, the top 10 games, and a random selection
- Search by title, with cover art and release dates in the results
- A details page for each game with its description, genres, release date, rating, platforms, and its trailer when one is available

**Track your collection**
- Add any game to your **Wish List** or **Owned List** from its details page
- Move a game from your Wish List to your Owned List once you buy it
- Remove games from either list
- A profile page that shows both lists together

**Accounts and security**
- Sign up and log in with a username and password
- Passwords are hashed with BCrypt, and never sent back by the API
- Logging in returns a JWT, which the app sends with every request for your data
- Users can only see and change their own lists
- Repeated failed logins are blocked for 15 minutes

## Tech stack

| Part | Technologies |
|---|---|
| Client | React 19, React Router 7, Vite 8, Axios, Bootstrap 5, React Slick |
| Server | Java 21, Spring Boot 4, Spring Data JPA, jjwt, Spring Security Crypto (BCrypt) |
| Database | H2 (in-memory), with a commented-out MySQL configuration in `application.properties` |
| Testing and CI | JUnit 5, ESLint, GitHub Actions |
| External API | RAWG Video Games Database |

## How it works

```
React client (port 5173)  -->  Spring Boot API (port 7098)  -->  RAWG API
                                        |
                                    H2 database
                              (users, wish lists, owned lists)
```

The client never calls RAWG directly. The Spring Boot server makes those requests, keeps the RAWG API key private, and stores each user's lists in its database.

## Getting started

### Requirements

- Java 21
- Node.js 20.19+ or 22.12+
- A free RAWG API key from [rawg.io/apidocs](https://rawg.io/apidocs)

### 1. Configure the server

Copy the example settings file:

```bash
cp server-side/.env.example server-side/.env
```

Then fill in both values in `server-side/.env`:

| Setting | What to put |
|---|---|
| `JWT_SECRET` | Any random string of at least 32 characters, used to sign login tokens |
| `RAWG_API_KEY` | Your RAWG API key |

To generate a secret, you can run `openssl rand -base64 48`. The server won't start without these settings, and it tells you which one is missing.

### 2. Start the server

```bash
cd server-side
./gradlew bootRun
```

The API runs at `http://localhost:7098`.

### 3. Start the client

In a second terminal:

```bash
cd client-side
npm install
npm run dev
```

Then open [http://localhost:5173](http://localhost:5173).

### Test accounts

The server creates three accounts at startup that you can log in with: `abc123`, `def123` and `hijk123`, each with the password `1234`.

The default H2 database is in memory, so new accounts and list changes are cleared when the server restarts.

## Running the checks

```bash
cd server-side && ./gradlew test
```

```bash
cd client-side && npm run lint && npm run build
```

GitHub Actions runs both on every pull request and every push to `main`.

## API overview

| Method | Endpoint | Description | Login required |
|---|---|---|---|
| `POST` | `/register` | Create an account | No |
| `POST` | `/login` | Log in and receive a token | No |
| `GET` | `/game/top_games` | Top 10 rated games | No |
| `GET` | `/game/new_games` | Recent releases | No |
| `GET` | `/game/search?query=` | Search games by title | No |
| `GET` | `/game/detail/{gameId}` | Details for one game | No |
| `GET` | `/game/video/{gameId}` | Trailers for one game | No |
| `GET` | `/user/id/{id}` | Your profile with both lists | Yes |
| `GET` | `/user/{userId}/wishlist` | Your Wish List | Yes |
| `GET` | `/user/{userId}/ownedlist` | Your Owned List | Yes |
| `POST` | `/user/add-to-wish-list/{gameId}?userId=` | Add a game to your Wish List | Yes |
| `POST` | `/user/add-to-owned-list/{gameId}?userId=` | Add a game to your Owned List | Yes |
| `POST` | `/user/{userId}/moveToOwnedList/{wishListId}` | Move a game to your Owned List | Yes |
| `DELETE` | `/user/{userId}/wishlist/remove/{wishListId}` | Remove a game from your Wish List | Yes |
| `DELETE` | `/user/{userId}/ownedlist/remove/{ownedListId}` | Remove a game from your Owned List | Yes |

Endpoints that require login expect an `Authorization: Bearer <token>` header, using the token from `/login`.

## Project structure

```
client-side/    React app (pages, components, styles)
server-side/    Spring Boot API (controllers, services, entities, repositories)
.github/        GitHub Actions workflow
```

## Team

| Name | Role |
|---|---|
| [Tion Carter](https://github.com/Codename-Carter) | Front-end Developer |
| [Wuyuan Sun](https://github.com/Wuyuan-WCCI) | Full Stack Software Developer |
| [Raymond Samuel](https://github.com/KRvision) | Front-end Developer |
| [Michael Vythilengam](https://github.com/Miketb12) | Back-end Developer |
| [Benjamin Pursley](https://github.com/BenjaminPursley) | Full-Stack Developer |

## Acknowledgments

Game data and images are provided by [RAWG](https://rawg.io).
