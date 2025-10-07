# QuickStart

A CLI tool for managing Docker Compose services through a centralized registry. Import service configurations once, then spin them up anywhere without maintaining compose files in every project.

## Core Concept

QuickStart keeps service definitions in `~/.quickstart/registry.json` and generates temporary compose files only when running services. The temp files are created in `~/.quickstart/tmp/`, executed via `docker compose up -d`, then immediately deleted. Your containers keep running—only the configuration file is temporary.

## Installation

### Prerequisites
- Java 21 or higher
- Docker or Docker Desktop

### Option 1: Download Pre-built Release (Recommended)

No Maven or Git required.

1. Go to the [latest release](https://github.com/kusoroadeolu/QuickStart/releases/latest)
2. Download `quickstart.jar` and `install.bat` (Windows) or `install.sh` (Unix/Linux/macOS)
3. Put both files in the same folder
4. Run the installer
 #### NOTE:  Ensure you're in the same directory as the  jar file before you run the installation script

**Windows:**
```bash
install.bat
```

**Unix/Linux/macOS:**
```bash
chmod +x install.sh
./install.sh
```

5. Restart your terminal and you're good to go

### Option 2: Build from Source

Requires Maven and Git.

```bash
git clone https://github.com/kusoroadeolu/QuickStart
cd quickstart
mvn clean package

# Run installer
install.bat         # Windows
./install.sh        # Unix/Linux/macOS
```

Restart your terminal after installation.

## Basic QuickStart Commands

```bash
qs init # initialize quickstart
qs add -f docker-compose.yml   # add the services from docker-compose.yml to the registry
qs up postgres redis # execute postgres and redis services from the registry
qs ls # list all services in the registry
```

## How It Works

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

When you run `qs up redis postgres`:
1. QuickStart fetches both service definitions from the registry
2. Creates a temporary compose file in `~/.quickstart/tmp/`
3. Runs `docker compose -f <temp-file> up -d`
4. Deletes the temp file
5. Your containers keep running

Profiles work the same way but store complete compose files at `~/.quickstart/profiles/`.

## Commands

### `qs init`

Initialize QuickStart directory structure.

```bash
qs init
```

Creates `~/.quickstart/registry.json`, `~/.quickstart/profiles/`, and `~/.quickstart/tmp/`.

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

Parses the `services:` section from your YAML file and stores each service as individual JSON entries in `registry.json`. Skips services that already exist unless `--force` is used.

**Options:**
- `-f, --file <file>` - YAML file to import (required)
- `-e, --exclude <services...>` - Services to skip
- `--force` - Overwrite existing services in registry

**Note:** Named volumes are automatically added to the top-level `volumes:` section. Bind mounts are excluded.

### `qs up`

Run services from the registry.

```bash
# Single service
qs up postgres

# Multiple services
qs up postgres redis rabbitmq
```

**Options:**
- `-v, --verbose` - Show stack traces on errors

### `qs ls`

List all services in the registry.

### `qs show`

Display service configurations as YAML.

```bash
qs show postgres redis
```

### `qs export`

Export services to a YAML file.

```bash
qs export postgres redis -f my-services.yml
```

**Options:**
- `-f, --file <file>` - Output filename (required)

### `qs rm`

Remove services from the registry.

```bash
qs rm postgres redis

# Remove all services
qs rm --all
```

Does not affect running containers.

### `qs exist`

Check if a service exists in the registry.

```bash
qs exist postgres
```

## Profiles

Profiles store complete compose files at `~/.quickstart/profiles/`. Unlike registry services which are merged, profiles are executed as-is.

### `qs profile create`

```bash
qs profile create django-dev
```

### `qs profile import`

```bash
qs profile import django-dev -f docker-compose.yml
```

**Options:**
- `-f, --file <file>` - Source file to import (required)

### `qs profile export`

```bash
qs profile export django-dev -f exported.yml
```

**Options:**
- `-f, --file <file>` - Output filename (required)

### `qs profile up`

```bash
qs profile up django-dev
```

### `qs profile ls`

List all available profiles.

### `qs profile show`

Display profile content.

```bash
qs profile show django-dev
```

### `qs profile rm`

```bash
qs profile rm django-dev

# Delete all profiles
qs profile rm --all
```

### `qs profile exist`

```bash
qs profile exist django-dev
```

## Common Workflows

**Daily Development**

```bash
# Import your common services once
qs add -f ~/configs/dev-services.yml

# Start what you need
cd ~/projects/my-app
qs up postgres redis

# Stop containers when done
docker compose down
```

**Project-Specific Stacks**

```bash
qs profile create spring-dev
qs profile import spring-dev -f spring-stack.yml

cd ~/projects/spring-app
qs profile up spring-dev
```

**Sharing Configs**

```bash
# Export your personal setup to share with others
qs export postgres redis nginx -f my-dev-setup.yml

# Someone else can import it
qs add -f my-dev-setup.yml
qs up postgres redis
```

## Registry vs Profiles

**Use Registry Services for:**
- Mixing individual services on the fly
- Building custom combinations per project
- Managing reusable service building blocks

**Use Profiles for:**
- Complete, fixed stacks
- Multiple services that always run together
- Pre-configured project environments

## File Locations

```
~/.quickstart/
├── registry.json          # Service definitions
├── profiles/              # Profile files
└── tmp/                   # Temporary compose files (auto-deleted)
```

## Global Options

- `-v, --verbose` - Show stack traces when errors occur
- `-h, --help` - Display help information
- `--version` - Show version

## Notes

- Temporary compose files are created in `~/.quickstart/tmp/` and deleted immediately after execution, if they fail to delete, they'll be deleted on the next run
- Your Docker containers continue running after temp file deletion
- Service names in the registry must be unique
- Use `--force` when importing to overwrite existing services 
- Named volumes from service definitions are automatically added to the top-level `volumes:` section (bind mounts are excluded)
- Similar service names are suggested using Levenshtein distance (up to 3 suggestions, distance ≤ 2)
- Profile names automatically get `.yml` extension

## Error Handling

```bash
$ qs up postgre
service not found: postgre (did you mean: postgres?)
  hint: run `quickstart ls` to see available services
```

Use `-v, --verbose` flags for full stack traces when debugging.

## Examples

```bash
# Import and run
qs add -f docker-compose.yml
qs up postgres redis

# Check what's available
qs ls
qs show postgres
qs up postgres

# Create a complete dev environment
qs profile create fullstack-dev
qs profile import fullstack-dev -f complete-stack.yml

# Next time you need this dev environment, you can just start it or export it to your workspace
qs export fullstack-dev -f compose.yaml #export the profile to your compose file in your current workspace
qs profile up fullstack-dev # start the dev environment

# Export for deployment
qs export postgres nginx -f production-services.yml
```