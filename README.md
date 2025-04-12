# Pencode

*Parallel encoder*

[![Java CI with Maven](https://github.com/jarnaud/pencode/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/jarnaud/pencode/actions/workflows/maven.yml)

[![codecov](https://codecov.io/github/jarnaud/pencode/graph/badge.svg?token=XRCIYGFN4M)](https://codecov.io/github/jarnaud/pencode)

## Description

This project generate a large amount of records in a database, and generate signatures for these messages in parallel.

## Usage

### Prerequisites

- Java 21+
- Docker

### Setup

Start Docker on your machine, then use the docker-compose file to start the required components (`docker compose up`).
You should see the following containers:
- PostGreSQL database.
- Kafka.
- Kafka UI (not mandatory but useful for topic status visualisation).


### Test

Launch the main test (`ApplicationTest`) to run the app.

## Architecture

![Pencode components](/doc/pencode.png)
