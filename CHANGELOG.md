<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Cloud (IaC) Security Changelog

## [2.0.5] 13-07-2025

### Added
- Kubernetes Security Standards: [Insecure sysctls](https://protsenko.dev/infrastructure-security/insecure-sysctls/)

## [2.0.4] 05-07-2025

### Added
- Kubernetes Security Standards: [Setting custom SELinux options](https://protsenko.dev/infrastructure-security/setting-selinux-is-restricted/)
- Kubernetes Security Standards: [Non-Default /proc mount](https://protsenko.dev/infrastructure-security/insecure-proc-mount/)
- Kubernetes Security Standards: [Unconfined seccomp profile](https://protsenko.dev/infrastructure-security/insecure-seccomp-profile/)

## [2.0.3] 22-06-2025

### Changed
- Performed refactoring of kubernetes inspections

### Added
- Kubernetes Security Standards: AppArmor is disable or override
- Kubernetes Security Standards: HostPort is used

## [2.0.2] 21-06-2025

### Added
- Kubernetes Security: detect using privileged containers
- Kubernetes Security: detect sharing the host namespace
- Kubernetes Security: detect using insecure hostPath volume types

### Changed
- Non-root containers inspection is more relaxed and analyzes only prohibited values.

### Blog
- Check out this [blog post](https://protsenko.dev/2025/06/14/cloud-security-plugin-for-jetbrains-ide-dev-diary-2/) about new features, rebranding, and plans for the plugin.

## [2.0.1] 15-06-2025

### Added
- Kubernetes Security: detect insecure capabilities for containers
- Quick fixes for non-root containers inspection

### Changed
- Places for highlighting non-root containers

## [2.0.0] 11-07-2025

This version marks a major step in the plugin’s lifecycle.  
In this release, I have begun implementing Kubernetes rules and completed the rebranding.

### Added
- Kubernetes: Detect non-root containers to align with the NSA Kubernetes Hardening Guide

### Updated
- Plugin name made more concise
- New branded logo featuring the dog “Jessica”

## [1.1.8] 08-06-2025

### Fixed
- Issue with analyzing docker compose files

### Improved
- Correct YAML key matching based on qualified names

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