<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Infrastructure as Code (IaC) Security Linter Changelog

## [1.1.7] 02-06-2025

### Fixed
- Fixed a problem with false negatives on [Dockerfile: Consolidate Multiple RUN Instructions](https://protsenko.dev/infrastructure-security/multiple-consecutive-run-commands/)

### Written
- [Read](https://protsenko.dev/2025/03/24/how-i-made-docker-linter-for-intellij-idea-and-other-jetbrains-ide/) my article about my experience creating this IDE plugin.

## [1.1.6] 14-05-2025

### Changed
- fixed issue with fetching digest
- updating IDEA until version

## [1.1.5] 04-03-2025

### Changed
- name: name of the plugin points to IaC security

## [1.1.4] 25-02-2025

### Added
- new rule: 'useradd' without the '-l' flag and a high UID may lead to an excessively large image.

## [1.1.3] 18-02-2025

### Added
- new rule: consecutive run commands

### Fixed
- issue: problem with replacing image version tag to digest

## [1.1.2] 13-02-2025

### Added
- New documentation for each highlighted problem
- Each problem have a link to the documentation

### Changed
- Adjusted highlighting for some problems

## [1.1.1] 09-02-2025

### Changed
- Start working on the documentation
- YAML plugin is optional for the plugin.

## [1.0.10] 06-02-2025 [skipped on marketplace as 1.1.1 approved firstly]

### Fixed
- Fixed issue(s) with applying quick fix to the PSI elements. Thanks to [boss-chifra](https://github.com/boss-chifra) for report
- Fixed issues with some inspections in newer versions of IDE's
- Fixed issue with false-positive triggering when image described as alias of another image

### Changed
- All inspections are used bundled DockerfileVisitor instead new one.  

## [1.0.9] 04-02-2025

### Changed
- fixed bug with checking --no-recommends for apt-get
- fixed bug with checking package-manager update instruction without install

## [1.0.8] 26-01-2025

### Added
- docker-compose support: using privileged in a service

### Changed
- docker-compose support: works with any yaml files that starts with docker

## [1.0.7] 15-01-2025

### Changed
- Removing env with secret remove entire line instead only variable
- Quick action to replace digest shows before quick action with adding user
- Inspections works with different file names of Dockerfiles 
- Healthcheck CMD instruction no more conflicting with existed CMD

### Added
- Quick action for removing referring to the current image
- Tracking image versions from environment variables

## [1.0.6] 11-01-2025

### Added
- docker-compose support: exposing insecure ports

## [1.0.5] 04-01-2025

### Added
- docker-compose support: using root user
- missing HEALTHCHECK instruction
- using apt instead apt-get or apt-cache

### Fixed
- bug with removing stage name after using quick fix


## [1.0.4] 28-10-2024

### Added
- docker-compose support: using unsafe images

## [1.0.3] 06-10-2024

### Added
- Added zypper, dnf, yum auto-confirm checks
- Added additional zypper dist-upgrade check
- Use arguments JSON notation for CMD and ENTRYPOINT arguments

### Changed

- Improved tracking image name specified by arguments
- Inspections merged by Dockerfile instructions
- Improved thread-safety for complex inspections

## [1.0.2] - 01-10-2024

### Added

- Looking for secrets in environment variables
- Looking for curl bashing
- Looking for unsafe RUN calls with dynamic arguments 
- Looking for apt-get without --no-install-recommends
- RUN inspections works with JSON notation

### Changed

- Most of RUN inspections were merge to one and moved to extensions
- USER command now tracking ARGS variables
- Updated highlighting types
- Higher supported IDE version now is 243
- Improved FROM parser for supporting image names with slash

## [1.0.1] - 15-09-2024

### Changed

- Added 23 docker inspections with actionable quick fixes