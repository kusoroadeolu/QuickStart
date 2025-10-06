# QuickStart

A CLI tool for managing Docker Compose services through a centralized registry. Import service configurations once, then spin them up anywhere without maintaining compose files in every project.

## Core Concept

QuickStart keeps service definitions in `~/.quickstart/registry.json` and generates temporary compose files only when running services. The temp files are created in `~/.quickstart/tmp/`, executed via `docker compose up -d`, then immediately deleted. Your containers keep running—only the configuration file is temporary.

## Installation

### Prerequisites
- Java 17 or higher
- Docker or Docker Desktop
- Git

### Build and Install
```bash
# Clone the repository
git clone https://github.com/kusoroadeolu/QuickStart
cd quickstart

# Build the project
mvn clean package

# Run the installer for your OS
install.bat              # Windows
chmod +x install.sh && ./install.sh    # Unix/Linux/macOS
```

After installation, use `qs` as your command.

## Quick Start

```bash
# Initialize QuickStart
qs init

# Import services from a compose file
qs add -f docker-compose.yml

# Run services (temp file created & destroyed)
qs up postgres redis

# List what's in your registry
qs ls
```

## How It Works

**Registry Storage:**
Services are stored as JSON in `~/.quickstart/registry.json`:

```json
{
  "redis": {
    "image": "redis:7-alpine",
    "container_name": "lightweight-redis",
    "ports": ["6379:6379"]
  },
  "postgres": {
    "image": "postgres:15-alpine",
    "environment": {
      "POSTGRES_PASSWORD": "dev"
    },
    "ports": ["5432:5432"]
  }
}
```

**Execution Flow:**
1. You run `qs up redis postgres`
2. QuickStart fetches both service definitions from the registry
3. Creates a temporary compose file with merged services in `~/.quickstart/tmp/`
4. Runs `docker compose -f <temp-file> up -d`
5. Deletes the temp file immediately after execution
6. Your containers are running, temp file is gone

**Profiles:**
Full compose files stored at `~/.quickstart/profiles/` for complete multi-service stacks. These work the same way—temp file created, executed, deleted.

---

## Commands

### `qs init`

Initialize QuickStart directory structure.

```bash
qs init
```

Creates:
- `~/.quickstart/registry.json` - Service definitions storage
- `~/.quickstart/profiles/` - Profile files directory
- `~/.quickstart/tesmp/` - Temporary compose files location

---

### `qs add`

Import services from a YAML file into the registry.

```bash
# Import all services
qs add -f docker-compose.yml

# Import specific services (exclude others)
qs add -f compose.yml --exclude nginx frontend

# Overwrite existing services
qs add -f compose.yml --force
```

**What happens:**
- Parses the `services:` section from your YAML file
- Extracts each service definition
- Stores them as individual JSON entries in `registry.json`
- Skips services that already exist (unless `--force` is used)

**Options:**
- `-f, --file <file>` - YAML file to import (required)
- `-e, --exclude <services...>` - Services to skip
- `--force` - Overwrite existing services in registry

**Note:** Named volumes (non-bind mounts) are automatically detected from service definitions and added to the top-level `volumes:` section when running or exporting services. Bind mounts (paths with `/` or `\`) are excluded from this auto-detection.

---

### `qs up`

Run services from the registry.

```bash
# Single service
qs up postgres

# Multiple services (merged into one temp compose file)
qs up postgres redis rabbitmq
```

**What happens:**
1. Fetches service configs from `registry.json`
2. Merges them into a single compose structure
3. Creates temp file at `~/.quickstart/tmp/<random>.yml`
4. Executes `docker compose -f <temp-file> up -d`
5. Deletes temp file via `AutoCloseable`
6. Docker containers continue running

**Options:**
- `-v, --verbose` - Show stack traces on errors

---

### `qs ls`

List all services in the registry.

```bash
qs ls
```

Shows service names stored in `registry.json`.

---

### `qs show`

Display service configurations as YAML.

```bash
# Single service
qs show postgres

# Multiple services
qs show postgres redis
```

Outputs the YAML representation of how services are stored in the registry.

---

### `qs export`

Export services to a YAML file in the current directory.

```bash
# Export to file
qs export postgres redis -f my-services.yml

# Export single service
qs export postgres -f postgres.yml
```

Creates a proper `docker-compose.yml` file with the exported services.

**Options:**
- `-f, --file <file>` - Output filename (required)

---

### `qs rm`

Remove services from the registry.

```bash
# Remove specific services
qs rm postgres redis

# Remove all services
qs rm --all
```

Deletes entries from `registry.json`. Does not affect running containers.

---

### `qs exist`

Check if a service exists in the registry.

```bash
qs exist postgres
```

Returns whether the service is present in `registry.json`.

---

## Profiles

Profiles store complete compose files at `~/.quickstart/profiles/`. Unlike registry services which are merged, profiles are executed as-is.

### `qs profile create`

Create a new empty profile.

```bash
qs profile create django-dev
```

Creates `~/.quickstart/profiles/django-dev.yml`.

---

### `qs profile import`

Import a compose file into a profile.

```bash
qs profile import django-dev -f docker-compose.yml
```

Copies the entire compose file content into the profile.

**Options:**
- `-f, --file <file>` - Source file to import (required)

---

### `qs profile export`

Export a profile to a file in the current directory.

```bash
qs profile export django-dev -f exported.yml
```

**Options:**
- `-f, --file <file>` - Output filename (required)

---

### `qs profile up`

Run all services defined in a profile.

```bash
qs profile up django-dev
```

**What happens:**
1. Reads the profile file from `~/.quickstart/profiles/django-dev.yml`
2. Creates temp file at `~/.quickstart/tmp/<random>.yml`
3. Executes `docker compose -f <temp-file> up -d`
4. Deletes temp file
5. Containers keep running

---

### `qs profile ls`

List all available profiles.

```bash
qs profile ls
```

Shows profile files in `~/.quickstart/profiles/`.

---

### `qs profile show`

Display profile content.

```bash
qs profile show django-dev
```

Outputs the raw YAML content of the profile file.

---

### `qs profile rm`

Delete a profile.

```bash
# Delete specific profile
qs profile rm django-dev

# Delete all profiles
qs profile rm --all
```

Removes profile files from `~/.quickstart/profiles/`.

---

### `qs profile exist`

Check if a profile exists.

```bash
qs profile exist django-dev
```

Returns whether the profile file exists.

---

## Common Workflows

### Daily Development

```bash
# Import your common services once
qs add -f ~/configs/dev-services.yml

# Start what you need for today's work
cd ~/projects/my-app
qs up postgres redis

# Work on your project...
# No compose files in your project directory

# Stop containers when done
docker compose down
```

### Project-Specific Stacks

```bash
# Create a profile for your Spring Boot stack
qs profile create spring-dev
qs profile import spring-dev -f spring-stack.yml

# Use it in any Spring project
cd ~/projects/spring-app
qs profile up spring-dev
```

### Team Collaboration

```bash
# Export your team's standard services
qs export postgres redis nginx -f team-services.yml

# Commit team-services.yml to your repo
git add team-services.yml
git commit -m "Add standard dev services"

# Teammates import it
git pull
qs add -f team-services.yml
qs up postgres redis
```

### Switching Between Projects

```bash
# Different projects, different stacks
cd ~/django-project
qs up postgres redis celery

cd ~/express-project
qs up mongodb redis

# No compose file pollution in either project
```

## Registry vs Profiles

**Use Registry Services when:**
- You want to mix and match individual services
- Building custom combinations per project
- Managing reusable service building blocks

**Use Profiles when:**
- You have a complete, fixed stack
- Multiple services that always run together
- Pre-configured project environments

## File Locations

```
~/.quickstart/
├── registry.json          # Service definitions
├── profiles/              # Profile files
│   ├── django-dev.yml
│   └── spring-dev.yml
└── tmp/                   # Temporary compose files (auto-deleted)
```

## Global Options

- `-v, --verbose` - Show stack traces when errors occur
- `-h, --help` - Display help information
- `--version` - Show version

## Important Notes

- Temporary compose files are created in `~/.quickstart/tmp/` and deleted immediately after execution
- Your Docker containers continue running after temp file deletion
- Service names in the registry must be unique
- Use `--force` when importing to overwrite existing services
- Named volumes from service definitions are automatically added to the top-level `volumes:` section (bind mounts are excluded)
- Similar service names are suggested using Levenshtein distance (up to 3 suggestions, distance ≤ 2)
- Profile names automatically get `.yml` extension

## Error Handling

QuickStart provides specific, actionable error messages:

```bash
# Service not found with suggestions
$ qs up postgre
service not found: postgre (did you mean: postgres?)
  hint: run `quickstart ls` to see available services

# Multiple services with some not found
$ qs up postgres redi mongodb
service not found: redi (did you mean: redis?)
service not found: mongodb
  hint: run `quickstart ls` to see available services
```

The tool uses Levenshtein distance to suggest up to 3 similar service names when you mistype. Use `-v` flag for full stack traces when debugging.

## Examples

**Import and run immediately:**
```bash
qs add -f docker-compose.yml
qs up postgres redis
```

**Check what's available before running:**
```bash
qs ls
qs show postgres
qs up postgres
```

**Create a complete dev environment:**
```bash
# Save your full stack as a profile
qs profile create fullstack-dev
qs profile import fullstack-dev -f complete-stack.yml

# Run everything at once
qs profile up fullstack-dev
```

**Export for a specific deployment:**
```bash
# Extract just what you need
qs export postgres nginx -f production-services.yml
```