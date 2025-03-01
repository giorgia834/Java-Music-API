# Java Music API

## Description

A simple API to store your favourite songs. This allows you to view all songs, select them by ID, delete them or update them.
It also includes two additional routes: one that shows the 15 songs with the highest danceability score, and the second one 15 songs with the lowest energy.

## Configuration

### Endpoints

This project supports all CRUD operations, but it also includes two custom routes /highdanceability and /lowenergy

| Method | Endpoint                | Description                                  |
| ------ | ----------------------- | -------------------------------------------- |
| GET    | /music                  | Retrieves all songs                          |
| GET    | /music/{id}             | Retrieves song specified by the {id}         |
| POST   | /music                  | Create a new song                            |
| PUT    | /music/{id}             | Updates song specified by the {id}           |
| GET    | /music/highdanceability | Retrieves 15 songs with highest danceability |
| GET    | /music/lowenergy        | Retrieves 15 songs with lowest energy        |

### Database

### Project Management

## Setup and Installation

## Usage
